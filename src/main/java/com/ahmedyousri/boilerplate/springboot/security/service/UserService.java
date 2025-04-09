package com.ahmedyousri.boilerplate.springboot.security.service;

import com.ahmedyousri.boilerplate.springboot.model.User;
import com.ahmedyousri.boilerplate.springboot.security.dto.AuthenticatedUserDto;
import com.ahmedyousri.boilerplate.springboot.security.dto.RegistrationRequest;
import com.ahmedyousri.boilerplate.springboot.security.dto.RegistrationResponse;

/**
 * Created on Ağustos, 2020
 *
 * @author Faruk
 */
public interface UserService {

	User findByUsername(String username);

	RegistrationResponse registration(RegistrationRequest registrationRequest);

	AuthenticatedUserDto findAuthenticatedUserByUsername(String username);

}
