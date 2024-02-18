package com.employee.management.controller;
import com.employee.management.DTO.AuthRequest;
import com.employee.management.exception.CompanyException;
import com.employee.management.exception.ResCodes;
import com.employee.management.util.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class Auth {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    JWTService jwtService;
    @PostMapping("/login")
    public String login(@RequestBody AuthRequest authRequest){
        Authentication authenticate = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(authRequest.getEmpId(), authRequest.getPassword()));
        if(authenticate.isAuthenticated())
            return jwtService.generateToken(authRequest.getEmpId());
        else throw new CompanyException(ResCodes.INVALID_ID_AND_PASSWORD);
    }
}
