package com.example.hng20.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.hng20.Models.Organisation;

import java.util.List;
import java.util.Optional;

import com.example.hng20.Models.User;
import com.example.hng20.Repositories.OrganisationRepository;

@Service
public class OrganisationService {
    @Autowired
    private OrganisationRepository organisationRepository;

    public Organisation createOrganisation(Organisation organisation) {
        return organisationRepository.save(organisation);
    }

    public List<Organisation> getUserOrganisations(User user) {
        return organisationRepository.findByUsersContaining(user);
    }

    public Optional<Organisation> findById(String id) {
        return organisationRepository.findById(id);
    }

    public Organisation addUserToOrganisation(Organisation organisation, User user) {
        organisation.getUsers().add(user);
        return organisationRepository.save(organisation);
    }
}
