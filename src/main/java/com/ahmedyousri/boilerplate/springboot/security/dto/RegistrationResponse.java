package com.ahmedyousri.boilerplate.springboot.security.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created on Ağustos, 2020
 *
 * @author Faruk
 */
@Getter
@Setter
@NoArgsConstructor
public class RegistrationResponse {

	private String message;

	public RegistrationResponse(String message) {
		this.message = message;
	}

}
