package br.fai.findcollectors.entities;

import br.fai.findcollectors.enums.GarbageType;

import java.sql.Timestamp;

public class Collect extends BaseEntity {
    private Timestamp date_And_Time;
    private GarbageType type;
    private boolean accept;
    private boolean collected;
    private boolean recurrent;
    private String cep;
    private String address;
    private String district;
    private String number;
    private County county_id;
    private Collector collector_id;
    private Recycler recycler_id;

    public Timestamp getDate_And_Time() {
        return date_And_Time;
    }

    public void setDate_And_Time(Timestamp date_And_Time) {
        this.date_And_Time = date_And_Time;
    }

    public GarbageType getType() {
        return type;
    }

    public void setType(GarbageType type) {
        this.type = type;
    }

    public boolean isAccept() {
        return accept;
    }

    public void setAccept(boolean accept) {
        this.accept = accept;
    }

    public boolean isCollected() {
        return collected;
    }

    public void setCollected(boolean collected) {
        this.collected = collected;
    }

    public boolean isRecurrent() {
        return recurrent;
    }

    public void setRecurrent(boolean recurrent) {
        this.recurrent = recurrent;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public County getCounty_id() {
        return county_id;
    }

    public void setCounty_id(County county_id) {
        this.county_id = county_id;
    }

    public Collector getCollector_id() {
        return collector_id;
    }

    public void setCollector_id(Collector collector_id) {
        this.collector_id = collector_id;
    }

    public Recycler getRecycler_id() {
        return recycler_id;
    }

    public void setRecycler_id(Recycler recycler_id) {
        this.recycler_id = recycler_id;
    }
}