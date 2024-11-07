package ca.gbc.eventservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@Data
@NoArgsConstructor
public class UserIdErrorResponse {
    private Long userId;
    private String errorMessage;

}
