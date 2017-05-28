package sony.com.bantutu;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

public class QuestionMenu extends AppCompatActivity {
    private RecyclerView.Adapter adapter;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseQuestion;
    private FloatingActionButton fab;
    private RecyclerView recyclerQuestion;
    private LinearLayoutManager layoutManager;
    private String subject;
    private AVLoadingIndicatorView avi;
    List<Question> questionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_menu);
        subject = getIntent().getExtras().getString("subject");

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            signInUser();
        }

        databaseQuestion = FirebaseDatabase.getInstance().getReference("questions");
        questionList = new ArrayList<>();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addQuestion();
            }
        });

        avi = (AVLoadingIndicatorView) findViewById(R.id.avi);

        recyclerQuestion = (RecyclerView) findViewById(R.id.recyclerQuestion);
        recyclerQuestion.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        databaseQuestion.child(subject).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                questionList.clear();
                for (DataSnapshot questionSnapshot : dataSnapshot.getChildren()) {
                    Question question = questionSnapshot.getValue(Question.class);
                    questionList.add(question);
                }
                if (questionList.size() > 0) {
                    avi.hide();
                    adapter = new QuestionAdapter(QuestionMenu.this, questionList, subject);
                    recyclerQuestion.setAdapter(adapter);
                    recyclerQuestion.setLayoutManager(layoutManager);
                } else {
                    avi.hide();
                    Toast.makeText(getApplicationContext(), "No data yet!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void signInUser() {
        Intent intent = new Intent(this, SignIn.class);
        startActivity(intent);
    }

    private void addQuestion() {
        Intent intent = new Intent(this, AddQuestion.class);
        startActivity(intent);
    }
}
