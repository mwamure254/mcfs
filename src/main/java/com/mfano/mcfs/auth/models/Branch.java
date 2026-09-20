package com.mfano.mcfs.auth.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "branches")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Branch extends CommonObject {
    private String location;
    private String manager;
    private String contact;  
    private String email;
    private String address;
    private String createdBy;
}
