package de.fred4jupiter.fredbet.imexport;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.List;

public record UserContainer(List<UserToExport> users) {
}
