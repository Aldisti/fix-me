package net.aldisti.common.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringUtilsTest {

    @Test
    void isBlank() {
        assertTrue(StringUtils.isBlank(""));
        assertTrue(StringUtils.isBlank(null));
        assertFalse(StringUtils.isBlank(" "));
        assertFalse(StringUtils.isBlank("ciao"));
        assertFalse(StringUtils.isBlank("1"));
    }

    @Test
    void leftPad() {
        assertNull(StringUtils.leftPad(null, 3, ' '));
        assertEquals("   ciao", StringUtils.leftPad("ciao", 7, ' '));
        assertEquals("   ", StringUtils.leftPad("", 3, ' '));
        assertEquals("ciao", StringUtils.leftPad("ciao", -6, ' '));
        assertEquals("XXXXX42 is better", StringUtils.leftPad("42 is better", 17, 'X'));
    }

    @Test
    void repeat() {
        int size = 10;
        String actual = StringUtils.repeat(' ', size);
        assertNotNull(actual);
        assertEquals(size, actual.length());
        assertEquals("          ", actual);

        assertNotNull(StringUtils.repeat(' ', -1));
        assertEquals(0, StringUtils.repeat(' ', -1).length());
        assertEquals("", StringUtils.repeat(' ', -1));
    }

    @Test
    void capitalize() {
        assertNull(StringUtils.capitalize(null));
        assertEquals("", StringUtils.capitalize(""));
        assertEquals("A", StringUtils.capitalize("a"));
        assertEquals("Avezzano", StringUtils.capitalize("avezzano"));
        assertEquals("42", StringUtils.capitalize("42"));
    }

    @Test
    void fill() {
    }

    @Test
    void strip() {
        assertNull(StringUtils.strip(null, "ciao"));
        assertEquals("", StringUtils.strip("", null));
        assertEquals(" ciao", StringUtils.strip(" ciao", ""));
        assertEquals("", StringUtils.strip("abc", "abc"));
        assertEquals("  abc", StringUtils.strip("  abc", null));
        assertEquals("ab", StringUtils.strip("abc  ", "c "));
        assertEquals("abc", StringUtils.strip(" abc  ", "b "));
        assertEquals("  abc", StringUtils.strip("  abcyx", "xyz"));
    }

    @Test
    void stripStart() {
        assertNull(StringUtils.stripStart(null, "ciao"));
        assertEquals("", StringUtils.stripStart("", null));
        assertEquals(" abc", StringUtils.stripStart(" abc", ""));
        assertEquals(" abc ", StringUtils.stripStart(" abc ", null));
        assertEquals("abc", StringUtils.stripStart("  abc", " "));
        assertEquals("abcxxx", StringUtils.stripStart("abcxxx", "x"));
        assertEquals("abc|", StringUtils.stripStart("|abc|", "|"));
        assertEquals("abc  ", StringUtils.stripStart("yxabc  ", "xyz"));
    }

    @Test
    void stripEnd() {
        assertNull(StringUtils.stripEnd(null, "ciao"));
        assertEquals("", StringUtils.stripEnd("", null));
        assertEquals("abc ", StringUtils.stripEnd("abc ", ""));
        assertEquals(" abc ", StringUtils.stripEnd(" abc ", null));
        assertEquals("abc", StringUtils.stripEnd("abc   ", " "));
        assertEquals("xyzabc", StringUtils.stripEnd("xyzabc", "xyz"));
        assertEquals("|abc", StringUtils.stripEnd("|abc|", "|"));
        assertEquals("  abc", StringUtils.stripEnd("  abczy", "xyz"));
    }
}