package com.fflog.fflogbot.model;

import lombok.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Component
public class Encounter {

    int encounterId;
    int totalKills;
    List<Rank> ranks;
}
