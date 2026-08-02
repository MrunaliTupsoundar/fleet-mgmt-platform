package com.mrunali.fleet_mgmt_platform.service.impl;

import com.mrunali.fleet_mgmt_platform.dto.request.InviteUserRequestDto;
import com.mrunali.fleet_mgmt_platform.dto.request.SetPasswordRequestDto;
import com.mrunali.fleet_mgmt_platform.dto.response.InviteUserResponseDto;
import com.mrunali.fleet_mgmt_platform.dto.response.UserResponseDto;
import com.mrunali.fleet_mgmt_platform.entity.Invitation;
import com.mrunali.fleet_mgmt_platform.entity.User;
import com.mrunali.fleet_mgmt_platform.entity.enums.UserStatus;
import com.mrunali.fleet_mgmt_platform.exception.InvalidTokenException;
import com.mrunali.fleet_mgmt_platform.exception.InvitationAlreadyExistsException;
import com.mrunali.fleet_mgmt_platform.exception.UserAlreadyExistsException;
import com.mrunali.fleet_mgmt_platform.exception.InvitationAlreadyAcceptedException;
import com.mrunali.fleet_mgmt_platform.exception.InvitationExpiredException;
import com.mrunali.fleet_mgmt_platform.repository.InvitationRepository;
import com.mrunali.fleet_mgmt_platform.repository.UserRepository;
import com.mrunali.fleet_mgmt_platform.service.EmailService;
import com.mrunali.fleet_mgmt_platform.service.InvitationService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class InvitationServiceImpl implements InvitationService {

    private final InvitationRepository invitationRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public InvitationServiceImpl(InvitationRepository invitationRepository, UserRepository userRepository, EmailService emailService, PasswordEncoder passwordEncoder) {
        this.invitationRepository = invitationRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public InviteUserResponseDto inviteUser(InviteUserRequestDto requestDto) {
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("User with this email already exists");
        }

        if (invitationRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new InvitationAlreadyExistsException("Invitation with this email already exists");
        }

        String token = UUID.randomUUID().toString();
        Invitation invitation = new Invitation();
        invitation.setName(requestDto.getName());
        invitation.setEmail(requestDto.getEmail());
        invitation.setPhone(requestDto.getPhone());
        invitation.setRole(requestDto.getRole());
        invitation.setToken(token);
        invitation.setExpiresAt(LocalDateTime.now().plusDays(1));
        invitation.setAccepted(false);
        invitation.setStatus(UserStatus.INVITED);

        Invitation savedInvitation = invitationRepository.save(invitation);

        sendInvitationEmail(savedInvitation.getEmail(), savedInvitation.getToken());

        return new InviteUserResponseDto(savedInvitation.getId(), savedInvitation.getEmail(), savedInvitation.getToken());
    }

    @Override
    @Transactional
    public UserResponseDto setUserPassword(SetPasswordRequestDto requestDto) {
        Invitation invitation = invitationRepository.findByToken(requestDto.getToken())
                .orElseThrow(() -> new InvalidTokenException("Invalid token"));

        if (invitation.isAccepted()) {
            throw new InvitationAlreadyAcceptedException("Invitation already accepted");
        }

        if (invitation.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvitationExpiredException("Invitation expired");
        }

        User user = new User();
        user.setName(invitation.getName());
        user.setEmail(invitation.getEmail());
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        user.setPhone(invitation.getPhone());
        user.setRole(invitation.getRole());
        user.setStatus(UserStatus.ACTIVE);

        User savedUser = userRepository.save(user);

        invitation.setAccepted(true);
        invitation.setStatus(UserStatus.ACTIVE);
        invitationRepository.save(invitation);

        return new UserResponseDto(savedUser.getId(), savedUser.getName(), savedUser.getEmail(), savedUser.getPhone(), savedUser.getRole(), savedUser.getStatus(), savedUser.getCreatedAt());
    }

    private void sendInvitationEmail(String email, String token) {
        String subject = "Invitation to Join Fleet Management Platform";
        String text = "You have been invited to join the Fleet Management Platform. Please click the link below to set your password:\n\n"
                + "http://localhost:8080/api/users/set-password?token=" + token;

        emailService.sendEmail(email, subject, text);
    }
}