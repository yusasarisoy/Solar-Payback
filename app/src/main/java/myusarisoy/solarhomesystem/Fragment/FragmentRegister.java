package myusarisoy.solarhomesystem.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;

import butterknife.BindView;
import myusarisoy.solarhomesystem.R;

public class FragmentRegister extends Fragment {
    @BindView(R.id.layoutRegister)
    LinearLayout layoutRegister;

    @BindView(R.id.imgGoBack)
    ImageView imgGoBack;

    @BindView(R.id.etName)
    EditText etName;

    @BindView(R.id.etMail)
    EditText etMail;

    @BindView(R.id.etPassword)
    EditText etPassword;

    @BindView(R.id.showPassword)
    ImageView showPassword;

    @BindView(R.id.buttonContinue)
    Button next;

    boolean isPasswordVisible = false;
    FirebaseAuth firebaseAuth;
    String name, mail, password;
    View view;

    public static FragmentRegister newInstance(Objects... objects) {
        FragmentRegister fragment = new FragmentRegister();
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
        view = inflater.inflate(R.layout.fragment_register, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

//        Go to previous fragment.
        goBack();

//        Show password.
        showPassword();

//        Register to the application.
        register();

        return view;
    }

    private void goBack() {
        imgGoBack = view.findViewById(R.id.imgGoBack);
        imgGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStackImmediate();
            }
        });
    }

    private void showPassword() {
        etPassword = view.findViewById(R.id.etPassword);
        showPassword = view.findViewById(R.id.showPassword);

        showPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPasswordVisible) {
                    isPasswordVisible = true;
                    showPassword.setImageDrawable(getResources().getDrawable(R.drawable.eye));
                    etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    etPassword.setSelection(etPassword.length());
                } else {
                    isPasswordVisible = false;
                    showPassword.setImageDrawable(getResources().getDrawable(R.drawable.eye_active));
                    etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    etPassword.setSelection(etPassword.length());
                }

            }
        });
    }

    private void register() {
        etName = view.findViewById(R.id.etName);
        etMail = view.findViewById(R.id.etMail);
        etPassword = view.findViewById(R.id.etPassword);
        next = view.findViewById(R.id.buttonContinue);

        next.setOnClickListener(v -> {
            name = etName.getText().toString().trim();
            mail = etMail.getText().toString().trim();
            password = etPassword.getText().toString().trim();

            if (TextUtils.isEmpty(name)) {
                showSnackbar(getResources().getString(R.string.enterFullName));
                return;
            }
            if (TextUtils.isEmpty(mail)) {
                showSnackbar(getResources().getString(R.string.enterYourMail));
                return;
            }
            if (TextUtils.isEmpty(password)) {
                showSnackbar(getResources().getString(R.string.enterPassword));
                return;
            }
            if (password.length() < 6) {
                showSnackbar(getResources().getString(R.string.sixDigits));
                return;
            }

            firebaseAuth.createUserWithEmailAndPassword(mail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Log.i("REGISTER", "Register action is successful.");
                    if (!task.isSuccessful()) {
                        Log.i("ERROR", "Authentication failed: " + task.getException());
                        showSnackbar(getResources().getString(R.string.authenticationFailed));
                    } else {
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(name).build();

                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        user.updateProfile(profileUpdates);

                        FragmentExperience fragmentExperience= new FragmentExperience();
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.enter_left_to_right, R.anim.exit_left_to_right, R.anim.enter_right_to_left, R.anim.exit_left_to_right)
                                .replace(R.id.layoutMain, fragmentExperience, "FragmentExperience")
                                .addToBackStack(null)
                                .commit();
                    }
                }
            });
        });
    }

    public void showSnackbar(String text) {
        layoutRegister = view.findViewById(R.id.layoutRegister);

        Snackbar snackbar = Snackbar.make(layoutRegister, text, Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(getResources().getColor(R.color.backgroundColor));
        TextView textView = snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(getResources().getColor(R.color.colorPrimary));
        snackbar.show();
    }
}
