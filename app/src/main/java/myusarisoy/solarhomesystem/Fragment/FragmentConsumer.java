package myusarisoy.solarhomesystem.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import butterknife.BindView;
import myusarisoy.solarhomesystem.R;

public class FragmentConsumer extends Fragment {
    @BindView(R.id.residentialLayout)
    LinearLayout residental;

    @BindView(R.id.commercialLayout)
    LinearLayout commercial;

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
        residental = view.findViewById(R.id.residentialLayout);
        commercial = view.findViewById(R.id.commercialLayout);

        residental.setOnClickListener(v -> {
            setConsumerType("Residental");

            FragmentCalculationType fragmentCalculationType = new FragmentCalculationType();
            Bundle bundle = new Bundle();
            bundle.putString("consumer", "residental");
            fragmentCalculationType.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_left_to_right, R.anim.exit_left_to_right, R.anim.enter_right_to_left, R.anim.exit_left_to_right)
                    .replace(R.id.layoutMain, fragmentCalculationType, "FragmentCalculationType")
                    .commit();
        });

        commercial.setOnClickListener(v -> {
            setConsumerType("Commercial");

            FragmentCalculationType fragmentCalculationType = new FragmentCalculationType();
            Bundle bundle = new Bundle();
            bundle.putString("consumer", "commercial");
            fragmentCalculationType.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_left_to_right, R.anim.exit_left_to_right, R.anim.enter_right_to_left, R.anim.exit_left_to_right)
                    .replace(R.id.layoutMain, fragmentCalculationType, "FragmentCalculationType")
                    .commit();
        });
    }

    private String setConsumerType(String consumer) {
//        Set user's consumer status about the solar energy.
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Consumer", 0); // Private Mode
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Consumer", consumer);
        editor.apply();

        return consumer;
    }
}