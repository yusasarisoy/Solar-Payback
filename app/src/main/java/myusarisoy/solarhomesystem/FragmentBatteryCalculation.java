package myusarisoy.solarhomesystem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDialog;
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

    @BindView(R.id.img_info)
    ImageView info;

    @BindView(R.id.layout_prices)
    LinearLayout layout_prices;

    @BindView(R.id.battery_layout)
    LinearLayout battery_layout;

    @BindView(R.id.inspection_layout)
    LinearLayout inspection_layout;

    @BindView(R.id.cleaning_layout)
    LinearLayout cleaning_layout;

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

    AppCompatDialog dialog_main_page, infoDialog, masterBatteryDialog, batteryOptionsDialog;
    Button no_main_page, yes_main_page, confirmBack, closeInfo;
    EditText heater, inverter, battery, inspection, cleaning;
    ImageView battery1, battery2;
    LinearLayout master_battery_layout, master_inspection_layout, master_cleaning_layout;
    TextView batteryPrice1, batteryPrice2;
    private RequestQueue requestQueueUSD;
    private double liraPerDollar, paybackYear;
    private int totalPrice, lowerProduction, panels, finalBattery;
    private String baseUSD, experience, grid;
    private int panelPrice, totalPayment, heaterPrice = 1800, inverterPrice = 150, batteryOption1 = 1000, batteryOption2 = 200, inspectionCost = 2, cleaningCost = 10;
    View view;

    public static FragmentBatteryCalculation newInstance(Object... objects) {
        FragmentBatteryCalculation fragment = new FragmentBatteryCalculation();
        Bundle args = new Bundle();
        args.putInt("panelPrice", (Integer) objects[0]);
        args.putInt("TotalPayment", (Integer) objects[1]);
        args.putString("Grid", (String) objects[2]);
        args.putInt("lowerProduction", (Integer) objects[3]);
        args.putInt("panels", (Integer) objects[4]);
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

        grid = getArguments().getString("Grid");
        lowerProduction = getArguments().getInt("lowerProduction");
        panels = getArguments().getInt("panels");

//        Get info about the app.
        getInfo();

//        Get currency;
        getCurrency();

//        Get results.
        getResults();

        return view;
    }

    private void getInfo() {
        info = view.findViewById(R.id.img_info);

        info.setOnClickListener(v -> {
            android.support.v7.app.AlertDialog.Builder reservationBuilder = new android.support.v7.app.AlertDialog.Builder(getContext());
            reservationBuilder.setView(R.layout.dialog_info);
            infoDialog = reservationBuilder.create();
            WindowManager.LayoutParams params = infoDialog.getWindow().getAttributes();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels;
            int height = displayMetrics.heightPixels;
            params.width = (int) (width * 0.9);
            params.height = (int) (height * 0.9);
            infoDialog.getWindow().setAttributes(params);
            infoDialog.show();

            closeInfo = infoDialog.findViewById(R.id.close_info);

            closeInfo.setOnClickListener(v1 -> infoDialog.dismiss());
        });
    }

    private void getCurrency() {
//        Get user's experience status about the solar energy.
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Experience", 0);
        experience = sharedPreferences.getString("Experience", "");

        if (grid.equals("On-Grid")) {
            battery_layout = view.findViewById(R.id.battery_layout);
            inspection_layout = view.findViewById(R.id.inspection_layout);
            cleaning_layout = view.findViewById(R.id.cleaning_layout);

            battery_layout.setVisibility(View.GONE);
            inspection_layout.setVisibility(View.GONE);
            cleaning_layout.setVisibility(View.GONE);

            if (experience.equals("Expert")) {
                android.support.v7.app.AlertDialog.Builder reservationBuilderMaster = new android.support.v7.app.AlertDialog.Builder(getContext());
                reservationBuilderMaster.setView(R.layout.dialog_master_battery);
                masterBatteryDialog = reservationBuilderMaster.create();
                WindowManager.LayoutParams paramsMaster = masterBatteryDialog.getWindow().getAttributes();
                DisplayMetrics displayMetricsMaster = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetricsMaster);
                int widthMaster = displayMetricsMaster.widthPixels;
                int heightMaster = displayMetricsMaster.heightPixels;
                paramsMaster.width = (int) (widthMaster * 0.7);
                paramsMaster.height = (int) (heightMaster * 0.7);
                masterBatteryDialog.getWindow().setAttributes(paramsMaster);
                masterBatteryDialog.show();

                heater = masterBatteryDialog.findViewById(R.id.heater_price);
                inverter = masterBatteryDialog.findViewById(R.id.inverter_price);
                battery = masterBatteryDialog.findViewById(R.id.battery_price);
                inspection = masterBatteryDialog.findViewById(R.id.inspection_cost);
                cleaning = masterBatteryDialog.findViewById(R.id.cleaning_cost);
                confirmBack = masterBatteryDialog.findViewById(R.id.confirm_back);
                master_battery_layout = masterBatteryDialog.findViewById(R.id.master_battery_layout);
                master_inspection_layout = masterBatteryDialog.findViewById(R.id.master_inspection_layout);
                master_cleaning_layout = masterBatteryDialog.findViewById(R.id.master_cleaning_layout);

                master_battery_layout.setVisibility(View.GONE);
                master_inspection_layout.setVisibility(View.GONE);
                master_cleaning_layout.setVisibility(View.GONE);

                confirmBack.setOnClickListener(v -> {
                    masterBatteryDialog.dismiss();

                    battery_layout = view.findViewById(R.id.battery_layout);
                    inspection_layout = view.findViewById(R.id.inspection_layout);
                    cleaning_layout = view.findViewById(R.id.cleaning_layout);
                    panel_price = view.findViewById(R.id.panel_price);
                    heater_price = view.findViewById(R.id.heater_price);
                    inverter_price = view.findViewById(R.id.inverter_price);
                    electricity_cost = view.findViewById(R.id.electricity_cost);
                    total_price = view.findViewById(R.id.total_price);
                    payback_period = view.findViewById(R.id.payback_period);

                    panelPrice = getArguments().getInt("panelPrice");
                    totalPayment = getArguments().getInt("TotalPayment");

                    int finalHeater = Integer.parseInt(heater.getText().toString());
                    int finalInverter = Integer.parseInt(inverter.getText().toString());

                    totalPrice = panelPrice + finalHeater + finalInverter;
                    paybackYear = totalPrice / totalPayment;

                    panel_price.setText(getResources().getString(R.string.panel_price) + ((panelPrice)) + " ₺");
                    heater_price.setText(getResources().getString(R.string.heater_price) + finalHeater + " ₺");
                    inverter_price.setText(getResources().getString(R.string.inverter_price) + finalInverter + " ₺");
                    electricity_cost.setText(getResources().getString(R.string.electricity_cost_and_income) + (totalPayment) + " ₺");
                    total_price.setText(getResources().getString(R.string.total_price) + (totalPrice + lowerProduction) + " ₺");
                    payback_period.setText(getResources().getString(R.string.payback_period) + paybackYear + getResources().getString(R.string.years));
                });
            } else if (experience.equals("Beginner")) {
                final ProgressDialog progressDialog = ProgressDialog.show(getContext(), getResources().getString(R.string.exchange_rate), getResources().getString(R.string.please_wait), true, true);
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
                        electricity_cost = view.findViewById(R.id.electricity_cost);
                        total_price = view.findViewById(R.id.total_price);
                        payback_period = view.findViewById(R.id.payback_period);

                        panelPrice = getArguments().getInt("panelPrice");
                        totalPayment = getArguments().getInt("TotalPayment");

                        int finalHeater = (int) (heaterPrice * liraPerDollar);
                        int finalInverter = (int) (inverterPrice * liraPerDollar);

                        totalPrice = panelPrice + finalHeater + finalInverter;
                        paybackYear = totalPrice / totalPayment;

                        panel_price.setText(getResources().getString(R.string.panel_price) + ((panelPrice)) + " ₺");
                        heater_price.setText(getResources().getString(R.string.heater_price) + finalHeater + " ₺");
                        inverter_price.setText(getResources().getString(R.string.inverter_price) + finalInverter + " ₺");
                        electricity_cost.setText(getResources().getString(R.string.electricity_cost_and_income) + (totalPayment) + " ₺");
                        total_price.setText(getResources().getString(R.string.total_price) + (totalPrice + lowerProduction) + " ₺");
                        payback_period.setText(getResources().getString(R.string.payback_period) + paybackYear + getResources().getString(R.string.years));

                        if (!heater_price.getText().toString().isEmpty())
                            progressDialog.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show());
                requestQueueUSD.add(jsonObjectRequestUSD);
            }
        } else if (grid.equals("Off-Grid")) {
            if (experience.equals("Expert")) {
                android.support.v7.app.AlertDialog.Builder reservationBuilderMaster = new android.support.v7.app.AlertDialog.Builder(getContext());
                reservationBuilderMaster.setView(R.layout.dialog_master_battery);
                masterBatteryDialog = reservationBuilderMaster.create();
                WindowManager.LayoutParams paramsMaster = masterBatteryDialog.getWindow().getAttributes();
                DisplayMetrics displayMetricsMaster = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetricsMaster);
                int widthMaster = displayMetricsMaster.widthPixels;
                int heightMaster = displayMetricsMaster.heightPixels;
                paramsMaster.width = (int) (widthMaster * 0.7);
                paramsMaster.height = (int) (heightMaster * 0.7);
                masterBatteryDialog.getWindow().setAttributes(paramsMaster);
                masterBatteryDialog.show();

                heater = masterBatteryDialog.findViewById(R.id.heater_price);
                inverter = masterBatteryDialog.findViewById(R.id.inverter_price);
                battery = masterBatteryDialog.findViewById(R.id.battery_price);
                inspection = masterBatteryDialog.findViewById(R.id.inspection_cost);
                cleaning = masterBatteryDialog.findViewById(R.id.cleaning_cost);
                confirmBack = masterBatteryDialog.findViewById(R.id.confirm_back);

                confirmBack.setOnClickListener(v -> {
                    masterBatteryDialog.dismiss();

                    battery_layout = view.findViewById(R.id.battery_layout);
                    inspection_layout = view.findViewById(R.id.inspection_layout);
                    cleaning_layout = view.findViewById(R.id.cleaning_layout);
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

                    int finalHeater = Integer.parseInt(heater.getText().toString());
                    int finalInverter = Integer.parseInt(inverter.getText().toString());
                    int finalBattery = Integer.parseInt(battery.getText().toString());
                    int finalInspection = Integer.parseInt(inspection.getText().toString());
                    int finalCleaning = Integer.parseInt(cleaning.getText().toString());

                    totalPrice = panelPrice + finalHeater + finalInverter + finalBattery + finalInspection + finalCleaning;
                    paybackYear = totalPrice / totalPayment;

                    panel_price.setText(getResources().getString(R.string.panel_price) + ((panelPrice)) + " ₺");
                    heater_price.setText(getResources().getString(R.string.heater_price) + finalHeater + " ₺");
                    inverter_price.setText(getResources().getString(R.string.inverter_price) + finalInverter + " ₺");
                    battery_price.setText(getResources().getString(R.string.battery_price) + finalBattery + " ₺");
                    inspection_cost.setText(getResources().getString(R.string.inspection_cost) + finalInspection + " ₺");
                    cleaning_cost.setText(getResources().getString(R.string.cleaning_cost) + finalCleaning + " ₺");
                    electricity_cost.setText(getResources().getString(R.string.electricity_cost) + (totalPayment) + " ₺");
                    total_price.setText(getResources().getString(R.string.total_price) + (totalPrice + lowerProduction) + " ₺");
                    payback_period.setText(getResources().getString(R.string.payback_period) + paybackYear + getResources().getString(R.string.years));
                });
            } else if (experience.equals("Beginner")) {
                final ProgressDialog progressDialog = ProgressDialog.show(getContext(), getResources().getString(R.string.exchange_rate), getResources().getString(R.string.please_wait), true, true);
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

                        android.support.v7.app.AlertDialog.Builder reservationBuilder = new android.support.v7.app.AlertDialog.Builder(getContext());
                        reservationBuilder.setView(R.layout.pop_up_battery);
                        batteryOptionsDialog = reservationBuilder.create();
                        WindowManager.LayoutParams params = batteryOptionsDialog.getWindow().getAttributes();
                        DisplayMetrics displayMetrics = new DisplayMetrics();
                        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                        int width = displayMetrics.widthPixels;
                        int height = displayMetrics.heightPixels;
                        params.width = (int) (width * 0.9);
                        params.height = (int) (height * 0.9);
                        batteryOptionsDialog.getWindow().setAttributes(params);
                        batteryOptionsDialog.setCancelable(false);
                        batteryOptionsDialog.show();

                        battery1 = batteryOptionsDialog.findViewById(R.id.img_battery_1);
                        battery2 = batteryOptionsDialog.findViewById(R.id.img_battery_2);
                        batteryPrice1 = batteryOptionsDialog.findViewById(R.id.pricing_battery_1);
                        batteryPrice2 = batteryOptionsDialog.findViewById(R.id.pricing_battery_2);

                        batteryPrice1.setText((int) (batteryOption1 * liraPerDollar) + " ₺");
                        batteryPrice2.setText((int) (batteryOption2 * liraPerDollar) + " ₺");

                        battery1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finalBattery = (int) (batteryOption1 * liraPerDollar);
                                battery_price.setText(getResources().getString(R.string.battery_price) + finalBattery + " ₺");
                                batteryOptionsDialog.dismiss();
                            }
                        });

                        battery2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finalBattery = (int) (batteryOption2 * liraPerDollar);
                                battery_price.setText(getResources().getString(R.string.battery_price) + finalBattery + " ₺");
                                batteryOptionsDialog.dismiss();
                            }
                        });

                        panelPrice = getArguments().getInt("panelPrice");
                        totalPayment = getArguments().getInt("TotalPayment");

                        int finalHeater = (int) (heaterPrice * liraPerDollar);
                        int finalInverter = (int) (inverterPrice * liraPerDollar);
                        int finalInspection = (int) (panels * inspectionCost * liraPerDollar * 25);
                        int finalCleaning = (int) (cleaningCost * panels * liraPerDollar * 5);

                        totalPrice = panelPrice + finalHeater + finalInverter + finalBattery + finalInspection + finalCleaning;
                        paybackYear = totalPrice / totalPayment;

                        panel_price.setText(getResources().getString(R.string.panel_price) + ((panelPrice)) + " ₺");
                        heater_price.setText(getResources().getString(R.string.heater_price) + finalHeater + " ₺");
                        inverter_price.setText(getResources().getString(R.string.inverter_price) + finalInverter + " ₺");
                        inspection_cost.setText(getResources().getString(R.string.inspection_cost) + finalInspection + " ₺");
                        cleaning_cost.setText(getResources().getString(R.string.cleaning_cost) + finalCleaning + " ₺");
                        electricity_cost.setText(getResources().getString(R.string.electricity_cost) + (totalPayment) + " ₺");
                        total_price.setText(getResources().getString(R.string.total_price) + (totalPrice + lowerProduction) + " ₺");
                        payback_period.setText(getResources().getString(R.string.payback_period) + paybackYear + getResources().getString(R.string.years));

                        if (!heater_price.getText().toString().isEmpty())
                            progressDialog.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show());
                requestQueueUSD.add(jsonObjectRequestUSD);
            }
        }
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

            if (grid.equals("Off-Grid") && paybackYear > 10)
                showSnackbar(getResources().getString(R.string.long_payback_period));
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

            yes_main_page.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog_main_page.dismiss();

                    Intent intent = new Intent(getContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });
        });
    }

    public void showSnackbar(String text) {
        layout_battery_calculation = view.findViewById(R.id.layout_battery_calculation);

        Snackbar snackbar = Snackbar.make(layout_battery_calculation, text, Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(getResources().getColor(R.color.cardBackgroundColor));
        TextView textView = snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(getResources().getColor(R.color.colorPrimary));
        snackbar.show();
    }
}