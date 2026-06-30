package de.di.duplicate_detection;

import de.di.Relation;
import de.di.duplicate_detection.structures.Duplicate;

import java.util.HashSet;
import java.util.Set;

public class TransitiveClosure {

    public Set<Duplicate> calculate(Set<Duplicate> duplicates) {
        Set<Duplicate> closedDuplicates = new HashSet<>();

        if (duplicates == null || duplicates.size() <= 1) {
            return duplicates;
        }

        Relation relation = duplicates.iterator().next().getRelation();
        int numberOfRecords = relation.getRecords().length;

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //                                      DATA INTEGRATION ASSIGNMENT                                           //

        /*
         * The idea is:
         * If record A is duplicate of B, and B is duplicate of C,
         * then A should also be duplicate of C.
         *
         * For this, we put connected duplicate records into the same group.
         */

        int[] group = new int[numberOfRecords];

        // At the beginning every record is in its own group
        for (int i = 0; i < numberOfRecords; i++) {
            group[i] = i;
        }

        // Merge the groups of all known duplicate pairs
        for (Duplicate duplicate : duplicates) {
            int firstRecord = duplicate.getIndex1();
            int secondRecord = duplicate.getIndex2();

            union(group, firstRecord, secondRecord);
        }

        // Now all records that are in the same group are duplicates
        for (int i = 0; i < numberOfRecords; i++) {
            for (int j = i + 1; j < numberOfRecords; j++) {
                if (find(group, i) == find(group, j)) {
                    closedDuplicates.add(new Duplicate(i, j, 1.0, relation));
                }
            }
        }

        //                                                                                                            //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        return closedDuplicates;
    }

    private int find(int[] group, int record) {
        while (group[record] != record) {
            record = group[record];
        }

        return record;
    }

    private void union(int[] group, int firstRecord, int secondRecord) {
        int firstGroup = find(group, firstRecord);
        int secondGroup = find(group, secondRecord);

        if (firstGroup != secondGroup) {
            group[secondGroup] = firstGroup;
        }
    }
}