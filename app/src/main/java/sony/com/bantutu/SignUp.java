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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity implements View.OnClickListener {
    private EditText editTextEmail, editTextPassword, editTextConfirmPassword, editTextName;
    private Button buttonRegister;
    private TextView textViewSignIn;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();
        if(firebaseAuth.getCurrentUser() != null) {
            goToMain();
        }

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextConfirmPassword = (EditText) findViewById(R.id.editTextConfirmPassword);
        editTextName = (EditText) findViewById(R.id.editTextName);
        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        textViewSignIn = (TextView) findViewById(R.id.textViewSignIn);
        progressDialog = new ProgressDialog(this);

        buttonRegister.setOnClickListener(this);
        textViewSignIn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == buttonRegister) {
            if(editTextPassword.getText().toString().trim().equals(editTextConfirmPassword.getText().toString().trim())) {
                if(isEmailValid(editTextEmail.getText().toString().trim())) {
                    registerUser();
                } else {
                    Toast.makeText(this, "Please enter valid e-mail address!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Password not match!", Toast.LENGTH_SHORT).show();
            }

        }
        if (view == textViewSignIn) {
            signInUser();
        }
    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter e-mail!", Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter password!", Toast.LENGTH_LONG).show();
            return;
        }

        progressDialog.setMessage("Registering...");
        progressDialog.show();
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            progressDialog.dismiss();
                            Toast.makeText(SignUp.this, "Successfully registered!", Toast.LENGTH_SHORT).show();
                            writeNewUser(firebaseAuth.getCurrentUser().getUid(), editTextName.getText().toString().trim());
                            goToMain();
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(SignUp.this, "Fail to register!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void writeNewUser(String userId, String name) {
        database.child("users").child(userId).child("name").setValue(name);
        database.child("users").child(userId).child("institution").setValue("");
        database.child("users").child(userId).child("points").setValue("0");
        database.child("users").child(userId).child("bestAnswers").setValue("0");
    }

    private void signInUser() {
        Intent intent = new Intent(this, SignIn.class);
        startActivity(intent);
    }

    private void goToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }
}
