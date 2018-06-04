package de.fred4jupiter.fredbet.service.excel;

import java.util.List;
import java.util.Locale;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;

import de.fred4jupiter.fredbet.domain.Bet;
import de.fred4jupiter.fredbet.repository.BetRepository;
import de.fred4jupiter.fredbet.repository.PointCountResult;
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
		Sort sort = Sort.by(new Order(Direction.DESC, "points"), new Order(Direction.ASC, "userName"));
		final List<Bet> bets = this.betRepository.findAll(sort);

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

				String formatedDate = DateUtils.formatByLocale(bet.getMatch().getKickOffDate(), locale);
				return new String[] { bet.getUserName(), country1, country2, formatedDate, "" + bet.getPoints() };
			}

		});
	}

	public MultiValuedMap<Integer, PointCountResult> reportPointsFrequency() {
		MultiValuedMap<Integer, PointCountResult> map = new ArrayListValuedHashMap<>();

		final List<PointCountResult> resultList = this.betRepository.countNumberOfPointsByUser();
		for (PointCountResult pointCountResult : resultList) {
			map.put(pointCountResult.getPoints(), pointCountResult);
		}

		return map;
	}

	public byte[] exportNumberOfPointsInBets(final Locale locale) {
		final List<PointCountResult> resultList = this.betRepository.countNumberOfPointsByUser();

		return excelExportService.exportEntriesToExcel("Bets point count export", resultList, new EntryCallback<>() {

			@Override
			public String[] getHeaderRow() {
				String userName = messageSourceUtil.getMessageFor("excel.export.username", locale);
				String points = messageSourceUtil.getMessageFor("excel.export.points", locale);
				String pointsCount = messageSourceUtil.getMessageFor("excel.export.pointsCount", locale);
				return new String[] { userName, points, pointsCount };
			}

			@Override
			public String[] getRowValues(PointCountResult pointCountResult) {
				return new String[] { pointCountResult.getUsername(), "" + pointCountResult.getPoints(),
						"" + pointCountResult.getNumberOfPointsCount() };
			}

		});
	}
}
