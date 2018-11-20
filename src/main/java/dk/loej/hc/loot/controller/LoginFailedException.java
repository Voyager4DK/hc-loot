package dk.loej.hc.loot.controller;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class LoginFailedException extends Exception {
	private static final long serialVersionUID = 5977979923756680157L;

	public LoginFailedException(String message) {
		super(message);		
	}
}
