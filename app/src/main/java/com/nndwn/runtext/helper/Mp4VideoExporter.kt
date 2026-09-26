package com.nndwn.runtext.helper

import android.content.Context
import android.graphics.Bitmap
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
import com.nndwn.runtext.data.model.FontData
import com.nndwn.runtext.data.repository.FontRepository
import com.nndwn.runtext.domain.morse.MorseElement
import com.nndwn.runtext.domain.morse.MorseEngine
import com.nndwn.runtext.utils.DisplayRatioManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.nio.ByteBuffer
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

  private data class VideoConfig(
    val width: Int,
    val height: Int,
    val bitRate: Int,
  )

  private data class RenderParams(
    val canvas: Canvas,
    val bitmap: Bitmap,
    val inputSurface: Surface,
    val settings: AppSettings,
    val rawText: String,
    val fonts: List<FontData>,
    val morseElements: List<MorseElement>,
    val unitMs: Long,
    val totalMorseTimeMs: Long,
    val paint: Paint,
  )

  suspend fun exportVideo(
    settings: AppSettings,
    onProgress: (Int) -> Unit = {},
  ): Uri = withContext(dispatcher) {
    DisplayRatioManager.init(context)

    val videoConfig = calculateVideoConfig()
    val rawText = settings.lastText.ifEmpty { "RUNNING TEXT" }
    val fonts = fontRepository.fonts.value

    val unitMs = MorseEngine.getUnitDurationMs(settings.morseConfig.morseWpm)
    val morseElements = if (settings.mode == AppMode.MORSE_CODE) {
      MorseEngine.textToMorseElements(rawText)
    } else {
      emptyList()
    }

    val totalMorseTimeMs = morseElements.sumOf { (it.durationMultiplier * unitMs) }.coerceAtLeast(1000L)
    val durationMs = calculateDurationMs(settings, videoConfig, rawText, totalMorseTimeMs)

    val totalFrames = (VIDEO_FPS * (durationMs / 1000f)).toInt().coerceAtLeast(VIDEO_FPS * 2)
    val frameDurationUs = 1_000_000L / VIDEO_FPS

    val videoFile = createVideoFile()

    val mimeType = MediaFormat.MIMETYPE_VIDEO_AVC
    val format = MediaFormat.createVideoFormat(mimeType, videoConfig.width, videoConfig.height).apply {
      setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface)
      setInteger(MediaFormat.KEY_BIT_RATE, videoConfig.bitRate)
      setInteger(MediaFormat.KEY_FRAME_RATE, VIDEO_FPS)
      setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1)
    }

    val codec = MediaCodec.createEncoderByType(mimeType)
    codec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
    val inputSurface: Surface = codec.createInputSurface()
    codec.start()

    val muxer = MediaMuxer(videoFile.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
    val encoderManager = EncoderManager(muxer, frameDurationUs)
    val bufferInfo = MediaCodec.BufferInfo()

    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val bitmap = createBitmap(videoConfig.width, videoConfig.height)
    val canvas = Canvas(bitmap)

    val renderParams = RenderParams(
      canvas = canvas,
      bitmap = bitmap,
      inputSurface = inputSurface,
      settings = settings,
      rawText = rawText,
      fonts = fonts,
      morseElements = morseElements,
      unitMs = unitMs,
      totalMorseTimeMs = totalMorseTimeMs,
      paint = paint,
    )

    for (frame in 0 until totalFrames) {
      val timeMs = (frame * 1000L) / VIDEO_FPS
      val progress = frame.toFloat() / totalFrames
      val progressPercent = (((frame + 1) * 100) / totalFrames).coerceAtMost(100)
      onProgress(progressPercent)

      renderFrameToSurface(renderParams, timeMs, progress)
      encoderManager.drainLoop(codec, bufferInfo, timeoutUs = 0)
    }

    codec.signalEndOfInputStream()
    encoderManager.drainLoop(codec, bufferInfo, timeoutUs = 10_000)

    codec.stop()
    codec.release()
    encoderManager.stopMuxer()

    FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", videoFile)
  }

  private fun calculateVideoConfig(): VideoConfig {
    val currentRatio = DisplayRatioManager.ratio.takeIf { it > 0f } ?: (16f / 9f)
    val rawWidth = DisplayRatioManager.width.takeIf { it > 0f } ?: 854f

    val cappedWidth = rawWidth.coerceAtMost(1920f)
    val cappedHeight = cappedWidth / currentRatio

    val videoWidth = ((cappedWidth.toInt() / 2) * 2).coerceAtLeast(320)
    val videoHeight = ((cappedHeight.toInt() / 2) * 2).coerceAtLeast(180)
    val bitRate = (videoWidth * videoHeight * 3.5f).toInt().coerceIn(2_000_000, 10_000_000)

    return VideoConfig(videoWidth, videoHeight, bitRate)
  }

  private fun calculateDurationMs(
    settings: AppSettings,
    videoConfig: VideoConfig,
    rawText: String,
    totalMorseTimeMs: Long,
  ): Int {
    return when (settings.mode) {
      AppMode.RUNNING_TEXT -> {
        if (settings.textConfig.isMove) {
          CanvasTextRenderer.calculateMarqueeDurationMillis(
            width = videoConfig.width,
            height = videoConfig.height,
            text = rawText,
            settings = settings,
          )
        } else {
          DEFAULT_DURATION_SECONDS * 1000
        }
      }
      AppMode.MORSE_CODE -> totalMorseTimeMs.toInt()
    }
  }

  private fun createVideoFile(): File {
    val appName = context.getString(R.string.app_name).replace(" ", "_")
    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val fileName = "${appName}_$timestamp.mp4"

    val videoDir = File(context.cacheDir, "videos").apply { mkdirs() }
    val videoFile = File(videoDir, fileName)
    if (videoFile.exists()) {
      runCatching { videoFile.delete() }
    }
    return videoFile
  }

  private fun renderFrameToSurface(
    params: RenderParams,
    timeMs: Long,
    progress: Float,
  ) {
    val bgColorInt = if (params.settings.mode == AppMode.RUNNING_TEXT) {
      params.settings.textConfig.bgColorArgb.toInt()
    } else {
      params.settings.morseConfig.bgColorMorse.toInt()
    }

    params.canvas.drawColor(bgColorInt)

    if (params.settings.mode == AppMode.RUNNING_TEXT) {
      CanvasTextRenderer.drawRunningText(
        canvas = params.canvas,
        width = params.bitmap.width,
        height = params.bitmap.height,
        text = params.rawText,
        settings = params.settings,
        progress = progress,
        fonts = params.fonts,
        context = context,
        paint = params.paint,
      )
    } else {
      CanvasTextRenderer.drawMorseSignal(
        canvas = params.canvas,
        elements = params.morseElements,
        unitMs = params.unitMs,
        timeMs = timeMs,
        totalMorseTimeMs = params.totalMorseTimeMs,
        settings = params.settings,
      )
    }

    val surfaceCanvas = params.inputSurface.lockCanvas(null)
    surfaceCanvas.drawBitmap(params.bitmap, 0f, 0f, null)
    params.inputSurface.unlockCanvasAndPost(surfaceCanvas)
  }

  private class EncoderManager(
    private val muxer: MediaMuxer,
    private val frameDurationUs: Long,
  ) {
    private var trackIndex = -1
    private var muxerStarted = false
    private var writtenFrames = 0L

    fun drainLoop(codec: MediaCodec, bufferInfo: MediaCodec.BufferInfo, timeoutUs: Long) {
      while (drainOutputBuffer(codec, bufferInfo, timeoutUs)) {
        // Continue draining available output buffers
      }
    }

    fun stopMuxer() {
      if (muxerStarted) {
        muxer.stop()
        muxer.release()
      }
    }

    private fun drainOutputBuffer(
      codec: MediaCodec,
      bufferInfo: MediaCodec.BufferInfo,
      timeoutUs: Long,
    ): Boolean {
      val outputBufferIndex = codec.dequeueOutputBuffer(bufferInfo, timeoutUs)
      return when {
        outputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER -> false
        outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> {
          handleFormatChanged(codec)
          true
        }
        outputBufferIndex >= 0 -> {
          handleOutputBufferAvailable(codec, outputBufferIndex, bufferInfo)
        }
        else -> true
      }
    }

    private fun handleFormatChanged(codec: MediaCodec) {
      if (muxerStarted) throw RuntimeException("Format changed twice")
      trackIndex = muxer.addTrack(codec.outputFormat)
      muxer.start()
      muxerStarted = true
    }

    private fun handleOutputBufferAvailable(
      codec: MediaCodec,
      outputBufferIndex: Int,
      bufferInfo: MediaCodec.BufferInfo,
    ): Boolean {
      val encodedData = codec.getOutputBuffer(outputBufferIndex)
      if (encodedData != null) {
        writeSample(encodedData, bufferInfo)
      }
      codec.releaseOutputBuffer(outputBufferIndex, false)
      return (bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM) == 0
    }

    private fun writeSample(
      encodedData: ByteBuffer,
      bufferInfo: MediaCodec.BufferInfo,
    ) {
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
    }
  }
}
