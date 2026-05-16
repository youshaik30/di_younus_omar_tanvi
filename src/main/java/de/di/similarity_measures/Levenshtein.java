package de.di.similarity_measures;

import lombok.AllArgsConstructor;

import java.util.Arrays;

@AllArgsConstructor
public class Levenshtein implements SimilarityMeasure {

    public static int min(int... numbers) {
        return Arrays.stream(numbers).min().orElse(Integer.MAX_VALUE);
    }

    private final boolean withDamerau;

    @Override
    public double calculate(final String string1, final String string2) {
        double levenshteinSimilarity = 0;

        String s1 = string1 == null ? "" : string1;
        String s2 = string2 == null ? "" : string2;

        int len1 = s1.length();
        int len2 = s2.length();

        if (len1 == 0 && len2 == 0) return 1.0;
        if (len1 == 0 || len2 == 0) return 0.0;

        int[] upperupperLine = new int[len1 + 1];
        int[] upperLine = new int[len1 + 1];
        int[] lowerLine = new int[len1 + 1];

        for (int i = 0; i <= len1; i++) {
            upperLine[i] = i;
        }

        for (int j = 1; j <= len2; j++) {
            lowerLine[0] = j;

            for (int i = 1; i <= len1; i++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;

                lowerLine[i] = min(
                        lowerLine[i - 1] + 1,
                        upperLine[i] + 1,
                        upperLine[i - 1] + cost
                );

                if (withDamerau && i > 1 && j > 1
                        && s1.charAt(i - 1) == s2.charAt(j - 2)
                        && s1.charAt(i - 2) == s2.charAt(j - 1)) {
                    lowerLine[i] = Math.min(lowerLine[i], upperupperLine[i - 2] + 1);
                }
            }

            int[] temp = upperupperLine;
            upperupperLine = upperLine;
            upperLine = lowerLine;
            lowerLine = temp;
        }

        int distance = upperLine[len1];
        levenshteinSimilarity = 1.0 - ((double) distance / Math.max(len1, len2));

        return levenshteinSimilarity;
    }

    @Override
    public double calculate(final String[] strings1, final String[] strings2) {
        double levenshteinSimilarity = 0;

        String[] s1 = strings1 == null ? new String[0] : strings1;
        String[] s2 = strings2 == null ? new String[0] : strings2;

        int len1 = s1.length;
        int len2 = s2.length;

        if (len1 == 0 && len2 == 0) return 1.0;
        if (len1 == 0 || len2 == 0) return 0.0;

        int[] upperupperLine = new int[len1 + 1];
        int[] upperLine = new int[len1 + 1];
        int[] lowerLine = new int[len1 + 1];

        for (int i = 0; i <= len1; i++) {
            upperLine[i] = i;
        }

        for (int j = 1; j <= len2; j++) {
            lowerLine[0] = j;

            for (int i = 1; i <= len1; i++) {
                int cost = (s1[i - 1].equals(s2[j - 1])) ? 0 : 1;

                lowerLine[i] = min(
                        lowerLine[i - 1] + 1,
                        upperLine[i] + 1,
                        upperLine[i - 1] + cost
                );

                if (withDamerau && i > 1 && j > 1
                        && s1[i - 1].equals(s2[j - 2])
                        && s1[i - 2].equals(s2[j - 1])) {
                    lowerLine[i] = Math.min(lowerLine[i], upperupperLine[i - 2] + 1);
                }
            }

            int[] temp = upperupperLine;
            upperupperLine = upperLine;
            upperLine = lowerLine;
            lowerLine = temp;
        }

        int distance = upperLine[len1];
        levenshteinSimilarity = 1.0 - ((double) distance / Math.max(len1, len2));

        return levenshteinSimilarity;
    }
}