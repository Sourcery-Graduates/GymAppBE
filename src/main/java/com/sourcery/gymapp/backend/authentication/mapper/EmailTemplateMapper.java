package com.sourcery.gymapp.backend.authentication.mapper;

import com.sourcery.gymapp.backend.authentication.model.User;
import com.sourcery.gymapp.backend.events.EmailSendEvent;
import com.sourcery.gymapp.backend.events.LastUserWorkoutEvent;
import org.springframework.stereotype.Component;

@Component
public class EmailTemplateMapper {

    public EmailSendEvent toRegisterVerificationEmail(User user, String verificationUrl) {
        String subject = "Email verification";
        String senderName = "User Registration Portal Service";
        String mailContent = """
                <p> Hi, %s </p>
                <p>Thank you for registering with us.</p>
                <p>Please, follow the link below to complete your registration. </p>
                <a href="%s">Verify your email to activate your account </a>
                <p> Thank you <br> Users Registration Portal Service</p>
                <p style="font-style: italic; text-decoration: underline;"> This is automated message, please dont reply to it</p>
                """.formatted(user.getUsername(), verificationUrl);

        return new EmailSendEvent(subject, senderName, mailContent, user.getEmail(), 0);
    }

    public EmailSendEvent toPasswordResetEmail(User user, String resetPasswordUrl) {
        String subject = "Password Reset";
        String senderName = "Password Reset Portal Service";
        String mailContent = """
                <p> Hi, %s </p>
                <p>We received a request to reset your password. Click the link below to set a new password:</p>
                <a href="%s">Reset Password</a>
                <p>If you did not request this, please ignore this email — your password will remain unchanged.</p>
                <p>For security reasons, this link will expire in 24 hours.</p>
                <p>All previous emails and their links about password reset are invalidated.</p>
                <p>Best regards <br> Gym app <br> Password Reset Portal Service</p>
                <p style="font-style: italic; text-decoration: underline;"> This is automated message, please dont reply to it</p>
                """.formatted(user.getUsername(), resetPasswordUrl);

        return new EmailSendEvent(subject, senderName, mailContent, user.getEmail(), 0);
    }

    public EmailSendEvent toLastWorkoutReminderEmail(LastUserWorkoutEvent event, User user) {
        String subject = "It's been a while";
        String senderName = "Gym App Portal Service";
        String mailContent = """
                <p> Hi, %s </p>
                <p>We have noticed that you haven't worked out with us for %s days.</p>
                <p>It's known that you will start to lose your hard-earned muscles <br> after 2 weeks of not working out.</p>
                <p>We encourage you to come back to our app and to keep going!</p>
                <p>Best regards <br> Gym app <br> Portal Service</p>
                <p style="font-style: italic; text-decoration: underline;"> This is automated message, please dont reply to it</p>
                """.formatted(user.getUsername(), event.daysSinceLastWorkout());

        return new EmailSendEvent(subject, senderName, mailContent, user.getEmail(), 0);
    }
}
