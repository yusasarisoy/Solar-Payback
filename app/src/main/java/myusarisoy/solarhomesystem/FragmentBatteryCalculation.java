package myusarisoy.solarhomesystem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import butterknife.BindView;

public class FragmentBatteryCalculation extends Fragment {
    @BindView(R.id.layout_battery_calculation)
    LinearLayout layout_battery_calculation;

    @BindView(R.id.layout_prices)
    LinearLayout layout_prices;

    @BindView(R.id.panel_price)
    TextView panel_price;

    @BindView(R.id.heater_price)
    TextView heater_price;

    @BindView(R.id.inverter_price)
    TextView inverter_price;

    @BindView(R.id.battery_price)
    TextView battery_price;

    @BindView(R.id.layout_costs)
    LinearLayout layout_costs;

    @BindView(R.id.inspection_cost)
    TextView inspection_cost;

    @BindView(R.id.cleaning_cost)
    TextView cleaning_cost;

    @BindView(R.id.electricity_cost)
    TextView electricity_cost;

    @BindView(R.id.total_price)
    TextView total_price;

    @BindView(R.id.payback_period)
    TextView payback_period;

    @BindView(R.id.layout_back)
    LinearLayout layout_back;

    @BindView(R.id.layout_next)
    LinearLayout layout_next;

    @BindView(R.id.layout_main_page)
    LinearLayout layout_main_page;

    @BindView(R.id.button_back)
    Button button_back;

    @BindView(R.id.button_next)
    Button button_next;

    @BindView(R.id.image_main_page)
    ImageView image_main_page;

    AppCompatDialog dialog_main_page;
    Button no_main_page, yes_main_page;
    private CountDownTimer countDownTimer;
    private RequestQueue requestQueueUSD, requestQueueTRY;
    private boolean successUSD, successTRY;
    private double dollarPerEuro, liraPerEuro, paybackYear;
    private int timestampUSD, timestampTRY, totalPrice;
    private String baseUSD, baseTRY, dateUSD, dateTRY;
    private int panelPrice, totalPayment, heaterPrice = 1800, inverterPrice = 3595, batteryPrice = 12980, inspectionCost = 150, cleaningCost = 2500, heater, inverter, battery, inspection, cleaning;
    View view;

    public static FragmentBatteryCalculation newInstance(Object... objects) {
        FragmentBatteryCalculation fragment = new FragmentBatteryCalculation();
        Bundle args = new Bundle();
        args.putInt("panelPrice", (Integer) objects[0]);
        args.putInt("TotalPayment", (Integer) objects[1]);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_battery_calculation, container, false);

//        Get currency;
        getCurrency();

//        Get results.
        getResults();

        return view;
    }

    private void getCurrency() {
        final ProgressDialog progressDialog = ProgressDialog.show(getContext(), "Getting exchange rates", "Please wait...", true, true);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        countDownTimer = new CountDownTimer(2000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
//                Get EUR/USD.
                String currencyUSD = "http://data.fixer.io/api/latest?access_key=5471e8c810ea396b3146e028c7d68ecb&base=EUR&symbols=USD";

                requestQueueUSD = Volley.newRequestQueue(getContext());
                final JsonObjectRequest jsonObjectRequestUSD = new JsonObjectRequest(Request.Method.GET, currencyUSD, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            successUSD = response.getBoolean("success");
                            timestampUSD = response.getInt("timestamp");
                            baseUSD = response.getString("base");
                            dateUSD = response.getString("date");
                            JSONObject jsonObject = response.getJSONObject("rates");
                            dollarPerEuro = jsonObject.getDouble("USD");

                            heater_price = view.findViewById(R.id.heater_price);
                            inverter_price = view.findViewById(R.id.inverter_price);
                            battery_price = view.findViewById(R.id.battery_price);
                            inspection_cost = view.findViewById(R.id.inspection_cost);
                            cleaning_cost = view.findViewById(R.id.cleaning_cost);

                            heater = (int) (heaterPrice / dollarPerEuro);
                            inverter = (int) (inverterPrice / dollarPerEuro);
                            battery = (int) (batteryPrice / dollarPerEuro);
                            inspection = (int) (inspectionCost / dollarPerEuro);
                            cleaning = (int) (cleaningCost / dollarPerEuro);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                requestQueueUSD.add(jsonObjectRequestUSD);

//                Get EUR/TRY.
                String currencyTRY = "http://data.fixer.io/api/latest?access_key=5471e8c810ea396b3146e028c7d68ecb&base=EUR&symbols=TRY";

                requestQueueTRY = Volley.newRequestQueue(getContext());
                final JsonObjectRequest jsonObjectRequestTRY = new JsonObjectRequest(Request.Method.GET, currencyTRY, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            successTRY = response.getBoolean("success");
                            timestampTRY = response.getInt("timestamp");
                            baseTRY = response.getString("base");
                            dateTRY = response.getString("date");
                            JSONObject jsonObject = response.getJSONObject("rates");
                            liraPerEuro = jsonObject.getDouble("TRY");

                            panel_price = view.findViewById(R.id.panel_price);
                            heater_price = view.findViewById(R.id.heater_price);
                            inverter_price = view.findViewById(R.id.inverter_price);
                            battery_price = view.findViewById(R.id.battery_price);
                            inspection_cost = view.findViewById(R.id.inspection_cost);
                            cleaning_cost = view.findViewById(R.id.cleaning_cost);
                            electricity_cost = view.findViewById(R.id.electricity_cost);
                            total_price = view.findViewById(R.id.total_price);
                            payback_period = view.findViewById(R.id.payback_period);

                            panelPrice = getArguments().getInt("panelPrice");
                            totalPayment = getArguments().getInt("TotalPayment");

                            int finalHeater = (int) (heater * liraPerEuro);
                            int finalInverter = (int) (inverter * liraPerEuro);
                            int finalBattery = (int) (battery * liraPerEuro);
                            int finalInspection = (int) (inspection * liraPerEuro * 25);
                            int finalCleaning = (int) (cleaning * liraPerEuro * 5);

                            totalPrice = (int) (panelPrice + (heater * liraPerEuro) + (inverter * liraPerEuro) + (battery * liraPerEuro) + (inspection * liraPerEuro * 25) + (cleaning * liraPerEuro * 5));
                            paybackYear = totalPrice / totalPayment;

                            panel_price.setText("Panel Price: " + ((panelPrice)) + " ₺");
                            heater_price.setText("Heater Price: " + finalHeater + " ₺");
                            inverter_price.setText("Inverter Price: " + finalInverter + " ₺");
                            battery_price.setText("Battery Price: " + finalBattery + " ₺");
                            inspection_cost.setText("Inspection Cost: " + finalInspection + " ₺");
                            cleaning_cost.setText("Cleaning Cost: " + finalCleaning + " ₺");
                            electricity_cost.setText("Electricity Cost: " + (totalPayment) + " ₺");
                            total_price.setText("Total Price: " + (totalPrice) + " ₺");
                            payback_period.setText("Payback Period: " + paybackYear + " year(s)");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                requestQueueTRY.add(jsonObjectRequestTRY);
            }

            @Override
            public void onFinish() {
                countDownTimer.cancel();
                progressDialog.dismiss();
            }
        }.start();
    }

    private void getResults() {
        layout_prices = view.findViewById(R.id.layout_prices);
        layout_costs = view.findViewById(R.id.layout_costs);
        layout_back = view.findViewById(R.id.layout_back);
        layout_next = view.findViewById(R.id.layout_next);
        layout_main_page = view.findViewById(R.id.layout_main_page);
        button_back = view.findViewById(R.id.button_back);
        button_next = view.findViewById(R.id.button_next);
        image_main_page = view.findViewById(R.id.image_main_page);

        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_prices.setVisibility(View.VISIBLE);
                layout_costs.setVisibility(View.GONE);
                layout_back.setVisibility(View.GONE);
                layout_next.setVisibility(View.VISIBLE);
                layout_main_page.setVisibility(View.GONE);
            }
        });

        button_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_prices.setVisibility(View.GONE);
                layout_costs.setVisibility(View.VISIBLE);
                layout_back.setVisibility(View.VISIBLE);
                layout_next.setVisibility(View.GONE);
                layout_main_page.setVisibility(View.VISIBLE);
            }
        });

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
}