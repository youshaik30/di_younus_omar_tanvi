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

        // remember pairs we already compared, so we don't compare the same pair twice
        // (overlapping windows and multiple sorting keys create a lot of repeats)
        Set<Long> comparedPairs = new HashSet<>();

        // run the Sorted Neighborhood Method once for each sorting key
        for (int key : sortingKeys) {
            // sort the records by the value in the current sorting key column
            Record[] sorted = records.clone();
            Arrays.sort(sorted, new Comparator<Record>() {
                @Override
                public int compare(Record r1, Record r2) {
                    return r1.getValues()[key].compareTo(r2.getValues()[key]);
                }
            });

            // slide a window of size windowSize over the sorted records
            for (int i = 0; i < sorted.length; i++) {
                for (int j = i + 1; j < i + windowSize && j < sorted.length; j++) {
                    int idx1 = sorted[i].getIndex();
                    int idx2 = sorted[j].getIndex();

                    // make a unique key for this pair (smaller index first)
                    int lo = Math.min(idx1, idx2);
                    int hi = Math.max(idx1, idx2);
                    long pairKey = (long) lo * records.length + hi;

                    // skip if this pair was already compared before
                    if (!comparedPairs.add(pairKey))
                        continue;

                    double sim = recordComparator.compare(sorted[i].getValues(), sorted[j].getValues());

                    if (recordComparator.isDuplicate(sim)) {
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

        // simple heuristic: compare every attribute with Levenshtein, equal weights, moderate threshold
        for (int i = 0; i < relation.getAttributes().length; i++) {
            attrSimWeights.add(new AttrSimWeight(i, new Levenshtein(true), 1.0));
        }
        threshold = 0.8;

        //                                                                                                            //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        return new RecordComparator(attrSimWeights, threshold);
    }
}