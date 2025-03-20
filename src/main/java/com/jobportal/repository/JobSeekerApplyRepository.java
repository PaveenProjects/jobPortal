package com.jobportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jobportal.entity.JobPostActivity;
import com.jobportal.entity.JobSeekerApply;
import com.jobportal.entity.JobSeekerProfile;

@Repository
public interface JobSeekerApplyRepository extends JpaRepository<JobSeekerApply, Integer> {

    List<JobSeekerApply> findByUserId(JobSeekerProfile userId);

    List<JobSeekerApply> findByJob(JobPostActivity job);
    
    @Query(value = "SELECT jsa.id AS jobSeekerApplyId, jsa.user_id, jsa.job, jsa.apply_date, jsa.cover_letter, jsa.status, " +
            "jc.name AS company_name, jl.city AS location_city, jl.state AS location_state, jl.country AS location_country, " +
            "CONCAT(jsp.first_name, ' ', jsp.last_name) AS full_name,jpa.job_title " +
            "FROM job_seeker_apply jsa " +
            "JOIN job_post_activity jpa ON jsa.job = jpa.job_post_id " +
            "JOIN job_company jc ON jpa.job_company_id = jc.id " +
            "JOIN job_location jl ON jpa.job_location_id = jl.id " +
            "JOIN job_seeker_profile jsp ON jsa.user_id = jsp.user_account_id " +
            "WHERE jpa.posted_by_id = :postedById", nativeQuery = true)
List<Object[]> findJobSeekerApplicationsByPostedById(@Param("postedById") Long postedById);
}
