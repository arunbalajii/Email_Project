package com.example.Email.model;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Document(collection="User")
public class UserModel {

	@Id
	private String ID;
	
	@NotBlank
	@Size(max = 20)
	private String username;
	
	@NotBlank
	@Size(max = 50)
	@Email
	private String email;
	//private Integer id;
	
	@NotBlank
	@Size(max = 120)
	private String password;
	private String gender;
	private String phone;
	private String validated;
	//private String[] interests;
	@DBRef
	private Set<Role> roles = new HashSet<>();
	
	public Address address;
	
	public Name name;
	
	//private String[] favoriteCategories;
	//private String[] recentSearches;
	
	//private String securityQuestion = new String();
	private Integer userId;
	public UserModel(String username, String email, String password, String gender, String phone,
			String validated, Integer userId, Address address, Name name) {
		super();
		this.username = username;
		this.email = email;
		this.password = password;
		this.gender = gender;
		this.phone = phone;
		this.validated = validated;
		this.userId = userId;
		this.address = address;
		this.name = name;
	}
	@Override
	public String toString() {
		return "UserModel [ID=" + ID + ", username=" + username + ", email=" + email + ", password=" + password
				+ ", gender=" + gender + ", phone=" + phone + ", validated=" + validated + ", roles="
				+ ", userId=" + userId + ", address=" + address + ", name=" + name + "]";
	}
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getValidated() {
		return validated;
	}
	public void setValidated(String validated) {
		this.validated = validated;
	}

	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}	
	
}
