package com.example.hng20.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

import com.example.hng20.Models.Organisation;
import com.example.hng20.Models.User;

public interface OrganisationRepository extends JpaRepository<Organisation, String> {
    List<Organisation> findByUsersContaining(User user);


    @Query("SELECT o FROM Organisation o LEFT JOIN FETCH o.users")
    List<Organisation> findAllWithUsers();

}
