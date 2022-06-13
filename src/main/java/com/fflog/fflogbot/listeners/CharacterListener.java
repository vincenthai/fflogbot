package com.fflog.fflogbot.listeners;

import com.fflog.fflogbot.handlers.EncounterHandler;
import com.fflog.fflogbot.handlers.MessageHandler;
import com.fflog.fflogbot.handlers.ResponseHandler;
import com.fflog.fflogbot.model.Asphodelos;
import com.fflog.fflogbot.model.Tier;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

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

    private static final Pattern LOG_PATTERN = Pattern.compile("\\$log\s+([\\w\\s]+);([\\w]+)");
    private static final Logger logger = LoggerFactory.getLogger(CharacterListener.class);
    private static final StopWatch watcher = new StopWatch();

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if(event.getAuthor().isBot()) return;
        Message message = event.getMessage();
        String content = message.getContentRaw();
        MessageChannel channel = event.getChannel();
        if(content.startsWith("$log")) {
            channel.sendTyping().queue();
            watcher.start();
            logger.info("user msg received");
            Matcher m = LOG_PATTERN.matcher(content);
            if(m.matches()) {
                String charName = m.group(1);
                String server = m.group(2);
                // handle the asphodelos tier
                logger.info("processing asphodelos tier for...{}",charName);
                asphodelosTier(charName, server, channel);
            }
        }
        else if(content.startsWith("$about")) {
            channel.sendMessage(responseHandler.handleAbout()).queue();
            watcher.stop();
        }
    }

    private void asphodelosTier(String charName, String server, MessageChannel channel) {
        Tier asphodelos = new Asphodelos();
        try {
            responseHandler.handleEncounter(channel,asphodelos, charName, server, 78, 79, 80, 81, 82);
            String payload = messageHandler.packMessage(asphodelos);
            if(payload.isEmpty()) {
                payload = "No savage parses found for this scrub!";
            }
            channel.sendMessage(payload).queue();
            watcher.stop();
            channel.sendMessageFormat("**Total command runtime**: %d secs",watcher.getLastTaskTimeMillis()/1000).queue();
        }
        catch (RuntimeException e) {
            watcher.stop();
            throw e;
        }
        finally {
            asphodelos.clearEncounters();
        }
    }
}
