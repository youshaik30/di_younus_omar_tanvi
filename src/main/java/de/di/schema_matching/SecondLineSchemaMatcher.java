package de.di.schema_matching;

import de.di.schema_matching.structures.CorrespondenceMatrix;
import de.di.schema_matching.structures.SimilarityMatrix;

import java.util.Arrays;

public class SecondLineSchemaMatcher {

    public CorrespondenceMatrix match(SimilarityMatrix similarityMatrix) {
        double[][] simMatrix = similarityMatrix.getMatrix();

        int[][] corrMatrix = null;

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //                                      DATA INTEGRATION ASSIGNMENT                                           //

        int numSources = simMatrix.length;
        int numTargets = simMatrix[0].length;

        // Step 1: build source preferences
        // for each source, rank all targets by similarity (highest first)
        int[][] sourcePrefs = new int[numSources][numTargets];
        for (int i = 0; i < numSources; i++) {
            for (int j = 0; j < numTargets; j++) sourcePrefs[i][j] = j;
            // selection sort: find highest similarity target at each position
            for (int a = 0; a < numTargets - 1; a++) {
                for (int b = a + 1; b < numTargets; b++) {
                    if (simMatrix[i][sourcePrefs[i][b]] > simMatrix[i][sourcePrefs[i][a]]) {
                        int tmp = sourcePrefs[i][a];
                        sourcePrefs[i][a] = sourcePrefs[i][b];
                        sourcePrefs[i][b] = tmp;
                    }
                }
            }
        }

        // Step 2: build target ranks
        // for each target, store the rank of each source (lower rank = more preferred)
        int[][] targetRanks = new int[numTargets][numSources];
        for (int j = 0; j < numTargets; j++) {
            int[] order = new int[numSources];
            for (int i = 0; i < numSources; i++) order[i] = i;
            // selection sort: find highest similarity source at each position
            for (int a = 0; a < numSources - 1; a++) {
                for (int b = a + 1; b < numSources; b++) {
                    if (simMatrix[order[b]][j] > simMatrix[order[a]][j]) {
                        int tmp = order[a];
                        order[a] = order[b];
                        order[b] = tmp;
                    }
                }
            }
            // store the rank of each source for this target
            for (int rank = 0; rank < numSources; rank++) {
                targetRanks[j][order[rank]] = rank;
            }
        }

        // Step 3: Stable Marriage (Gale-Shapley) algorithm, source-proposing
        int[] sourceMatch  = new int[numSources];  // sourceMatch[i]  = target matched to source i, or -1
        int[] targetMatch  = new int[numTargets];  // targetMatch[j]  = source matched to target j, or -1
        int[] nextProposal = new int[numSources];  // next preference index each source will propose to
        Arrays.fill(sourceMatch, -1);
        Arrays.fill(targetMatch, -1);

        while (true) {
            boolean madeProposal = false;

            for (int i = 0; i < numSources; i++) {
                if (sourceMatch[i] != -1) continue;          // source already matched
                if (nextProposal[i] >= numTargets) continue; // source exhausted all options

                madeProposal = true;
                int target = sourcePrefs[i][nextProposal[i]++]; // propose to next preferred target

                if (targetMatch[target] == -1) {
                    // target is free, accept
                    sourceMatch[i] = target;
                    targetMatch[target] = i;
                } else {
                    // target is taken, check if it prefers the new source over its current match
                    int rival = targetMatch[target];
                    if (targetRanks[target][i] < targetRanks[target][rival]) {
                        // target prefers new source, swap
                        sourceMatch[i] = target;
                        targetMatch[target] = i;
                        sourceMatch[rival] = -1; // rival is now unmatched
                    }
                    // else: target keeps its current match, source i will try next preference
                }
            }

            if (!madeProposal) break; // all sources are matched or have no more options
        }

        corrMatrix = assignmentArray2correlationMatrix(sourceMatch, simMatrix);

        //                                                                                                            //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        return new CorrespondenceMatrix(corrMatrix, similarityMatrix.getSourceRelation(), similarityMatrix.getTargetRelation());
    }

    private int[][] assignmentArray2correlationMatrix(int[] sourceAssignments, double[][] simMatrix) {
        int[][] corrMatrix = new int[simMatrix.length][];
        for (int i = 0; i < simMatrix.length; i++) {
            corrMatrix[i] = new int[simMatrix[i].length];
            for (int j = 0; j < simMatrix[i].length; j++)
                corrMatrix[i][j] = 0;
        }
        for (int i = 0; i < sourceAssignments.length; i++)
            if (sourceAssignments[i] >= 0)
                corrMatrix[i][sourceAssignments[i]] = 1;
        return corrMatrix;
    }
}