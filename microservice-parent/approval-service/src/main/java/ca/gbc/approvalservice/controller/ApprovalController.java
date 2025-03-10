package ca.gbc.approvalservice.controller;

import ca.gbc.approvalservice.dto.ApprovalRequest;
import ca.gbc.approvalservice.dto.ApprovalResponse;
import ca.gbc.approvalservice.service.ApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/approval")
@RequiredArgsConstructor
public class ApprovalController {

    private final ApprovalService approvalService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApprovalResponse> createApproval(@RequestBody ApprovalRequest approvalRequest) {
        ApprovalResponse createdApproval = approvalService.createApproval(approvalRequest);

        if (createdApproval == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);  // In case creation fails
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/api/approval/" + createdApproval.id());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .headers(headers)
                .contentType(MediaType.APPLICATION_JSON)
                .body(createdApproval);
    }

    @GetMapping
//    @PreAuthorize("permitAll()")
    @ResponseStatus(HttpStatus.OK)
    public List<ApprovalResponse> getAllApprovals() {
        return approvalService.getAllApprovals();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApprovalResponse> getApprovalById(@PathVariable String id) {
        ApprovalResponse approval = approvalService.getApprovalById(id);
        return approval != null ? new ResponseEntity<>(approval, HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // Approve an approval
    @PutMapping("/approve")
//    @PreAuthorize("hasRole('ROLE_staff')")
    public ResponseEntity<ApprovalResponse> approveApproval(
            @RequestBody ApprovalRequest approvalRequest) {
        try {
            ApprovalResponse response = approvalService.approve(approvalRequest.id(), approvalRequest.approverId(), approvalRequest.comments());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/deny")
//    @PreAuthorize("hasRole('ROLE_staff')")
    public ResponseEntity<ApprovalResponse> denyApproval(
//            @PathVariable String id,
            @RequestBody ApprovalRequest approvalRequest) {
        try {
            ApprovalResponse response = approvalService.deny(approvalRequest.id(), approvalRequest.approverId(), approvalRequest.comments());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApproval(@PathVariable String id) {
        approvalService.deleteApproval(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
