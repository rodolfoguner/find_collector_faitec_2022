package br.fai.findcollectors.entities;

public class City extends BaseEntity {
    private String name;
    private State stateId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public State getStateId() {
        return stateId;
    }

    public void setStateId(State stateId) {
        this.stateId = stateId;
    }
}