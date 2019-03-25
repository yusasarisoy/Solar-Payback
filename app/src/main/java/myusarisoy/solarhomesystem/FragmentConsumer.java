package myusarisoy.solarhomesystem;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import butterknife.BindView;

public class FragmentConsumer extends Fragment {
    @BindView(R.id.img_residental)
    ImageView residental;

    @BindView(R.id.img_commercial)
    ImageView commercial;

    View view;

    public static FragmentConsumer newInstance(Object... objects) {
        FragmentConsumer fragment = new FragmentConsumer();
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
        view = inflater.inflate(R.layout.fragment_consumer, container, false);

//        Choice of consumer type.
        consumerType();

        return view;
    }

    private void consumerType() {
        residental = view.findViewById(R.id.img_residental);
        commercial = view.findViewById(R.id.img_commercial);

        residental.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentCalculationType fragmentCalculationType = new FragmentCalculationType();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.layout_main, fragmentCalculationType, "FragmentCalculationType")
                        .commit();
            }
        });

        commercial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentCalculationType fragmentCalculationType = new FragmentCalculationType();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.layout_main, fragmentCalculationType, "FragmentCalculationType")
                        .commit();
            }
        });
    }
}