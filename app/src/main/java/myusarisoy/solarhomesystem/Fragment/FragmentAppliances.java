package myusarisoy.solarhomesystem.Fragment;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import myusarisoy.solarhomesystem.Adapter.RecyclerViewApplianceAdapter;
import myusarisoy.solarhomesystem.R;
import myusarisoy.solarhomesystem.Model.SelectedAppliance;

public class FragmentAppliances extends Fragment {
    @BindView(R.id.layoutAppliances)
    LinearLayout layoutAppliances;

    @BindView(R.id.recyclerViewAppliance)
    RecyclerView recyclerView;

    @BindView(R.id.buttonBack)
    Button buttonBack;

    @BindView(R.id.buttonContinue)
    Button buttonContinue;

    private RecyclerViewApplianceAdapter adapter;
    private ArrayList<SelectedAppliance> selectedAppliancesList = new ArrayList<>();
    private ArrayList<Integer> consumptionList = new ArrayList<>();
    private ArrayList<String> arrayListName = new ArrayList<>();
    private ArrayList<Integer> arrayListImage = new ArrayList<>();
    public String cityLocation;
    public double irradianceLocation;
    View view;

    public static FragmentAppliances newInstance(Object... objects) {
        FragmentAppliances fragment = new FragmentAppliances();
        Bundle args = new Bundle();
        args.putStringArrayList("AppliancesName", (ArrayList<String>) objects[0]);
        args.putIntegerArrayList("AppliancesImage", (ArrayList<Integer>) objects[1]);
        args.putString("city", (String) objects[2]);
        args.putDouble("irradiance", (Double) objects[3]);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_appliances, container, false);

//        Get list of appliances.
        arrayListName = getArguments().getStringArrayList("AppliancesName");
        arrayListImage = getArguments().getIntegerArrayList("AppliancesImage");

//        Get city and its solar irradiance data.
        cityLocation = getArguments().getString("City");
        irradianceLocation = getArguments().getDouble("CityIrradiance");

//        Set adapter for Recycler View.
        setAdapter();

//        Show list of appliances.
        initSelectedAppliances();

//        Go to grid choice.
        gotoGridChoice();

        return view;
    }

    private void setAdapter() {
        recyclerView = view.findViewById(R.id.recyclerViewAppliance);

        adapter = new RecyclerViewApplianceAdapter(selectedAppliancesList, getContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.item_divider));
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    public void initSelectedAppliances() {
        recyclerView = view.findViewById(R.id.recyclerViewAppliance);

        for (int i = 0; i < arrayListName.size(); i++) {
            SelectedAppliance selectedAppliance = new SelectedAppliance(arrayListImage.get(i), arrayListName.get(i));
            selectedAppliancesList.add(selectedAppliance);
        }

        adapter.notifyDataSetChanged();
    }

    private void gotoGridChoice() {
        buttonContinue = view.findViewById(R.id.buttonContinue);

        consumptionList = adapter.getData();

        buttonContinue.setOnClickListener(v -> {
            if (consumptionList.size() != arrayListName.size())
                showSnackbar(getResources().getString(R.string.completePartsAppliances));
            else {
                FragmentGridChoice fragmentGridChoice = new FragmentGridChoice();
                Bundle bundle = new Bundle();
                bundle.putString("choice", "appliance");
                bundle.putStringArrayList("AppliancesName", arrayListName);
                bundle.putIntegerArrayList("AppliancesImage", arrayListImage);
                bundle.putIntegerArrayList("AppliancesConsumption", consumptionList);
                bundle.putString("City", cityLocation);
                bundle.putDouble("CityIrradiance", irradianceLocation);
                fragmentGridChoice.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.enter_left_to_right, R.anim.exit_left_to_right, R.anim.enter_right_to_left, R.anim.exit_left_to_right)
                        .replace(R.id.layoutMain, fragmentGridChoice, "FragmentGridChoice")
                        .commit();
            }
        });
    }

    public void showSnackbar(String text) {
        layoutAppliances = view.findViewById(R.id.layoutAppliances);

        Snackbar snackbar = Snackbar.make(layoutAppliances, text, Snackbar.LENGTH_SHORT);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(getResources().getColor(R.color.backgroundColor));
        TextView textView = snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(getResources().getColor(R.color.colorPrimary));
        snackbar.show();
    }
}