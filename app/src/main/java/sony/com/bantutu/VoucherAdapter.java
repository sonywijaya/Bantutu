package sony.com.bantutu;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
 * Created by Sony Surya on 23/05/2017.
 */

public class VoucherAdapter extends RecyclerView.Adapter<VoucherAdapter.ViewHolder> {
    private String[] vouchers = {"Free Food Rp10.000", "Free A4 Paper 10pcs", "Free Printing 10pcs", "Free Parking 1 Day"};
    private String[] voucherPrice = {"100", "100", "100", "250"};
    private String[] voucherCode = {"FOOD10K", "A410FREE", "PRINT10", "PARKME1DAY"};
    private int[] voucherImage = {R.mipmap.food, R.mipmap.a4, R.mipmap.printer, R.mipmap.parking};
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    Context context;

    public VoucherAdapter(Context context) {
        this.context = context;
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public static class ViewHolder extends  RecyclerView.ViewHolder {
        public TextView voucherTitle, pointsPrice;
        public Button buttonExchange;
        public ImageView backgroundVoucher;
        public CardView cardVoucher;
        public ViewHolder(View v) {
            super(v);
            voucherTitle = (TextView) v.findViewById(R.id.voucherTitle);
            pointsPrice = (TextView) v.findViewById(R.id.pointsPrice);
            buttonExchange = (Button) v.findViewById(R.id.buttonExchange);
            backgroundVoucher = (ImageView) v.findViewById(R.id.backgroundVoucher);
            cardVoucher = (CardView) v.findViewById(R.id.cardVoucher);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_voucher, parent, false);
        VoucherAdapter.ViewHolder viewHolder = new VoucherAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.voucherTitle.setText(vouchers[position]);
        holder.pointsPrice.setText(voucherPrice[position]);
        holder.backgroundVoucher.setImageResource(voucherImage[position]);
        holder.buttonExchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.child("users").child(firebaseAuth.getCurrentUser().getUid()).child("points").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(Integer.valueOf(dataSnapshot.getValue().toString()) > Integer.valueOf(voucherPrice[position])) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Exchange Points").setMessage("Are you sure to exchange " + voucherPrice[position] + " points for " + vouchers[position] + "?");
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    updatePoints(Integer.valueOf(voucherPrice[position]));
                                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle("Congratulation!").setMessage("Save and use this code: " + voucherCode[position]);
                                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    });
                                    builder.show();
                                }
                            });
                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            });
                            builder.show();
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
    }

    private void updatePoints(final int pointsSpent) {
        databaseReference.child("users").child(firebaseAuth.getCurrentUser().getUid()).child("points").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Integer currentTotalPoints = Integer.valueOf(dataSnapshot.getValue().toString());
                Integer newTotalPoints = currentTotalPoints - pointsSpent;
                databaseReference.child("users").child(firebaseAuth.getCurrentUser().getUid()).child("points").setValue(newTotalPoints.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return vouchers.length;
    }
}
