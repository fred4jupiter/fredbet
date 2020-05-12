package de.fred4jupiter.fredbet.web.info.pointcourse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BarChartData {

    private final List<String> labels = new ArrayList<>();

    private final List<DataSet> datasets = new ArrayList<>();

    public BarChartData(String... labels) {
        this.labels.addAll(Arrays.asList(labels));
    }

    public BarChartData(List<String> labels) {
        this.labels.addAll(labels);
    }

    public void addDataSet(DataSet dataSet) {
        this.datasets.add(dataSet);
    }

    public void addDataSet(String name, Integer... values) {
        DataSet dataSet = new DataSet(name);
        for (Integer value : values) {
            dataSet.addData(value);
        }
        this.datasets.add(dataSet);
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
