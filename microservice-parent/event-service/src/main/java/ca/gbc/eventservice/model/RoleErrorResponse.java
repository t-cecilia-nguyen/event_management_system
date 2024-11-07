package ca.gbc.eventservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@Data
@NoArgsConstructor
public class RoleErrorResponse {
    private String role;
    private String errorMessage;

}
