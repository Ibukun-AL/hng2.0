package com.example.hng20.Models;

import lombok.Data;

@Data
public class OrganisationDTO {
    private String orgId;
    private String name;
    private String description;
    // Getters and setters

    public OrganisationDTO(Organisation org) {
        this.orgId = org.getOrgId();
        this.name = org.getName();
        this.description = org.getDescription();
        // Map other fields
    }
}
