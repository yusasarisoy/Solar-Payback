package myusarisoy.solarhomesystem;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApplianceOverview {
    private String monthName;
    private int monthPayment;
    private int monthPowerConsumption;
}
