package ca.gbc.approvalservice.dto;

import ca.gbc.approvalservice.model.Approval.ApprovalStatus;

public record ApprovalRequest(
        String id,
        String eventId,
        String approverId,
        ApprovalStatus status, 
        String comments
) {}