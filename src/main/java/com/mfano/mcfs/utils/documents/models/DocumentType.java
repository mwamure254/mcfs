package com.mfano.mcfs.utils.documents.models;
import com.mfano.mcfs.auth.models.CommonObject;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "dotes")
@Setter
@Getter
public class DocumentType extends CommonObject {

}