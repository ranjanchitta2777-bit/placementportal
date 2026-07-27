package com.placement.controller;

import com.placement.entity.Student;
import com.placement.repository.ApplicationRepository;
import com.placement.repository.CompanyRepository;
import com.placement.repository.StudentRepository;
import com.placement.service.EmailService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import com.placement.entity.Company;
import com.placement.entity.Application;
import com.placement.repository.CompanyRepository;
import com.placement.repository.ApplicationRepository;
import java.util.List;
import com.placement.entity.Application;

@Controller
public class StudentController {


    @Autowired
    private StudentRepository repository;


    @Autowired
    private EmailService emailService;


    @Autowired
private CompanyRepository companyRepository;

@Autowired
private ApplicationRepository applicationRepository;


    // ================= REGISTER =================

    @GetMapping("/register")
    public String registerPage(Model model) {

        model.addAttribute("student", new Student());

        return "register";
    }



    @PostMapping("/register")
    public String saveStudent(
            @ModelAttribute Student student,
            Model model) {


        repository.save(student);


        model.addAttribute(
                "success",
                "Registration Successful!"
        );


        model.addAttribute(
                "student",
                new Student()
        );


        return "redirect:/login";
    }





    // ================= LOGIN PAGE =================

    @GetMapping("/login")
    public String loginPage() {

        return "login";
    }





    // ================= PASSWORD LOGIN =================

    @PostMapping("/login/password")
    public String passwordLogin(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            Model model) {


        Student student =
                repository.findByEmail(email);



        if(student != null &&
                student.getPassword().equals(password)) {


            session.setAttribute(
                    "student",
                    student
            );


            return "redirect:/dashboard";
        }



        model.addAttribute(
                "message",
                "Invalid Email or Password"
        );


        return "login";
    }





    // ================= SEND OTP =================

    @PostMapping("/login/otp")
    public String sendOtp(
            @RequestParam String email,
            HttpSession session,
            Model model) {


        Student student =
                repository.findByEmail(email);



        if(student == null) {


            model.addAttribute(
                    "message",
                    "Email not registered"
            );


            return "login";
        }




        String otp =
                String.valueOf(
                        (int)(Math.random() * 900000) + 100000
                );



        // Store OTP

        session.setAttribute(
                "otp",
                otp
        );


        session.setAttribute(
                "otpEmail",
                email
        );



        System.out.println(
                "Generated OTP : " + otp
        );



        emailService.sendOTP(
                email,
                otp
        );



        model.addAttribute(
                "email",
                email
        );



        return "verify-otp";
    }






    // ================= DASHBOARD =================

@GetMapping("/dashboard")
public String dashboard(HttpSession session, Model model) {

    Student student = (Student) session.getAttribute("student");

    if (student == null) {
        return "redirect:/login";
    }

    // Refresh student data
    student = repository.findByEmail(student.getEmail());

    session.setAttribute("student", student);

    model.addAttribute("student", student);

    // All companies
    model.addAttribute("companies", companyRepository.findAll());

    // Student applications
    List<Application> applications =
            applicationRepository.findByStudentEmail(student.getEmail());

    model.addAttribute("applications", applications);

    // Dashboard Statistics
    long applied = applications.size();

    long selected = applications.stream()
            .filter(a -> "Selected".equals(a.getStatus()))
            .count();

    long rejected = applications.stream()
            .filter(a -> "Rejected".equals(a.getStatus()))
            .count();

    long pending = applications.stream()
            .filter(a -> "Pending".equals(a.getStatus()))
            .count();

    model.addAttribute("applied", applied);
    model.addAttribute("selected", selected);
    model.addAttribute("rejected", rejected);
    model.addAttribute("pending", pending);
    int profileCompletion = 0;

if(student.getName() != null && !student.getName().isBlank()) profileCompletion += 10;
if(student.getEmail() != null && !student.getEmail().isBlank()) profileCompletion += 10;
if(student.getPhone() != null && !student.getPhone().isBlank()) profileCompletion += 10;
if(student.getCollege() != null && !student.getCollege().isBlank()) profileCompletion += 10;
if(student.getBranch() != null && !student.getBranch().isBlank()) profileCompletion += 10;
if(student.getSkills() != null && !student.getSkills().isBlank()) profileCompletion += 10;
if(student.getGithub() != null && !student.getGithub().isBlank()) profileCompletion += 10;
if(student.getLinkedin() != null && !student.getLinkedin().isBlank()) profileCompletion += 10;
if(student.getResume() != null && !student.getResume().isBlank()) profileCompletion += 10;
if(student.getPhoto() != null && !student.getPhoto().isBlank()) profileCompletion += 10;

model.addAttribute("profileCompletion", profileCompletion);

    return "dashboard";
}
    @GetMapping("/edit-profile")
public String editProfile(HttpSession session,
                          Model model) {

    Student student =
            (Student) session.getAttribute("student");

    if(student == null) {

        return "redirect:/login";
    }

    model.addAttribute("student", student);

    return "edit-profile";
}

@PostMapping("/update-profile")
public String updateProfile(@ModelAttribute Student student,
                            HttpSession session) {

    Student existing = repository.findById(student.getId()).orElse(null);

    if (existing == null) {
        return "redirect:/dashboard";
    }

    existing.setName(student.getName());
    existing.setPhone(student.getPhone());
    existing.setCollege(student.getCollege());
    existing.setBranch(student.getBranch());
    existing.setCgpa(student.getCgpa());

    existing.setSkills(student.getSkills());
    existing.setGithub(student.getGithub());
    existing.setLinkedin(student.getLinkedin());
    existing.setLeetcode(student.getLeetcode());

    repository.save(existing);

    session.setAttribute("student", existing);

    return "redirect:/dashboard";
}


    // ================= LOGOUT =================

    @GetMapping("/logout")
    public String logout(
            HttpSession session) {


        session.invalidate();


        return "redirect:/login";
    }

   @PostMapping("/uploadResume")
public String uploadResume(@RequestParam("file") MultipartFile file,
                           HttpSession session) {

    try {

        Student student = (Student) session.getAttribute("student");

        if (student == null) {
            return "redirect:/login";
        }

        student = repository.findByEmail(student.getEmail());

        String uploadDir = "uploads/";

        Files.createDirectories(Paths.get(uploadDir));

        // Delete old resume
        if (student.getResume() != null && !student.getResume().isBlank()) {

            Path oldResume = Paths.get(uploadDir + student.getResume());

            Files.deleteIfExists(oldResume);
        }

        // Save new resume
        String fileName = student.getId() + "_" + file.getOriginalFilename();

        Path newResume = Paths.get(uploadDir + fileName);

        Files.copy(file.getInputStream(),
                newResume,
                StandardCopyOption.REPLACE_EXISTING);

        student.setResume(fileName);

        repository.save(student);

        session.setAttribute("student", student);

    } catch (Exception e) {
        e.printStackTrace();
    }

    return "redirect:/dashboard";
}
@PostMapping("/apply/{id}")
public String apply(@PathVariable Long id,
                    HttpSession session,
                    org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {

    Student student = (Student) session.getAttribute("student");

    if (student == null) {
        return "redirect:/login";
    }

    Company company = companyRepository.findById(id).orElse(null);

    if (company == null) {
        return "redirect:/dashboard";
    }

    // Already Applied
    boolean alreadyApplied =
            applicationRepository.existsByStudentEmailAndCompanyName(
                    student.getEmail(),
                    company.getCompanyName());

    if (alreadyApplied) {

        redirectAttributes.addFlashAttribute(
                "error",
                "You have already applied for this company.");

        return "redirect:/dashboard";
    }

    // CGPA Eligibility
    if (student.getCgpa() < company.getMinCgpa()) {

        redirectAttributes.addFlashAttribute(
                "error",
                "Not Eligible! Minimum CGPA required is "
                        + company.getMinCgpa());

        return "redirect:/dashboard";
    }

    // Branch Eligibility
    if (!student.getBranch().equalsIgnoreCase(company.getEligibleBranch())) {

        redirectAttributes.addFlashAttribute(
                "error",
                "Only " + company.getEligibleBranch()
                        + " students can apply.");

        return "redirect:/dashboard";
    }

    // Apply
    Application application = new Application();

    application.setStudentName(student.getName());
    application.setStudentEmail(student.getEmail());
    application.setCompanyName(company.getCompanyName());
    application.setRole(company.getRole());
    application.setStatus("Pending");

    applicationRepository.save(application);

    redirectAttributes.addFlashAttribute(
            "success",
            "Application submitted successfully!");

    return "redirect:/dashboard";
}
// ================= FORGOT PASSWORD PAGE =================

@GetMapping("/forgot-password")
public String forgotPasswordPage() {
    return "forgot-password";
}

// ================= SEND OTP FOR PASSWORD RESET =================

@PostMapping("/forgot-password")
public String forgotPassword(@RequestParam String email,
                             HttpSession session,
                             Model model) {

    Student student = repository.findByEmail(email);

    if (student == null) {

        model.addAttribute("error", "Email is not registered.");

        return "forgot-password";
    }

    String otp = String.valueOf(
            (int)(Math.random() * 900000) + 100000
    );

    session.setAttribute("resetOtp", otp);
    session.setAttribute("resetEmail", email);

    emailService.sendOTP(email, otp);

    model.addAttribute("email", email);

    return "reset-otp";
}

// ================= VERIFY RESET OTP =================

@PostMapping("/verify-reset-otp")
public String verifyResetOtp(@RequestParam String otp,
                             HttpSession session,
                             Model model) {

    String sessionOtp = (String) session.getAttribute("resetOtp");

    if (sessionOtp != null && sessionOtp.equals(otp)) {

        return "new-password";
    }

    model.addAttribute("error", "Invalid OTP");

    model.addAttribute("email",
            session.getAttribute("resetEmail"));

    return "reset-otp";
}
// ================= RESET PASSWORD =================

@PostMapping("/reset-password")
public String resetPassword(@RequestParam String password,
                            HttpSession session,
                            Model model) {

    String email = (String) session.getAttribute("resetEmail");

    Student student = repository.findByEmail(email);

    if (student == null) {

        return "redirect:/login";
    }

    student.setPassword(password);

    repository.save(student);

    session.removeAttribute("resetOtp");
    session.removeAttribute("resetEmail");

    model.addAttribute("message",
            "Password updated successfully. Please login.");

    return "login";
}

@GetMapping("/deletePhoto")
public String deletePhoto(HttpSession session) {

    try {

        Student student = (Student) session.getAttribute("student");

        if (student == null) {
            return "redirect:/login";
        }

        student = repository.findByEmail(student.getEmail());

        if (student.getPhoto() != null && !student.getPhoto().isBlank()) {

            Path path = Paths.get("uploads/photos/" + student.getPhoto());

            Files.deleteIfExists(path);

            student.setPhoto(null);

            repository.save(student);

            session.setAttribute("student", student);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return "redirect:/dashboard";
}

@GetMapping("/deleteResume")
public String deleteResume(HttpSession session) {

    try {

        Student student = (Student) session.getAttribute("student");

        if (student == null) {
            return "redirect:/login";
        }

        student = repository.findByEmail(student.getEmail());

        if (student.getResume() != null && !student.getResume().isBlank()) {

            Path path = Paths.get("uploads/" + student.getResume());

            Files.deleteIfExists(path);

            student.setResume(null);

            repository.save(student);

            session.setAttribute("student", student);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return "redirect:/dashboard";
}



}