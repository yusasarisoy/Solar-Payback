package myusarisoy.solarhomesystem.Fragment;

import android.content.SharedPreferences;
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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import myusarisoy.solarhomesystem.R;

public class FragmentPanelCalculation extends Fragment {
    @BindView(R.id.layoutPanelCalculation)
    LinearLayout linearLayout;

    @BindView(R.id.layoutDecisions)
    LinearLayout layoutDecisions;

    @BindView(R.id.selectedPanelEnergy)
    TextView selectedPanelEnergy;

    @BindView(R.id.selectedPanelArea)
    TextView selectedPanelArea;

    @BindView(R.id.selectedCity)
    TextView selectedCity;

    @BindView(R.id.selectedCityIrradiance)
    TextView selectedCityIrradiance;

    @BindView(R.id.mostPowerConsumption)
    TextView mostPowerConsumption;

    @BindView(R.id.layoutResults)
    LinearLayout layoutResults;

    @BindView(R.id.producedEnergy)
    TextView tvProducedEnergy;

    @BindView(R.id.requiredPanels)
    TextView tvRequiredPanels;

    @BindView(R.id.requiredArea)
    TextView tvRequiredArea;

    @BindView(R.id.layoutExtraCost)
    LinearLayout layoutExtraCost;

    @BindView(R.id.extraCost)
    TextView tvExtraCost;

    @BindView(R.id.totalPayment)
    TextView tvTotalPayment;

    @BindView(R.id.layoutBack)
    LinearLayout layoutBack;

    @BindView(R.id.layoutNext)
    LinearLayout layoutNext;

    @BindView(R.id.layoutContinue)
    LinearLayout layoutContinue;

    @BindView(R.id.buttonBack)
    Button buttonBack;

    @BindView(R.id.buttonNext)
    Button buttonNext;

    @BindView(R.id.buttonContinue)
    Button buttonContinue;

    RequestQueue requestQueueUSD;
    AppCompatDialog generatorDialog;
    Button buttonNo, buttonYes;
    public String cityLocation, grid, baseUSD, consumer;
    public double liraPerEuro, irradianceLocation, panelArea, euroPerWatt = 0.441, liraPerDollar, liraForOverProduction = 0.13, liraForLowerProduction;
    public int panelEnergy, mostConsumption, producedEnergy, howManyPanels, requiredArea, totalPrice, totalPayment, totalConsumption, applianceTotalConsumption, overProduction, lowerProduction;
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
        args.putInt("TotalConsumption", (Integer) objects[7]);
        args.putString("Grid", (String) objects[8]);
        args.putInt("AreaInfo", (Integer) objects[9]);
        args.putString("choice", (String) objects[10]);
        args.putInt("Area", (Integer) objects[11]);
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

        grid = getArguments().getString("Grid");

//        Get USD/TRY.
        String currencyUSD = "https://api.exchangeratesapi.io/latest?base=USD";

        requestQueueUSD = Volley.newRequestQueue(getContext());
        final JsonObjectRequest jsonObjectRequestUSD = new JsonObjectRequest(Request.Method.GET, currencyUSD, null, response -> {
            try {
                baseUSD = response.getString("base");
                JSONObject jsonObject = response.getJSONObject("rates");
                liraPerDollar = jsonObject.getDouble("TRY");
                liraForOverProduction = liraPerDollar * liraForOverProduction;
                liraForLowerProduction = liraPerDollar;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show());
        requestQueueUSD.add(jsonObjectRequestUSD);

//        Get all arguments.
        getArgument();

//        Calculate how many panels need.
        calculatePanels();

//        Get results.
        getResults();

        return view;
    }

    private void getArgument() {
        selectedPanelEnergy = view.findViewById(R.id.selectedPanelEnergy);
        selectedPanelArea = view.findViewById(R.id.selectedPanelArea);
        selectedCity = view.findViewById(R.id.selectedCity);
        selectedCityIrradiance = view.findViewById(R.id.selectedCityIrradiance);
        mostPowerConsumption = view.findViewById(R.id.mostPowerConsumption);

        if (getArguments().getString("Panel").equals("panel1"))
            panelEnergy = 330;
        else if (getArguments().getString("Panel").equals("panel2"))
            panelEnergy = 245;
        else if (getArguments().getString("Panel").equals("panel3"))
            panelEnergy = 300;

        panelArea = getArguments().getDouble("PanelArea");
        liraPerEuro = getArguments().getDouble("LiraPerEuro");
        cityLocation = getArguments().getString("City");
        irradianceLocation = getArguments().getDouble("CityIrradiance");
        mostConsumption = getArguments().getInt("MostConsumption");
        totalPayment = getArguments().getInt("TotalPayment");
        totalConsumption = getArguments().getInt("TotalConsumption");

        selectedPanelEnergy.setText(getResources().getString(R.string.selectedPanelEnergy) + panelEnergy + " W");
        selectedPanelArea.setText(getResources().getString(R.string.selectedPanelArea) + panelArea + " m²");
        selectedCity.setText(getResources().getString(R.string.selectedCity) + cityLocation);
        selectedCityIrradiance.setText(getResources().getString(R.string.selectedIrradiance) + irradianceLocation);
        if (getArguments().getString("choice").equals("appliance")) {
            applianceTotalConsumption = (totalConsumption * 12);
            mostPowerConsumption.setText(getResources().getString(R.string.selectedConsumption) + applianceTotalConsumption + " kWh/year");
        } else
            mostPowerConsumption.setText(getResources().getString(R.string.selectedConsumption) + totalConsumption + " kWh/year");
    }

    private void calculatePanels() {
        tvProducedEnergy = view.findViewById(R.id.producedEnergy);
        tvRequiredPanels = view.findViewById(R.id.requiredPanels);
        tvRequiredArea = view.findViewById(R.id.requiredArea);
        tvTotalPayment = view.findViewById(R.id.totalPayment);

        if (getArguments().getString("Grid").equals("On-Grid")) {
            if (getArguments().getString("Panel").equals("panel1")) {
                howManyPanels = getArguments().getInt("AreaInfo");
                producedEnergy = (int) (360 * 0.264 * irradianceLocation * howManyPanels);
            } else if (getArguments().getString("Panel").equals("panel2")) {
                howManyPanels = getArguments().getInt("AreaInfo");
                producedEnergy = (int) (360 * 0.207 * irradianceLocation * howManyPanels);
            } else if (getArguments().getString("Panel").equals("panel3")) {
                howManyPanels = getArguments().getInt("AreaInfo");
                producedEnergy = (int) (360 * 0.226 * irradianceLocation * howManyPanels);
            }
        } else if (getArguments().getString("Grid").equals("Off-Grid")) {
            if (getArguments().getString("choice").equals("appliance")) {
                if (getArguments().getString("Panel").equals("panel1")) {
                    howManyPanels = (int) ((applianceTotalConsumption / 30) / (irradianceLocation * 0.245 * 360)) + 1;
                    producedEnergy = (int) (360 * 0.264 * irradianceLocation * howManyPanels);
                } else if (getArguments().getString("Panel").equals("panel2")) {
                    howManyPanels = (int) ((applianceTotalConsumption / 30) / (irradianceLocation * 0.186 * 360)) + 1;
                    producedEnergy = (int) (360 * 0.207 * irradianceLocation * howManyPanels);
                } else if (getArguments().getString("Panel").equals("panel3")) {
                    howManyPanels = (int) ((applianceTotalConsumption / 30) / (irradianceLocation * 0.226 * 360)) + 1;
                    producedEnergy = (int) (360 * 0.226 * irradianceLocation * howManyPanels);
                }
            } else {
                if (getArguments().getString("Panel").equals("panel1")) {
                    howManyPanels = (int) ((totalConsumption / 30) / (irradianceLocation * 0.245 * 360)) + 1;
                    producedEnergy = (int) (360 * 0.264 * irradianceLocation * howManyPanels);
                } else if (getArguments().getString("Panel").equals("panel2")) {
                    howManyPanels = (int) ((totalConsumption / 30) / (irradianceLocation * 0.186 * 360)) + 1;
                    producedEnergy = (int) (360 * 0.207 * irradianceLocation * howManyPanels);
                } else if (getArguments().getString("Panel").equals("panel3")) {
                    howManyPanels = (int) ((totalConsumption / 30) / (irradianceLocation * 0.226 * 360)) + 1;
                    producedEnergy = (int) (360 * 0.226 * irradianceLocation * howManyPanels);
                }
            }
        }

//        Get over and lower productions.
        overProduction = producedEnergy - totalConsumption;
        lowerProduction = totalConsumption - producedEnergy;

        if (overProduction >= 0) {
            overProduction = (int) (overProduction * liraForOverProduction);
            lowerProduction = 0;
        } else if (lowerProduction > 0) {
            layoutExtraCost = view.findViewById(R.id.layoutExtraCost);
            tvExtraCost = view.findViewById(R.id.extraCost);

//            Get user's consumer status about the solar energy.
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Consumer", 0);
            consumer = sharedPreferences.getString("Consumer", "");

            if (consumer.equals("Commercial"))
                lowerProduction = (int) (lowerProduction * liraForLowerProduction * 0.71);
            else if (consumer.equals("Residental"))
                lowerProduction = (int) (lowerProduction * liraForLowerProduction * 0.69);

            tvExtraCost.setText(getResources().getString(R.string.extraCost) + lowerProduction + " ₺");
        }

        if (grid.equals("On-Grid"))
            totalPayment += overProduction;

        tvProducedEnergy.setText(getResources().getString(R.string.producedEnergy) + producedEnergy + " kWh/year");
        if (getArguments().get("choice").equals("appliance")) {
            if (grid.equals("On-Grid")) {
                requiredArea = getArguments().getInt("Area");
                totalPrice = (int) (panelEnergy * euroPerWatt * howManyPanels * liraPerEuro);
                tvRequiredPanels.setText(getResources().getString(R.string.requiredPanels) + getArguments().getInt("AreaInfo"));
            } else {
                requiredArea = (int) (((applianceTotalConsumption / producedEnergy) * panelArea) + 1);
                totalPrice = (int) (panelEnergy * euroPerWatt * (applianceTotalConsumption / producedEnergy) * liraPerEuro);
                tvRequiredPanels.setText(getResources().getString(R.string.requiredPanels) + ((applianceTotalConsumption / producedEnergy) + 1));
            }
        } else {
            requiredArea = (int) ((howManyPanels * panelArea) + 1);
            totalPrice = (int) (panelEnergy * euroPerWatt * howManyPanels * liraPerEuro);
            tvRequiredPanels.setText(getResources().getString(R.string.requiredPanels) + ((applianceTotalConsumption / producedEnergy) + 1));
        }

        tvRequiredArea.setText(getResources().getString(R.string.requiredArea) + (requiredArea + 2) + " m²");
        tvTotalPayment.setText(getResources().getString(R.string.totalPanelPayment) + totalPrice + " ₺");
    }

    private void getResults() {
        layoutDecisions = view.findViewById(R.id.layoutDecisions);
        layoutResults = view.findViewById(R.id.layoutResults);
        layoutBack = view.findViewById(R.id.layoutBack);
        layoutNext = view.findViewById(R.id.layoutNext);
        layoutContinue = view.findViewById(R.id.layoutContinue);
        buttonBack = view.findViewById(R.id.buttonBack);
        buttonNext = view.findViewById(R.id.buttonNext);
        buttonContinue = view.findViewById(R.id.buttonContinue);

        buttonBack.setOnClickListener(v -> {
            layoutDecisions.setVisibility(View.VISIBLE);
            layoutResults.setVisibility(View.GONE);
            layoutBack.setVisibility(View.GONE);
            layoutNext.setVisibility(View.VISIBLE);
            layoutContinue.setVisibility(View.GONE);
        });

        buttonNext.setOnClickListener(v -> {
            layoutDecisions.setVisibility(View.GONE);
            layoutResults.setVisibility(View.VISIBLE);
            layoutBack.setVisibility(View.VISIBLE);
            layoutNext.setVisibility(View.GONE);
            layoutContinue.setVisibility(View.VISIBLE);
        });

        buttonContinue.setOnClickListener(v -> {
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

            buttonNo = generatorDialog.findViewById(R.id.buttonNo);
            buttonYes = generatorDialog.findViewById(R.id.buttonYes);

            buttonNo.setOnClickListener(v1 -> {
                generatorDialog.dismiss();

                FragmentBatteryCalculation fragmentBatteryCalculation = new FragmentBatteryCalculation();
                Bundle bundle = new Bundle();
                bundle.putInt("priceOfPanel", totalPrice);
                bundle.putInt("TotalPayment", totalPayment);
                bundle.putString("Grid", grid);
                bundle.putInt("lowerProduction", lowerProduction);
                bundle.putInt("panels", howManyPanels);
                fragmentBatteryCalculation.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.enter_left_to_right, R.anim.exit_left_to_right, R.anim.enter_right_to_left, R.anim.exit_left_to_right)
                        .replace(R.id.layoutMain, fragmentBatteryCalculation, "FragmentBatteryCalculation")
                        .commit();
            });

            buttonYes.setOnClickListener(v12 -> {
                generatorDialog.dismiss();

                FragmentGeneratorChoice fragmentGeneratorChoice = new FragmentGeneratorChoice();
                Bundle bundle = new Bundle();
                bundle.putInt("priceOfPanel", totalPrice);
                bundle.putInt("TotalPayment", totalPayment);
                bundle.putString("Grid", grid);
                bundle.putInt("lowerProduction", lowerProduction);
                bundle.putInt("panels", howManyPanels);
                fragmentGeneratorChoice.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.enter_left_to_right, R.anim.exit_left_to_right, R.anim.enter_right_to_left, R.anim.exit_left_to_right)
                        .replace(R.id.layoutMain, fragmentGeneratorChoice, "FragmentGeneratorChoice")
                        .commit();
            });
        });
    }
}