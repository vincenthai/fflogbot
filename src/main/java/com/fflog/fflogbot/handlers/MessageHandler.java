package com.fflog.fflogbot.handlers;

import com.fflog.fflogbot.model.Rank;
import com.fflog.fflogbot.model.Tier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;


@Component
public class MessageHandler {
    @Autowired
    EncounterHandler encounterHandler;

    public String packMessage(Tier tier) {
        StringBuilder resp = new StringBuilder("");
        tier.getEncounters().forEach(encounter -> {
            int totalKills = encounter.getTotalKills();
            Rank topRank = encounterHandler.getTopRank(encounter.getRanks());
            resp
                .append("__")
                .append(tier.getEncounter2Acronym().get(encounter.getEncounterId()))
                .append("__ \n")
                .append("  **")
                .append("Total Kills**: ")
                .append(totalKills)
                .append("\n")
                .append("  **")
                .append("Top Parse**: ")
                .append((int)topRank.getRankPercent());
                if(topRank.getRankPercent() > 0) {
                    resp.append(" as ")
                    .append(topRank.getSpec());
                }
            resp
                .append("\n")
                .append("  **")
                .append("Avg Dmg ↓**: ")
                .append(new DecimalFormat("0.00").
                        format(encounterHandler.getAverageDebuffs(encounter.getRanks(), encounter.getTotalKills())))
                .append("\n");
        });
        return resp.toString();
    }
}
