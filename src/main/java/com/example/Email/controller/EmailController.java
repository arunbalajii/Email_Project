package com.example.Email.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.Email.exception.EmailNotFound;
import com.example.Email.service.EmailService;

import org.springframework.security.crypto.password.PasswordEncoder;

@RestController
public class EmailController {
	
	Logger logger = LoggerFactory.getLogger(EmailController.class);
	
	private EmailService Eserv;
	ResponseEntity<?> resentity;
	
	@Autowired
	PasswordEncoder encoder;
	
	@Autowired
	public EmailController(EmailService eserv)
	{
		this.Eserv=eserv;
	}
	
	@GetMapping("/greeting")
	public String HelloWorld()
	{
		return "Hello World";
	}
	
	/*@PostMapping(path="/sendEmailLink", consumes = "application/json")
	public ResponseEntity<?> sendEmailLink(@RequestBody Map<String, String> payload) throws Exception
	{
		logger.info(payload.toString());
		String finalCode ;
		
		resentity=new ResponseEntity<>(payload,HttpStatus.OK);
		
		for (String emailID:payload.values())
		{
			finalCode = Eserv.GenerateCode(emailID);
			Eserv.sendMail(emailID, finalCode);
		}
		
		return resentity;
	}*/
	
	//@CrossOrigin(origins = "http://localhost:3000")
	@CrossOrigin(origins = "*", maxAge = 3600)
	@PostMapping(path="/sendHTMLEmailLink", consumes = "application/json", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> sendHTMLEmailLink(@RequestBody Map<String, String> payload) throws Exception
	{
		logger.info(payload.toString());
		String finalCode="";
		String response = "";
		String mailId="";
		String action="";
		
		for (Map.Entry<String,String> eachvalue : payload.entrySet()) 
		{
            if(eachvalue.getKey()=="email")
            {
            	mailId=eachvalue.getValue();
            }
            else
            {
            	action= eachvalue.getValue();
            }
		}
		finalCode = Eserv.GenerateCode(mailId);
		response = Eserv.sendHTMLMail(mailId,finalCode,action);
		
		resentity=new ResponseEntity<>(response,HttpStatus.OK);
		
		/*if (response.contains("Mail sent"))
		{
			resentity=new ResponseEntity<>(response,HttpStatus.OK);
		}*/
		
		
		return resentity;
	}
	
	//@CrossOrigin(origins = "http://localhost:3000")
	@CrossOrigin(origins = "*", maxAge = 3600)
	@PostMapping(path="/validateLink", consumes = "application/json")
	public ResponseEntity<?> validateLink(@RequestBody Map<String, String> payload) throws Exception
	{
		logger.info("Payload: "+payload);
		String response = "";
		
		for (String validationCode:payload.values())
		{
			try
			{
				response = Eserv.validateEmailCode(validationCode);
			}
			catch (Exception e) {
	             logger.error("Error while Validating Code" + e.toString());
	             response = "Error while Validating Code. Please try generating a new code!";
	             resentity=new ResponseEntity<>(response,HttpStatus.OK);
	         }
			if (response.contains("Code validated") || response.contains("Code invalid"))
			{
				resentity=new ResponseEntity<>(response,HttpStatus.OK);
			}
			else
				resentity=new ResponseEntity<>(response,HttpStatus.OK);
		}
		
		return resentity;
	}
	
	//@CrossOrigin(origins = "http://localhost:3000")
	@CrossOrigin(origins = "*", maxAge = 3600)
	@PostMapping(path="/validatePassword", consumes = "application/json", produces = "application/json")
	public ResponseEntity<?> validatePassword(@RequestBody Map<String, String> payload) throws Exception
	{
		
		String mailId="";
		//String oldPassword="";
		String newPassword="";
		String confirmNewPassword="";
		String response="";
		
		try
		{
			for (Map.Entry<String,String> eachvalue : payload.entrySet()) 
			{
	            if(eachvalue.getKey()=="email")
	            {
	            	mailId=eachvalue.getValue();
	            }
	            /*else if(eachvalue.getKey()=="oldPassword")
	            {
	            	oldPassword=eachvalue.getValue();
	            }*/
	            else if(eachvalue.getKey()=="newPassword")
	            {
	            	newPassword=eachvalue.getValue();
	            }
	            else if(eachvalue.getKey()=="confirmNewPassword")
	            {
	            	confirmNewPassword=eachvalue.getValue();
	            }
			}
			
			if (!newPassword.equals(confirmNewPassword))
			{
				response = "New password and confirm new password doesn’t match";
				resentity=new ResponseEntity<>(response,HttpStatus.OK);
			}
			/*else if (newPassword.equals(oldPassword))
			{
				response = "New password and old password cannot be same";
				resentity=new ResponseEntity<>(response,HttpStatus.OK);
			}*/
			else
			{
				//response = Eserv.passwordValidation(mailId, newPassword);
				response = Eserv.passwordValidation(mailId, encoder.encode(newPassword));
				resentity=new ResponseEntity<>(response,HttpStatus.OK);
			}
		}
		catch (Exception e) 
		{
            logger.error("Error while updating password " + e.toString());
            response = "Password change failed. Please try again!";
            resentity=new ResponseEntity<>(response,HttpStatus.OK);
            //throw new Exception("Error while changing password");
            
		}
		
		return resentity;
	}
	
	//@CrossOrigin(origins = "http://localhost:3000")
	@CrossOrigin(origins = "*", maxAge = 3600)
	@DeleteMapping("delEmailId/{eid}")
	@ExceptionHandler(EmailNotFound.class)
	public ResponseEntity<?> delemp(@PathVariable("eid") String id) throws EmailNotFound
	{
		boolean resp = false;
		try 
		{
			resp = Eserv.deleteEmail(id);
			if (resp)
				resentity=new ResponseEntity<>("Email deleted",HttpStatus.OK);
			else
				resentity=new ResponseEntity<>("Email deletion failed",HttpStatus.OK);
		}
		catch(EmailNotFound e)
		{
			throw new EmailNotFound();
			
		}
		catch(Exception e)
		{
			//throw new EmployeeNotFoundException();
			logger.error("Error while deleting user: "+e.toString());
		}
		return resentity;
	}

}
