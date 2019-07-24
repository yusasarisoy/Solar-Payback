package myusarisoy.solarhomesystem.Fragment;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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

public class FragmentForgotPassword extends Fragment {
    @BindView(R.id.layoutForgotPassword)
    LinearLayout layoutForgotPassword;

    @BindView(R.id.imgGoBack)
    ImageView goBack;

    @BindView(R.id.etForgotPasswordMail)
    EditText mail;

    @BindView(R.id.buttonForgotPasswordReset)
    Button resetPassword;

    private FirebaseAuth firebaseAuth;
    View root;

    public static FragmentForgotPassword newInstance(Objects... objects) {
        FragmentForgotPassword fragment = new FragmentForgotPassword();
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
        root = inflater.inflate(R.layout.fragment_forgot_password, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

//        Go to previous fragment.
        goBack();

//        Reset password.
        reset_password();

        return root;
    }

    private void goBack() {
        goBack = root.findViewById(R.id.imgGoBack);
        goBack.setOnClickListener(v -> getFragmentManager().popBackStackImmediate());
    }

    private void reset_password() {
        mail = root.findViewById(R.id.etForgotPasswordMail);
        resetPassword = root.findViewById(R.id.buttonForgotPasswordReset);

        resetPassword.setOnClickListener(v -> {
            String email = mail.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                showSnackbar(getResources().getString(R.string.registeredEmail));
                return;
            }

            firebaseAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            showSnackbar(getResources().getString(R.string.resetYourPassword));

                            FragmentWelcome fragmentWelcome = new FragmentWelcome();
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.anim.enter_left_to_right, R.anim.exit_left_to_right, R.anim.enter_right_to_left, R.anim.exit_left_to_right)
                                    .replace(R.id.layoutMain, fragmentWelcome, "FragmentWelcome")
                                    .addToBackStack(null)
                                    .commit();
                        } else
                            showSnackbar(getResources().getString(R.string.failedToResetPassword));
                    });
        });
    }

    public void showSnackbar(String text) {
        layoutForgotPassword = root.findViewById(R.id.layoutForgotPassword);

        Snackbar snackbar = Snackbar.make(layoutForgotPassword, text, Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(getResources().getColor(R.color.backgroundColor));
        TextView textView = snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(getResources().getColor(R.color.colorPrimary));
        snackbar.show();
    }
}