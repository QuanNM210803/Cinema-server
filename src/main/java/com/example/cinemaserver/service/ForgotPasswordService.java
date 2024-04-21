package com.example.cinemaserver.service;

import com.example.cinemaserver.request.OTPVerificationRequest;
import com.example.cinemaserver.model.User;
import com.example.cinemaserver.repository.UserRepository;
import com.example.cinemaserver.response.OTPVerificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ForgotPasswordService {

    private final UserRepository userRepository;
    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;
    private static final int OTP_LENGTH = 6;
    public static final int OTP_EXPIRATION_MINUTES = 5;

    public static Boolean otpVerification(OTPVerificationRequest otpVerificationRequest) {
        return otpVerificationRequest.getSystemOTP().equals(otpVerificationRequest.getUserOTP().trim())
                && LocalDateTime.now().isBefore(otpVerificationRequest.getOTPExpirationTime());
    }

    public OTPVerificationResponse sendOtpByEmail(String email) {
        userRepository.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("Not found user."));
        LocalDateTime OTPExpirationTime=LocalDateTime.now().plusMinutes(OTP_EXPIRATION_MINUTES);
        String otp = generateOTP();
        String subject = "[LuxC] OTP Verification";
        String text = "Your OTP is: " + otp+". The otp code is valid for 5 minutes from the time it is sent.";
        sendEmail(email, subject, text);
        return new OTPVerificationResponse(otp,OTPExpirationTime);
    }

    public static String generateOTP() {
        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder(OTP_LENGTH);
        for (int i = 0; i < OTP_LENGTH; i++) {
            int randomDigit = random.nextInt(10);
            otp.append(randomDigit);
        }
        return otp.toString();
    }

    private void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        try {
            javaMailSender.send(message);
        }catch (MailException e){
            throw new RuntimeException("Failed to send email.");
        }
    }

    public User resetPassword(String email, String password) {
        Optional<?> userOptional=userRepository.findByEmail(email);
        if(userOptional.isPresent()){
            User user= (User) userOptional.get();
            user.setPassword(passwordEncoder.encode(password));
            return userRepository.save(user);
        }else {
            throw new UsernameNotFoundException("Not found user.");
        }
    }
}
