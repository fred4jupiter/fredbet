package de.fred4jupiter.fredbet.service.excel;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import de.fred4jupiter.fredbet.domain.Bet;
import de.fred4jupiter.fredbet.repository.BetRepository;
import de.fred4jupiter.fredbet.util.DateUtils;

@Service
public class ExcelExportService {

	private static final Logger LOG = LoggerFactory.getLogger(ExcelExportService.class);

	@Autowired
	private BetRepository betRepository;

	public byte[] exportBetsToExcel(File file) {
		final List<Bet> bets = this.betRepository
				.findAll(new Sort(new Order(Direction.DESC, "points"), new Order(Direction.ASC, "userName")));

		if (CollectionUtils.isEmpty(bets)) {
			return null;
		}

		String sheetName = "Bets export";// name of sheet

		try (FileOutputStream fileOut = new FileOutputStream(file); XSSFWorkbook wb = new XSSFWorkbook();) {
			XSSFSheet sheet = wb.createSheet(sheetName);

			XSSFRow headerRow = sheet.createRow(0);

			createCells(wb, headerRow, true, "Benutzer", "Team 1", "Team 2", "Datum", "Punkte");

			for (int i = 1; i < bets.size(); i++) {
				Bet bet = bets.get(i);
				XSSFRow row = sheet.createRow(i);
				createCells(wb, row, bet.getUserName(), bet.getMatch().getCountryOne().name(), bet.getMatch().getCountryTwo().name(),
						format(bet.getMatch().getKickOffDate()), "" + bet.getPoints());

			}
			wb.write(fileOut);
			fileOut.flush();
		} catch (Exception e) {
			LOG.error("Error creating excel file. cause: {}", e.getMessage(), e);
		}

		return null;
	}

	private String format(Date kickOffDate) {
		LocalDateTime localDateTime = DateUtils.toLocalDateTime(kickOffDate);
		return localDateTime.format(DateTimeFormatter.ISO_DATE_TIME);
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
