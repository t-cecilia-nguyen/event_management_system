package ca.gbc.approvalservice.controller;

import ca.gbc.approvalservice.dto.ApprovalRequest;
import ca.gbc.approvalservice.dto.ApprovalResponse;
import ca.gbc.approvalservice.service.ApprovalService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.access.annotation.Secured;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.Map;


@Slf4j
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
    @ResponseStatus(HttpStatus.OK)
    public List<ApprovalResponse> getAllApprovals() {
        log.info("getApproval");
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
//    @PreAuthorize("hasRole('SCOPE_email')")
    public ResponseEntity<ApprovalResponse> approveApproval(
            @RequestBody ApprovalRequest approvalRequest,
            Authentication authentication) {

        log.info("Authenticator: {}",  authentication.getAuthorities());

//        boolean hasStaffRole = authentication.getAuthorities().stream()
//                .anyMatch(authority -> authority.getAuthority().equals("ROLE_staff"));
//
//        if (!hasStaffRole) {
//            log.warn("User {} does not have the required ROLE_staff authority", authentication.getName());
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//        }

        try {
            ApprovalResponse response = approvalService.approve(
                    approvalRequest.id(),
                    approvalRequest.approverId(),
                    approvalRequest.comments()
            );
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            log.error("Error processing approval request: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

//    @PutMapping("/approve")
//    public ResponseEntity<ApprovalResponse> approveApproval(@RequestBody ApprovalRequest approvalRequest,
//                                                            @AuthenticationPrincipal Jwt jwt,
//                                                            Authentication authentication) {
//        log.info("Request reached controller");  // Log here to check if the method is called
//        try {
//            ApprovalResponse response = approvalService.approve(
//                    approvalRequest.id(),
//                    approvalRequest.approverId(),
//                    approvalRequest.comments()
//            );
//            return ResponseEntity.ok(response);
//        } catch (ResponseStatusException e) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//        }
//    }


    @PutMapping("/deny")
//    @PreAuthorize("hasRole('ROLE_email')")
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
        log.info("deleting approval request");
        approvalService.deleteApproval(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/debug-token")
    public Map<String, Object> debugToken(@AuthenticationPrincipal Jwt jwt, Authentication authentication) {
        log.info("Claims from debug-token: {}", jwt.getClaims());

        // Log the authentication principal and authorities
        log.info("Authenticated user: {}", authentication.getName());
        log.info("Authorities from Authentication: {}", authentication.getAuthorities());

        // Check if "ROLE_staff" is in authorities
        boolean hasRoleStaff = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_staff"));

        log.info("User has ROLE_staff: {}", hasRoleStaff);

        return jwt.getClaims();  // Returning the JWT claims with updated authorities
    }
}
