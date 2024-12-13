package com.christmas.letter.sender.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Letter {
    @Email
    @NotBlank
    private String email;
    private String name;
    @NotBlank
    private String body;
    @NotEmpty
    private List<String> wishes;
    @NotBlank
    private String location;
}
