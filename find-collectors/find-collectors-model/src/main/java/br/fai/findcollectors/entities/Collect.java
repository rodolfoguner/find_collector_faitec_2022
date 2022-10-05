package br.fai.findcollectors.entities;

import br.fai.findcollectors.enums.GarbageType;

import java.sql.Timestamp;

public class Collect extends BaseEntity {
    private Timestamp dateAndTime;
    private GarbageType type;
    private boolean accept;
    private boolean collected;
    private boolean recurrent;
    private String cep;
    private String address;
    private String district;
    private String number;
    private City cityId;
    private int collectorId;
    private Person collector;
    private Recycler recyclerId;

    public int getCollectorId() {
        return collectorId;
    }

    public void setCollectorId(int collectorId) {
        this.collectorId = collectorId;
    }

    public Person getCollector() {
        return collector;
    }

    public void setCollector(Person collector) {
        this.collector = collector;
    }

    public Timestamp getDateAndTime() {
        return dateAndTime;
    }

    public void setDateAndTime(Timestamp dateAndTime) {
        this.dateAndTime = dateAndTime;
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

    public City getCityId() {
        return cityId;
    }

    public void setCityId(City cityId) {
        this.cityId = cityId;
    }


    public Recycler getRecyclerId() {
        return recyclerId;
    }

    public void setRecyclerId(Recycler recyclerId) {
        this.recyclerId = recyclerId;
    }
}