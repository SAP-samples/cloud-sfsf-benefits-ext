package com.sap.hana.cloud.samples.benefits.persistence.model;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "BENEFITS", uniqueConstraints = { @UniqueConstraint(columnNames = { "name" }) })
public class Benefit implements IDBEntity {

    @Id
    @GeneratedValue
    @Column(name = "BENEFIT_ID")
    private Long id;

    @Basic
    private String name;

    @Basic
    private String description;

    @Basic
    private String link;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "benefit", fetch = FetchType.LAZY, targetEntity = BenefitType.class)
    private Collection<BenefitType> types;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Collection<BenefitType> getTypes() {
        if (this.types == null) {
            this.types = new ArrayList<>();
        }
        return types;
    }

    public void addType(BenefitType type) {
        getTypes().add(type);
        if (type.getBenefit() != this) {
            type.setBenefit(this);
        }
    }

    public void setTypes(Collection<BenefitType> types) {
        this.types = types;
    }
}
