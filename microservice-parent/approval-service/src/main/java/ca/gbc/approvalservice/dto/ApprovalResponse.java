package ca.gbc.approvalservice.dto;

public record ApprovalResponse(
        String id,
        String eventId,
        String approverId,
        boolean approved,
        String comments
) {}
