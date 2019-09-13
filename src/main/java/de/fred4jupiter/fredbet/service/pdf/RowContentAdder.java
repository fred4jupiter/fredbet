package de.fred4jupiter.fredbet.service.pdf;

import java.util.ArrayList;
import java.util.List;

public class RowContentAdder {

    private final List<String> rowContent = new ArrayList<>();

    public void addCellContent(String content) {
        this.rowContent.add(content);
    }

    public List<String> getRowContent() {
        return rowContent;
    }
}
