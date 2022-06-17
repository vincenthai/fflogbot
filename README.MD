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
* [Gradle](https://gradle.org/install/) 7.4.2+
* [FFLogs](https://www.fflogs.com/) Account
* [Docker Desktop](https://www.docker.com/products/docker-desktop/)

## Usage
* Checkout the repo and create a new file under `src/main/resources` called `application.properties`.
  * You will need the following keys:
    * `graphql.client.url`
    * `graphql.client.oauth2.client-id`
    * `graphql.client.oauth2.client-secret`
    * `graphql.client.oauth2.token-uri`
  * You can follow instructions provided by fflogs on how to populate those fields: [link](https://articles.fflogs.com/help/api-documentation)
  * Note: these steps are critical for getting access to the fflog GraphQL API
* Create a `private.env` file and supply an environment variable with the key `TOKEN` and the value being your Discord bot's token
* Run `./gradlew clean build`
* Run `docker-compose --env-file ./version.env up -d --force-recreate --build`
  * `--env-file <param>` allows for specifying the version through the `version.env` file
  * `-d` will have the container run in detached mode
  * `--force-recreate` will force container recreation even if the image hasn't changed
  * `--build` rebuilds the docker image based on the `Dockerfile` generated via the [Gradle Docker Plugin](https://github.com/bmuschko/gradle-docker-plugin)