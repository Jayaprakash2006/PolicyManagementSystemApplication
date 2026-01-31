package com.pms.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "customer")
public class Customer {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "name")
    @NotEmpty(message = "Name is required")
    private String name;

    @Column(name = "age")
    @NotNull(message = "Age is required")
    @Min(value = 18, message = "Age must be at least 18")
    @Max(value = 100, message = "Age must not exceed 100")
    private Integer age;

    @Column(name = "phone")
    @NotEmpty(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be of 10 digits and Numbers only")
    private String phone;

    @Column(name = "email")
    @NotEmpty(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Column(name = "address")
    @NotEmpty(message = "Address is required")
    private String address;

    @Column(name = "regDate")
    private String regDate;

    @Column(name="password")
    @NotEmpty(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[@$!%*?&])(?=.*[0-9])[A-Za-z\\d@$!%*?&]{6,}$",
            message = "Password must contain at least one uppercase letter, one special character, and one number"
    )
    private String password;

    @Column(name="verified")
    private Boolean verified = false;

    @Column(name="active")
    private Boolean active = false;


    @JsonBackReference(value="customer-policies")
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoughtPolicy> policies;

    @JsonManagedReference(value="customer-payments")
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments;;
    
    @JsonIgnore
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Feedback> feedbacks;

    // Getters and Setters
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }
    public void setAge(Integer age) {
        this.age = age;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getRegDate() {
        return regDate;
    }
    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }

    public boolean getVerified() {
    	return verified != null ? verified : false;
    }
    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public boolean getActive() {
        return active != null ? active : false;
    }
    public void setActive(boolean active) {
        this.active = active;
    }

    public List<BoughtPolicy> getPolicies() {
        return policies;
    }
    public void setPolicies(List<BoughtPolicy> policies) {
        this.policies = policies;
    }
    public List<Payment> getPayments() {
        return payments;
    }
    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }
}