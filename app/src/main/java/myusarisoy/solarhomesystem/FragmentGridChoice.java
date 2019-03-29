package myusarisoy.solarhomesystem;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;

public class FragmentGridChoice extends Fragment {
    @BindView(R.id.img_on_grid)
    ImageView on_grid;

    @BindView(R.id.img_off_grid)
    ImageView off_grid;

    public ArrayList<String> monthName = new ArrayList<>();
    public ArrayList<Integer> monthPowerConsumption = new ArrayList<>();
    public ArrayList<Integer> monthPayment = new ArrayList<>();
    View view;

    public static FragmentGridChoice newInstance(Object... objects) {
        FragmentGridChoice fragment = new FragmentGridChoice();
        Bundle args = new Bundle();
        args.putStringArrayList("MonthName", (ArrayList<String>) objects[0]);
        args.putIntegerArrayList("MonthPayment", (ArrayList<Integer>) objects[1]);
        args.putIntegerArrayList("MonthPowerConsumption", (ArrayList<Integer>) objects[2]);
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

        monthName = getArguments().getStringArrayList("MonthName");
        monthPayment = getArguments().getIntegerArrayList("MonthPayment");
        monthPowerConsumption = getArguments().getIntegerArrayList("MonthPowerConsumption");

//        Grid choices.
        gridChoice();

        return view;
    }

    private void gridChoice() {
        on_grid = view.findViewById(R.id.img_on_grid);
        off_grid = view.findViewById(R.id.img_off_grid);

        on_grid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentOverview fragmentOverview = new FragmentOverview();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.layout_main, fragmentOverview, "FragmentOverview")
                        .commit();
            }
        });

        off_grid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentOverview fragmentOverview = new FragmentOverview();
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("MonthName", monthName);
                bundle.putIntegerArrayList("MonthPayment", monthPayment);
                bundle.putIntegerArrayList("MonthPowerConsumption", monthPowerConsumption);
                fragmentOverview.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.layout_main, fragmentOverview, "FragmentOverview")
                        .commit();
            }
        });
    }
}