package com.example.auth.controller;

import com.example.auth.config.JwtTokenUtil;
import com.example.auth.model.JwtRequest;
import com.example.auth.model.Token;
import com.example.auth.model.User;
import com.example.auth.model.UserDTO;
import com.example.auth.service.JwtUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Optional;

@RestController
@CrossOrigin
public class JwtAuthenticationController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    @PostMapping("/signin")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {
        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        final UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        final String token = jwtTokenUtil.generateToken(userDetails);

        final Token storedToken = jwtUserDetailsService.saveTokenToUser(authenticationRequest.getUsername(), token);

        return ResponseEntity.ok(storedToken);
    }

    @PostMapping("/signout")
    public void logout(@RequestHeader(name = "Authorization") String authorization) {
        final String token = authorization.substring(7);
        final String username = jwtTokenUtil.getUsernameFromToken(token);
        jwtUserDetailsService.removeTokenFromUser(username, token);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> saveUser(@RequestBody UserDTO user) throws Exception {

        Optional<User> userCheck = jwtUserDetailsService.getUserByUsername(user.getUsername());

        if (userCheck.isEmpty()) {
            final UserDetails userDetails = new org.springframework.security.core.userdetails.
                    User(user.getUsername(), user.getPassword(), new ArrayList<>());

            final String generatedToken = jwtTokenUtil.generateToken(userDetails);

            final User createdUser = jwtUserDetailsService.save(user, generatedToken);
            createdUser.removeTokensExcept(generatedToken);

            return ResponseEntity.ok(createdUser);
        }

        return ResponseEntity.status(400).body("User already exist with given details");
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
}
