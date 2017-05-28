package sony.com.bantutu;

/**
 * Created by Sony Surya on 06/05/2017.
 */

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class TabSubject extends Fragment {
    private RecyclerView.Adapter adapter;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference database;
    private FloatingActionButton fab;

    public TabSubject() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_subject, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() == null) {
            signInUser();
        }

        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addQuestion();
            }
        });

        RecyclerView recyclerQuestion = (RecyclerView)rootView.findViewById(R.id.recyclerSubject);
        recyclerQuestion.setHasFixedSize(true);

        adapter = new SubjectAdapter(getContext());
        recyclerQuestion.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerQuestion.setLayoutManager(layoutManager);

        return rootView;
    }

    private void signInUser() {
        Intent intent = new Intent(getContext(), SignIn.class);
        startActivity(intent);
    }

    private void addQuestion() {
        Intent intent = new Intent(getContext(), AddQuestion.class);
        startActivity(intent);
    }

}

