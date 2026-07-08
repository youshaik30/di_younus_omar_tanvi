package de.di.schema_matching;

import de.di.Relation;
import de.di.schema_matching.structures.SimilarityMatrix;
import de.di.similarity_measures.Jaccard;
import de.di.similarity_measures.helper.Tokenizer;

public class FirstLineSchemaMatcher {

    public SimilarityMatrix match(Relation sourceRelation, Relation targetRelation) {
        String[][] sourceColumns = sourceRelation.getColumns();
        String[][] targetColumns = targetRelation.getColumns();
        String[] sourceNames = sourceRelation.getAttributes();
        String[] targetNames = targetRelation.getAttributes();

        double[][] matrix = new double[sourceColumns.length][];
        for (int i = 0; i < sourceColumns.length; i++)
            matrix[i] = new double[targetColumns.length];

        
        Jaccard nameJaccard = new Jaccard(new Tokenizer(3, true), false);

        
        Jaccard valueJaccard = new Jaccard(new Tokenizer(2, false), false);

        for (int i = 0; i < sourceColumns.length; i++) {


            String[] srcValues = toLowercase(sourceColumns[i]);
            String[] srcWords  = extractAllWords(sourceColumns[i]);

            for (int j = 0; j < targetColumns.length; j++) {

                String[] tgtValues = toLowercase(targetColumns[j]);
                String[] tgtWords  = extractAllWords(targetColumns[j]);

                double valueSim = valueJaccard.calculate(srcValues, tgtValues);

                double wordSim = valueJaccard.calculate(srcWords, tgtWords);

                String srcName = sourceNames[i].toLowerCase();
                String tgtName = targetNames[j].toLowerCase();
                double nameSim = nameJaccard.calculate(srcName, tgtName);

                // Combine: value content is the strongest signal (together 80%),
                // column names are a useful secondary hint (20%)
                matrix[i][j] = 0.5 * valueSim + 0.3 * wordSim + 0.2 * nameSim;
            }
        }

        return new SimilarityMatrix(matrix, sourceRelation, targetRelation);
    }

    private String[] toLowercase(String[] values) {
        String[] result = new String[values.length];
        for (int i = 0; i < values.length; i++)
            result[i] = (values[i] == null) ? "" : values[i].toLowerCase().trim();
        return result;
    }

    private String[] extractAllWords(String[] values) {
        // First pass: count how many non-empty words there are in total
        int total = 0;
        for (String value : values) {
            if (value == null || value.trim().isEmpty()) continue;
            for (String word : value.toLowerCase().split("[^a-zA-Z0-9]+"))
                if (!word.isEmpty()) total++;
        }

        // Second pass: fill the result array with the words
        String[] words = new String[total];
        int index = 0;
        for (String value : values) {
            if (value == null || value.trim().isEmpty()) continue;
            for (String word : value.toLowerCase().split("[^a-zA-Z0-9]+"))
                if (!word.isEmpty()) words[index++] = word;
        }
        return words;
    }
}