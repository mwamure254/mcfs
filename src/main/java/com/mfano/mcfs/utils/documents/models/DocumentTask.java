package com.mfano.mcfs.utils.documents.models;

import com.mfano.mcfs.auth.models.BaseObject;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
//@AllArgsConstructor
//@Builder
public class DocumentTask extends BaseObject {
     /**
     * Person/organization that sent the document.
     */
    @Column(name = "sender", length = 255)
    private String sender;
     /**
     * Person/organization receiving the document.
     */
    @Column(name = "recipient", length = 255)
    private String recipient;

}
