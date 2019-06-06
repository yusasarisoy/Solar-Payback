package myusarisoy.solarhomesystem.Activity;

import android.content.Context;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;

import butterknife.BindView;
import myusarisoy.solarhomesystem.Fragment.FragmentSplash;
import myusarisoy.solarhomesystem.R;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.layout_main)
    LinearLayout layout_main;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction().add(R.id.layout_main, new FragmentSplash());
        fragmentTransaction.commit();
    }
}