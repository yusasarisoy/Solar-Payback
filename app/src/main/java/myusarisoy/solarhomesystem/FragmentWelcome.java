package myusarisoy.solarhomesystem;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDialog;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

import butterknife.BindView;

public class FragmentWelcome extends Fragment {
    @BindView(R.id.image_language)
    ImageView language;

    @BindView(R.id.button_create_account)
    Button button_create_account;

    @BindView(R.id.tv_login)
    TextView tv_login;

    AppCompatDialog languageDialog;
    private ImageView img_english, img_german, img_turkish;
    View view;

    public static FragmentWelcome newInstance(Object... objects) {
        FragmentWelcome fragment = new FragmentWelcome();
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
        view = inflater.inflate(R.layout.fragment_welcome, container, false);

//        Change language.
        loadLocale();
        languageClick();

//        Create an account.
        createAccount();

//        Login.
        login();

        return view;
    }

    private void languageClick() {
        language = view.findViewById(R.id.image_language);

        language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v7.app.AlertDialog.Builder reservationBuilder = new android.support.v7.app.AlertDialog.Builder(getContext());
                reservationBuilder.setView(R.layout.pop_up_language);
                languageDialog = reservationBuilder.create();
                WindowManager.LayoutParams params = languageDialog.getWindow().getAttributes();
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int width = displayMetrics.widthPixels;
                int height = displayMetrics.heightPixels;
                params.width = (int) (width * 0.8);
                params.height = (int) (height * 0.8);
                languageDialog.getWindow().setAttributes(params);
                languageDialog.show();

                img_english = languageDialog.findViewById(R.id.img_english);
                img_german = languageDialog.findViewById(R.id.img_german);
                img_turkish = languageDialog.findViewById(R.id.img_turkish);

                img_english.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        languageDialog.dismiss();
                        setLocale("en");
                        getActivity().recreate();
                    }
                });

                img_german.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        languageDialog.dismiss();
                        setLocale("de");
                        getActivity().recreate();
                    }
                });

                img_turkish.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        languageDialog.dismiss();
                        setLocale("tr");
                        getActivity().recreate();
                    }
                });
            }
        });
    }

    private void setLocale(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getContext().getResources().updateConfiguration(configuration, getContext().getResources().getDisplayMetrics());

        SharedPreferences.Editor editor = getContext().getSharedPreferences("Settings", Context.MODE_PRIVATE).edit();
        editor.putString("language", language);
        editor.apply();
    }

    public void loadLocale() {
        SharedPreferences preferences = getContext().getSharedPreferences("Settings", getActivity().MODE_PRIVATE);
        String language = preferences.getString("language", "");
        setLocale(language);
    }

    public void createAccount() {
        button_create_account = view.findViewById(R.id.button_create_account);
        button_create_account.setOnClickListener(v -> {
            FragmentRegister fragmentRegister = new FragmentRegister();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.layout_main, fragmentRegister, "FragmentRegister")
                    .addToBackStack(null)
                    .commit();
        });
    }

    public void login() {
        tv_login = view.findViewById(R.id.tv_login);

        tv_login.setOnClickListener(v -> {
            FragmentLogin fragmentLogin = new FragmentLogin();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.layout_main, fragmentLogin, "FragmentLogin")
                    .addToBackStack(null)
                    .commit();
        });
    }
}