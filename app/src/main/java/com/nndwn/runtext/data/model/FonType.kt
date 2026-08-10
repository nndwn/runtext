package com.nndwn.runtext.data.model


enum class ScriptCategory {
    LATIN,
    ARABIC,
    JAPANESE,
    CHINESE,
    KOREAN,
    THAI,
    DEVANAGARI,
    KHMER,
    HEBREW
}
enum class FontType(
    val displayName: String,
    val scriptCategory: ScriptCategory = ScriptCategory.LATIN,
    val googleFontName: String = displayName.substringBefore(" (")
) {
    // ── Modern & Clean ──
    ROBOTO("Roboto"),
    OPEN_SANS("Open Sans"),
    LATO("Lato"),
    MONTSERRAT("Montserrat"),
    POPPINS("Poppins"),
    INTER("Inter"),
    OSWALD("Oswald"),
    RALEWAY("Raleway"),
    QUICKSAND("Quicksand"),
    NUNITO("Nunito"),
    ABRIL_FATFACE("Abril Fatface"),

    // ── Display & Bold ──
    ANTON("Anton"),
    BEBAS_NEUE("Bebas Neue"),
    ARCHIVO_BLACK("Archivo Black"),
    RIGHTEOUS("Righteous"),
    LOBSTER("Lobster"),
    PACIFICO("Pacifico"),
    PERMANENT_MARKER("Permanent Marker"),
    PLAYFAIR_DISPLAY("Playfair Display"),
    SHARE_TECH_MONO("Share Tech Mono"),
    CREEPSTER("Creepster"),

    // ── Retro & Pixel ──
    PRESS_START_2P("Press Start 2P"),
    SILKSCREEN("Silkscreen"),

    VT323("VT323"),
    COURIER_PRIME("Courier Prime"),

    // ── Decorative & Fun ──
    BANGERS("Bangers"),
    ORBITRON("Orbitron"),

    COMFORTAA("Comfortaa"),
    PATRICK_HAND("Patrick Hand"),
    SATISFY("Satisfy"),
    KAUSHAN_SCRIPT("Kaushan Script"),
    YELLOWTAIL("Yellowtail"),
    COURGETTE("Courgette"),
    DANCING_SCRIPT("Dancing Script"),
    GREAT_VIBES("Great Vibes"),
    SACRAMENTO("Sacramento"),

    // ── International Support ──
    DELA_GOTHIC_ONE("Dela Gothic One (日本語)", ScriptCategory.JAPANESE),
    KOSUGI_MARU("Kosugi Maru (小杉丸ゴシック)", ScriptCategory.JAPANESE),
    CHERRY_BOMB_ONE("Cherry Bomb One", ScriptCategory.JAPANESE),
    DOT_GOTHIC("DotGothic16", ScriptCategory.JAPANESE),

    BLACK_HAN_SANS("Black Han Sans (한국어)", ScriptCategory.KOREAN),

    ZCOOL_KUAILE("ZCOOL KuaiLe (简体中文)", ScriptCategory.CHINESE),
    ZCOOL_XIAOWEI("ZCOOL XiaoWei (站酷小薇体)", ScriptCategory.CHINESE),
    MA_SHAN_ZHENG("Ma Shan Zheng (马善政毛笔楷书)", ScriptCategory.CHINESE),

    LALEZAR("Lalezar (العربية)", ScriptCategory.ARABIC),
    REEM_KUFI("Reem Kufi (العربية)", ScriptCategory.ARABIC),
    CAIRO("Cairo (القاهرة)", ScriptCategory.ARABIC),
    ALMARAI("Almarai (المراعي)", ScriptCategory.ARABIC),

    KALAM("Kalam (हिन्दी)", ScriptCategory.DEVANAGARI),
    RAJDHANI("Rajdhani (हिन्दी)", ScriptCategory.DEVANAGARI),
    MODAK("Modak (िन्दी)", ScriptCategory.DEVANAGARI),


    ITIM("Itim (ไทย)", ScriptCategory.THAI),
    ANKOR("Ankor (សួស្តី)", ScriptCategory.KHMER),
    FREDOKA("Fredoka", ScriptCategory.HEBREW),
}
