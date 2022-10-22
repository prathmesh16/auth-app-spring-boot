package com.example.auth.service;

import com.example.auth.model.Token;
import com.example.auth.model.User;
import com.example.auth.model.UserDTO;
import com.example.auth.repository.TokenRepository;
import com.example.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                new ArrayList<>());
    }

    public User save(UserDTO user, String token) {
        User newUser = new User();
        newUser.setUsername(user.getUsername());
        newUser.setPassword(bCryptPasswordEncoder().encode(user.getPassword()));
        final Token tokenObj = new Token();
        tokenObj.setToken(token);
        tokenObj.setUser(newUser);
        tokenRepository.save(tokenObj);
        newUser.addToken(tokenObj);
        return userRepository.save(newUser);
    }

    public Optional<User> getUserByUsername(String username) {
        return Optional.ofNullable(userRepository.findByUsername(username));
    }

    public Token saveTokenToUser(String username, String token) {
        final User user = userRepository.findByUsername(username);
        final Token tokenObj = new Token();
        tokenObj.setToken(token);
        tokenObj.setUser(user);
        final Token storedToken = tokenRepository.save(tokenObj);
        user.addToken(tokenObj);
        userRepository.save(user);
        return storedToken;
    }

    public void removeTokenFromUser(String username, String token) {
        final User user = userRepository.findByUsername(username);
        System.out.println("User id:");
        System.out.println(user.getId());
        final Token storedToken = tokenRepository.findByToken(token);
        user.removeToken(storedToken);
        tokenRepository.delete(storedToken);
        userRepository.save(user);
    }
}
