package de.di.data_profiling;

import de.di.Relation;
import de.di.data_profiling.structures.AttributeList;
import de.di.data_profiling.structures.PositionListIndex;
import de.di.data_profiling.structures.UCC;

import java.util.ArrayList;
import java.util.List;

public class UCCProfiler {

    public List<UCC> profile(Relation relation) {
        int numAttributes = relation.getAttributes().length;
        List<UCC> uniques = new ArrayList<>();
        List<PositionListIndex> currentNonUniques = new ArrayList<>();

        // Calculate all unary UCCs and unary non-UCCs
        for (int attribute = 0; attribute < numAttributes; attribute++) {
            AttributeList attributes = new AttributeList(attribute);
            PositionListIndex pli = new PositionListIndex(attributes, relation.getColumns()[attribute]);
            if (pli.isUnique())
                uniques.add(new UCC(relation, attributes));
            else
                currentNonUniques.add(pli);
        }

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //                                      DATA INTEGRATION ASSIGNMENT                                           //

        // level-wise lattice traversal: combine PLIs level by level
        // at each level, combine pairs that share the same prefix (Apriori principle)
        while (!currentNonUniques.isEmpty()) {
            List<PositionListIndex> nextNonUniques = new ArrayList<>();

            for (int i = 0; i < currentNonUniques.size(); i++) {
                for (int j = i + 1; j < currentNonUniques.size(); j++) {
                    AttributeList attrs1 = currentNonUniques.get(i).getAttributes();
                    AttributeList attrs2 = currentNonUniques.get(j).getAttributes();

                    // only combine if they share the same prefix
                    if (!attrs1.samePrefixAs(attrs2)) continue;

                    // candidate is the union of both attribute lists
                    AttributeList candidate = attrs1.union(attrs2);

                    // prune: skip if candidate is a superset of a known UCC (not minimal)
                    boolean pruned = false;
                    for (UCC ucc : uniques) {
                        if (candidate.supersetOf(ucc.getAttributeList())) {
                            pruned = true;
                            break;
                        }
                    }
                    if (pruned) continue;

                    // intersect the two PLIs to check uniqueness
                    PositionListIndex intersected = currentNonUniques.get(i).intersect(currentNonUniques.get(j));

                    if (intersected.isUnique()) {
                        uniques.add(new UCC(relation, candidate));
                    } else {
                        nextNonUniques.add(intersected);
                    }
                }
            }

            currentNonUniques = nextNonUniques;
        }

        //                                                                                                            //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        return uniques;
    }
}