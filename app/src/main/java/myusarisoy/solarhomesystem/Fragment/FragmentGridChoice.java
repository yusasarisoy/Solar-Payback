package myusarisoy.solarhomesystem.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import butterknife.BindView;
import myusarisoy.solarhomesystem.R;

public class FragmentGridChoice extends Fragment {
    @BindView(R.id.imgOnGrid)
    ImageView onGrid;

    @BindView(R.id.imgOffGrid)
    ImageView offGrid;

    public ArrayList<String> stringArray = new ArrayList<>();
    public ArrayList<Integer> integerArray = new ArrayList<>();
    public ArrayList<Integer> integerArray2 = new ArrayList<>();
    public String cityLocation;
    public double irradianceLocation;
    View view;

    public static FragmentGridChoice newInstance(Object... objects) {
        FragmentGridChoice fragment = new FragmentGridChoice();
        Bundle args = new Bundle();
        args.putString("choice", (String) objects[0]);
        args.putStringArrayList("stringArray", (ArrayList<String>) objects[1]);
        args.putIntegerArrayList("integerArray", (ArrayList<Integer>) objects[2]);
        args.putIntegerArrayList("integerArray2", (ArrayList<Integer>) objects[3]);
        args.putString("city", (String) objects[4]);
        args.putDouble("irradiance", (Double) objects[5]);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_grid_choice, container, false);

        if (getArguments().getString("choice").equals("bill")) {
            stringArray = getArguments().getStringArrayList("MonthName");
            integerArray = getArguments().getIntegerArrayList("MonthPayment");
            integerArray2 = getArguments().getIntegerArrayList("MonthPowerConsumption");
        } else if (getArguments().getString("choice").equals("appliance")) {
            stringArray = getArguments().getStringArrayList("AppliancesName");
            integerArray = getArguments().getIntegerArrayList("AppliancesImage");
            integerArray2 = getArguments().getIntegerArrayList("AppliancesConsumption");
        }

        cityLocation = getArguments().getString("City");
        irradianceLocation = getArguments().getDouble("CityIrradiance");

//        Grid choices.
        gridChoice();

        return view;
    }

    private void gridChoice() {
        onGrid = view.findViewById(R.id.imgOnGrid);
        offGrid = view.findViewById(R.id.imgOffGrid);

        onGrid.setOnClickListener(v -> {
            if (getArguments().getString("choice").equals("bill")) {
                FragmentOverview fragmentOverview = new FragmentOverview();
                Bundle bundle = new Bundle();
                bundle.putString("Grid", "On-Grid");
                bundle.putStringArrayList("stringArray", stringArray);
                bundle.putIntegerArrayList("integerArray", integerArray);
                bundle.putIntegerArrayList("integerArray2", integerArray2);
                bundle.putString("City", cityLocation);
                bundle.putDouble("CityIrradiance", irradianceLocation);
                fragmentOverview.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.layoutMain, fragmentOverview, "FragmentOverview")
                        .commit();
            } else if (getArguments().getString("choice").equals("appliance")) {
                FragmentOverviewAppliances fragmentOverviewAppliances = new FragmentOverviewAppliances();
                Bundle bundle = new Bundle();
                bundle.putString("Grid", "On-Grid");
                bundle.putStringArrayList("stringArray", stringArray);
                bundle.putIntegerArrayList("integerArray", integerArray);
                bundle.putIntegerArrayList("integerArray2", integerArray2);
                bundle.putString("City", cityLocation);
                bundle.putDouble("CityIrradiance", irradianceLocation);
                bundle.putString("choice", "appliance");
                fragmentOverviewAppliances.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.layoutMain, fragmentOverviewAppliances, "FragmentOverviewAppliances")
                        .commit();
            }
        });

        offGrid.setOnClickListener(v -> {
            if (getArguments().getString("choice").equals("bill")) {
                FragmentOverview fragmentOverview = new FragmentOverview();
                Bundle bundle = new Bundle();
                bundle.putString("Grid", "Off-Grid");
                bundle.putStringArrayList("stringArray", stringArray);
                bundle.putIntegerArrayList("integerArray", integerArray);
                bundle.putIntegerArrayList("integerArray2", integerArray2);
                bundle.putString("City", cityLocation);
                bundle.putDouble("CityIrradiance", irradianceLocation);
                fragmentOverview.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.layoutMain, fragmentOverview, "FragmentOverview")
                        .commit();
            } else if (getArguments().getString("choice").equals("appliance")) {
                FragmentOverviewAppliances fragmentOverviewAppliances = new FragmentOverviewAppliances();
                Bundle bundle = new Bundle();
                bundle.putString("Grid", "Off-Grid");
                bundle.putStringArrayList("stringArray", stringArray);
                bundle.putIntegerArrayList("integerArray", integerArray);
                bundle.putIntegerArrayList("integerArray2", integerArray2);
                bundle.putString("City", cityLocation);
                bundle.putDouble("CityIrradiance", irradianceLocation);
                bundle.putString("choice", "appliance");
                fragmentOverviewAppliances.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.layoutMain, fragmentOverviewAppliances, "FragmentOverviewAppliances")
                        .commit();
            }
        });
    }
}