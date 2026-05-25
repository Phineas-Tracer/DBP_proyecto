package org.ide.dbp_proyecto.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ide.dbp_proyecto.dto.AuthResponse;
import org.ide.dbp_proyecto.dto.LoginRequest;
import org.ide.dbp_proyecto.dto.RequestUser;
import org.ide.dbp_proyecto.dto.ResponseUser;
import org.ide.dbp_proyecto.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ResponseUser> registerUser(@Valid @RequestBody RequestUser requestUser) {

        ResponseUser responseUser = userService.RegisterUser(requestUser);

        return ResponseEntity.ok(responseUser);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(userService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<ResponseUser> getProfile(
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                userService.getProfile(authentication)
        );
    }

    @PutMapping("/me")
    public ResponseEntity<ResponseUser> updateProfile(
            Authentication authentication,
            @RequestBody RequestUser request
    ) {

        return ResponseEntity.ok(
                userService.updateProfile(authentication, request)
        );
    }


}
