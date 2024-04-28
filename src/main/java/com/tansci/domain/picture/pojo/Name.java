package com.tansci.domain.picture.pojo;

import lombok.Data;

@Data
public class Name {
    private String firstName;
    private String lastName;
    public Name(String firstName, String lastName){
        this.firstName=firstName;
        this.lastName=lastName;
    }
}