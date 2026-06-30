package de.di.duplicate_detection;

import de.di.Relation;
import de.di.duplicate_detection.structures.Duplicate;

import java.util.*;

public class TransitiveClosure {

    public Set<Duplicate> calculate(Set<Duplicate> duplicates) {
        Set<Duplicate> closedDuplicates = new HashSet<>(2 * duplicates.size());

        if (duplicates.size() <= 1)
            return duplicates;

        Relation relation = duplicates.iterator().next().getRelation();

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //                                      DATA INTEGRATION ASSIGNMENT                                           //

        // give every record a group id; records in the same group are duplicates of each other
        // start by giving each record its own group id (its own index)
        Map<Integer, Integer> group = new HashMap<>();
        for (Duplicate d : duplicates) {
            if (!group.containsKey(d.getIndex1())) group.put(d.getIndex1(), d.getIndex1());
            if (!group.containsKey(d.getIndex2())) group.put(d.getIndex2(), d.getIndex2());
        }

        // keep merging groups until nothing changes
        boolean changed = true;
        while (changed) {
            changed = false;
            for (Duplicate d : duplicates) {
                int g1 = group.get(d.getIndex1());
                int g2 = group.get(d.getIndex2());
                if (g1 != g2) {
                    // put both records (and everyone in their groups) into the smaller group id
                    int small = Math.min(g1, g2);
                    int big = Math.max(g1, g2);
                    for (Integer record : group.keySet()) {
                        if (group.get(record) == big) {
                            group.put(record, small);
                        }
                    }
                    changed = true;
                }
            }
        }

        // collect all records that belong to each group
        Map<Integer, List<Integer>> members = new HashMap<>();
        for (Integer record : group.keySet()) {
            int g = group.get(record);
            if (!members.containsKey(g)) members.put(g, new ArrayList<>());
            members.get(g).add(record);
        }

        // for each group, make a duplicate for every pair of records in it
        for (List<Integer> list : members.values()) {
            for (int a = 0; a < list.size(); a++) {
                for (int b = a + 1; b < list.size(); b++) {
                    closedDuplicates.add(new Duplicate(list.get(a), list.get(b), 1.0, relation));
                }
            }
        }

        //                                                                                                            //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        return closedDuplicates;
    }
}