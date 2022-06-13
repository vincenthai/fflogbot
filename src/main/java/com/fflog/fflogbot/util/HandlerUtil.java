package com.fflog.fflogbot.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fflog.fflogbot.model.ReportIdentifier;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class HandlerUtil {

    public String getCharacterQuery() {
        try {
            return new String(
                    HandlerUtil.class.getClassLoader().getResourceAsStream( "characterData.graphql").readAllBytes());
        } catch (IOException e) {
            return "";
        }
    }

    public String getReportQuery() {
        try {
            return new String(
                    HandlerUtil.class.getClassLoader().getResourceAsStream( "fightData.graphql").readAllBytes());
        } catch (IOException e) {
            return "";
        }
    }

    public String getDebuffQuery() {
        try {
            return new String(
                    HandlerUtil.class.getClassLoader().getResourceAsStream( "debuffs.graphql").readAllBytes());
        } catch (IOException e) {
            return "";
        }
    }

    public String getAbout() {
        try {
            return new String(
                    HandlerUtil.class.getClassLoader().getResourceAsStream( "about.txt").readAllBytes());
        } catch (IOException e) {
            return "";
        }
    }

    public String getVarsForDebuff(ReportIdentifier reportIdentifier) {
        return getVarsAsJson(reportIdentifier);
    }

    public String getVarsForEncounter(String name, String server, int encounter) {
        Variables vars = new CharacterDataVariables(name, server, encounter);
        return getVarsAsJson(vars);
    }

    public String getVarsForReport(String code, int fightId) {
        Variables vars = new ReportDataVariables(code, new int[]{fightId});
        return getVarsAsJson(vars);
    }

    private String getVarsAsJson(Variables vars) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(vars);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

    public abstract static class Variables {}

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    private static class CharacterDataVariables extends Variables {
        String name;
        String server;
        int encounter;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    private static class ReportDataVariables extends Variables {
        String code;
        int[] fightId;
    }
}
