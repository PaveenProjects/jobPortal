package com.jobportal.controller;

import com.jobportal.entity.JobPostActivity;
import com.jobportal.entity.JobSeekerApply;
import com.jobportal.entity.JobSeekerProfile;
import com.jobportal.entity.JobSeekerSave;
import com.jobportal.entity.Users;
import com.jobportal.repository.JobSeekerApplyRepository;
import com.jobportal.services.JobPostActivityService;
import com.jobportal.services.JobSeekerProfileService;
import com.jobportal.services.JobSeekerSaveService;
import com.jobportal.services.UsersService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.*;

@Controller
public class JobSeekerSaveController {

    private final UsersService usersService;
    private final JobSeekerProfileService jobSeekerProfileService;
    private final JobPostActivityService jobPostActivityService;
    private final JobSeekerSaveService jobSeekerSaveService;
    private final JobSeekerApplyRepository jobSeekerApplyRepository;
    

    
    public JobSeekerSaveController(UsersService usersService, JobSeekerProfileService jobSeekerProfileService, JobPostActivityService jobPostActivityService, JobSeekerSaveService jobSeekerSaveService , JobSeekerApplyRepository jobSeekerApplyRepository) {
        this.usersService = usersService;
        this.jobSeekerProfileService = jobSeekerProfileService;
        this.jobPostActivityService = jobPostActivityService;
        this.jobSeekerSaveService = jobSeekerSaveService;
        this.jobSeekerApplyRepository = jobSeekerApplyRepository;
    }

//    @PostMapping("job-details/save/{id}")
//    @Transactional
//    public String save(@PathVariable("id") int id, JobSeekerSave jobSeekerSave) {
//   
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (!(authentication instanceof AnonymousAuthenticationToken)) {
//            String currentUsername = authentication.getName();
//            Users user = usersService.findByEmail(currentUsername);
//            Optional<JobSeekerProfile> seekerProfile = jobSeekerProfileService.getOne(user.getUserId());
//            JobPostActivity jobPostActivity = jobPostActivityService.getOne(id);
//            if (seekerProfile.isPresent() && jobPostActivity != null) {
//                jobSeekerSave.setJob(jobPostActivity);
//                jobSeekerSave.setUserId(seekerProfile.get());
//            } else {
//                throw new RuntimeException("User not found");
//            }
//            jobSeekerSaveService.addNew(jobSeekerSave);
//        }
//        return "redirect:/dashboard/";
//    }

    @PostMapping("job-details/save/{id}")
    @Transactional
    public String save(@PathVariable("id") int id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUsername = authentication.getName();
            Users user = usersService.findByEmail(currentUsername);
            Optional<JobSeekerProfile> seekerProfile = jobSeekerProfileService.getOne(user.getUserId());
            JobPostActivity jobPostActivity = jobPostActivityService.getOne(id);

            if (seekerProfile.isPresent() && jobPostActivity != null) {
                jobSeekerSaveService.saveOrUpdate(seekerProfile.get(), jobPostActivity);
            } else {
                throw new RuntimeException("User not found");
            }
        }
        return "redirect:/dashboard/";
    }


    @GetMapping("saved-jobs/")
    public String savedJobs(Model model) {

        List<JobPostActivity> jobPost = new ArrayList<>();
        Object currentUserProfile = usersService.getCurrentUserProfile();

        List<JobSeekerSave> jobSeekerSaveList = jobSeekerSaveService.getCandidatesJob((JobSeekerProfile) currentUserProfile);
        for (JobSeekerSave jobSeekerSave : jobSeekerSaveList) {
            jobPost.add(jobSeekerSave.getJob());
        }

        model.addAttribute("jobPost", jobPost);
        model.addAttribute("user", currentUserProfile);

        return "saved-jobs";
    }
    
    @GetMapping("applied-jobs/")
    public String appliedJobs(Model model) {

        List<JobPostActivity> jobPost = new ArrayList<>();
        Object currentUserProfile = usersService.getCurrentUserProfile();

        List<JobSeekerApply> jobSeekerApplyList = jobSeekerSaveService.getAppliedJobs((JobSeekerProfile) currentUserProfile);
        for (JobSeekerApply jobSeekerApply : jobSeekerApplyList) {
            jobPost.add(jobSeekerApply.getJob());
        }

        model.addAttribute("jobPost", jobPost);
        model.addAttribute("user", currentUserProfile);

        return "applied-jobs";
    }
    
    @GetMapping("applied-jobs-rec/")
    public String appliedJobsRec(Model model) {
System.out.println("rec job ====================");
        List<JobPostActivity> jobPost = new ArrayList<>();
        Object currentUserProfile = usersService.getCurrentUserProfile();

        List<JobSeekerApply> jobSeekerApplyList = jobSeekerApplyRepository.findAll();
        for (JobSeekerApply jobSeekerApply : jobSeekerApplyList) {
            jobPost.add(jobSeekerApply.getJob());
        }

        model.addAttribute("jobPost", jobSeekerApplyList);
        model.addAttribute("user", currentUserProfile);

        return "applied-jobs-rec";
    }
}
