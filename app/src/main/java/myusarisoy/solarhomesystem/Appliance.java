package myusarisoy.solarhomesystem;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Data
public class Appliance {
    private int applianceChecked;
    private int imageResource;
    private String appliance;
}
