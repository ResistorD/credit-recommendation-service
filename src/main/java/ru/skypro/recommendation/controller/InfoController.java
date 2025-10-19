package ru.skypro.recommendation.controller;

import org.springframework.boot.info.BuildProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/management")
public class InfoController {
    private final BuildProperties build;
    public InfoController(BuildProperties build) { this.build = build; }

    @GetMapping("/info")
    public Map<String, String> info() {
        return Map.of("name", build.getName(), "version", build.getVersion());
    }
}
