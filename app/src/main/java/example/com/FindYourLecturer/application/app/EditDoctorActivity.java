package example.com.FindYourLecturer.application.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
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

public class EditDoctorActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private DoctorAdapter doctorAdapter;
    private ListView doctorAdminView;
    private ArrayList<DoctorInfo> doctorAdminList = new ArrayList<DoctorInfo>();
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference doctorAdmin;
    private ImageView addNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editdoctor);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        addNew = (ImageView) toolbar.findViewById(R.id.addIcon);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Doctor");

        firebaseDatabase = FirebaseDatabase.getInstance();
        doctorAdmin = firebaseDatabase.getReference("Doctor");

        doctorAdminView = (ListView) findViewById(R.id.doctorAdminListView);
        progressBar = (ProgressBar) findViewById(R.id.doctorAdminProgressBar);
        progressBar.setVisibility(View.VISIBLE);

        doctorAdapter = new DoctorAdapter(EditDoctorActivity.this, doctorAdminList);
        doctorAdminView.setAdapter(doctorAdapter);
        callDatabase();

        addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditDoctorActivity.this, UpdateDoctorActivity.class);
                intent.putExtra("doctorAction", false);
                startActivity(intent);
            }
        });

        doctorAdminView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                DoctorInfo doctorInfo;
                doctorInfo = doctorAdminList.get(position);

                Intent intent = new Intent(EditDoctorActivity.this, UpdateDoctorActivity.class);
                intent.putExtra("doctorID", doctorInfo.DoctorID);
                intent.putExtra("doctorImage", doctorInfo.DoctorImage);
                intent.putExtra("doctorKey", doctorInfo.DoctorKey);
                intent.putExtra("doctorName", doctorInfo.DoctorName);
                intent.putExtra("doctorAction", true);
                intent.putExtra("doctorSize", doctorAdminList.size());
                startActivity(intent);
            }
        });
    }

    public void callDatabase() {

        doctorAdmin.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                doctorAdminList.clear();
                progressBar.setVisibility(View.GONE);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DoctorInfo doctorInfo = snapshot.getValue(DoctorInfo.class);
                    if (doctorInfo != null) {
                        doctorAdminList.add(doctorInfo);
                    }
                }
                Collections.sort(doctorAdminList, new Comparator<DoctorInfo>() {
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
                Toast.makeText(EditDoctorActivity.this, "Failed to Connect Database", Toast.LENGTH_LONG).show();
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
