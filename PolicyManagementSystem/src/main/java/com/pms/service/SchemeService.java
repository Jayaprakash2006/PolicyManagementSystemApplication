package com.pms.service;

import com.pms.exception.InvalidEntityException;
import com.pms.entity.Scheme;
import com.pms.repository.SchemeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import java.util.List;
import java.util.Optional;

@Service
public class SchemeService {

    @Autowired
    private SchemeRepository schemeRepository;

    @Autowired
    private UserSchemeService userSchemeService;

    private static final Logger log = LoggerFactory.getLogger(SchemeService.class);

    public List<Scheme> getAllSchemes() {
        return schemeRepository.findAll();
    }

    public Optional<Scheme> getSchemeById(int id) throws InvalidEntityException {
        return Optional.ofNullable(schemeRepository.findById(id)
                .orElseThrow(() -> new InvalidEntityException("Scheme not found with ID: " + id)));
    }

//    public Scheme createScheme(Scheme scheme) {
//        scheme.setId(null); // force insert
//        log.info("Received payload for scheme creation: {}", scheme);
//        return schemeRepository.save(scheme);
//    }
    public Scheme createScheme(Scheme scheme) {
        scheme.setId(null);
        return schemeRepository.save(scheme);
    }



    public Scheme updateScheme(int id, Scheme schemeDetails) throws InvalidEntityException{
        return schemeRepository.findById(id).map(scheme -> {
            log.info("Received payload for scheme update: {} {}", id, schemeDetails);
            scheme.setSchemeDetails(schemeDetails.getSchemeDetails());
            scheme.setSchemeName(schemeDetails.getSchemeName());
            scheme.setDescription(schemeDetails.getDescription());
            scheme.setEligibilityCriteria(schemeDetails.getEligibilityCriteria());
            scheme.setBenefits(schemeDetails.getBenefits());
            Scheme updatedScheme = schemeRepository.save(scheme);
            log.info("Updated scheme: {}", updatedScheme);
            return updatedScheme;
        }).orElseThrow(() -> new InvalidEntityException("Scheme not found with id: " + id));
    }

    public void deleteScheme(int id) {
        schemeRepository.deleteById(id);
    }

    public Scheme setSchemeActiveStatus(int id, boolean isActive) throws InvalidEntityException {
        return schemeRepository.findById(id).map(scheme -> {
            scheme.setSchemeIsActive(isActive);
            return schemeRepository.save(scheme);
        }).orElseThrow(() -> new InvalidEntityException("Scheme not found with id: " + id));
    }

    public List<Scheme> getAllActiveSchemes(){
        return schemeRepository.findBySchemeIsActiveIsTrue();
    }

    public List<Scheme> getAllSchemesBySchemeName(String schemeName) {
        return schemeRepository.findSchemeBySchemeNameAndSchemeIsActiveIsTrue(schemeName);
    }
}
