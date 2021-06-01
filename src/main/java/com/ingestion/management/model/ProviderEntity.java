package com.ingestion.management.model;

//import javax.persistence.*;

public class ProviderEntity {

    // The associated id
    Integer id;

    // The provider's name (for example BBC)
    String name;

    // The average credibility based on all the posts gathered from this source
    double credibility;

    // A URL to the avatar picture of this provider
    String avatar;

    public ProviderEntity() {

    }

    public ProviderEntity(String name, double credibility, String avatar) {
        this.name = name;
        this.credibility = credibility;
        this.avatar = avatar;
    }

    public ProviderEntity(Integer id, String name, double credibility, String avatar) {
        this.id = id;
        this.name = name;
        this.credibility = credibility;
        this.avatar = avatar;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getCredibility() {
        return credibility;
    }

    public void setCredibility(double credibility) {
        this.credibility = credibility;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ProviderEntity that = (ProviderEntity) o;

        if (Double.compare(that.credibility, credibility) != 0)
            return false;
        if (!id.equals(that.id))
            return false;
        if (!name.equals(that.name))
            return false;
        return avatar.equals(that.avatar);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id.hashCode();
        result = 31 * result + name.hashCode();
        temp = Double.doubleToLongBits(credibility);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + avatar.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ProviderEntity{" + "id=" + id + ", name='" + name + '\'' + ", credibility=" + credibility + ", avatar='"
                + avatar + '\'' + '}';
    }
}
