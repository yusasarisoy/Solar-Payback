package myusarisoy.solarhomesystem;

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

public class FragmentLogin extends Fragment {
    @BindView(R.id.layout_login)
    LinearLayout layout_login;

    @BindView(R.id.img_go_back)
    ImageView go_back;

    @BindView(R.id.et_login_mail)
    EditText login_mail;

    @BindView(R.id.et_login_password)
    EditText login_password;

    @BindView(R.id.show_login_password)
    ImageView show_password;

    @BindView(R.id.tv_forgot_password)
    TextView forgot_password;

    @BindView(R.id.button_login)
    Button button_login;

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
            FragmentConsumer fragmentConsumer = new FragmentConsumer();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.layout_main, fragmentConsumer, "FragmentConsumer")
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
        go_back = view.findViewById(R.id.img_go_back);
        go_back.setOnClickListener(v -> getFragmentManager().popBackStackImmediate());
    }

    private void showLoginPassword() {
        login_password = view.findViewById(R.id.et_login_password);
        show_password = view.findViewById(R.id.show_login_password);

        show_password.setOnClickListener(v -> {
            if (!isLoginPasswordVisible) {
                isLoginPasswordVisible = true;
                show_password.setImageDrawable(getResources().getDrawable(R.drawable.eye));
                login_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                login_password.setSelection(login_password.length());
            } else {
                isLoginPasswordVisible = false;
                show_password.setImageDrawable(getResources().getDrawable(R.drawable.eye_active));
                login_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                login_password.setSelection(login_password.length());
            }

        });
    }

    private void forgotPassword() {
        forgot_password = view.findViewById(R.id.tv_forgot_password);

        forgot_password.setOnClickListener(v -> {
            FragmentForgotPassword fragmentForgotPassword = new FragmentForgotPassword();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.layout_main, fragmentForgotPassword, "FragmentForgotPassword")
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void login() {
        login_mail = view.findViewById(R.id.et_login_mail);
        login_password = view.findViewById(R.id.et_login_password);
        button_login = view.findViewById(R.id.button_login);

        button_login.setOnClickListener(v -> {
            final ProgressDialog progressDialog = ProgressDialog.show(getContext(), "Login", "Please wait...", true, true);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);

            mail = login_mail.getText().toString();
            password = login_password.getText().toString();

            if (TextUtils.isEmpty(mail)) {
                progressDialog.dismiss();
                showSnackbar("Please enter your email address");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                progressDialog.dismiss();
                showSnackbar("Please enter your password");
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
                        showSnackbar("Password must contains at least 6 characters");
                    else
                        showSnackbar("Wrong password");
                } else {
                    FragmentConsumer fragmentConsumer = new FragmentConsumer();
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.layout_main, fragmentConsumer, "FragmentConsumer")
                            .addToBackStack(null)
                            .commit();
                }
            });
        });
    }

    public void showSnackbar(String text) {
        layout_login = view.findViewById(R.id.layout_login);

        Snackbar snackbar = Snackbar.make(layout_login, text, Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(getResources().getColor(R.color.dark_slate_gray));
        TextView textView = snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(getResources().getColor(R.color.colorPrimary));
        snackbar.show();
    }
}