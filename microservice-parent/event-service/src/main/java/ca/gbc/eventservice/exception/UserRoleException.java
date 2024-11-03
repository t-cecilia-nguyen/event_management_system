package ca.gbc.eventservice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserRoleException extends Exception {
    String role;
    String message;

    @Override
    public String toString() {
        return "UserRoleException{" +
                "role='" + role + '\'' +
                ", error='" + message + '\'' +
                '}';
    }
}
