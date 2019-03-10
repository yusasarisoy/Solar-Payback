package myusarisoy.solarhomesystem;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.Objects;

import butterknife.BindView;

public class FragmentGridChoice extends Fragment {
    @BindView(R.id.img_on_grid)
    ImageView on_grid;

    @BindView(R.id.img_off_grid)
    ImageView off_grid;

    View view;

    public static FragmentGridChoice newInstance(Objects... objects) {
        FragmentGridChoice fragment = new FragmentGridChoice();
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
        view = inflater.inflate(R.layout.fragment_grid_choice, container, false);

//        Grid choices.
//        gridChoice();

        return view;
    }

    private void gridChoice() {
        on_grid = view.findViewById(R.id.img_on_grid);
        off_grid = view.findViewById(R.id.img_off_grid);

        on_grid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentAppliances fragmentAppliances = new FragmentAppliances();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.layout_main, fragmentAppliances, "FragmentAppliances")
                        .commit();
            }
        });

        off_grid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentAppliances fragmentAppliances = new FragmentAppliances();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.layout_main, fragmentAppliances, "FragmentAppliances")
                        .commit();
            }
        });
    }
}