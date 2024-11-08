package ca.gbc.userservice.controller;

import ca.gbc.userservice.dto.UserRequest;
import ca.gbc.userservice.model.User;
import ca.gbc.userservice.dto.UserResponse;
import ca.gbc.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest userRequest) {
        UserResponse createdUser = userService.createUser(userRequest);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/users/" + createdUser.id());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .headers(headers)
                .contentType(MediaType.APPLICATION_JSON)
                .body(createdUser);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }



    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateUser(@PathVariable("id") Long id,
                           @RequestBody UserRequest userRequest) {
        userService.updateUser(id, userRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @GetMapping("/usertype")
    @ResponseStatus(HttpStatus.OK)
    public List<User> checkAllUserType(@RequestParam String userType) {
        return userService.checkAllUserType(userType);
    }



    @GetMapping("/exist/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Boolean isUserExist(@PathVariable("id") Long id) {
        return userService.isUserExist(id);
    }

    @GetMapping("/{id}/usertype")
    @ResponseStatus(HttpStatus.OK)
    public String checkUserType(@PathVariable Long id) {
        return userService.checkUserType(id);
    }
}
