package org.ide.dbp_proyecto.service;


import lombok.RequiredArgsConstructor;
import org.ide.dbp_proyecto.user.Role;
import org.ide.dbp_proyecto.repository.UserRepository;
import org.ide.dbp_proyecto.entity.User;
import org.springframework.security.core.Authentication;
import org.ide.dbp_proyecto.dto.AuthResponse;
import org.ide.dbp_proyecto.dto.LoginRequest;
import org.ide.dbp_proyecto.dto.RequestUser;
import org.ide.dbp_proyecto.dto.ResponseUser;
import org.ide.dbp_proyecto.event.UsuarioRegistradoEvent;
import org.ide.dbp_proyecto.exception.UserExitsException;
import org.ide.dbp_proyecto.jwt.JwtService;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder  passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final ApplicationEventPublisher eventPublisher;

    public ResponseUser RegisterUser(RequestUser requestUser) {

        if(userRepository.existsByName(requestUser.getName())){
            throw new UserExitsException("Nombre ya existente");
        }


        if(userRepository.existsByEmail(requestUser.getEmail())) {
            throw new UserExitsException("Email ya existente");
        }

        User user = modelMapper.map(requestUser, User.class);

        user.setPassword(
                passwordEncoder.encode(
                        requestUser.getPassword())
        );
        user.setRole(Role.USER);
        userRepository.save(user);

        // Publicar evento — listener async enviará email de bienvenida
        eventPublisher.publishEvent(new UsuarioRegistradoEvent(this, user));

        return modelMapper.map(user, ResponseUser.class);
    }


    public AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );


        UserDetails userDetails =
                userDetailsService.loadUserByUsername(
                        request.getEmail()
                );

        String token =
                jwtService.generateToken(userDetails);

        return new AuthResponse(token);
    }


    public ResponseUser getProfile(
            Authentication authentication
    ) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Usuario no encontrado"));

        return modelMapper.map(user, ResponseUser.class);
    }

    public ResponseUser updateProfile(
            Authentication authentication,
            RequestUser request
    ) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Usuario no encontrado"));

        user.setName(request.getName());

        user.setEmail(request.getEmail());

        if(request.getPassword() != null &&
                !request.getPassword().isBlank()) {

            user.setPassword(
                    passwordEncoder.encode(
                            request.getPassword()
                    )
            );
        }

        userRepository.save(user);

        return modelMapper.map(user, ResponseUser.class);
    }
}