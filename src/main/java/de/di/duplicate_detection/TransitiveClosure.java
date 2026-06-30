package de.di.duplicate_detection;

import de.di.Relation;
import de.di.duplicate_detection.structures.Duplicate;

import java.util.HashSet;
import java.util.Set;

public class TransitiveClosure {

    public Set<Duplicate> calculate(Set<Duplicate> duplicates) {
        Set<Duplicate> closedDuplicates = new HashSet<>(2 * duplicates.size());

        if (duplicates.size() <= 1)
            return duplicates;

        Relation relation = duplicates.iterator().next().getRelation();
        int numRecords = relation.getRecords().length;

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //                                      DATA INTEGRATION ASSIGNMENT                                           //

        // build an adjacency matrix from the duplicate pairs
        boolean[][] matrix = new boolean[numRecords][numRecords];
        for (Duplicate d : duplicates) {
            matrix[d.getIndex1()][d.getIndex2()] = true;
            matrix[d.getIndex2()][d.getIndex1()] = true; // duplicates are commutative
        }

        // Warshall's algorithm: if i-k and k-j are connected, then i-j is connected
        for (int k = 0; k < numRecords; k++) {
            for (int i = 0; i < numRecords; i++) {
                for (int j = 0; j < numRecords; j++) {
                    if (matrix[i][k] && matrix[k][j]) {
                        matrix[i][j] = true;
                    }
                }
            }
        }

        // read all connected pairs back out as duplicates (only i < j to avoid duplicates and identity)
        for (int i = 0; i < numRecords; i++) {
            for (int j = i + 1; j < numRecords; j++) {
                if (matrix[i][j]) {
                    closedDuplicates.add(new Duplicate(i, j, 1.0, relation));
                }
            }
        }

        //                                                                                                            //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        return closedDuplicates;
    }
}