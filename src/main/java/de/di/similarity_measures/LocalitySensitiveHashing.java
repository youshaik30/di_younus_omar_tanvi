package de.di.similarity_measures;

import de.di.similarity_measures.helper.MinHash;
import de.di.similarity_measures.helper.Tokenizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LocalitySensitiveHashing implements SimilarityMeasure {

    private final Tokenizer tokenizer;
    private final List<MinHash> minHashFunctions;

    public LocalitySensitiveHashing(final Tokenizer tokenizer, final int numHashFunctions) {
        this.tokenizer = tokenizer;
        this.minHashFunctions = new ArrayList<>(numHashFunctions);
        for (int i = 0; i < numHashFunctions; i++)
            this.minHashFunctions.add(new MinHash(i));
    }

    @Override
    public double calculate(final String string1, final String string2) {
        String[] strings1 = this.tokenizer.tokenize(string1);
        String[] strings2 = this.tokenizer.tokenize(string2);
        return this.calculate(strings1, strings2);
    }

    @Override
    public double calculate(final String[] strings1, final String[] strings2) {
        double lshJaccard = 0;
        int k = this.minHashFunctions.size();

        String[] signature1 = new String[k];
        String[] signature2 = new String[k];

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //                                      DATA INTEGRATION ASSIGNMENT                                           //

        // apply each MinHash function to both token arrays to build the two signatures
        for (int i = 0; i < k; i++) {
            signature1[i] = this.minHashFunctions.get(i).hash(strings1);
            signature2[i] = this.minHashFunctions.get(i).hash(strings2);
        }

        // count how many positions agree between the two signatures
        // the fraction of agreements is the LSH estimate of Jaccard similarity
        int matches = 0;
        for (int i = 0; i < k; i++) {
            if (signature1[i].equals(signature2[i])) {
                matches++;
            }
        }
        lshJaccard = (k == 0) ? 0.0 : (double) matches / k;

        //                                                                                                            //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        return lshJaccard;
    }
}