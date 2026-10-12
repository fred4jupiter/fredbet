package de.fred4jupiter.fredbet.web.team;

import de.fred4jupiter.fredbet.domain.entity.Team;
import de.fred4jupiter.fredbet.security.FredBetPermission;
import de.fred4jupiter.fredbet.team.InvalidCrestException;
import de.fred4jupiter.fredbet.team.TeamAlreadyExistsException;
import de.fred4jupiter.fredbet.team.TeamInUseException;
import de.fred4jupiter.fredbet.team.TeamService;
import de.fred4jupiter.fredbet.teambundle.TeamBundle;
import de.fred4jupiter.fredbet.util.MessageSourceUtil;
import de.fred4jupiter.fredbet.web.WebMessageUtil;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Locale;
import java.util.Objects;

@Controller
@RequestMapping("/teams")
@PreAuthorize("hasAuthority('" + FredBetPermission.PERM_ADMINISTRATION + "')")
public class TeamController {

    private static final Logger LOG = LoggerFactory.getLogger(TeamController.class);

    private static final String VIEW_LIST_TEAMS = "team/list";

    private static final String VIEW_EDIT_TEAM = "team/edit";

    private static final String REDIRECT_LIST_TEAMS = "redirect:/teams";

    private static final String CONTENT_TYPE_SVG = "image/svg+xml";

    private final TeamService teamService;

    private final WebMessageUtil webMessageUtil;

    private final MessageSourceUtil messageSourceUtil;

    public TeamController(TeamService teamService, WebMessageUtil webMessageUtil, MessageSourceUtil messageSourceUtil) {
        this.teamService = teamService;
        this.webMessageUtil = webMessageUtil;
        this.messageSourceUtil = messageSourceUtil;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("allTeams", teamService.findAllTeams());
        model.addAttribute("availableTeamBundles", teamService.getCreatableTeamBundles());
        return VIEW_LIST_TEAMS;
    }

    @PostMapping("/bundle")
    public String createTeamsOfBundle(@RequestParam(required = false) TeamBundle teamBundle, RedirectAttributes redirect) {
        if (teamBundle == null || !teamService.getCreatableTeamBundles().contains(teamBundle)) {
            webMessageUtil.addErrorMsg(redirect, "team.bundle.msg.noBundleSelected");
            return REDIRECT_LIST_TEAMS;
        }

        int createdTeams = teamService.createTeamsOfBundle(teamBundle);
        LOG.debug("created {} teams of team bundle {}", createdTeams, teamBundle);
        String teamBundleName = messageSourceUtil.getMessageFor("teamBundle." + teamBundle);
        webMessageUtil.addInfoMsg(redirect, "team.bundle.msg.created", createdTeams, teamBundleName);
        return REDIRECT_LIST_TEAMS;
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("teamCommand", new TeamCommand());
        return showEditView(model);
    }

    @GetMapping("/{teamId}")
    public String edit(@PathVariable Long teamId, Model model, RedirectAttributes redirect) {
        Team team = teamService.findTeamById(teamId);
        if (team == null) {
            webMessageUtil.addErrorMsg(redirect, "team.msg.notFound", teamId);
            return REDIRECT_LIST_TEAMS;
        }

        model.addAttribute("teamCommand", toTeamCommand(team));
        return showEditView(model);
    }

    @PostMapping
    public String save(@Valid TeamCommand teamCommand, BindingResult result, Model model, RedirectAttributes redirect) {
        validateCountryOrName(teamCommand, result);
        byte[] crestSvg = readCrestFile(teamCommand.getCrestFile(), result);
        if (result.hasErrors()) {
            return showEditView(model);
        }

        try {
            Team savedTeam = teamCommand.isNew()
                ? teamService.createTeam(teamCommand.getCountry(), teamCommand.getName(), crestSvg)
                : teamService.updateTeam(teamCommand.getTeamId(), teamCommand.getCountry(), teamCommand.getName(), crestSvg);

            String msgKey = teamCommand.isNew() ? "team.msg.created" : "team.msg.updated";
            webMessageUtil.addInfoMsg(redirect, msgKey, savedTeam.getNameTranslated(messageSourceUtil));
            return REDIRECT_LIST_TEAMS + "#team-" + savedTeam.getId();
        } catch (TeamAlreadyExistsException e) {
            String field = teamCommand.isUseCountry() ? "country" : "name";
            result.rejectValue(field, "team.form.msg.alreadyExists");
        } catch (InvalidCrestException e) {
            result.rejectValue("crestFile", "team.form.msg.invalidSvg");
        }

        return showEditView(model);
    }

    @PostMapping("/delete/{teamId}")
    public String delete(@PathVariable Long teamId, RedirectAttributes redirect) {
        Team team = teamService.findTeamById(teamId);
        if (team == null) {
            webMessageUtil.addErrorMsg(redirect, "team.msg.notFound", teamId);
            return REDIRECT_LIST_TEAMS;
        }

        String teamName = team.getNameTranslated(messageSourceUtil);
        try {
            teamService.deleteTeam(teamId);
            LOG.debug("deleted team with id={}", teamId);
            webMessageUtil.addInfoMsg(redirect, "team.msg.deleted", teamName);
        } catch (TeamInUseException e) {
            webMessageUtil.addErrorMsg(redirect, "team.msg.inUse", teamName);
        }

        return REDIRECT_LIST_TEAMS;
    }

    private String showEditView(Model model) {
        model.addAttribute("availableCountries", teamService.getAllCountriesSorted());
        return VIEW_EDIT_TEAM;
    }

    private void validateCountryOrName(TeamCommand teamCommand, BindingResult result) {
        if (teamCommand.isUseCountry()) {
            teamCommand.setName(null);
            if (teamCommand.getCountry() == null) {
                result.rejectValue("country", "team.form.msg.countryRequired");
            }
        } else {
            teamCommand.setCountry(null);
            if (StringUtils.isBlank(teamCommand.getName())) {
                result.rejectValue("name", "team.form.msg.nameRequired");
            }
        }
    }

    private byte[] readCrestFile(MultipartFile crestFile, BindingResult result) {
        if (crestFile == null || crestFile.isEmpty()) {
            return null;
        }

        String filename = Objects.toString(crestFile.getOriginalFilename(), "").toLowerCase(Locale.ROOT);
        if (!filename.endsWith(".svg") || !CONTENT_TYPE_SVG.equals(crestFile.getContentType())) {
            result.rejectValue("crestFile", "team.form.msg.invalidSvg");
            return null;
        }

        try {
            return crestFile.getBytes();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            result.rejectValue("crestFile", "team.form.msg.invalidSvg");
            return null;
        }
    }

    private TeamCommand toTeamCommand(Team team) {
        TeamCommand command = new TeamCommand();
        command.setTeamId(team.getId());
        command.setVersion(team.getVersion());
        command.setUseCountry(team.getCountry() != null);
        command.setCountry(team.getCountry());
        command.setName(team.getName());
        command.setDeletable(teamService.isDeletable(team));
        return command;
    }
}
