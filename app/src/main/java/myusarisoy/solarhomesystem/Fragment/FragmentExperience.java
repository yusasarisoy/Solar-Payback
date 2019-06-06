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

public class FragmentExperience extends Fragment {
    @BindView(R.id.beginner_layout)
    LinearLayout beginner;

    @BindView(R.id.expert_layout)
    LinearLayout expert;

    View view;

    public static FragmentExperience newInstance(Object... objects) {
        FragmentExperience fragment = new FragmentExperience();
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
        view = inflater.inflate(R.layout.fragment_experience, container, false);

//        Choose your experience.
        experienceChoice();

        return view;
    }

    private void experienceChoice() {
        beginner = view.findViewById(R.id.beginner_layout);
        expert = view.findViewById(R.id.expert_layout);

        beginner.setOnClickListener(v -> gotoConsumerPage("Beginner"));

        expert.setOnClickListener(v -> gotoConsumerPage("Expert"));
    }

    private String gotoConsumerPage(String experience) {
//        Set user's experience status about the solar energy.
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Experience", 0); // Private Mode
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Experience", experience);
        editor.apply();

        FragmentConsumer fragmentConsumer = new FragmentConsumer();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.layout_main, fragmentConsumer, "FragmentConsumer")
                .addToBackStack(null)
                .commit();

        return experience;
    }
}