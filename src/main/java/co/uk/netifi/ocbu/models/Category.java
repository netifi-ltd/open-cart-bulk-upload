package co.uk.netifi.ocbu.models;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;

public class Category {

    private int categoryId;
    private int parentId;
    private String name;

    private Category parent;
    private List<Category> children = new ArrayList<>();

    public Category () {

    }

    public Category (int categoryId, int parentId, String name) {
        this.categoryId = categoryId;
        this.parentId = parentId;
        this.name = name;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getParent() {
        return parent;
    }

    public void setParent(Category parent) {
        this.parent = parent;
    }

    public void addChild(Category child) {
        if (!this.children.contains(child) && child != null)
            this.children.add(child);
    }

    public List<Category> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("name", name)
                .toString();
    }
}
