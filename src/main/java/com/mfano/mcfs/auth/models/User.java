package com.mfano.mcfs.auth.models;

import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Setter
@Getter
@NoArgsConstructor
public class User extends BaseObject {
    private String fin;
    private String lan;

    public void setFin(String fin) {
        fin = fin.trim().toLowerCase();
        this.fin = Character.toUpperCase(fin.charAt(0)) + fin.substring(1);
    }

    public void setLan(String lan) {
        lan = lan.trim().toLowerCase();
        this.lan = Character.toUpperCase(lan.charAt(0)) + lan.substring(1);
    }

    @Column(unique = true)
    private String email;

    public void setEmail(String email) {
        this.email = email.toLowerCase();
    }

    private String gender;

    private String password;
    @Column(nullable = false)
    private boolean enabled;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;// = new HashSet<>();

}
