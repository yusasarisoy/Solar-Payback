package myusarisoy.solarhomesystem;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.Objects;

import butterknife.BindView;

public class FragmentSplash extends Fragment {
    @BindView(R.id.icon)
    ImageView icon;

    CountDownTimer countDownTimer;
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

        // Display the application icon during 1 second.
        countDownTimer = new CountDownTimer(1000, 500) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                countDownTimer.cancel();

                FragmentInstructions fragmentInstructions = new FragmentInstructions();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.layout_main, fragmentInstructions, "FragmentInstructions")
                        .addToBackStack(null)
                        .commit();
            }
        }.start();

        return view;
    }

}
