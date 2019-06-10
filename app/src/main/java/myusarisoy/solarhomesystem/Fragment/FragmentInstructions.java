package myusarisoy.solarhomesystem.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import butterknife.BindView;
import myusarisoy.solarhomesystem.Adapter.InstructionsAdapter;
import myusarisoy.solarhomesystem.R;

public class FragmentInstructions extends Fragment {
    @BindView(R.id.viewPager)
    ViewPager viewPager;

    @BindView(R.id.pagerIndicator)
    LinearLayout pagerIndicator;

    @BindView(R.id.button)
    Button buttonBack;

    @BindView(R.id.buttonNext)
    Button buttonNext;

    FirebaseAuth firebaseAuth;
    private int currentPage;
    private TextView[] dots;
    InstructionsAdapter adapter;
    View view;

    public static FragmentInstructions newInstance(Objects... objects) {
        FragmentInstructions fragment = new FragmentInstructions();
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
        view = inflater.inflate(R.layout.fragment_instructions, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            FragmentExperience fragmentExperience = new FragmentExperience();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.layoutMain, fragmentExperience, "FragmentExperience")
                    .addToBackStack(null)
                    .commit();
        }

        adapter = new InstructionsAdapter(getContext());

        viewPager = view.findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);

        dotsIndicator(0);

        viewPager.addOnPageChangeListener(listener);

        buttonBack = view.findViewById(R.id.button);
        buttonNext = view.findViewById(R.id.buttonNext);

        buttonBack.setEnabled(false);
        buttonBack.setVisibility(View.GONE);
        buttonNext.setEnabled(true);
        buttonNext.setText(R.string.next);

        buttonNext.setOnClickListener(v -> {
            if (buttonNext.getText().toString().equals(getResources().getString(R.string.finish))) {
                FragmentWelcome fragmentWelcome = new FragmentWelcome();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.layoutMain, fragmentWelcome, "FragmentWelcome")
                        .addToBackStack(null)
                        .commit();
            } else
                viewPager.setCurrentItem(currentPage + 1);
        });

        buttonBack.setOnClickListener(v -> viewPager.setCurrentItem(currentPage - 1));

        return view;
    }

    public void dotsIndicator(int position) {
        pagerIndicator = view.findViewById(R.id.pagerIndicator);
        pagerIndicator.removeAllViews();

        dots = new TextView[3];

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(getContext());
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.light_gray));

            pagerIndicator.addView(dots[i]);
        }
        if (dots.length > 0)
            dots[position].setTextColor(getResources().getColor(R.color.colorAccent));
    }

    ViewPager.OnPageChangeListener listener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i) {
            buttonBack = view.findViewById(R.id.button);
            buttonNext = view.findViewById(R.id.buttonNext);

            dotsIndicator(i);

            currentPage = i;

            if (i == 0) {
                buttonBack.setEnabled(false);
                buttonBack.setVisibility(View.GONE);
                buttonNext.setEnabled(true);
                buttonNext.setText(R.string.next);
            } else if (i == dots.length - 1) {
                buttonBack.setEnabled(true);
                buttonBack.setVisibility(View.VISIBLE);
                buttonBack.setText(R.string.back);
                buttonNext.setEnabled(true);
                buttonNext.setText(R.string.finish);

            } else {
                buttonBack.setEnabled(true);
                buttonBack.setVisibility(View.VISIBLE);
                buttonBack.setText(R.string.back);
                buttonNext.setEnabled(true);
                buttonNext.setText(R.string.next);
            }
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };
}