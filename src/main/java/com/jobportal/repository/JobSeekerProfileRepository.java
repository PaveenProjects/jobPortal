package com.jobportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jobportal.entity.JobSeekerProfile;

import jakarta.transaction.Transactional;


public interface JobSeekerProfileRepository extends JpaRepository<JobSeekerProfile, Integer> {

	@Modifying
    @Transactional
    @Query(value = "DELETE FROM job_seeker_profile WHERE user_account_id = :userId", nativeQuery = true)
    void deleteByUserId(@Param("userId") Long userId);

}
