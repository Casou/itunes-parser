package com.bparent.itunes.utils;

import org.apache.commons.text.similarity.LevenshteinDistance;

public class StringUtils {

    private static final LevenshteinDistance levenshteinDistance = new LevenshteinDistance();

    private StringUtils() {}

    public static Integer getLevenshteinDistance(String s1, String s2) {
        return levenshteinDistance.apply(s1.toLowerCase(), s2.toLowerCase());
    }

    public static boolean areAlmostIdentical(String s1, String s2) {
        return getLevenshteinDistance(s1, s2) < 3;
    }

}
