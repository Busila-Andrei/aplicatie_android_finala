package com.example.aplicatie_android_finala.data.dto;

public class RegisterRequest {

    String firstname;
    String lastname;
    String email;
    String password;

    public RegisterRequest(String firstname, String lastname, String email, String password) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
    }

}
