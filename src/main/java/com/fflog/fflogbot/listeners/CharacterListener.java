package com.fflog.fflogbot.listeners;

import com.fflog.fflogbot.handlers.EncounterHandler;
import com.fflog.fflogbot.handlers.MessageHandler;
import com.fflog.fflogbot.handlers.ResponseHandler;
import com.fflog.fflogbot.model.Asphodelos;
import com.fflog.fflogbot.model.Tier;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.text.WordUtils;
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
                asphodelosTier(charName, server, message);
            }
        }
        else if(content.startsWith("$about")) {
            channel.sendMessage(responseHandler.handleAbout()).queue();
            watcher.stop();
        }
    }

    private void asphodelosTier(String charName, String server, Message message) {
        Tier asphodelos = new Asphodelos();
        try {
            responseHandler.handleEncounter(asphodelos, charName, server, 78, 79, 80, 81, 82);
            EmbedBuilder embedBuilder = messageHandler.packMessage(asphodelos, charName, server);
            watcher.stop();
            embedBuilder.setFooter("Parse processing completed in: " + watcher.getLastTaskTimeMillis()/1000 + " secs");
            message.replyEmbeds(embedBuilder.build()).queue();
        }
        catch (RuntimeException e) {
            watcher.stop();
            message.replyEmbeds(new EmbedBuilder()
                    .setTitle("Failed to process " + WordUtils.capitalizeFully(charName) + " from " + WordUtils.capitalize(server))
                    .setImage("https://i.kym-cdn.com/photos/images/newsfeed/001/889/560/b85.gif").build()).queue();
        }
        finally {
            asphodelos.clearEncounters();
        }
    }
}
