# FFLog Discord Bot
A bot that will crawl through a given user's encounters and then dive into the report for each cleared fight to identify
how many times that character **did a mechanic wrong** 🙃.

## Overview
This bot is a personal project started By Ponti for educational purposes. Underneath the hood is a basic Discord bot
using [JDA](https://github.com/DV8FromTheWorld/JDA), [Spring Boot](https://spring.io/projects/spring-boot), and several
other dependencies that handle OAuth2 and GraphQL queries to [fflogs](https://www.fflogs.com/).

## Pre-requisites
* Coretto JDK 17+
* IntelliJ (optional)
* fflogs account

## Usage
* Checkout the repo and create a new file under `src/main/resources` called `application.properties`.
  * You will need the following keys:
    * `graphql.client.url`
    * `graphql.client.oauth2.client-id`
    * `graphql.client.oauth2.client-secret`
    * `graphql.client.oauth2.token-uri`
  * You can follow instructions provided by fflogs on how to populate those fields: [link](https://articles.fflogs.com/help/api-documentation)
  * Note: these steps are critical for getting access to the fflog GraphQL API
* If you're using IntelliJ, you can create a runtime application using the class [`FflogbotApplication`](https://github.com/vincenthai/fflogbot/blob/master/src/test/java/com/fflog/fflogbot/FflogbotApplicationTests.java)
  * You will also need to supply your Discord bot's token using the environment key `TOKEN`