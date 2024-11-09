package ca.gbc.approvalservice.service;

import ca.gbc.approvalservice.dto.ApprovalRequest;
import ca.gbc.approvalservice.dto.ApprovalResponse;
import ca.gbc.approvalservice.model.Approval;

import java.util.List;

public interface ApprovalService {
    ApprovalResponse createApproval(ApprovalRequest approvalRequest);
    List<ApprovalResponse> getAllApprovals();
    ApprovalResponse getApprovalById(String approvalId);
    ApprovalResponse approve(String approvalId, String approverId, String comments);
    ApprovalResponse deny(String approvalId, String approverId, String comments);
    void deleteApproval(String approvalId);

}
