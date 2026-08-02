package com.mrunali.fleet_mgmt_platform.service;

import com.mrunali.fleet_mgmt_platform.dto.request.InviteUserRequestDto;
import com.mrunali.fleet_mgmt_platform.dto.request.SetPasswordRequestDto;
import com.mrunali.fleet_mgmt_platform.dto.response.InviteUserResponseDto;
import com.mrunali.fleet_mgmt_platform.dto.response.UserResponseDto;
import com.mrunali.fleet_mgmt_platform.entity.Invitation;
import com.mrunali.fleet_mgmt_platform.entity.User;
import com.mrunali.fleet_mgmt_platform.entity.enums.Role;
import com.mrunali.fleet_mgmt_platform.entity.enums.UserStatus;
import com.mrunali.fleet_mgmt_platform.exception.InvalidTokenException;
import com.mrunali.fleet_mgmt_platform.exception.InvitationAlreadyAcceptedException;
import com.mrunali.fleet_mgmt_platform.exception.InvitationAlreadyExistsException;
import com.mrunali.fleet_mgmt_platform.exception.InvitationExpiredException;
import com.mrunali.fleet_mgmt_platform.exception.UserAlreadyExistsException;
import com.mrunali.fleet_mgmt_platform.repository.InvitationRepository;
import com.mrunali.fleet_mgmt_platform.repository.UserRepository;
import com.mrunali.fleet_mgmt_platform.service.impl.InvitationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class InvitationServiceImplTest {

    @Mock
    private InvitationRepository invitationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private InvitationServiceImpl invitationService;

    private InviteUserRequestDto inviteUserRequestDto;
    private Invitation invitation;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        inviteUserRequestDto = new InviteUserRequestDto();
        inviteUserRequestDto.setName("Test User");
        inviteUserRequestDto.setEmail("test@example.com");
        inviteUserRequestDto.setPhone("1234567890");
        inviteUserRequestDto.setRole(Role.DRIVER);

        invitation = new Invitation();
        invitation.setId(UUID.randomUUID());
        invitation.setEmail("test@example.com");
        invitation.setToken(UUID.randomUUID().toString());
        invitation.setExpiresAt(LocalDateTime.now().plusDays(1));
        invitation.setAccepted(false);
    }

    @Test
    public void testInviteUser_Success() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(invitationRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(invitationRepository.save(any(Invitation.class))).thenReturn(invitation);
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString());

        InviteUserResponseDto response = invitationService.inviteUser(inviteUserRequestDto);

        assertNotNull(response);
        assertEquals(invitation.getEmail(), response.getEmail());
        verify(invitationRepository, times(1)).save(any(Invitation.class));
        verify(emailService, times(1)).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    public void testInviteUser_UserAlreadyExists() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(new User()));

        assertThrows(UserAlreadyExistsException.class, () -> invitationService.inviteUser(inviteUserRequestDto));
        verify(invitationRepository, never()).save(any(Invitation.class));
    }

    @Test
    public void testInviteUser_InvitationAlreadyExists() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(invitationRepository.findByEmail(anyString())).thenReturn(Optional.of(new Invitation()));

        assertThrows(InvitationAlreadyExistsException.class, () -> invitationService.inviteUser(inviteUserRequestDto));
        verify(invitationRepository, never()).save(any(Invitation.class));
    }

    @Test
    public void testSetUserPassword_Success() {
        SetPasswordRequestDto setPasswordRequestDto = new SetPasswordRequestDto();
        setPasswordRequestDto.setToken("valid-token");
        setPasswordRequestDto.setPassword("password");
        when(invitationRepository.findByToken(anyString())).thenReturn(Optional.of(invitation));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        UserResponseDto response = invitationService.setUserPassword(setPasswordRequestDto);

        assertNotNull(response);
        assertEquals(invitation.getEmail(), response.getEmail());
        assertEquals(UserStatus.ACTIVE, response.getStatus());
        verify(userRepository, times(1)).save(any(User.class));
        verify(invitationRepository, times(1)).save(any(Invitation.class));
    }

    @Test
    public void testSetUserPassword_InvalidToken() {
        SetPasswordRequestDto setPasswordRequestDto = new SetPasswordRequestDto();
        setPasswordRequestDto.setToken("invalid-token");
        setPasswordRequestDto.setPassword("password");
        when(invitationRepository.findByToken(anyString())).thenReturn(Optional.empty());

        assertThrows(InvalidTokenException.class, () -> invitationService.setUserPassword(setPasswordRequestDto));
    }

    @Test
    public void testSetUserPassword_InvitationAlreadyAccepted() {
        invitation.setAccepted(true);
        SetPasswordRequestDto setPasswordRequestDto = new SetPasswordRequestDto();
        setPasswordRequestDto.setToken("valid-token");
        setPasswordRequestDto.setPassword("password");
        when(invitationRepository.findByToken(anyString())).thenReturn(Optional.of(invitation));

        assertThrows(InvitationAlreadyAcceptedException.class, () -> invitationService.setUserPassword(setPasswordRequestDto));
    }

    @Test
    public void testSetUserPassword_InvitationExpired() {
        invitation.setExpiresAt(LocalDateTime.now().minusDays(1));
        SetPasswordRequestDto setPasswordRequestDto = new SetPasswordRequestDto();
        setPasswordRequestDto.setToken("valid-token");
        setPasswordRequestDto.setPassword("password");
        when(invitationRepository.findByToken(anyString())).thenReturn(Optional.of(invitation));

        assertThrows(InvitationExpiredException.class, () -> invitationService.setUserPassword(setPasswordRequestDto));
    }
}