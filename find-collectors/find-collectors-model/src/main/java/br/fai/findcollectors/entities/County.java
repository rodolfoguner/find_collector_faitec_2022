package br.fai.findcollectors.entities;

public class County extends BaseEntity{
    private String city;
    private State state_id;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public State getState_id() {
        return state_id;
    }

    public void setState_id(State state_id) {
        this.state_id = state_id;
    }
}