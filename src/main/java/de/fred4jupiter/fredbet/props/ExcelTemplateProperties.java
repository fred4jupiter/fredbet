package de.fred4jupiter.fredbet.props;

import java.util.List;
import java.util.Optional;

public record ExcelTemplateProperties(List<String> templateFiles) {

    public Optional<String> getResourceLocationByFilename(String filename) {
        return templateFiles.stream()
            .filter(file -> file.contains(filename))
            .map(file -> "classpath:/excelimport/" + file)
            .findFirst();
    }
}
