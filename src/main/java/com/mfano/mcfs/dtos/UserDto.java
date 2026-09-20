package com.mfano.mcfs.dtos;

import java.util.HashSet;
import java.util.Set;

import com.mfano.mcfs.auth.models.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter 
@Getter 
@NoArgsConstructor 
@AllArgsConstructor 
public class UserDto {
  @Email 
  @NotBlank 
  private String email;
  @NotBlank
  private String password;
  private String fin;
  private String lan;
  private String gender;
  @NotNull 
  private Long branch;
  @NotNull 
  private Long role;

}
