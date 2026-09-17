package com.nndwn.runtext.data.model

import androidx.annotation.FontRes
import com.nndwn.runtext.R
import kotlinx.serialization.Serializable

@Serializable
enum class ScriptCategory {
  LATIN,
  ARABIC,
  JAPANESE,
  CHINESE,
  KOREAN,
  THAI,
  DEVANAGARI,
  KHMER,
  HEBREW,
}

@Serializable
enum class FontType(
  val displayName: String,
  val scriptCategory: ScriptCategory = ScriptCategory.LATIN,
  @FontRes val localResId: Int? = null,
  val googleFontName: String? = null,
) {
  ROBOTO("Roboto", googleFontName = "Roboto"),
  OPEN_SANS("Open Sans", googleFontName = "Open Sans"),
  LATO("Lato", localResId = R.font.lato_bold),
  MONTSERRAT("Montserrat", googleFontName = "Montserrat"),
  POPPINS("Poppins", googleFontName = "Poppins"),
  INTER("Inter", googleFontName = "Inter"),
  OSWALD("Oswald", localResId = R.font.oswald_regular),
  RALEWAY("Raleway", localResId = R.font.raleway_medium),
  QUICKSAND("Quicksand", googleFontName = "Quicksand"),
  NUNITO("Nunito", googleFontName = "Nunito"),
  ABRIL_FATFACE("Abril Fatface", localResId = R.font.abrilfatface_regular),
  ANTON("Anton", localResId = R.font.anton_regular),
  BEBAS_NEUE("Bebas Neue", localResId = R.font.bebasneue_regular),
  ARCHIVO_BLACK("Archivo Black", localResId = R.font.archivoblack_regular),
  RIGHTEOUS("Righteous", googleFontName = "Righteous"),
  LOBSTER("Lobster", localResId = R.font.lobster_regular),
  PACIFICO("Pacifico", localResId = R.font.pacifico_regular),
  PERMANENT_MARKER("Permanent Marker", localResId = R.font.permanentmarker_regular),
  PLAYFAIR_DISPLAY("Playfair Display", googleFontName = "Playfair Display"),
  SHARE_TECH_MONO("Share Tech Mono", localResId = R.font.sharetechmono_regular),
  CREEPSTER("Creepster", localResId = R.font.creepster_regular),
  PRESS_START_2P("Press Start 2P", googleFontName = "Press Start 2P"),
  SILKSCREEN("Silkscreen", localResId = R.font.silkscreen_regular),
  VT323("VT323", googleFontName = "VT323"),
  COURIER_PRIME("Courier Prime", localResId = R.font.courierprime_regular),
  BANGERS("Bangers", localResId = R.font.bangers_regular),
  ORBITRON("Orbitron", localResId = R.font.orbitron_medium),
  COMFORTAA("Comfortaa", googleFontName = "Comfortaa"),
  PATRICK_HAND("Patrick Hand", localResId = R.font.patrickhand_regular),
  SATISFY("Satisfy", googleFontName = "Satisfy"),
  KAUSHAN_SCRIPT("Kaushan Script", localResId = R.font.kaushanscript_regular),
  YELLOWTAIL("Yellowtail", googleFontName = "Yellowtail"),
  COURGETTE("Courgette", localResId = R.font.courgette_regular),
  DANCING_SCRIPT("Dancing Script", googleFontName = "Dancing Script"),
  GREAT_VIBES("Great Vibes", localResId = R.font.greatvibes_regular),
  SACRAMENTO("Sacramento", localResId = R.font.sacramento_regular),

  // Japanese
  DELA_GOTHIC_ONE("Dela Gothic One (日本語)", ScriptCategory.JAPANESE, googleFontName = "Dela Gothic One"),
  KOSUGI_MARU("Kosugi Maru (小杉丸ゴシック)", ScriptCategory.JAPANESE, googleFontName = "Kosugi Maru"),
  CHERRY_BOMB_ONE("Cherry Bomb One", ScriptCategory.JAPANESE, googleFontName = "Cherry Bomb One"),
  DOT_GOTHIC("DotGothic16", ScriptCategory.JAPANESE, localResId = R.font.dotgothic16_regular),

  // Korean
  BLACK_HAN_SANS("Black Han Sans (한국어)", ScriptCategory.KOREAN, googleFontName = "Black Han Sans"),

  // Chinese
  ZCOOL_KUAILE("ZCOOL KuaiLe (简体中文)", ScriptCategory.CHINESE, googleFontName = "ZCOOL KuaiLe"),
  ZCOOL_XIAOWEI("ZCOOL XiaoWei (站酷小薇体)", ScriptCategory.CHINESE, googleFontName = "ZCOOL XiaoWei"),
  MA_SHAN_ZHENG("Ma Shan Zheng (马善政毛笔楷书)", ScriptCategory.CHINESE, googleFontName = "Ma Shan Zheng"),

  // Arabic
  LALEZAR("Lalezar (العربية)", ScriptCategory.ARABIC, googleFontName = "Lalezar"),
  REEM_KUFI("Reem Kufi (العربية)", ScriptCategory.ARABIC, googleFontName = "Reem Kufi"),
  CAIRO("Cairo (القاهرة)", ScriptCategory.ARABIC, googleFontName = "Cairo"),
  ALMARAI("Almarai (المراعي)", ScriptCategory.ARABIC, googleFontName = "Almarai"),

  // Devanagari
  KALAM("Kalam (हिन्दी)", ScriptCategory.DEVANAGARI, googleFontName = "Kalam"),
  RAJDHANI("Rajdhani (हिन्दी)", ScriptCategory.DEVANAGARI, googleFontName = "Rajdhani"),
  MODAK("Modak (िन्दी)", ScriptCategory.DEVANAGARI, googleFontName = "Modak"),

  // Thai
  ITIM("Itim (ไทย)", ScriptCategory.THAI, googleFontName = "Itim"),

  // Khmer
  ANKOR("Ankor (សួស្តី)", ScriptCategory.KHMER, googleFontName = "Ankor"),

  // Hebrew
  FREDOKA("Fredoka", ScriptCategory.HEBREW, localResId = R.font.fredoka),
}
