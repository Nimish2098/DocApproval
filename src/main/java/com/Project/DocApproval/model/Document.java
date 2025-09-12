package com.Project.DocApproval.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import java.time.LocalDateTime;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Document {

    @Id
    private Long documentId;

    private String documentName;

    private String filePath;

//    private LocalDateTime localDateTime;

    private String uploadedBy;



}
