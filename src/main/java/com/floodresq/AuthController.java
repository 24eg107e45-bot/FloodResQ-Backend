
package com.floodresq.floodresq_backend;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(
    origins = {
        "http://localhost:5173",
        "http://localhost:5174"
    },
    allowCredentials = "true"
)
public class AuthController {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(
            AppUserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody Map<String, String> request) {

        String fullName = request.getOrDefault("fullName", "").trim();
        String email = request.getOrDefault("email", "")
                .trim().toLowerCase();
        String password = request.getOrDefault("password", "");

        if (fullName.isBlank() || email.isBlank()
                || password.length() < 6) {
            return ResponseEntity.badRequest().body(
                Map.of("message",
                    "Name, email and password of at least 6 characters are required.")
            );
        }

        if (userRepository.existsByEmailIgnoreCase(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                Map.of("message", "This email is already registered.")
            );
        }

        AppUser user = new AppUser();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("VICTIM");

        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(
            Map.of("message", "Account created successfully. Please sign in.")
        );
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody Map<String, String> request,
            HttpSession session) {

        String email = request.getOrDefault("email", "")
                .trim().toLowerCase();
        String password = request.getOrDefault("password", "");

        AppUser user = userRepository.findByEmailIgnoreCase(email)
                .orElse(null);

        if (user == null
                || !passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                Map.of("message", "Invalid email or password.")
            );
        }

        session.setAttribute("userId", user.getId());
        session.setAttribute("userRole", user.getRole());
        session.setAttribute("userEmail", user.getEmail());

        return ResponseEntity.ok(Map.of(
            "message", "Login successful.",
            "fullName", user.getFullName(),
            "email", user.getEmail(),
            "role", user.getRole()
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<?> currentUser(HttpSession session) {
        Object userId = session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                Map.of("message", "Please sign in.")
            );
        }

        return ResponseEntity.ok(Map.of(
            "userId", userId,
            "role", session.getAttribute("userRole"),
            "email", session.getAttribute("userEmail")
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();

        return ResponseEntity.ok(
            Map.of("message", "Logged out successfully.")
        );
    }
}
