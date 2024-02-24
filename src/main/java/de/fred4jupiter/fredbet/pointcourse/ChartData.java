package de.fred4jupiter.fredbet.pointcourse;

import java.util.ArrayList;
import java.util.List;

public class ChartData {

    private final List<String> labels = new ArrayList<>();

    private final List<DataSet> datasets = new ArrayList<>();

    public ChartData(List<String> labels) {
        this.labels.addAll(labels);
    }

    public void addDataSet(String name, List<Integer> values) {
        this.datasets.add(new DataSet(name, values));
    }

    public List<String> getLabels() {
        return labels;
    }

    public List<DataSet> getDatasets() {
        return datasets;
    }

    public boolean isEmpty() {
        return this.datasets.isEmpty();
    }
}
