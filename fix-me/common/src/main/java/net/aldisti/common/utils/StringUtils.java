package net.aldisti.common.utils;

import java.util.Arrays;

public class StringUtils {
    private StringUtils() {}

    public static boolean isBlank(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * Left pad a String with a specified character.

     * @param str  the String to pad out, may be null
     * @param size  the size to pad to
     * @param padChar  the character to pad with
     * @return left padded String or original String if no padding is necessary,
     *  {@code null} if null String input
     * @since 2.0
     */
    public static String leftPad(final String str, final int size, final char padChar) {
        if (str == null) {
            return null;
        }
        final int pads = size - str.length();
        if (pads <= 0) {
            return str; // returns original String when possible
        }
        return repeat(padChar, pads).concat(str);
    }

    /**
     * Returns padding using the specified delimiter repeated
     * to a given length.
     *
     * @param ch  character to repeat
     * @param repeat  number of times to repeat char, negative treated as zero
     * @return String with repeated character
     */
    public static String repeat(char ch, int repeat) {
        return repeat <= 0 ? "" : new String(fill(new char[repeat], ch));
    }

    /**
     * Capitalizes a String changing the first character to title case as
     * per {@link Character#toTitleCase(int)}. No other characters are changed.
     *
     * @param str the String to capitalize, may be null
     * @return the capitalized String, {@code null} if null String input
     */
    public static String capitalize(final String str) {
        if (isBlank(str)) {
            return str;
        }

        final int strLen = str.length();
        final int firstCodepoint = str.codePointAt(0);
        final int newCodePoint = Character.toTitleCase(firstCodepoint);
        if (firstCodepoint == newCodePoint) {
            // already capitalized
            return str;
        }

        final int[] newCodePoints = new int[strLen]; // cannot be longer than the char array
        int outOffset = 0;
        newCodePoints[outOffset++] = newCodePoint; // copy the first code point
        for (int inOffset = Character.charCount(firstCodepoint); inOffset < strLen; ) {
            final int codePoint = str.codePointAt(inOffset);
            newCodePoints[outOffset++] = codePoint; // copy the remaining ones
            inOffset += Character.charCount(codePoint);
        }
        return new String(newCodePoints, 0, outOffset);
    }

    /**
     * Fills and returns the given array.
     *
     * @param a   the array to be filled (may be null).
     * @param val the value to be stored in all elements of the array.
     * @return the given array.
     * @see Arrays#fill(char[],char)
     */
    public static char[] fill(final char[] a, final char val) {
        if (a != null) {
            Arrays.fill(a, val);
        }
        return a;
    }

    /**
     * Strips any of a set of characters from the start and end of a String.
     * This is similar to {@link String#trim()} but allows the characters
     * to be stripped to be controlled.
     *
     * <p>A {@code null} input String returns {@code null}.
     * An empty string ("") input returns the empty string.</p>
     *
     * @param str  the String to remove characters from, may be null
     * @param chars  the characters to remove, null treated as whitespace
     * @return the stripped String, {@code null} if null String input
     */
    public static String strip(String str, String chars) {
        return stripEnd(stripStart(str, chars), chars);
    }

    /**
     * Strips any of a set of characters from the start of a String.
     *
     * <p>A {@code null} input String returns {@code null}.
     * An empty string ("") input returns the empty string.</p>
     *
     * @param str  the String to remove characters from, may be null
     * @param chars  the characters to remove, null treated as whitespace
     * @return the stripped String, {@code null} if null String input
     */
    public static String stripStart(String str, String chars) {
        if (isBlank(str) || isBlank(chars))
            return str;

        int start = 0;
        while (start < str.length() && chars.indexOf(str.charAt(start)) != -1)
            start++;

        return str.substring(start);
    }

    /**
     * Strips any of a set of characters from the end of a String.
     *
     * <p>A {@code null} input String returns {@code null}.
     * An empty string ("") input returns the empty string.</p>
     *
     * <p>If the stripChars String is {@code null}, whitespace is
     * stripped as defined by {@link Character#isWhitespace(char)}.</p>
     *
     * @param str  the String to remove characters from, may be null
     * @param chars  the set of characters to remove, null treated as whitespace
     * @return the stripped String, {@code null} if null String input
     */
    public static String stripEnd(String str, String chars) {
        if (isBlank(str) || isBlank(chars))
            return str;

        int end = str.length() - 1;
        while (end >= 0 && chars.indexOf(str.charAt(end)) != -1)
            end--;

        return str.substring(0, end + 1);
    }
}
