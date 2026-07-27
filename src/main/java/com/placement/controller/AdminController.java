package com.placement.controller;

import com.placement.entity.Admin;
import com.placement.entity.Application;
import com.placement.entity.Company;
import com.placement.repository.AdminRepository;
import com.placement.repository.ApplicationRepository;
import com.placement.repository.CompanyRepository;
import com.placement.repository.StudentRepository;
import com.placement.service.EmailService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AdminController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    // ================= ADMIN LOGIN PAGE =================

    @GetMapping("/admin")
    public String adminLoginPage() {
        return "admin-login";
    }

    // ================= ADMIN LOGIN =================

    @PostMapping("/admin/login")
    public String adminLogin(@RequestParam String username,
                             @RequestParam String password,
                             HttpSession session,
                             Model model) {

        Admin admin = adminRepository.findByUsername(username);

        if (admin != null && admin.getPassword().equals(password)) {

            session.setAttribute("admin", admin);

            return "redirect:/admin/dashboard";
        }

        model.addAttribute("error", "Invalid Username or Password");

        return "admin-login";
    }

    // ================= DASHBOARD =================

    @GetMapping("/admin/dashboard")
    public String adminDashboard(HttpSession session, Model model) {

        if (session.getAttribute("admin") == null) {
            return "redirect:/admin";
        }

        model.addAttribute("students", studentRepository.findAll());

        model.addAttribute("studentCount", studentRepository.count());

        model.addAttribute("companyCount", companyRepository.count());

        model.addAttribute("applicationCount", applicationRepository.count());

        long selectedCount = applicationRepository.findAll()
                .stream()
                .filter(a -> "Selected".equals(a.getStatus()))
                .count();

        model.addAttribute("selectedCount", selectedCount);
        model.addAttribute("admin", session.getAttribute("admin"));

        return "admin-dashboard";
    }

    // ================= ADD COMPANY =================

    @GetMapping("/admin/add-company")
    public String addCompanyPage(Model model,
                                 HttpSession session) {

        if (session.getAttribute("admin") == null) {
            return "redirect:/admin";
        }

        model.addAttribute("company", new Company());

        return "add-company";
    }

    @PostMapping("/admin/add-company")
    public String saveCompany(@ModelAttribute Company company,
                              HttpSession session) {

        if (session.getAttribute("admin") == null) {
            return "redirect:/admin";
        }

        companyRepository.save(company);

        return "redirect:/admin/companies";
    }

    // ================= COMPANY LIST =================

    @GetMapping("/admin/companies")
    public String companyList(Model model,
                              HttpSession session) {

        if (session.getAttribute("admin") == null) {
            return "redirect:/admin";
        }

        model.addAttribute("companies", companyRepository.findAll());

        return "company-list";
    }

    // ================= EDIT COMPANY =================

    @GetMapping("/admin/edit-company/{id}")
    public String editCompany(@PathVariable Long id,
                              Model model,
                              HttpSession session) {

        if (session.getAttribute("admin") == null) {
            return "redirect:/admin";
        }

        Company company = companyRepository.findById(id).orElse(null);

        if (company == null) {
            return "redirect:/admin/companies";
        }

        model.addAttribute("company", company);

        return "edit-company";
    }

    // ================= UPDATE COMPANY =================

    @PostMapping("/admin/update-company")
    public String updateCompany(@ModelAttribute Company company,
                                HttpSession session) {

        if (session.getAttribute("admin") == null) {
            return "redirect:/admin";
        }

        companyRepository.save(company);

        return "redirect:/admin/companies";
    }

    // ================= DELETE COMPANY =================

    @GetMapping("/admin/delete-company/{id}")
    public String deleteCompany(@PathVariable Long id,
                                HttpSession session) {

        if (session.getAttribute("admin") == null) {
            return "redirect:/admin";
        }

        companyRepository.deleteById(id);

        return "redirect:/admin/companies";
    }

    // ================= APPLICATION LIST =================

    @GetMapping("/admin/applications")
    public String viewApplications(Model model,
                                   HttpSession session) {

        if (session.getAttribute("admin") == null) {
            return "redirect:/admin";
        }

        model.addAttribute("applications",
                applicationRepository.findAll());

        return "application-list";
    }

    // ================= SELECT STUDENT =================

    @PostMapping("/admin/select/{id}")
    public String selectStudent(@PathVariable Long id,
                                HttpSession session) {

        if (session.getAttribute("admin") == null) {
            return "redirect:/admin";
        }

        Application application =
                applicationRepository.findById(id).orElse(null);

        if (application != null) {

            application.setStatus("Selected");

            applicationRepository.save(application);

            emailService.sendSelectionEmail(
                    application.getStudentEmail(),
                    application.getStudentName(),
                    application.getCompanyName(),
                    application.getRole()
            );
        }

        return "redirect:/admin/applications";
    }

    // ================= REJECT STUDENT =================

    @PostMapping("/admin/reject/{id}")
    public String rejectStudent(@PathVariable Long id,
                                HttpSession session) {

        if (session.getAttribute("admin") == null) {
            return "redirect:/admin";
        }

        Application application =
                applicationRepository.findById(id).orElse(null);

        if (application != null) {

            application.setStatus("Rejected");

            applicationRepository.save(application);

            emailService.sendRejectionEmail(
                    application.getStudentEmail(),
                    application.getStudentName(),
                    application.getCompanyName(),
                    application.getRole()
            );
        }

        return "redirect:/admin/applications";
    }
    @GetMapping("/admin/admin-list")
public String adminList(HttpSession session, Model model) {

    Admin admin = (Admin) session.getAttribute("admin");

    if (admin == null) {
        return "redirect:/admin";
    }

    if (!admin.getRole().equals("SUPER_ADMIN")) {
        return "redirect:/admin/dashboard";
    }

    model.addAttribute("admins", adminRepository.findAll());

    return "admin-list";
}
@GetMapping("/admin/delete-admin/{id}")
public String deleteAdmin(@PathVariable Long id,
                          HttpSession session) {

    Admin currentAdmin = (Admin) session.getAttribute("admin");

    if(currentAdmin == null){
        return "redirect:/admin";
    }

    if(!currentAdmin.getRole().equals("SUPER_ADMIN")){
        return "redirect:/admin/dashboard";
    }

    Admin admin = adminRepository.findById(id).orElse(null);

    if(admin != null && !"SUPER_ADMIN".equals(admin.getRole())){
        adminRepository.delete(admin);
    }

    return "redirect:/admin/admin-list";
}
@GetMapping("/admin/add-admin")
public String addAdminPage(HttpSession session, Model model) {

    Admin admin = (Admin) session.getAttribute("admin");

    if(admin == null){
        return "redirect:/admin";
    }

    if(!"SUPER_ADMIN".equals(admin.getRole())){
        return "redirect:/admin/dashboard";
    }

    model.addAttribute("admin", new Admin());

    return "add-admin";
}
@PostMapping("/admin/add-admin")
public String saveAdmin(@ModelAttribute Admin newAdmin,
                        HttpSession session) {

    Admin admin = (Admin) session.getAttribute("admin");

    if(admin == null){
        return "redirect:/admin";
    }

    if(!"SUPER_ADMIN".equals(admin.getRole())){
        return "redirect:/admin/dashboard";
    }

    newAdmin.setRole("ADMIN");

    adminRepository.save(newAdmin);

    return "redirect:/admin/admin-list";
}
@GetMapping("/admin/delete-student/{id}")
public String deleteStudent(@PathVariable Long id,
                            HttpSession session) {

    Admin admin = (Admin) session.getAttribute("admin");

    if (admin == null) {
        return "redirect:/admin";
    }

    studentRepository.deleteById(id);

    return "redirect:/admin/dashboard";
}
    
}