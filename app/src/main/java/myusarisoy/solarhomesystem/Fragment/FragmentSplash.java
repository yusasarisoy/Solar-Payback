package myusarisoy.solarhomesystem.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.Objects;

import butterknife.BindView;
import myusarisoy.solarhomesystem.R;

public class FragmentSplash extends Fragment {
    @BindView(R.id.icon)
    ImageView icon;

    View view;

    public static FragmentSplash newInstance(Objects... objects) {
        FragmentSplash fragment = new FragmentSplash();
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
        view = inflater.inflate(R.layout.fragment_splash, container, false);

        new Handler().postDelayed(() -> {
            FragmentInstructions fragmentInstructions = new FragmentInstructions();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.layout_main, fragmentInstructions, "FragmentInstructions")
                    .addToBackStack(null)
                    .commit();
        }, 250);

        return view;
    }
}