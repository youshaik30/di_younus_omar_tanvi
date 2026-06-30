package de.di.schema_matching;

import de.di.Relation;
import de.di.schema_matching.structures.SimilarityMatrix;
import de.di.similarity_measures.Jaccard;
import de.di.similarity_measures.helper.Tokenizer;

public class FirstLineSchemaMatcher {

    public SimilarityMatrix match(Relation sourceRelation, Relation targetRelation) {
        String[][] sourceColumns = sourceRelation.getColumns();
        String[][] targetColumns = targetRelation.getColumns();

        double[][] matrix = new double[sourceColumns.length][];
        for (int i = 0; i < sourceColumns.length; i++)
            matrix[i] = new double[targetColumns.length];

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //                                      DATA INTEGRATION ASSIGNMENT                                           //

        // use Jaccard with set semantics to compare value domains of each column pair
        // if two columns share many of the same values, they are likely the same attribute
        Jaccard jaccard = new Jaccard(new Tokenizer(2, false), false); // set semantics

        for (int i = 0; i < sourceColumns.length; i++) {
            for (int j = 0; j < targetColumns.length; j++) {
                // compare the value sets of source column i and target column j directly
                matrix[i][j] = jaccard.calculate(sourceColumns[i], targetColumns[j]);
            }
        }

        //                                                                                                            //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        return new SimilarityMatrix(matrix, sourceRelation, targetRelation);
    }
}