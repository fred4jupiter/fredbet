package de.fred4jupiter.fredbet.repository;

import de.fred4jupiter.fredbet.domain.SvgImage;
import de.fred4jupiter.fredbet.domain.entity.ImageBinary;
import de.fred4jupiter.fredbet.domain.entity.Team;
import de.fred4jupiter.fredbet.image.ImageBinaryRepository;
import de.fred4jupiter.fredbet.info.InfoService;
import de.fred4jupiter.fredbet.match.TeamRepository;
import de.fred4jupiter.fredbet.web.info.InfoType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Locale;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Stores the binary and text content of images, team crests and infos and reads it back, through JPA and with plain SQL.
 * The plain SQL makes sure that the content is really stored in the column and not somewhere else (e.g. as large object).
 */
abstract class AbstractDatabaseContentIT {

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    private ImageBinaryRepository imageBinaryRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private InfoService infoService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Test
    public void storeAndLoadImageWithThumbnail() {
        // larger than a page of the database, so it is really stored as binary data
        byte[] image = randomBytes(300_000);
        byte[] thumbnail = randomBytes(5_000);

        String imageKey = transactionTemplate.execute(status -> imageBinaryRepository.saveImage(image, thumbnail));

        // read in a new transaction, as in a following request
        transactionTemplate.executeWithoutResult(status -> {
            ImageBinary imageBinary = imageBinaryRepository.findById(imageKey).orElseThrow();
            assertThat(imageBinary.getImageBinary()).isEqualTo(image);
            assertThat(imageBinary.getThumbImageBinary()).isEqualTo(thumbnail);
        });
        assertThat(jdbcTemplate.queryForObject("select image_bytes from image_binary where image_key = ?", byte[].class, imageKey)).isEqualTo(image);
        assertThat(jdbcTemplate.queryForObject("select thumb_image_bytes from image_binary where image_key = ?", byte[].class, imageKey)).isEqualTo(thumbnail);

        transactionTemplate.executeWithoutResult(status -> imageBinaryRepository.deleteImage(imageKey));
        assertThat(imageBinaryRepository.findById(imageKey)).isEmpty();
    }

    @Test
    public void storeAndLoadTeamCrestAsSvg() {
        String svg = "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 10 10\"><title>Crest äöü</title>"
            + "<rect width=\"10\" height=\"10\"/>".repeat(5_000) + "</svg>";

        Long teamId = transactionTemplate.execute(status -> {
            Team team = new Team("FC Database");
            team.setSvgContent(new SvgImage(svg));
            return teamRepository.save(team).getId();
        });

        String loadedSvg = transactionTemplate.execute(status -> teamRepository.findById(teamId).orElseThrow().getSvgContent());
        assertThat(loadedSvg).isEqualTo(svg);
        assertThat(jdbcTemplate.queryForObject("select svg_content from team where id = ?", String.class, teamId)).isEqualTo(svg);

        transactionTemplate.executeWithoutResult(status -> teamRepository.deleteById(teamId));
    }

    @Test
    public void storeAndLoadLongInfoText() {
        String content = "<p>Regeln für das Tippspiel</p>".repeat(2_000);

        transactionTemplate.executeWithoutResult(status -> infoService.saveInfoContent(InfoType.PRICES, content, Locale.ITALIAN));

        String loadedContent = transactionTemplate.execute(status -> infoService.findBy(InfoType.PRICES, Locale.ITALIAN).getContent());
        assertThat(loadedContent).isEqualTo(content);
        assertThat(jdbcTemplate.queryForObject("select content from info_content where info_name = ? and locale_string = ?", String.class,
            InfoType.PRICES.name().toLowerCase(), Locale.ITALIAN.getLanguage())).isEqualTo(content);

        jdbcTemplate.update("delete from info_content where locale_string = ?", Locale.ITALIAN.getLanguage());
    }

    private byte[] randomBytes(int size) {
        byte[] bytes = new byte[size];
        new Random(42).nextBytes(bytes);
        return bytes;
    }
}
