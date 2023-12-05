package com.example.Email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;


//import com.example.Email.model.UserModel;
//import com.example.Email.repository.UserRepository;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableMongoRepositories
//@SpringBootApplication
public class EmailApplication{
	
	//@Autowired
	//UserRepository user;
	
	Logger logger = LoggerFactory.getLogger(EmailApplication.class);
	
	public static void main(String[] args) {
		SpringApplication.run(EmailApplication.class, args);
	}
	
	/*@Override
	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub
		/*for (UserModel u :user.findAll())
		{
			System.out.println(u.toString());
		}*/
		//UserModel u = user.findEmail("anindita.guha@walmart.com");
		//System.out.println(u.toString());
	//}*/

}