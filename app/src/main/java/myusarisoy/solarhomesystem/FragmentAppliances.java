package myusarisoy.solarhomesystem;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;

public class FragmentAppliances extends Fragment {
    @BindView(R.id.recycler_view_appliance)
    RecyclerView recyclerView;

    @BindView(R.id.button_continue)
    Button button_continue;

    private RecyclerViewApplianceAdapter adapter;
    private ArrayList<SelectedAppliance> selectedAppliancesList = new ArrayList<>();
    private ArrayList<String> arrayListName = new ArrayList<>();
    private ArrayList<Integer> arrayListImage = new ArrayList<>();
    View view;

    public static FragmentAppliances newInstance(Object... objects) {
        FragmentAppliances fragment = new FragmentAppliances();
        Bundle args = new Bundle();
        args.putStringArrayList("AppliancesName", (ArrayList<String>) objects[0]);
        args.putIntegerArrayList("AppliancesImage", (ArrayList<Integer>) objects[1]);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_appliances, container, false);

//        Get list of appliances.
        arrayListName = getArguments().getStringArrayList("AppliancesName");
        arrayListImage = getArguments().getIntegerArrayList("AppliancesImage");

//        Set adapter for Recycler View.
        setAdapter();

//        Show list of appliances.
        initSelectedAppliances();

//        Go to grid choice.
        gotoGridChoice();

        return view;
    }

    private void setAdapter() {
        recyclerView = view.findViewById(R.id.recycler_view_appliance);

        adapter = new RecyclerViewApplianceAdapter(selectedAppliancesList, getContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.item_divider));
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    public void initSelectedAppliances() {
        recyclerView = view.findViewById(R.id.recycler_view_appliance);

        for (int i = 0; i < arrayListName.size(); i++) {
            SelectedAppliance selectedAppliance = new SelectedAppliance(arrayListImage.get(i), arrayListName.get(i));
            selectedAppliancesList.add(selectedAppliance);
        }

        adapter.notifyDataSetChanged();
    }

    private void gotoGridChoice() {
        button_continue = view.findViewById(R.id.button_continue);

        button_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentGridChoice fragmentGridChoice = new FragmentGridChoice();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.layout_main, fragmentGridChoice, "FragmentGridChoice")
                        .commit();
            }
        });
    }
}