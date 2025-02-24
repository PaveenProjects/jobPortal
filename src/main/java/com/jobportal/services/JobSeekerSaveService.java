package com.jobportal.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.jobportal.entity.JobPostActivity;
import com.jobportal.entity.JobSeekerApply;
import com.jobportal.entity.JobSeekerProfile;
import com.jobportal.entity.JobSeekerSave;
import com.jobportal.repository.JobSeekerApplyRepository;
import com.jobportal.repository.JobSeekerSaveRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional  
public class JobSeekerSaveService {

    private final JobSeekerSaveRepository jobSeekerSaveRepository;
    
    private final JobSeekerApplyRepository jobSeekerApplyRepository;

    public JobSeekerSaveService(JobSeekerSaveRepository jobSeekerSaveRepository, JobSeekerApplyRepository jobSeekerApplyRepository) {
        this.jobSeekerSaveRepository = jobSeekerSaveRepository;
        this.jobSeekerApplyRepository = jobSeekerApplyRepository;
    }

    public List<JobSeekerSave> getCandidatesJob(JobSeekerProfile userAccountId) {
        return jobSeekerSaveRepository.findByUserId(userAccountId);
    }
	public List<JobSeekerApply> getAppliedJobs(JobSeekerProfile userAccountId) {
		 return jobSeekerApplyRepository.findByUserId(userAccountId);
	}

    public List<JobSeekerSave> getJobCandidates(JobPostActivity job) {
        return jobSeekerSaveRepository.findByJob(job);
    }

    public void addNew(JobSeekerSave jobSeekerSave) {
        jobSeekerSaveRepository.save(jobSeekerSave);
    }

//    public Optional<JobSeekerSave> findByUserAndJob(JobSeekerProfile user, JobPostActivity job) {
//        return jobSeekerSaveRepository.findByUserIdAndJob(user, job);
//    }
    
    @Transactional
    public JobSeekerSave saveOrUpdate(JobSeekerProfile user, JobPostActivity job) {
        Optional<JobSeekerSave> existingJobSeekerSave = jobSeekerSaveRepository.findByUserIdAndJob(user, job);

        JobSeekerSave jobSeekerSave;
        if (existingJobSeekerSave.isPresent()) {
            jobSeekerSave = existingJobSeekerSave.get();
        } else {
            jobSeekerSave = new JobSeekerSave();
            jobSeekerSave.setUserId(user);
            jobSeekerSave.setJob(job);
        }

        return jobSeekerSaveRepository.save(jobSeekerSave);
    }



}
