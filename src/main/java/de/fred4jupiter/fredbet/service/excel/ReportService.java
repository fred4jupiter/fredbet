package de.fred4jupiter.fredbet.service.excel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;

import de.fred4jupiter.fredbet.domain.Bet;
import de.fred4jupiter.fredbet.repository.BetRepository;
import de.fred4jupiter.fredbet.service.excel.ExcelExportService.EntryCallback;
import de.fred4jupiter.fredbet.util.DateUtils;

@Service
public class ReportService {

	@Autowired
	private ExcelExportService excelExportService;

	@Autowired
	private BetRepository betRepository;

	public byte[] exportBetsToExcel() {
		final List<Bet> bets = this.betRepository
				.findAll(new Sort(new Order(Direction.DESC, "points"), new Order(Direction.ASC, "userName")));

		return excelExportService.exportEntriesToExcel("Bets export", bets, new EntryCallback<Bet>() {

			@Override
			public String[] getHeaderRow() {
				return new String[] { "Benutzer", "Team 1", "Team 2", "Datum", "Punkte" };
			}

			@Override
			public String[] getRowValues(Bet bet) {
				return new String[] { bet.getUserName(), bet.getMatch().getCountryOne().name(), bet.getMatch().getCountryTwo().name(),
						format(bet.getMatch().getKickOffDate()), "" + bet.getPoints() };
			}

		});
	}

	private String format(Date kickOffDate) {
		LocalDateTime localDateTime = DateUtils.toLocalDateTime(kickOffDate);
		return localDateTime.format(DateTimeFormatter.ISO_DATE_TIME);
	}
}
