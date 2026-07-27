package com.placement.controller;


import com.placement.entity.Student;
import com.placement.repository.StudentRepository;


import jakarta.servlet.http.HttpSession;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;



@Controller
public class OtpController {


    @Autowired
    private StudentRepository repository;




    // ================= VERIFY OTP =================

    @PostMapping("/verify-otp")
    public String verifyOTP(
            @RequestParam String email,
            @RequestParam String otp,
            HttpSession session,
            Model model) {



        String savedOtp =
                (String) session.getAttribute("otp");



        String savedEmail =
                (String) session.getAttribute("otpEmail");



        System.out.println(
                "Entered OTP : " + otp
        );


        System.out.println(
                "Saved OTP : " + savedOtp
        );



        if(savedEmail != null &&
                savedOtp != null &&
                email.equals(savedEmail) &&
                otp.trim().equals(savedOtp.trim())) {



            Student student =
                    repository.findByEmail(email);



            session.setAttribute(
                    "student",
                    student
            );



            // remove OTP after successful login

            session.removeAttribute("otp");

            session.removeAttribute("otpEmail");



            return "redirect:/dashboard";

        }




        model.addAttribute(
                "email",
                email
        );


        model.addAttribute(
                "message",
                "Invalid OTP"
        );



        return "verify-otp";
    }

}