package myusarisoy.solarhomesystem;

import android.content.Context;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;

import butterknife.BindView;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.layout_main)
    LinearLayout layout_main;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Places.initialize(getApplicationContext(), "AIzaSyB8-Uz4rjP3l30iUUJVc1mXP3DzoMpcYhs");
        PlacesClient placesClient = Places.createClient(getApplicationContext());

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction().add(R.id.layout_main, new FragmentSplash());
        fragmentTransaction.commit();
    }
}