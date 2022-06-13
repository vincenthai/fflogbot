package com.fflog.fflogbot.model;

import lombok.*;
import org.springframework.stereotype.Component;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Component
public class Rank {
    boolean lockedIn;
    double rankPercent = 0;
    String spec;
    int debuffs = 0;
}
