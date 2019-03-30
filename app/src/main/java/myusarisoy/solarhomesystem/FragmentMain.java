package myusarisoy.solarhomesystem;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.libraries.places.api.Places;

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
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
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

public class FragmentMain extends Fragment {
    @BindView(R.id.layout_main)
    LinearLayout layout_main;

    @BindView(R.id.image_icon)
    ImageView icon;

    @BindView(R.id.recycler_view_appliance)
    RecyclerView recycler_view_appliance;

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

    private LocationManager locationManager;
    private CountDownTimer countDownTimer;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private Button cancel_sign_out, confirm_sign_out, cancel_location, confirm_location, cancel_appliances, confirm_appliances;
    private AppCompatDialog signOutDialog, locationDialog, searchLocationDialog, appliancesDialog;
    private FirebaseAuth firebaseAuth;
    private RecyclerViewAdapter adapter;
    private TextView sure_to_add_appliance, appliances_list;
    private ArrayList<Appliance> applianceList = new ArrayList<>();
    public ArrayList<String> appliancesName = new ArrayList<>();
    public ArrayList<Integer> appliancesImage = new ArrayList<>();
    private ArrayList<String> cityList = new ArrayList<>();
    private ArrayList<Double> solarIrradianceList = new ArrayList<>();
    double irradianceData;
    String consumer, cityName = "", city = "";
    private RequestQueue requestQueue;
    View view;

    public static FragmentMain newInstance(Object... objects) {
        FragmentMain fragment = new FragmentMain();
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
        view = inflater.inflate(R.layout.fragment_main, container, false);

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

//        Sign out.
        clickToSignOut();

//        Check user's current location.
        locationClick();

        return view;
    }

    private void setDataToView(FirebaseUser user) {
//        email.setText("User Email: " + user.getEmail());
    }

    FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
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

    public void clickToSignOut() {
        button_sign_out = view.findViewById(R.id.button_sign_out);
        button_sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                confirm_sign_out.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        signOutDialog.dismiss();

                        final ProgressDialog progressDialog = ProgressDialog.show(getContext(), "Logout", "Please wait...", true, true);
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
                    }
                });
            }
        });
    }

    public void signOut() {
        firebaseAuth.signOut();

        FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
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

        Appliance airConditioner = new Appliance(false, R.drawable.air_conditioner, "Air Conditioner");
        applianceList.add(airConditioner);

        Appliance bakery = new Appliance(false, R.drawable.bakery, "Bakery");
        applianceList.add(bakery);

        Appliance coffeeMachine = new Appliance(false, R.drawable.coffee_machine, "Coffee Machine");
        applianceList.add(coffeeMachine);

        Appliance computer = new Appliance(false, R.drawable.computer, "Computer");
        applianceList.add(computer);

        Appliance fridge = new Appliance(false, R.drawable.fridge, "Fridge");
        applianceList.add(fridge);

        Appliance hairDryer = new Appliance(false, R.drawable.hair_dryer, "Hair Dryer");
        applianceList.add(hairDryer);

        Appliance iron = new Appliance(false, R.drawable.iron, "Iron");
        applianceList.add(iron);

        Appliance lights = new Appliance(false, R.drawable.lights, "Lights");
        applianceList.add(lights);

        Appliance oven = new Appliance(false, R.drawable.oven, "Oven");
        applianceList.add(oven);

        Appliance smartphone = new Appliance(false, R.drawable.smartphone, "Smartphone");
        applianceList.add(smartphone);

        Appliance television = new Appliance(false, R.drawable.television, "Television");
        applianceList.add(television);

        Appliance vacuumCleaner = new Appliance(false, R.drawable.vacuum_cleaner, "Vacuum Cleaner");
        applianceList.add(vacuumCleaner);

        Appliance washingMachine = new Appliance(false, R.drawable.washing_machine, "Washing Machine");
        applianceList.add(washingMachine);

        Appliance waterHeater = new Appliance(false, R.drawable.water_heater, "Water Heater");
        applianceList.add(waterHeater);

        adapter.notifyDataSetChanged();
    }

    private void setAdapter() {
        recycler_view_appliance = view.findViewById(R.id.recycler_view_appliance);

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
        button_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    }
                }

                if (data.equals("")) {
                    sure_to_add_appliance.setText(R.string.no_appliance);
                    cancel_appliances.setText(R.string.back);
                    confirm_appliances.setVisibility(View.GONE);
                    appliances_list.setVisibility(View.GONE);
                } else
                    appliances_list.setText(data);

                cancel_appliances.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        appliancesDialog.dismiss();
                    }
                });

                confirm_appliances.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        appliancesDialog.dismiss();

                        // Display the progress during 1 second.
                        countDownTimer = new CountDownTimer(1000, 500) {
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
                                fragmentAppliances.setArguments(bundle);
                                getActivity().getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.layout_main, fragmentAppliances, "FragmentAppliances")
                                        .commit();
                            }
                        }.start();
                    }
                });
            }
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
                String cityName = getCityName(myCoordinates);
                showSnackbar("Current location is: " + cityName);
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

                layout_search.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        locationDialog.dismiss();

                        android.support.v7.app.AlertDialog.Builder reservationBuilder = new android.support.v7.app.AlertDialog.Builder(getContext());
                        reservationBuilder.setView(R.layout.pop_up_location);
                        searchLocationDialog = reservationBuilder.create();
                        final WindowManager.LayoutParams params = searchLocationDialog.getWindow().getAttributes();
                        DisplayMetrics displayMetrics = new DisplayMetrics();
                        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                        int width = displayMetrics.widthPixels;
                        int height = displayMetrics.heightPixels;
                        params.width = (int) (width * 0.8);
                        params.height = (int) (height * 0.8);
                        searchLocationDialog.getWindow().setAttributes(params);
                        searchLocationDialog.show();

                        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

                        autocompleteFragment.setPlaceFields(Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.ID, com.google.android.libraries.places.api.model.Place.Field.NAME, com.google.android.libraries.places.api.model.Place.Field.LAT_LNG));

                        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                            @Override
                            public void onPlaceSelected(@NonNull com.google.android.libraries.places.api.model.Place place) {
                                cityName = place.getName();
                                Log.i("LOCATION", "Place: " + place.getName() + ", " + place.getId());
                            }

                            @Override
                            public void onError(@NonNull Status status) {
                                Log.i("LOCATION", "An error occurred: " + status);
                            }
                        });

                        cancel_location = searchLocationDialog.findViewById(R.id.cancel_location);
                        confirm_location = searchLocationDialog.findViewById(R.id.confirm_location);

                        cancel_location.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                searchLocationDialog.dismiss();
                            }
                        });

                        confirm_location.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
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
                                                        cityList.add(city);
                                                        solarIrradianceList.add(irradianceData);

                                                        if (cityList.get(i).equals(cityName))
                                                            showSnackbar("City: " + city + ", Solar Irradiance Data: " + solarIrradianceList.get(i));
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Log.i("VOLLEY_ERROR", "" + error);
                                            }
                                        });
                                        requestQueue.add(jsonArrayRequest);
                                    }
                                }.start();
                            }
                        });
                    }
                });
            }
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
            final String cityName = getCityName(myCoordinates);

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
                            cityList.add(city);
                            solarIrradianceList.add(irradianceData);

                            if (cityList.get(i).equals(cityName))
                                showSnackbar("Current City: " + city + "Solar Irradiance Data: " + solarIrradianceList.get(i));
                            else
                                showSnackbar("Current City: " + cityName);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("VOLLEY_ERROR", "" + error);
                }
            });
            requestQueue.add(jsonArrayRequest);
        } else {
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setNumUpdates(1);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            showSnackbar("We are trying to detect your location...");
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

        Snackbar snackbar = Snackbar.make(layout_main, text, Snackbar.LENGTH_SHORT);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(getResources().getColor(R.color.dark_slate_gray));
        TextView textView = snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(getResources().getColor(R.color.colorPrimary));
        snackbar.show();
    }
}