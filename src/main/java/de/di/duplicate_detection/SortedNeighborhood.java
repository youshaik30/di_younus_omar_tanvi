package de.di.duplicate_detection;

import de.di.Relation;
import de.di.duplicate_detection.structures.AttrSimWeight;
import de.di.duplicate_detection.structures.Duplicate;
import de.di.similarity_measures.Jaccard;
import de.di.similarity_measures.Levenshtein;
import de.di.similarity_measures.helper.Tokenizer;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.*;

public class SortedNeighborhood {

    @Data
    @AllArgsConstructor
    private static class Record {
        private int index;
        private String[] values;
    }

    public Set<Duplicate> detectDuplicates(Relation relation, int[] sortingKeys, int windowSize, RecordComparator recordComparator) {
        Set<Duplicate> duplicates = new HashSet<>();

        Record[] records = new Record[relation.getRecords().length];
        for (int i = 0; i < relation.getRecords().length; i++)
            records[i] = new Record(i, relation.getRecords()[i]);

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //                                      DATA INTEGRATION ASSIGNMENT                                           //

        // do one Sorted Neighborhood run for each sorting key
        for (int k = 0; k < sortingKeys.length; k++) {
            final int key = sortingKeys[k];

            // sort the records by the value in the sorting key column
            Record[] sorted = records.clone();
            Arrays.sort(sorted, (r1, r2) -> r1.getValues()[key].compareTo(r2.getValues()[key]));

            // slide a window over the sorted records and compare records inside the window
            for (int i = 0; i < sorted.length; i++) {
                for (int j = i + 1; j < i + windowSize && j < sorted.length; j++) {
                    double sim = recordComparator.compare(sorted[i].getValues(), sorted[j].getValues());

                    if (recordComparator.isDuplicate(sim)) {
                        int idx1 = sorted[i].getIndex();
                        int idx2 = sorted[j].getIndex();
                        duplicates.add(new Duplicate(idx1, idx2, sim, relation));
                    }
                }
            }
        }

        //                                                                                                            //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        return duplicates;
    }

    public static RecordComparator suggestRecordComparatorFor(Relation relation) {
        List<AttrSimWeight> attrSimWeights = new ArrayList<>(relation.getAttributes().length);
        double threshold = 0.0;

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //                                      DATA INTEGRATION ASSIGNMENT                                           //

        // compare the meaningful text columns (skip column 0 because it is a unique id)
        attrSimWeights.add(new AttrSimWeight(1, new Levenshtein(true), 0.1));
        attrSimWeights.add(new AttrSimWeight(2, new Levenshtein(true), 0.1));
        attrSimWeights.add(new AttrSimWeight(3, new Jaccard(new Tokenizer(3, true), false), 0.2));
        attrSimWeights.add(new AttrSimWeight(4, new Levenshtein(true), 0.1));
        attrSimWeights.add(new AttrSimWeight(5, new Levenshtein(true), 0.1));
        attrSimWeights.add(new AttrSimWeight(6, new Jaccard(new Tokenizer(3, true), false), 0.1));
        attrSimWeights.add(new AttrSimWeight(7, new Levenshtein(true), 0.1));
        attrSimWeights.add(new AttrSimWeight(8, new Levenshtein(true), 0.2));
        threshold = 0.8;

        //                                                                                                            //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        return new RecordComparator(attrSimWeights, threshold);
    }
}