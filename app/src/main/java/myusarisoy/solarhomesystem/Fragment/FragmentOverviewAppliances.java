package myusarisoy.solarhomesystem.Fragment;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import myusarisoy.solarhomesystem.Adapter.RecyclerViewEnergySaverTipsAdapter;
import myusarisoy.solarhomesystem.Adapter.RecyclerViewOverviewAppliancesAdapter;
import myusarisoy.solarhomesystem.Adapter.RecyclerViewPowerConsumptionAdapter;
import myusarisoy.solarhomesystem.Model.ApplianceEnergySaverTips;
import myusarisoy.solarhomesystem.Model.ApplianceOverviewItem;
import myusarisoy.solarhomesystem.Model.AppliancePowerConsumption;
import myusarisoy.solarhomesystem.R;

public class FragmentOverviewAppliances extends Fragment {
    @BindView(R.id.layoutOverviewAppliances)
    LinearLayout linearLayout;

    @BindView(R.id.layoutApplianceOverview)
    LinearLayout layout_appliance_overview;

    @BindView(R.id.recyclerViewOverview)
    RecyclerView recyclerView;

    @BindView(R.id.imageViewConsumption)
    ImageView consumption;

    @BindView(R.id.recyclerViewPowerConsumption)
    RecyclerView recyclerViewPowerConsumption;

    @BindView(R.id.imageViewTips)
    ImageView tips;

    @BindView(R.id.recyclerViewEnergySaverTips)
    RecyclerView recyclerViewEnergySaverTips;

    @BindView(R.id.buttonNext)
    Button buttonNext;

    @BindView(R.id.layoutOnGoing)
    LinearLayout layoutOnGoing;

    @BindView(R.id.imageMainPage)
    ImageView imageMainPage;

    AppCompatDialog dialogPowerConsumption, dialogEnergySaverTips, dialogMainPage, annualBillingDialog, dialogArea;
    Button okPowerConsumption, okEnergySaverTips, noMainPage, yesMainPage, cancelBack, confirmBack, cancelBackArea, confirmBackArea;
    EditText areaPlaceInfo;
    private RecyclerViewOverviewAppliancesAdapter adapter;
    private RecyclerViewPowerConsumptionAdapter adapterPowerConsumption;
    private RecyclerViewEnergySaverTipsAdapter adapterEnergySaverTips;
    private ArrayList<ApplianceOverviewItem> applianceOverview = new ArrayList<>();
    private ArrayList<AppliancePowerConsumption> appliancePowerConsumptions = new ArrayList<>();
    private ArrayList<ApplianceEnergySaverTips> applianceEnergySaverTips = new ArrayList<>();
    public ArrayList<String> stringArray = new ArrayList<>();
    public ArrayList<Integer> integerArray = new ArrayList<>();
    public ArrayList<Integer> integerArray2 = new ArrayList<>();
    public EditText applianceAnnualBilling;
    public String cityLocation, grid;
    public double irradianceLocation;
    int totalConsumption, mostConsumption = 0, annualBilling, areaInfo;
    View view;

    public static FragmentOverviewAppliances newInstance(Object... objects) {
        FragmentOverviewAppliances fragment = new FragmentOverviewAppliances();
        Bundle args = new Bundle();
        args.putString("Grid", (String) objects[0]);
        args.putStringArrayList("stringArray", (ArrayList<String>) objects[1]);
        args.putIntegerArrayList("integerArray", (ArrayList<Integer>) objects[2]);
        args.putIntegerArrayList("integerArray2", (ArrayList<Integer>) objects[3]);
        args.putString("City", (String) objects[4]);
        args.putDouble("Irradiance", (Double) objects[5]);
        args.putString("choice", (String) objects[6]);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_overview_appliances, container, false);

        stringArray = getArguments().getStringArrayList("stringArray");
        integerArray = getArguments().getIntegerArrayList("integerArray");
        integerArray2 = getArguments().getIntegerArrayList("integerArray2");
        cityLocation = getArguments().getString("City");
        irradianceLocation = getArguments().getDouble("CityIrradiance");
        grid = getArguments().getString("Grid");

        if (grid.equals("On-Grid"))
            enterTheAreaOfYourPlace();

//        Set adapter and run RecyclerView.
        setAdapter();
        initRecyclerView();

//        Show power consumption and energy saver lists.
        showConsumption();
        showTips();

//        Go to panels.
        gotoPanels();

//        Toast.makeText(getContext(), "Total power consumption: " + totalConsumption + " kWh", Toast.LENGTH_LONG).show();

        return view;
    }

    public void initRecyclerView() {
        recyclerView = view.findViewById(R.id.recyclerViewOverview);

        for (int i = 0; i < integerArray.size(); i++) {
            ApplianceOverviewItem item = new ApplianceOverviewItem(integerArray.get(i), stringArray.get(i), integerArray2.get(i));
            applianceOverview.add(item);
            totalConsumption += integerArray2.get(i);

            if (integerArray2.get(i) > mostConsumption)
                mostConsumption = integerArray2.get(i);
        }

        adapter.notifyDataSetChanged();
    }

    private void setAdapter() {
        recyclerView = view.findViewById(R.id.recyclerViewOverview);

        adapter = new RecyclerViewOverviewAppliancesAdapter(applianceOverview, getContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.item_divider));
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void enterTheAreaOfYourPlace() {
        android.support.v7.app.AlertDialog.Builder reservationBuilder = new android.support.v7.app.AlertDialog.Builder(getContext());
        reservationBuilder.setView(R.layout.dialog_area);
        dialogArea = reservationBuilder.create();
        WindowManager.LayoutParams params = dialogArea.getWindow().getAttributes();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        params.width = (int) (width * 0.9);
        params.height = (int) (height * 0.9);
        dialogArea.getWindow().setAttributes(params);
        dialogArea.show();

        areaPlaceInfo = dialogArea.findViewById(R.id.areaPlaceInfo);
        cancelBackArea = dialogArea.findViewById(R.id.cancelBack);
        confirmBackArea = dialogArea.findViewById(R.id.confirmBack);

        cancelBackArea.setOnClickListener(v1 -> dialogArea.dismiss());

        confirmBackArea.setOnClickListener(v -> {
            if (!areaPlaceInfo.getText().toString().equals("")) {
                dialogArea.dismiss();
                areaInfo = Integer.parseInt(areaPlaceInfo.getText().toString());
            }
        });
    }

    public void initRecyclerViewPowerConsumption() {
        recyclerViewPowerConsumption = dialogPowerConsumption.findViewById(R.id.recyclerViewPowerConsumption);

        AppliancePowerConsumption airConditioner = new AppliancePowerConsumption(R.drawable.air_conditioner, "Air Conditioner", getResources().getString(R.string.airConditionerConsumptionText), getResources().getColor(R.color.blue));
        appliancePowerConsumptions.add(airConditioner);

        AppliancePowerConsumption waterHeater = new AppliancePowerConsumption(R.drawable.water_heater, "Water Heater", getResources().getString(R.string.waterHeaterConsumptionText), getResources().getColor(R.color.darkSlateGray));
        appliancePowerConsumptions.add(waterHeater);

        AppliancePowerConsumption fridge = new AppliancePowerConsumption(R.drawable.fridge, "Fridge", getResources().getString(R.string.fridgeConsumptionText), getResources().getColor(R.color.green));
        appliancePowerConsumptions.add(fridge);

        AppliancePowerConsumption lights = new AppliancePowerConsumption(R.drawable.lights, "Lights", getResources().getString(R.string.lightsConsumptionText), getResources().getColor(R.color.orange));
        appliancePowerConsumptions.add(lights);

        adapterPowerConsumption.notifyDataSetChanged();
    }

    private void setAdapterPowerConsumption() {
        recyclerViewPowerConsumption = dialogPowerConsumption.findViewById(R.id.recyclerViewPowerConsumption);

        adapterPowerConsumption = new RecyclerViewPowerConsumptionAdapter(appliancePowerConsumptions, getContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerViewPowerConsumption.setLayoutManager(mLayoutManager);
        recyclerViewPowerConsumption.setItemAnimator(new DefaultItemAnimator());
        recyclerViewPowerConsumption.setAdapter(adapterPowerConsumption);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.item_divider));
        recyclerViewPowerConsumption.addItemDecoration(dividerItemDecoration);
    }

    public void initRecyclerViewEnergySaverTips() {
        recyclerViewEnergySaverTips = dialogEnergySaverTips.findViewById(R.id.recyclerViewEnergySaverTips);

        ApplianceEnergySaverTips airConditioner = new ApplianceEnergySaverTips(R.drawable.air_conditioner, "Air Conditioner", getResources().getString(R.string.airConditionerSaverText), getResources().getColor(R.color.blue));
        applianceEnergySaverTips.add(airConditioner);

        ApplianceEnergySaverTips waterHeater = new ApplianceEnergySaverTips(R.drawable.water_heater, "Water Heater", getResources().getString(R.string.waterHeaterSaverText), getResources().getColor(R.color.darkSlateGray));
        applianceEnergySaverTips.add(waterHeater);

        ApplianceEnergySaverTips fridge = new ApplianceEnergySaverTips(R.drawable.fridge, "Fridge", getResources().getString(R.string.fridgeSaverText), getResources().getColor(R.color.green));
        applianceEnergySaverTips.add(fridge);

        ApplianceEnergySaverTips lights = new ApplianceEnergySaverTips(R.drawable.lights, "Lights", getResources().getString(R.string.lightsSaverText), getResources().getColor(R.color.orange));
        applianceEnergySaverTips.add(lights);

        adapterEnergySaverTips.notifyDataSetChanged();
    }

    private void setAdapterEnergySaverTips() {
        recyclerViewEnergySaverTips = dialogEnergySaverTips.findViewById(R.id.recyclerViewEnergySaverTips);

        adapterEnergySaverTips = new RecyclerViewEnergySaverTipsAdapter(applianceEnergySaverTips, getContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerViewEnergySaverTips.setLayoutManager(mLayoutManager);
        recyclerViewEnergySaverTips.setItemAnimator(new DefaultItemAnimator());
        recyclerViewEnergySaverTips.setAdapter(adapterEnergySaverTips);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.item_divider));
        recyclerViewEnergySaverTips.addItemDecoration(dividerItemDecoration);
    }

    private void showConsumption() {
        consumption = view.findViewById(R.id.imageViewConsumption);
        consumption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v7.app.AlertDialog.Builder reservationBuilder = new android.support.v7.app.AlertDialog.Builder(getContext());
                reservationBuilder.setView(R.layout.layout_top_power_consumption);
                dialogPowerConsumption = reservationBuilder.create();
                WindowManager.LayoutParams params = dialogPowerConsumption.getWindow().getAttributes();
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int width = displayMetrics.widthPixels;
                int height = displayMetrics.heightPixels;
                params.width = (int) (width * 0.9);
                params.height = (int) (height * 0.9);
                dialogPowerConsumption.getWindow().setAttributes(params);
                dialogPowerConsumption.show();

                setAdapterPowerConsumption();
                initRecyclerViewPowerConsumption();

                okPowerConsumption = dialogPowerConsumption.findViewById(R.id.okPowerConsumption);

                okPowerConsumption.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogPowerConsumption.dismiss();
                    }
                });
            }
        });
    }

    private void showTips() {
        tips = view.findViewById(R.id.imageViewTips);
        tips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v7.app.AlertDialog.Builder reservationBuilder = new android.support.v7.app.AlertDialog.Builder(getContext());
                reservationBuilder.setView(R.layout.layout_energy_saver_tips);
                dialogEnergySaverTips = reservationBuilder.create();
                WindowManager.LayoutParams params = dialogEnergySaverTips.getWindow().getAttributes();
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int width = displayMetrics.widthPixels;
                int height = displayMetrics.heightPixels;
                params.width = (int) (width * 0.9);
                params.height = (int) (height * 0.9);
                dialogEnergySaverTips.getWindow().setAttributes(params);
                dialogEnergySaverTips.show();

                setAdapterEnergySaverTips();
                initRecyclerViewEnergySaverTips();

                okEnergySaverTips = dialogEnergySaverTips.findViewById(R.id.ok_energy_saver_tips);

                okEnergySaverTips.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogEnergySaverTips.dismiss();
                    }
                });
            }
        });
    }

    private void gotoPanels() {
        buttonNext = view.findViewById(R.id.buttonNext);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v7.app.AlertDialog.Builder reservationBuilder = new android.support.v7.app.AlertDialog.Builder(getContext());
                reservationBuilder.setView(R.layout.dialog_annual_billing);
                annualBillingDialog = reservationBuilder.create();
                WindowManager.LayoutParams params = annualBillingDialog.getWindow().getAttributes();
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int width = displayMetrics.widthPixels;
                int height = displayMetrics.heightPixels;
                params.width = (int) (width * 0.8);
                params.height = (int) (height * 0.8);
                annualBillingDialog.getWindow().setAttributes(params);
                annualBillingDialog.show();

                applianceAnnualBilling = annualBillingDialog.findViewById(R.id.applianceAnnualBilling);
                cancelBack = annualBillingDialog.findViewById(R.id.cancelBack);
                confirmBack = annualBillingDialog.findViewById(R.id.confirmBack);

                cancelBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        annualBillingDialog.dismiss();
                    }
                });

                confirmBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (applianceAnnualBilling.getText().toString().isEmpty() || applianceAnnualBilling.getText().toString().equals("0"))
                            showSnackbar(getResources().getString(R.string.validAmount));
                        else {
                            annualBillingDialog.dismiss();

                            annualBilling = Integer.parseInt(applianceAnnualBilling.getText().toString());

                            if (getArguments().getString("Grid").equals("On-Grid")) {
                                FragmentPanels fragmentPanels = new FragmentPanels();
                                Bundle bundle = new Bundle();
                                bundle.putString("City", cityLocation);
                                bundle.putDouble("CityIrradiance", irradianceLocation);
                                bundle.putInt("MostConsumption", mostConsumption);
                                bundle.putInt("TotalPayment", annualBilling);
                                bundle.putInt("TotalConsumption", totalConsumption);
                                bundle.putString("Grid", grid);
                                bundle.putInt("AreaInfo", areaInfo);
                                bundle.putString("choice", getArguments().getString("choice"));
                                fragmentPanels.setArguments(bundle);
                                getActivity().getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.layoutMain, fragmentPanels, "FragmentPanels")
                                        .commit();
                            } else if (getArguments().getString("Grid").equals("Off-Grid")) {
                                FragmentPanels fragmentPanels = new FragmentPanels();
                                Bundle bundle = new Bundle();
                                bundle.putString("City", cityLocation);
                                bundle.putDouble("CityIrradiance", irradianceLocation);
                                bundle.putInt("MostConsumption", mostConsumption);
                                bundle.putInt("TotalPayment", annualBilling);
                                bundle.putInt("TotalConsumption", totalConsumption);
                                bundle.putString("Grid", grid);
                                bundle.putInt("AreaInfo", 0);
                                bundle.putString("choice", getArguments().getString("choice"));
                                fragmentPanels.setArguments(bundle);
                                getActivity().getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.layoutMain, fragmentPanels, "FragmentPanels")
                                        .commit();
                            }
                        }
                    }
                });
            }
        });
    }

    private void showSnackbar(String text) {
        linearLayout = view.findViewById(R.id.layoutOverviewAppliances);

        Snackbar snackbar = Snackbar.make(linearLayout, text, Snackbar.LENGTH_SHORT);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(getResources().getColor(R.color.backgroundColor));
        TextView textView = snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(getResources().getColor(R.color.colorPrimary));
        snackbar.show();
    }
}