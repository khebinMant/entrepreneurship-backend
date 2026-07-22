package com.project.emprendia.user.service;

import com.project.emprendia.user.dto.ForgotPasswordRequest;
import com.project.emprendia.user.dto.ResetPasswordRequest;

public interface PasswordResetService {
    void forgotPassword(ForgotPasswordRequest request);
    void resetPassword(ResetPasswordRequest request);
}
