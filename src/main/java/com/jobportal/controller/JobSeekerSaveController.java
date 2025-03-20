package com.jobportal.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.jobportal.entity.JobCompany;
import com.jobportal.entity.JobLocation;
import com.jobportal.entity.JobPostActivity;
import com.jobportal.entity.JobSeekerApply;
import com.jobportal.entity.JobSeekerProfile;
import com.jobportal.entity.JobSeekerSave;
import com.jobportal.entity.RecruiterProfile;
import com.jobportal.entity.Users;
import com.jobportal.repository.JobSeekerApplyRepository;
import com.jobportal.services.JobPostActivityService;
import com.jobportal.services.JobSeekerProfileService;
import com.jobportal.services.JobSeekerSaveService;
import com.jobportal.services.UsersService;

import jakarta.transaction.Transactional;

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
        RecruiterProfile profile = (RecruiterProfile) currentUserProfile;
        Long userAccountId = (long) profile.getUserAccountId();
        System.out.println("current"+currentUserProfile);
        
        List<Object[]> results = jobSeekerApplyRepository.findJobSeekerApplicationsByPostedById(userAccountId);
        List<JobSeekerApply> jobSeekerApplies = new ArrayList<>();
System.out.println(results);
        for (Object[] result : results) {
            Integer jobSeekerApplyId = (Integer) result[0];
            Integer userId = (Integer) result[1];
            Integer jobPostId = (Integer) result[2];
            Date applyDate = (Date) result[3];
            String coverLetter = (String) result[4];
            String status = (String) result[5];
            String companyName = (String) result[6];
            String locationCity = (String) result[7];
            String locationState = (String) result[8];
            String locationCountry = (String) result[9];
            String fullName = (String) result[10];
            String jobTitle = (String) result[11];

            // Create the JobSeekerApply object
            JobSeekerApply jobSeekerApply = new JobSeekerApply();
            jobSeekerApply.setId(jobSeekerApplyId);
            jobSeekerApply.setApplyDate(applyDate);
            jobSeekerApply.setCoverLetter(coverLetter);
            jobSeekerApply.setStatus(status);

            // Set the JobSeekerProfile (userId) and JobPostActivity (job) manually if needed
            JobSeekerProfile userProfile = new JobSeekerProfile();
            Users users=new Users();
            users.setUserId(userId);
            System.out.println(userId);
            userProfile.setUserAccountId(userId);
            userProfile.setUserId(users);
            userProfile.setFirstName(fullName);
            jobSeekerApply.setUserId(userProfile);
            JobLocation job=new JobLocation();
            job.setId(jobPostId);
            
            JobCompany jobCompany=new JobCompany();
            jobCompany.setName(companyName);
           
            JobLocation jobLocation =new JobLocation();
            jobLocation.setCity(locationCity);
            jobLocation.setState(locationState);
            
            JobPostActivity jobPostActivity = new JobPostActivity();
            jobPostActivity.setJobPostId(jobPostId);
            jobPostActivity.setJobLocationId(job);
            jobPostActivity.setJobCompanyId(jobCompany);
            jobPostActivity.setJobLocationId(jobLocation);
            jobSeekerApply.setJob(jobPostActivity);
            jobPostActivity.setJobTitle(jobTitle);
            jobPostActivity.setPostedDate(applyDate);

            // Optionally set company and location info if required for display, logging, or further processing
            System.out.println("Company: " + companyName + ", Location: " + locationCity + ", " + locationState + ", " + locationCountry);
            System.out.println("Applicant Full Name: " + fullName);

            jobSeekerApplies.add(jobSeekerApply);
        }

        List<JobSeekerApply> jobSeekerApplyList = jobSeekerApplyRepository.findAll();
        System.out.println(jobSeekerApplyList);
        System.out.println(jobSeekerApplies);
        for (JobSeekerApply jobSeekerApply : jobSeekerApplies) {
            jobPost.add(jobSeekerApply.getJob());
        }

        model.addAttribute("jobPost", jobSeekerApplies);
        model.addAttribute("user", currentUserProfile);
        model.addAttribute("name", currentUserProfile);

        return "applied-jobs-rec";
    }
}
