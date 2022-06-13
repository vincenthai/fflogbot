package com.fflog.fflogbot.model;

import lombok.Getter;

import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public class Asphodelos extends Tier {
    public Map<Integer,String> encounter2Acronym = Stream.of(
                    new AbstractMap.SimpleImmutableEntry<>(78,"Erichthonios"),
                    new AbstractMap.SimpleImmutableEntry<>(79,"Hippokampos"),
                    new AbstractMap.SimpleImmutableEntry<>(80,"Phoinix"),
                    new AbstractMap.SimpleImmutableEntry<>(81,"Hesperos"),
                    new AbstractMap.SimpleImmutableEntry<>(82,"Hesperos II"))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
}
