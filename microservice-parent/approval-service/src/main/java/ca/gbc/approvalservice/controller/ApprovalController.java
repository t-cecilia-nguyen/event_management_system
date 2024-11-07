package ca.gbc.approvalservice.controller;

import ca.gbc.approvalservice.dto.ApprovalRequest;
import ca.gbc.approvalservice.dto.ApprovalResponse;
import ca.gbc.approvalservice.service.ApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/api/approval/" + createdApproval.id());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .headers(headers)
                .contentType(MediaType.APPLICATION_JSON)
                .body(createdApproval);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ApprovalResponse> getAllApprovals() {
        return approvalService.getAllApprovals();
    }

    @GetMapping("/{approvalId}")
    public ResponseEntity<ApprovalResponse> getApprovalById(@PathVariable("approvalId") String approvalId) {
        ApprovalResponse approval = approvalService.getApprovalById(approvalId);
        return ResponseEntity.ok(approval);
    }

    @PutMapping("/{approvalId}")
    public ResponseEntity<?> updateApproval(@PathVariable("approvalId") String approvalId,
                                            @RequestBody ApprovalRequest approvalRequest) {
        String updatedApprovalId = String.valueOf(approvalService.updateApproval(approvalId, approvalRequest));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/api/approval/" + updatedApprovalId);

        return new ResponseEntity<>(headers, HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{approvalId}")
    public ResponseEntity<?> deleteApproval(@PathVariable("approvalId") String approvalId) {
        approvalService.deleteApproval(approvalId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
