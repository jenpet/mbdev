package pet.jen.mbdev.connectedvehicle.domain;

import lombok.Data;

@Data
public class Vehicle {
    private String id;
    private String licenseplate;
    private String salesdesignation;
    private String finorvin;
    private String modelyear;
    private String colorname;
    private String fueltype;
    private String powerhp;
    private String powerkw;
    private String numberofdoors;
    private String numberofseats;
}
