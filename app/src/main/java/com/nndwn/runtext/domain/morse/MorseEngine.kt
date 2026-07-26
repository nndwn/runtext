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

    private val MORSE_MAP: Map<Char, String> = mapOf(
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

    /** Pre-built SOS pattern: ···−−−··· */
    val SOS_PATTERN: List<MorseElement> = buildList {
        // S: · · ·
        add(MorseElement.Dot); add(MorseElement.IntraCharGap)
        add(MorseElement.Dot); add(MorseElement.IntraCharGap)
        add(MorseElement.Dot)
        add(MorseElement.CharGap)
        // O: − − −
        add(MorseElement.Dash); add(MorseElement.IntraCharGap)
        add(MorseElement.Dash); add(MorseElement.IntraCharGap)
        add(MorseElement.Dash)
        add(MorseElement.CharGap)
        // S: · · ·
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
        val words = text.uppercase().split(' ')

        for ((wordIndex, word) in words.withIndex()) {
            if (wordIndex > 0 && word.isNotEmpty()) {
                elements.add(MorseElement.WordGap)
            }
            var charAdded = false
            for (char in word) {
                val morseStr = MORSE_MAP[char] ?: continue
                if (charAdded) elements.add(MorseElement.CharGap)
                for ((symbolIndex, symbol) in morseStr.withIndex()) {
                    if (symbolIndex > 0) elements.add(MorseElement.IntraCharGap)
                    when (symbol) {
                        '.' -> elements.add(MorseElement.Dot)
                        '-' -> elements.add(MorseElement.Dash)
                    }
                }
                charAdded = true
            }
        }
        return elements
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