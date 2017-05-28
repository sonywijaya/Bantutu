package sony.com.bantutu;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AddAnswer extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 123;
    private EditText editAnswerTitle, editAnswerDescription;
    private Button buttonUpload, buttonSubmit;
    private Uri filePath;
    private String questionId, userId, answerId, answerTitle, answerDescription, answerPostDate, answerPostTime, isBestAnswer, subject;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_answer);

        Bundle extras = getIntent().getExtras();
        questionId = extras.getString("questionId");
        subject = extras.getString("subject");
        firebaseAuth = FirebaseAuth.getInstance();
        editAnswerTitle = (EditText) findViewById(R.id.editAnswerTitle);
        editAnswerDescription = (EditText) findViewById(R.id.editAnswerDescription);
        buttonUpload = (Button) findViewById(R.id.buttonUpload);
        buttonSubmit = (Button) findViewById(R.id.buttonSubmit);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        answerId = databaseReference.push().getKey();
        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //showChooser();
            }
        });
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //uploadFile();
                addAnswer();
            }
        });
    }

    private void showChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void uploadFile() {
        if (filePath != null) {

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference answerRef = storageReference.child("images").child("answers").child(questionId).child(answerId).child("something.jpg");
            answerRef.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    addAnswer();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Failed to upload file!", Toast.LENGTH_LONG).show();
                }
            });
        }
        else {

        }
    }

    private void addAnswer() {
        answerTitle = editAnswerTitle.getText().toString();
        answerDescription = editAnswerDescription.getText().toString();
        answerPostDate = getDate();
        answerPostTime = getTime();
        isBestAnswer = "0";

        if(!TextUtils.isEmpty(answerTitle)) {
            userId = firebaseAuth.getCurrentUser().getUid();
            Answer answer = new Answer(questionId, userId, answerId, answerTitle, answerDescription, answerPostDate, answerPostTime, isBestAnswer);
            databaseReference.child("answers").child(questionId).child(answerId).setValue(answer);
            updateReplyCount();
            Toast.makeText(this, "Answer successfully added", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(this, "Please enter your answer!", Toast.LENGTH_LONG).show();
        }
    }

    private void updateReplyCount() {
        databaseReference.child("questions").child(subject).child(questionId).child("questionReply").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Integer replyCount = Integer.valueOf(dataSnapshot.getValue().toString());
                Integer newReplyCount = replyCount + 1;
                databaseReference.child("questions").child(subject).child(questionId).child("questionReply").setValue(newReplyCount.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && requestCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            Toast.makeText(getApplicationContext(), filePath.toString(), Toast.LENGTH_LONG).show();
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
}
