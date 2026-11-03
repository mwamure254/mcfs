package com.mfano.mcfs.records.models;
import com.mfano.mcfs.auth.models.CommonObject;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "docas")
@Setter
@Getter
public class DocumentClass extends CommonObject {

}