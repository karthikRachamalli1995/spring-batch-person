package com.spring.batchprocessing.resource;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonResource {

    private String firstname;
    private String lastname;
    private String dob;
}

