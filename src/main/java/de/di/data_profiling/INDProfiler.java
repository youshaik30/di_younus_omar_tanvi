package de.di.data_profiling;

import de.di.Relation;
import de.di.data_profiling.structures.IND;

import java.util.*;

public class INDProfiler {

    public List<IND> profile(List<Relation> relations, boolean discoverNary) {
        List<IND> inclusionDependencies = new ArrayList<>();

        // pre-compute value sets for every column in every relation
        List<List<Set<String>>> allColumnSets = new ArrayList<>();
        for (Relation relation : relations) {
            allColumnSets.add(toColumnSets(relation.getColumns()));
        }

        // check every pair of (relation, column) for an IND
        // R1.c1 ⊆ R2.c2 if every value in c1 also appears in c2
        for (int r1 = 0; r1 < relations.size(); r1++) {
            int numCols1 = relations.get(r1).getAttributes().length;

            for (int c1 = 0; c1 < numCols1; c1++) {
                Set<String> set1 = allColumnSets.get(r1).get(c1);

                for (int r2 = 0; r2 < relations.size(); r2++) {
                    int numCols2 = relations.get(r2).getAttributes().length;

                    for (int c2 = 0; c2 < numCols2; c2++) {
                        // skip trivial case: same column in same relation
                        if (r1 == r2 && c1 == c2) continue;

                        Set<String> set2 = allColumnSets.get(r2).get(c2);

                        // if every value of c1 appears in c2, we have an IND
                        if (set2.containsAll(set1)) {
                            inclusionDependencies.add(new IND(relations.get(r1), c1, relations.get(r2), c2));
                        }
                    }
                }
            }
        }

    
        if (discoverNary)
            throw new RuntimeException("Sorry, n-ary IND discovery is not supported by this solution.");

        return inclusionDependencies;
    }

    private List<Set<String>> toColumnSets(String[][] columns) {
        List<Set<String>> result = new ArrayList<>();
        for (String[] col : columns) {
            Set<String> s = new HashSet<>();
            for (String v : col) s.add(v);
            result.add(s);
        }
        return result;
    }
}