package example.com.FindYourLecturer.application.app;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import example.com.FindYourLecturer.R;
import example.com.FindYourLecturer.application.model.AppointmentInfo;
import example.com.FindYourLecturer.application.model.UserInfo;

public class NewBookingActivity extends AppCompatActivity{

    private int mYear;
    private int mMonth;
    private int mDay;
    private Calendar calendar;
    private Calendar minCalendar;
    private Calendar maxCalendar;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat simpleDateFormat;

    private EditText nameArea;
    private EditText matricArea;
    private EditText phoneArea;
    private EditText dateArea;
    private EditText timeArea;
    private EditText reasonArea;
    private Button confirmButton;

    private String currentUserID;
    private String currentUserToken;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference users;
    private DatabaseReference appointmentExist;
    private String uniqueKey;

    private ProgressDialog progressDialogConfirm;
    private ProgressDialog progressDialogChecking;

    private String[] timeChoice;
    private String chosenDateTime;
    private int countAppointment;
    private HashMap<String, String> appointmentMap = new HashMap<String, String>();
    private Boolean slotAvailability = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newbooking);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("New Booking");

        timeChoice = getResources().getStringArray(R.array.time_choice);

        dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.US);
        calendar = Calendar.getInstance();

        nameArea = (EditText) findViewById(R.id.nameArea);
        matricArea = (EditText) findViewById(R.id.matricArea);
        phoneArea = (EditText) findViewById(R.id.phoneArea);
        dateArea = (EditText) findViewById(R.id.dateArea);
        timeArea = (EditText) findViewById(R.id.timeArea);
        reasonArea = (EditText) findViewById(R.id.reasonArea);
        confirmButton = (Button) findViewById(R.id.confirmButton);

        firebaseDatabase = FirebaseDatabase.getInstance();
        users = firebaseDatabase.getReference("Users");
        appointmentExist = firebaseDatabase.getReference("Appointment");
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        currentUserToken = FirebaseInstanceId.getInstance().getToken();

        timeArea.setEnabled(false);

        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserInfo userInfo = dataSnapshot.child(currentUserID).getValue(UserInfo.class);
                if (userInfo != null) {
                    matricArea.setText(userInfo.MatricNumber);
                    matricArea.setEnabled(false);
                    nameArea.setText(userInfo.Name);
                    nameArea.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(NewBookingActivity.this, "Failed to Connect Database", Toast.LENGTH_LONG).show();
            }
        });

        dateArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker();
            }
        });

        timeArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePicker();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneArea.setEnabled(false);
                dateArea.setEnabled(false);
                timeArea.setEnabled(false);
                reasonArea.setEnabled(false);
                confirmButton.setEnabled(false);

                uniqueKey = FirebaseDatabase.getInstance().getReference("Appointment").push().getKey();

                appointmentMap.put("Booked", dateFormat.format(Calendar.getInstance().getTime()));

                if (phoneArea.getText().toString().trim().length() > 0) {
                    appointmentMap.put("Phone", phoneArea.getText().toString().trim());
                } else {
                    Toast.makeText(NewBookingActivity.this, "Please Enter Phone Number", Toast.LENGTH_LONG).show();
                    phoneArea.setEnabled(true);
                    dateArea.setEnabled(true);
                    timeArea.setEnabled(true);
                    reasonArea.setEnabled(true);
                    confirmButton.setEnabled(true);
                    return;
                }

                if (dateArea.getText().toString().trim().length() > 0 && timeArea.getText().toString().trim().length() > 0) {
                    chosenDateTime = dateArea.getText().toString().trim() + " " + timeArea.getText().toString().trim();
                    if (slotAvailability) {
                        appointmentMap.put("DateTime", chosenDateTime);
                        Date milli = null;
                        try {
                            milli = simpleDateFormat.parse(chosenDateTime);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        appointmentMap.put("Milli", String.valueOf(milli.getTime()));
                    } else {
                        Toast.makeText(NewBookingActivity.this, "Slot is Not Available", Toast.LENGTH_LONG).show();
                        phoneArea.setEnabled(true);
                        dateArea.setEnabled(true);
                        timeArea.setEnabled(true);
                        reasonArea.setEnabled(true);
                        confirmButton.setEnabled(true);
                        return;
                    }
                } else {
                    Toast.makeText(NewBookingActivity.this, "Please Choose Date and Time", Toast.LENGTH_LONG).show();
                    phoneArea.setEnabled(true);
                    dateArea.setEnabled(true);
                    timeArea.setEnabled(true);
                    reasonArea.setEnabled(true);
                    confirmButton.setEnabled(true);
                    return;
                }

                appointmentMap.put("Matric", matricArea.getText().toString().trim());
                appointmentMap.put("Name", nameArea.getText().toString().trim());

                if (reasonArea.getText().toString().trim().length() > 0) {
                    appointmentMap.put("Reason", reasonArea.getText().toString().trim());
                } else {
                    Toast.makeText(NewBookingActivity.this, "Please Enter Reason", Toast.LENGTH_LONG).show();
                    phoneArea.setEnabled(true);
                    dateArea.setEnabled(true);
                    timeArea.setEnabled(true);
                    reasonArea.setEnabled(true);
                    confirmButton.setEnabled(true);
                    return;
                }

                appointmentMap.put("Sequence", "");
                appointmentMap.put("Status", "Pending");
                appointmentMap.put("Key", uniqueKey);
                appointmentMap.put("Token", currentUserToken);

                confirmAppointment();
            }
        });
    }

    public void datePicker() {

        minCalendar = Calendar.getInstance();
        maxCalendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(NewBookingActivity.this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                calendar.set(year, monthOfYear, dayOfMonth);
                dateArea.setText(dateFormat.format(calendar.getTime()));
                timeArea.setEnabled(true);
                if (timeArea.getText().toString().trim().length() > 0) {
                    checkingAvailability();
                }

            }
        }, mYear, mMonth, mDay);
        minCalendar.add(Calendar.DAY_OF_YEAR, 1);
        maxCalendar.add(Calendar.DAY_OF_YEAR, 7);
        datePickerDialog.getDatePicker().setMinDate(minCalendar.getTimeInMillis());
        datePickerDialog.getDatePicker().setMaxDate(maxCalendar.getTimeInMillis());
        datePickerDialog.show();
    }

    public void timePicker() {

        AlertDialog.Builder builder = new AlertDialog.Builder(NewBookingActivity.this);
        builder.setTitle("Choose Your Appointment Time");
        builder.setSingleChoiceItems(timeChoice, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                timeArea.setText(timeChoice[which]);
                dialog.dismiss();
                checkingAvailability();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void checkingAvailability() {
        progressDialogChecking = ProgressDialog.show(new ContextThemeWrapper(NewBookingActivity.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert), "", "Checking Availability...", true, false);
        countAppointment = 0;
        appointmentExist.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    AppointmentInfo latestInfo = snapshot.getValue(AppointmentInfo.class);
                    if (latestInfo != null) {
                        if (latestInfo.DateTime.equals(dateArea.getText().toString().trim() + " " + timeArea.getText().toString().trim())) {
                            countAppointment++;
                        }
                    }
                }
                if (countAppointment < 5) {
                    progressDialogChecking.dismiss();
                    timeArea.setTextColor(getResources().getColor(R.color.colorBlack));
                    slotAvailability = true;
                } else {
                    progressDialogChecking.dismiss();
                    timeArea.setTextColor(getResources().getColor(R.color.colorRed));
                    Toast.makeText(NewBookingActivity.this, "Slot is Not Available", Toast.LENGTH_LONG).show();
                    slotAvailability = false;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialogChecking.dismiss();
                Toast.makeText(NewBookingActivity.this, "Failed to Connect Database", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void confirmAppointment() {

        progressDialogConfirm = ProgressDialog.show(new ContextThemeWrapper(NewBookingActivity.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert), "", "Please Wait...", true, false);

        FirebaseDatabase.getInstance().getReference("Appointment").child(uniqueKey).setValue(appointmentMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isComplete()) {
                    progressDialogConfirm.dismiss();
                    finish();
                } else {
                    phoneArea.setEnabled(true);
                    dateArea.setEnabled(true);
                    timeArea.setEnabled(true);
                    reasonArea.setEnabled(true);
                    confirmButton.setEnabled(true);
                    progressDialogConfirm.dismiss();
                    Toast.makeText(NewBookingActivity.this, "Failed to Connect Database", Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                phoneArea.setEnabled(true);
                dateArea.setEnabled(true);
                timeArea.setEnabled(true);
                reasonArea.setEnabled(true);
                confirmButton.setEnabled(true);
                progressDialogConfirm.dismiss();
                Toast.makeText(NewBookingActivity.this, "Failed to Connect Database", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        View view = getCurrentFocus();
        if (view != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) && view instanceof EditText) {
            int scrcoords[] = new int[2];
            view.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + view.getLeft() - scrcoords[0];
            float y = ev.getRawY() + view.getTop() - scrcoords[1];
            if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom())
                ((InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((this.getWindow().getDecorView().getApplicationWindowToken()), 0);
        }
        return super.dispatchTouchEvent(ev);
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
