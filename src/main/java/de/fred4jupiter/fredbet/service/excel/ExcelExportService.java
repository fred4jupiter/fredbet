package de.fred4jupiter.fredbet.service.excel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class ExcelExportService {

	private static final Logger LOG = LoggerFactory.getLogger(ExcelExportService.class);

	public <T> byte[] exportEntriesToExcel(String sheetName, List<T> entries, EntryCallback<T> callback) {
		if (CollectionUtils.isEmpty(entries)) {
			return null;
		}

		try (ByteArrayOutputStream out = new ByteArrayOutputStream(); XSSFWorkbook wb = new XSSFWorkbook();) {
			XSSFSheet sheet = wb.createSheet(sheetName);

			XSSFRow headerRow = sheet.createRow(0);

			createCells(wb, headerRow, true, callback.getHeaderRow());

			for (int i = 1; i < entries.size(); i++) {
				T bet = entries.get(i);
				XSSFRow row = sheet.createRow(i);
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

	public interface EntryCallback<T> {

		String[] getHeaderRow();

		String[] getRowValues(T entry);
	}
}
