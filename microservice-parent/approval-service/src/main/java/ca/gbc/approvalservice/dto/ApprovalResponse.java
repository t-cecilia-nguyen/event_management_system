package ca.gbc.approvalservice.dto;

public record ApprovalResponse(
        String id,
        String eventId,
        String approverId,
        ca.gbc.approvalservice.model.Approval.ApprovalStatus approved,
        String comments
) {}
