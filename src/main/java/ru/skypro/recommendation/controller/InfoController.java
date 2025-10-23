package ru.skypro.recommendation.controller;

import org.springframework.boot.info.BuildProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
@RestController
@RequestMapping("/management/info")
public class InfoController {

    private final BuildProperties buildProperties;

    public InfoController(BuildProperties buildProperties) {
        this.buildProperties = buildProperties;
    }

    @GetMapping
    public Map<String, String> getInfo() {
        Map<String, String> info = new HashMap<>();
        info.put("name", buildProperties.getName());
        info.put("version", buildProperties.getVersion());
        return info;
    }
}