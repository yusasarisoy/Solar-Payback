package myusarisoy.solarhomesystem;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;

public class FragmentMain extends Fragment {
    @BindView(R.id.layout_main)
    LinearLayout layout_main;

    @BindView(R.id.recycler_view_appliance)
    RecyclerView recycler_view_appliance;

    @BindView(R.id.button_sign_out)
    Button button_sign_out;

    @BindView(R.id.button_continue)
    Button button_continue;

    CountDownTimer countDownTimer;
    private Button cancel_sign_out, confirm_sign_out;
    private AppCompatDialog signOutDialog;
    private FirebaseAuth firebaseAuth;
    private RecyclerViewAdapter adapter;
    private ArrayList<Appliance> applianceList = new ArrayList<>();
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

        recycler_view_appliance = view.findViewById(R.id.recycler_view_appliance);

        adapter = new RecyclerViewAdapter(applianceList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recycler_view_appliance.setLayoutManager(mLayoutManager);
        recycler_view_appliance.setItemAnimator(new DefaultItemAnimator());
        recycler_view_appliance.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.item_divider));
        recycler_view_appliance.addItemDecoration(dividerItemDecoration);

        initAppliances();

//        Sign out.
        clickToSignOut();

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

        Appliance airConditioner = new Appliance(getResources().getDrawable(R.drawable.non_checked), getResources().getDrawable(R.drawable.air_conditioner), "Air Conditioner");
        applianceList.add(airConditioner);

        Appliance bakery = new Appliance(getResources().getDrawable(R.drawable.non_checked), getResources().getDrawable(R.drawable.bakery), "Bakery");
        applianceList.add(bakery);

        Appliance coffeeMachine = new Appliance(getResources().getDrawable(R.drawable.non_checked), getResources().getDrawable(R.drawable.coffee_machine), "Coffee Machine");
        applianceList.add(coffeeMachine);

        Appliance computer = new Appliance(getResources().getDrawable(R.drawable.non_checked), getResources().getDrawable(R.drawable.computer), "Computer");
        applianceList.add(computer);

        Appliance fridge = new Appliance(getResources().getDrawable(R.drawable.non_checked), getResources().getDrawable(R.drawable.fridge), "Fridge");
        applianceList.add(fridge);

        Appliance iron = new Appliance(getResources().getDrawable(R.drawable.non_checked), getResources().getDrawable(R.drawable.iron), "Iron");
        applianceList.add(iron);

        Appliance lights = new Appliance(getResources().getDrawable(R.drawable.non_checked), getResources().getDrawable(R.drawable.lights), "Lights");
        applianceList.add(lights);

        Appliance oven = new Appliance(getResources().getDrawable(R.drawable.non_checked), getResources().getDrawable(R.drawable.oven), "Oven");
        applianceList.add(oven);

        Appliance television = new Appliance(getResources().getDrawable(R.drawable.non_checked), getResources().getDrawable(R.drawable.television), "Television");
        applianceList.add(television);

        Appliance vacuumCleaner = new Appliance(getResources().getDrawable(R.drawable.non_checked), getResources().getDrawable(R.drawable.vacuum_cleaner), "Vacuum Cleaner");
        applianceList.add(vacuumCleaner);

        Appliance washingMachine = new Appliance(getResources().getDrawable(R.drawable.non_checked), getResources().getDrawable(R.drawable.washing_machine), "Washing Machine");
        applianceList.add(washingMachine);

        adapter.notifyDataSetChanged();
    }

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

   /* public void showSnackbar(String text) {
        layout_main = view.findViewById(R.id.layout_main);

        Snackbar snackbar = Snackbar.make(layout_main, text, Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(getResources().getColor(R.color.dark_slate_gray));
        TextView textView = snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(getResources().getColor(R.color.colorPrimary));
        snackbar.show();
    }*/
}