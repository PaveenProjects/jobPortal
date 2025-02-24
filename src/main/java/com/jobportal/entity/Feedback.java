package com.jobportal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "feedback")
public class Feedback 
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(name= "email")
	private String email;
	
	@Column(name= "first_name")
	private String firstName;
	
	@Column(name= "last_name")
	private String lastName;

	@Column(name="job_opportunities")
	private int jobOpportunities;

	@Column(name="job_search")
	private int jobSearch;

	@Column(name="job_application")
	private int jobApplication;

	@Column(name="overall_experience")
	private int overallExperience;

	@Column(name= "comment")
	private String comment;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public int getJobOpportunities() {
		return this.jobOpportunities;
	}

	public void setJobOpportunities(int jobOpportunities) {
		this.jobOpportunities = jobOpportunities;
	}

	public int getJobSearch() {
		return this.jobSearch;
	}

	public void setJobSearch(int jobSearch) {
		this.jobSearch = jobSearch;
	}

	public int getJobApplication() {
		return this.jobApplication;
	}

	public void setJobApplication(int jobApplication) {
		this.jobApplication = jobApplication;
	}

	public int getOverallExperience() {
		return this.overallExperience;
	}

	public void setOverallExperience(int overallExperience) {
		this.overallExperience = overallExperience;
	}

	public String getComment() {
		return this.comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
	@Override
	public String toString() {
		return "{" +
			" id='" + getId() + "'" +
			", email='" + getEmail() + "'" +
			", firstName='" + getFirstName() + "'" +
			", lastName='" + getLastName() + "'" +
			", jobOpportunities='" + getJobOpportunities() + "'" +
			", jobSearch='" + getJobSearch() + "'" +
			", jobApplication='" + getJobApplication() + "'" +
			", overallExperience='" + getOverallExperience() + "'" +
			", comment='" + getComment() + "'" +
			"}";
	}
	
}
