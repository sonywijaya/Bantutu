package sony.com.bantutu;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignIn extends AppCompatActivity implements View.OnClickListener {
    private EditText editTextLoginEmail, editTextLoginPassword;
    private Button buttonLogin;
    private TextView textViewSignUp;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() != null) {
            goToMain();
        }

        editTextLoginEmail = (EditText) findViewById(R.id.editTextLoginEmail);
        editTextLoginPassword = (EditText) findViewById(R.id.editTextLoginPassword);
        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        textViewSignUp = (TextView) findViewById(R.id.textViewSignUp);
        progressDialog = new ProgressDialog(this);

        buttonLogin.setOnClickListener(this);
        textViewSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == buttonLogin) {
            userLogin();
        }
        if (view == textViewSignUp) {
            signUpUser();
        }
    }

    private void userLogin() {
        String email = editTextLoginEmail.getText().toString().trim();
        String password = editTextLoginPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter e-mail!", Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter password!", Toast.LENGTH_LONG).show();
            return;
        }

        progressDialog.setMessage("Logging in...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()) {
                            goToMain();
                        }
                    }
                });
    }

    private void signUpUser() {
        Intent intent = new Intent(this, SignUp.class);
        startActivity(intent);
    }

    private void goToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
