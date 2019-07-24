package myusarisoy.solarhomesystem.Fragment;

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
import myusarisoy.solarhomesystem.R;
import myusarisoy.solarhomesystem.ThemeSelector.SharedPreferencesTheme;

public class FragmentWelcome extends Fragment {
    @BindView(R.id.imageLanguage)
    ImageView language;

    @BindView(R.id.imageTheme)
    ImageView imageTheme;

    @BindView(R.id.buttonCreateAccount)
    Button buttonCreateAccount;

    @BindView(R.id.tvLogin)
    TextView tvLogin;

    AppCompatDialog languageDialog, themeDialog;
    private ImageView imgEnglish, imgGerman, imgTurkish, imgLight, imgDark;
    SharedPreferencesTheme sharedPreferencesTheme;
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

        sharedPreferencesTheme = new SharedPreferencesTheme(getContext());

        if (sharedPreferencesTheme.loadNightModeState())
            getActivity().setTheme(R.style.DarkTheme);
        else if (sharedPreferencesTheme.loadLightModeState())
            getActivity().setTheme(R.style.AppTheme);

//        Change theme.
        changeTheme();

//        Change language.
        loadLocale();
        languageClick();

//        Create an account.
        createAccount();

//        Login.
        login();

        return view;
    }

    private void changeTheme() {
        imageTheme = view.findViewById(R.id.imageTheme);
        imageTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v7.app.AlertDialog.Builder reservationBuilder = new android.support.v7.app.AlertDialog.Builder(getContext());
                reservationBuilder.setView(R.layout.dialog_theme);
                themeDialog = reservationBuilder.create();
                WindowManager.LayoutParams params = themeDialog.getWindow().getAttributes();
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int width = displayMetrics.widthPixels;
                int height = displayMetrics.heightPixels;
                params.width = (int) (width * 0.8);
                params.height = (int) (height * 0.8);
                themeDialog.getWindow().setAttributes(params);
                themeDialog.show();

                imgLight = themeDialog.findViewById(R.id.imgLight);
                imgDark = themeDialog.findViewById(R.id.imgDark);

                imgLight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        themeDialog.dismiss();
                        sharedPreferencesTheme.setLightModeState(true);
                        sharedPreferencesTheme.setNightModeState(false);
                        getActivity().recreate();
                    }
                });

                imgDark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        themeDialog.dismiss();
                        sharedPreferencesTheme.setLightModeState(false);
                        sharedPreferencesTheme.setNightModeState(true);
                        getActivity().recreate();
                    }
                });
            }
        });
    }

    private void languageClick() {
        language = view.findViewById(R.id.imageLanguage);

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

                imgEnglish = languageDialog.findViewById(R.id.imgEnglish);
//                imgGerman = languageDialog.findViewById(R.id.imgGerman);
                imgTurkish = languageDialog.findViewById(R.id.imgTurkish);

                imgEnglish.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        languageDialog.dismiss();
                        setLocale("en");
                        getActivity().recreate();
                    }
                });

//                imgGerman.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        languageDialog.dismiss();
//                        setLocale("de");
//                        getActivity().recreate();
//                    }
//                });

                imgTurkish.setOnClickListener(new View.OnClickListener() {
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
        buttonCreateAccount = view.findViewById(R.id.buttonCreateAccount);
        buttonCreateAccount.setOnClickListener(v -> {
            FragmentRegister fragmentRegister = new FragmentRegister();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_left_to_right, R.anim.exit_left_to_right, R.anim.enter_right_to_left, R.anim.exit_left_to_right)
                    .replace(R.id.layoutMain, fragmentRegister, "FragmentRegister")
                    .addToBackStack(null)
                    .commit();
        });
    }

    public void login() {
        tvLogin = view.findViewById(R.id.tvLogin);

        tvLogin.setOnClickListener(v -> {
            FragmentLogin fragmentLogin = new FragmentLogin();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_left_to_right, R.anim.exit_left_to_right, R.anim.enter_right_to_left, R.anim.exit_left_to_right)
                    .replace(R.id.layoutMain, fragmentLogin, "FragmentLogin")
                    .addToBackStack(null)
                    .commit();
        });
    }
}