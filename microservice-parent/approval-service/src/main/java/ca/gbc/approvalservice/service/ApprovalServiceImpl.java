package ca.gbc.approvalservice.service;

import ca.gbc.approvalservice.dto.ApprovalRequest;
import ca.gbc.approvalservice.dto.ApprovalResponse;
import ca.gbc.approvalservice.model.Approval;
import ca.gbc.approvalservice.repository.ApprovalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j // For logging
@RequiredArgsConstructor
public class ApprovalServiceImpl implements ApprovalService{
    private final ApprovalRepository approvalRepository;
    private final MongoTemplate mongoTemplate; // For advanced queries

    @Override
    public ApprovalResponse createApproval(ApprovalRequest approvalRequest) {
        log.debug("Creating a new approval for event {}", approvalRequest.eventId());

        Approval approval = Approval.builder()
                .id(approvalRequest.id())
                .eventId(approvalRequest.eventId())
                .approverId(approvalRequest.approverId())
                .approved(approvalRequest.approved())
                .comments(approvalRequest.comments())
                .build();


        approvalRepository.save(approval);

        log.info("Approval {} is saved", approval.getId());

        return new ApprovalResponse(approval.getId(),
                approval.getEventId(),
                approval.getApproverId(),
                approval.isApproved(),
                approval.getComments());
    }

    @Override
    public List<ApprovalResponse> getAllApprovals() {
        log.debug("Returning a list of approvals");

        List<Approval> approvals = approvalRepository.findAll();
        return approvals.stream().map(this::mapToApprovalResponse).toList();
    }

    private ApprovalResponse mapToApprovalResponse(Approval approval) {
        return new ApprovalResponse(
                approval.getId(),
                approval.getEventId(),
                approval.getApproverId(),
                approval.isApproved(),
                approval.getComments()
        );
    }

    @Override
    public ApprovalResponse getApprovalById(String approvalId) {
        log.debug("Fetching approval with id {}", approvalId);

        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(approvalId));
        Approval approval = mongoTemplate.findOne(query, Approval.class); // Finds approval by ID

        if (approval != null) {
            return mapToApprovalResponse(approval);
        }

        log.warn("Approval with id {} not found", approvalId);
        return null;
    }

    @Override
    public ApprovalResponse updateApproval(String approvalId, ApprovalRequest approvalRequest) {
        log.debug("Updating approval with id {}", approvalId);

        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(approvalId));
        Approval approval = mongoTemplate.findOne(query, Approval.class);

        if (approval != null) {
            approval.setApproved(approvalRequest.approved());
            approval.setComments(approvalRequest.comments());

            // Save updated approval and create ApprovalResponse
            Approval updatedApproval = approvalRepository.save(approval);

            // Return ApprovalResponse using the updated approval details
            return new ApprovalResponse(
                    updatedApproval.getId(),
                    updatedApproval.getEventId(),
                    updatedApproval.getApproverId(),
                    updatedApproval.isApproved(),
                    updatedApproval.getComments()
            );
        }

        log.warn("Approval with id {} not found", approvalId);
        return null;
    }

    @Override
    public void deleteApproval(String approvalId) {
        log.debug("Deleting approval with id {}", approvalId);
        approvalRepository.deleteById(approvalId);
    }
}
