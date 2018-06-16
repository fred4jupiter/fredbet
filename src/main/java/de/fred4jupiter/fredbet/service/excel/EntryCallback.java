package de.fred4jupiter.fredbet.service.excel;

interface EntryCallback<T> {

    String[] getHeaderRow();

    String[] getRowValues(T entry);
}
