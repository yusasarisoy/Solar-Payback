package myusarisoy.solarhomesystem;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;

public class FragmentPanels extends Fragment {
    @BindView(R.id.layout_panels)
    LinearLayout layout_panels;

    @BindView(R.id.img_panel_1)
    ImageView panel_1;

    @BindView(R.id.pricing_panel_1)
    TextView pricing_panel_1;

    @BindView(R.id.pricing_panel_2)
    TextView pricing_panel_2;

    @BindView(R.id.img_panel_2)
    ImageView panel_2;

    private RequestQueue requestQueue;
    private boolean success;
    private double liraPerEuro;
    private int timestamp, panel1, panel2, mostConsumption, totalPayment;
    private String base, date, cityLocation;
    public double irradianceLocation;
    View view;

    public static FragmentPanels newInstance(Object... objects) {
        FragmentPanels fragment = new FragmentPanels();
        Bundle args = new Bundle();
        args.putString("city", (String) objects[0]);
        args.putDouble("irradiance", (Double) objects[1]);
        args.putInt("consumption", (Integer) objects[2]);
        args.putInt("TotalPayment", (Integer) objects[3]);
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

//       Get currency.
        getCurrency();

//        Go to panel calculation with click.
        clickPanels();

        return view;
    }

    private void getCurrency() {
        pricing_panel_1 = view.findViewById(R.id.pricing_panel_1);
        pricing_panel_2 = view.findViewById(R.id.pricing_panel_2);

        final ProgressDialog progressDialog = ProgressDialog.show(getContext(), "Getting exchange rates", "Please wait...", true, true);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        Thread thread = new Thread(() -> {
            String currencyAPI = "http://data.fixer.io/api/latest?access_key=5471e8c810ea396b3146e028c7d68ecb&%20base=EUR&symbols=TRY";

            requestQueue = Volley.newRequestQueue(getContext());
            final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, currencyAPI, null, response -> {
                try {
                    success = response.getBoolean("success");
                    timestamp = response.getInt("timestamp");
                    base = response.getString("base");
                    date = response.getString("date");
                    JSONObject jsonObject = response.getJSONObject("rates");
                    liraPerEuro = jsonObject.getDouble("TRY");

                    panel1 = (int) (145.53 * liraPerEuro);
                    panel2 = (int) (110.25 * liraPerEuro);

                    pricing_panel_1.setText(panel1 + " ₺");
                    pricing_panel_2.setText(panel2 + " ₺");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, error -> Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show());
            requestQueue.add(jsonObjectRequest);
        });
        thread.start();

        progressDialog.dismiss();
    }

    private void clickPanels() {
        panel_1 = view.findViewById(R.id.img_panel_1);
        panel_2 = view.findViewById(R.id.img_panel_2);

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
            fragmentPanelCalculation.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.layout_main, fragmentPanelCalculation, "FragmentPanelCalculation")
                    .commit();
        });
    }
}