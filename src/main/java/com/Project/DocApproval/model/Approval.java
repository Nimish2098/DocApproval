package com.Project.DocApproval.model;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@Entity
public class Approval {

    private Long approverId;
    private Long documentId;
    private DocumentStatus status;
    private String approvalOrder;
}
