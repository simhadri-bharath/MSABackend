package com.bharath.msa.controller;

import com.bharath.msa.config.JwtUtil;
import com.bharath.msa.entity.Customer;
import com.bharath.msa.entity.Vendor;
import com.bharath.msa.repository.CustomerRepository;
import com.bharath.msa.repository.VendorRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/role-auth")
public class RoleAuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private VendorRepository vendorRepository;

    @PostMapping("/owner/login")
    public Map<String, Object> ownerLogin(@RequestBody Customer loginRequest) {
        if (!"msa@gmail.com".equalsIgnoreCase(loginRequest.getEmail())) {
            throw new RuntimeException("Unauthorized Owner Login");
        }
        return generateLoginResponse(loginRequest, "OWNER");
    }

    @PostMapping("/vendor/login")
    public ResponseEntity<?> vendorLogin(@RequestBody Customer loginRequest) {
    	
        List<Vendor> vendors = vendorRepository.findByName(loginRequest.getName());

        if (vendors == null || vendors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Vendor not found");
        }

        // If vendor exists, return the token (use internal method)
        Map<String, Object> response = generateLoginResponse(loginRequest, "VENDOR");
        System.out.println("Vendor login successful for: " + loginRequest.getName());

        return ResponseEntity.ok(response);
    }



    @PostMapping("/pharmacist/login")
    public Map<String, Object> pharmacistLogin(@RequestBody Customer loginRequest) {
        return generateLoginResponse(loginRequest, "PHARMACIST");
    }

    /**
     * Generates login response with JWT token and role.
     */
    private Map<String, Object> generateLoginResponse(Customer loginRequest, String role) {
        // Authenticate using Spring Security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        

        // Generate token with role
        String token = jwtUtil.generateTokenWithRole(userDetails, role);

        // Response payload
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("email", loginRequest.getEmail());
        response.put("role", role);
        return response;
    }

	
}
