package de.di.duplicate_detection;

import de.di.duplicate_detection.structures.AttrSimWeight;
import de.di.similarity_measures.SimilarityMeasure;

import java.util.List;
import java.util.stream.Collectors;

public class RecordComparator {

    private List<AttrSimWeight> attrSimWeights;
    private double threshold;

    public RecordComparator(List<AttrSimWeight> attrSimWeights, double threshold) {
        this.attrSimWeights = this.normalize(attrSimWeights);
        this.threshold = threshold;
    }

    private List<AttrSimWeight> normalize(List<AttrSimWeight> attrSimWeights) {
        double correction = 1 / attrSimWeights.stream()
                .map(AttrSimWeight::getWeight)
                .mapToDouble(Double::doubleValue)
                .sum();
        return attrSimWeights.stream()
                .map(a -> new AttrSimWeight(a.getAttribute(), a.getSimilarityMeasure(), a.getWeight() * correction))
                .collect(Collectors.toList());
    }

    public double compare(String[] tuple1, String[] tuple2) {
        double recordSimilarity = 0;


        // for each (attribute, measure, weight) triple, compute the attribute similarity
        // and add it to the total, weighted by its (already normalized) weight
        for (AttrSimWeight asw : this.attrSimWeights) {
            int attr = asw.getAttribute();
            SimilarityMeasure measure = asw.getSimilarityMeasure();

            double sim = measure.calculate(tuple1[attr], tuple2[attr]);
            recordSimilarity += sim * asw.getWeight();
        }


        return recordSimilarity;
    }

    public boolean isDuplicate(double similarity) {
        return similarity > this.threshold;
    }
}