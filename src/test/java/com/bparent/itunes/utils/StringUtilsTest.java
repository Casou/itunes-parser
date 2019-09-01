package com.bparent.itunes.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringUtilsTest {

    @Test
    void areSame_should_return_true_if_string_are_identical() {
        assertTrue(StringUtils.areAlmostIdentical("Same string", "Same string"));
    }

    @Test
    void areSame_should_return_true_if_string_are_identical_except_case() {
        assertTrue(StringUtils.areAlmostIdentical("Same string", "same StrinG"));
    }

    @Test
    void areSame_should_return_true_if_string_are_one_character_different() {
        assertTrue(StringUtils.areAlmostIdentical("Same string", "Same strong"));
    }

    @Test
    void areSame_should_return_true_if_string_are_one_less_character_different() {
        assertTrue(StringUtils.areAlmostIdentical("Same string", "Same stringg"));
    }

    @Test
    void areSame_should_return_true_if_string_are_one_less_character_different_except_case() {
        assertTrue(StringUtils.areAlmostIdentical("Same string", "Same StrinGG"));
    }

    @Test
    void areSame_should_return_true_if_string_are_one_character_different_except_case() {
        assertTrue(StringUtils.areAlmostIdentical("Same string", "Same StronG"));
    }

    @Test
    void areSame_should_return_true_if_string_are_two_characters_different_except_case() {
        assertTrue(StringUtils.areAlmostIdentical("Same string", "Same StrinGGG"));
    }

    @Test
    void areSame_should_return_false_if_string_are_three_characters_different_or_more() {
        assertFalse(StringUtils.areAlmostIdentical("Same string", "Same stranger"));
        assertFalse(StringUtils.areAlmostIdentical("Same string", "Totally fuck up string"));
    }

    @Test
    void getLevenshteinDistance_should_return_0_if_string_are_identical() {
        assertEquals(Integer.valueOf(0), StringUtils.getLevenshteinDistance("Same string", "Same string"));
    }

    @Test
    void getLevenshteinDistance_should_return_1_if_string_are_one_character_different_except_case() {
        assertEquals(Integer.valueOf(1), StringUtils.getLevenshteinDistance("Same string", "Same Strong"));
        assertEquals(Integer.valueOf(1), StringUtils.getLevenshteinDistance("Same string", "Same stringg"));
        assertEquals(Integer.valueOf(1), StringUtils.getLevenshteinDistance("Same string", "SAME STRINGG"));
    }

}