package org.quicksplit.models;

import java.util.List;

public class Group {

    private String id;
    private String name;
    private String admin;
    private List<String> memberships;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getAdmin() {
        return admin;
    }

    public List<String> getMemberships() {
        return memberships;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMemberships(List<String> memberships) {
        this.memberships = memberships;
    }

    @Override
    public String toString() {
        return name;
    }
}