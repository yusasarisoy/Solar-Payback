package myusarisoy.solarhomesystem;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDialog;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;

public class FragmentPanelCalculation extends Fragment {
    @BindView(R.id.layout_panel_calculation)
    LinearLayout linearLayout;

    @BindView(R.id.layout_decisions)
    LinearLayout layoutDecisions;

    @BindView(R.id.selected_panel_energy)
    TextView selected_panel_energy;

    @BindView(R.id.selected_panel_area)
    TextView selected_panel_area;

    @BindView(R.id.selected_city)
    TextView selected_city;

    @BindView(R.id.selected_city_irradiance)
    TextView selected_city_irradiance;

    @BindView(R.id.most_power_consumption)
    TextView most_power_consumption;

    @BindView(R.id.layout_results)
    LinearLayout layoutResults;

    @BindView(R.id.produced_energy)
    TextView produced_energy;

    @BindView(R.id.required_panels)
    TextView required_panels;

    @BindView(R.id.required_area)
    TextView required_area;

    @BindView(R.id.total_payment)
    TextView total_payment;

    @BindView(R.id.layout_back)
    LinearLayout layout_back;

    @BindView(R.id.layout_next)
    LinearLayout layout_next;

    @BindView(R.id.layout_continue)
    LinearLayout layout_continue;

    @BindView(R.id.button_back)
    Button button_back;

    @BindView(R.id.button_next)
    Button button_next;

    @BindView(R.id.button_continue)
    Button button_continue;

    AppCompatDialog generatorDialog;
    Button buttonNo, buttonYes;
    public String cityLocation;
    public double liraPerEuro, irradianceLocation, panelArea, euroPerWatt = 0.441;
    public int panelEnergy, mostConsumption, producedEnergy, howManyPanels, requiredArea, totalPrice, totalPayment;
    View view;

    public static FragmentPanelCalculation newInstance(Object... objects) {
        FragmentPanelCalculation fragment = new FragmentPanelCalculation();
        Bundle args = new Bundle();
        args.putString("Panel", (String) objects[0]);
        args.putDouble("PanelArea", (Double) objects[1]);
        args.putDouble("LiraPerEuro", (Double) objects[2]);
        args.putString("City", (String) objects[3]);
        args.putDouble("CityIrradiance", (Double) objects[4]);
        args.putInt("MostConsumption", (Integer) objects[5]);
        args.putInt("TotalPayment", (Integer) objects[6]);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_panel_calculation, container, false);

//        Get all arguments.
        getArgument();

//        Calculate how many panels need.
        calculatePanels();

//        Get results.
        getResults();

        return view;
    }

    private void getArgument() {
        selected_panel_energy = view.findViewById(R.id.selected_panel_energy);
        selected_panel_area = view.findViewById(R.id.selected_panel_area);
        selected_city = view.findViewById(R.id.selected_city);
        selected_city_irradiance = view.findViewById(R.id.selected_city_irradiance);
        most_power_consumption = view.findViewById(R.id.most_power_consumption);

        if (getArguments().getString("Panel").equals("panel1"))
            panelEnergy = 330;
        else if (getArguments().getString("Panel").equals("panel2"))
            panelEnergy = 245;

        panelArea = getArguments().getDouble("PanelArea");
        liraPerEuro = getArguments().getDouble("LiraPerEuro");
        cityLocation = getArguments().getString("City");
        irradianceLocation = getArguments().getDouble("CityIrradiance");
        mostConsumption = getArguments().getInt("MostConsumption");
        totalPayment = getArguments().getInt("TotalPayment");

        selected_panel_energy.setText(getResources().getString(R.string.selected_panel_energy) + panelEnergy + " W");
        selected_panel_area.setText(getResources().getString(R.string.selected_panel_area) + panelArea + " m²");
        selected_city.setText(getResources().getString(R.string.selected_city) + cityLocation);
        selected_city_irradiance.setText(getResources().getString(R.string.selected_irradiance) + irradianceLocation);
        most_power_consumption.setText(getResources().getString(R.string.selected_consumption) + mostConsumption + getResources().getString(R.string.watt_per_day));
    }

    private void calculatePanels() {
        produced_energy = view.findViewById(R.id.produced_energy);
        required_panels = view.findViewById(R.id.required_panels);
        required_area = view.findViewById(R.id.required_area);
        total_payment = view.findViewById(R.id.total_payment);

        if (getArguments().getString("Panel").equals("panel1")) {
            howManyPanels = (int) (mostConsumption / (irradianceLocation * 0.245)) + 1;
            producedEnergy = (int) (0.245 * irradianceLocation * howManyPanels);
        } else if (getArguments().getString("Panel").equals("panel2")) {
            howManyPanels = (int) (mostConsumption / (irradianceLocation * 0.186)) + 1;
            producedEnergy = (int) (0.186 * irradianceLocation * howManyPanels);
        }

        requiredArea = (int) ((howManyPanels * panelArea) + 1);
        totalPrice = (int) (panelEnergy * euroPerWatt * howManyPanels * liraPerEuro);

        required_panels.setText(getResources().getString(R.string.required_panels) + howManyPanels);
        produced_energy.setText(getResources().getString(R.string.produced_energy) + producedEnergy + " Wh");
        required_area.setText(getResources().getString(R.string.required_area) + requiredArea + " m²");
        total_payment.setText(getResources().getString(R.string.total_panel_payment) + totalPrice + " ₺");
    }

    private void getResults() {
        layoutDecisions = view.findViewById(R.id.layout_decisions);
        layoutResults = view.findViewById(R.id.layout_results);
        layout_back = view.findViewById(R.id.layout_back);
        layout_next = view.findViewById(R.id.layout_next);
        layout_continue = view.findViewById(R.id.layout_continue);
        button_back = view.findViewById(R.id.button_back);
        button_next = view.findViewById(R.id.button_next);
        button_continue = view.findViewById(R.id.button_continue);

        button_back.setOnClickListener(v -> {
            layoutDecisions.setVisibility(View.VISIBLE);
            layoutResults.setVisibility(View.GONE);
            layout_back.setVisibility(View.GONE);
            layout_next.setVisibility(View.VISIBLE);
            layout_continue.setVisibility(View.GONE);
        });

        button_next.setOnClickListener(v -> {
            layoutDecisions.setVisibility(View.GONE);
            layoutResults.setVisibility(View.VISIBLE);
            layout_back.setVisibility(View.VISIBLE);
            layout_next.setVisibility(View.GONE);
            layout_continue.setVisibility(View.VISIBLE);
        });

        button_continue.setOnClickListener(v -> {
            android.support.v7.app.AlertDialog.Builder reservationBuilder = new android.support.v7.app.AlertDialog.Builder(getContext());
            reservationBuilder.setView(R.layout.dialog_generator);
            generatorDialog = reservationBuilder.create();
            WindowManager.LayoutParams params = generatorDialog.getWindow().getAttributes();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels;
            int height = displayMetrics.heightPixels;
            params.width = (int) (width * 0.8);
            params.height = (int) (height * 0.8);
            generatorDialog.getWindow().setAttributes(params);
            generatorDialog.show();

            buttonNo = generatorDialog.findViewById(R.id.button_no);
            buttonYes = generatorDialog.findViewById(R.id.button_yes);

            buttonNo.setOnClickListener(v1 -> {
                generatorDialog.dismiss();

                FragmentBatteryCalculation fragmentBatteryCalculation = new FragmentBatteryCalculation();
                Bundle bundle = new Bundle();
                bundle.putInt("panelPrice", totalPrice);
                bundle.putInt("TotalPayment", totalPayment);
                fragmentBatteryCalculation.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.layout_main, fragmentBatteryCalculation, "FragmentBatteryCalculation")
                        .commit();
            });

            buttonYes.setOnClickListener(v12 -> {
                generatorDialog.dismiss();

                FragmentGeneratorChoice fragmentGeneratorChoice = new FragmentGeneratorChoice();
                Bundle bundle = new Bundle();
                bundle.putInt("panelPrice", totalPrice);
                bundle.putInt("TotalPayment", totalPayment);
                fragmentGeneratorChoice.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.layout_main, fragmentGeneratorChoice, "FragmentGeneratorChoice")
                        .commit();
            });
        });
    }
}