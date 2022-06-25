package com.fflog.fflogbot.listeners;

import com.fflog.fflogbot.handlers.EncounterHandler;
import com.fflog.fflogbot.handlers.MessageHandler;
import com.fflog.fflogbot.handlers.ResponseHandler;
import com.fflog.fflogbot.model.tiers.Asphodelos;
import com.fflog.fflogbot.model.tiers.EdensPromise;
import com.fflog.fflogbot.model.tiers.EdensVerse;
import com.fflog.fflogbot.model.tiers.Tier;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class CharacterListener extends ListenerAdapter {

    @Autowired
    private ResponseHandler responseHandler;
    @Autowired
    EncounterHandler encounterHandler;
    @Autowired
    MessageHandler messageHandler;

    private static final Pattern LOG_PATTERN = Pattern.compile("\\$log\s+([\\w\\s]+)@([\\w]+;?)(;\\w+)?");
    private static final Logger logger = LoggerFactory.getLogger(CharacterListener.class);
    private static final StopWatch watcher = new StopWatch();
    private static final String LATEST_TIER = "asphodelos";

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if(event.getAuthor().isBot()) return;
        Message message = event.getMessage();
        String content = message.getContentRaw();
        MessageChannel channel = event.getChannel();
        if(content.startsWith("$log")) {
            Matcher m = LOG_PATTERN.matcher(content);
            if(m.matches()) {
                channel.sendTyping().queue();
                watcher.start();
                String charName = m.group(1);
                String server = m.group(2);
                if(server.contains(";")) {
                    server = server.substring(0,server.length()-1);
                }
                logger.info("log requested for {} @ {} ", charName, server);
                String tierStr = m.group(3) != null ? m.group(3).substring(1) : LATEST_TIER;
                logger.info("processing {} tier for {}", tierStr, charName);
                Optional<Tier> tier = findTier(tierStr);

                if(tier.isEmpty()) {
                    message.reply("Could not find tier '"+tierStr+"'. Please use `$tiers` to get the appropriate tier names").queue();
                    return;
                }

                processTier(tier.get(), charName, server, message);
            }
        }
        else if(content.startsWith("$about")) {
            message.reply(responseHandler.handleAbout()).queue();
        }
        else if(content.startsWith("$tiers")) {
            message.reply(responseHandler.handleSupportedTiers()).queue();
        }
        else if(content.contains("$toxic")) {
            message.reply("https://www.youtube.com/watch?v=LOZuxwVk7TU").queue();
        }
    }

    private Optional<Tier> findTier(String tierStr) {
        if(StringUtils.containsIgnoreCase(tierStr,Asphodelos.class.getSimpleName())) {
            return Optional.of(new Asphodelos());
        }
        else if(StringUtils.containsIgnoreCase(tierStr, EdensPromise.class.getSimpleName())) {
            return Optional.of(new EdensPromise());
        }
        else if(StringUtils.containsIgnoreCase(tierStr, EdensVerse.class.getSimpleName())) {
            return Optional.of(new EdensVerse());
        }

        return Optional.empty();
    }

    private void processTier(Tier tier, String charName, String server, Message message) {
        try {
            responseHandler.handleEncounter(tier, charName, server, tier.getEncounter2Acronym().keySet());
            EmbedBuilder embedBuilder = messageHandler.packMessage(tier, charName, server);
            watcher.stop();
            embedBuilder.setFooter("Parse processing completed in: " + watcher.getLastTaskTimeMillis()/1000 + " secs");
            message.replyEmbeds(embedBuilder.build()).queue();
        }
        catch (Exception e) {
            watcher.stop();
            message.replyEmbeds(new EmbedBuilder()
                    .setTitle("Failed to process " + WordUtils.capitalizeFully(charName) + " from " + WordUtils.capitalize(server))
                    .setImage("https://i.kym-cdn.com/photos/images/newsfeed/001/889/560/b85.gif").build()).queue();
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        finally {
            tier.clearEncounters();
        }
    }
}
