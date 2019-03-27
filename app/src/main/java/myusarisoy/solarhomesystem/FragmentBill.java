package myusarisoy.solarhomesystem;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

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

    @BindView(R.id.app_name)
    TextView app_name;

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

    private LocationManager locationManager;
    private String location = "";
    private CountDownTimer countDownTimer;
    private LocationCallback mLocationCallback;
    private Button cancel_sign_out, confirm_sign_out, cancel_location, confirm_location;
    private AppCompatDialog signOutDialog, locationDialog, searchLocationDialog;
    private FirebaseAuth firebaseAuth;
    private ArrayList<String> months = new ArrayList<>();
    String paymentData = "", powerConsumptionData = "";
    int monthIncrementer = 0;
    View view;

    public static FragmentBill newInstance(Object... objects) {
        FragmentBill fragment = new FragmentBill();
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
        view = inflater.inflate(R.layout.fragment_bill, container, false);

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

//        Make text white.
        makeTextWhite();

//        Make text black.
        makeTextBlack();

//        Add months to ArrayList.
        addMonths();

//        Check months.
        checkMonths();

//        Check current location.
        checkCurrentLocation();

//        Detect location with button click.
        locationClick();

//        Sign out.
        clickToSignOut();

//        Check user's current location.
        locationClick();

//        Continue to GridChoice.
        continueToGridChoice();

        return view;
    }

    private void addMonths() {
        months.add("January");
        months.add("February");
        months.add("March");
        months.add("April");
        months.add("May");
        months.add("June");
        months.add("July");
        months.add("August");
        months.add("September");
        months.add("October");
        months.add("November");
        months.add("December");
    }

    private void checkMonths() {
        bill_months = view.findViewById(R.id.bill_months);
        bill_payment = view.findViewById(R.id.bill_payment);
        bill_power_consumption = view.findViewById(R.id.bill_power_consumption);
        button_next = view.findViewById(R.id.button_next);
        button_continue = view.findViewById(R.id.button_continue);

        bill_months.setText(months.get(monthIncrementer));

        button_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bill_months.setText(months.get(monthIncrementer += 1));
                paymentData += bill_payment.getText().toString() + "\n";
                powerConsumptionData = bill_power_consumption.getText().toString() + "\n";

                bill_payment.getText().clear();
                bill_power_consumption.getText().clear();

                if (months.get(monthIncrementer).equals("March")) {
                    layout_bill.setBackgroundResource(R.drawable.spring);
                    makeTextBlack();
                } else if (months.get(monthIncrementer).equals("June")) {
                    layout_bill.setBackgroundResource(R.drawable.summer);
                    makeTextWhite();
                } else if (months.get(monthIncrementer).equals("September")) {
                    layout_bill.setBackgroundResource(R.drawable.autumn);
                    makeTextWhite();
                } else if (months.get(monthIncrementer).equals("December")) {
                    layout_bill.setBackgroundResource(R.drawable.winter);
                    makeTextBlack();
                    button_next.setVisibility(View.GONE);
                    button_continue.setVisibility(View.VISIBLE);
                }
            }
        });
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

    private void continueToGridChoice() {
        bill_payment = view.findViewById(R.id.bill_payment);
        bill_power_consumption = view.findViewById(R.id.bill_power_consumption);
        button_continue = view.findViewById(R.id.button_continue);

        button_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bill_payment.getText().toString().isEmpty() && !bill_power_consumption.getText().toString().isEmpty()) {
                    FragmentGridChoice fragmentGridChoice = new FragmentGridChoice();
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.layout_main, fragmentGridChoice, "FragmentGridChoice")
                            .commit();
                } else
                    showSnackbar("Please makes sure to complete the missing parts.");
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
                                location = place.getName();
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
                                        showSnackbar("Selected location is " + location + ".");
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
            showSnackbar("We are trying to detect your location...");
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

        Snackbar snackbar = Snackbar.make(layout_bill, text, Snackbar.LENGTH_SHORT);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(getResources().getColor(R.color.dark_slate_gray));
        TextView textView = snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(getResources().getColor(R.color.colorPrimary));
        snackbar.show();
    }
}