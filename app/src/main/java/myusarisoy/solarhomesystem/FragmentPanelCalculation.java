package myusarisoy.solarhomesystem;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import butterknife.BindView;

public class FragmentPanelCalculation extends Fragment {
    @BindView(R.id.layout_panel_calculation)
    LinearLayout linearLayout;

    View view;

    public static FragmentPanelCalculation newInstance(String param1, String param2) {
        FragmentPanelCalculation fragment = new FragmentPanelCalculation();
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
        view = inflater.inflate(R.layout.fragment_panel_calculation, container, false);

        return view;
    }
}