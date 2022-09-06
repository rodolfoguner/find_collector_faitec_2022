package br.fai.findcollectors.entities;

import br.fai.findcollectors.enums.GarbageType;

public class Collector extends Person {
    private GarbageType type;
    private String description;
    private boolean collectPoint;
    private Person personId;
    private Recycler recyclerId;

    public GarbageType getType() {
        return type;
    }

    public void setType(GarbageType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCollectPoint() {
        return collectPoint;
    }

    public void setCollectPoint(boolean collectPoint) {
        this.collectPoint = collectPoint;
    }

    public Person getPersonId() {
        return personId;
    }

    public void setPersonId(Person personId) {
        this.personId = personId;
    }

    public Recycler getRecyclerId() {
        return recyclerId;
    }

    public void setRecyclerId(Recycler recyclerId) {
        this.recyclerId = recyclerId;
    }
}