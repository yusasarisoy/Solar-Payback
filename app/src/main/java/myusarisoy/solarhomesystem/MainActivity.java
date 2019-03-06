package myusarisoy.solarhomesystem;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;

import butterknife.BindView;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.layout_main)
    LinearLayout layout_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction().add(R.id.layout_main, new FragmentSplash());
        fragmentTransaction.commit();
    }
}