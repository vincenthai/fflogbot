package com.fflog.fflogbot.model.tiers;

import lombok.Getter;

import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public class EdensPromise extends Eden {
    public Map<Integer,String> encounter2Acronym = Stream.of(
                    new AbstractMap.SimpleImmutableEntry<>(73,"Cloud of Darkness"),
                    new AbstractMap.SimpleImmutableEntry<>(74,"Shadowkeeper"),
                    new AbstractMap.SimpleImmutableEntry<>(75,"Fatebreaker"),
                    new AbstractMap.SimpleImmutableEntry<>(76,"Eden's Promise"),
                    new AbstractMap.SimpleImmutableEntry<>(77,"Oracle of Darkness"))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    public int debuffId = 1002092;
}
