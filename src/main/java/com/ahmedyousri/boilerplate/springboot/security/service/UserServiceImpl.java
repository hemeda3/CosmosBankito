package com.ahmedyousri.boilerplate.springboot.security.service;

import com.ahmedyousri.boilerplate.springboot.service.UserValidationService;
import com.ahmedyousri.boilerplate.springboot.model.User;
import com.ahmedyousri.boilerplate.springboot.model.UserRole;
import com.ahmedyousri.boilerplate.springboot.security.dto.AuthenticatedUserDto;
import com.ahmedyousri.boilerplate.springboot.security.dto.RegistrationRequest;
import com.ahmedyousri.boilerplate.springboot.security.dto.RegistrationResponse;
import com.ahmedyousri.boilerplate.springboot.security.mapper.UserMapper;
import com.ahmedyousri.boilerplate.springboot.utils.GeneralMessageAccessor;
import com.ahmedyousri.boilerplate.springboot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Created on Ağustos, 2020
 *
 * @author Faruk
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private static final String REGISTRATION_SUCCESSFUL = "registration_successful";

	private final UserRepository userRepository;

	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	private final UserValidationService userValidationService;

	private final GeneralMessageAccessor generalMessageAccessor;

	@Override
	public User findByUsername(String username) {

		return userRepository.findByUsername(username);
	}

	@Override
	public RegistrationResponse registration(RegistrationRequest registrationRequest) {

		userValidationService.validateUser(registrationRequest);

		final User user = UserMapper.INSTANCE.convertToUser(registrationRequest);
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		user.setUserRole(UserRole.USER);

		userRepository.save(user);

		final String username = registrationRequest.getUsername();
		final String registrationSuccessMessage = generalMessageAccessor.getMessage(null, REGISTRATION_SUCCESSFUL, username);

		log.info("{} registered successfully!", username);

		return new RegistrationResponse(registrationSuccessMessage);
	}

	@Override
	public AuthenticatedUserDto findAuthenticatedUserByUsername(String username) {

		final User user = findByUsername(username);

		return UserMapper.INSTANCE.convertToAuthenticatedUserDto(user);
	}
}
