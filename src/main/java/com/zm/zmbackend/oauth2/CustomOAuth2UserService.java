package com.zm.zmbackend.oauth2;

import com.zm.zmbackend.entities.User;
import com.zm.zmbackend.oauth2.user.OAuth2UserInfo;
import com.zm.zmbackend.oauth2.user.OAuth2UserInfoFactory;
import com.zm.zmbackend.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepo userRepository;
    @Autowired
    public CustomOAuth2UserService(UserRepo userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            // Throwing an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        String registrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, oAuth2User.getAttributes());

        if(!StringUtils.hasText(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
        }

        Optional<User> userOptional = userRepository.findByEmail(oAuth2UserInfo.getEmail());
        User user;

        if(userOptional.isPresent()) {
            user = userOptional.get();

            // Check if user has a provider name and it doesn't match the current registration
            if(user.getProviderName() != null && !user.getProviderName().equals(registrationId)) {
                throw new OAuth2AuthenticationException("You're signed up with " + user.getProviderName() + 
                    ". Please use your " + user.getProviderName() + " account to login.");
            }

            user = updateExistingUser(user, oAuth2UserInfo);
        } else {
            user = registerNewUser(oAuth2UserRequest, oAuth2UserInfo);
        }

        return UserPrincipal.create(user, oAuth2User.getAttributes());
    }

    private User registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) {
        User user = new User();

        user.setProviderName(oAuth2UserRequest.getClientRegistration().getRegistrationId());
        user.setProviderId(oAuth2UserInfo.getId());
        user.setFirstName(oAuth2UserInfo.getName().split(" ")[0]);
        user.setLastName(oAuth2UserInfo.getName().contains(" ") ? 
                        oAuth2UserInfo.getName().substring(oAuth2UserInfo.getName().indexOf(" ") + 1) : "");
        user.setEmail(oAuth2UserInfo.getEmail());
        user.setPicture(oAuth2UserInfo.getImageUrl());
        user.setEmailVerified(true);
        user.setPhoneVerified(false);
        user.setPassword(""); // OAuth2 users don't have a password
        user.setAddress(""); // Default empty address
        user.setBirthday(LocalDate.now()); // Default to current date, should be updated by user
        user.setPhoneNumber(""); // Default empty phone number
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());

        return userRepository.save(user);
    }

    private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {
        existingUser.setFirstName(oAuth2UserInfo.getName().split(" ")[0]);
        existingUser.setLastName(oAuth2UserInfo.getName().contains(" ") ? 
                               oAuth2UserInfo.getName().substring(oAuth2UserInfo.getName().indexOf(" ") + 1) : "");
        existingUser.setPicture(oAuth2UserInfo.getImageUrl());
        existingUser.setUpdatedAt(Instant.now());

        return userRepository.save(existingUser);
    }
}
