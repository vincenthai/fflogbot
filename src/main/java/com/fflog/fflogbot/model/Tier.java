package com.fflog.fflogbot.model;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public abstract class Tier {
    public List<Encounter> encounters = new LinkedList<>();
    public String errorMsg;
    public Map<Integer,String> encounter2Acronym;
    public String lodestoneId;

    public void addEncounter(Encounter encounter) {
        encounters.add(encounter);
    }

    public void clearEncounters() {
        encounters.clear();
    }

}
