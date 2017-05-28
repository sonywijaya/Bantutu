package sony.com.bantutu;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class OpenQuestion extends AppCompatActivity {
    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerAnswer;
    private LinearLayoutManager layoutManager;
    List<Answer> answerList;
    private TextView textNameQuestion, textDateQuestion, textTimeQuestion;
    private TextView textTitleQuestion, textDescriptionQuestion, textReply, textStatusQuestion;
    private ImageView imageQuestion;
    private CircleImageView userPicture;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FloatingActionButton fab;
    private String viewUserId, subject, questionId, userId, questionPostDate, questionPostTime, questionTitle, questionDescription, questionStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_question);

        firebaseAuth = FirebaseAuth.getInstance();
        viewUserId = firebaseAuth.getCurrentUser().getUid();
        Bundle extras = getIntent().getExtras();
        subject = extras.getString("subject");
        questionId = extras.getString("questionId");
        userId = extras.getString("userId");
        questionPostDate = extras.getString("questionPostDate");
        questionPostTime = extras.getString("questionPostTime");
        questionTitle = extras.getString("questionTitle");
        questionDescription = extras.getString("questionDescription");
        questionStatus = extras.getString("questionStatus");
        initView();
        if (viewUserId.equals(userId) || questionStatus.equals("Completed")) {
            fab.setVisibility(View.GONE);
        } else {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addAnswer();
                }
            });
        }

        textDateQuestion.setText(questionPostDate);
        textTimeQuestion.setText(questionPostTime);
        textTitleQuestion.setText(questionTitle);
        textDescriptionQuestion.setText(questionDescription);
        getName(userId);
        answerList = new ArrayList<>();
        recyclerAnswer = (RecyclerView) findViewById(R.id.recyclerAnswer);
        recyclerAnswer.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
    }

    private void initView() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        fab = (FloatingActionButton) findViewById(R.id.fab);
        textNameQuestion = (TextView) findViewById(R.id.textNameQuestion);
        textDateQuestion = (TextView) findViewById(R.id.textDateQuestion);
        textTimeQuestion = (TextView) findViewById(R.id.textTimeQuestion);
        textTitleQuestion = (TextView) findViewById(R.id.textTitleQuestion);
        textDescriptionQuestion = (TextView) findViewById(R.id.textDescriptionQuestion);
        textReply = (TextView) findViewById(R.id.textReply);
        textStatusQuestion = (TextView) findViewById(R.id.textStatusQuestion);
        imageQuestion = (ImageView) findViewById(R.id.imageQuestion);
        userPicture = (CircleImageView) findViewById(R.id.userPicture);
    }

    private void getName(String userId) {
        databaseReference.child("users").child(userId).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                textNameQuestion.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addAnswer() {
        Intent intent = new Intent(this, AddAnswer.class);
        Bundle extras = new Bundle();
        extras.putString("questionId", questionId);
        extras.putString("subject", subject);
        intent.putExtras(extras);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        databaseReference.child("answers").child(questionId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                answerList.clear();
                for (DataSnapshot questionSnapshot : dataSnapshot.getChildren()) {
                    Answer answer = questionSnapshot.getValue(Answer.class);
                    answerList.add(answer);
                }
                if (answerList.size() > 0) {
                    adapter = new AnswerAdapter(OpenQuestion.this, answerList, userId, subject, questionStatus);
                    recyclerAnswer.setAdapter(adapter);
                    recyclerAnswer.setLayoutManager(layoutManager);
                } else {
                    Toast.makeText(getApplicationContext(), "No answer yet!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

