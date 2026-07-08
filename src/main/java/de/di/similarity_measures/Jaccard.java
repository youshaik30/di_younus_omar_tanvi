package de.di.similarity_measures;

import de.di.similarity_measures.helper.Tokenizer;
import lombok.AllArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
public class Jaccard implements SimilarityMeasure {

    private final Tokenizer tokenizer;
    private final boolean bagSemantics;

    @Override
    public double calculate(String string1, String string2) {
        string1 = (string1 == null) ? "" : string1;
        string2 = (string2 == null) ? "" : string2;

        String[] strings1 = this.tokenizer.tokenize(string1);
        String[] strings2 = this.tokenizer.tokenize(string2);
        return this.calculate(strings1, strings2);
    }

    @Override
    public double calculate(String[] strings1, String[] strings2) {
        double jaccardSimilarity = 0;


        if (bagSemantics) {
            // BAG semantics: duplicates count
            // formula: intersection / (|A| + |B|)
            // where intersection = sum of min(count in A, count in B) for each token

            // count how many times each token appears in each array
            Map<String, Integer> freq1 = new HashMap<>();
            for (String t : strings1) {
                freq1.put(t, freq1.getOrDefault(t, 0) + 1);
            }

            Map<String, Integer> freq2 = new HashMap<>();
            for (String t : strings2) {
                freq2.put(t, freq2.getOrDefault(t, 0) + 1);
            }

            // intersection = sum of overlapping counts
            int intersect = 0;
            for (String t : freq1.keySet()) {
                if (freq2.containsKey(t)) {
                    intersect += Math.min(freq1.get(t), freq2.get(t));
                }
            }

            int total = strings1.length + strings2.length;
            if (total > 0) {
                jaccardSimilarity = (double) intersect / total;
            }

        } else {
            // SET semantics: duplicates are removed first
            // formula: |intersection| / |union|

            Set<String> set1 = new HashSet<>();
            for (String t : strings1) set1.add(t);

            Set<String> set2 = new HashSet<>();
            for (String t : strings2) set2.add(t);

            // count how many tokens appear in both sets
            int intersect = 0;
            for (String t : set1) {
                if (set2.contains(t)) intersect++;
            }

            // union size = |A| + |B| - |A ∩ B|
            int unionSize = set1.size() + set2.size() - intersect;
            if (unionSize > 0) {
                jaccardSimilarity = (double) intersect / unionSize;
            }
        }

        return jaccardSimilarity;
    }
}