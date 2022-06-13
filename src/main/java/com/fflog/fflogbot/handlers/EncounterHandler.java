package com.fflog.fflogbot.handlers;

import com.fflog.fflogbot.client.ClientWrapper;
import com.fflog.fflogbot.model.Rank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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

    public float getAverageDebuffs(List<Rank> ranks, int totalKills) {
        if(totalKills == 0) return 0;
        AtomicInteger debuffs = new AtomicInteger(0);
        ranks.forEach(rank -> debuffs.addAndGet(rank.getDebuffs()));
        return (float)debuffs.get()/totalKills;
    }
}
