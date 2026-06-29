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

        int[] upperupperLine = new int[string1.length() + 1];
        int[] upperLine = new int[string1.length() + 1];
        int[] lowerLine = new int[string1.length() + 1];

        for (int i = 0; i <= string1.length(); i++)
            upperLine[i] = i;

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //                                      DATA INTEGRATION ASSIGNMENT                                           //

        // edge case: both empty strings are the same
        if (string1.length() == 0 && string2.length() == 0) {
            levenshteinSimilarity = 1.0;
        } else {
            // fill the DP table row by row (each row = one char of string2)
            for (int j = 1; j <= string2.length(); j++) {
                lowerLine[0] = j;  // cost to delete j chars from string2 prefix

                for (int i = 1; i <= string1.length(); i++) {
                    int cost = (string1.charAt(i - 1) == string2.charAt(j - 1)) ? 0 : 1;

                    lowerLine[i] = min(
                            upperLine[i] + 1,         // delete
                            lowerLine[i - 1] + 1,     // insert
                            upperLine[i - 1] + cost   // replace or match
                    );

                    // Damerau: if two adjacent chars are swapped, that's only 1 operation
                    if (withDamerau && i > 1 && j > 1
                            && string1.charAt(i - 1) == string2.charAt(j - 2)
                            && string1.charAt(i - 2) == string2.charAt(j - 1)) {
                        lowerLine[i] = min(lowerLine[i], upperupperLine[i - 2] + 1);
                    }
                }

                // shift lines up for next iteration
                upperupperLine = upperLine;
                upperLine = lowerLine;
                lowerLine = new int[string1.length() + 1];
            }

            int distance = upperLine[string1.length()];
            int maxLen = Math.max(string1.length(), string2.length());
            levenshteinSimilarity = 1.0 - (double) distance / maxLen;
        }

        //                                                                                                            //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        return levenshteinSimilarity;
    }

    @Override
    public double calculate(final String[] strings1, final String[] strings2) {
        double levenshteinSimilarity = 0;

        int[] upperupperLine = new int[strings1.length + 1];
        int[] upperLine = new int[strings1.length + 1];
        int[] lowerLine = new int[strings1.length + 1];

        for (int i = 0; i <= strings1.length; i++)
            upperLine[i] = i;

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //                                      DATA INTEGRATION ASSIGNMENT                                           //

        // same algorithm as above, but compare tokens with .equals() instead of chars
        if (strings1.length == 0 && strings2.length == 0) {
            levenshteinSimilarity = 1.0;
        } else {
            for (int j = 1; j <= strings2.length; j++) {
                lowerLine[0] = j;

                for (int i = 1; i <= strings1.length; i++) {
                    int cost = strings1[i - 1].equals(strings2[j - 1]) ? 0 : 1;

                    lowerLine[i] = min(
                            upperLine[i] + 1,
                            lowerLine[i - 1] + 1,
                            upperLine[i - 1] + cost
                    );

                    if (withDamerau && i > 1 && j > 1
                            && strings1[i - 1].equals(strings2[j - 2])
                            && strings1[i - 2].equals(strings2[j - 1])) {
                        lowerLine[i] = min(lowerLine[i], upperupperLine[i - 2] + 1);
                    }
                }

                upperupperLine = upperLine;
                upperLine = lowerLine;
                lowerLine = new int[strings1.length + 1];
            }

            int distance = upperLine[strings1.length];
            int maxLen = Math.max(strings1.length, strings2.length);
            levenshteinSimilarity = 1.0 - (double) distance / maxLen;
        }

        //                                                                                                            //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        return levenshteinSimilarity;
    }
}