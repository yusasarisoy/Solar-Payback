package myusarisoy.solarhomesystem;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;

import static android.content.Context.LOCATION_SERVICE;

public class FragmentBill extends Fragment {
    @BindView(R.id.layout_bill)
    LinearLayout layout_bill;

    @BindView(R.id.image_icon)
    ImageView icon;

    @BindView(R.id.app_name)
    TextView app_name;

    @BindView(R.id.image_language_english)
    ImageView languageEnglish;

    @BindView(R.id.image_language_turkish)
    ImageView languageTurkish;

    @BindView(R.id.bill_desc)
    TextView bill_desc;

    @BindView(R.id.bill_months)
    TextView bill_months;

    @BindView(R.id.text_payment)
    TextView text_payment;

    @BindView(R.id.bill_payment)
    EditText bill_payment;

    @BindView(R.id.text_power_consumption)
    TextView text_power_consumption;

    @BindView(R.id.bill_power_consumption)
    EditText bill_power_consumption;

    @BindView(R.id.button_sign_out)
    Button button_sign_out;

    @BindView(R.id.button_location)
    Button button_location;

    @BindView(R.id.layout_detect)
    LinearLayout layout_detect;

    @BindView(R.id.layout_search)
    LinearLayout layout_search;

    @BindView(R.id.button_next)
    Button button_next;

    @BindView(R.id.button_continue)
    Button button_continue;

    Context context;
    private LocationManager locationManager;
    private CountDownTimer countDownTimer;
    private LocationCallback mLocationCallback;
    private Button cancel_sign_out, confirm_sign_out, cancel_location, confirm_location, exit_from_app;
    private AppCompatDialog signOutDialog, locationDialog, searchLocationDialog, exitDialog;
    private FirebaseAuth firebaseAuth;
    private LinearLayout layout_location;
    private ImageView locationPicker;
    private Spinner locationSpinner;
    public ArrayList<String> monthName = new ArrayList<>();
    public ArrayList<Integer> monthPowerConsumption = new ArrayList<>();
    public ArrayList<Integer> monthPayment = new ArrayList<>();
    private ArrayList<String> cityList = new ArrayList<>();
    public ArrayList<String> cityPostal = new ArrayList<>();
    private ArrayList<Double> solarIrradianceList = new ArrayList<>();
    int monthIncrementer = 0;
    double irradianceData, irradianceLocation;
    String consumer, cityName = "", postalCode = "", city = "", cityLocation = "";
    private RequestQueue requestQueue;
    View view;

    public static FragmentBill newInstance(Object... objects) {
        FragmentBill fragment = new FragmentBill();
        Bundle args = new Bundle();
        args.putString("consumer", (String) objects[0]);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_bill, container, false);

        context = container.getContext();

        firebaseAuth = FirebaseAuth.getInstance();

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        setDataToView(user);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    FragmentWelcome fragmentWelcome = new FragmentWelcome();
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.layout_main, fragmentWelcome, "FragmentWelcome")
                            .addToBackStack(null)
                            .commit();
                }
            }
        };

        icon = view.findViewById(R.id.image_icon);
        consumer = getArguments().getString("consumer");

        if (consumer.equals("residental"))
            icon.setImageResource(R.drawable.residental);
        else if (consumer.equals("commercial"))
            icon.setImageResource(R.drawable.commercial);

        requestQueue = Volley.newRequestQueue(getContext());

        String apiUrl = "https://private-54ade8-apiforpaybackcalculationsystem.apiary-mock.com/questions";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, apiUrl, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject cityObject = response.getJSONObject(i);
                        city = cityObject.getString("city");
                        irradianceData = cityObject.getDouble("solar_irradiance");
                        postalCode = cityObject.getString("postal_code");
                        cityList.add(city);
                        cityPostal.add(postalCode);
                        solarIrradianceList.add(irradianceData);

                        if (cityList.get(i).equals(cityName)) {
                            cityLocation = cityList.get(i);
                            irradianceLocation = solarIrradianceList.get(i);
                            showSnackbar(getResources().getString(R.string.city) + cityList.get(i) + getResources().getString(R.string.solar_irradiance) + solarIrradianceList.get(i));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, error -> {
            Log.i("VOLLEY_ERROR", "" + error);
            showSnackbar(getResources().getString(R.string.internet_connection));
            exitFromApplication();
        });
        requestQueue.add(jsonArrayRequest);

//        Change language.
        loadLocale();
        languageClick();

//        Make text white.
        makeTextWhite();

//        Make text black.
        makeTextBlack();

//        Add monthName to ArrayList.
        addMonths();

//        Check monthName.
        checkMonths();

//        Check current cityName.
        checkCurrentLocation();

//        Detect cityName with button click.
        locationClick();

//        Sign out.
        clickToSignOut();

//        Check user's current cityName.
        locationClick();

//        Continue to GridChoice.
        continueToGridChoice();

        return view;
    }

    private void languageClick() {
        languageEnglish = view.findViewById(R.id.image_language_english);
        languageTurkish = view.findViewById(R.id.image_language_turkish);

        languageEnglish.setOnClickListener(v -> {
            setLocale("en");
            getActivity().recreate();
        });

        languageTurkish.setOnClickListener(v -> {
            setLocale("tr");
            getActivity().recreate();
        });
    }

    private void setLocale(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getContext().getResources().updateConfiguration(configuration, getContext().getResources().getDisplayMetrics());

        SharedPreferences.Editor editor = getContext().getSharedPreferences("Settings", Context.MODE_PRIVATE).edit();
        editor.putString("language", language);
        editor.apply();
    }

    public void loadLocale() {
        SharedPreferences preferences = getContext().getSharedPreferences("Settings", getActivity().MODE_PRIVATE);
        String language = preferences.getString("language", "");
        setLocale(language);
    }

    private void addMonths() {
        monthName.add("January");
        monthName.add("February");
        monthName.add("March");
        monthName.add("April");
        monthName.add("May");
        monthName.add("June");
        monthName.add("July");
        monthName.add("August");
        monthName.add("September");
        monthName.add("October");
        monthName.add("November");
        monthName.add("December");
    }

    private void checkMonths() {
        bill_months = view.findViewById(R.id.bill_months);
        bill_payment = view.findViewById(R.id.bill_payment);
        bill_power_consumption = view.findViewById(R.id.bill_power_consumption);
        button_next = view.findViewById(R.id.button_next);
        button_continue = view.findViewById(R.id.button_continue);

        bill_months.setText(monthName.get(monthIncrementer));

        button_next.setOnClickListener(v -> {
            if (!bill_payment.getText().toString().isEmpty() && !bill_power_consumption.getText().toString().isEmpty()) {
                bill_months.setText(monthName.get(monthIncrementer += 1));
                monthPayment.add(Integer.parseInt(bill_payment.getText().toString()));
                monthPowerConsumption.add(Integer.parseInt(bill_power_consumption.getText().toString()));

                bill_payment.getText().clear();
                bill_power_consumption.getText().clear();
                bill_payment.requestFocus();

                if (monthName.get(monthIncrementer).equals("March")) {
                    layout_bill.setBackgroundResource(R.drawable.spring);
                    makeTextBlack();
                } else if (monthName.get(monthIncrementer).equals("June")) {
                    layout_bill.setBackgroundResource(R.drawable.summer);
                    makeTextWhite();
                } else if (monthName.get(monthIncrementer).equals("September")) {
                    layout_bill.setBackgroundResource(R.drawable.autumn);
                    makeTextWhite();
                } else if (monthName.get(monthIncrementer).equals("December")) {
                    layout_bill.setBackgroundResource(R.drawable.winter);
                    makeTextBlack();
                    button_next.setVisibility(View.GONE);
                    button_continue.setVisibility(View.VISIBLE);
                }
            } else
                showSnackbar(getResources().getString(R.string.make_sure_to_complete));
        });
    }

    private void setDataToView(FirebaseUser user) {
//        email.setText("User Email: " + user.getEmail());
    }

    FirebaseAuth.AuthStateListener authStateListener = firebaseAuth -> {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            FragmentWelcome fragmentWelcome = new FragmentWelcome();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.layout_main, fragmentWelcome, "FragmentWelcome")
                    .addToBackStack(null)
                    .commit();
        }
    };

    public void clickToSignOut() {
        button_sign_out = view.findViewById(R.id.button_sign_out);
        button_sign_out.setOnClickListener(v -> {
            android.support.v7.app.AlertDialog.Builder reservationBuilder = new android.support.v7.app.AlertDialog.Builder(getContext());
            reservationBuilder.setView(R.layout.dialog_sign_out);
            signOutDialog = reservationBuilder.create();
            WindowManager.LayoutParams params = signOutDialog.getWindow().getAttributes();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels;
            int height = displayMetrics.heightPixels;
            params.width = (int) (width * 0.8);
            params.height = (int) (height * 0.8);
            signOutDialog.getWindow().setAttributes(params);
            signOutDialog.show();

            cancel_sign_out = signOutDialog.findViewById(R.id.cancel_sign_out);
            confirm_sign_out = signOutDialog.findViewById(R.id.confirm_sign_out);

            cancel_sign_out.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signOutDialog.dismiss();
                }
            });

            confirm_sign_out.setOnClickListener(v1 -> {
                signOutDialog.dismiss();

                final ProgressDialog progressDialog = ProgressDialog.show(getContext(), "Logout", "Please wait...", true, true);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);

                // Display the progress during 1 second.
                countDownTimer = new CountDownTimer(1000, 500) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }

                    @Override
                    public void onFinish() {
                        countDownTimer.cancel();
                        progressDialog.dismiss();
                        signOut();
                    }
                }.start();
            });
        });
    }

    public void signOut() {
        firebaseAuth.signOut();

        FirebaseAuth.AuthStateListener authStateListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user == null) {
                FragmentWelcome fragmentWelcome = new FragmentWelcome();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.layout_main, fragmentWelcome, "FragmentWelcome")
                        .addToBackStack(null)
                        .commit();
            }
        };
    }

    private void continueToGridChoice() {
        bill_payment = view.findViewById(R.id.bill_payment);
        bill_power_consumption = view.findViewById(R.id.bill_power_consumption);
        button_continue = view.findViewById(R.id.button_continue);

        button_continue.setOnClickListener(v -> {
            if (cityLocation.isEmpty())
                showSnackbar(getResources().getString(R.string.no_location));
            else if (bill_payment.getText().toString().isEmpty() && bill_power_consumption.getText().toString().isEmpty())
                showSnackbar(getResources().getString(R.string.make_sure_to_complete));
            else {
                monthPayment.add(Integer.parseInt(bill_payment.getText().toString()));
                monthPowerConsumption.add(Integer.parseInt(bill_power_consumption.getText().toString()));
                Log.i("TOTAL POWER CONSUMPTION", monthPowerConsumption + " kWh");

                FragmentGridChoice fragmentGridChoice = new FragmentGridChoice();
                Bundle bundle = new Bundle();
                bundle.putString("choice", "bill");
                bundle.putStringArrayList("MonthName", monthName);
                bundle.putIntegerArrayList("MonthPayment", monthPayment);
                bundle.putIntegerArrayList("MonthPowerConsumption", monthPowerConsumption);
                bundle.putString("City", cityLocation);
                bundle.putDouble("CityIrradiance", irradianceLocation);
                fragmentGridChoice.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.layout_main, fragmentGridChoice, "FragmentGridChoice")
                        .commit();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    public void checkCurrentLocation() {
        locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location mCurrentLocation = locationResult.getLastLocation();
                LatLng myCoordinates = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                final String postal = getCityName(myCoordinates);

                requestQueue = Volley.newRequestQueue(getContext());

                String apiUrl = "https://private-54ade8-apiforpaybackcalculationsystem.apiary-mock.com/questions";

                JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, apiUrl, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject cityObject = response.getJSONObject(i);
                                city = cityObject.getString("city");
                                irradianceData = cityObject.getDouble("solar_irradiance");
                                postalCode = cityObject.getString("postal_code");
                                cityList.add(city);
                                cityPostal.add(postalCode);
                                solarIrradianceList.add(irradianceData);

                                if (cityPostal.get(i).equals(postal)) {
                                    cityLocation = cityList.get(i);
                                    irradianceLocation = solarIrradianceList.get(i);
                                    showSnackbar(getResources().getString(R.string.city) + cityList.get(i) + getResources().getString(R.string.solar_irradiance) + solarIrradianceList.get(i));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, error -> {
                    Log.i("VOLLEY_ERROR", "" + error);
                    showSnackbar(getResources().getString(R.string.internet_connection));
                    exitFromApplication();
                });
                requestQueue.add(jsonArrayRequest);
            }
        };
    }

    public void locationClick() {
        button_location = view.findViewById(R.id.button_location);
        button_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v7.app.AlertDialog.Builder reservationBuilder = new android.support.v7.app.AlertDialog.Builder(getContext());
                reservationBuilder.setView(R.layout.dialog_location);
                locationDialog = reservationBuilder.create();
                final WindowManager.LayoutParams params = locationDialog.getWindow().getAttributes();
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int width = displayMetrics.widthPixels;
                int height = displayMetrics.heightPixels;
                params.width = (int) (width * 0.8);
                params.height = (int) (height * 0.8);
                locationDialog.getWindow().setAttributes(params);
                locationDialog.show();

                layout_detect = locationDialog.findViewById(R.id.layout_detect);
                layout_search = locationDialog.findViewById(R.id.layout_search);

                layout_detect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        locationDialog.dismiss();

                        if (Build.VERSION.SDK_INT >= 23) {
                            if (getContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                Log.d("LOCATION", "Not granted");
                                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            } else
                                requestLocation();
                        } else
                            requestLocation();
                    }
                });

                layout_search.setOnClickListener(v1 -> {
                    locationDialog.dismiss();

                    android.support.v7.app.AlertDialog.Builder reservationBuilder1 = new android.support.v7.app.AlertDialog.Builder(getContext());
                    reservationBuilder1.setView(R.layout.pop_up_location);
                    searchLocationDialog = reservationBuilder1.create();
                    final WindowManager.LayoutParams params1 = searchLocationDialog.getWindow().getAttributes();
                    DisplayMetrics displayMetrics1 = new DisplayMetrics();
                    getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics1);
                    int width1 = displayMetrics1.widthPixels;
                    int height1 = displayMetrics1.heightPixels;
                    params1.width = (int) (width1 * 0.8);
                    params1.height = (int) (height1 * 0.8);
                    searchLocationDialog.getWindow().setAttributes(params1);
                    searchLocationDialog.show();

                    layout_location = searchLocationDialog.findViewById(R.id.layout_location);
                    locationSpinner = searchLocationDialog.findViewById(R.id.locationSpinner);
                    locationPicker = searchLocationDialog.findViewById(R.id.locationPicker);
                    cancel_location = searchLocationDialog.findViewById(R.id.cancel_location);
                    confirm_location = searchLocationDialog.findViewById(R.id.confirm_location);

                    locationPicker.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v1) {
                            locationSpinner.performClick();
                        }
                    });

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        locationSpinner.setPopupBackgroundResource(R.color.core_white);
                    }
                    ArrayAdapter<String> locationSpinnerAdapter = new ArrayAdapter<>(context, R.layout.spinner_item, cityList);
                    locationSpinnerAdapter.setDropDownViewResource(R.layout.spinner_item);
                    locationSpinner.setAdapter(locationSpinnerAdapter);

                    locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            cityName = parent.getItemAtPosition(position).toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                    cancel_location.setOnClickListener(v11 -> searchLocationDialog.dismiss());

                    confirm_location.setOnClickListener(v112 -> {
                        searchLocationDialog.dismiss();

                        countDownTimer = new CountDownTimer(500, 250) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                            }

                            @Override
                            public void onFinish() {
                                countDownTimer.cancel();

                                requestQueue = Volley.newRequestQueue(getContext());

                                String apiUrl = "https://private-54ade8-apiforpaybackcalculationsystem.apiary-mock.com/questions";

                                JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, apiUrl, null, new Response.Listener<JSONArray>() {
                                    @Override
                                    public void onResponse(JSONArray response) {
                                        try {
                                            for (int i = 0; i < response.length(); i++) {
                                                JSONObject cityObject = response.getJSONObject(i);
                                                city = cityObject.getString("city");
                                                irradianceData = cityObject.getDouble("solar_irradiance");
                                                postalCode = cityObject.getString("postal_code");
                                                cityList.add(city);
                                                cityPostal.add(postalCode);
                                                solarIrradianceList.add(irradianceData);

                                                if (cityList.get(i).equals(cityName)) {
                                                    cityLocation = cityList.get(i);
                                                    irradianceLocation = solarIrradianceList.get(i);
                                                    showSnackbar(getResources().getString(R.string.city) + cityList.get(i) + getResources().getString(R.string.solar_irradiance) + solarIrradianceList.get(i));
                                                }
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, error -> {
                                    Log.i("VOLLEY_ERROR", "" + error);
                                    showSnackbar(getResources().getString(R.string.internet_connection));
                                    exitFromApplication();
                                });
                                requestQueue.add(jsonArrayRequest);
                            }
                        }.start();
                    });
                });
            }
        });
    }

    public void exitFromApplication() {
        android.support.v7.app.AlertDialog.Builder reservationBuilder = new android.support.v7.app.AlertDialog.Builder(getContext());
        reservationBuilder.setView(R.layout.dialog_exit);
        exitDialog = reservationBuilder.create();
        final WindowManager.LayoutParams params = exitDialog.getWindow().getAttributes();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        params.width = (int) (width * 0.8);
        params.height = (int) (height * 0.8);
        exitDialog.getWindow().setAttributes(params);
        exitDialog.setCancelable(false);
        exitDialog.show();

        exit_from_app = exitDialog.findViewById(R.id.exit_from_app);
        exit_from_app.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
    }

    private String getCityName(LatLng myCoordinates) {
        String myCity = "";
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(myCoordinates.latitude, myCoordinates.longitude, 1);
            String address = addresses.get(0).getAddressLine(0);
            myCity = addresses.get(0).getPostalCode().substring(0, 2);
            Log.i("mylog", "Complete Address: " + addresses.toString());
            Log.i("mylog", "Address: " + address);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return myCity;
    }

    private void requestLocation() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_MEDIUM);
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
        String provider = locationManager.getBestProvider(criteria, true);
        @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(provider);
        Log.d("mylog", "In Requesting Location");
        if (location != null && (System.currentTimeMillis() - location.getTime()) <= 1000 * 2) {
            LatLng myCoordinates = new LatLng(location.getLatitude(), location.getLongitude());
            final String postal = getCityName(myCoordinates);

            requestQueue = Volley.newRequestQueue(getContext());

            String apiUrl = "https://private-54ade8-apiforpaybackcalculationsystem.apiary-mock.com/questions";

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, apiUrl, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject cityObject = response.getJSONObject(i);
                            city = cityObject.getString("city");
                            irradianceData = cityObject.getDouble("solar_irradiance");
                            postalCode = cityObject.getString("postal_code");
                            cityList.add(city);
                            cityPostal.add(postalCode);
                            solarIrradianceList.add(irradianceData);

                            if (cityPostal.get(i).equals(postal)) {
                                cityLocation = cityList.get(i);
                                irradianceLocation = solarIrradianceList.get(i);
                                showSnackbar(getResources().getString(R.string.city) + cityList.get(i) + getResources().getString(R.string.solar_irradiance) + solarIrradianceList.get(i));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, error -> {
                Log.i("VOLLEY_ERROR", "" + error);
                showSnackbar(getResources().getString(R.string.internet_connection));
                exitFromApplication();
            });
            requestQueue.add(jsonArrayRequest);
        } else {
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setNumUpdates(1);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            showSnackbar(getResources().getString(R.string.trying_to_location));
            FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mFusedLocationClient.requestLocationUpdates(locationRequest,
                    mLocationCallback, Looper.myLooper());
        }
    }

    private void makeTextWhite() {
        layout_bill = view.findViewById(R.id.layout_bill);
        app_name = view.findViewById(R.id.app_name);
        bill_desc = view.findViewById(R.id.bill_desc);
        bill_months = view.findViewById(R.id.bill_months);
        text_payment = view.findViewById(R.id.text_payment);
        text_power_consumption = view.findViewById(R.id.text_power_consumption);

        app_name.setTextColor(getResources().getColor(R.color.core_white));
        bill_desc.setTextColor(getResources().getColor(R.color.core_white));
        bill_months.setTextColor(getResources().getColor(R.color.core_white));
        text_payment.setTextColor(getResources().getColor(R.color.core_white));
        text_power_consumption.setTextColor(getResources().getColor(R.color.core_white));
    }

    private void makeTextBlack() {
        layout_bill = view.findViewById(R.id.layout_bill);
        app_name = view.findViewById(R.id.app_name);
        bill_desc = view.findViewById(R.id.bill_desc);
        bill_months = view.findViewById(R.id.bill_months);
        text_payment = view.findViewById(R.id.text_payment);
        text_power_consumption = view.findViewById(R.id.text_power_consumption);

        app_name.setTextColor(getResources().getColor(R.color.core_black));
        bill_desc.setTextColor(getResources().getColor(R.color.core_black));
        bill_months.setTextColor(getResources().getColor(R.color.core_black));
        text_payment.setTextColor(getResources().getColor(R.color.core_black));
        text_power_consumption.setTextColor(getResources().getColor(R.color.core_black));
    }

    public void showSnackbar(String text) {
        layout_bill = view.findViewById(R.id.layout_bill);

        Snackbar snackbar = Snackbar.make(layout_bill, text, Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(getResources().getColor(R.color.dark_slate_gray));
        TextView textView = snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(getResources().getColor(R.color.colorPrimary));
        snackbar.show();
    }
}