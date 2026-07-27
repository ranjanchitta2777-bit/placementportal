package com.placement.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class OTPService {

    private final Map<String, String> otpStorage = new HashMap<>();

    public String generateOTP(String email) {

        String otp = String.valueOf(100000 + new Random().nextInt(900000));

        otpStorage.put(email, otp);

        return otp;
    }

    public boolean verifyOTP(String email, String otp) {

        return otp.equals(otpStorage.get(email));
    }

    public void removeOTP(String email) {
        otpStorage.remove(email);
    }
}