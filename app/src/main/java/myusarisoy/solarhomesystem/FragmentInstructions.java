package myusarisoy.solarhomesystem;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import butterknife.BindView;

public class FragmentInstructions extends Fragment {
    @BindView(R.id.view_pager)
    ViewPager viewPager;

    @BindView(R.id.pager_indicator)
    LinearLayout pager_indicator;

    @BindView(R.id.button)
    Button button_back;

    @BindView(R.id.button_next)
    Button button_next;

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
                    .replace(R.id.layout_main, fragmentExperience, "FragmentExperience")
                    .addToBackStack(null)
                    .commit();
        }

        adapter = new InstructionsAdapter(getContext());

        viewPager = view.findViewById(R.id.view_pager);
        viewPager.setAdapter(adapter);

        dotsIndicator(0);

        viewPager.addOnPageChangeListener(listener);

        button_back = view.findViewById(R.id.button);
        button_next = view.findViewById(R.id.button_next);

        button_back.setEnabled(false);
        button_back.setVisibility(View.GONE);
        button_next.setEnabled(true);
        button_next.setText(R.string.next);

        button_next.setOnClickListener(v -> {
            if (button_next.getText().toString().equals(getResources().getString(R.string.finish))) {
                FragmentWelcome fragmentWelcome = new FragmentWelcome();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.layout_main, fragmentWelcome, "FragmentWelcome")
                        .addToBackStack(null)
                        .commit();
            } else
                viewPager.setCurrentItem(currentPage + 1);
        });

        button_back.setOnClickListener(v -> viewPager.setCurrentItem(currentPage - 1));

        return view;
    }

    public void dotsIndicator(int position) {
        pager_indicator = view.findViewById(R.id.pager_indicator);
        pager_indicator.removeAllViews();

        dots = new TextView[3];

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(getContext());
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.light_gray));

            pager_indicator.addView(dots[i]);
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
            button_back = view.findViewById(R.id.button);
            button_next = view.findViewById(R.id.button_next);

            dotsIndicator(i);

            currentPage = i;

            if (i == 0) {
                button_back.setEnabled(false);
                button_back.setVisibility(View.GONE);
                button_next.setEnabled(true);
                button_next.setText(R.string.next);
            } else if (i == dots.length - 1) {
                button_back.setEnabled(true);
                button_back.setVisibility(View.VISIBLE);
                button_back.setText(R.string.back);
                button_next.setEnabled(true);
                button_next.setText(R.string.finish);

            } else {
                button_back.setEnabled(true);
                button_back.setVisibility(View.VISIBLE);
                button_back.setText(R.string.back);
                button_next.setEnabled(true);
                button_next.setText(R.string.next);
            }
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };
}