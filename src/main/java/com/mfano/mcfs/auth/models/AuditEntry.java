package com.mfano.mcfs.auth.models;

import com.mfano.mcfs.auth.models.BaseObject;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "audits")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuditEntry extends BaseObject {
    private String action;
    private String details;
}
