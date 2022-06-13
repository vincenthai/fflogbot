package com.fflog.fflogbot.client;

import com.fasterxml.jackson.databind.JsonNode;
import graphql.kickstart.spring.webclient.boot.GraphQLRequest;
import graphql.kickstart.spring.webclient.boot.GraphQLResponse;
import graphql.kickstart.spring.webclient.boot.GraphQLWebClient;
import org.springframework.stereotype.Component;

@Component
public class ClientWrapper {
    private final GraphQLWebClient webclient;

    ClientWrapper(GraphQLWebClient webClient) {
        this.webclient = webClient;
    }

    public GraphQLResponse getData(String query, String vars) {
        GraphQLRequest request = GraphQLRequest.builder()
                .query(query)
                .variables(vars)
                .build();
        return webclient.post(request).block();

    }

}
