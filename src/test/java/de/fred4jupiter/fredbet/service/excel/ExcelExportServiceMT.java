package de.fred4jupiter.fredbet.service.excel;

import java.io.File;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import de.fred4jupiter.fredbet.Application;
import de.fred4jupiter.fredbet.props.FredBetProfile;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.NONE)
@ActiveProfiles(value = { FredBetProfile.LOCALDB, FredBetProfile.INTEGRATION_TEST })
public class ExcelExportServiceMT {

	@Autowired
	private ExcelExportService excelExportService;
	
	@Test
	public void exportResultsToExcel() {
		excelExportService.exportBetsToExcel(new File("d://Temp1/export.xlsx"));
	}

}
