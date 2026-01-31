package com.pms.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pms.entity.BoughtPolicy;
import com.pms.entity.Customer;
import com.pms.entity.Policy;
import com.pms.entity.Scheme;
import com.pms.exception.InvalidEntityException;
import com.pms.service.PolicyService;

@RestController
@RequestMapping("/policy")
@CrossOrigin(origins = "http://localhost:8031")
public class PolicyController {
    
    @Autowired
    private PolicyService service;
    
    @GetMapping("/viewPolicies")
    public List<Policy> viewPolicies(){
        return service.viewPolicies();
    }

    @PostMapping("/viewPolicyDetails")
    public Optional<Policy> viewPolicyDetails(@RequestBody Policy p){
        return service.viewPolicyDetails(p);
    }
    
//    @PostMapping("/viewCustPolicies")
//    public List<BoughtPolicy> viewCustPolicies(@RequestBody Customer cust){
//        return service.viewCustPolicies(cust.getId());
//    }
        @PostMapping("/viewCustPolicies")
        public List<BoughtPolicy> viewCustPolicies(@RequestParam String customerId) {
            return service.viewCustPolicies(customerId);
        }


    @PostMapping("/viewSchemePolicies")
    public List<Policy> viewSchemePolicies(@RequestBody Scheme sc){
        return service.viewSchemePolicies(sc.getId());
    }
    
    @PostMapping("/create")
    public ResponseEntity<Policy> createPolicy(@RequestBody Policy policy) throws InvalidEntityException {
        Policy createdPolicy = service.createPolicy(policy);
        return ResponseEntity.ok(createdPolicy);
    }
    
    @PutMapping("/update/{id}")
    public ResponseEntity<Policy> updatePolicy(@PathVariable String id, 
                                               @RequestBody Policy policy) throws InvalidEntityException {
        Policy updatedPolicy = service.updatePolicy(id, policy);
        return ResponseEntity.ok(updatedPolicy);
    }
    
    @GetMapping("/{policyId}")
    public ResponseEntity<Policy> getPolicyByPolicyId(@PathVariable String policyId) throws InvalidEntityException {
        Policy policy = service.getPolicyByPolicyId(policyId);
        return ResponseEntity.ok(policy);
    }
    
    @GetMapping("/scheme/{schemeName}")
    public ResponseEntity<List<Policy>> getPoliciesByScheme(@PathVariable String schemeName) {
        List<Policy> policies = service.getPoliciesBySchemeName(schemeName);
        return ResponseEntity.ok(policies);
    }
    
    @GetMapping("/date/{date}")
    public ResponseEntity<List<Policy>> getPoliciesByDate(@PathVariable String date) {
        LocalDate d = LocalDate.parse(date);
        List<Policy> policies = service.getPoliciesByStartDate(d);
        return ResponseEntity.ok(policies);
    }
    
    @GetMapping("/premium")
    public ResponseEntity<List<Policy>> getPoliciesByPremium(@RequestParam Double min,
                                                             @RequestParam Double max) {
        List<Policy> policies = service.getPoliciesByPremiumAmountRange(min, max);
        return ResponseEntity.ok(policies);
    }
    
    @GetMapping("/maturity")
    public ResponseEntity<List<Policy>> getPoliciesByMaturity(@RequestParam Double min,
                                                              @RequestParam Double max) {
        List<Policy> policies = service.getPoliciesByMaturityAmountRange(min, max);
        return ResponseEntity.ok(policies);
    }
    
    @GetMapping("/terms/{term}")
    public ResponseEntity<List<Policy>> getPoliciesByPolicyTerm(@PathVariable Integer term) {
        List<Policy> policies = service.getPoliciesByPolicyTerm(term);
        return ResponseEntity.ok(policies);
    }
    
    // New endpoint to support POST with request body for buying a policy.
    @PostMapping("/buy")
    public ResponseEntity<BoughtPolicy> buyPolicy(@RequestBody BoughtPolicy boughtPolicy) throws InvalidEntityException {
        String policyId = boughtPolicy.getPolicy().getPolicyId();
        String customerId = boughtPolicy.getCustomer().getId();
        BoughtPolicy savedPolicy = service.buyPolicy(policyId, customerId);
        return ResponseEntity.ok(savedPolicy);
    }

    @GetMapping("/bought/customer/{customerId}")
    public ResponseEntity<BoughtPolicy[]> getBoughtPoliciesByCustomerId(@PathVariable String customerId) throws InvalidEntityException {
        BoughtPolicy[] policies = service.getBoughtPoliciesByCustomerId(customerId);
        return ResponseEntity.ok(policies);
    }
    
    @GetMapping("/bought/scheme/{schemeName}")
    public ResponseEntity<BoughtPolicy[]> getBoughtPoliciesByScheme(@PathVariable String schemeName) throws InvalidEntityException {
        BoughtPolicy[] policies = service.getBoughtPoliciesByScheme(schemeName);
        return ResponseEntity.ok(policies);
    }
    
    @GetMapping("/bought/term/{policyTerm}")
    public ResponseEntity<BoughtPolicy[]> getBoughtPoliciesByTerm(@PathVariable Integer policyTerm) throws InvalidEntityException {
        BoughtPolicy[] policies = service.getBoughtPoliciesByTerm(policyTerm);
        return ResponseEntity.ok(policies);
    }
    
    @GetMapping("/bought/maturity")
    public ResponseEntity<BoughtPolicy[]> getBoughtPoliciesByMaturityRange(@RequestParam("min") Double min,
                                                                           @RequestParam("max") Double max) throws InvalidEntityException {
        BoughtPolicy[] policies = service.getBoughtPoliciesByMaturityRange(min, max);
        return ResponseEntity.ok(policies);
    }
    
    @GetMapping("/bought/premium")
    public ResponseEntity<BoughtPolicy[]> getBoughtPoliciesByPremiumRange(@RequestParam("min") Double min,
                                                                          @RequestParam("max") Double max) throws InvalidEntityException {
        BoughtPolicy[] policies = service.getBoughtPoliciesByPremiumRange(min, max);
        return ResponseEntity.ok(policies);
    }
    
    @GetMapping("/bought/{policyId}")
    public ResponseEntity<BoughtPolicy> getBoughtPoliciesByPolicyId(@PathVariable Long policyId ) throws InvalidEntityException {
    		BoughtPolicy policies = service.getBoughtPoliciesByPolicyId(policyId);
    		return ResponseEntity.ok(policies);
    }
}
