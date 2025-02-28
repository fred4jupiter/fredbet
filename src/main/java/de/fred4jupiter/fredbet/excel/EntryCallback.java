package de.fred4jupiter.fredbet.excel;

interface EntryCallback<T> {

    String[] getHeaderRow();

    String[] getRowValues(T entry);
}
