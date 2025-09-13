package com.Project.DocApproval.model;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Data
@Getter
@Setter
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long documentId;

    private String documentName;

    private String type;

    @Lob
    @Column(length = Integer.MAX_VALUE)
    private byte[] data;

    public Document(){}

    public Document(String name,String type, byte[]data){
        this.documentName = name;
        this.type = type;
        this.data = data;
    }

}
