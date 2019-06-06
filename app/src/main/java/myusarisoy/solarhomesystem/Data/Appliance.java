package myusarisoy.solarhomesystem.Data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Data
public class Appliance {
    private boolean check;
    private int imageResource;
    private String appliance;
}
