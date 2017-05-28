package sony.com.bantutu;

/**
 * Created by Sony Surya on 06/05/2017.
 */

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class TabPoint extends Fragment {
    private DatabaseReference database;
    private FirebaseAuth firebaseAuth;
    private TextView totalPoints;
    private RecyclerView.Adapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_point, container, false);
        totalPoints = (TextView) rootView.findViewById(R.id.totalPoints);
        database = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() == null) {
            signInUser();
        }
        database.child("users").child(firebaseAuth.getCurrentUser().getUid()).child("points").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                totalPoints.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        RecyclerView recyclerVoucher = (RecyclerView)rootView.findViewById(R.id.recyclerVoucher);
        recyclerVoucher.setHasFixedSize(true);

        adapter = new VoucherAdapter(getContext());
        recyclerVoucher.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerVoucher.setLayoutManager(layoutManager);
        return rootView;
    }

    private void signInUser() {
        Intent intent = new Intent(getContext(), SignIn.class);
        startActivity(intent);
    }
}
