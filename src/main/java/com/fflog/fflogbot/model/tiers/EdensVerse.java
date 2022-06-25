package com.fflog.fflogbot.model.tiers;

import lombok.Getter;

import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public class EdensVerse extends Eden {
    public Map<Integer,String> encounter2Acronym = Stream.of(
                    new AbstractMap.SimpleImmutableEntry<>(69,"Ramuh"),
                    new AbstractMap.SimpleImmutableEntry<>(70,"Ifrit and Garuda"),
                    new AbstractMap.SimpleImmutableEntry<>(71,"The Idol of Darkness"),
                    new AbstractMap.SimpleImmutableEntry<>(72,"Shiva"))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    public int debuffId = 1002092;
}
