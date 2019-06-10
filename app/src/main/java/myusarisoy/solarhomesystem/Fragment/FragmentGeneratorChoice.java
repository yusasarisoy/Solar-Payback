package myusarisoy.solarhomesystem.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import butterknife.BindView;
import myusarisoy.solarhomesystem.R;

public class FragmentGeneratorChoice extends Fragment {
    @BindView(R.id.generator1Layout)
    LinearLayout generator1;

    @BindView(R.id.generator2Layout)
    LinearLayout generator2;

    int panelPrice, totalPayment, generatorPrice1 = 15000, generatorPrice2 = 75000, lowerProduction;
    String grid;
    View view;

    public static FragmentGeneratorChoice newInstance(Object... objects) {
        FragmentGeneratorChoice fragment = new FragmentGeneratorChoice();
        Bundle args = new Bundle();
        args.putInt("priceOfPanel", (Integer) objects[0]);
        args.putInt("TotalPayment", (Integer) objects[1]);
        args.putString("Grid", (String) objects[2]);
        args.putInt("lowerProduction", (Integer) objects[3]);
        args.putInt("panels", (Integer) objects[4]);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_generator_choice, container, false);

        panelPrice = getArguments().getInt("priceOfPanel");
        totalPayment = getArguments().getInt("TotalPayment");
        grid = getArguments().getString("Grid");
        lowerProduction = getArguments().getInt("lowerProduction");

//        Go to battery calculation.
        gotoBatteryCalculation();

        return view;
    }

    private void gotoBatteryCalculation() {
        generator1 = view.findViewById(R.id.generator1Layout);
        generator2 = view.findViewById(R.id.generator2Layout);

        generator1.setOnClickListener(v -> {
            panelPrice = panelPrice + generatorPrice1;
            FragmentBatteryCalculation fragmentBatteryCalculation = new FragmentBatteryCalculation();
            Bundle bundle = new Bundle();
            bundle.putInt("priceOfPanel", panelPrice);
            bundle.putInt("TotalPayment", totalPayment);
            bundle.putString("Grid", grid);
            bundle.putInt("lowerProduction", lowerProduction);
            bundle.putInt("panels", getArguments().getInt("panels"));
            fragmentBatteryCalculation.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.layoutMain, fragmentBatteryCalculation, "FragmentBatteryCalculation")
                    .commit();
        });

        generator2.setOnClickListener(v -> {
            panelPrice = panelPrice + generatorPrice2;
            FragmentBatteryCalculation fragmentBatteryCalculation = new FragmentBatteryCalculation();
            Bundle bundle = new Bundle();
            bundle.putInt("priceOfPanel", panelPrice);
            bundle.putInt("TotalPayment", totalPayment);
            bundle.putString("Grid", grid);
            bundle.putInt("lowerProduction", lowerProduction);
            bundle.putInt("panels", getArguments().getInt("panels"));
            fragmentBatteryCalculation.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.layoutMain, fragmentBatteryCalculation, "FragmentBatteryCalculation")
                    .commit();
        });
    }
}