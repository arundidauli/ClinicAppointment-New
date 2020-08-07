package example.com.FindYourLecturer.application.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import example.com.FindYourLecturer.R;
import example.com.FindYourLecturer.application.adapter.UpcomingAppointmentAdapter;
import example.com.FindYourLecturer.application.model.AppointmentInfo;
import example.com.FindYourLecturer.application.model.UserInfo;

public class UpcomingAppointmentActivity extends AppCompatActivity{

    private UpcomingAppointmentAdapter upcomingAppointmentAdapter;
    private ListView upcomingAppointmentView;
    private TextView noRecord;
    private ProgressBar progressBar;
    private ArrayList<AppointmentInfo> upcomingAppointmentList = new ArrayList<AppointmentInfo>();
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference appointment;
    private DatabaseReference currentUser;
    private String currentUserID;
    private UserInfo userInfo;
    private SimpleDateFormat simpleDateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcomingappointment);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("History");

        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.US);

        firebaseDatabase = FirebaseDatabase.getInstance();
        appointment = firebaseDatabase.getReference("Appointment");
        currentUser = firebaseDatabase.getReference("Users");
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        noRecord = (TextView) findViewById(R.id.noRecord);
        upcomingAppointmentView = (ListView) findViewById(R.id.upcomingAppointmentList);
        progressBar = (ProgressBar) findViewById(R.id.upcomingProgressBar);

        progressBar.setVisibility(View.VISIBLE);

        currentUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userInfo = dataSnapshot.child(currentUserID).getValue(UserInfo.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UpcomingAppointmentActivity.this, "Failed to Connect Database", Toast.LENGTH_LONG).show();
            }
        });

        upcomingAppointmentAdapter = new UpcomingAppointmentAdapter(UpcomingAppointmentActivity.this, upcomingAppointmentList);
        upcomingAppointmentView.setAdapter(upcomingAppointmentAdapter);

        appointment.orderByChild("Milli").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                upcomingAppointmentList.clear();
                progressBar.setVisibility(View.GONE);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    AppointmentInfo appointmentInfo = snapshot.getValue(AppointmentInfo.class);
                    if (appointmentInfo != null) {
                        Date dateTime = new Date();
                        try {
                            dateTime = simpleDateFormat.parse(appointmentInfo.DateTime);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (userInfo.MatricNumber.equals(appointmentInfo.Matric)) {
                            if (dateTime.getTime() > System.currentTimeMillis()) {
                                upcomingAppointmentList.add(appointmentInfo);
                            }
                        }
                    }
                }
                upcomingAppointmentAdapter.notifyDataSetChanged();
                if (upcomingAppointmentList.size() == 0) {
                    noRecord.setVisibility(View.VISIBLE);
                    upcomingAppointmentView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(UpcomingAppointmentActivity.this, "Failed to Connect Database", Toast.LENGTH_LONG).show();
            }
        });

        upcomingAppointmentView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                AppointmentInfo appointmentInfo;
                appointmentInfo = upcomingAppointmentList.get(position);

                Intent intent = new Intent(UpcomingAppointmentActivity.this, StatusAppointmentActivity.class);
                intent.putExtra("booked", appointmentInfo.Booked);
                intent.putExtra("datetime", appointmentInfo.DateTime);
                intent.putExtra("key", appointmentInfo.Key);
                intent.putExtra("matric", appointmentInfo.Matric);
                intent.putExtra("name", appointmentInfo.Name);
                intent.putExtra("phone", appointmentInfo.Phone);
                intent.putExtra("reason", appointmentInfo.Reason);
                intent.putExtra("sequence", appointmentInfo.Sequence);
                intent.putExtra("status", appointmentInfo.Status);
                intent.putExtra("show", false);
                startActivity(intent);
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
