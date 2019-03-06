package myusarisoy.solarhomesystem;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Movie;
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
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;

import static android.content.Context.LOCATION_SERVICE;

public class FragmentMain extends Fragment {
    @BindView(R.id.layout_main)
    LinearLayout layout_main;

    @BindView(R.id.recycler_view_appliance)
    RecyclerView recycler_view_appliance;

    @BindView(R.id.button_sign_out)
    Button button_sign_out;

    @BindView(R.id.button_location)
    Button button_location;

    @BindView(R.id.button_continue)
    Button button_continue;

    LocationManager locationManager;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    CountDownTimer countDownTimer;
    private Button cancel_sign_out, confirm_sign_out;
    private AppCompatDialog signOutDialog;
    private FirebaseAuth firebaseAuth;
    private RecyclerViewAdapter adapter;
    private ArrayList<Appliance> applianceList = new ArrayList<>();
    private int isApplianceChecked = 0;
    View view;

    public static FragmentMain newInstance(Objects... objects) {
        FragmentMain fragment = new FragmentMain();
        Bundle args = new Bundle();
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

//        Set adapter for Recycler View.
        setAdapter();

//        Check current location.
        checkCurrentLocation();

//        Detect location with button click.
        locationClick();

//        Show list of appliances.
        initAppliances();

//        Sign out.
        clickToSignOut();

//        Check user's current location.
        locationClick();

        gotoAppliances();

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

        Appliance iron = new Appliance(false, R.drawable.iron, "Iron");
        applianceList.add(iron);

        Appliance lights = new Appliance(false, R.drawable.lights, "Lights");
        applianceList.add(lights);

        Appliance oven = new Appliance(false, R.drawable.oven, "Oven");
        applianceList.add(oven);

        Appliance television = new Appliance(false, R.drawable.television, "Television");
        applianceList.add(television);

        Appliance vacuumCleaner = new Appliance(false, R.drawable.vacuum_cleaner, "Vacuum Cleaner");
        applianceList.add(vacuumCleaner);

        Appliance washingMachine = new Appliance(false, R.drawable.washing_machine, "Washing Machine");
        applianceList.add(washingMachine);

        adapter.notifyDataSetChanged();
    }

    private void setAdapter() {
        recycler_view_appliance = view.findViewById(R.id.recycler_view_appliance);

        adapter = new RecyclerViewAdapter(applianceList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recycler_view_appliance.setLayoutManager(mLayoutManager);
        recycler_view_appliance.setItemAnimator(new DefaultItemAnimator());
        recycler_view_appliance.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.item_divider));
        recycler_view_appliance.addItemDecoration(dividerItemDecoration);


        recycler_view_appliance.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recycler_view_appliance, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Appliance appliance = applianceList.get(position);

                if (applianceList.get(position).isCheck() == false) {
                    appliance.setCheck(true);
                    showSnackbar(appliance.getAppliance() + " selected.");
                    activateButton();
                    isApplianceChecked += 1;
                } else {
                    showSnackbar(appliance.getAppliance() + " unselected.");
                    appliance.setCheck(false);
                    isApplianceChecked -= 1;
                    if (isApplianceChecked == 0)
                        deactivateButton();
                }
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));
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
    }

    private String getCityName(LatLng myCoordinates) {
        String myCity = "";
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(myCoordinates.latitude, myCoordinates.longitude, 1);
            String address = addresses.get(0).getAddressLine(0);
            myCity = addresses.get(0).getLocality();
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
            String cityName = getCityName(myCoordinates);
            showSnackbar("Current location is: " + cityName);
        } else {
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setNumUpdates(1);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            showSnackbar("Your location cannot be determined!");
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mFusedLocationClient.requestLocationUpdates(locationRequest,
                    mLocationCallback, Looper.myLooper());
        }
    }

    private void gotoAppliances() {
        button_continue = view.findViewById(R.id.button_continue);
        button_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentAppliances fragmentAppliances = new FragmentAppliances();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.layout_main, fragmentAppliances, "FragmentAppliances")
                        .commit();
            }
        });
    }

    private void activateButton() {
        button_continue = view.findViewById(R.id.button_continue);
        button_continue.setFocusable(true);
        button_continue.setClickable(true);
        button_continue.setTextColor(getResources().getColor(R.color.green));
    }

    private void deactivateButton() {
        button_continue = view.findViewById(R.id.button_continue);
        button_continue.setFocusable(false);
        button_continue.setClickable(false);
        button_continue.setTextColor(getResources().getColor(R.color.dark_slate_gray));
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        startLocationUpdates();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        fusedLocationClient.removeLocationUpdates(locationCallback);
//    }

//    public void setAdapter(ArrayList<Appliance> applianceList) {
//        recycler_view_appliance = view.findViewById(R.id.recycler_view_appliance);
//
//        adapter = new RecyclerViewAdapter(applianceList);
//        recycler_view_appliance.setAdapter(adapter);
//
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
//        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.item_divider));
//        recycler_view_appliance.addItemDecoration(dividerItemDecoration);
//
//        initAppliances();
//    }

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