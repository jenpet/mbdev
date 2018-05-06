package pet.jen.mbdev.connectedvehicle;

import lombok.Data;

/**
 * POJO representation of vehicle returned via JSON from the API.
 *
 * @author Jens Petersohn <me@jen.pet>
 */
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
