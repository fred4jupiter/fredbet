package de.fred4jupiter.fredbet.web.info.pointcourse;

import java.util.ArrayList;
import java.util.List;

public class ChartData {

    private final List<String> labels = new ArrayList<>();

    private final List<DataSet> datasets = new ArrayList<>();

    public ChartData(List<String> labels) {
        this.labels.addAll(labels);
    }

    public void addDataSet(String name, List<Integer> values) {
        DataSet dataSet = new DataSet(name);
        for (Integer value : values) {
            dataSet.addData(value);
        }
        this.datasets.add(dataSet);
    }

    public List<String> getLabels() {
        return labels;
    }

    public List<DataSet> getDatasets() {
        return datasets;
    }
}
