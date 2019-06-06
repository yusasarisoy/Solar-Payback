package myusarisoy.solarhomesystem.Data;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ApplianceOverviewItem {
    private int image;
    private String name;
    private int powerConsumption;
}
