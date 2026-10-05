package com.mfano.mcfs.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter 
@Getter 
@NoArgsConstructor 
public class UserDto {
  @Email 
  @NotBlank 
  private String email;
  @NotBlank
  private String password;
  private String fin;
  private String lan;
  private String gender;
  private Long branch;
  private Long role;
}
