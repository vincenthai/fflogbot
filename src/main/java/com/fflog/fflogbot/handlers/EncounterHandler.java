package com.fflog.fflogbot.handlers;

import com.fflog.fflogbot.client.ClientWrapper;
import com.fflog.fflogbot.model.Rank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class EncounterHandler {
    @Autowired
    ClientWrapper client;

    public Rank getTopRank(List<Rank> ranks) {
        return ranks.stream()
                .filter(Rank::isLockedIn)
                .max(Comparator.comparingDouble(Rank::getRankPercent))
                .orElse(new Rank());
    }

    // calculate percentage rate of getting damage down
    public float getAverageDebuffs(List<Rank> ranks, int totalKills) {
        if (totalKills == 0) return 0;
        int fightsWithDmgDown = (int) ranks.stream().filter(rank -> rank.getDebuffs() > 0).count();
        return (float)fightsWithDmgDown/totalKills;
    }
}
