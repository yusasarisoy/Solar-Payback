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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.libraries.places.api.Places;

import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import myusarisoy.solarhomesystem.Adapter.RecyclerViewAdapter;
import myusarisoy.solarhomesystem.Model.Appliance;
import myusarisoy.solarhomesystem.R;
import myusarisoy.solarhomesystem.ThemeSelector.SharedPreferencesTheme;

import static android.content.Context.LOCATION_SERVICE;

public class FragmentMain extends Fragment {
    @BindView(R.id.layoutMain)
    LinearLayout layoutMain;

    @BindView(R.id.imageIcon)
    ImageView icon;

    @BindView(R.id.imageLanguage)
    ImageView language;

    @BindView(R.id.imageTheme)
    ImageView imageTheme;

    @BindView(R.id.recyclerViewAppliance)
    RecyclerView recyclerViewAppliance;

    @BindView(R.id.buttonAdd)
    FloatingActionButton buttonAdd;

    @BindView(R.id.buttonSignOut)
    Button buttonSignOut;

    @BindView(R.id.buttonLocation)
    Button buttonLocation;

    @BindView(R.id.layoutDetect)
    LinearLayout layoutDetect;

    @BindView(R.id.layoutSearch)
    LinearLayout layoutSearch;

    @BindView(R.id.buttonContinue)
    Button buttonContinue;

    Context context;
    private boolean turkey, usa;
    private LocationManager locationManager;
    private CountDownTimer countDownTimer;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private Button cancelBack, confirmBack, cancelSignOut, confirmSignOut, cancelLocation, confirmLocation, cancelAppliances, confirmAppliances, exitFromApp, confirmCountry;
    private AppCompatDialog signOutDialog, addApplianceDialog, locationDialog, searchLocationDialog, appliancesDialog, exitDialog, countryDialog, languageDialog, themeDialog;
    private FirebaseAuth firebaseAuth;
    private RecyclerViewAdapter adapter;
    private EditText applianceName;
    private LinearLayout layoutLocation;
    private ImageView applianceImage, locationPicker, imgEnglish, imgGerman, imgTurkish, imgLight, imgDark;
    private Spinner locationSpinner;
    private TextView sureToAddAppliance, appliancesList;
    private ArrayList<Appliance> applianceList = new ArrayList<>();
    public ArrayList<String> appliancesName = new ArrayList<>();
    public ArrayList<Integer> appliancesImage = new ArrayList<>();
    private ArrayList<String> cityList = new ArrayList<>();
    private ArrayList<Double> solarIrradianceList = new ArrayList<>();
    SharedPreferencesTheme sharedPreferencesTheme;
    double irradianceData, irradianceLocation;
    String consumer, cityName = "", city = "", cityLocation = "", finalApiUrl = "";
    private RequestQueue requestQueue;
    View view;

    public static FragmentMain newInstance(Object... objects) {
        FragmentMain fragment = new FragmentMain();
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
        view = inflater.inflate(R.layout.fragment_main, container, false);

        sharedPreferencesTheme = new SharedPreferencesTheme(getContext());

        if (sharedPreferencesTheme.loadNightModeState())
            getActivity().setTheme(R.style.DarkTheme);
        else if (sharedPreferencesTheme.loadLightModeState())
            getActivity().setTheme(R.style.AppTheme);

        context = container.getContext();

        firebaseAuth = FirebaseAuth.getInstance();

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        setDataToView(user);

        authStateListener = firebaseAuth -> {
            FirebaseUser user1 = firebaseAuth.getCurrentUser();
            if (user1 == null) {
                FragmentWelcome fragmentWelcome = new FragmentWelcome();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.layoutMain, fragmentWelcome, "FragmentWelcome")
                        .addToBackStack(null)
                        .commit();
            }
        };

        if (!Places.isInitialized())
            Places.initialize(getContext(), "AIzaSyDNymBWXFV6aueL7rJacOpwxHXvMALidJI");

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

//        Set adapter for Recycler View.
        setAdapter();

//        Check current location.
        checkCurrentLocation();

//        Detect location with button click.
        locationClick();

//        Show list of appliances.
        initAppliances();

//        Activate or deactivate the appliance.
        changeAppliances();

//        Add an appliance.
        addNewAppliance();

//        Sign out.
        clickToSignOut();

//        Check user's current location.
        locationClick();

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
//                imgGerman = languageDialog.findViewById(R.id.img_german);
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

    private void setDataToView(FirebaseUser user) {
//        email.setText("User Email: " + user.getEmail());
    }

    FirebaseAuth.AuthStateListener authStateListener = firebaseAuth -> {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            FragmentWelcome fragmentWelcome = new FragmentWelcome();
            getActivity().getSupportFragmentManager().beginTransaction()
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

            cancelSignOut.setOnClickListener(v12 -> signOutDialog.dismiss());

            confirmSignOut.setOnClickListener(v1 -> {
                signOutDialog.dismiss();

                final ProgressDialog progressDialog = ProgressDialog.show(getContext(), getResources().getString(R.string.logout), getResources().getString(R.string.pleaseWait), true, true);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setIndeterminate(true);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                signOut();

                progressDialog.dismiss();
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
                        .replace(R.id.layoutMain, fragmentWelcome, "FragmentWelcome")
                        .addToBackStack(null)
                        .commit();
            }
        };
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

    public void initAppliances() {
        recyclerViewAppliance = view.findViewById(R.id.recyclerViewAppliance);

        Appliance airConditioner = new Appliance(false, R.drawable.air_conditioner, getContext().getResources().getString(R.string.airConditioner));
        applianceList.add(airConditioner);

        Appliance bakery = new Appliance(false, R.drawable.bakery, getContext().getResources().getString(R.string.bakery));
        applianceList.add(bakery);

        Appliance coffeeMachine = new Appliance(false, R.drawable.coffee_machine, getContext().getResources().getString(R.string.coffeeMachine));
        applianceList.add(coffeeMachine);

        Appliance computer = new Appliance(false, R.drawable.computer, getContext().getResources().getString(R.string.computer));
        applianceList.add(computer);

        Appliance fridge = new Appliance(false, R.drawable.fridge, getContext().getResources().getString(R.string.fridge));
        applianceList.add(fridge);

        Appliance hairDryer = new Appliance(false, R.drawable.hair_dryer, getContext().getResources().getString(R.string.hairDryer));
        applianceList.add(hairDryer);

        Appliance iron = new Appliance(false, R.drawable.iron, getContext().getResources().getString(R.string.iron));
        applianceList.add(iron);

        Appliance lights = new Appliance(false, R.drawable.lights, getContext().getResources().getString(R.string.lights));
        applianceList.add(lights);

        Appliance oven = new Appliance(false, R.drawable.oven, getContext().getResources().getString(R.string.oven));
        applianceList.add(oven);

        Appliance smartphone = new Appliance(false, R.drawable.smartphone, getContext().getResources().getString(R.string.smartphone));
        applianceList.add(smartphone);

        Appliance television = new Appliance(false, R.drawable.television, getContext().getResources().getString(R.string.television));
        applianceList.add(television);

        Appliance vacuumCleaner = new Appliance(false, R.drawable.vacuum_cleaner, getContext().getResources().getString(R.string.vacuumCleaner));
        applianceList.add(vacuumCleaner);

        Appliance washingMachine = new Appliance(false, R.drawable.washing_machine, getContext().getResources().getString(R.string.washingMachine));
        applianceList.add(washingMachine);

        Appliance waterHeater = new Appliance(false, R.drawable.water_heater, getContext().getResources().getString(R.string.waterHeater));
        applianceList.add(waterHeater);

        adapter.notifyDataSetChanged();
    }

    private void setAdapter() {
        recyclerViewAppliance = view.findViewById(R.id.recyclerViewAppliance);
        recyclerViewAppliance.setHasFixedSize(true);

        adapter = new RecyclerViewAdapter(applianceList, getContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerViewAppliance.setLayoutManager(mLayoutManager);
        recyclerViewAppliance.setItemAnimator(new DefaultItemAnimator());
        recyclerViewAppliance.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.item_divider));
        recyclerViewAppliance.addItemDecoration(dividerItemDecoration);
    }

    private void changeAppliances() {
        buttonContinue = view.findViewById(R.id.buttonContinue);
        buttonContinue.setOnClickListener(v -> {
            if (cityLocation.equals("")) {
                showSnackbar(getResources().getString(R.string.noLocation));
            } else {
                android.support.v7.app.AlertDialog.Builder reservationBuilder = new android.support.v7.app.AlertDialog.Builder(getContext());
                reservationBuilder.setView(R.layout.dialog_appliances);
                appliancesDialog = reservationBuilder.create();
                WindowManager.LayoutParams params = appliancesDialog.getWindow().getAttributes();
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int width = displayMetrics.widthPixels;
                int height = displayMetrics.heightPixels;
                params.width = (int) (width * 0.8);
                params.height = (int) (height * 0.8);
                appliancesDialog.getWindow().setAttributes(params);
                appliancesDialog.show();

                sureToAddAppliance = appliancesDialog.findViewById(R.id.sureToAddAppliance);
                appliancesList = appliancesDialog.findViewById(R.id.appliancesList);
                cancelAppliances = appliancesDialog.findViewById(R.id.cancelAppliances);
                confirmAppliances = appliancesDialog.findViewById(R.id.confirmAppliances);

                String data = "";
                applianceList = adapter.getData();

                for (int i = 0; i < applianceList.size(); i++) {
                    Appliance appliance = applianceList.get(i);
                    if (appliance.isCheck()) {
                        data += "\n" + appliance.getAppliance();
                        appliancesName.add(appliance.getAppliance());
                        appliancesImage.add(appliance.getImageResource());

                        appliancesList.setText(data);
                    }
                }

                if (data.equals("")) {
                    sureToAddAppliance.setText(R.string.noAppliance);
                    cancelAppliances.setText(R.string.back);
                    confirmAppliances.setVisibility(View.GONE);
                    appliancesList.setVisibility(View.GONE);
                }

                cancelAppliances.setOnClickListener(v12 -> appliancesDialog.dismiss());

                confirmAppliances.setOnClickListener(v1 -> {
                    appliancesDialog.dismiss();

                    // Display the progress during 1.1 seconds.
                    countDownTimer = new CountDownTimer(1100, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                        }

                        @Override
                        public void onFinish() {
                            countDownTimer.cancel();

                            FragmentAppliances fragmentAppliances = new FragmentAppliances();
                            Bundle bundle = new Bundle();
                            bundle.putStringArrayList("AppliancesName", appliancesName);
                            bundle.putIntegerArrayList("AppliancesImage", appliancesImage);
                            bundle.putString("City", cityLocation);
                            bundle.putDouble("CityIrradiance", irradianceLocation);
                            fragmentAppliances.setArguments(bundle);
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.layoutMain, fragmentAppliances, "FragmentAppliances")
                                    .commit();
                        }
                    }.start();
                });
            }
        });
    }

    private void addNewAppliance() {
        buttonAdd = view.findViewById(R.id.buttonAdd);
        buttonAdd.setOnClickListener(v -> {
            android.support.v7.app.AlertDialog.Builder reservationBuilder = new android.support.v7.app.AlertDialog.Builder(getContext());
            reservationBuilder.setView(R.layout.dialog_new_appliance);
            addApplianceDialog = reservationBuilder.create();
            final WindowManager.LayoutParams params = addApplianceDialog.getWindow().getAttributes();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels;
            int height = displayMetrics.heightPixels;
            params.width = (int) (width * 0.8);
            params.height = (int) (height * 0.8);
            addApplianceDialog.getWindow().setAttributes(params);
            addApplianceDialog.show();

            applianceImage = addApplianceDialog.findViewById(R.id.applianceImage);
            applianceName = addApplianceDialog.findViewById(R.id.applianceName);
            cancelBack = addApplianceDialog.findViewById(R.id.cancelBack);
            confirmBack = addApplianceDialog.findViewById(R.id.confirmBack);

            cancelBack.setOnClickListener(v12 -> addApplianceDialog.dismiss());

            confirmBack.setOnClickListener(v1 -> {
                if (!applianceName.getText().toString().isEmpty()) {
                    addApplianceDialog.dismiss();

                    Appliance newAppliance = new Appliance(false, R.drawable.user, applianceName.getText().toString());
                    applianceList.add(newAppliance);

                    adapter.notifyDataSetChanged();

                    showSnackbar(applianceName.getText().toString() + getResources().getString(R.string.added));
                }
            });
        });
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

                String apiUrl = finalApiUrl;

                requestQueue = Volley.newRequestQueue(getContext());

                JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, apiUrl, null, response -> {
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
                }, error -> {
                    Log.i("VOLLEY_ERROR", "" + error);
                    showSnackbar(getResources().getString(R.string.internetConnection));
                    exitFromApplication();
                });
                requestQueue.add(jsonArrayRequest);
            }
        };
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

    public void locationClick() {
        buttonLocation = view.findViewById(R.id.buttonLocation);
        buttonLocation.setOnClickListener(v -> {

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

            layoutDetect.setOnClickListener(v15 -> {
                locationDialog.dismiss();

                if (Build.VERSION.SDK_INT >= 23) {
                    if (getContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        Log.d("LOCATION", "Not granted");
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    } else
                        requestLocation();
                } else
                    requestLocation();
            });

            layoutSearch.setOnClickListener(v14 -> {
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

                locationPicker.setOnClickListener(v13 -> locationSpinner.performClick());

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

                cancelLocation.setOnClickListener(v12 -> searchLocationDialog.dismiss());

                confirmLocation.setOnClickListener(v1 -> {
                    searchLocationDialog.dismiss();

                    countDownTimer = new CountDownTimer(1000, 500) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                        }

                        @Override
                        public void onFinish() {
                            countDownTimer.cancel();

                            String apiUrl = finalApiUrl;

                            requestQueue = Volley.newRequestQueue(getContext());

                            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, apiUrl, null, response -> {
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
                            }, error -> {
                                Log.i("VOLLEY_ERROR", "" + error);
                                showSnackbar(getResources().getString(R.string.internetConnection));
                            });
                            requestQueue.add(jsonArrayRequest);
                        }
                    }.start();
                });
            });
        });
    }

    private String getCityName(LatLng myCoordinates) {
        String myCity = "";
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(myCoordinates.latitude, myCoordinates.longitude, 1);
            String address = addresses.get(0).getAddressLine(0);
            myCity = addresses.get(0).getAdminArea();
            Log.d("mylog", "Complete Address: " + addresses.toString());
            Log.d("mylog", "Address: " + address);
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

            String apiUrl = finalApiUrl;

            requestQueue = Volley.newRequestQueue(getContext());

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, apiUrl, null, response -> {
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
            }, error -> {
                Log.i("VOLLEY_ERROR", "" + error);
                showSnackbar(getResources().getString(R.string.internetConnection));
            });
            requestQueue.add(jsonArrayRequest);
        } else {
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setNumUpdates(1);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            showSnackbar(getResources().getString(R.string.tryingToLocation));
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mFusedLocationClient.requestLocationUpdates(locationRequest,
                    mLocationCallback, Looper.myLooper());
        }
    }

    public void showSnackbar(String text) {
        layoutMain = view.findViewById(R.id.layoutMain);

        Snackbar snackbar = Snackbar.make(layoutMain, text, Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(getResources().getColor(R.color.backgroundColor));
        TextView textView = snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(getResources().getColor(R.color.colorPrimary));
        snackbar.show();
    }
}