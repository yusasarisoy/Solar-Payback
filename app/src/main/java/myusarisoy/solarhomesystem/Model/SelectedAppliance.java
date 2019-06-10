package myusarisoy.solarhomesystem.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class SelectedAppliance {
    private int imageResource;
    private String appliance;
}
