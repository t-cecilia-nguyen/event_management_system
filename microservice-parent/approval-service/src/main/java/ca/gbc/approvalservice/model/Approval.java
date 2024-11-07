package ca.gbc.approvalservice.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


// Lombok Annotations
@Document(value="approval") // This class is a Document (table for non-relational db)
@Data /
@AllArgsConstructor // Constructors
@NoArgsConstructor
@Builder
public class Approval {
    @Id
    private String id;
    private String eventId;
    private String approverId;
    private boolean approved;
    private String comments;
}
