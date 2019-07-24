package myusarisoy.solarhomesystem.Fragment;

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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import myusarisoy.solarhomesystem.R;
import myusarisoy.solarhomesystem.ThemeSelector.SharedPreferencesTheme;

import static android.content.Context.LOCATION_SERVICE;

public class FragmentBill extends Fragment {
    @BindView(R.id.layoutBill)
    LinearLayout layoutBill;

    @BindView(R.id.imageIcon)
    ImageView icon;

    @BindView(R.id.appName)
    TextView appName;

    @BindView(R.id.imageLanguage)
    ImageView language;

    @BindView(R.id.imageTheme)
    ImageView imageTheme;

    @BindView(R.id.billDesc)
    TextView billDesc;

    @BindView(R.id.billMonths)
    TextView billMonths;

    @BindView(R.id.textPayment)
    TextView textPayment;

    @BindView(R.id.billPayment)
    EditText billPayment;

    @BindView(R.id.textPowerConsumption)
    TextView textPowerConsumption;

    @BindView(R.id.billPowerConsumption)
    EditText billPowerConsumption;

    @BindView(R.id.buttonSignOut)
    Button buttonSignOut;

    @BindView(R.id.buttonLocation)
    Button buttonLocation;

    @BindView(R.id.layoutDetect)
    LinearLayout layoutDetect;

    @BindView(R.id.layoutSearch)
    LinearLayout layoutSearch;

    @BindView(R.id.buttonNext)
    Button buttonNext;

    @BindView(R.id.buttonContinue)
    Button buttonContinue;

    Context context;
    private LocationManager locationManager;
    private CountDownTimer countDownTimer;
    private LocationCallback mLocationCallback;
    private Button cancelSignOut, confirmSignOut, cancelLocation, confirmLocation, exitFromApp;
    private AppCompatDialog signOutDialog, locationDialog, searchLocationDialog, exitDialog, languageDialog, themeDialog;
    private FirebaseAuth firebaseAuth;
    private LinearLayout layoutLocation;
    private ImageView locationPicker, imgEnglish, imgGerman, imgTurkish, imgLight, imgDark;
    private Spinner locationSpinner;
    public ArrayList<String> monthName = new ArrayList<>();
    public ArrayList<Integer> monthPowerConsumption = new ArrayList<>();
    public ArrayList<Integer> monthPayment = new ArrayList<>();
    private ArrayList<String> cityList = new ArrayList<>();
    private ArrayList<Double> solarIrradianceList = new ArrayList<>();
    int monthIncrementer = 0;
    double irradianceData, irradianceLocation;
    String consumer, cityName = "", city = "", cityLocation = "", finalApiUrl = "";
    SharedPreferencesTheme sharedPreferencesTheme;
    private RequestQueue requestQueue;
    View view;

    public static FragmentBill newInstance(Object... objects) {
        FragmentBill fragment = new FragmentBill();
        Bundle args = new Bundle();
        args.putString("consumer", (String) objects[0]);
        args.putString("api", (String) objects[1]);
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

        sharedPreferencesTheme = new SharedPreferencesTheme(getContext());

        if (sharedPreferencesTheme.loadNightModeState())
            getActivity().setTheme(R.style.DarkTheme);
        else if(sharedPreferencesTheme.loadLightModeState())
            getActivity().setTheme(R.style.AppTheme);

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
                            .setCustomAnimations(R.anim.enter_left_to_right, R.anim.exit_left_to_right, R.anim.enter_right_to_left, R.anim.exit_left_to_right)
                            .replace(R.id.layoutMain, fragmentWelcome, "FragmentWelcome")
                            .addToBackStack(null)
                            .commit();
                }
            }
        };

        icon = view.findViewById(R.id.imageIcon);
        consumer = getArguments().getString("consumer");

        if (consumer.equals("residental"))
            icon.setImageResource(R.drawable.residental);
        else if (consumer.equals("commercial"))
            icon.setImageResource(R.drawable.commercial);

        finalApiUrl = getArguments().getString("api");

        String apiUrl = finalApiUrl;

        requestQueue = Volley.newRequestQueue(getContext());

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, apiUrl, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject cityObject = response.getJSONObject(i);
                        city = cityObject.getString("city");
                        irradianceData = cityObject.getDouble("solar_irradiance");
                        cityList.add(city);
                        solarIrradianceList.add(irradianceData);

                        if (cityList.get(i).equals(cityName)) {
                            cityLocation = cityList.get(i);
                            irradianceLocation = solarIrradianceList.get(i);
                            showSnackbar(getResources().getString(R.string.city) + cityList.get(i) + getResources().getString(R.string.solarIrradiance) + solarIrradianceList.get(i));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, error -> {
            Log.i("VOLLEY_ERROR", "" + error);
            showSnackbar(getResources().getString(R.string.internetConnection));
            exitFromApplication();
        });
        requestQueue.add(jsonArrayRequest);

//        Change theme.
        changeTheme();

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

    private void changeTheme() {
        imageTheme = view.findViewById(R.id.imageTheme);
        imageTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v7.app.AlertDialog.Builder reservationBuilder = new android.support.v7.app.AlertDialog.Builder(getContext());
                reservationBuilder.setView(R.layout.dialog_theme);
                themeDialog = reservationBuilder.create();
                WindowManager.LayoutParams params = themeDialog.getWindow().getAttributes();
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int width = displayMetrics.widthPixels;
                int height = displayMetrics.heightPixels;
                params.width = (int) (width * 0.8);
                params.height = (int) (height * 0.8);
                themeDialog.getWindow().setAttributes(params);
                themeDialog.show();

                imgLight = themeDialog.findViewById(R.id.imgLight);
                imgDark = themeDialog.findViewById(R.id.imgDark);

                imgLight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        themeDialog.dismiss();
                        sharedPreferencesTheme.setLightModeState(true);
                        sharedPreferencesTheme.setNightModeState(false);
                        getActivity().recreate();
                    }
                });

                imgDark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        themeDialog.dismiss();
                        sharedPreferencesTheme.setLightModeState(false);
                        sharedPreferencesTheme.setNightModeState(true);
                        getActivity().recreate();
                    }
                });
            }
        });
    }

    private void languageClick() {
        language = view.findViewById(R.id.imageLanguage);

        language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v7.app.AlertDialog.Builder reservationBuilder = new android.support.v7.app.AlertDialog.Builder(getContext());
                reservationBuilder.setView(R.layout.pop_up_language);
                languageDialog = reservationBuilder.create();
                WindowManager.LayoutParams params = languageDialog.getWindow().getAttributes();
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int width = displayMetrics.widthPixels;
                int height = displayMetrics.heightPixels;
                params.width = (int) (width * 0.8);
                params.height = (int) (height * 0.8);
                languageDialog.getWindow().setAttributes(params);
                languageDialog.show();

                imgEnglish = languageDialog.findViewById(R.id.imgEnglish);
//                img_german = languageDialog.findViewById(R.id.img_german);
                imgTurkish = languageDialog.findViewById(R.id.imgTurkish);

                imgEnglish.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        languageDialog.dismiss();
                        setLocale("en");
                        getActivity().recreate();
                    }
                });

//                imgGerman.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        languageDialog.dismiss();
//                        setLocale("de");
//                        getActivity().recreate();
//                    }
//                });

                imgTurkish.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        languageDialog.dismiss();
                        setLocale("tr");
                        getActivity().recreate();
                    }
                });
            }
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
        monthName.add(getResources().getString(R.string.january));
        monthName.add(getResources().getString(R.string.february));
        monthName.add(getResources().getString(R.string.march));
        monthName.add(getResources().getString(R.string.april));
        monthName.add(getResources().getString(R.string.may));
        monthName.add(getResources().getString(R.string.june));
        monthName.add(getResources().getString(R.string.july));
        monthName.add(getResources().getString(R.string.august));
        monthName.add(getResources().getString(R.string.september));
        monthName.add(getResources().getString(R.string.october));
        monthName.add(getResources().getString(R.string.november));
        monthName.add(getResources().getString(R.string.december));
    }

    private void checkMonths() {
        billMonths = view.findViewById(R.id.billMonths);
        billPayment = view.findViewById(R.id.billPayment);
        billPowerConsumption = view.findViewById(R.id.billPowerConsumption);
        buttonNext = view.findViewById(R.id.buttonNext);
        buttonContinue = view.findViewById(R.id.buttonContinue);

        billMonths.setText(monthName.get(monthIncrementer));

        buttonNext.setOnClickListener(v -> {
            if (!billPayment.getText().toString().isEmpty() && !billPowerConsumption.getText().toString().isEmpty()) {
                billMonths.setText(monthName.get(monthIncrementer += 1));
                monthPayment.add(Integer.parseInt(billPayment.getText().toString()));
                monthPowerConsumption.add(Integer.parseInt(billPowerConsumption.getText().toString()));

                billPayment.getText().clear();
                billPowerConsumption.getText().clear();
                billPayment.requestFocus();

                if (monthName.get(monthIncrementer).equals(getResources().getString(R.string.march))) {
                    layoutBill.setBackgroundResource(R.drawable.spring);
                    makeTextBlack();
                } else if (monthName.get(monthIncrementer).equals(getResources().getString(R.string.june))) {
                    layoutBill.setBackgroundResource(R.drawable.summer);
                    makeTextWhite();
                } else if (monthName.get(monthIncrementer).equals(getResources().getString(R.string.september))) {
                    layoutBill.setBackgroundResource(R.drawable.autumn);
                    makeTextWhite();
                } else if (monthName.get(monthIncrementer).equals(getResources().getString(R.string.december))) {
                    layoutBill.setBackgroundResource(R.drawable.winter);
                    makeTextBlack();
                    buttonNext.setVisibility(View.GONE);
                    buttonContinue.setVisibility(View.VISIBLE);
                }
            } else
                showSnackbar(getResources().getString(R.string.makeSureToComplete));
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
                    .setCustomAnimations(R.anim.enter_left_to_right, R.anim.exit_left_to_right, R.anim.enter_right_to_left, R.anim.exit_left_to_right)
                    .replace(R.id.layoutMain, fragmentWelcome, "FragmentWelcome")
                    .addToBackStack(null)
                    .commit();
        }
    };

    public void clickToSignOut() {
        buttonSignOut = view.findViewById(R.id.buttonSignOut);
        buttonSignOut.setOnClickListener(v -> {
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

            cancelSignOut = signOutDialog.findViewById(R.id.cancelSignOut);
            confirmSignOut = signOutDialog.findViewById(R.id.confirmSignOut);

            cancelSignOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signOutDialog.dismiss();
                }
            });

            confirmSignOut.setOnClickListener(v1 -> {
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
                        .setCustomAnimations(R.anim.enter_left_to_right, R.anim.exit_left_to_right, R.anim.enter_right_to_left, R.anim.exit_left_to_right)
                        .replace(R.id.layoutMain, fragmentWelcome, "FragmentWelcome")
                        .addToBackStack(null)
                        .commit();
            }
        };
    }

    private void continueToGridChoice() {
        billPayment = view.findViewById(R.id.billPayment);
        billPowerConsumption = view.findViewById(R.id.billPowerConsumption);
        buttonContinue = view.findViewById(R.id.buttonContinue);

        buttonContinue.setOnClickListener(v -> {
            if (cityLocation.isEmpty())
                showSnackbar(getResources().getString(R.string.noLocation));
            else if (billPayment.getText().toString().isEmpty() && billPowerConsumption.getText().toString().isEmpty())
                showSnackbar(getResources().getString(R.string.makeSureToComplete));
            else {
                monthPayment.add(Integer.parseInt(billPayment.getText().toString()));
                monthPowerConsumption.add(Integer.parseInt(billPowerConsumption.getText().toString()));
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
                        .setCustomAnimations(R.anim.enter_left_to_right, R.anim.exit_left_to_right, R.anim.enter_right_to_left, R.anim.exit_left_to_right)
                        .replace(R.id.layoutMain, fragmentGridChoice, "FragmentGridChoice")
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
                final String selectedCity = getCityName(myCoordinates);

                requestQueue = Volley.newRequestQueue(getContext());

                String apiUrl = finalApiUrl;

                JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, apiUrl, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject cityObject = response.getJSONObject(i);
                                city = cityObject.getString("city");
                                irradianceData = cityObject.getDouble("solar_irradiance");
                                cityList.add(city);
                                solarIrradianceList.add(irradianceData);

                                if (cityList.get(i).equals(selectedCity)) {
                                    cityLocation = cityList.get(i);
                                    irradianceLocation = solarIrradianceList.get(i);
                                    showSnackbar(getResources().getString(R.string.city) + cityList.get(i) + getResources().getString(R.string.solarIrradiance) + solarIrradianceList.get(i));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, error -> {
                    Log.i("VOLLEY_ERROR", "" + error);
                    showSnackbar(getResources().getString(R.string.internetConnection));
                    exitFromApplication();
                });
                requestQueue.add(jsonArrayRequest);
            }
        };
    }

    public void locationClick() {
        buttonLocation = view.findViewById(R.id.buttonLocation);
        buttonLocation.setOnClickListener(new View.OnClickListener() {
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

                layoutDetect = locationDialog.findViewById(R.id.layoutDetect);
                layoutSearch = locationDialog.findViewById(R.id.layoutSearch);

                layoutDetect.setOnClickListener(new View.OnClickListener() {
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

                layoutSearch.setOnClickListener(v1 -> {
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

                    layoutLocation = searchLocationDialog.findViewById(R.id.layoutLocation);
                    locationSpinner = searchLocationDialog.findViewById(R.id.locationSpinner);
                    locationPicker = searchLocationDialog.findViewById(R.id.locationPicker);
                    cancelLocation = searchLocationDialog.findViewById(R.id.cancelLocation);
                    confirmLocation = searchLocationDialog.findViewById(R.id.confirmLocation);

                    locationPicker.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v1) {
                            locationSpinner.performClick();
                        }
                    });

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        locationSpinner.setPopupBackgroundResource(R.color.coreWhite);
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

                    cancelLocation.setOnClickListener(v11 -> searchLocationDialog.dismiss());

                    confirmLocation.setOnClickListener(v112 -> {
                        searchLocationDialog.dismiss();

                        countDownTimer = new CountDownTimer(500, 250) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                            }

                            @Override
                            public void onFinish() {
                                countDownTimer.cancel();

                                requestQueue = Volley.newRequestQueue(getContext());

                                String apiUrl = finalApiUrl;

                                JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, apiUrl, null, new Response.Listener<JSONArray>() {
                                    @Override
                                    public void onResponse(JSONArray response) {
                                        try {
                                            for (int i = 0; i < response.length(); i++) {
                                                JSONObject cityObject = response.getJSONObject(i);
                                                city = cityObject.getString("city");
                                                irradianceData = cityObject.getDouble("solar_irradiance");
                                                cityList.add(city);
                                                solarIrradianceList.add(irradianceData);

                                                if (cityList.get(i).equals(cityName)) {
                                                    cityLocation = cityList.get(i);
                                                    irradianceLocation = solarIrradianceList.get(i);
                                                    showSnackbar(getResources().getString(R.string.city) + cityList.get(i) + getResources().getString(R.string.solarIrradiance) + solarIrradianceList.get(i));
                                                }
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, error -> {
                                    Log.i("VOLLEY_ERROR", "" + error);
                                    showSnackbar(getResources().getString(R.string.internetConnection));
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

        exitFromApp = exitDialog.findViewById(R.id.exitFromApp);
        exitFromApp.setOnClickListener(v -> {
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
            myCity = addresses.get(0).getAdminArea();
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
            final String selectedCity = getCityName(myCoordinates);

            requestQueue = Volley.newRequestQueue(getContext());

            String apiUrl = finalApiUrl;

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, apiUrl, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject cityObject = response.getJSONObject(i);
                            city = cityObject.getString("city");
                            irradianceData = cityObject.getDouble("solar_irradiance");
                            cityList.add(city);
                            solarIrradianceList.add(irradianceData);

                            if (cityList.get(i).equals(selectedCity)) {
                                cityLocation = cityList.get(i);
                                irradianceLocation = solarIrradianceList.get(i);
                                showSnackbar(getResources().getString(R.string.city) + cityList.get(i) + getResources().getString(R.string.solarIrradiance) + solarIrradianceList.get(i));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, error -> {
                Log.i("VOLLEY_ERROR", "" + error);
                showSnackbar(getResources().getString(R.string.internetConnection));
                exitFromApplication();
            });
            requestQueue.add(jsonArrayRequest);
        } else {
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setNumUpdates(1);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            showSnackbar(getResources().getString(R.string.tryingToLocation));
            FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mFusedLocationClient.requestLocationUpdates(locationRequest,
                    mLocationCallback, Looper.myLooper());
        }
    }

    private void makeTextWhite() {
        layoutBill = view.findViewById(R.id.layoutBill);
        appName = view.findViewById(R.id.appName);
        billDesc = view.findViewById(R.id.billDesc);
        billMonths = view.findViewById(R.id.billMonths);
        textPayment = view.findViewById(R.id.textPayment);
        textPowerConsumption = view.findViewById(R.id.textPowerConsumption);

        appName.setTextColor(getResources().getColor(R.color.coreWhite));
        billDesc.setTextColor(getResources().getColor(R.color.coreWhite));
        billMonths.setTextColor(getResources().getColor(R.color.coreWhite));
        textPayment.setTextColor(getResources().getColor(R.color.coreWhite));
        textPowerConsumption.setTextColor(getResources().getColor(R.color.coreWhite));
    }

    private void makeTextBlack() {
        layoutBill = view.findViewById(R.id.layoutBill);
        appName = view.findViewById(R.id.appName);
        billDesc = view.findViewById(R.id.billDesc);
        billMonths = view.findViewById(R.id.billMonths);
        textPayment = view.findViewById(R.id.textPayment);
        textPowerConsumption = view.findViewById(R.id.textPowerConsumption);

        appName.setTextColor(getResources().getColor(R.color.coreBlack));
        billDesc.setTextColor(getResources().getColor(R.color.coreBlack));
        billMonths.setTextColor(getResources().getColor(R.color.coreBlack));
        textPayment.setTextColor(getResources().getColor(R.color.coreBlack));
        textPowerConsumption.setTextColor(getResources().getColor(R.color.coreBlack));
    }

    public void showSnackbar(String text) {
        layoutBill = view.findViewById(R.id.layoutBill);

        Snackbar snackbar = Snackbar.make(layoutBill, text, Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(getResources().getColor(R.color.backgroundColor));
        TextView textView = snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(getResources().getColor(R.color.colorPrimary));
        snackbar.show();
    }
}