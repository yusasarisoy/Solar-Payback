package myusarisoy.solarhomesystem;

import android.graphics.drawable.Drawable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Data
public class Appliance {
    private Drawable applianceChecked;
    private Drawable imageResource;
    private String appliance;
}
