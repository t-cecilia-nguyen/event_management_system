package ca.gbc.eventservice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserIdException extends Exception {

    Long userId;
    String errorMessage;

    @Override
    public String toString() {
        return "UserIdException{" +
                "userId=" + userId +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
