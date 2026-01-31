package com.pms.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.pms.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.pms.entity.Policy.PolicyStatus;
import com.pms.exception.InvalidEntityException;
import com.pms.repository.BoughtPolicyRepository;
import com.pms.repository.CustomerRepository;
import com.pms.repository.PolicyRepository;
import com.pms.repository.SchemeRepository;

@Service
public class PolicyService {

    @Autowired
    private PolicyRepository repo;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private CustomerRepository customerRepo;
    
    @Autowired
    private SchemeRepository schemeRepo;
    
    @Autowired
    private BoughtPolicyRepository boughtPolicyRepo;

    public List<Policy> viewPolicies() {
        return repo.findAll();
    }

    public Optional<Policy> viewPolicyDetails(Policy p) {
        return repo.findByPolicyId(p.getPolicyId());
    }

    public List<BoughtPolicy> viewCustPolicies(String id) {
        return boughtPolicyRepo.findByCustomerId(id);
    }

    public List<Policy> viewSchemePolicies(Integer id) {
        return repo.findBySchemeId(id);
    }
    
    public Policy createPolicy(Policy policy) throws InvalidEntityException {
        // Remove the customer check, since policy may be created without a customer
        if (policy.getScheme() == null || policy.getScheme().getId() == null ||
            !schemeRepo.existsById(policy.getScheme().getId())) {
            throw new InvalidEntityException("Scheme not found with id " +
                (policy.getScheme() != null ? policy.getScheme().getId() : "null"));
        }
        
        if (policy.getStartDate() == null) {
            throw new IllegalArgumentException("Start date must be provided and be a valid date.");
        }
        if (policy.getTotalPremiumAmount() == null || policy.getTotalPremiumAmount() <= 0) {
            throw new IllegalArgumentException("Total premium amount must be a positive number.");
        }
        if (policy.getMaturityAmount() == null || policy.getMaturityAmount() <= 0) {
            throw new IllegalArgumentException("Maturity amount must be a positive number.");
        }
        if (policy.getPolicyTerm() == null || policy.getPolicyTerm() <= 0) {
            throw new IllegalArgumentException("Policy Term must be a positive number.");
        }
        if (policy.getPolicyStatus() == null ||
            (policy.getPolicyStatus() != PolicyStatus.ACTIVE &&
             policy.getPolicyStatus() != PolicyStatus.INACTIVE)) {
            throw new IllegalArgumentException("Policy status must be either ACTIVE or INACTIVE.");
        }
        if (policy.getAnnuityTerm() == null ||
            (policy.getAnnuityTerm() != AnnuityTerm.QUARTERLY &&
             policy.getAnnuityTerm() != AnnuityTerm.HALF_YEARLY &&
             policy.getAnnuityTerm() != AnnuityTerm.ANNUAL &&
             policy.getAnnuityTerm() != AnnuityTerm.ONE_TIME)) {
            throw new IllegalArgumentException("Annuity Term must be one of: QUARTERLY, HALF_YEARLY, ANNUAL, ONE_TIME.");
        }

        Policy lastPolicy = repo.findTopByOrderByPolicyIdDesc();
        String newPolicyId;
        if (lastPolicy == null || lastPolicy.getPolicyId() == null) {
            newPolicyId = "POLICY001";
        } else {
            String lastId = lastPolicy.getPolicyId();
            int lastNumber = Integer.parseInt(lastId.substring("POLICY".length()));
            int newNumber = lastNumber + 1;
            newPolicyId = String.format("POLICY%03d", newNumber);
        }

        policy.setPolicyId(newPolicyId);
        return repo.save(policy);
    }

    public Policy updatePolicy(String id, Policy policyDetails) throws InvalidEntityException {
        Policy existingPolicy = repo.findByPolicyId(id)
                .orElseThrow(() -> new InvalidEntityException("Policy not found with id " + id));

        // Disallow updates for certain fields.
        if (policyDetails.getScheme() != null) {
            throw new IllegalArgumentException("Scheme field is not allowed in policy update.");
        }
        if (policyDetails.getPolicyId() != null) {
            throw new IllegalArgumentException("Policy ID field is not allowed in policy update.");
        }

        if (policyDetails.getStartDate() != null) {
            existingPolicy.setStartDate(policyDetails.getStartDate());
        }

        if (policyDetails.getTotalPremiumAmount() != null) {
            if (policyDetails.getTotalPremiumAmount() <= 0) {
                throw new IllegalArgumentException("Total premium amount must be a positive number.");
            }
            existingPolicy.setTotalPremiumAmount(policyDetails.getTotalPremiumAmount());
        }

        if (policyDetails.getMaturityAmount() != null) {
            if (policyDetails.getMaturityAmount() <= 0) {
                throw new IllegalArgumentException("Maturity amount must be a positive number.");
            }
            existingPolicy.setMaturityAmount(policyDetails.getMaturityAmount());
        }

        if (policyDetails.getPolicyTerm() != null) {
            if (policyDetails.getPolicyTerm() <= 0) {
                throw new IllegalArgumentException("Policy Term must be a positive number.");
            }
            existingPolicy.setPolicyTerm(policyDetails.getPolicyTerm());
        }

        if (policyDetails.getPolicyStatus() != null) {
            String statusStr = policyDetails.getPolicyStatus().toString();
            if (!(statusStr.equalsIgnoreCase("ACTIVE") || statusStr.equalsIgnoreCase("INACTIVE"))) {
                throw new IllegalArgumentException("Policy status must be either ACTIVE or INACTIVE.");
            }
            PolicyStatus newStatus = PolicyStatus.valueOf(statusStr.toUpperCase());
            existingPolicy.setPolicyStatus(newStatus);
        }

        if (policyDetails.getAnnuityTerm() != null) {
            String termStr = policyDetails.getAnnuityTerm().toString();
            if (!(termStr.equalsIgnoreCase("QUARTERLY") ||
                  termStr.equalsIgnoreCase("HALF_YEARLY") ||
                  termStr.equalsIgnoreCase("ANNUAL") ||
                  termStr.equalsIgnoreCase("ONE_TIME"))) {
                throw new IllegalArgumentException("Annuity Term must be one of: QUARTERLY, HALF_YEARLY, ANNUAL, ONE_TIME.");
            }
            AnnuityTerm newTerm = AnnuityTerm.valueOf(termStr.toUpperCase());
            existingPolicy.setAnnuityTerm(newTerm);
        }

        return repo.save(existingPolicy);
    }
    
    public Policy getPolicyByPolicyId(String policyId) throws InvalidEntityException {
        return repo.findByPolicyId(policyId)
                   .orElseThrow(() -> new InvalidEntityException("Policy not found with id " + policyId));
    }
    
    public List<Policy> getPoliciesBySchemeName(String schemeName) {
        return repo.findBySchemeSchemeName(schemeName);
    }
    
    public List<Policy> getPoliciesByStartDate(LocalDate startDate) {
        return repo.findByStartDate(startDate);
    }
    
    public List<Policy> getPoliciesByPremiumAmountRange(Double min, Double max) {
        return repo.findByTotalPremiumAmountBetween(min, max);
    }
    
    public List<Policy> getPoliciesByMaturityAmountRange(Double min, Double max) {
        return repo.findByMaturityAmountBetween(min, max);
    }
    
    public List<Policy> getPoliciesByPolicyTerm(Integer term) {
        return repo.findByPolicyTerm(term);
    }
    
    // Updated buyPolicy method that sets the entire policy object in BoughtPolicy.
    public BoughtPolicy buyPolicy(String policyId, String customerId) throws InvalidEntityException {
        Policy policy = repo.findByPolicyId(policyId)
                .orElseThrow(() -> new InvalidEntityException("Policy not found with id " + policyId));

        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new InvalidEntityException("Customer not found with id " + customerId));

        BoughtPolicy boughtPolicy = new BoughtPolicy();
        boughtPolicy.setCustomer(customer);
        // Set the policy object so that all policy details are captured.
        boughtPolicy.setPolicy(policy);
        boughtPolicy.setStartDate(policy.getStartDate());
        boughtPolicy.setTotalPremiumAmount(policy.getTotalPremiumAmount());
        boughtPolicy.setMaturityAmount(policy.getMaturityAmount());
        boughtPolicy.setPolicyTerm(policy.getPolicyTerm());
        boughtPolicy.setPolicyStatus(policy.getPolicyStatus());
        boughtPolicy.setAnnuityTerm(policy.getAnnuityTerm());

        return boughtPolicyRepo.save(boughtPolicy);

    }
    
    // Updated updateBoughtPolicy method using the proper identifier field and allowed fields.
    public BoughtPolicy updateBoughtPolicy(Long id, BoughtPolicy boughtPolicyDetails) throws InvalidEntityException {
        // Retrieve the existing bought policy record by its id.
        BoughtPolicy existingBoughtPolicy = boughtPolicyRepo.findById(id)
                .orElseThrow(() -> new InvalidEntityException("Bought Policy not found with id " + id));

        // Disallow updates to fields that should remain unchanged.
        if (boughtPolicyDetails.getCustomer() != null) {
            throw new IllegalArgumentException("Customer field is not allowed in bought policy update.");
        }
        // Disallow update of the bought policy identifier.
        if (boughtPolicyDetails.getBoughtPolicyId() != null) {
            throw new IllegalArgumentException("Bought Policy ID field is not allowed in update.");
        }

        // Update allowed fields.
        if (boughtPolicyDetails.getStartDate() != null) {
            existingBoughtPolicy.setStartDate(boughtPolicyDetails.getStartDate());
        }

        if (boughtPolicyDetails.getTotalPremiumAmount() != null) {
            if (boughtPolicyDetails.getTotalPremiumAmount() <= 0) {
                throw new IllegalArgumentException("Total premium amount must be a positive number.");
            }
            existingBoughtPolicy.setTotalPremiumAmount(boughtPolicyDetails.getTotalPremiumAmount());
        }

        if (boughtPolicyDetails.getMaturityAmount() != null) {
            if (boughtPolicyDetails.getMaturityAmount() <= 0) {
                throw new IllegalArgumentException("Maturity amount must be a positive number.");
            }
            existingBoughtPolicy.setMaturityAmount(boughtPolicyDetails.getMaturityAmount());
        }

        if (boughtPolicyDetails.getPolicyTerm() != null) {
            if (boughtPolicyDetails.getPolicyTerm() <= 0) {
                throw new IllegalArgumentException("Policy term must be a positive number.");
            }
            existingBoughtPolicy.setPolicyTerm(boughtPolicyDetails.getPolicyTerm());
        }

        if (boughtPolicyDetails.getPolicyStatus() != null) {
            String statusStr = boughtPolicyDetails.getPolicyStatus().toString();
            if (!(statusStr.equalsIgnoreCase("ACTIVE") || statusStr.equalsIgnoreCase("INACTIVE"))) {
                throw new IllegalArgumentException("Policy status must be either ACTIVE or INACTIVE.");
            }
            existingBoughtPolicy.setPolicyStatus(Policy.PolicyStatus.valueOf(statusStr.toUpperCase()));
        }

        if (boughtPolicyDetails.getAnnuityTerm() != null) {
            String termStr = boughtPolicyDetails.getAnnuityTerm().toString();
            if (!(termStr.equalsIgnoreCase("QUARTERLY") ||
                  termStr.equalsIgnoreCase("HALF_YEARLY") ||
                  termStr.equalsIgnoreCase("ANNUAL") ||
                  termStr.equalsIgnoreCase("ONE_TIME"))) {
                throw new IllegalArgumentException("Annuity Term must be one of: QUARTERLY, HALF_YEARLY, ANNUAL, ONE_TIME.");
            }
            existingBoughtPolicy.setAnnuityTerm(
                    com.pms.entity.AnnuityTerm.valueOf(termStr.toUpperCase())
            );
        }

        return boughtPolicyRepo.save(existingBoughtPolicy);
    }
    

    public BoughtPolicy[] getBoughtPoliciesByCustomerId(String customerId) throws InvalidEntityException {
        List<BoughtPolicy> policies = boughtPolicyRepo.findByCustomerId(customerId);
        // Throw an exception if the list is empty
        List<BoughtPolicy> validPolicies = Optional.ofNullable(policies)
            .filter(list -> !list.isEmpty())
            .orElseThrow(() -> new InvalidEntityException("No policies found for customer id: " + customerId));
        return validPolicies.toArray(new BoughtPolicy[0]);
    }

    public BoughtPolicy[] getBoughtPoliciesByScheme(String schemeName) throws InvalidEntityException {
        List<BoughtPolicy> policies = boughtPolicyRepo.findBySchemeName(schemeName);
        List<BoughtPolicy> validPolicies = Optional.ofNullable(policies)
            .filter(list -> !list.isEmpty())
            .orElseThrow(() -> new InvalidEntityException("No policies found for scheme: " + schemeName));
        return validPolicies.toArray(new BoughtPolicy[0]);
    }

    public BoughtPolicy[] getBoughtPoliciesByTerm(Integer policyTerm) throws InvalidEntityException {
        List<BoughtPolicy> policies = boughtPolicyRepo.findByPolicyTerm(policyTerm);
        List<BoughtPolicy> validPolicies = Optional.ofNullable(policies)
            .filter(list -> !list.isEmpty())
            .orElseThrow(() -> new InvalidEntityException("No policies found for term: " + policyTerm));
        return validPolicies.toArray(new BoughtPolicy[0]);
    }

    public BoughtPolicy[] getBoughtPoliciesByMaturityRange(Double min, Double max) throws InvalidEntityException {
        List<BoughtPolicy> policies = boughtPolicyRepo.findByMaturityAmountBetween(min, max);
        List<BoughtPolicy> validPolicies = Optional.ofNullable(policies)
            .filter(list -> !list.isEmpty())
            .orElseThrow(() -> new InvalidEntityException("No policies found in maturity range: " + min + " to " + max));
        return validPolicies.toArray(new BoughtPolicy[0]);
    }

    public BoughtPolicy[] getBoughtPoliciesByPremiumRange(Double min, Double max) throws InvalidEntityException {
        List<BoughtPolicy> policies = boughtPolicyRepo.findByTotalPremiumAmountBetween(min, max);
        List<BoughtPolicy> validPolicies = Optional.ofNullable(policies)
            .filter(list -> !list.isEmpty())
            .orElseThrow(() -> new InvalidEntityException("No policies found in premium range: " + min + " to " + max));
        return validPolicies.toArray(new BoughtPolicy[0]);
    }

    public BoughtPolicy getBoughtPoliciesByPolicyId(Long policyId) throws InvalidEntityException {
        return boughtPolicyRepo.findById(policyId)
            .orElseThrow(() -> new InvalidEntityException("Policy with id " + policyId + " not found"));
    }

}
