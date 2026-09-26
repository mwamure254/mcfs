package com.mfano.mcfs.auth.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.OneToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;
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

    @OneToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "user_id")
    private User manager;

    private String contact;  
    private String email;
    private String address;
}
