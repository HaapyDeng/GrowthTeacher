package com.mpl.GrowthTeacher.Bean;

public class ChooseItemBean {
    public ChooseItemBean(String id, String name) {
        this.id = id;
        this.name = name;
    }

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;
}
