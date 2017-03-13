package de.fred4jupiter.fredbet.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.apache.commons.io.FilenameUtils;
import org.junit.Test;

import de.fred4jupiter.fredbet.domain.Country;

public class DemoTest {

	@Test
	public void printFiles() throws IOException {
		File file = new File("d://development_synced/2_Github/fredbet/src/main/resources/static/images/flags/42_28");

		Files.walkFileTree(Paths.get(file.getAbsolutePath()), new SimpleFileVisitor<Path>() {

			@Override
			public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
				final File file = path.toFile();
				if (!attrs.isDirectory()) {
					String isoCode = FilenameUtils.removeExtension(file.getName());
					Country country = Country.fromIsoCode(isoCode);
					if (country != null) {
						System.out.println("country." + isoCode + "=" + toCountryNameEnglish(country.name()));
					} else {
						//System.err.println("Could not found country for isoCode=" + isoCode);
					}
				}
				return FileVisitResult.CONTINUE;
			}

		});

	}

	private String toCountryNameEnglish(String name) {
		String lowereCaseAll = name.toLowerCase();
		String firstCharUpper = lowereCaseAll.substring(0, 1).toUpperCase();
		String otherLower = lowereCaseAll.substring(1).toLowerCase();

		return firstCharUpper + otherLower;
	}
}
