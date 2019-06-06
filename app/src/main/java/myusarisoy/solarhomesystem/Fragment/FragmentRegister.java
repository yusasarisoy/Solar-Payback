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
    @BindView(R.id.layout_register)
    LinearLayout layout_register;

    @BindView(R.id.img_go_back)
    ImageView img_go_back;

    @BindView(R.id.et_name)
    EditText et_name;

    @BindView(R.id.et_mail)
    EditText et_mail;

    @BindView(R.id.et_password)
    EditText et_password;

    @BindView(R.id.show_password)
    ImageView show_password;

    @BindView(R.id.button_continue)
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
        img_go_back = view.findViewById(R.id.img_go_back);
        img_go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStackImmediate();
            }
        });
    }

    private void showPassword() {
        et_password = view.findViewById(R.id.et_password);
        show_password = view.findViewById(R.id.show_password);

        show_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPasswordVisible) {
                    isPasswordVisible = true;
                    show_password.setImageDrawable(getResources().getDrawable(R.drawable.eye));
                    et_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    et_password.setSelection(et_password.length());
                } else {
                    isPasswordVisible = false;
                    show_password.setImageDrawable(getResources().getDrawable(R.drawable.eye_active));
                    et_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    et_password.setSelection(et_password.length());
                }

            }
        });
    }

    private void register() {
        et_name = view.findViewById(R.id.et_name);
        et_mail = view.findViewById(R.id.et_mail);
        et_password = view.findViewById(R.id.et_password);
        next = view.findViewById(R.id.button_continue);

        next.setOnClickListener(v -> {
            name = et_name.getText().toString().trim();
            mail = et_mail.getText().toString().trim();
            password = et_password.getText().toString().trim();

            if (TextUtils.isEmpty(name)) {
                showSnackbar(getResources().getString(R.string.enter_full_name));
                return;
            }
            if (TextUtils.isEmpty(mail)) {
                showSnackbar(getResources().getString(R.string.enter_your_mail));
                return;
            }
            if (TextUtils.isEmpty(password)) {
                showSnackbar(getResources().getString(R.string.enter_password));
                return;
            }
            if (password.length() < 6) {
                showSnackbar(getResources().getString(R.string.six_digits));
                return;
            }

            firebaseAuth.createUserWithEmailAndPassword(mail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Log.i("REGISTER", "Register action is successful.");
                    if (!task.isSuccessful()) {
                        Log.i("ERROR", "Authentication failed: " + task.getException());
                        showSnackbar(getResources().getString(R.string.authentication_failed));
                    } else {
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(name).build();

                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        user.updateProfile(profileUpdates);

                        FragmentExperience fragmentExperience= new FragmentExperience();
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.layout_main, fragmentExperience, "FragmentExperience")
                                .addToBackStack(null)
                                .commit();
                    }
                }
            });
        });
    }

    public void showSnackbar(String text) {
        layout_register = view.findViewById(R.id.layout_register);

        Snackbar snackbar = Snackbar.make(layout_register, text, Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(getResources().getColor(R.color.backgroundColor));
        TextView textView = snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(getResources().getColor(R.color.colorPrimary));
        snackbar.show();
    }
}
