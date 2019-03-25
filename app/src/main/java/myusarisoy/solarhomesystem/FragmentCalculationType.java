package myusarisoy.solarhomesystem;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import butterknife.BindView;

public class FragmentCalculationType extends Fragment {
    @BindView(R.id.img_bill)
    ImageView bill;

    @BindView(R.id.img_appliances)
    ImageView appliances;

    View view;

    public static FragmentCalculationType newInstance(Object... objects) {
        FragmentCalculationType fragment = new FragmentCalculationType();
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
        view = inflater.inflate(R.layout.fragment_calculation_type, container, false);

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
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.layout_main, fragmentBill, "FragmentBill")
                        .commit();
            }
        });

        appliances.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentMain fragmentMain = new FragmentMain();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.layout_main, fragmentMain, "FragmentMain")
                        .addToBackStack(null)
                        .commit();
            }
        });
    }
}