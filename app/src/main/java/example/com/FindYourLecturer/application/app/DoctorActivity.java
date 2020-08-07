package example.com.FindYourLecturer.application.app;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import example.com.FindYourLecturer.R;
import example.com.FindYourLecturer.application.adapter.DoctorAdapter;
import example.com.FindYourLecturer.application.model.DoctorInfo;

public class DoctorActivity extends AppCompatActivity{

    private ProgressBar progressBar;
    private DoctorAdapter doctorAdapter;
    private ListView doctorView;
    private ArrayList<DoctorInfo> doctorInfoList = new ArrayList<DoctorInfo>();
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference doctor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Doctor List");

        firebaseDatabase = FirebaseDatabase.getInstance();
        doctor = firebaseDatabase.getReference("Doctor");

        doctorView = (ListView) findViewById(R.id.doctorListView);
        progressBar = (ProgressBar) findViewById(R.id.doctorProgressBar);
        progressBar.setVisibility(View.VISIBLE);

        doctorAdapter = new DoctorAdapter(DoctorActivity.this, doctorInfoList);
        doctorView.setAdapter(doctorAdapter);

        doctor.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                doctorInfoList.clear();
                progressBar.setVisibility(View.GONE);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DoctorInfo doctorInfo = snapshot.getValue(DoctorInfo.class);
                    if (doctorInfo != null) {
                        doctorInfoList.add(doctorInfo);
                    }
                }
                Collections.sort(doctorInfoList, new Comparator<DoctorInfo>() {
                    @Override
                    public int compare(DoctorInfo lhs, DoctorInfo rhs) {
                        return lhs.DoctorName.trim().compareTo(rhs.DoctorName.trim());
                    }
                });
                doctorAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(DoctorActivity.this, "Failed to Connect Database", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
