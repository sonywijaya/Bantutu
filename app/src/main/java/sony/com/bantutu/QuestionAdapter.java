package sony.com.bantutu;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Sony Surya on 09/05/2017.
 */

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> {

    private List<Question> questionList;
    private Context context;
    private Question question;
    private DatabaseReference databaseQuestion;
    private String subject;

    public QuestionAdapter(Context context, List<Question> questionList, String subject) {
        this.context = context;
        this.questionList = questionList;
        this.subject = subject;
        databaseQuestion = FirebaseDatabase.getInstance().getReference();
    }

    public static class ViewHolder extends  RecyclerView.ViewHolder {
        public TextView textNameQuestion, textDateQuestion, textTimeQuestion;
        public TextView textTitleQuestion, textDescriptionQuestion, textReply, textStatusQuestion;
        public ImageView imageQuestion;
        public CircleImageView userPicture;
        public CardView cardQuestionList;
        public ViewHolder(View v) {
            super(v);
            textNameQuestion = (TextView) v.findViewById(R.id.textNameQuestion);
            textDateQuestion = (TextView) v.findViewById(R.id.textDateQuestion);
            textTimeQuestion = (TextView) v.findViewById(R.id.textTimeQuestion);
            textTitleQuestion = (TextView) v.findViewById(R.id.textTitleQuestion);
            textDescriptionQuestion = (TextView) v.findViewById(R.id.textDescriptionQuestion);
            textReply = (TextView) v.findViewById(R.id.textReply);
            textStatusQuestion = (TextView) v.findViewById(R.id.textStatusQuestion);
            imageQuestion = (ImageView) v.findViewById(R.id.imageQuestion);
            userPicture = (CircleImageView) v.findViewById(R.id.userPicture);
            cardQuestionList = (CardView) v.findViewById(R.id.cardQuestionList);
        }
    }

    @Override
    public QuestionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final QuestionAdapter.ViewHolder holder, int position) {
        question = questionList.get(position);
        getName(holder);
        holder.textDateQuestion.setText(question.getQuestionPostDate());
        holder.textTimeQuestion.setText(question.getQuestionPostTime());
        holder.textStatusQuestion.setText(question.getQuestionStatus());
        holder.textReply.setText(question.getQuestionReply());
        holder.textTitleQuestion.setText(question.getQuestionTitle());
        holder.textDescriptionQuestion.setText(question.getQuestionDescription());
        holder.cardQuestionList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, OpenQuestion.class);
                Bundle extras = new Bundle();
                extras.putString("questionId", question.getQuestionId());
                extras.putString("subject", subject);
                extras.putString("userId", question.getUserId());
                extras.putString("questionPostDate", question.getQuestionPostDate());
                extras.putString("questionPostTime", question.getQuestionPostTime());
                extras.putString("questionTitle", question.getQuestionTitle());
                extras.putString("questionDescription", question.getQuestionDescription());
                extras.putString("questionStatus", question.getQuestionStatus());
                intent.putExtras(extras);
                context.startActivity(intent);
            }
        });
    }

    private void getName(final ViewHolder holder) {
        databaseQuestion.child("users").child(question.getUserId()).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                holder.textNameQuestion.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public int getItemCount() {
        return questionList.size();
    }
}
