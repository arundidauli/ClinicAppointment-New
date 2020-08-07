package example.com.FindYourLecturer.application.app;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import example.com.FindYourLecturer.R;
import example.com.FindYourLecturer.application.adapter.UpcomingAppointmentAdapter;
import example.com.FindYourLecturer.application.model.AppointmentInfo;

public class EditAppointmentActivity extends AppCompatActivity{

    private int mYear;
    private int mMonth;
    private int mDay;
    private Calendar calendar;
    private Calendar minCalendar;
    private Calendar maxCalendar;
    private SimpleDateFormat dateFormat;
    private EditText chooseDate;
    private TextView noRecord;
    private TextView clickToView;
    private ListView appointmentView;
    private UpcomingAppointmentAdapter upcomingAppointmentAdapter;
    private ArrayList<AppointmentInfo> appointmentList = new ArrayList<AppointmentInfo>();
    private ProgressBar progressBar;
    private String selectedDate;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference appointment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editappointment);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Appointment Made");

        firebaseDatabase = FirebaseDatabase.getInstance();
        appointment = firebaseDatabase.getReference("Appointment");

        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        chooseDate = (EditText) findViewById(R.id.chooseDateArea);
        clickToView = (TextView) findViewById(R.id.clickToView);
        noRecord = (TextView) findViewById(R.id.noRecord);
        appointmentView = (ListView) findViewById(R.id.editAppointmentList);
        progressBar = (ProgressBar) findViewById(R.id.editProgressBar);

        chooseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker();
            }
        });

        appointmentView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                AppointmentInfo appointmentInfo;
                appointmentInfo = appointmentList.get(position);

                Intent intent = new Intent(EditAppointmentActivity.this, StatusAppointmentActivity.class);
                intent.putExtra("booked", appointmentInfo.Booked);
                intent.putExtra("datetime", appointmentInfo.DateTime);
                intent.putExtra("key", appointmentInfo.Key);
                intent.putExtra("matric", appointmentInfo.Matric);
                intent.putExtra("name", appointmentInfo.Name);
                intent.putExtra("phone", appointmentInfo.Phone);
                intent.putExtra("reason", appointmentInfo.Reason);
                intent.putExtra("sequence", appointmentInfo.Sequence);
                intent.putExtra("status", appointmentInfo.Status);
                intent.putExtra("token", appointmentInfo.Token);

                if (appointmentInfo.Status.equals("Pending")) {
                    intent.putExtra("show", true);
                    startActivity(intent);
                } else {
                    intent.putExtra("show", false);
                    startActivity(intent);
                }
            }
        });
    }

    public void datePicker() {

        minCalendar = Calendar.getInstance();
        maxCalendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(EditAppointmentActivity.this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                calendar.set(year, monthOfYear, dayOfMonth);
                chooseDate.setText(dateFormat.format(calendar.getTime()));
                selectedDate = dateFormat.format(calendar.getTime());
                loadAppointment();

            }
        }, mYear, mMonth, mDay);
        minCalendar.add(Calendar.DAY_OF_YEAR, 1);
        maxCalendar.add(Calendar.DAY_OF_YEAR, 7);
        datePickerDialog.getDatePicker().setMinDate(minCalendar.getTimeInMillis());
        datePickerDialog.getDatePicker().setMaxDate(maxCalendar.getTimeInMillis());
        datePickerDialog.show();
    }

    public void loadAppointment() {

        progressBar.setVisibility(View.VISIBLE);

        upcomingAppointmentAdapter = new UpcomingAppointmentAdapter(EditAppointmentActivity.this, appointmentList);
        appointmentView.setAdapter(upcomingAppointmentAdapter);

        appointment.orderByChild("Milli").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                appointmentList.clear();
                progressBar.setVisibility(View.GONE);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    AppointmentInfo appointmentInfo = snapshot.getValue(AppointmentInfo.class);
                    if (appointmentInfo != null) {
                        Date dateTime;
                        String chosenDate = null;
                        try {
                            dateTime = dateFormat.parse(appointmentInfo.DateTime);
                            chosenDate = dateFormat.format(dateTime);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (selectedDate.equals(chosenDate)) {
                            appointmentList.add(appointmentInfo);
                        }
                    }
                }
                upcomingAppointmentAdapter.notifyDataSetChanged();
                if (appointmentList.size() == 0) {
                    noRecord.setVisibility(View.VISIBLE);
                    clickToView.setVisibility(View.GONE);
                    appointmentView.setVisibility(View.GONE);
                } else if (appointmentList.size() > 0) {
                    noRecord.setVisibility(View.GONE);
                    clickToView.setVisibility(View.VISIBLE);
                    appointmentView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(EditAppointmentActivity.this, "Failed to Connect Database", Toast.LENGTH_LONG).show();
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