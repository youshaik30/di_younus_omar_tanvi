package de.di.schema_matching;

import de.di.schema_matching.structures.CorrespondenceMatrix;
import de.di.schema_matching.structures.SimilarityMatrix;

public class SecondLineSchemaMatcher {

    public CorrespondenceMatrix match(SimilarityMatrix similarityMatrix) {
        double[][] simMatrix = similarityMatrix.getMatrix();
        int numSources = simMatrix.length;
        int numTargets = simMatrix[0].length;
        int[][] corrMatrix = new int[numSources][numTargets];

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //                                      DATA INTEGRATION ASSIGNMENT                                           //

        // global greedy: always pick the highest similarity pair not yet matched
        boolean[] usedSource = new boolean[numSources];
        boolean[] usedTarget = new boolean[numTargets];

        int total = Math.min(numSources, numTargets);

        for (int m = 0; m < total; m++) {
            double bestSim = -1;
            int bestI = -1;
            int bestJ = -1;

            for (int i = 0; i < numSources; i++) {
                if (usedSource[i]) continue;
                for (int j = 0; j < numTargets; j++) {
                    if (usedTarget[j]) continue;
                    if (simMatrix[i][j] > bestSim) {
                        bestSim = simMatrix[i][j];
                        bestI = i;
                        bestJ = j;
                    }
                }
            }

            if (bestI == -1) break;

            corrMatrix[bestI][bestJ] = 1;
            usedSource[bestI] = true;
            usedTarget[bestJ] = true;
        }

        //                                                                                                            //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        return new CorrespondenceMatrix(corrMatrix, similarityMatrix.getSourceRelation(), similarityMatrix.getTargetRelation());
    }
}