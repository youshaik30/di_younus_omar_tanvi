package de.di.similarity_measures;

import de.di.similarity_measures.helper.MinHash;
import de.di.similarity_measures.helper.Tokenizer;

import java.util.ArrayList;
import java.util.List;

public class LocalitySensitiveHashing implements SimilarityMeasure {

    // The tokenizer that is used to transform string inputs into token lists.
    private final Tokenizer tokenizer;

    // The MinHash functions that are used to calculate the LSH signatures.
    private final List<MinHash> minHashFunctions;

    public LocalitySensitiveHashing(final Tokenizer tokenizer, final int numHashFunctions) {
        this.tokenizer = tokenizer;
        this.minHashFunctions = new ArrayList<>(numHashFunctions);
        for (int i = 0; i < numHashFunctions; i++)
            this.minHashFunctions.add(new MinHash(i));
    }

    /**
     * Calculates the LSH similarity of the two input strings.
     */
    @Override
    public double calculate(final String string1, final String string2) {
        String[] strings1 = this.tokenizer.tokenize(string1);
        String[] strings2 = this.tokenizer.tokenize(string2);
        return this.calculate(strings1, strings2);
    }

    /**
     * Calculates the LSH similarity of the two input string arrays.
     */
    @Override
    public double calculate(final String[] strings1, final String[] strings2) {
        double lshJaccard = 0;
        int k = this.minHashFunctions.size();

        String[] signature1 = new String[k];
        String[] signature2 = new String[k];

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //                                      DATA INTEGRATION ASSIGNMENT                                           //

        // Step 1: Calculate the two signatures using the internal MinHash functions
        for (int i = 0; i < k; i++) {
            signature1[i] = this.minHashFunctions.get(i).hash(strings1);
            signature2[i] = this.minHashFunctions.get(i).hash(strings2);
        }

        // Step 2: Use the signatures to approximate the Jaccard similarity.
        // For LSH, the approximated Jaccard similarity is the fraction of positional matches.
        int matches = 0;
        for (int i = 0; i < k; i++) {
            if (signature1[i].equals(signature2[i])) {
                matches++;
            }
        }
        lshJaccard = (double) matches / k;

        //                                                                                                            //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        return lshJaccard;
    }
}