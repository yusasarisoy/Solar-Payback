package myusarisoy.solarhomesystem;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;

public class FragmentWelcome extends Fragment {
    @BindView(R.id.image_language)
    ImageView language;

    @BindView(R.id.button_create_account)
    Button button_create_account;

    @BindView(R.id.tv_login)
    TextView tv_login;

    View view;

    public static FragmentWelcome newInstance(Objects... objects) {
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
                String languageToLoad = "tr";
                Locale locale = new Locale(languageToLoad);
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                getContext().getResources().updateConfiguration(config, getContext().getResources().getDisplayMetrics());

                Intent intent = getActivity().getBaseContext().getPackageManager().getLaunchIntentForPackage(getActivity().getBaseContext().getPackageName());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    public void createAccount() {
        button_create_account = view.findViewById(R.id.button_create_account);
        button_create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentRegister fragmentRegister = new FragmentRegister();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.layout_main, fragmentRegister, "FragmentRegister")
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    public void login() {
        tv_login = view.findViewById(R.id.tv_login);

        SpannableString spannableString = new SpannableString("Have an account? Log in");

        ClickableSpan login = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                FragmentLogin fragmentLogin = new FragmentLogin();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.layout_main, fragmentLogin, "FragmentLogin")
                        .addToBackStack(null)
                        .commit();
            }
        };

        spannableString.setSpan(login, 17, 23, 0);
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary)), 17, 23, 0);
        setClickableSpan(tv_login, R.id.tv_login, spannableString);
    }

    public void setClickableSpan(TextView textView, int tvId, SpannableString spannableString) {
        textView = view.findViewById(tvId);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setText(spannableString, TextView.BufferType.SPANNABLE);
        textView.setSelected(true);
    }
}