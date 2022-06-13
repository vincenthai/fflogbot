package com.fflog.fflogbot.model;

import lombok.Getter;

import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public class Asphodelos extends Tier {
    public Map<Integer,String> encounter2Acronym = Stream.of(
                    new AbstractMap.SimpleImmutableEntry<>(78,"P1S"),
                    new AbstractMap.SimpleImmutableEntry<>(79,"P2S"),
                    new AbstractMap.SimpleImmutableEntry<>(80,"P3S"),
                    new AbstractMap.SimpleImmutableEntry<>(81,"P4S pt.1"),
                    new AbstractMap.SimpleImmutableEntry<>(82,"P4S pt.2"))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
}
