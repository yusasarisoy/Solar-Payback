package myusarisoy.solarhomesystem.Fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import butterknife.BindView;
import myusarisoy.solarhomesystem.R;

public class FragmentLogin extends Fragment {
    @BindView(R.id.layoutLogin)
    LinearLayout layoutLogin;

    @BindView(R.id.imgGoBack)
    ImageView goBack;

    @BindView(R.id.etLoginMail)
    EditText loginMail;

    @BindView(R.id.etLoginPassword)
    EditText loginPassword;

    @BindView(R.id.showLoginPassword)
    ImageView showPassword;

    @BindView(R.id.tvForgotPassword)
    TextView forgotPassword;

    @BindView(R.id.buttonLogin)
    Button buttonLogin;

    boolean isLoginPasswordVisible = false;
    FirebaseAuth firebaseAuth;
    String mail, password;
    View view;

    public static FragmentLogin newInstance(Objects... objects) {
        FragmentLogin fragment = new FragmentLogin();
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
        view = inflater.inflate(R.layout.fragment_login, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            FragmentExperience fragmentExperience = new FragmentExperience();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_left_to_right, R.anim.exit_left_to_right, R.anim.enter_right_to_left, R.anim.exit_left_to_right)
                    .replace(R.id.layoutMain, fragmentExperience, "FragmentExperience")
                    .addToBackStack(null)
                    .commit();
        }

//        Go to previous fragment.
        goBack();

//        Show password.
        showLoginPassword();

//        Go to FragmentNotes.
        login();

//        Go to FragmentForgotPassword.
        forgotPassword();

        return view;
    }

    private void goBack() {
        goBack = view.findViewById(R.id.imgGoBack);
        goBack.setOnClickListener(v -> getFragmentManager().popBackStackImmediate());
    }

    private void showLoginPassword() {
        loginPassword = view.findViewById(R.id.etLoginPassword);
        showPassword = view.findViewById(R.id.showLoginPassword);

        showPassword.setOnClickListener(v -> {
            if (!isLoginPasswordVisible) {
                isLoginPasswordVisible = true;
                showPassword.setImageDrawable(getResources().getDrawable(R.drawable.eye));
                loginPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                loginPassword.setSelection(loginPassword.length());
            } else {
                isLoginPasswordVisible = false;
                showPassword.setImageDrawable(getResources().getDrawable(R.drawable.eye_active));
                loginPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                loginPassword.setSelection(loginPassword.length());
            }

        });
    }

    private void forgotPassword() {
        forgotPassword = view.findViewById(R.id.tvForgotPassword);

        forgotPassword.setOnClickListener(v -> {
            FragmentForgotPassword fragmentForgotPassword = new FragmentForgotPassword();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_left_to_right, R.anim.exit_left_to_right, R.anim.enter_right_to_left, R.anim.exit_left_to_right)
                    .replace(R.id.layoutMain, fragmentForgotPassword, "FragmentForgotPassword")
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void login() {
        loginMail = view.findViewById(R.id.etLoginMail);
        loginPassword = view.findViewById(R.id.etLoginPassword);
        buttonLogin = view.findViewById(R.id.buttonLogin);

        buttonLogin.setOnClickListener(v -> {
            final ProgressDialog progressDialog = ProgressDialog.show(getContext(),
                    getResources().getString(R.string.login), getResources().getString(R.string.pleaseWait), true, true);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);

            mail = loginMail.getText().toString();
            password = loginPassword.getText().toString();

            if (TextUtils.isEmpty(mail)) {
                progressDialog.dismiss();
                showSnackbar(getResources().getString(R.string.enterEmail));
                return;
            }

            if (TextUtils.isEmpty(password)) {
                progressDialog.dismiss();
                showSnackbar(getResources().getString(R.string.enterPassword));
                return;
            }

            //authenticate user
            firebaseAuth.signInWithEmailAndPassword(mail, password).addOnCompleteListener(task -> {
                progressDialog.dismiss();
                // If sign in fails, display a message to the user. If sign in succeeds
                // the auth state listener will be notified and logic to handle the
                // signed in user can be handled in the listener.
                if (!task.isSuccessful()) {
                    // there was an error
                    if (password.length() < 6)
                        showSnackbar(getResources().getString(R.string.sixDigits));
                    else
                        showSnackbar(getResources().getString(R.string.wrongPassword));
                } else {
                    FragmentExperience fragmentExperience = new FragmentExperience();
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.enter_left_to_right, R.anim.exit_left_to_right, R.anim.enter_right_to_left, R.anim.exit_left_to_right)
                            .replace(R.id.layoutMain, fragmentExperience, "FragmentExperience")
                            .addToBackStack(null)
                            .commit();
                }
            });
        });
    }

    public void showSnackbar(String text) {
        layoutLogin = view.findViewById(R.id.layoutLogin);

        Snackbar snackbar = Snackbar.make(layoutLogin, text, Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(getResources().getColor(R.color.backgroundColor));
        TextView textView = snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(getResources().getColor(R.color.colorPrimary));
        snackbar.show();
    }
}