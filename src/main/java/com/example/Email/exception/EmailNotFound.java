package com.example.Email.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(code=HttpStatus.NOT_FOUND, reason="Email Not Found")
public class EmailNotFound extends Exception{

	}