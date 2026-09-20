package com.mfano.mcfs.auth.models;

import com.mfano.mcfs.auth.models.CommonObject;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "roles")
@Setter
@Getter
public class Role extends CommonObject {

}
