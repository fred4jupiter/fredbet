package de.fred4jupiter.fredbet.excel;

import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Generic class creating MS Excel files.
 *
 * @author michael
 */
@Service
class ExcelExportService {

    private static final Logger LOG = LoggerFactory.getLogger(ExcelExportService.class);

    public <T> byte[] exportEntriesToExcel(String sheetName, List<T> entries, EntryCallback<T> callback) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream(); XSSFWorkbook wb = new XSSFWorkbook()) {
            XSSFSheet sheet = wb.createSheet(sheetName);

            XSSFRow headerRow = sheet.createRow(0);

            createCells(wb, headerRow, true, callback.getHeaderRow());

            for (int i = 0; i < entries.size(); i++) {
                T bet = entries.get(i);
                XSSFRow row = sheet.createRow(i + 1);
                callback.getRowValues(bet);
                createCells(wb, row, callback.getRowValues(bet));
            }
            wb.write(out);
            out.flush();
            return out.toByteArray();
        } catch (IOException e) {
            LOG.error("Error creating excel file. cause: {}", e.getMessage(), e);
            return null;
        }
    }

    private void createCells(XSSFWorkbook wb, XSSFRow row, String... cellValues) {
        createCells(wb, row, false, cellValues);
    }

    private void createCells(XSSFWorkbook wb, XSSFRow row, boolean bold, String... cellValues) {
        final XSSFCellStyle style = wb.createCellStyle();
        XSSFFont font = wb.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setBold(true);
        style.setFont(font);
        for (int i = 0; i < cellValues.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(cellValues[i]);
            if (bold) {
                cell.setCellStyle(style);
            }
        }
    }
}
