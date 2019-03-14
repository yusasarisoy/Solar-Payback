package myusarisoy.solarhomesystem;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApplianceOverview {
    private int imageResource;
    private String appliance;
    private int powerConsumption;
}
