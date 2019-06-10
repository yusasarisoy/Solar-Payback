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
    @BindView(R.id.layoutPanels)
    LinearLayout layoutPanels;

    @BindView(R.id.panel1Layout)
    LinearLayout panel1Layout;

    @BindView(R.id.pricingPanel1)
    TextView pricingPanel1;

    @BindView(R.id.pricingPanel2)
    TextView pricingPanel2;

    @BindView(R.id.panel2Layout)
    LinearLayout panel2Layout;

    @BindView(R.id.pricingPanel3)
    TextView pricingPanel3;

    @BindView(R.id.panel3Layout)
    LinearLayout panel3Layout;

    private RequestQueue requestQueue;
    private double liraPerEuro;
    private int panel1, panel2, panel3, mostConsumption, totalPayment, totalConsumption, area_info;
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
        pricingPanel1 = view.findViewById(R.id.pricingPanel1);
        pricingPanel2 = view.findViewById(R.id.pricingPanel2);
        pricingPanel3 = view.findViewById(R.id.pricingPanel3);

        final ProgressDialog progressDialog = ProgressDialog.show(getContext(), getResources().getString(R.string.exchangeRate), getResources().getString(R.string.pleaseWait), true, true);
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

                panel1 = (int) (133 * liraPerEuro);
                panel2 = (int) (104 * liraPerEuro);
                panel3 = (int) (111 * liraPerEuro);

                pricingPanel1.setText(panel1 + " ₺");
                pricingPanel2.setText(panel2 + " ₺");
                pricingPanel3.setText(panel3 + " ₺");

                if (!pricingPanel1.getText().toString().isEmpty())
                    progressDialog.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show());
        requestQueue.add(jsonObjectRequest);
    }

    private void clickPanels() {
        panel1Layout = view.findViewById(R.id.panel1Layout);
        panel2Layout = view.findViewById(R.id.panel2Layout);
        panel3Layout = view.findViewById(R.id.panel3Layout);


        if (getArguments().getString("Grid").equals("On-Grid")) {
            panel1Layout.setOnClickListener(v -> {
                FragmentPanelCalculation fragmentPanelCalculation = new FragmentPanelCalculation();
                Bundle bundle = new Bundle();
                bundle.putString("Panel", "panel1");
                bundle.putDouble("PanelArea", 2.07);
                bundle.putDouble("LiraPerEuro", liraPerEuro);
                bundle.putString("City", cityLocation);
                bundle.putDouble("CityIrradiance", irradianceLocation);
                bundle.putInt("MostConsumption", mostConsumption);
                bundle.putInt("TotalPayment", totalPayment);
                bundle.putInt("TotalConsumption", totalConsumption);
                bundle.putString("Grid", grid);
                bundle.putInt("AreaInfo", (int) (area_info / 2.07));
                bundle.putString("choice", getArguments().getString("choice"));
                bundle.putInt("Area", area_info);
                fragmentPanelCalculation.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.layoutMain, fragmentPanelCalculation, "FragmentPanelCalculation")
                        .commit();
            });

            panel2Layout.setOnClickListener(v -> {
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
                bundle.putInt("AreaInfo", (int) (area_info / 1.67));
                bundle.putString("choice", getArguments().getString("choice"));
                bundle.putInt("Area", area_info);
                fragmentPanelCalculation.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.layoutMain, fragmentPanelCalculation, "FragmentPanelCalculation")
                        .commit();
            });

            panel3Layout.setOnClickListener(v -> {
                FragmentPanelCalculation fragmentPanelCalculation = new FragmentPanelCalculation();
                Bundle bundle = new Bundle();
                bundle.putString("Panel", "panel3");
                bundle.putDouble("PanelArea", 1.67);
                bundle.putDouble("LiraPerEuro", liraPerEuro);
                bundle.putString("City", cityLocation);
                bundle.putDouble("CityIrradiance", irradianceLocation);
                bundle.putInt("MostConsumption", mostConsumption);
                bundle.putInt("TotalPayment", totalPayment);
                bundle.putInt("TotalConsumption", totalConsumption);
                bundle.putString("Grid", grid);
                bundle.putInt("AreaInfo", (int) (area_info / 1.67));
                bundle.putString("choice", getArguments().getString("choice"));
                bundle.putInt("Area", area_info);
                fragmentPanelCalculation.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.layoutMain, fragmentPanelCalculation, "FragmentPanelCalculation")
                        .commit();
            });
        } else if (getArguments().getString("Grid").equals("Off-Grid")) {
            panel1Layout.setOnClickListener(v -> {
                FragmentPanelCalculation fragmentPanelCalculation = new FragmentPanelCalculation();
                Bundle bundle = new Bundle();
                bundle.putString("Panel", "panel1");
                bundle.putDouble("PanelArea", 2.07);
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
                        .replace(R.id.layoutMain, fragmentPanelCalculation, "FragmentPanelCalculation")
                        .commit();
            });

            panel2Layout.setOnClickListener(v -> {
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
                        .replace(R.id.layoutMain, fragmentPanelCalculation, "FragmentPanelCalculation")
                        .commit();
            });

            panel3Layout.setOnClickListener(v -> {
                FragmentPanelCalculation fragmentPanelCalculation = new FragmentPanelCalculation();
                Bundle bundle = new Bundle();
                bundle.putString("Panel", "panel3");
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
                        .replace(R.id.layoutMain, fragmentPanelCalculation, "FragmentPanelCalculation")
                        .commit();
            });
        }
    }
}