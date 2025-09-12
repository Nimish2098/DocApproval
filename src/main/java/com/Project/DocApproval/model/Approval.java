package com.Project.DocApproval.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import java.time.LocalDateTime;


public class Approval {


    private Long approvalId;

    private Long approverId;
    private String approverName;
    private String status;

}
