package ca.gbc.approvalservice.service;

import ca.gbc.approvalservice.client.EventClient;
import ca.gbc.approvalservice.client.UserClient;
import ca.gbc.approvalservice.dto.ApprovalRequest;
import ca.gbc.approvalservice.dto.ApprovalResponse;
import ca.gbc.approvalservice.model.Approval;
import ca.gbc.approvalservice.repository.ApprovalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApprovalServiceImpl implements ApprovalService{
    private final ApprovalRepository approvalRepository;
    private final MongoTemplate mongoTemplate;
    private final UserClient userClient;
    private final EventClient eventClient;

    @Override
    public ApprovalResponse createApproval(ApprovalRequest approvalRequest) {
        log.debug("Creating a new approval for event {}", approvalRequest.eventId());

        try {
            Approval approval = Approval.builder()
                    .id(approvalRequest.id())
                    .eventId(approvalRequest.eventId())
                    .approverId(approvalRequest.approverId())
                    .status(Approval.ApprovalStatus.PENDING)  // Set to PENDING initially
                    .comments(approvalRequest.comments())
                    .build();
            approvalRepository.save(approval);
            log.info("Approval {} is saved", approval.getId());
            return mapToApprovalResponse(approval);

        } catch (HttpClientErrorException e) {
            log.error("Error checking user type or event existence: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public ApprovalResponse approve(String approvalId, String approverId, String comments) {
        var isStaff = userClient.checkUserType(approverId);

        if (!Objects.equals(isStaff, "STAFF")) {
            log.warn("User {} is not authorized to approve", approverId);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User not authorized to approve");
        }

        return updateApprovalStatus(approvalId, Approval.ApprovalStatus.APPROVED, comments);
    }

    @Override
    public ApprovalResponse deny(String approvalId, String approverId, String comments) {
        var isStaff = userClient.checkUserType(approverId);

        if (!Objects.equals(isStaff, "STAFF")) {
            log.warn("User {} is not authorized to deny", approverId);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User not authorized to deny");
        }

        return updateApprovalStatus(approvalId, Approval.ApprovalStatus.DENIED, comments);
    }

    private ApprovalResponse updateApprovalStatus(String approvalId, Approval.ApprovalStatus status, String comments) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(approvalId));
        Approval approval = mongoTemplate.findOne(query, Approval.class);

        if (approval != null) {
            approval.setStatus(status);
            approval.setComments(comments);

            Approval updatedApproval = approvalRepository.save(approval);
            return mapToApprovalResponse(updatedApproval);
        } else {
            log.warn("Approval with id {} not found", approvalId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Approval not found");
        }
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
                approval.getStatus(),
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
    public void deleteApproval(String approvalId) {
        log.debug("Deleting approval with id {}", approvalId);
        approvalRepository.deleteById(approvalId);
    }


}
