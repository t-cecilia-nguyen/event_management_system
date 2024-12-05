package ca.gbc.approvalservice.dto;

import ca.gbc.approvalservice.model.Approval.ApprovalStatus;

public record ApprovalResponse(
        String id,
        String eventId,
        String approverId,
        ApprovalStatus approved,
        String comments
) {

    public ApprovalResponse {
        if (approverId == null || approverId.isEmpty()) {
            approverId = "default-approver-id";
        }
    }
}
