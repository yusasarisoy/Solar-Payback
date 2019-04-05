package myusarisoy.solarhomesystem;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

import butterknife.BindView;

public class FragmentAppliances extends Fragment {
    @BindView(R.id.recycler_view_appliance)
    RecyclerView recyclerView;

    @BindView(R.id.button_back)
    Button button_back;

    @BindView(R.id.button_continue)
    Button button_continue;

    private Button cancel_back, confirm_back;
    private AppCompatDialog backDialog;
    private CountDownTimer countDownTimer;
    private RecyclerViewApplianceAdapter adapter;
    private ArrayList<SelectedAppliance> selectedAppliancesList = new ArrayList<>();
    private ArrayList<Integer> consumptionList = new ArrayList<>();
    private ArrayList<String> arrayListName = new ArrayList<>();
    private ArrayList<Integer> arrayListImage = new ArrayList<>();
    public String cityLocation;
    public double irradianceLocation;
    View view;

    public static FragmentAppliances newInstance(Object... objects) {
        FragmentAppliances fragment = new FragmentAppliances();
        Bundle args = new Bundle();
        args.putStringArrayList("AppliancesName", (ArrayList<String>) objects[0]);
        args.putIntegerArrayList("AppliancesImage", (ArrayList<Integer>) objects[1]);
        args.putString("city", (String) objects[2]);
        args.putDouble("irradiance", (Double) objects[3]);
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

//        Get city and its solar irradiance data.
        cityLocation = getArguments().getString("City");
        irradianceLocation = getArguments().getDouble("CityIrradiance");

//        Set adapter for Recycler View.
        setAdapter();

//        Show list of appliances.
        initSelectedAppliances();

//        Go to grid choice.
        gotoGridChoice();

//        Click to go previous page.
        clickToBack();

        return view;
    }

    private void clickToBack() {
        button_back = view.findViewById(R.id.button_back);
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v7.app.AlertDialog.Builder reservationBuilder = new android.support.v7.app.AlertDialog.Builder(getContext());
                reservationBuilder.setView(R.layout.dialog_back);
                backDialog = reservationBuilder.create();
                WindowManager.LayoutParams params = backDialog.getWindow().getAttributes();
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int width = displayMetrics.widthPixels;
                int height = displayMetrics.heightPixels;
                params.width = (int) (width * 0.8);
                params.height = (int) (height * 0.8);
                backDialog.getWindow().setAttributes(params);
                backDialog.show();

                cancel_back = backDialog.findViewById(R.id.cancel_back);
                confirm_back = backDialog.findViewById(R.id.confirm_back);

                cancel_back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        backDialog.dismiss();
                    }
                });

                confirm_back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        backDialog.dismiss();

                        countDownTimer = new CountDownTimer(500, 250) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                            }

                            @Override
                            public void onFinish() {
                                countDownTimer.cancel();

                                FragmentMain fragmentMain = new FragmentMain();
                                getActivity().getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.layout_main, fragmentMain, "FragmentMain")
                                        .commit();
                            }
                        }.start();
                    }
                });
            }
        });
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

        consumptionList = adapter.getData();

        button_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentGridChoice fragmentGridChoice = new FragmentGridChoice();
                Bundle bundle = new Bundle();
                bundle.putString("choice", "appliance");
                bundle.putStringArrayList("AppliancesName", arrayListName);
                bundle.putIntegerArrayList("AppliancesImage", arrayListImage);
                bundle.putIntegerArrayList("AppliancesConsumption", consumptionList);
                bundle.putString("City", cityLocation);
                bundle.putDouble("CityIrradiance", irradianceLocation);
                fragmentGridChoice.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.layout_main, fragmentGridChoice, "FragmentGridChoice")
                        .commit();
            }
        });
    }
}