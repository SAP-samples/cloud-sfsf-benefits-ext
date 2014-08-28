package com.sap.hana.cloud.samples.benefits.persistence.model;

import java.util.ArrayList;
import java.util.List;

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
@Table(name = "BENEFITS_INFO", uniqueConstraints = { @UniqueConstraint(columnNames = { "name" }) })
public class BenefitInfo implements IDBEntity {

	@Id
	@GeneratedValue
	@Column(name = "BENEFIT_INFO_ID")
	private Long id;

	@Basic
	private String name;

	@Basic
	private String description;

	@Basic
	private String link;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "benefitInfo", fetch = FetchType.EAGER, targetEntity = BenefitType.class)
	private List<BenefitType> types;

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

	public List<BenefitType> getTypes() {
		if (this.types == null) {
			this.types = new ArrayList<>();
		}
		return types;
	}

	public void addType(BenefitType type) {
		getTypes().add(type);
		if (type.getBenefitInfo() != this) {
			type.setBenefitInfo(this);
		}
	}

	public void setTypes(List<BenefitType> types) {
		this.types = types;
	}
}
