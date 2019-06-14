package myusarisoy.solarhomesystem.Fragment;

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
import myusarisoy.solarhomesystem.Activity.MainActivity;
import myusarisoy.solarhomesystem.R;

public class FragmentBatteryCalculation extends Fragment {
    @BindView(R.id.layoutBatteryCalculation)
    LinearLayout layoutBatteryCalculation;

    @BindView(R.id.imgInfo)
    ImageView info;

    @BindView(R.id.layoutPrices)
    LinearLayout layoutPrices;

    @BindView(R.id.batteryLayout)
    LinearLayout batteryLayout;

    @BindView(R.id.inspectionLayout)
    LinearLayout inspectionLayout;

    @BindView(R.id.cleaningLayout)
    LinearLayout cleaningLayout;

    @BindView(R.id.panelPrice)
    TextView panelPrice;

    @BindView(R.id.heaterPrice)
    TextView heaterPrice;

    @BindView(R.id.inverterPrice)
    TextView inverterPrice;

    @BindView(R.id.batteryPrice)
    TextView batteryPrice;

    @BindView(R.id.layoutCosts)
    LinearLayout layoutCosts;

    @BindView(R.id.inspectionCost)
    TextView inspectionCost;

    @BindView(R.id.cleaningCost)
    TextView cleaningCost;

    @BindView(R.id.electricityCost)
    TextView electricityCost;

    @BindView(R.id.totalPrice)
    TextView totalPrice;

    @BindView(R.id.paybackPeriod)
    TextView paybackPeriod;

    @BindView(R.id.layoutBack)
    LinearLayout layoutBack;

    @BindView(R.id.layoutNext)
    LinearLayout layoutNext;

    @BindView(R.id.layoutMainPage)
    LinearLayout layoutMainPage;

    @BindView(R.id.buttonBack)
    Button buttonBack;

    @BindView(R.id.buttonNext)
    Button buttonNext;

    @BindView(R.id.imageMainPage)
    ImageView imageMainPage;

    AppCompatDialog dialog_main_page, infoDialog, masterBatteryDialog, batteryOptionsDialog;
    Button noMainPage, yesMainPage, confirmBack, closeInfo;
    EditText heater, inverter, battery, inspection, cleaning;
    LinearLayout master_battery_layout, master_inspection_layout, master_cleaning_layout, battery1, battery2;
    TextView batteryPrice1, batteryPrice2;
    private RequestQueue requestQueueUSD;
    private double liraPerDollar, paybackYear;
    private int price, lowerProduction, panels, finalBattery;
    private String baseUSD, experience, grid;
    private int priceOfPanel, totalPayment, priceOfHeater = 1800, priceOfInverter = 150, batteryOption1 = 1000, batteryOption2 = 200, costOfInspection = 2, costOfCleaning = 10;
    View view;

    public static FragmentBatteryCalculation newInstance(Object... objects) {
        FragmentBatteryCalculation fragment = new FragmentBatteryCalculation();
        Bundle args = new Bundle();
        args.putInt("priceOfPanel", (Integer) objects[0]);
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
        info = view.findViewById(R.id.imgInfo);

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

            closeInfo = infoDialog.findViewById(R.id.closeInfo);

            closeInfo.setOnClickListener(v1 -> infoDialog.dismiss());
        });
    }

    private void getCurrency() {
//        Get user's experience status about the solar energy.
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Experience", 0);
        experience = sharedPreferences.getString("Experience", "");

        if (grid.equals("On-Grid")) {
            batteryLayout = view.findViewById(R.id.batteryLayout);
            inspectionLayout = view.findViewById(R.id.inspectionLayout);
            cleaningLayout = view.findViewById(R.id.cleaningLayout);

            batteryLayout.setVisibility(View.GONE);
            inspectionLayout.setVisibility(View.GONE);
            cleaningLayout.setVisibility(View.GONE);

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

                heater = masterBatteryDialog.findViewById(R.id.heaterPrice);
                inverter = masterBatteryDialog.findViewById(R.id.inverterPrice);
                battery = masterBatteryDialog.findViewById(R.id.batteryPrice);
                inspection = masterBatteryDialog.findViewById(R.id.inspectionCost);
                cleaning = masterBatteryDialog.findViewById(R.id.cleaningCost);
                confirmBack = masterBatteryDialog.findViewById(R.id.confirm_back);
                master_battery_layout = masterBatteryDialog.findViewById(R.id.master_battery_layout);
                master_inspection_layout = masterBatteryDialog.findViewById(R.id.master_inspection_layout);
                master_cleaning_layout = masterBatteryDialog.findViewById(R.id.master_cleaning_layout);

                master_battery_layout.setVisibility(View.GONE);
                master_inspection_layout.setVisibility(View.GONE);
                master_cleaning_layout.setVisibility(View.GONE);

                confirmBack.setOnClickListener(v -> {
                    masterBatteryDialog.dismiss();

                    batteryLayout = view.findViewById(R.id.batteryLayout);
                    inspectionLayout = view.findViewById(R.id.inspectionLayout);
                    cleaningLayout = view.findViewById(R.id.cleaningLayout);
                    panelPrice = view.findViewById(R.id.panelPrice);
                    heaterPrice = view.findViewById(R.id.heaterPrice);
                    inverterPrice = view.findViewById(R.id.inverterPrice);
                    electricityCost = view.findViewById(R.id.electricityCost);
                    totalPrice = view.findViewById(R.id.totalPrice);
                    paybackPeriod = view.findViewById(R.id.paybackPeriod);

                    priceOfPanel = getArguments().getInt("priceOfPanel");
                    totalPayment = getArguments().getInt("TotalPayment");

                    int finalHeater = Integer.parseInt(heater.getText().toString());
                    int finalInverter = Integer.parseInt(inverter.getText().toString());

                    price = priceOfPanel + finalHeater + finalInverter;
                    paybackYear = (double) price / totalPayment;

                    panelPrice.setText(getResources().getString(R.string.panelPrice) + ((priceOfPanel)) + " ₺");
                    heaterPrice.setText(getResources().getString(R.string.heaterPrice) + finalHeater + " ₺");
                    inverterPrice.setText(getResources().getString(R.string.inverterPrice) + finalInverter + " ₺");
                    electricityCost.setText(getResources().getString(R.string.electricityCostAndIncome) + (totalPayment) + " ₺");
                    totalPrice.setText(getResources().getString(R.string.totalPrice) + (price + lowerProduction) + " ₺");
                    paybackPeriod.setText(getResources().getString(R.string.paybackPeriod) + String.format("%.1f", paybackYear) + getResources().getString(R.string.years));
                });
            } else if (experience.equals("Beginner")) {
                final ProgressDialog progressDialog = ProgressDialog.show(getContext(), getResources().getString(R.string.exchangeRate), getResources().getString(R.string.pleaseWait), true, true);
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

                        panelPrice = view.findViewById(R.id.panelPrice);
                        heaterPrice = view.findViewById(R.id.heaterPrice);
                        inverterPrice = view.findViewById(R.id.inverterPrice);
                        electricityCost = view.findViewById(R.id.electricityCost);
                        totalPrice = view.findViewById(R.id.totalPrice);
                        paybackPeriod = view.findViewById(R.id.paybackPeriod);

                        priceOfPanel = getArguments().getInt("priceOfPanel");
                        totalPayment = getArguments().getInt("TotalPayment");

                        int finalHeater = (int) (priceOfHeater * liraPerDollar);
                        int finalInverter = (int) (priceOfInverter * liraPerDollar);

                        price = priceOfPanel + finalHeater + finalInverter;
                        paybackYear = (double) price / totalPayment;

                        panelPrice.setText(getResources().getString(R.string.panelPrice) + ((priceOfPanel)) + " ₺");
                        heaterPrice.setText(getResources().getString(R.string.heaterPrice) + finalHeater + " ₺");
                        inverterPrice.setText(getResources().getString(R.string.inverterPrice) + finalInverter + " ₺");
                        electricityCost.setText(getResources().getString(R.string.electricityCostAndIncome) + (totalPayment) + " ₺");
                        totalPrice.setText(getResources().getString(R.string.totalPrice) + (price + lowerProduction) + " ₺");
                        paybackPeriod.setText(getResources().getString(R.string.paybackPeriod) + String.format("%.1f", paybackYear) + getResources().getString(R.string.years));

                        if (!heaterPrice.getText().toString().isEmpty())
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

                heater = masterBatteryDialog.findViewById(R.id.heaterPrice);
                inverter = masterBatteryDialog.findViewById(R.id.inverterPrice);
                battery = masterBatteryDialog.findViewById(R.id.batteryPrice);
                inspection = masterBatteryDialog.findViewById(R.id.inspectionCost);
                cleaning = masterBatteryDialog.findViewById(R.id.cleaningCost);
                confirmBack = masterBatteryDialog.findViewById(R.id.confirm_back);

                confirmBack.setOnClickListener(v -> {
                    masterBatteryDialog.dismiss();

                    batteryLayout = view.findViewById(R.id.batteryLayout);
                    inspectionLayout = view.findViewById(R.id.inspectionLayout);
                    cleaningLayout = view.findViewById(R.id.cleaningLayout);
                    panelPrice = view.findViewById(R.id.panelPrice);
                    heaterPrice = view.findViewById(R.id.heaterPrice);
                    inverterPrice = view.findViewById(R.id.inverterPrice);
                    batteryPrice = view.findViewById(R.id.batteryPrice);
                    inspectionCost = view.findViewById(R.id.inspectionCost);
                    cleaningCost = view.findViewById(R.id.cleaningCost);
                    electricityCost = view.findViewById(R.id.electricityCost);
                    totalPrice = view.findViewById(R.id.totalPrice);
                    paybackPeriod = view.findViewById(R.id.paybackPeriod);

                    priceOfPanel = getArguments().getInt("priceOfPanel");
                    totalPayment = getArguments().getInt("TotalPayment");

                    int finalHeater = Integer.parseInt(heater.getText().toString());
                    int finalInverter = Integer.parseInt(inverter.getText().toString());
                    int finalBattery = Integer.parseInt(battery.getText().toString());
                    int finalInspection = Integer.parseInt(inspection.getText().toString());
                    int finalCleaning = Integer.parseInt(cleaning.getText().toString());

                    price = priceOfPanel + finalHeater + finalInverter + finalBattery + finalInspection + finalCleaning;
                    paybackYear = (double) price / totalPayment;

                    panelPrice.setText(getResources().getString(R.string.panelPrice) + ((priceOfPanel)) + " ₺");
                    heaterPrice.setText(getResources().getString(R.string.heaterPrice) + finalHeater + " ₺");
                    inverterPrice.setText(getResources().getString(R.string.inverterPrice) + finalInverter + " ₺");
                    batteryPrice.setText(getResources().getString(R.string.batteryPrice) + finalBattery + " ₺");
                    inspectionCost.setText(getResources().getString(R.string.inspectionCost) + finalInspection + " ₺");
                    cleaningCost.setText(getResources().getString(R.string.cleaningCost) + finalCleaning + " ₺");
                    electricityCost.setText(getResources().getString(R.string.electricityCost) + (totalPayment) + " ₺");
                    totalPrice.setText(getResources().getString(R.string.totalPrice) + (price + lowerProduction) + " ₺");
                    paybackPeriod.setText(getResources().getString(R.string.paybackPeriod) + String.format("%.1f", paybackYear) + getResources().getString(R.string.years));
                });
            } else if (experience.equals("Beginner")) {
                final ProgressDialog progressDialog = ProgressDialog.show(getContext(), getResources().getString(R.string.exchangeRate), getResources().getString(R.string.pleaseWait), true, true);
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

                        panelPrice = view.findViewById(R.id.panelPrice);
                        heaterPrice = view.findViewById(R.id.heaterPrice);
                        inverterPrice = view.findViewById(R.id.inverterPrice);
                        batteryPrice = view.findViewById(R.id.batteryPrice);
                        inspectionCost = view.findViewById(R.id.inspectionCost);
                        cleaningCost = view.findViewById(R.id.cleaningCost);
                        electricityCost = view.findViewById(R.id.electricityCost);
                        totalPrice = view.findViewById(R.id.totalPrice);
                        paybackPeriod = view.findViewById(R.id.paybackPeriod);

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

                        battery1 = batteryOptionsDialog.findViewById(R.id.layoutBattery1);
                        battery2 = batteryOptionsDialog.findViewById(R.id.layoutBattery2);
                        batteryPrice1 = batteryOptionsDialog.findViewById(R.id.pricingBattery1);
                        batteryPrice2 = batteryOptionsDialog.findViewById(R.id.pricingBattery2);

                        batteryPrice1.setText((int) (batteryOption1 * liraPerDollar) + " ₺");
                        batteryPrice2.setText((int) (batteryOption2 * liraPerDollar) + " ₺");

                        battery1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finalBattery = (int) (batteryOption1 * liraPerDollar);
                                batteryPrice.setText(getResources().getString(R.string.batteryPrice) + finalBattery + " ₺");
                                batteryOptionsDialog.dismiss();
                            }
                        });

                        battery2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finalBattery = (int) (batteryOption2 * liraPerDollar);
                                batteryPrice.setText(getResources().getString(R.string.batteryPrice) + finalBattery + " ₺");
                                batteryOptionsDialog.dismiss();
                            }
                        });

                        priceOfPanel = getArguments().getInt("priceOfPanel");
                        totalPayment = getArguments().getInt("TotalPayment");

                        int finalHeater = (int) (priceOfHeater * liraPerDollar);
                        int finalInverter = (int) (priceOfInverter * liraPerDollar);
                        int finalInspection = (int) (panels * costOfInspection * liraPerDollar * 25);
                        int finalCleaning = (int) (costOfCleaning * panels * liraPerDollar * 5);

                        price = priceOfPanel + finalHeater + finalInverter + finalBattery + finalInspection + finalCleaning;
                        paybackYear = (double) price / totalPayment;

                        panelPrice.setText(getResources().getString(R.string.panelPrice) + ((priceOfPanel)) + " ₺");
                        heaterPrice.setText(getResources().getString(R.string.heaterPrice) + finalHeater + " ₺");
                        inverterPrice.setText(getResources().getString(R.string.inverterPrice) + finalInverter + " ₺");
                        inspectionCost.setText(getResources().getString(R.string.inspectionCost) + finalInspection + " ₺");
                        cleaningCost.setText(getResources().getString(R.string.cleaningCost) + finalCleaning + " ₺");
                        electricityCost.setText(getResources().getString(R.string.electricityCost) + (totalPayment) + " ₺");
                        totalPrice.setText(getResources().getString(R.string.totalPrice) + (price + lowerProduction) + " ₺");
                        paybackPeriod.setText(getResources().getString(R.string.paybackPeriod) + String.format("%.1f", paybackYear) + getResources().getString(R.string.years));

                        if (!heaterPrice.getText().toString().isEmpty())
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
        layoutPrices = view.findViewById(R.id.layoutPrices);
        layoutCosts = view.findViewById(R.id.layoutCosts);
        layoutBack = view.findViewById(R.id.layoutBack);
        layoutNext = view.findViewById(R.id.layoutNext);
        layoutMainPage = view.findViewById(R.id.layoutMainPage);
        buttonBack = view.findViewById(R.id.buttonBack);
        buttonNext = view.findViewById(R.id.buttonNext);
        imageMainPage = view.findViewById(R.id.imageMainPage);

        buttonBack.setOnClickListener(v -> {
            layoutPrices.setVisibility(View.VISIBLE);
            layoutCosts.setVisibility(View.GONE);
            layoutBack.setVisibility(View.GONE);
            layoutNext.setVisibility(View.VISIBLE);
            layoutMainPage.setVisibility(View.GONE);
        });

        buttonNext.setOnClickListener(v -> {
            layoutPrices.setVisibility(View.GONE);
            layoutCosts.setVisibility(View.VISIBLE);
            layoutBack.setVisibility(View.VISIBLE);
            layoutNext.setVisibility(View.GONE);
            layoutMainPage.setVisibility(View.VISIBLE);

            if (grid.equals("Off-Grid") && paybackYear > 10.0)
                showSnackbar(getResources().getString(R.string.longPaybackPeriod));
        });

        imageMainPage.setOnClickListener(v -> {
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

            noMainPage = dialog_main_page.findViewById(R.id.noMainPage);
            yesMainPage = dialog_main_page.findViewById(R.id.yesMainPage);

            noMainPage.setOnClickListener(v1 -> dialog_main_page.dismiss());

            yesMainPage.setOnClickListener(new View.OnClickListener() {
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
        layoutBatteryCalculation = view.findViewById(R.id.layoutBatteryCalculation);

        Snackbar snackbar = Snackbar.make(layoutBatteryCalculation, text, Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(getResources().getColor(R.color.backgroundColor));
        TextView textView = snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(getResources().getColor(R.color.colorPrimary));
        snackbar.show();
    }
}