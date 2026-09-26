package com.mfano.mcfs.auth.models;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.mfano.mcfs.auth.models.BaseObject;
import com.mfano.mcfs.auth.models.User;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Profile extends BaseObject{
    // personal
    private String about;
    private String fin;
    private String lan;
    private String other;
    private String SN;
    private String phone;
    // TSC or PF Number
    private String image;
    private String gender;
    // address
    private String county;
    private String address;
    private String designation;
    // Social Media
    private String twitter;
    private String facebook;
    private String linkedin;
    private String instagram;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userid", nullable = true, insertable=false, updatable=false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;
    private Long userid;
}