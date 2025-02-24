package com.jobportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jobportal.entity.JobPostActivity;
import com.jobportal.entity.JobSeekerProfile;
import com.jobportal.entity.JobSeekerSave;

import jakarta.persistence.LockModeType;

@Repository
public interface JobSeekerSaveRepository extends JpaRepository<JobSeekerSave, Integer> {

    public List<JobSeekerSave> findByUserId(JobSeekerProfile userAccountId);

    List<JobSeekerSave> findByJob(JobPostActivity job);

   // Optional<JobSeekerSave> findByUserIdAndJob(JobSeekerProfile userId, JobPostActivity job);
    
    @Query("SELECT j FROM JobSeekerSave j WHERE j.userId = :userId AND j.job = :job")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<JobSeekerSave> findByUserIdAndJob(@Param("userId") JobSeekerProfile userId, @Param("job") JobPostActivity job);


}
