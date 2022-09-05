package br.fai.findcollectors.entities;

public class County extends BaseEntity{
    private String city;
    private State stateId;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public State getStateId() {
        return stateId;
    }

    public void setStateId(State stateId) {
        this.stateId = stateId;
    }
}