package de.di.duplicate_detection;

import de.di.Relation;
import de.di.duplicate_detection.structures.Duplicate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TransitiveClosure {

    // find the "root" of a record's group, with path compression
    private int find(int[] parent, int x) {
        while (parent[x] != x) {
            parent[x] = parent[parent[x]]; // path compression
            x = parent[x];
        }
        return x;
    }

    public Set<Duplicate> calculate(Set<Duplicate> duplicates) {
        Set<Duplicate> closedDuplicates = new HashSet<>(2 * duplicates.size());

        if (duplicates.size() <= 1)
            return duplicates;

        Relation relation = duplicates.iterator().next().getRelation();
        int numRecords = relation.getRecords().length;

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //                                      DATA INTEGRATION ASSIGNMENT                                           //

        // Union-Find: each record starts as its own group
        int[] parent = new int[numRecords];
        for (int i = 0; i < numRecords; i++)
            parent[i] = i;

        // union every duplicate pair into the same group
        for (Duplicate d : duplicates) {
            int root1 = find(parent, d.getIndex1());
            int root2 = find(parent, d.getIndex2());
            if (root1 != root2)
                parent[root1] = root2;
        }

        // group all records by their root
        Map<Integer, java.util.List<Integer>> groups = new HashMap<>();
        for (Duplicate d : duplicates) {
            for (int idx : new int[]{d.getIndex1(), d.getIndex2()}) {
                int root = find(parent, idx);
                if (!groups.containsKey(root))
                    groups.put(root, new java.util.ArrayList<>());
                if (!groups.get(root).contains(idx))
                    groups.get(root).add(idx);
            }
        }

        // for each group, create a duplicate for every pair inside it
        for (java.util.List<Integer> group : groups.values()) {
            for (int a = 0; a < group.size(); a++) {
                for (int b = a + 1; b < group.size(); b++) {
                    closedDuplicates.add(new Duplicate(group.get(a), group.get(b), 1.0, relation));
                }
            }
        }

        //                                                                                                            //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        return closedDuplicates;
    }
}