package com.fflog.fflogbot.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fflog.fflogbot.client.ClientWrapper;
import com.fflog.fflogbot.model.Encounter;
import com.fflog.fflogbot.model.Rank;
import com.fflog.fflogbot.model.ReportIdentifier;
import com.fflog.fflogbot.model.tiers.Eden;
import com.fflog.fflogbot.model.tiers.Tier;
import com.fflog.fflogbot.util.HandlerUtil;
import graphql.kickstart.spring.webclient.boot.GraphQLResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ResponseHandler {

    @Autowired
    ClientWrapper client;
    @Autowired
    private HandlerUtil util;

    private String reportQuery;
    private String debuffQuery;
    private final StopWatch watcher = new StopWatch();
    private final Logger logger = LoggerFactory.getLogger(ResponseHandler.class);

    public String handleAbout() {
        return util.getAbout();
    }

    public String handleSupportedTiers() {
        return util.getSupportedTiers();
    }

    public void handleEncounter(Tier tier, String charName, String server, Set<Integer> encounterIds) {
        logger.info("starting graphql gen for fflog apis...");
        reportQuery = util.getReportQuery();
        debuffQuery = util.getDebuffQuery();
        encounterIds.stream().sorted().forEach(encounterId -> {
            logger.info("characterDetail graphql api starting...");
            watcher.start();
            GraphQLResponse response = client.getData(
                    util.getCharacterQuery(),
                    util.getVarsForEncounter(charName, server, encounterId));
            watcher.stop();
            logger.info("characterDetail graphql api completed in...{} ms",watcher.getLastTaskTimeMillis());

            if(response == null || response.get("characterData", JsonNode.class).get("character") instanceof NullNode) {
                throw new RuntimeException("I couldn't find a char with the provided name and server");
            }

            logger.info("processing encounter...{}", encounterId);
            JsonNode characterData = response.get("characterData", JsonNode.class);
            JsonNode encounterRankings = characterData.get("character").get("encounterRankings");
            if(encounterRankings == null) {
                logger.info("no encounter found for...{}", encounterId);
                return;
            }
            int totalKills = encounterRankings.get("totalKills").asInt();
            ArrayNode ranks = (ArrayNode) encounterRankings.get("ranks");
            if(ranks.isEmpty()) {
                logger.info("no encounter found for...{}", encounterId);
                return;
            }

            tier.addEncounter(new Encounter(encounterId, totalKills, parseRanks(charName,ranks,tier)));
            // unfortunate repetitive call for now to set the lodestone id
            IntNode lodestoneId = (IntNode) characterData.get("character").get("lodestoneID");
            tier.setLodestoneId(lodestoneId.asText());
        });
    }

    private List<Rank> parseRanks(String charName, ArrayNode ranksJsonNode, Tier tier) {
        List<JsonNode> rankNodes = new ArrayList<>();
        ranksJsonNode.forEach(rankNodes::add);
        try {
            watcher.start();
            List<Rank> ranks = rankNodes.parallelStream().map(rank ->
                new Rank(
                    rank.get("lockedIn").asBoolean(),
                    rank.get("rankPercent").asDouble(),
                    rank.get("spec").asText(),
                    parseDebuffs(charName, rank.get("report"), 0, tier)
                )
            ).collect(Collectors.toList());
            logger.info("completed rank and debuff query/parsing in...{} ms", watcher.getLastTaskTimeMillis());
            watcher.stop();

            return ranks;
        } catch (Exception e) {
            watcher.stop();
            throw e;
        }
//        return Collections.emptyList();
    }

    private int parseDebuffs(String charName, JsonNode reportNode, int attempts, Tier tier) {
        AtomicInteger debuffCount = new AtomicInteger(0);
        // get the report code and fight ID from the encounter
        String reportCode = reportNode.get("code").asText();
        int fightId = reportNode.get("fightID").asInt();
        // using those values, make another graphql request to get the report start and end timestamps
        GraphQLResponse response = client.getData(reportQuery,util.getVarsForReport(reportCode,fightId));
        JsonNode reportData = response.get("reportData", JsonNode.class);
        // if someone's report hides a fight, we will lose visibility and get a null node
        if(reportData.get("report") instanceof NullNode && attempts < 3)
            return debuffCount.get();
        JsonNode report = reportData.get("report").withArray("fights").get(0);
        if(report == null && attempts < 3) {
            return parseDebuffs(charName,reportNode,++attempts,tier);
        }
        else if (attempts >= 3) {
            logger.info("Reached max attempts {} trying to parse debuffs for this encounter. returning 0 and skipping", attempts);
            return debuffCount.get();
        }
        // package the identifier and map it back as vars to another graphql request to get the debuffs
        ReportIdentifier reportIdentifier = new ReportIdentifier(reportCode,fightId,
                report.get("startTime").floatValue(),
                report.get("endTime").floatValue(),
                tier.getDebuffId());

       response = client.getData(debuffQuery,util.getVarsForDebuff(reportIdentifier));
       ArrayNode debuffs;
       try {
            debuffs = response.get("reportData", JsonNode.class)
                .get("report").get("table").get("data").withArray("auras");
           if(tier instanceof Eden) {
               debuffs.forEach(debuffEntity -> {
                   if (!debuffEntity.get("name").asText().equalsIgnoreCase(charName)) return;
                   debuffCount.set(debuffEntity.get("totalUses").intValue());
               });
           }
           else {
               debuffs.forEach(debuffEntity -> {
                   if (!debuffEntity.get("name").asText().equalsIgnoreCase(charName)) return;
                   debuffCount.set(debuffEntity.withArray("appliedByAbilities").size());
               });
           }
       }
       catch (Exception e) {
           return debuffCount.get();
       }

       return debuffCount.get();
    }
}
