package com.pms.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "schemes")
public class Scheme{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotBlank(message = "Scheme name is mandatory")
	@Size(max = 20, message = "Scheme name cannot exceed 20 characters")
	private String schemeName;

//	@Size(max = 500, message = "Description cannot exceed 500 characters")
//	@NotBlank(message = "Eligibility criteria is required")
//	private String description;
	@NotBlank(message = "Description is required")
	@Size(max = 500)
	private String description;

	@NotBlank(message = "Eligibility criteria is required")
	private String eligibilityCriteria;

	@NotBlank(message = "Benefits are required")
	private String benefits;

//	@Size(max = 1000, message = "Scheme details cannot exceed 1000 characters")
//	@NotBlank(message = "Scheme details are required")
//	private String schemeDetails;
	@NotBlank(message = "Scheme details are required")
	@Size(max = 1000)
	private String schemeDetails;

	private boolean schemeIsActive;

//	@OneToMany(mappedBy = "scheme", cascade = CascadeType.ALL, orphanRemoval = true)
//	private List<Policy> policies;
//
//	@OneToMany(mappedBy = "scheme", cascade = CascadeType.ALL, orphanRemoval = true)
//	private List<Feedback> feedbacks;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSchemeName() {
		return schemeName;
	}

	public void setSchemeName(String schemeName) {
		this.schemeName = schemeName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}



	public String getEligibilityCriteria() {
		return eligibilityCriteria;
	}

	public void setEligibilityCriteria (String eligibilityCriteria) {
		this.eligibilityCriteria = eligibilityCriteria;
	}

	public String getBenefits() {
		return benefits;
	}

	public void setBenefits (String benefits) {
		this.benefits = benefits;
	}

	public String getSchemeDetails() {
		return schemeDetails;
	}

	public void setSchemeDetails(String schemeDetails) {
		this.schemeDetails = schemeDetails;
	}

	public boolean isSchemeIsActive() {
		return schemeIsActive;
	}

	public void setSchemeIsActive(boolean schemeIsActive) {
		this.schemeIsActive = schemeIsActive;
	}

	@Override
	public String toString() {
		return "Scheme{" +
				"id=" + id +
				", schemeName='" + schemeName + '\'' +
				", description='" + description + '\'' +
				", eligibiltyCriteria='" + eligibilityCriteria + '\'' +
				", benifits='" + benefits + '\'' +
				", schemeIsActive=" + schemeIsActive +
				'}';
	}
}