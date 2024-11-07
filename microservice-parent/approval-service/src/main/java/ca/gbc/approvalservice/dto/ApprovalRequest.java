package ca.gbc.approvalservice.dto;

public record ApprovalRequest(
     String id,
     String eventId,
     String approverId,
     boolean approved,
     String comments
     ) {}
