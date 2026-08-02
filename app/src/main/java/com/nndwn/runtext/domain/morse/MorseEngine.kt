package com.nndwn.runtext.domain.morse

/**
 * Represents a single timing element in International Morse Code.
 * Each element has a [durationMultiplier] relative to the base unit duration.
 *
 * ITU standard timing:
 *   Dot  = 1 unit, Dash = 3 units
 *   Intra-char gap = 1 unit, Char gap = 3 units, Word gap = 7 units
 */
sealed class MorseElement(val durationMultiplier: Int) {
    data object Dot : MorseElement(1)
    data object Dash : MorseElement(3)
    data object IntraCharGap : MorseElement(1)
    data object CharGap : MorseElement(3)
    data object WordGap : MorseElement(7)
}

object MorseEngine {

    private val LATIN_MORSE_MAP: Map<Char, String> = mapOf(
        'A' to ".-",    'B' to "-...",  'C' to "-.-.",  'D' to "-..",
        'E' to ".",     'F' to "..-.",  'G' to "--.",   'H' to "....",
        'I' to "..",    'J' to ".---",  'K' to "-.-",   'L' to ".-..",
        'M' to "--",    'N' to "-.",    'O' to "---",   'P' to ".--.",
        'Q' to "--.-",  'R' to ".-.",   'S' to "...",   'T' to "-",
        'U' to "..-",   'V' to "...-",  'W' to ".--",  'X' to "-..-",
        'Y' to "-.--",  'Z' to "--..",
        '0' to "-----", '1' to ".----", '2' to "..---",
        '3' to "...--", '4' to "....-", '5' to ".....",
        '6' to "-....", '7' to "--...", '8' to "---..", '9' to "----.",
        '.' to ".-.-.-", ',' to "--..--", '?' to "..--..",
        '\'' to ".----.", '!' to "-.-.--", '/' to "-..-.",
        '(' to "-.--.",  ')' to "-.--.-", '&' to ".-...",
        ':' to "---...", ';' to "-.-.-.", '=' to "-...-",
        '+' to ".-.-.",  '-' to "-....-", '_' to "..--.-",
        '"' to ".-..-.", '$' to "...-..-", '@' to ".--.-.",
    )
    private val HANGUL_JAMO_MORSE_MAP: Map<Char, String> = mapOf(
        // Consonants (Cho-seong & Jong-seong)
        'ㄱ' to ".-..",  'ㄴ' to "..-.",  'ㄷ' to "-...",  'ㄹ' to "...-",
        'ㅁ' to "--",    'ㅂ' to ".--",   'ㅅ' to "--.",   'ㅇ' to "-.-",
        'ㅈ' to ".--.",  'ㅊ' to "-.-.",  'ㅋ' to "-..-",  'ㅌ' to "--..",
        'ㅍ' to "---",   'ㅎ' to ".---",

        // Double Consonants (Konsonan Ganda)
        'ㄲ' to ".-.. .-..",  'ㄸ' to "-... -...",  'ㅃ' to ".-- .--",
        'ㅆ' to "--. --.",    'ㅉ' to ".--. .--.",

        // Vowels (Jung-seong)
        'ㅏ' to ".",     'ㅑ' to "..",    'ㅓ' to "-",     'ㅕ' to "...",
        'ㅗ' to ".-",    'ㅛ' to "-.-",   'ㅜ' to "....",  'ㅠ' to ".-.",
        'ㅡ' to "-..",   'ㅣ' to "..-",   'ㅐ' to "--.-",  'ㅔ' to "-.--",
        'ㅒ' to ".. --.-", 'ㅖ' to "... -.--",
        'ㅘ' to ".- .",   'ㅙ' to ".- --.-", 'ㅚ' to ".- ..-",
        'ㅝ' to ".... -", 'ㅞ' to ".... -.--", 'ㅟ' to ".... ..-",
        'ㅢ' to "-.. ..-"
    )

    private val CHO_SEONG = charArrayOf(
        'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ',
        'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
    )

    private val JUNG_SEONG = charArrayOf(
        'ㅏ', 'ㅐ', 'ㅑ', 'ㅒ', 'ㅓ', 'ㅔ', 'ㅕ', 'ㅖ', 'ㅗ', 'ㅘ',
        'ㅙ', 'ㅚ', 'ㅛ', 'ㅜ', 'ㅝ', 'ㅞ', 'ㅟ', 'ㅠ', 'ㅡ', 'ㅢ', 'ㅣ'
    )

    private val JONG_SEONG = charArrayOf(
        ' ', 'ㄱ', 'ㄲ', 'ㄳ', 'ㄴ', 'ㄵ', 'ㄶ', 'ㄷ', 'ㄹ', 'ㄺ',
        'ㄻ', 'ㄼ', 'ㄽ', 'ㄾ', 'ㄿ', 'ㅀ', 'ㅁ', 'ㅂ', 'ㅄ', 'ㅅ',
        'ㅆ', 'ㅇ', 'ㅈ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
    )

    /** Pre-built SOS pattern: ···−−−··· */
    val SOS_PATTERN: List<MorseElement> = buildList {
        add(MorseElement.Dot); add(MorseElement.IntraCharGap)
        add(MorseElement.Dot); add(MorseElement.IntraCharGap)
        add(MorseElement.Dot); add(MorseElement.CharGap)
        add(MorseElement.Dash); add(MorseElement.IntraCharGap)
        add(MorseElement.Dash); add(MorseElement.IntraCharGap)
        add(MorseElement.Dash); add(MorseElement.CharGap)
        add(MorseElement.Dot); add(MorseElement.IntraCharGap)
        add(MorseElement.Dot); add(MorseElement.IntraCharGap)
        add(MorseElement.Dot)
    }
    /**
     * Convert [text] to a list of [MorseElement] with proper inter-symbol,
     * inter-character, and inter-word gaps. Unknown characters are silently skipped.
     */
    fun textToMorseElements(text: String): List<MorseElement> {
        val elements = mutableListOf<MorseElement>()
        val words = text.split(' ')

        for ((wordIndex, word) in words.withIndex()) {
            if (wordIndex > 0 && word.isNotEmpty()) {
                elements.add(MorseElement.WordGap)
            }
            var charAdded = false
            for (char in word) {
                // Konversi karakter tunggal (Latin / Syllable Hangul / Jamo) ke string kode morse
                val morseCodes = getMorseStringForChar(char)
                if (morseCodes.isEmpty()) continue

                if (charAdded) elements.add(MorseElement.CharGap)

                for ((codeIndex, codeStr) in morseCodes.withIndex()) {
                    if (codeIndex > 0) elements.add(MorseElement.CharGap)
                    for ((symbolIndex, symbol) in codeStr.withIndex()) {
                        if (symbolIndex > 0) elements.add(MorseElement.IntraCharGap)
                        when (symbol) {
                            '.' -> elements.add(MorseElement.Dot)
                            '-' -> elements.add(MorseElement.Dash)
                        }
                    }
                }
                charAdded = true
            }
        }
        return elements
    }
    private fun getMorseStringForChar(char: Char): List<String> {

        val upperChar = char.uppercaseChar()

        LATIN_MORSE_MAP[upperChar]?.let { return listOf(it) }


        HANGUL_JAMO_MORSE_MAP[char]?.let { return it.split(' ') }

        if (char.code in 0xAC00..0xD7A3) {
            val syllableIndex = char.code - 0xAC00
            val choIndex = syllableIndex / (21 * 28)
            val jungIndex = (syllableIndex % (21 * 28)) / 28
            val jongIndex = syllableIndex % 28

            val result = mutableListOf<String>()

            HANGUL_JAMO_MORSE_MAP[CHO_SEONG[choIndex]]?.let { result.addAll(it.split(' ')) }
            HANGUL_JAMO_MORSE_MAP[JUNG_SEONG[jungIndex]]?.let { result.addAll(it.split(' ')) }


            if (jongIndex > 0) {
                val jongChar = JONG_SEONG[jongIndex]
                val decompoundedJong = decompoundJongSeong(jongChar)
                for (j in decompoundedJong) {
                    HANGUL_JAMO_MORSE_MAP[j]?.let { result.addAll(it.split(' ')) }
                }
            }
            return result
        }

        return emptyList()
    }
    private fun decompoundJongSeong(jong: Char): List<Char> = when (jong) {
        'ㄳ' -> listOf('ㄱ', 'ㅅ')
        'ㄵ' -> listOf('ㄴ', 'ㅈ')
        'ㄶ' -> listOf('ㄴ', 'ㅎ')
        'ㄺ' -> listOf('ㄹ', 'ㄱ')
        'ㄻ' -> listOf('ㄹ', 'ㅁ')
        'ㄼ' -> listOf('ㄹ', 'ㅂ')
        'ㄽ' -> listOf('ㄹ', 'ㅅ')
        'ㄾ' -> listOf('ㄹ', 'ㅌ')
        'ㄿ' -> listOf('ㄹ', 'ㅍ')
        'ㅀ' -> listOf('ㄹ', 'ㅎ')
        'ㅄ' -> listOf('ㅂ', 'ㅅ')
        else -> listOf(jong)
    }


    /**
     * Calculate the duration of one Morse unit in milliseconds.
     * Based on the PARIS standard: "PARIS" = 50 units → 1 unit = 1200/wpm ms.
     */
    fun getUnitDurationMs(wpm: Int): Long = 1200L / wpm.coerceAtLeast(1)

    /** Returns true if [element] is a signal (dot or dash) rather than a gap. */
    fun isSignalElement(element: MorseElement): Boolean =
        element is MorseElement.Dot || element is MorseElement.Dash
}