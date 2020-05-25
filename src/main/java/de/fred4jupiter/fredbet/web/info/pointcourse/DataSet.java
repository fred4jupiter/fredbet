package de.fred4jupiter.fredbet.web.info.pointcourse;

import java.util.ArrayList;
import java.util.List;

public class DataSet {

    private final String label;

    private final List<Integer> data = new ArrayList<>();

    public DataSet(String label) {
        this.label = label;
    }

    public void addData(Integer value) {
        this.data.add(value);
    }

    public String getLabel() {
        return label;
    }

    public List<Integer> getData() {
        return data;
    }
}
