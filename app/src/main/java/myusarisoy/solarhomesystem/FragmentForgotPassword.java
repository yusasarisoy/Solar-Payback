package myusarisoy.solarhomesystem;

import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import butterknife.BindView;

public class FragmentForgotPassword extends Fragment {
    @BindView(R.id.layout_forgot_password)
    LinearLayout layout_forgot_password;

    @BindView(R.id.img_go_back)
    ImageView go_back;

    @BindView(R.id.et_forgot_password_mail)
    EditText mail;

    @BindView(R.id.button_forgot_password_reset)
    Button reset_password;

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
        go_back = root.findViewById(R.id.img_go_back);
        go_back.setOnClickListener(v -> getFragmentManager().popBackStackImmediate());
    }

    private void reset_password() {
        mail = root.findViewById(R.id.et_forgot_password_mail);
        reset_password = root.findViewById(R.id.button_forgot_password_reset);

        reset_password.setOnClickListener(v -> {
            String email = mail.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                showSnackbar(getResources().getString(R.string.registered_email));
                return;
            }

            firebaseAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            showSnackbar(getResources().getString(R.string.reset_your_password));

                            FragmentWelcome fragmentWelcome = new FragmentWelcome();
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.layout_main, fragmentWelcome, "FragmentWelcome")
                                    .addToBackStack(null)
                                    .commit();
                        } else
                            showSnackbar(getResources().getString(R.string.failed_to_reset_passowrd));
                    });
        });
    }

    public void showSnackbar(String text) {
        layout_forgot_password = root.findViewById(R.id.layout_forgot_password);

        Snackbar snackbar = Snackbar.make(layout_forgot_password, text, Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(getResources().getColor(R.color.cardBackgroundColor));
        TextView textView = snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(getResources().getColor(R.color.colorPrimary));
        snackbar.show();
    }
}