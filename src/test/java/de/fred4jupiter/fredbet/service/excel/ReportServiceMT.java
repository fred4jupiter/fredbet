package de.fred4jupiter.fredbet.service.excel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
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
public class ReportServiceMT {

	@Autowired
	private ReportService reportService;

	@Test
	public void exportResultsToExcel() throws FileNotFoundException, IOException {
		File file = new File("d://Temp1/export.xlsx");

		byte[] export = reportService.exportBetsToExcel();

		IOUtils.write(export, new FileOutputStream(file));
	}

}
