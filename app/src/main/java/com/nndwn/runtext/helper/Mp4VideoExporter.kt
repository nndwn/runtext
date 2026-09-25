package com.nndwn.runtext.helper

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.MediaMuxer
import android.net.Uri
import android.view.Surface
import androidx.core.content.FileProvider
import androidx.core.graphics.createBitmap
import com.nndwn.runtext.R
import com.nndwn.runtext.data.model.AppMode
import com.nndwn.runtext.data.model.AppSettings
import com.nndwn.runtext.data.repository.FontRepository
import com.nndwn.runtext.domain.morse.MorseEngine
import com.nndwn.runtext.utils.DisplayRatioManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Singleton
class Mp4VideoExporter @Inject constructor(
  @ApplicationContext private val context: Context,
  private val fontRepository: FontRepository,
  private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

  private companion object Config {
    const val VIDEO_FPS = 30
    const val DEFAULT_DURATION_SECONDS = 5
  }

  suspend fun exportVideo(settings: AppSettings): Uri = withContext(dispatcher) {
    DisplayRatioManager.init(context)

    val currentRatio = DisplayRatioManager.ratio.takeIf { it > 0f } ?: (16f / 9f)

    // Batasi maksimum lebar ke 1920 (Full HD) untuk mencegah OOM atau MediaCodec error pada HP resolusi tinggi (4K/1440p),
    // sambil tetap mempertahankan rasio lanskap yang presisi.
    val rawWidth = DisplayRatioManager.width.takeIf { it > 0f } ?: 854f

    val cappedWidth = rawWidth.coerceAtMost(1920f)
    val cappedHeight = cappedWidth / currentRatio

    // MediaCodec H.264 memerlukan dimensi angka genap (divisible by 2) & minimal 320x180
    val videoWidth = ((cappedWidth.toInt() / 2) * 2).coerceAtLeast(320)
    val videoHeight = ((cappedHeight.toInt() / 2) * 2).coerceAtLeast(180)
    val bitRate = (videoWidth * videoHeight * 3.5f).toInt().coerceIn(2_000_000, 10_000_000)

    val rawText = settings.lastText.ifEmpty { "RUNNING TEXT" }
    val fonts = fontRepository.fonts.value

    val unitMs = MorseEngine.getUnitDurationMs(settings.morseConfig.morseWpm)
    val morseElements = if (settings.mode == AppMode.MORSE_CODE) {
      MorseEngine.textToMorseElements(rawText)
    } else emptyList()

    val totalMorseTimeMs = morseElements.sumOf { (it.durationMultiplier * unitMs) }.coerceAtLeast(1000L)

    val durationMs = if (settings.mode == AppMode.RUNNING_TEXT && settings.textConfig.isMove) {
      CanvasTextRenderer.calculateMarqueeDurationMillis(
        width = videoWidth,
        height = videoHeight,
        text = rawText,
        settings = settings,
      )
    } else if (settings.mode == AppMode.MORSE_CODE) {
      totalMorseTimeMs.toInt()
    } else {
      DEFAULT_DURATION_SECONDS * 1000
    }

    val totalFrames = (VIDEO_FPS * (durationMs / 1000f)).toInt().coerceAtLeast(VIDEO_FPS * 2)
    val frameDurationUs = 1_000_000L / VIDEO_FPS

    val appName = context.getString(R.string.app_name).replace(" ", "_")
    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val fileName = "${appName}_${timestamp}.mp4"

    val videoDir = File(context.cacheDir, "videos").apply { mkdirs() }
    val videoFile = File(videoDir, fileName)
    if (videoFile.exists()) videoFile.delete()

    val mimeType = MediaFormat.MIMETYPE_VIDEO_AVC
    val format = MediaFormat.createVideoFormat(mimeType, videoWidth, videoHeight).apply {
      setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface)
      setInteger(MediaFormat.KEY_BIT_RATE, bitRate)
      setInteger(MediaFormat.KEY_FRAME_RATE, VIDEO_FPS)
      setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1)
    }

    val codec = MediaCodec.createEncoderByType(mimeType)
    codec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
    val inputSurface: Surface = codec.createInputSurface()
    codec.start()

    val muxer = MediaMuxer(videoFile.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
    var trackIndex = -1
    var muxerStarted = false
    var writtenFrames = 0L

    val bufferInfo = MediaCodec.BufferInfo()
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val bitmap = createBitmap(videoWidth, videoHeight)
    val canvas = Canvas(bitmap)

    for (frame in 0 until totalFrames) {
      val timeMs = (frame * 1000L) / VIDEO_FPS
      val progress = frame.toFloat() / totalFrames

      val bgColorInt = if (settings.mode == AppMode.RUNNING_TEXT) {
        settings.textConfig.bgColorArgb.toInt()
      } else {
        settings.morseConfig.bgColorMorse.toInt()
      }

      canvas.drawColor(bgColorInt)

      if (settings.mode == AppMode.RUNNING_TEXT) {
        CanvasTextRenderer.drawRunningText(
          canvas = canvas,
          width = videoWidth,
          height = videoHeight,
          text = rawText,
          settings = settings,
          progress = progress,
          fonts = fonts,
          context = context,
          paint = paint,
        )
      } else {
        CanvasTextRenderer.drawMorseSignal(
          canvas = canvas,
          elements = morseElements,
          unitMs = unitMs,
          timeMs = timeMs,
          totalMorseTimeMs = totalMorseTimeMs,
          settings = settings,
        )
      }

      val surfaceCanvas = inputSurface.lockCanvas(null)
      surfaceCanvas.drawBitmap(bitmap, 0f, 0f, null)
      inputSurface.unlockCanvasAndPost(surfaceCanvas)

      while (true) {
        val outputBufferIndex = codec.dequeueOutputBuffer(bufferInfo, 0)
        if (outputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
          break
        } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
          if (muxerStarted) throw RuntimeException("Format changed twice")
          val newFormat = codec.outputFormat
          trackIndex = muxer.addTrack(newFormat)
          muxer.start()
          muxerStarted = true
        } else if (outputBufferIndex >= 0) {
          val encodedData = codec.getOutputBuffer(outputBufferIndex) ?: continue
          if (bufferInfo.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG != 0) {
            bufferInfo.size = 0
          }
          if (bufferInfo.size != 0 && muxerStarted) {
            encodedData.position(bufferInfo.offset)
            encodedData.limit(bufferInfo.offset + bufferInfo.size)
            bufferInfo.presentationTimeUs = writtenFrames * frameDurationUs
            writtenFrames++
            muxer.writeSampleData(trackIndex, encodedData, bufferInfo)
          }
          codec.releaseOutputBuffer(outputBufferIndex, false)
          if (bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
            break
          }
        }
      }
    }

    codec.signalEndOfInputStream()

    var draining = true
    while (draining) {
      val outputBufferIndex = codec.dequeueOutputBuffer(bufferInfo, 10_000)
      if (outputBufferIndex >= 0) {
        val encodedData = codec.getOutputBuffer(outputBufferIndex) ?: continue
        if (bufferInfo.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG != 0) {
          bufferInfo.size = 0
        }
        if (bufferInfo.size != 0 && muxerStarted) {
          encodedData.position(bufferInfo.offset)
          encodedData.limit(bufferInfo.offset + bufferInfo.size)
          bufferInfo.presentationTimeUs = writtenFrames * frameDurationUs
          writtenFrames++
          muxer.writeSampleData(trackIndex, encodedData, bufferInfo)
        }
        codec.releaseOutputBuffer(outputBufferIndex, false)
        if (bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
          draining = false
        }
      } else if (outputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
        draining = false
      }
    }

    codec.stop()
    codec.release()
    if (muxerStarted) {
      muxer.stop()
      muxer.release()
    }

    FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", videoFile)
  }
}