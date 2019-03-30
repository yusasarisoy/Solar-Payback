package myusarisoy.solarhomesystem;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import butterknife.BindView;

public class FragmentCalculationType extends Fragment {
    @BindView(R.id.img_bill)
    ImageView bill;

    @BindView(R.id.img_appliances)
    ImageView appliances;

    public String consumer;
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
        Log.i("CONSUMER", consumer + "");

//        Choose calculation type.
        calculationType();

        return view;
    }

    private void calculationType() {
        bill = view.findViewById(R.id.img_bill);
        appliances = view.findViewById(R.id.img_appliances);

        bill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentBill fragmentBill = new FragmentBill();
                Bundle bundle = new Bundle();
                bundle.putString("consumer", consumer);
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
                fragmentMain.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.layout_main, fragmentMain, "FragmentMain")
                        .addToBackStack(null)
                        .commit();
            }
        });
    }
}