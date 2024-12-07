package ca.gbc.approvalservice.dto;

import ca.gbc.approvalservice.model.Approval.ApprovalStatus;
import lombok.Getter;
import lombok.Setter;


public record ApprovalRequest(
        String id,
        String eventId,
        String approverId,
        ApprovalStatus status,
        String comments
) {

}
