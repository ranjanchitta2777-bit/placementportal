package com.placement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.placement.repository.CompanyRepository;

@Controller
public class HomeController {

    @Autowired
    private CompanyRepository companyRepository;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/aptitude")
    public String aptitude() {
        return "aptitude";
    }

    @GetMapping("/coding")
    public String coding() {
        return "coding";
    }

    @GetMapping("/company-drives")
    public String companyDrives(Model model) {

        model.addAttribute("companies", companyRepository.findAll());

        return "company-drives";
    }
    @GetMapping("/about")
public String about() {
    return "about";
}
}