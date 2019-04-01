package myusarisoy.solarhomesystem;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import butterknife.BindView;

public class FragmentPanels extends Fragment {
    @BindView(R.id.layout_panels)
    LinearLayout layout_panels;

    @BindView(R.id.img_panel_1)
    ImageView panel_1;

    @BindView(R.id.img_panel_2)
    ImageView panel_2;

    View view;

    public static FragmentPanels newInstance(Object... objects) {
        FragmentPanels fragment = new FragmentPanels();
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
        view = inflater.inflate(R.layout.fragment_panels, container, false);

//        Go to panel calculation with click.
        clickPanels();

        return view;
    }

    private void clickPanels() {
        panel_1 = view.findViewById(R.id.img_panel_1);
        panel_2 = view.findViewById(R.id.img_panel_2);

        panel_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoPanelCalculation();
            }
        });

        panel_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoPanelCalculation();
            }
        });
    }

    private void gotoPanelCalculation() {
        FragmentPanelCalculation fragmentPanelCalculation = new FragmentPanelCalculation();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.layout_main, fragmentPanelCalculation, "FragmentPanelCalculation")
                .commit();
    }
}