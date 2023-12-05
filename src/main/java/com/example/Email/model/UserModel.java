package com.example.Email.model;

import java.util.Arrays;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="User")
public class UserModel {

	@Id
	public String ID;
	private String email;
	
	@Override
	public String toString() {
		return "UserModel [email=" + email + ", id=" + id + ", password=" + password + ", avtar=" + avtar
				+ ", validated=" + validated + ", interests=" + Arrays.toString(interests) + ", roles="
				+ Arrays.toString(roles) + ", favoriteCategories=" + Arrays.toString(favoriteCategories)
				+ ", recentSearches=" + Arrays.toString(recentSearches) + ", phone=" + phone + ", securityQuestion="
				+ securityQuestion + ", name=" + name + ", address=" + address + "]";
	}
	private Integer id;
	private String password;
	private String avtar;
	private String validated;
	private String[] interests;
	private String[] roles;
	private String[] favoriteCategories;
	private String[] recentSearches;
	private String phone;
	private String securityQuestion = new String();
	private String name = new String();
	private String address= new String();
	public UserModel(String email, Integer id, String password, String avtar, String validated, String[] interests,
			String[] roles, String[] favoriteCategories, String[] recentSearches, String phone, String securityQuestion,
			String name, String address) {
		super();
		this.email = email;
		this.id = id;
		this.password = password;
		this.avtar = avtar;
		this.validated = validated;
		this.interests = interests;
		this.roles = roles;
		this.favoriteCategories = favoriteCategories;
		this.recentSearches = recentSearches;
		this.phone = phone;
		this.securityQuestion = securityQuestion;
		this.name = name;
		this.address = address;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getAvtar() {
		return avtar;
	}
	public void setAvtar(String avtar) {
		this.avtar = avtar;
	}
	public String getValidated() {
		return validated;
	}
	public void setValidated(String validated) {
		this.validated = validated;
	}
	public String[] getInterests() {
		return interests;
	}
	public void setInterests(String[] interests) {
		this.interests = interests;
	}
	public String[] getRoles() {
		return roles;
	}
	public void setRoles(String[] roles) {
		this.roles = roles;
	}
	public String[] getFavoriteCategories() {
		return favoriteCategories;
	}
	public void setFavoriteCategories(String[] favoriteCategories) {
		this.favoriteCategories = favoriteCategories;
	}
	public String[] getRecentSearches() {
		return recentSearches;
	}
	public void setRecentSearches(String[] recentSearches) {
		this.recentSearches = recentSearches;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getSecurityQuestion() {
		return securityQuestion;
	}
	public void setSecurityQuestion(String securityQuestion) {
		this.securityQuestion = securityQuestion;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
}
