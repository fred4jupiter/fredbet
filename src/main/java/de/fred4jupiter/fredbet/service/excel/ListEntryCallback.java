package de.fred4jupiter.fredbet.service.excel;

import java.util.ArrayList;
import java.util.List;

abstract class ListEntryCallback<T> implements EntryCallback<T> {

    @Override
    public final String[] getHeaderRow() {
        final List<String> header = new ArrayList<>();
        addHeaderRow(header);
        String[] headerArr = new String[header.size()];
        return header.toArray(headerArr);
    }

    @Override
    public final String[] getRowValues(T entry) {
        final List<String> rowValues = new ArrayList<>();
        addValueRow(entry, rowValues);
        String[] rowArr = new String[rowValues.size()];
        return rowValues.toArray(rowArr);
    }

    public abstract void addHeaderRow(List<String> header);

    public abstract void addValueRow(T entry, List<String> rowValues);
}