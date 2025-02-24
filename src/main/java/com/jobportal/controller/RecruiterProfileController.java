package com.jobportal.controller;

import com.jobportal.entity.JobPostActivity;
import com.jobportal.entity.JobSeekerApply;
import com.jobportal.entity.RecruiterProfile;
import com.jobportal.entity.Users;
import com.jobportal.repository.JobSeekerApplyRepository;
import com.jobportal.repository.UsersRepository;
import com.jobportal.services.JobPostActivityService;
import com.jobportal.services.JobSeekerApplyService;
import com.jobportal.services.RecruiterProfileService;
import com.jobportal.util.FileUploadUtil;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/recruiter-profile")
public class RecruiterProfileController {

    private final UsersRepository usersRepository;
    private final RecruiterProfileService recruiterProfileService;
    private final JobSeekerApplyService jobSeekerApplyService;
    private final JobPostActivityService jobPostActivityService;
    private final JobSeekerApplyRepository jobSeekerApplyRepository;


    public RecruiterProfileController(UsersRepository usersRepository, RecruiterProfileService recruiterProfileService,JobSeekerApplyService jobSeekerApplyService,JobPostActivityService jobPostActivityService,JobSeekerApplyRepository jobSeekerApplyRepository) {
        this.usersRepository = usersRepository;
        this.recruiterProfileService = recruiterProfileService;
        this.jobSeekerApplyService = jobSeekerApplyService;
        this.jobPostActivityService = jobPostActivityService;
        this.jobSeekerApplyRepository = jobSeekerApplyRepository;
    }

    @GetMapping("/")
    public String recruiterProfile(Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUsername = authentication.getName();
            Users users = usersRepository.findByEmail(currentUsername).orElseThrow(() -> new UsernameNotFoundException("Could not " + "found user"));
            Optional<RecruiterProfile> recruiterProfile = recruiterProfileService.getOne(users.getUserId());

            if (!recruiterProfile.isEmpty())
                model.addAttribute("profile", recruiterProfile.get());

        }

        return "recruiter_profile";
    }

    @PostMapping("/addNew")
    public String addNew(RecruiterProfile recruiterProfile, @RequestParam("image") MultipartFile multipartFile, Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUsername = authentication.getName();
            Users users = usersRepository.findByEmail(currentUsername).orElseThrow(() -> new UsernameNotFoundException("Could not " + "found user"));
            recruiterProfile.setUserId(users);
            recruiterProfile.setUserAccountId(users.getUserId());
        }
        model.addAttribute("profile", recruiterProfile);
        String fileName = "";
        if (!multipartFile.getOriginalFilename().equals("")) {
            fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            recruiterProfile.setProfilePhoto(fileName);
        }
        RecruiterProfile savedUser = recruiterProfileService.addNew(recruiterProfile);

        String uploadDir = "photos/recruiter/" + savedUser.getUserAccountId();
        try {
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return "redirect:/dashboard/";
    }
    
    @GetMapping("/job/update/{id}/{status}")
	public String getEditPage(Model model, RedirectAttributes attributes, @PathVariable("id") int id,@PathVariable("status") String status) {
    	 List<JobPostActivity> jobPost = new ArrayList<>();
    	 JobPostActivity jobDetails = jobPostActivityService.getOne(id);
         List<JobSeekerApply> jobSeekerApplyList = jobSeekerApplyService.getJobCandidates(jobDetails);
         for (JobSeekerApply jobSeekerApply : jobSeekerApplyList) {
        	 	jobSeekerApply.setStatus(status);
        	 	jobSeekerApplyRepository.save(jobSeekerApply);
             }
        jobSeekerApplyList = jobSeekerApplyRepository.findAll();
         for (JobSeekerApply jobSeekerApply : jobSeekerApplyList) {
             jobPost.add(jobSeekerApply.getJob());
         }

         model.addAttribute("jobPost", jobPost);
         
         return "redirect:/dashboard/";
	}
}








