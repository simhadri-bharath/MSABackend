package com.bharath.msa.controller;

import com.bharath.msa.config.JwtUtil;
import com.bharath.msa.entity.Customer;
import com.bharath.msa.service.CustomerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
//@CrossOrigin("http://localhost:3000/")
public class customerController {
	
	@Autowired
	CustomerServiceImpl customerServiceImpl;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	 @Autowired
	    private AuthenticationManager authenticationManager;

	    @Autowired
	    private JwtUtil jwtUtil;
	
//		@PostMapping("/customer")
//		public ResponseEntity<Customer> addCustomer(@RequestBody Customer customer) {
//			 return new ResponseEntity<>(customerServiceImpl.addCustomer(customer),HttpStatus.CREATED);
//			 
//		}
	
	    @PostMapping("/customer")
	    public ResponseEntity<Customer> addCustomer(@RequestBody Customer customer) {
	        // Encode the password before saving
	        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
	        return new ResponseEntity<>(customerServiceImpl.addCustomer(customer), HttpStatus.CREATED);
	    }

		
		 @GetMapping("/customer")
		    public List<Customer> getAllCustomers() {
		        return customerServiceImpl.getAllCustomers();
		    }
		 
		    @GetMapping("/customer/{id}")
		    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
		        return customerServiceImpl.getCustomerById(id)
		                .map(customer -> new ResponseEntity<>(customer, HttpStatus.OK))
		                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
		    }
		    @DeleteMapping("/customer/{id}")
		    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
		        customerServiceImpl.deleteCustomer(id);
		        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		    }
		    @PutMapping("/customer/{id}")
		    public ResponseEntity<Customer> updateCustomer(@PathVariable Long id, @RequestBody Customer customerDetails) {
		        if (customerDetails.getPassword() != null && !customerDetails.getPassword().isEmpty()) {
		            customerDetails.setPassword(passwordEncoder.encode(customerDetails.getPassword()));
		        }
		        Customer updatedCustomer = customerServiceImpl.updateCustomer(id, customerDetails);
		        return new ResponseEntity<>(updatedCustomer, HttpStatus.OK);
		    }
//		    @PutMapping("/customer/{id}")
//		    public ResponseEntity<Customer> updateCustomer(@PathVariable Long id, @RequestBody Customer customerDetails) {
//		        Customer updatedCustomer = customerServiceImpl.updateCustomer(id, customerDetails);
//		        return new ResponseEntity<>(updatedCustomer, HttpStatus.OK);
//		    }
		    
//		    @PostMapping("/customer/verify")
//		    public ResponseEntity<String> verifyCustomer(@RequestParam String email, @RequestParam String password) {
//		        boolean exists = customerServiceImpl.verifyCustomer(email, password);
//		        if (exists) {
//		            return ResponseEntity.ok("true");
//		        } else {
//		            return ResponseEntity.ok("false");
//		        }
//		    }
		    
		    @PostMapping("/customer/verify")
		    public ResponseEntity<Map<String, Object>> verifyCustomer(@RequestParam String email, @RequestParam String password) {
		        Optional<Customer> customer = customerServiceImpl.verifyCustomer(email, password);

		        Map<String, Object> response = new HashMap<>();
		        if (customer.isPresent()) {
		            response.put("success", "true");
		            response.put("customer", customer.get());
		        } else {
		            response.put("success", "false");
		            response.put("customer", null);
		        }
		        return ResponseEntity.ok(response);
		    }

		    @PostMapping("/customer/login")
		    public ResponseEntity<Map<String, Object>> login(@RequestParam String email, @RequestParam String password) {
		        Map<String, Object> response = new HashMap<>();
		        try {
		            // Authenticate the user
		            authenticationManager.authenticate(
		                new UsernamePasswordAuthenticationToken(email, password)
		            );

		            // Fetch customer details from DB
		            Customer customer = customerServiceImpl.findByEmail(email);
		            if (customer != null) {
		                customer.setPassword(null); // Do not expose password
		            }

		            // Generate JWT token
		            String token = jwtUtil.generateToken(
		                    org.springframework.security.core.userdetails.User.builder()
		                            .username(email)
		                            .password(password)
		                            .roles("USER")
		                            .build()
		            );

		            response.put("success", true);
		            response.put("token", token);
		            response.put("customer", customer);

		            return ResponseEntity.ok(response);
		        } catch (Exception e) {
		            response.put("success", false);
		            response.put("message", "Invalid email or password");
		            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
		        }
		    }

//		    @PostMapping("/customer/{customerId}/orders")
//		    public ResponseEntity<Order> createOrder(@PathVariable Long customerId, @RequestBody Order order) {
//		        Order createdOrder = customerServiceImpl.createOrder(customerId, order);
//		        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
//		    }
//	
//		    @GetMapping("/customer/{customerId}/orders")
//		    public List<Order> getOrdersForCustomer(@PathVariable Long customerId) {
//		        return customerServiceImpl.getOrdersForCustomer(customerId);
//		    }

}
