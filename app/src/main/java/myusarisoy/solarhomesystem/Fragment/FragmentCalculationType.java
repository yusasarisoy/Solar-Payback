package myusarisoy.solarhomesystem.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDialog;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import butterknife.BindView;
import myusarisoy.solarhomesystem.R;

public class FragmentCalculationType extends Fragment {
    @BindView(R.id.bill_layout)
    LinearLayout bill;

    @BindView(R.id.appliances_layout)
    LinearLayout appliances;

    AppCompatDialog countryDialog;
    boolean turkey = false, usa = false;
    Button confirm_country;
    ImageView img_turkey, img_usa;
    String consumer, apiUrl;
    View view;

    public static FragmentCalculationType newInstance(Object... objects) {
        FragmentCalculationType fragment = new FragmentCalculationType();
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
        view = inflater.inflate(R.layout.fragment_calculation_type, container, false);

        consumer = getArguments().getString("consumer");

//        Get country.
//        getCountry();

//        Choose calculation type.
        calculationType();

        return view;
    }

    private void getCountry() {
        android.support.v7.app.AlertDialog.Builder reservationBuilder = new android.support.v7.app.AlertDialog.Builder(getContext());
        reservationBuilder.setView(R.layout.pop_up_country);
        countryDialog = reservationBuilder.create();
        final WindowManager.LayoutParams params = countryDialog.getWindow().getAttributes();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        params.width = (int) (width * 0.8);
        params.height = (int) (height * 0.8);
        countryDialog.getWindow().setAttributes(params);
        countryDialog.show();
        countryDialog.setCancelable(false);

        img_turkey = countryDialog.findViewById(R.id.img_turkey);
        img_usa = countryDialog.findViewById(R.id.img_usa);
        confirm_country = countryDialog.findViewById(R.id.confirm_country);

        img_turkey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!turkey) {
                    turkey = true;
                    usa = false;
                    confirm_country.setClickable(true);
                    confirm_country.setFocusable(true);
                    confirm_country.setBackgroundResource(R.color.green);
                    img_turkey.setBackgroundResource(R.drawable.dark_shape_selected);
                    img_usa.setBackgroundResource(R.color.transparent);
                    apiUrl = "https://private-54ade8-apiforpaybackcalculationsystem.apiary-mock.com/questions";
                } else {
                    turkey = false;
                    confirm_country.setClickable(false);
                    confirm_country.setFocusable(false);
                    confirm_country.setBackgroundResource(R.color.gray);
                    img_turkey.setBackgroundResource(R.color.transparent);
                }
            }
        });

        img_usa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!usa) {
                    usa = true;
                    turkey = false;
                    confirm_country.setClickable(true);
                    confirm_country.setFocusable(true);
                    confirm_country.setBackgroundResource(R.color.green);
                    img_turkey.setBackgroundResource(R.color.transparent);
                    img_usa.setBackgroundResource(R.drawable.dark_shape_selected);
                    apiUrl = "https://private-5ec0c4-apiforsolarpaybackusa.apiary-mock.com/questions";
                } else {
                    usa = false;
                    confirm_country.setClickable(false);
                    confirm_country.setFocusable(false);
                    confirm_country.setBackgroundResource(R.color.gray);
                    img_usa.setBackgroundResource(R.color.transparent);
                }
            }
        });

        confirm_country.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countryDialog.dismiss();
            }
        });
    }

    private void calculationType() {
        bill = view.findViewById(R.id.bill_layout);
        appliances = view.findViewById(R.id.appliances_layout);

        bill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentBill fragmentBill = new FragmentBill();
                Bundle bundle = new Bundle();
                bundle.putString("consumer", consumer);
                bundle.putString("api", "https://private-54ade8-apiforpaybackcalculationsystem.apiary-mock.com/questions");
                fragmentBill.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.layout_main, fragmentBill, "FragmentBill")
                        .commit();
            }
        });

        appliances.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentMain fragmentMain = new FragmentMain();
                Bundle bundle = new Bundle();
                bundle.putString("consumer", consumer);
                bundle.putString("api", "https://private-54ade8-apiforpaybackcalculationsystem.apiary-mock.com/questions");
                fragmentMain.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.layout_main, fragmentMain, "FragmentMain")
                        .addToBackStack(null)
                        .commit();
            }
        });
    }
}