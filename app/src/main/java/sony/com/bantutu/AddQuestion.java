package sony.com.bantutu;

import android.content.Intent;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

public class AddQuestion extends AppCompatActivity {
    private EditText editTitle, editDescription;
    private Button buttonubmit;
    private String title, description, subject;
    private MaterialBetterSpinner spinnerSubject;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseQuestion;
    private String selected;
    private String[] subjectSpinner = {"Arts", "Biology", "Economics", "Government & Law", "Mathematics", "Programming Language"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() == null) {
            signInUser();
        }

        editTitle = (EditText) findViewById(R.id.editTitle);
        editDescription = (EditText) findViewById(R.id.editDescription);
        buttonubmit = (Button) findViewById(R.id.buttonSubmit);
        databaseQuestion = FirebaseDatabase.getInstance().getReference("questions");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, subjectSpinner);
        spinnerSubject = (MaterialBetterSpinner)
                findViewById(R.id.spinnerSubject);
        spinnerSubject.setAdapter(arrayAdapter);

        buttonubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addQuestion();
            }
        });
    }

    private void addQuestion() {
        title = editTitle.getText().toString();
        description = editDescription.getText().toString();
        selected = spinnerSubject.getText().toString();

        if(!TextUtils.isEmpty(title)) {
            if(!TextUtils.isEmpty(description)) {
                String questionId = databaseQuestion.push().getKey();
                String userId = firebaseAuth.getCurrentUser().getUid();
                String questionStatus = "Uncompleted";
                String questionPostDate = getDate();
                String questionPostTime = getTime();
                String questionReply = "0";
                Question question = new Question(questionId, title, description, questionStatus, questionPostDate, questionPostTime, userId, questionReply);
                databaseQuestion.child(selected).child(questionId).setValue(question);
                Toast.makeText(this, "Question added", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this, "Please enter question description!", Toast.LENGTH_LONG).show();
            }
        }
        else {
            Toast.makeText(this, "Please enter question title!", Toast.LENGTH_LONG).show();
        }
    }

    public String getDate() {
        String date = new java.text.SimpleDateFormat("EEE, d MMM yyyy").format(java.util.Calendar.getInstance().getTime());
        return date;
    }

    public String getTime() {
        String time = new java.text.SimpleDateFormat("hh:mm aaa").format(java.util.Calendar.getInstance().getTime());
        return time;
    }

    private void signInUser() {
        Intent intent = new Intent(this, SignIn.class);
        startActivity(intent);
    }
}
