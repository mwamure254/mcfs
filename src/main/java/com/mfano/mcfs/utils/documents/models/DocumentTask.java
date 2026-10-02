package com.mfano.mcfs.utils.documents.models;

import java.util.Set;

import com.mfano.mcfs.auth.models.CommonObject;
import com.mfano.mcfs.auth.models.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class DocumentTask extends CommonObject {
    @Column(unique = false, nullable = false)
    String reference;
    /*
     * Person/organization receiving the document.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recipient", nullable = false)
    private Role recipient;

    private String dosa;

}
