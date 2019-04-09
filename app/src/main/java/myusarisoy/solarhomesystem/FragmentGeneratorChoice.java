package myusarisoy.solarhomesystem;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import butterknife.BindView;

public class FragmentGeneratorChoice extends Fragment {
    @BindView(R.id.img_generator_1)
    ImageView generator1;

    @BindView(R.id.img_generator_2)
    ImageView generator2;

    int panelPrice, totalPayment;
    View view;

    public static FragmentGeneratorChoice newInstance(Object... objects) {
        FragmentGeneratorChoice fragment = new FragmentGeneratorChoice();
        Bundle args = new Bundle();
        args.putInt("panelPrice", (Integer) objects[0]);
        args.putInt("TotalPayment", (Integer) objects[1]);
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

        panelPrice = getArguments().getInt("panelPrice");
        totalPayment = getArguments().getInt("TotalPayment");

        gotoBatteryCalculation();

        return view;
    }

    private void gotoBatteryCalculation() {
        generator1 = view.findViewById(R.id.img_generator_1);
        generator2 = view.findViewById(R.id.img_generator_2);

        generator1.setOnClickListener(v -> {
            totalPayment += 121896;
            FragmentBatteryCalculation fragmentBatteryCalculation = new FragmentBatteryCalculation();
            Bundle bundle = new Bundle();
            bundle.putInt("panelPrice", panelPrice);
            bundle.putInt("TotalPayment", totalPayment);
            fragmentBatteryCalculation.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.layout_main, fragmentBatteryCalculation, "FragmentBatteryCalculation")
                    .commit();
        });

        generator2.setOnClickListener(v -> {
            totalPayment += 72493;
            FragmentBatteryCalculation fragmentBatteryCalculation = new FragmentBatteryCalculation();
            Bundle bundle = new Bundle();
            bundle.putInt("panelPrice", panelPrice);
            bundle.putInt("TotalPayment", totalPayment);
            fragmentBatteryCalculation.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.layout_main, fragmentBatteryCalculation, "FragmentBatteryCalculation")
                    .commit();
        });
    }
}