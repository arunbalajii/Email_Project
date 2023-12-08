package com.example.Email.service;

import java.io.File;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import jakarta.mail.internet.MimeMessage;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.Email.exception.EmailNotFound;
import com.example.Email.model.UserModel;
import com.example.Email.repository.UserRepository;



@Service
@EnableMongoRepositories
public class EmailService implements UserDetailsService {
	
	static Logger logger = LoggerFactory.getLogger(EmailService.class);
	
	UserRepository userRepo;
	
    @Autowired
	private JavaMailSender javaMailSender;
    public EmailService(UserRepository ur) {
        this.userRepo = ur;
    }
    MongoTemplate mongoTemplate;
	
	@Value("${email.html.content.password}")
	String contentP;
	
	@Value("${email.html.subject.password}")
	String subjectP;
	
	@Value("${email.html.subject.passwordSuccess}")
	String subjectPV;
	
	@Value("${email.html.content.password.verified}")
	String contentPV;
	
	@Value("${email.html.content.validate}")
	String contentV;
	
	@Value("${email.html.subject.validate}")
	String subjectV;
	
	@Value("${email.html.subject.validateSuccess}")
	String subjectEV;

	@Value("${email.html.host.validate}")
	String linkpartV;
	
	@Value("${email.html.host.password}")
	String linkpartP;
	
	@Value("${email.html.host.delete}")
	String linkpartD;
	
	@Value("${email.encryption.key}")
	String secKey;
	
	@Value("${email.encryption.substring}")
	String encryptionPart;
	
	@Value("${email.html.logo.path}")
	String logoPath;
	
	@Value("${email.html.verified.path}")
	String verifiedPath;
	
	@Value("${email.from.address}")
	String fromAddress;
    
	private static SecretKeySpec secretKey;
    private static byte[] key;
    private static final String ALGORITHM = "AES";
    
    public void setMailSender(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    } 
    
    /*getting row based on email, updating validate field for succesful validation*/
    public String getUserByEmail(String email) throws EmailNotFound{
    	 
    	 String DBresp = "False";
         //System.out.println("Getting user by email: " + email);
         logger.info("Getting user by email: " + email);
         UserModel user = userRepo.findEmail(email);
         logger.info(user.toString());
         user.setValidated("YES");
         //System.out.println("User: "+user.toString());
         userRepo.save(user);
         //System.out.println(user.getValidated());
         logger.info(user.getValidated());
         if(user.getValidated().equalsIgnoreCase("YES"))
         {
        	 //System.out.println("Update success message in react page");
        	 logger.info("Update success message in react page");
        	 DBresp = "True";
         }
         
         return DBresp;
     }
    
    public String GenerateCode(String Email) throws Exception
    {
        //final String secKey = "s3cr3t";
       
        String originalString = Email;

        //AESEncryptionDecryption aesEncryptionDecryption = new AESEncryptionDecryption();
        
        long epochMilliseconds = Instant.now().toEpochMilli();
        logger.info("Epoch in Milliseconds: " + epochMilliseconds);
        String epoch_string=Long.toString(epochMilliseconds);
        
        logger.info("epoch_string: "+ epoch_string);
        
        String final_string = encrypt(originalString+"time"+epoch_string, secKey);

        String decryptedString = decrypt(final_string, secKey);

        logger.info(originalString+"time"+epoch_string);
        logger.info(final_string);
        logger.info(decryptedString);
    	return final_string;
    }
    
    public static void prepareSecretKey(String myKey) 
    {
    	   MessageDigest sha = null;
           try {
               key = myKey.getBytes(StandardCharsets.UTF_8);
               sha = MessageDigest.getInstance("SHA-1");
               key = sha.digest(key);
               key = Arrays.copyOf(key, 16);
               secretKey = new SecretKeySpec(key, ALGORITHM);
           } catch (NoSuchAlgorithmException e) {
        	   logger.error("Exception in prepareSecretKey: " + e.toString());
               e.printStackTrace();
           }
    	
    }
    
    public static String encrypt(String strToEncrypt, String secret) 
    
    {	  
    	String encryptedString="";
    	  try {
              prepareSecretKey(secret);
              Cipher cipher = Cipher.getInstance(ALGORITHM);
              cipher.init(Cipher.ENCRYPT_MODE, secretKey);
              encryptedString=Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
          		} catch (Exception e) {
              logger.error("Error while encrypting: " + e.toString());
          }
          
		return encryptedString;
    }
    
    public static String decrypt(String strToDecrypt, String secret) throws Exception
    
    {
    	String decryptedString="";
        try {
            prepareSecretKey(secret);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            decryptedString= new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (Exception e) {
            logger.error("Error while decrypting: " + e.toString());
            throw new Exception("Invalid String to decrypt");
        }
        return decryptedString;
    }
    
    /*public  void sendMail(String Email,String finalCode)
    {
  
    	try {
    	
	    	logger.info("Email :"+ Email);
	    	SimpleMailMessage message = new SimpleMailMessage();
	    	message.setTo(Email);
	    	message.setFrom("no-reply@walmartlabs.com");
	    	message.setSubject("Email verification link"+contentV);
	    	message.setText(linkpartV+URLEncoder.encode(finalCode,"UTF-8"));
	    	
	    	javaMailSender.send(message);
	    	
	    	//message.setContent(htmlTemplate, "text/html; charset=utf-8");
    	
    	}
    	 catch (Exception e) {
             logger.error("Error while Sending Mail: "+e.toString()); 
         }
 
    }*/
    
    public String sendHTMLMail(String Email,String finalCode, String action) throws EmailNotFound
    {
    	String response = "";
    	String link="";
    	String emailContent="";
    	String deletelink="";
    	deletelink= linkpartD+Email;
    	UserModel user = null;

    	try {
    		if(!action.equalsIgnoreCase("validate") && !action.equalsIgnoreCase("EmailVerified"))
	    	{
	    		List<UserModel> list = userRepo.findAll(Email);
	    		if (list.size() > 0)
	    		{
	    			user = userRepo.findEmail(Email);
	    		}
	    		else
	    		{
	    			response = "User does not exist";
	    			return response;
	    		}
	    	}
	    		
    		//logger.info("Name: "+user.getName());
    		
	    	logger.info("Email :"+ Email);
	    	//logger.info("User: "+ user.toString());
	    	MimeMessage htmlmessage = javaMailSender.createMimeMessage();
	    	MimeMessageHelper helper = new MimeMessageHelper(htmlmessage, true);
	    	//SimpleMailMessage htmlmessage = new SimpleMailMessage();
	    	helper.setTo(Email);
	    	helper.setFrom(fromAddress);
	    	
	    	if(action.equalsIgnoreCase("validate"))
	    	{
		    	helper.setSubject(subjectV);
		    	link = linkpartV+URLEncoder.encode(finalCode,"UTF-8");
		    	emailContent = contentV;
		    	emailContent=emailContent.replace("type_of_action", "Email Verification");
		    	emailContent=emailContent.replace("link_delete", deletelink);
		    	emailContent=emailContent.replace("email.html.user", "");
		    	
		    	
	    	}
	    	else if(action.equalsIgnoreCase("Password"))
	    	{
    			if (user.getValidated().equalsIgnoreCase("No"))
    			{
    				response = "Email is not verified";
    				//System.out.println(response);
    			}
    			else
    			{
		    		helper.setSubject(subjectP);
			    	link = linkpartP;
			    	emailContent = contentP;
			    	emailContent=emailContent.replace("type_of_action", "Password Reset");
			    	emailContent=emailContent.replace("link_delete", deletelink);
			    	/*if (user.getName() == null)
			    	{
			    		emailContent=emailContent.replace("email.html.user", "");
			    	}
			    	else
			    	{
			    		emailContent=emailContent.replace("email.html.user", user.getName());
			    	}*/
    			}
	    	}
	    	else if(action.equalsIgnoreCase("PasswordVerified"))
	    	{
	    		helper.setSubject(subjectPV);
		    	emailContent = contentPV;
		    	emailContent = contentPV.replace("email.html.successmessage", "Your password has been successfully reset");
		    	emailContent=emailContent.replace("type_of_action", "Password Reset");
		    	emailContent=emailContent.replace("link_delete", deletelink);
		    	emailContent=emailContent.replace("email.html.success.subject", "Your password reset has been completed successfully");
		    	/*if (user.getName() == null)
		    	{
		    		emailContent=emailContent.replace("email.html.user", "");
		    	}
		    	else
		    	{
		    		emailContent=emailContent.replace("email.html.user", user.getName());
		    	}*/
	    	}
			else if(action.equalsIgnoreCase("EmailVerified"))
	    	{
	    		helper.setSubject(subjectEV);
	    		emailContent = contentPV.replace("email.html.successmessage", "Your email has been successfully verified");
	    		emailContent=emailContent.replace("type_of_action", "Email Verification");
	    		emailContent=emailContent.replace("link_delete", deletelink);
	    		emailContent=emailContent.replace("email.html.success.subject", "Your email has been successfully verified");
	    		/*if (user.getName() == null)
		    	{
		    		emailContent=emailContent.replace("email.html.user", "");
		    	}
		    	else
		    	{
		    		emailContent=emailContent.replace("email.html.user", user.getName());
		    	}*/
	    	}
	    	if (!finalCode.isEmpty() && response.isEmpty())
	    	{
		    	String final_content=emailContent.replace("email.html.url", link);
		    	
		    	Resource res = new FileSystemResource(new File(logoPath));
		    	Resource res1 = new FileSystemResource(new File(verifiedPath));
		    	//String emailContent = templateEngine.process(templateName, context);
		    	
		    	helper.setText(final_content, true);
		    	helper.addInline("logo_image",res);
		    	
		    	if(action.equalsIgnoreCase("PasswordVerified") || action.equalsIgnoreCase("EmailVerified"))
		    	{
		    		helper.addInline("verified_image",res1);
		    	}
		    	
		    	//htmlmessage.setContent(emailContent, "text/html");
		    	
		    	javaMailSender.send(htmlmessage);
		    	
		    	//message.setContent(htmlTemplate, "text/html; charset=utf-8");
		    	logger.info("Mail sent");
		    	response = "Mail sent";
	    	}
    	
    	}
    	 catch (Exception e) {
             logger.error("Error while Sending Mail: "+e.toString());
             response = "Mail sending failed";
         }
    	
    	return response;
 
    }
    
    public String validateEmailCode(String validationCode) throws Exception
    {
    	String response = null;
    	String DBresponse = "";
    	try {
    		
    		//URL decoding is not required because browser automatically handles this
    		//String decryptedString = decrypt(URLDecoder.decode(validationCode,"UTF-8"), secKey);--uncomment when testing from Postman
    		String decryptedString = decrypt(validationCode, secKey);//This is when testing from browser
    		if (!decryptedString.isEmpty())
    		{
	    		String email = decryptedString.substring(0, decryptedString.indexOf(encryptionPart));
	    		Long epochTime = Long.parseLong(decryptedString.substring(decryptedString.indexOf(encryptionPart)+encryptionPart.length()));
	    		//System.out.println("email: "+email);
	    		logger.info("epochTime: "+epochTime);
	    		
	    		Long epochPast = Instant.now().minus(15, ChronoUnit.MINUTES).toEpochMilli();
	    		
	    		if (epochTime >= epochPast)
	    		{
	    			DBresponse = getUserByEmail(email);
	    			if (DBresponse.equalsIgnoreCase("True"))
	    			{
	    				response = "We have successfully verified your email id.";
	    				sendHTMLMail(email,"ABC","EmailVerified");
	    			}
	    		}
	    		else
	    			response = "Your email verification code has expired. Please request for a new verification email.";
	    			System.out.println(response);
    		}
    	}
    	catch (Exception e) {
             logger.error("Error while Validating Code " + e.toString());
             response = "Your email verification code has expired. Please request for a new verification email.";
             throw new Exception("Invalid String to decrypt");
         }
    	return response;
    }
    
    
	public boolean deleteEmail(String id) throws EmailNotFound
	{	
		boolean status=false;
	
		try
		{
			
			List<UserModel> userall=userRepo.findAll(id);
			if(userall.size()>0)
			{
				userRepo.deleteById(id);
				List<UserModel> user = userRepo.findAll(id);
				System.out.println("Email: "+id);
				System.out.println(user);
				if (user.size()==0)
				{
					status = true;
				}
			}
			else
				throw new EmailNotFound();
		}
		catch (Exception e)
		{
			logger.error("Exception deleting: +" + e.toString());
			return status;
		}
	
		return status;		
	}
	
	public String passwordValidation(String email,String newPassword) throws Exception
	{
		
		String resp = "";
		
		try
		{
			UserModel user = userRepo.findEmail(email);
			/*if(!oldPassword.equals(user.getPassword()))
			{
				resp = "Old password doesn't match";
			}
			else
			{*/
			user.setPassword(newPassword);
			userRepo.save(user);
			if (user.getPassword().equals(newPassword))
			{
				resp = "Password changed successfully";
				sendHTMLMail(email,"ABC","PasswordVerified");
				
			}
			else
			{
				resp = "Password change failed. Please try again!";
			}
			//}
		}
		catch (Exception e) {
            logger.error("Error while updating password " + e.toString());
            resp = "Password change failed. Please try again!";
            throw new Exception("Error while changing password");
		}
		
		return resp;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		UserModel user = userRepo.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + email));
		return (UserDetails) user;
	}
	
	
}
