package com.ahmedyousri.boilerplate.springboot.exceptions;

import lombok.Getter;

/**
 * Created on Ağustos, 2020
 *
 * @author Faruk
 */
@Getter
public class RegistrationException extends RuntimeException {

	private final String errorMessage;

	public RegistrationException(String errorMessage) {
		super(errorMessage);
		this.errorMessage = errorMessage;
	}

}
