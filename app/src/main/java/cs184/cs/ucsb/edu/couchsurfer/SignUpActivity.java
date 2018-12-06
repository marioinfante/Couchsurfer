package cs184.cs.ucsb.edu.couchsurfer;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private ProgressBar progressBar;
    private EditText emailText, passText, phoneNoText, fullNameText, usernameText, cityText, stateText;

    private FirebaseAuth mAuth;

    //private static HitchDatabase db = new HitchDatabase();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        emailText = (EditText) findViewById(R.id.emailText);
        passText = (EditText) findViewById(R.id.passText);
        fullNameText = (EditText) findViewById(R.id.fullNameText);
        usernameText = (EditText) findViewById(R.id.usernameText);
        phoneNoText = (EditText) findViewById(R.id.phoneNoText);
        cityText = (EditText) findViewById(R.id.cityText);
        stateText = (EditText) findViewById(R.id.stateText);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.signUpButton).setOnClickListener(this);
        findViewById(R.id.signinText).setOnClickListener(this);
    }

    private void registerUser() {
        String email = emailText.getText().toString().trim();
        String password = passText.getText().toString().trim();

        if (email.isEmpty()) {
            emailText.setError("Email is required.");
            emailText.requestFocus();
            return;
        }
        //if email entered is not valid
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("Please enter a valid email.");
            emailText.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            passText.setError("Password is required.");
            passText.requestFocus();
            return;
        }
        //min password length for firebase auth = 6
        if (password.length() < 6) {
            passText.setError("Minimum length of password is 6.");
            passText.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "User Registered Successfully", Toast.LENGTH_SHORT).show();

                    FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
                    String username = usernameText.getText().toString().trim();
                    String fullName = fullNameText.getText().toString().trim();
                    String city = cityText.getText().toString().trim();
                    String state = stateText.getText().toString().trim();
                    String phoneNo = phoneNoText.getText().toString().trim();
                    User user = new User(fbUser.getUid(), username, fullName, phoneNo, city, state);
                    //db.addUser(user);
                    Intent logInIntent = new Intent(getApplicationContext(), LogInActivity.class);
                    startActivity(logInIntent);
                }
                else {
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(getApplicationContext(), "This email is already registered", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.signUpButton:
                registerUser();
                break;
            case R.id.signinText:
                startActivity(new Intent(this, LogInActivity.class));
                break;
        }
    }


}
