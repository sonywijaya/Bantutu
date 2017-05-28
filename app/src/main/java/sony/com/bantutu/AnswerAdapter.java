package sony.com.bantutu;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Sony Surya on 22/05/2017.
 */

public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.ViewHolder> {
    private List<Answer> answerList;
    private Context context;
    private Answer answer;
    private DatabaseReference databaseReference;
    private String userId, pointsGiven, subject, questionStatus;
    private FirebaseAuth firebaseAuth;

    public AnswerAdapter(Context context, List<Answer> answerList, String userId, String subject, String questionStatus) {
        this.context = context;
        this.answerList = answerList;
        this.userId = userId;
        this.subject = subject;
        this.questionStatus = questionStatus;
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public static class ViewHolder extends  RecyclerView.ViewHolder {
        public TextView textNameAnswer, textDateAnswer, textTimeAnswer;
        public TextView textTitleAnswer, textDescriptionAnswer, textBestAnswer;
        public View viewBar;
        public ImageView thumb;
        public CircleImageView userPicture;
        public LinearLayout bestAnswer;
        public CardView cardAnswerList;
        public ViewHolder(View v) {
            super(v);
            textNameAnswer = (TextView) v.findViewById(R.id.textNameAnswer);
            textDateAnswer = (TextView) v.findViewById(R.id.textDateAnswer);
            textTimeAnswer = (TextView) v.findViewById(R.id.textTimeAnswer);
            textTitleAnswer = (TextView) v.findViewById(R.id.textTitleAnswer);
            textBestAnswer = (TextView) v.findViewById(R.id.textBestAnswer);
            viewBar = v.findViewById(R.id.viewBar);
            textDescriptionAnswer = (TextView) v.findViewById(R.id.textDescriptionAnswer);
            thumb = (ImageView) v.findViewById(R.id.thumb);
            userPicture = (CircleImageView) v.findViewById(R.id.userPicture);
            bestAnswer = (LinearLayout) v.findViewById(R.id.bestAnswer);
            cardAnswerList = (CardView) v.findViewById(R.id.cardAnswerList);
        }
    }

    @Override
    public AnswerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_answer, parent, false);
        AnswerAdapter.ViewHolder viewHolder = new AnswerAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        answer = answerList.get(position);
        if (isCompleted() && answer.getIsBestAnswer().equals("1")) {
            holder.viewBar.setVisibility(View.VISIBLE);
            holder.bestAnswer.setVisibility(View.VISIBLE);
            holder.textBestAnswer.setText("Choosen as Best Answer!");
            holder.textBestAnswer.setTextColor(Color.parseColor("#27ae60"));
        }
        getName(holder);
        holder.textTitleAnswer.setText(answer.getAnswerTitle());
        holder.textDescriptionAnswer.setText(answer.getAnswerDescription());
        holder.textDateAnswer.setText(answer.getAnswerPostDate());
        holder.textTimeAnswer.setText(answer.getAnswerPostTime());
        if (isQuestionMaker() && !isCompleted()) {
            holder.bestAnswer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Mark as Best Answer").setMessage("How many points will be rewarded?");
                    final EditText input = new EditText(context);
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            databaseReference.child("users").child(userId).child("points").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(Integer.valueOf(dataSnapshot.getValue().toString()) > Integer.valueOf(input.getText().toString())) {
                                        addPoints(input);
                                    }
                                    else {
                                        Toast.makeText(context, "Not enough points!", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    builder.show();
                }
            });
        }
        else if(isQuestionMaker() && isCompleted() && !answer.getIsBestAnswer().equals("1")) {
            holder.viewBar.setVisibility(View.GONE);
            holder.bestAnswer.setVisibility(View.GONE);
        }
        else if(!isQuestionMaker() && !isCompleted()) {
            holder.viewBar.setVisibility(View.GONE);
            holder.bestAnswer.setVisibility(View.GONE);
        }

    }

    private boolean isCompleted() {
        if (questionStatus.equals("Completed")) {
            return true;
        }
        else {
            return false;
        }
    }

    private void addPoints(EditText input) {
        pointsGiven = input.getText().toString();
        databaseReference.child("answers").child(answer.getQuestionId()).child(answer.getAnswerId()).child("isBestAnswer").setValue("1");
        databaseReference.child("users").child(answer.getUserId()).child("points").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Integer pointsEarned = Integer.valueOf(dataSnapshot.getValue().toString());
                Integer newPoints = pointsEarned + Integer.valueOf(pointsGiven);
                databaseReference.child("users").child(answer.getUserId()).child("points").setValue(newPoints.toString());
                updatePoints(Integer.valueOf(pointsGiven));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        databaseReference.child("questions").child(subject).child(answer.getQuestionId()).child("questionStatus").setValue("Completed");
    }

    private void updatePoints(final int pointsSpent) {
        databaseReference.child("users").child(userId).child("points").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Integer currentTotalPoints = Integer.valueOf(dataSnapshot.getValue().toString());
                Integer newTotalPoints = currentTotalPoints - pointsSpent;
                databaseReference.child("users").child(userId).child("points").setValue(newTotalPoints.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getName(final AnswerAdapter.ViewHolder holder) {
        databaseReference.child("users").child(answer.getUserId()).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                holder.textNameAnswer.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public int getItemCount() {
        return answerList.size();
    }

    public boolean isQuestionMaker() {
        firebaseAuth = FirebaseAuth.getInstance();
        String viewUserId = firebaseAuth.getCurrentUser().getUid();
        if (viewUserId.equals(userId)) {
            return true;
        }
        else {
            return false;
        }
    }
}
