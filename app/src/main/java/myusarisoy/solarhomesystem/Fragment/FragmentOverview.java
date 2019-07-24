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
import myusarisoy.solarhomesystem.Adapter.RecyclerViewOverviewAdapter;
import myusarisoy.solarhomesystem.Adapter.RecyclerViewPowerConsumptionAdapter;
import myusarisoy.solarhomesystem.Model.ApplianceEnergySaverTips;
import myusarisoy.solarhomesystem.Model.ApplianceOverview;
import myusarisoy.solarhomesystem.Model.AppliancePowerConsumption;
import myusarisoy.solarhomesystem.R;

public class FragmentOverview extends Fragment {
    @BindView(R.id.layoutOverview)
    LinearLayout linearLayout;

    @BindView(R.id.layoutMonthlyOverview)
    LinearLayout layoutMonthlyOverview;

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

    AppCompatDialog dialog_power_consumption, dialog_energy_saver_tips, dialog_main_page, dialog_area;
    Button ok_power_consumption, ok_energy_saver_tips, no_main_page, yes_main_page, cancel_back, confirm_back;
    EditText area_place_info;
    private RecyclerViewOverviewAdapter adapter;
    private RecyclerViewPowerConsumptionAdapter adapterPowerConsumption;
    private RecyclerViewEnergySaverTipsAdapter adapterEnergySaverTips;
    private ArrayList<ApplianceOverview> applianceOverview = new ArrayList<>();
    private ArrayList<AppliancePowerConsumption> appliancePowerConsumptions = new ArrayList<>();
    private ArrayList<ApplianceEnergySaverTips> applianceEnergySaverTips = new ArrayList<>();
    public ArrayList<String> stringArray = new ArrayList<>();
    public ArrayList<Integer> integerArray = new ArrayList<>();
    public ArrayList<Integer> integerArray2 = new ArrayList<>();
    public String cityLocation, grid;
    public double irradianceLocation;
    int totalPayment, totalConsumption, mostConsumption = 0, area_info;
    View view;

    public static FragmentOverview newInstance(Object... objects) {
        FragmentOverview fragment = new FragmentOverview();
        Bundle args = new Bundle();
        args.putString("Grid", (String) objects[0]);
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
        view = inflater.inflate(R.layout.fragment_overview, container, false);

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

//        Toast.makeText(getContext(), "Total payment: " + totalPayment + " ₺\nTotal power consumption: " + totalConsumption + "kWh", Toast.LENGTH_LONG).show();

        return view;
    }

    public void initRecyclerView() {
        recyclerView = view.findViewById(R.id.recyclerViewOverview);

        for (int i = 0; i < stringArray.size(); i++) {
            ApplianceOverview month = new ApplianceOverview(stringArray.get(i), integerArray.get(i), integerArray2.get(i));
            applianceOverview.add(month);
            totalPayment += integerArray.get(i);
            totalConsumption += integerArray2.get(i);

            if (integerArray2.get(i) > mostConsumption)
                mostConsumption = integerArray2.get(i);
        }

        adapter.notifyDataSetChanged();
    }

    private void setAdapter() {
        recyclerView = view.findViewById(R.id.recyclerViewOverview);

        adapter = new RecyclerViewOverviewAdapter(applianceOverview, getContext());
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
        dialog_area = reservationBuilder.create();
        WindowManager.LayoutParams params = dialog_area.getWindow().getAttributes();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        params.width = (int) (width * 0.9);
        params.height = (int) (height * 0.9);
        dialog_area.getWindow().setAttributes(params);
        dialog_area.show();

        area_place_info = dialog_area.findViewById(R.id.areaPlaceInfo);
        cancel_back = dialog_area.findViewById(R.id.cancel_back);
        confirm_back = dialog_area.findViewById(R.id.confirm_back);

        cancel_back.setOnClickListener(v1 -> dialog_area.dismiss());

        confirm_back.setOnClickListener(v -> {
            if (!area_place_info.getText().toString().equals("")) {
                dialog_area.dismiss();
                area_info = Integer.parseInt(area_place_info.getText().toString());
            }
        });
    }

    public void initRecyclerViewPowerConsumption() {
        recyclerViewPowerConsumption = dialog_power_consumption.findViewById(R.id.recyclerViewPowerConsumption);

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
        recyclerViewPowerConsumption = dialog_power_consumption.findViewById(R.id.recyclerViewPowerConsumption);

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
        recyclerViewEnergySaverTips = dialog_energy_saver_tips.findViewById(R.id.recyclerViewEnergySaverTips);

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
        recyclerViewEnergySaverTips = dialog_energy_saver_tips.findViewById(R.id.recyclerViewEnergySaverTips);

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
        consumption.setOnClickListener(v -> {
            android.support.v7.app.AlertDialog.Builder reservationBuilder = new android.support.v7.app.AlertDialog.Builder(getContext());
            reservationBuilder.setView(R.layout.layout_top_power_consumption);
            dialog_power_consumption = reservationBuilder.create();
            WindowManager.LayoutParams params = dialog_power_consumption.getWindow().getAttributes();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels;
            int height = displayMetrics.heightPixels;
            params.width = (int) (width * 0.9);
            params.height = (int) (height * 0.9);
            dialog_power_consumption.getWindow().setAttributes(params);
            dialog_power_consumption.show();

            setAdapterPowerConsumption();
            initRecyclerViewPowerConsumption();

            ok_power_consumption = dialog_power_consumption.findViewById(R.id.okPowerConsumption);

            ok_power_consumption.setOnClickListener(v1 -> dialog_power_consumption.dismiss());
        });
    }

    private void showTips() {
        tips = view.findViewById(R.id.imageViewTips);
        tips.setOnClickListener(v -> {
            android.support.v7.app.AlertDialog.Builder reservationBuilder = new android.support.v7.app.AlertDialog.Builder(getContext());
            reservationBuilder.setView(R.layout.layout_energy_saver_tips);
            dialog_energy_saver_tips = reservationBuilder.create();
            WindowManager.LayoutParams params = dialog_energy_saver_tips.getWindow().getAttributes();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels;
            int height = displayMetrics.heightPixels;
            params.width = (int) (width * 0.9);
            params.height = (int) (height * 0.9);
            dialog_energy_saver_tips.getWindow().setAttributes(params);
            dialog_energy_saver_tips.show();

            setAdapterEnergySaverTips();
            initRecyclerViewEnergySaverTips();

            ok_energy_saver_tips = dialog_energy_saver_tips.findViewById(R.id.ok_energy_saver_tips);

            ok_energy_saver_tips.setOnClickListener(v1 -> dialog_energy_saver_tips.dismiss());
        });
    }

    private void gotoPanels() {
        buttonNext = view.findViewById(R.id.buttonNext);
        buttonNext.setOnClickListener(v -> {
            if (getArguments().getString("Grid").equals("On-Grid")) {
                FragmentPanels fragmentPanels = new FragmentPanels();
                Bundle bundle = new Bundle();
                bundle.putString("City", cityLocation);
                bundle.putDouble("CityIrradiance", irradianceLocation);
                bundle.putInt("MostConsumption", mostConsumption);
                bundle.putInt("TotalPayment", totalPayment);
                bundle.putInt("TotalConsumption", totalConsumption);
                bundle.putString("Grid", grid);
                bundle.putInt("AreaInfo", area_info);
                bundle.putString("choice", "");
                fragmentPanels.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.enter_left_to_right, R.anim.exit_left_to_right, R.anim.enter_right_to_left, R.anim.exit_left_to_right)
                        .replace(R.id.layoutMain, fragmentPanels, "FragmentPanels")
                        .commit();
            } else if (getArguments().getString("Grid").equals("Off-Grid")) {
                FragmentPanels fragmentPanels = new FragmentPanels();
                Bundle bundle = new Bundle();
                bundle.putString("City", cityLocation);
                bundle.putDouble("CityIrradiance", irradianceLocation);
                bundle.putInt("MostConsumption", mostConsumption);
                bundle.putInt("TotalPayment", totalPayment);
                bundle.putInt("TotalConsumption", totalConsumption);
                bundle.putString("Grid", grid);
                bundle.putInt("AreaInfo", 0);
                bundle.putString("choice", "");
                fragmentPanels.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.enter_left_to_right, R.anim.exit_left_to_right, R.anim.enter_right_to_left, R.anim.exit_left_to_right)
                        .replace(R.id.layoutMain, fragmentPanels, "FragmentPanels")
                        .commit();
            }
        });
    }

    private void showSnackbar(String text) {
        linearLayout = view.findViewById(R.id.layoutOverview);

        Snackbar snackbar = Snackbar.make(linearLayout, text, Snackbar.LENGTH_SHORT);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(getResources().getColor(R.color.backgroundColor));
        TextView textView = snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(getResources().getColor(R.color.colorPrimary));
        snackbar.show();
    }
}