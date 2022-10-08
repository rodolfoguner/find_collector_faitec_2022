package br.fai.findcollectors.entities;

import br.fai.findcollectors.enums.GarbageType;
import br.fai.findcollectors.enums.PersonType;

import java.util.List;

public class Person extends BaseEntity {

    private String email;
    private String password;
    private String name;
    private String telephone;
    private String cep;
    private String address;
    private String district;
    private String number;

    private PersonType personType;

    private int godfatherId;

    private Person godfather;

    private List<GarbageType> garbageType;

    private String description;

    private boolean collectPoint;

    private int cityId;
    private City city;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTelephone() {
        return telephone;
    }

    public PersonType getPersonType() {
        return personType;
    }

    public void setPersonType(PersonType personType) {
        this.personType = personType;
    }

    public int getGodfatherId() {
        return godfatherId;
    }

    public void setGodfatherId(int godfatherId) {
        this.godfatherId = godfatherId;
    }

    public Person getGodfather() {
        return godfather;
    }

    public void setGodfather(Person godfather) {
        this.godfather = godfather;
    }

    public List<GarbageType> getGarbageType() {
        return garbageType;
    }

    public void setGarbageType(List<GarbageType> garbageType) {
        this.garbageType = garbageType;
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

    public void setTelephone(String telephone) {
        this.telephone = telephone;
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

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }
}