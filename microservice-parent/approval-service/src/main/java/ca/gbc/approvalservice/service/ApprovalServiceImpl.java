package ca.gbc.approvalservice.service;

import ca.gbc.approvalservice.client.EventClient;
import ca.gbc.approvalservice.client.UserClient;
import ca.gbc.approvalservice.dto.ApprovalRequest;
import ca.gbc.approvalservice.dto.ApprovalResponse;
import ca.gbc.approvalservice.model.Approval;
import ca.gbc.approvalservice.repository.ApprovalRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.Map;
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

//            ResponseEntity<Map> eventResponse = (ResponseEntity<Map>) eventClient.getEventById(approvalRequest.eventId());
//
//            if (eventResponse == null || eventResponse.getBody() == null) {
//                log.warn("Event with ID {} does not exist", approvalRequest.eventId());
//                return null;
//            }

            // Proceed with checking if the user is authorized to approve
            var isStaff = userClient.checkUserType(approvalRequest.approverId());

            if (Objects.equals(isStaff, "STAFF")) {
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
            } else {
                log.warn("User is not authorized to approve");
                return null;
            }
        } catch (FeignException | HttpClientErrorException e) {
            log.error("Error checking user type or event existence: {}", e.getMessage());
            return null;
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
    public ApprovalResponse updateApproval(String approvalId, ApprovalRequest approvalRequest) {
        log.debug("Updating approval with id {}", approvalId);

        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(approvalId));
        Approval approval = mongoTemplate.findOne(query, Approval.class);

        if (approval != null) {
            approval.setStatus(approvalRequest.status());  // Update status here
            approval.setComments(approvalRequest.comments());

            Approval updatedApproval = approvalRepository.save(approval);
            return mapToApprovalResponse(updatedApproval);
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
