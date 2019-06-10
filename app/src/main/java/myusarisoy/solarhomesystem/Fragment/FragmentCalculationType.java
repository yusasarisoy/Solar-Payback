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
    @BindView(R.id.billLayout)
    LinearLayout bill;

    @BindView(R.id.appliancesLayout)
    LinearLayout appliances;

    AppCompatDialog countryDialog;
    boolean turkey = false, usa = false;
    Button confirmCountry;
    ImageView imgTurkey, imgUsa;
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

        imgTurkey = countryDialog.findViewById(R.id.imgTurkey);
        imgUsa = countryDialog.findViewById(R.id.imgUsa);
        confirmCountry = countryDialog.findViewById(R.id.confirmCountry);

        imgTurkey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!turkey) {
                    turkey = true;
                    usa = false;
                    confirmCountry.setClickable(true);
                    confirmCountry.setFocusable(true);
                    confirmCountry.setBackgroundResource(R.color.green);
                    imgTurkey.setBackgroundResource(R.drawable.dark_shape_selected);
                    imgUsa.setBackgroundResource(R.color.transparent);
                    apiUrl = "https://private-54ade8-apiforpaybackcalculationsystem.apiary-mock.com/questions";
                } else {
                    turkey = false;
                    confirmCountry.setClickable(false);
                    confirmCountry.setFocusable(false);
                    confirmCountry.setBackgroundResource(R.color.gray);
                    imgTurkey.setBackgroundResource(R.color.transparent);
                }
            }
        });

        imgUsa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!usa) {
                    usa = true;
                    turkey = false;
                    confirmCountry.setClickable(true);
                    confirmCountry.setFocusable(true);
                    confirmCountry.setBackgroundResource(R.color.green);
                    imgTurkey.setBackgroundResource(R.color.transparent);
                    imgUsa.setBackgroundResource(R.drawable.dark_shape_selected);
                    apiUrl = "https://private-5ec0c4-apiforsolarpaybackusa.apiary-mock.com/questions";
                } else {
                    usa = false;
                    confirmCountry.setClickable(false);
                    confirmCountry.setFocusable(false);
                    confirmCountry.setBackgroundResource(R.color.gray);
                    imgUsa.setBackgroundResource(R.color.transparent);
                }
            }
        });

        confirmCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countryDialog.dismiss();
            }
        });
    }

    private void calculationType() {
        bill = view.findViewById(R.id.billLayout);
        appliances = view.findViewById(R.id.appliancesLayout);

        bill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentBill fragmentBill = new FragmentBill();
                Bundle bundle = new Bundle();
                bundle.putString("consumer", consumer);
                bundle.putString("api", "https://private-54ade8-apiforpaybackcalculationsystem.apiary-mock.com/questions");
                fragmentBill.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.layoutMain, fragmentBill, "FragmentBill")
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
                        .replace(R.id.layoutMain, fragmentMain, "FragmentMain")
                        .addToBackStack(null)
                        .commit();
            }
        });
    }
}