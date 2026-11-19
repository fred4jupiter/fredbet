package de.fred4jupiter.fredbet.web.info;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Shows the server address the browser (or the installed app) is connected to. The values are
 * determined in the browser (see connection_info.js), because only the browser knows the address
 * the user really uses, independent of reverse proxies in front of the application.
 */
@Controller
@RequestMapping("/info/connection")
public class ConnectionInfoController {

    private static final String PAGE_CONNECTION_INFO = "info/connection";

    @GetMapping
    public String show() {
        return PAGE_CONNECTION_INFO;
    }
}
