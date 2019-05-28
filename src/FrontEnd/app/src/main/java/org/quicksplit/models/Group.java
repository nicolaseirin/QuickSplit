package org.quicksplit.models;

import java.util.List;

public class Group {

    private String name;
    private String admin;
    private List<String> members;

    public String getName() {
        return name;
    }

    public String getAdmin() {
        return admin;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }
}