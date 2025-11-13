package org.example;

import java.util.ArrayList;
import java.util.List;

/**
 * Knuth–Morris–Pratt string searching algorithm implementation.
 *
 * Provides a search method that returns starting indices of all occurrences
 * of a pattern in a given text. Also includes a helper to compute the LPS array.
 *
 * Complexity:
 * - Time: O(n + m) where n = text.length(), m = pattern.length()
 * - Space: O(m) additional for the LPS array (plus output list)
 */
public class KMP {

    /**
     * Compute the longest prefix-suffix (LPS) array for the pattern.
     * lps[i] = the length of the longest proper prefix of pattern[0..i]
     * which is also a suffix of this substring.
     *
     * @param pattern non-null pattern string
     * @return lps array of length pattern.length()
     */
    public static int[] computeLPS(String pattern) {
        int m = pattern.length();
        int[] lps = new int[m];
        if (m == 0) return lps;

        lps[0] = 0; // length 0 for first char
        int len = 0; // length of previous longest prefix-suffix
        int i = 1;
        while (i < m) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len != 0) {
                    // fall back to previous longest prefix suffix
                    len = lps[len - 1];
                    // do NOT increment i here
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }
        return lps;
    }

    /**
     * Search for all occurrences of pattern in text using KMP.
     * Returns a list of starting indices (0-based).
     *
     * @param text the text to search in
     * @param pattern the pattern to search for
     * @return list of start indices where pattern occurs in text
     */
    public static List<Integer> search(String text, String pattern) {
        List<Integer> matches = new ArrayList<>();
        if (pattern == null || text == null) return matches;
        int n = text.length();
        int m = pattern.length();
        if (m == 0) {
            // define: empty pattern matches at every position
            for (int i = 0; i <= n; i++) matches.add(i);
            return matches;
        }
        int[] lps = computeLPS(pattern);

        int i = 0; // index for text
        int j = 0; // index for pattern
        while (i < n) {
            if (text.charAt(i) == pattern.charAt(j)) {
                i++; j++;
                if (j == m) {
                    matches.add(i - j);
                    // continue searching for next possible match
                    j = lps[j - 1];
                }
            } else {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }
        return matches;
    }
}

