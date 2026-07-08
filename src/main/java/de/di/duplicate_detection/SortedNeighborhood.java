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


        for (int sortingKey : sortingKeys) {
            Record[] sortedRecords = records.clone();
            Arrays.sort(sortedRecords, Comparator.comparing(record -> record.getValues()[sortingKey]));

            for (int i = 0; i < sortedRecords.length; i++) {
                for (int j = i + 1; j < Math.min(i + windowSize, sortedRecords.length); j++) {
                    Record record1 = sortedRecords[i];
                    Record record2 = sortedRecords[j];

                    double similarity = recordComparator.compare(record1.getValues(), record2.getValues());
                    if (recordComparator.isDuplicate(similarity))
                        duplicates.add(new Duplicate(record1.getIndex(), record2.getIndex(), similarity, relation));
                }
            }
        }


        return duplicates;
    }

    public static RecordComparator suggestRecordComparatorFor(Relation relation) {
        List<AttrSimWeight> attrSimWeights = new ArrayList<>(relation.getAttributes().length);
        double threshold = 0.0;


        int numAttributes = relation.getAttributes().length;
        String[][] columns = relation.getColumns();
        int numRecords = relation.getRecords().length;

      
        List<Integer> usefulAttributes = new ArrayList<>();
        for (int attribute = 2; attribute < numAttributes; attribute++) {
            long nonEmpty = Arrays.stream(columns[attribute])
                    .filter(v -> v != null && !v.isEmpty())
                    .count();
            double fillRate = (double) nonEmpty / numRecords;
            if (fillRate >= 0.10)
                usefulAttributes.add(attribute);
        }

        double weight = 1.0 / usefulAttributes.size();
        for (int attribute : usefulAttributes) {
            double averageLength = Arrays.stream(columns[attribute])
                    .mapToInt(value -> value == null ? 0 : value.length())
                    .average()
                    .orElse(0);

            if (averageLength > 15)
                attrSimWeights.add(new AttrSimWeight(attribute, new Jaccard(new Tokenizer(3, true), false), weight));
            else
                attrSimWeights.add(new AttrSimWeight(attribute, new Levenshtein(true), weight));
        }

        threshold = 0.5;

        return new RecordComparator(attrSimWeights, threshold);
    }
}