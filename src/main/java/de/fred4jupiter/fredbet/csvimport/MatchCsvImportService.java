package de.fred4jupiter.fredbet.csvimport;

import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.SvgImage;
import de.fred4jupiter.fredbet.domain.entity.Match;
import de.fred4jupiter.fredbet.domain.entity.Team;
import de.fred4jupiter.fredbet.match.MatchService;
import de.fred4jupiter.fredbet.team.InvalidCrestException;
import de.fred4jupiter.fredbet.team.TeamService;
import de.fred4jupiter.fredbet.teambundle.TeamBundleEntry;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Imports the matches of a tournament from a CSV file. Each column of the CSV file is mapped to a
 * {@link CsvImportField}. Team names that can be resolved to a team bundle entry (country or club team) are saved as
 * such, all other teams are saved with their name. The crest of a new team can be given as SVG content or as URL of an
 * image to download. The import is all or nothing: if a single line is invalid, no
 * match is imported.
 */
@Service
@Transactional
public class MatchCsvImportService {

    private static final Logger LOG = LoggerFactory.getLogger(MatchCsvImportService.class);

    private static final int MAX_SVG_SIZE = 2 * 1024 * 1024;

    private final TeamNameResolver teamNameResolver;

    private final GroupResolver groupResolver;

    private final KickOffDateParser kickOffDateParser;

    private final CrestImageDownloader crestImageDownloader;

    private final TeamService teamService;

    private final MatchService matchService;

    MatchCsvImportService(TeamNameResolver teamNameResolver, GroupResolver groupResolver, KickOffDateParser kickOffDateParser,
                                 CrestImageDownloader crestImageDownloader, TeamService teamService, MatchService matchService) {
        this.teamNameResolver = teamNameResolver;
        this.groupResolver = groupResolver;
        this.kickOffDateParser = kickOffDateParser;
        this.crestImageDownloader = crestImageDownloader;
        this.teamService = teamService;
        this.matchService = matchService;
    }

    /**
     * Reads the uploaded CSV file.
     *
     * @throws CsvImportException if the file cannot be read
     */
    @Transactional(readOnly = true)
    public CsvTable parseCsv(byte[] content) {
        return CsvParser.parse(content);
    }

    /**
     * Suggests a field for each column based on the column header, e.g. 'Home Team' is mapped to {@link CsvImportField#TEAM_ONE}.
     *
     * @return the suggested field for each column (in the order of the columns), null for a column that is not imported
     */
    @Transactional(readOnly = true)
    public List<CsvImportField> suggestMapping(List<String> headers) {
        List<CsvImportField> mapping = new ArrayList<>();
        for (String header : headers) {
            String normalized = TranslationLookup.normalize(header);
            CsvImportField field = null;
            for (CsvImportField candidate : CsvImportField.values()) {
                if (!mapping.contains(candidate) && candidate.matchesHeader(normalized)) {
                    field = candidate;
                    break;
                }
            }
            mapping.add(field);
        }
        return mapping;
    }

    /**
     * Imports the matches of the CSV file.
     *
     * @param mapping    the field of each column (in the order of the columns), null for a column that is not imported
     * @param dateFormat optional pattern of the kick-off date, e.g. dd.MM.yyyy HH:mm. If empty, common formats are detected.
     */
    public MatchCsvImportResult importMatches(CsvTable csvTable, List<CsvImportField> mapping, String dateFormat) {
        List<CsvImportError> mappingErrors = validateMapping(mapping, dateFormat);
        if (!mappingErrors.isEmpty()) {
            return MatchCsvImportResult.failed(mappingErrors);
        }

        Map<CsvImportField, Integer> columnIndexes = toColumnIndexes(mapping);
        List<CsvImportError> errors = new ArrayList<>();
        List<ImportRow> importRows = new ArrayList<>();
        for (int i = 0; i < csvTable.rowCount(); i++) {
            ImportRow importRow = toImportRow(CsvTable.lineNumberOf(i), csvTable.rows().get(i), columnIndexes, dateFormat, errors);
            if (importRow != null) {
                importRows.add(importRow);
            }
        }

        if (!errors.isEmpty()) {
            return MatchCsvImportResult.failed(errors);
        }

        return saveMatches(importRows);
    }

    private List<CsvImportError> validateMapping(List<CsvImportField> mapping, String dateFormat) {
        List<CsvImportError> errors = new ArrayList<>();
        List<CsvImportField> safeMapping = mapping != null ? mapping : List.of();

        for (CsvImportField field : CsvImportField.values()) {
            int count = Collections.frequency(safeMapping, field);
            if (count == 0 && field.isRequired()) {
                errors.add(CsvImportError.ofMapping("csvimport.error.requiredFieldNotMapped", field));
            } else if (count > 1) {
                errors.add(CsvImportError.ofMapping("csvimport.error.fieldMappedTwice", field));
            }
        }

        if (!kickOffDateParser.isValidDateFormat(dateFormat)) {
            errors.add(new CsvImportError(null, "csvimport.error.invalidDateFormat", CsvImportField.KICK_OFF_DATE, dateFormat));
        }
        return errors;
    }

    private Map<CsvImportField, Integer> toColumnIndexes(List<CsvImportField> mapping) {
        Map<CsvImportField, Integer> columnIndexes = new EnumMap<>(CsvImportField.class);
        for (int i = 0; i < mapping.size(); i++) {
            if (mapping.get(i) != null) {
                columnIndexes.put(mapping.get(i), i);
            }
        }
        return columnIndexes;
    }

    private ImportRow toImportRow(int lineNumber, List<String> row, Map<CsvImportField, Integer> columnIndexes, String dateFormat,
                                  List<CsvImportError> errors) {
        int errorCountBefore = errors.size();

        String teamOne = valueOf(row, columnIndexes, CsvImportField.TEAM_ONE);
        String teamTwo = valueOf(row, columnIndexes, CsvImportField.TEAM_TWO);
        String groupValue = valueOf(row, columnIndexes, CsvImportField.GROUP);
        String kickOffDateValue = valueOf(row, columnIndexes, CsvImportField.KICK_OFF_DATE);
        String venue = valueOf(row, columnIndexes, CsvImportField.VENUE);
        String teamOneImage = valueOf(row, columnIndexes, CsvImportField.TEAM_ONE_IMAGE);
        String teamTwoImage = valueOf(row, columnIndexes, CsvImportField.TEAM_TWO_IMAGE);
        String teamOneSvg = valueOf(row, columnIndexes, CsvImportField.TEAM_ONE_SVG);
        String teamTwoSvg = valueOf(row, columnIndexes, CsvImportField.TEAM_TWO_SVG);

        requireValue(lineNumber, CsvImportField.TEAM_ONE, teamOne, errors);
        requireValue(lineNumber, CsvImportField.TEAM_TWO, teamTwo, errors);

        LocalDateTime kickOffDate = null;
        if (requireValue(lineNumber, CsvImportField.KICK_OFF_DATE, kickOffDateValue, errors)) {
            kickOffDate = kickOffDateParser.parse(kickOffDateValue, dateFormat);
            if (kickOffDate == null) {
                errors.add(CsvImportError.ofLine(lineNumber, "csvimport.error.invalidKickOffDate", CsvImportField.KICK_OFF_DATE, kickOffDateValue));
            }
        }

        Group group = groupResolver.resolve(groupValue);
        if (StringUtils.isNotBlank(groupValue) && group == null) {
            errors.add(CsvImportError.ofLine(lineNumber, "csvimport.error.unknownGroup", CsvImportField.GROUP, groupValue));
        }

        validateImageUrl(lineNumber, CsvImportField.TEAM_ONE_IMAGE, teamOneImage, errors);
        validateImageUrl(lineNumber, CsvImportField.TEAM_TWO_IMAGE, teamTwoImage, errors);
        SvgImage teamOneCrest = toCrest(lineNumber, CsvImportField.TEAM_ONE_SVG, teamOneSvg, errors);
        SvgImage teamTwoCrest = toCrest(lineNumber, CsvImportField.TEAM_TWO_SVG, teamTwoSvg, errors);

        if (errors.size() > errorCountBefore) {
            return null;
        }

        return new ImportRow(lineNumber, new ImportTeam(teamOne, teamNameResolver.resolve(teamOne), teamOneImage, teamOneCrest),
            new ImportTeam(teamTwo, teamNameResolver.resolve(teamTwo), teamTwoImage, teamTwoCrest), group, kickOffDate, StringUtils.trimToNull(venue));
    }

    private String valueOf(List<String> row, Map<CsvImportField, Integer> columnIndexes, CsvImportField field) {
        Integer index = columnIndexes.get(field);
        if (index == null || index >= row.size()) {
            return null;
        }
        return StringUtils.trimToNull(row.get(index));
    }

    private boolean requireValue(int lineNumber, CsvImportField field, String value, List<CsvImportError> errors) {
        if (StringUtils.isBlank(value)) {
            errors.add(CsvImportError.ofLine(lineNumber, "csvimport.error.valueMissing", field, null));
            return false;
        }
        return true;
    }

    private void validateImageUrl(int lineNumber, CsvImportField field, String url, List<CsvImportError> errors) {
        if (StringUtils.isNotBlank(url) && !CrestImageDownloader.isValidImageUrl(url)) {
            errors.add(CsvImportError.ofLine(lineNumber, "csvimport.error.invalidImageUrl", field, url));
        }
    }

    /**
     * Converts the SVG content of a column to a crest.
     *
     * @return the crest or null, if the column is empty or contains no valid SVG image (an error is added then)
     */
    private SvgImage toCrest(int lineNumber, CsvImportField field, String svgContent, List<CsvImportError> errors) {
        if (StringUtils.isBlank(svgContent)) {
            return null;
        }

        byte[] svgBinary = svgContent.getBytes(StandardCharsets.UTF_8);
        if (svgBinary.length > MAX_SVG_SIZE) {
            errors.add(CsvImportError.ofLine(lineNumber, "csvimport.error.svgTooLarge", field, null));
            return null;
        }
        try {
            return teamService.convertToCrest(svgBinary);
        } catch (InvalidCrestException e) {
            errors.add(CsvImportError.ofLine(lineNumber, "csvimport.error.invalidSvg", field, null));
            return null;
        }
    }

    private MatchCsvImportResult saveMatches(List<ImportRow> importRows) {
        TeamCreator teamCreator = new TeamCreator();
        for (ImportRow importRow : importRows) {
            Match match = new Match();
            match.setTeamOne(teamCreator.findOrCreateTeam(importRow.lineNumber(), importRow.teamOne(), CsvImportField.TEAM_ONE_IMAGE));
            match.setTeamTwo(teamCreator.findOrCreateTeam(importRow.lineNumber(), importRow.teamTwo(), CsvImportField.TEAM_TWO_IMAGE));
            match.setGroup(importRow.group());
            match.setKickOffDate(importRow.kickOffDate());
            match.setVenue(importRow.venue());
            matchService.save(match);
        }

        LOG.info("Imported {} matches from CSV file. Created {} new teams.", importRows.size(), teamCreator.createdTeams);
        return new MatchCsvImportResult(importRows.size(), teamCreator.createdTeams, List.of(), List.copyOf(teamCreator.warnings));
    }

    /**
     * Creates the teams of the import. The crest of a team is only set for a newly created team, so that crests of
     * existing teams are not overwritten. A crest given as SVG content takes precedence over a crest URL.
     */
    private final class TeamCreator {

        private final Map<String, Optional<SvgImage>> downloadedCrests = new HashMap<>();

        private final List<CsvImportError> warnings = new ArrayList<>();

        private int createdTeams;

        Team findOrCreateTeam(int lineNumber, ImportTeam importTeam, CsvImportField imageField) {
            return teamService.findOrCreateTeam(importTeam.teamBundleEntry(), importTeam.name(), newTeam -> {
                createdTeams++;
                if (importTeam.crest() != null) {
                    newTeam.setSvgContent(importTeam.crest());
                    return;
                }
                if (StringUtils.isBlank(importTeam.imageUrl())) {
                    return;
                }

                Optional<SvgImage> crest = downloadedCrests.computeIfAbsent(importTeam.imageUrl(), crestImageDownloader::download);
                crest.ifPresentOrElse(newTeam::setSvgContent,
                    () -> warnings.add(CsvImportError.ofLine(lineNumber, "csvimport.warning.crestDownloadFailed", imageField, importTeam.imageUrl())));
            });
        }
    }

    private record ImportTeam(String name, TeamBundleEntry teamBundleEntry, String imageUrl, SvgImage crest) {
    }

    private record ImportRow(int lineNumber, ImportTeam teamOne, ImportTeam teamTwo, Group group, LocalDateTime kickOffDate, String venue) {
    }
}
