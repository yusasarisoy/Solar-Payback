package myusarisoy.solarhomesystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

    @BindView(R.id.button_next)
    Button button_next;

    @BindView(R.id.image_main_page)
    ImageView image_main_page;

    AppCompatDialog dialog_main_page;
    Button no_main_page, yes_main_page;
    public String cityLocation;
    public double liraPerEuro, irradianceLocation, panelArea, euroPerWatt = 0.441;
    public int panelEnergy, mostConsumption, producedEnergy, howManyPanels, requiredArea, aThousand = 1000, totalPrice;
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

        selected_panel_energy.setText("Selected Panel Energy: " + panelEnergy + " W");
        selected_panel_area.setText("Selected Panel Area: " + panelArea + " m²");
        selected_city.setText("Selected City: " + cityLocation);
        selected_city_irradiance.setText("Selected City Irradiance: " + irradianceLocation);
        most_power_consumption.setText("Consumption: " + mostConsumption + " kWh/day");

        Log.i("ARGUMENTS", panelEnergy + "\n" + panelArea + "\n" + cityLocation + "\n" + irradianceLocation + "\n" + mostConsumption);
    }

    private void calculatePanels() {
        produced_energy = view.findViewById(R.id.produced_energy);
        required_panels = view.findViewById(R.id.required_panels);
        required_area = view.findViewById(R.id.required_area);
        total_payment = view.findViewById(R.id.total_payment);

        producedEnergy = (int) ((mostConsumption / irradianceLocation) * aThousand);
        howManyPanels = ((producedEnergy / panelEnergy) + 1);
        requiredArea = (int) ((howManyPanels * panelArea) + 1);
        totalPrice = (int) (panelEnergy * euroPerWatt * howManyPanels * liraPerEuro);
        Log.i("SUMMARY", producedEnergy + " W\n" + howManyPanels + " panels\n" + requiredArea + " m²\n" + totalPrice + " ₺");

        produced_energy.setText("Produced Energy: " + producedEnergy + " W");
        required_panels.setText("Required Panels: " + howManyPanels);
        required_area.setText("Required Area: " + requiredArea + " m²");
        total_payment.setText("Total Payment: " + totalPrice + " ₺");
    }

    private void getResults() {
        layoutDecisions = view.findViewById(R.id.layout_decisions);
        layoutResults = view.findViewById(R.id.layout_results);
        button_next = view.findViewById(R.id.button_next);
        image_main_page = view.findViewById(R.id.image_main_page);

        button_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutDecisions.setVisibility(View.GONE);
                layoutResults.setVisibility(View.VISIBLE);
                button_next.setVisibility(View.GONE);
                image_main_page.setVisibility(View.VISIBLE);

                image_main_page.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        android.support.v7.app.AlertDialog.Builder reservationBuilder = new android.support.v7.app.AlertDialog.Builder(getContext());
                        reservationBuilder.setView(R.layout.layout_goto_main_page);
                        dialog_main_page = reservationBuilder.create();
                        WindowManager.LayoutParams params = dialog_main_page.getWindow().getAttributes();
                        DisplayMetrics displayMetrics = new DisplayMetrics();
                        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                        int width = displayMetrics.widthPixels;
                        int height = displayMetrics.heightPixels;
                        params.width = (int) (width * 0.9);
                        params.height = (int) (height * 0.9);
                        dialog_main_page.getWindow().setAttributes(params);
                        dialog_main_page.show();

                        no_main_page = dialog_main_page.findViewById(R.id.no_main_page);
                        yes_main_page = dialog_main_page.findViewById(R.id.yes_main_page);

                        no_main_page.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog_main_page.dismiss();
                            }
                        });

                        yes_main_page.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog_main_page.dismiss();

                                Intent intent = new Intent(getContext(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        });
                    }
                });
            }
        });
    }
}