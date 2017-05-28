package sony.com.bantutu;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.ViewHolder> {

    private String[] subjects = {"Arts", "Biology", "Economics", "Government & Law", "Mathematics", "Programming Language"};
    private int[] images = {R.mipmap.arts, R.mipmap.biology, R.mipmap.economics, R.mipmap.government, R.mipmap.mathematics, R.mipmap.programming};
    private Context context;

    public SubjectAdapter(Context context){
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView cardSubject;
        public TextView textSubject;
        public ImageView imageSubject;
        public ViewHolder(View v) {
            super(v);
            cardSubject = (CardView)v.findViewById(R.id.cardSubject);
            textSubject = (TextView) v.findViewById(R.id.textSubject);
            imageSubject = (ImageView) v.findViewById(R.id.imageSubject);
        }
    }

    @Override
    public SubjectAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subject, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.textSubject.setText(subjects[position]);
        holder.imageSubject.setImageResource(images[position]);
        holder.cardSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, QuestionMenu.class);
                intent.putExtra("subject", subjects[position]);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return subjects.length;
    }
}
