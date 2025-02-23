package com.blu4ck.fundickonot.model;

public class SubFolder {
    private final int id;
    private final String name;
    private final int parentId;
    private final String parentName;

    public SubFolder(int id, String name, int parentId, String parentName) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
        this.parentName = parentName;
    }

    public int getId() { return id; }

    public String getName() { return name; }

    public int getParentId() { return parentId; }

    public String getParentName() { return parentName; }

    @Override
    public String toString() {
        return name;
    }
}
