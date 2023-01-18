package com.itemreader.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true) // If we passed the key-value in Json but no mapping here then we can
                                            // ignore.Without this Exception occurs.
public class StudentJson {

    private Long id;
    @JsonProperty("firstName") // we can give any value in json file but we need to mention the given key here.
    private String firstName;
    private String lastName;
    private String email;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "StudentJson [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", email=" + email
                + "]";
    }
}
