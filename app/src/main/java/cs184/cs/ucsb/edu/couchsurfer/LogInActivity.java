package cs184.cs.ucsb.edu.couchsurfer;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.util.Patterns;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LogInActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private EditText emailText, passText;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        emailText = (EditText) findViewById(R.id.usernameText);
        passText = (EditText) findViewById(R.id.passwordText);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.signupTextView).setOnClickListener(this);
        findViewById(R.id.signInButton).setOnClickListener(this);
    }

    private void userLogin() {

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
            passText.setError("Minimum length if password is 6.");
            passText.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.VISIBLE);
                if (task.isSuccessful()) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                    Log.e("TAG", "successful login");
                }
                else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.signupTextView:
                startActivity(new Intent(this, SignUpActivity.class));
                break;
            case R.id.signInButton:
                userLogin();
                break;
        }
    }
}
