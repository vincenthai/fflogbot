package com.fflog.fflogbot.handlers;

import com.fflog.fflogbot.model.Rank;
import com.fflog.fflogbot.model.Tier;
import net.dv8tion.jda.api.EmbedBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicInteger;


@Component
public class MessageHandler {
    @Autowired
    EncounterHandler encounterHandler;
    private final Logger logger = LoggerFactory.getLogger(MessageHandler.class);

    public EmbedBuilder packMessage(Tier tier, String charName, String server) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(WordUtils.capitalizeFully(charName) + " from " + StringUtils.capitalize(server));
        String lodestoneId = tier.getLodestoneId();
        Document doc;
        String imgSrc = "";
        AtomicInteger atomicTotal = new AtomicInteger();
        try {
            doc = Jsoup.parse(new URL("https://na.finalfantasyxiv.com/lodestone/character/"+lodestoneId+"/"),20000);
            Element img = doc.select("img[src*=640x873.jpg]").first();
            imgSrc = img.getElementsByAttribute("src").first().attr("src");
        } catch (IOException e) {
            logger.error("couldn't get lodestone character image for lodestone id {}", lodestoneId);
        }

        if(StringUtils.isNotEmpty(imgSrc)) {
            embedBuilder.setThumbnail(imgSrc);
        }

        tier.getEncounters().forEach(encounter -> {
            int totalKills = encounter.getTotalKills();
            Rank topRank = encounterHandler.getTopRank(encounter.getRanks());
            embedBuilder.addField(tier.getEncounter2Acronym().get(encounter.getEncounterId()) + " Kills",String.valueOf(totalKills),true);
            embedBuilder.addField("Top Parse",String.valueOf((int)topRank.getRankPercent()),true);
            embedBuilder.addField("Avg Dmg ↓",new DecimalFormat("0.00").
                    format(encounterHandler.getAverageDebuffs(encounter.getRanks(), encounter.getTotalKills())),true);

            atomicTotal.addAndGet((int) topRank.getRankPercent());

        });

        int avg = 0;
        if(!tier.getEncounters().isEmpty()) {
            avg = atomicTotal.get() / tier.getEncounters().size();
        }

        if(avg < 25) {
            embedBuilder.setColor(Color.GRAY);
        }
        else if (avg < 50) {
            embedBuilder.setColor(Color.GREEN);
        }
        else if (avg < 75) {
            embedBuilder.setColor(Color.BLUE);
        }
        else if (avg < 95) {
            embedBuilder.setColor(Color.MAGENTA);
        }
        else if (avg < 99) {
            embedBuilder.setColor(Color.ORANGE);
        }
        else if (avg == 99) {
            embedBuilder.setColor(Color.PINK);
        }
        else if (avg == 100) {
            embedBuilder.setColor(Color.YELLOW);
        }

        return embedBuilder;
    }
}
