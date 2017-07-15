package de.fred4jupiter.fredbet.service.excel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;

import de.fred4jupiter.fredbet.domain.Bet;
import de.fred4jupiter.fredbet.repository.BetRepository;
import de.fred4jupiter.fredbet.service.excel.ExcelExportService.EntryCallback;
import de.fred4jupiter.fredbet.util.DateUtils;
import de.fred4jupiter.fredbet.util.MessageSourceUtil;

@Service
public class ReportService {

	@Autowired
	private ExcelExportService excelExportService;

	@Autowired
	private BetRepository betRepository;

	@Autowired
	private MessageSourceUtil messageSourceUtil;

	public byte[] exportBetsToExcel(final Locale locale) {
		final List<Bet> bets = this.betRepository
				.findAll(new Sort(new Order(Direction.DESC, "points"), new Order(Direction.ASC, "userName")));

		return excelExportService.exportEntriesToExcel("Bets export", bets, new EntryCallback<Bet>() {

			@Override
			public String[] getHeaderRow() {
				String userName = messageSourceUtil.getMessageFor("excel.export.username", locale);
				String team1 = messageSourceUtil.getMessageFor("excel.export.team1", locale);
				String team2 = messageSourceUtil.getMessageFor("excel.export.team2", locale);
				String date = messageSourceUtil.getMessageFor("excel.export.date", locale);
				String points = messageSourceUtil.getMessageFor("excel.export.points", locale);
				return new String[] { userName, team1, team2, date, points };
			}

			@Override
			public String[] getRowValues(Bet bet) {
				String country1 = messageSourceUtil.getCountryName(bet.getMatch().getCountryOne(), locale);
				String country2 = messageSourceUtil.getCountryName(bet.getMatch().getCountryTwo(), locale);

				return new String[] { bet.getUserName(), country1, country2, format(bet.getMatch().getKickOffDate()),
						"" + bet.getPoints() };
			}

		});
	}

	public byte[] exportNumberOfPointsInBets() {
		// TODO: implement

		/*
		 * Something like that: select * from (select user_name, points,
		 * count(points) as anzahl from bet group by user_name,points) as
		 * result1 where result1.points = 3 order by result1.anzahl desc;
		 */
		return null;
	}

	private String format(Date kickOffDate) {
		LocalDateTime localDateTime = DateUtils.toLocalDateTime(kickOffDate);
		return localDateTime.format(DateTimeFormatter.ISO_DATE_TIME);
	}
}
