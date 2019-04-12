package myusarisoy.solarhomesystem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

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
    private RequestQueue requestQueueUSD;
    private double liraPerDollar, paybackYear;
    private int totalPrice;
    private String baseUSD;
    private int panelPrice, totalPayment, heaterPrice = 1800, inverterPrice = 3595, batteryPrice = 12980, inspectionCost = 150, cleaningCost = 2500;
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
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

//            Get USD/TRY.
        String currencyUSD = "https://api.exchangeratesapi.io/latest?base=USD";

        requestQueueUSD = Volley.newRequestQueue(getContext());
        final JsonObjectRequest jsonObjectRequestUSD = new JsonObjectRequest(Request.Method.GET, currencyUSD, null, response -> {
            try {
                baseUSD = response.getString("base");
                JSONObject jsonObject = response.getJSONObject("rates");
                liraPerDollar = jsonObject.getDouble("TRY");

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

                int finalHeater = (int) (heaterPrice * liraPerDollar);
                int finalInverter = (int) (inverterPrice * liraPerDollar);
                int finalBattery = (int) (batteryPrice * liraPerDollar);
                int finalInspection = (int) (inspectionCost * liraPerDollar * 25);
                int finalCleaning = (int) (cleaningCost * liraPerDollar * 5);

                totalPrice = panelPrice + finalHeater + finalInverter + finalBattery + finalInspection + finalCleaning;
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

                if (!heater_price.getText().toString().isEmpty())
                    progressDialog.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show());
        requestQueueUSD.add(jsonObjectRequestUSD);
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

        button_back.setOnClickListener(v -> {
            layout_prices.setVisibility(View.VISIBLE);
            layout_costs.setVisibility(View.GONE);
            layout_back.setVisibility(View.GONE);
            layout_next.setVisibility(View.VISIBLE);
            layout_main_page.setVisibility(View.GONE);
        });

        button_next.setOnClickListener(v -> {
            layout_prices.setVisibility(View.GONE);
            layout_costs.setVisibility(View.VISIBLE);
            layout_back.setVisibility(View.VISIBLE);
            layout_next.setVisibility(View.GONE);
            layout_main_page.setVisibility(View.VISIBLE);

            if (paybackYear > 10)
                showSnackbar("Payback period is too long. You should use On-Grid method.");
        });

        image_main_page.setOnClickListener(v -> {
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

            no_main_page.setOnClickListener(v1 -> dialog_main_page.dismiss());

            yes_main_page.setOnClickListener(v12 -> {
                dialog_main_page.dismiss();

                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            });
        });
    }

    public void showSnackbar(String text) {
        layout_battery_calculation = view.findViewById(R.id.layout_battery_calculation);

        Snackbar snackbar = Snackbar.make(layout_battery_calculation, text, Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(getResources().getColor(R.color.dark_slate_gray));
        TextView textView = snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(getResources().getColor(R.color.colorPrimary));
        snackbar.show();
    }
}