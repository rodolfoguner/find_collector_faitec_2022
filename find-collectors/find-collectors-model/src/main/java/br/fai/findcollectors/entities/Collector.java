package br.fai.findcollectors.entities;

import br.fai.findcollectors.enums.GarbageType;

public class Collector extends Person {
    private GarbageType type;
    private String description;
    private boolean collect_point;
    private Person person_id;
    private Recycler recycler_id;

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

    public boolean isCollect_point() {
        return collect_point;
    }

    public void setCollect_point(boolean collect_point) {
        this.collect_point = collect_point;
    }

    public Person getPerson_id() {
        return person_id;
    }

    public void setPerson_id(Person person_id) {
        this.person_id = person_id;
    }

    public Recycler getRecycler_id() {
        return recycler_id;
    }

    public void setRecycler_id(Recycler recycler_id) {
        this.recycler_id = recycler_id;
    }
}