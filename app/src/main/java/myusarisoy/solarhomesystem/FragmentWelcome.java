package myusarisoy.solarhomesystem;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

import butterknife.BindView;

public class FragmentWelcome extends Fragment {
    @BindView(R.id.image_language_english)
    ImageView languageEnglish;

    @BindView(R.id.image_language_turkish)
    ImageView languageTurkish;

    @BindView(R.id.button_create_account)
    Button button_create_account;

    @BindView(R.id.tv_login)
    TextView tv_login;

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
        languageEnglish = view.findViewById(R.id.image_language_english);
        languageTurkish = view.findViewById(R.id.image_language_turkish);

        languageEnglish.setOnClickListener(v -> {
            setLocale("en");
            getActivity().recreate();
        });

        languageTurkish.setOnClickListener(v -> {
            setLocale("tr");
            getActivity().recreate();
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