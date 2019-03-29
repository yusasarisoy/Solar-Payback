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


        return view;
    }
}