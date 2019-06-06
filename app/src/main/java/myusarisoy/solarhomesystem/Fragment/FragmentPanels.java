package myusarisoy.solarhomesystem.Fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class FragmentPanels extends Fragment {
    @BindView(R.id.layout_panels)
    LinearLayout layout_panels;

    @BindView(R.id.panel_1_layout)
    LinearLayout panel_1;

    @BindView(R.id.pricing_panel_1)
    TextView pricing_panel_1;

    @BindView(R.id.pricing_panel_2)
    TextView pricing_panel_2;

    @BindView(R.id.panel_2_layout)
    LinearLayout panel_2;

    private RequestQueue requestQueue;
    private double liraPerEuro;
    private int panel1, panel2, mostConsumption, totalPayment, totalConsumption, area_info;
    private String base, cityLocation, grid;
    public double irradianceLocation;
    View view;

    public static FragmentPanels newInstance(Object... objects) {
        FragmentPanels fragment = new FragmentPanels();
        Bundle args = new Bundle();
        args.putString("city", (String) objects[0]);
        args.putDouble("irradiance", (Double) objects[1]);
        args.putInt("consumption", (Integer) objects[2]);
        args.putInt("TotalPayment", (Integer) objects[3]);
        args.putInt("TotalConsumption", (Integer) objects[4]);
        args.putString("Grid", (String) objects[5]);
        args.putInt("AreaInfo", (Integer) objects[6]);
        args.putString("choice", (String) objects[7]);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_panels, container, false);

        cityLocation = getArguments().getString("City");
        irradianceLocation = getArguments().getDouble("CityIrradiance");
        mostConsumption = getArguments().getInt("MostConsumption");
        totalPayment = getArguments().getInt("TotalPayment");
        totalConsumption = getArguments().getInt("TotalConsumption");
        grid = getArguments().getString("Grid");

        if (grid.equals("On-Grid"))
            area_info = getArguments().getInt("AreaInfo");

//       Get currency.
        getCurrency();

//        Go to panel calculation with click.
        clickPanels();

        return view;
    }

    private void getCurrency() {
        pricing_panel_1 = view.findViewById(R.id.pricing_panel_1);
        pricing_panel_2 = view.findViewById(R.id.pricing_panel_2);

        final ProgressDialog progressDialog = ProgressDialog.show(getContext(), getResources().getString(R.string.exchange_rate), getResources().getString(R.string.please_wait), true, true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        String currencyAPI = "https://api.exchangeratesapi.io/latest?base=EUR";

        requestQueue = Volley.newRequestQueue(getContext());
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, currencyAPI, null, response -> {
            try {
                base = response.getString("base");
                JSONObject jsonObject = response.getJSONObject("rates");
                liraPerEuro = jsonObject.getDouble("TRY");

                panel1 = (int) (145.53 * liraPerEuro);
                panel2 = (int) (110.25 * liraPerEuro);

                pricing_panel_1.setText(panel1 + " ₺");
                pricing_panel_2.setText(panel2 + " ₺");

                if (!pricing_panel_1.getText().toString().isEmpty())
                    progressDialog.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show());
        requestQueue.add(jsonObjectRequest);
    }

    private void clickPanels() {
        panel_1 = view.findViewById(R.id.panel_1_layout);
        panel_2 = view.findViewById(R.id.panel_2_layout);

        if (getArguments().getString("Grid").equals("On-Grid")) {
            panel_1.setOnClickListener(v -> {
                FragmentPanelCalculation fragmentPanelCalculation = new FragmentPanelCalculation();
                Bundle bundle = new Bundle();
                bundle.putString("Panel", "panel1");
                bundle.putDouble("PanelArea", 1.685);
                bundle.putDouble("LiraPerEuro", liraPerEuro);
                bundle.putString("City", cityLocation);
                bundle.putDouble("CityIrradiance", irradianceLocation);
                bundle.putInt("MostConsumption", mostConsumption);
                bundle.putInt("TotalPayment", totalPayment);
                bundle.putInt("TotalConsumption", totalConsumption);
                bundle.putString("Grid", grid);
                bundle.putInt("AreaInfo", (int) (area_info / 1.685));
                bundle.putString("choice", getArguments().getString("choice"));
                bundle.putInt("Area", area_info);
                fragmentPanelCalculation.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.layout_main, fragmentPanelCalculation, "FragmentPanelCalculation")
                        .commit();
            });

            panel_2.setOnClickListener(v -> {
                FragmentPanelCalculation fragmentPanelCalculation = new FragmentPanelCalculation();
                Bundle bundle = new Bundle();
                bundle.putString("Panel", "panel2");
                bundle.putDouble("PanelArea", 1.67);
                bundle.putDouble("LiraPerEuro", liraPerEuro);
                bundle.putString("City", cityLocation);
                bundle.putDouble("CityIrradiance", irradianceLocation);
                bundle.putInt("MostConsumption", mostConsumption);
                bundle.putInt("TotalPayment", totalPayment);
                bundle.putInt("TotalConsumption", totalConsumption);
                bundle.putString("Grid", grid);
                bundle.putInt("AreaInfo", (int) (area_info / 1.685));
                bundle.putString("choice", getArguments().getString("choice"));
                bundle.putInt("Area", area_info);
                fragmentPanelCalculation.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.layout_main, fragmentPanelCalculation, "FragmentPanelCalculation")
                        .commit();
            });
        } else if (getArguments().getString("Grid").equals("Off-Grid")) {
            panel_1.setOnClickListener(v -> {
                FragmentPanelCalculation fragmentPanelCalculation = new FragmentPanelCalculation();
                Bundle bundle = new Bundle();
                bundle.putString("Panel", "panel1");
                bundle.putDouble("PanelArea", 1.685);
                bundle.putDouble("LiraPerEuro", liraPerEuro);
                bundle.putString("City", cityLocation);
                bundle.putDouble("CityIrradiance", irradianceLocation);
                bundle.putInt("MostConsumption", mostConsumption);
                bundle.putInt("TotalPayment", totalPayment);
                bundle.putInt("TotalConsumption", totalConsumption);
                bundle.putString("Grid", grid);
                bundle.putInt("AreaInfo", 0);
                bundle.putString("choice", getArguments().getString("choice"));
                bundle.putInt("Area", area_info);
                fragmentPanelCalculation.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.layout_main, fragmentPanelCalculation, "FragmentPanelCalculation")
                        .commit();
            });

            panel_2.setOnClickListener(v -> {
                FragmentPanelCalculation fragmentPanelCalculation = new FragmentPanelCalculation();
                Bundle bundle = new Bundle();
                bundle.putString("Panel", "panel2");
                bundle.putDouble("PanelArea", 1.67);
                bundle.putDouble("LiraPerEuro", liraPerEuro);
                bundle.putString("City", cityLocation);
                bundle.putDouble("CityIrradiance", irradianceLocation);
                bundle.putInt("MostConsumption", mostConsumption);
                bundle.putInt("TotalPayment", totalPayment);
                bundle.putInt("TotalConsumption", totalConsumption);
                bundle.putString("Grid", grid);
                bundle.putInt("AreaInfo", 0);
                bundle.putString("choice", getArguments().getString("choice"));
                bundle.putInt("Area", area_info);
                fragmentPanelCalculation.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.layout_main, fragmentPanelCalculation, "FragmentPanelCalculation")
                        .commit();
            });
        }
    }
}