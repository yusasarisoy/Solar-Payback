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

import static android.content.Context.LOCATION_SERVICE;

public class FragmentMain extends Fragment {
    @BindView(R.id.layout_main)
    LinearLayout layout_main;

    @BindView(R.id.image_icon)
    ImageView icon;

    @BindView(R.id.image_language)
    ImageView language;

    @BindView(R.id.recycler_view_appliance)
    RecyclerView recycler_view_appliance;

    @BindView(R.id.button_add)
    FloatingActionButton button_add;

    @BindView(R.id.button_sign_out)
    Button button_sign_out;

    @BindView(R.id.button_location)
    Button button_location;

    @BindView(R.id.layout_detect)
    LinearLayout layout_detect;

    @BindView(R.id.layout_search)
    LinearLayout layout_search;

    @BindView(R.id.button_continue)
    Button button_continue;

    Context context;
    private boolean turkey, usa;
    private LocationManager locationManager;
    private CountDownTimer countDownTimer;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private Button cancel_back, confirm_back, cancel_sign_out, confirm_sign_out, cancel_location, confirm_location, cancel_appliances, confirm_appliances, exit_from_app, confirm_country;
    private AppCompatDialog signOutDialog, addApplianceDialog, locationDialog, searchLocationDialog, appliancesDialog, exitDialog, countryDialog, languageDialog;
    private FirebaseAuth firebaseAuth;
    private RecyclerViewAdapter adapter;
    private EditText appliance_name;
    private LinearLayout layout_location;
    private ImageView appliance_image, locationPicker, img_english, img_german, img_turkish;
    private Spinner locationSpinner;
    private TextView sure_to_add_appliance, appliances_list;
    private ArrayList<Appliance> applianceList = new ArrayList<>();
    public ArrayList<String> appliancesName = new ArrayList<>();
    public ArrayList<Integer> appliancesImage = new ArrayList<>();
    private ArrayList<String> cityList = new ArrayList<>();
    private ArrayList<Double> solarIrradianceList = new ArrayList<>();
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

        context = container.getContext();

        firebaseAuth = FirebaseAuth.getInstance();

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        setDataToView(user);

        authStateListener = firebaseAuth -> {
            FirebaseUser user1 = firebaseAuth.getCurrentUser();
            if (user1 == null) {
                FragmentWelcome fragmentWelcome = new FragmentWelcome();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.layout_main, fragmentWelcome, "FragmentWelcome")
                        .addToBackStack(null)
                        .commit();
            }
        };

        icon = view.findViewById(R.id.image_icon);
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

    private void languageClick() {
        language = view.findViewById(R.id.image_language);

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

                img_english = languageDialog.findViewById(R.id.img_english);
                img_german = languageDialog.findViewById(R.id.img_german);
                img_turkish = languageDialog.findViewById(R.id.img_turkish);

                img_english.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        languageDialog.dismiss();
                        setLocale("en");
                        getActivity().recreate();
                    }
                });

                img_german.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        languageDialog.dismiss();
                        setLocale("de");
                        getActivity().recreate();
                    }
                });

                img_turkish.setOnClickListener(new View.OnClickListener() {
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

            cancel_sign_out.setOnClickListener(v12 -> signOutDialog.dismiss());

            confirm_sign_out.setOnClickListener(v1 -> {
                signOutDialog.dismiss();

                final ProgressDialog progressDialog = ProgressDialog.show(getContext(), getResources().getString(R.string.logout), getResources().getString(R.string.please_wait), true, true);
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
                        .replace(R.id.layout_main, fragmentWelcome, "FragmentWelcome")
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
        recycler_view_appliance = view.findViewById(R.id.recycler_view_appliance);

        Appliance airConditioner = new Appliance(false, R.drawable.air_conditioner, getContext().getResources().getString(R.string.air_conditioner));
        applianceList.add(airConditioner);

        Appliance bakery = new Appliance(false, R.drawable.bakery, getContext().getResources().getString(R.string.bakery));
        applianceList.add(bakery);

        Appliance coffeeMachine = new Appliance(false, R.drawable.coffee_machine, getContext().getResources().getString(R.string.coffee_machine));
        applianceList.add(coffeeMachine);

        Appliance computer = new Appliance(false, R.drawable.computer, getContext().getResources().getString(R.string.computer));
        applianceList.add(computer);

        Appliance fridge = new Appliance(false, R.drawable.fridge, getContext().getResources().getString(R.string.fridge));
        applianceList.add(fridge);

        Appliance hairDryer = new Appliance(false, R.drawable.hair_dryer, getContext().getResources().getString(R.string.hair_dryer));
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

        Appliance vacuumCleaner = new Appliance(false, R.drawable.vacuum_cleaner, getContext().getResources().getString(R.string.vacuum_cleaner));
        applianceList.add(vacuumCleaner);

        Appliance washingMachine = new Appliance(false, R.drawable.washing_machine, getContext().getResources().getString(R.string.washing_machine));
        applianceList.add(washingMachine);

        Appliance waterHeater = new Appliance(false, R.drawable.water_heater, getContext().getResources().getString(R.string.water_heater));
        applianceList.add(waterHeater);

        adapter.notifyDataSetChanged();
    }

    private void setAdapter() {
        recycler_view_appliance = view.findViewById(R.id.recycler_view_appliance);
        recycler_view_appliance.setHasFixedSize(true);

        adapter = new RecyclerViewAdapter(applianceList, getContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recycler_view_appliance.setLayoutManager(mLayoutManager);
        recycler_view_appliance.setItemAnimator(new DefaultItemAnimator());
        recycler_view_appliance.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.item_divider));
        recycler_view_appliance.addItemDecoration(dividerItemDecoration);
    }

    private void changeAppliances() {
        button_continue = view.findViewById(R.id.button_continue);
        button_continue.setOnClickListener(v -> {
            if (cityLocation.equals("")) {
                showSnackbar(getResources().getString(R.string.no_location));
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

                sure_to_add_appliance = appliancesDialog.findViewById(R.id.sure_to_add_appliance);
                appliances_list = appliancesDialog.findViewById(R.id.appliances_list);
                cancel_appliances = appliancesDialog.findViewById(R.id.cancel_appliances);
                confirm_appliances = appliancesDialog.findViewById(R.id.confirm_appliances);

                String data = "";
                applianceList = adapter.getData();

                for (int i = 0; i < applianceList.size(); i++) {
                    Appliance appliance = applianceList.get(i);
                    if (appliance.isCheck()) {
                        data += "\n" + appliance.getAppliance();
                        appliancesName.add(appliance.getAppliance());
                        appliancesImage.add(appliance.getImageResource());

                        appliances_list.setText(data);
                    }
                }

                if (data.equals("")) {
                    sure_to_add_appliance.setText(R.string.no_appliance);
                    cancel_appliances.setText(R.string.back);
                    confirm_appliances.setVisibility(View.GONE);
                    appliances_list.setVisibility(View.GONE);
                }

                cancel_appliances.setOnClickListener(v12 -> appliancesDialog.dismiss());

                confirm_appliances.setOnClickListener(v1 -> {
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
                                    .replace(R.id.layout_main, fragmentAppliances, "FragmentAppliances")
                                    .commit();
                        }
                    }.start();
                });
            }
        });
    }

    private void addNewAppliance() {
        button_add = view.findViewById(R.id.button_add);
        button_add.setOnClickListener(v -> {
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

            appliance_image = addApplianceDialog.findViewById(R.id.appliance_image);
            appliance_name = addApplianceDialog.findViewById(R.id.appliance_name);
            cancel_back = addApplianceDialog.findViewById(R.id.cancel_back);
            confirm_back = addApplianceDialog.findViewById(R.id.confirm_back);

            cancel_back.setOnClickListener(v12 -> addApplianceDialog.dismiss());

            confirm_back.setOnClickListener(v1 -> {
                if (!appliance_name.getText().toString().isEmpty()) {
                    addApplianceDialog.dismiss();

                    Appliance newAppliance = new Appliance(false, R.drawable.user, appliance_name.getText().toString());
                    applianceList.add(newAppliance);

                    adapter.notifyDataSetChanged();
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
                                showSnackbar(getResources().getString(R.string.city) + cityList.get(i) + getResources().getString(R.string.solar_irradiance) + solarIrradianceList.get(i));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
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

    public void locationClick() {
        button_location = view.findViewById(R.id.button_location);
        button_location.setOnClickListener(v -> {

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

            layout_detect.setOnClickListener(v15 -> {
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

            layout_search.setOnClickListener(v14 -> {
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

                locationPicker.setOnClickListener(v13 -> locationSpinner.performClick());

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

                cancel_location.setOnClickListener(v12 -> searchLocationDialog.dismiss());

                confirm_location.setOnClickListener(v1 -> {
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
                                            showSnackbar(getResources().getString(R.string.city) + cityList.get(i) + getResources().getString(R.string.solar_irradiance) + solarIrradianceList.get(i));
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }, error -> {
                                Log.i("VOLLEY_ERROR", "" + error);
                                showSnackbar(getResources().getString(R.string.internet_connection));
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
                            showSnackbar(getResources().getString(R.string.city) + cityList.get(i) + getResources().getString(R.string.solar_irradiance) + solarIrradianceList.get(i));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, error -> {
                Log.i("VOLLEY_ERROR", "" + error);
                showSnackbar(getResources().getString(R.string.internet_connection));
            });
            requestQueue.add(jsonArrayRequest);
        } else {
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setNumUpdates(1);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            showSnackbar(getResources().getString(R.string.trying_to_location));
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mFusedLocationClient.requestLocationUpdates(locationRequest,
                    mLocationCallback, Looper.myLooper());
        }
    }

    public void showSnackbar(String text) {
        layout_main = view.findViewById(R.id.layout_main);

        Snackbar snackbar = Snackbar.make(layout_main, text, Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(getResources().getColor(R.color.dark_slate_gray));
        TextView textView = snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(getResources().getColor(R.color.colorPrimary));
        snackbar.show();
    }
}