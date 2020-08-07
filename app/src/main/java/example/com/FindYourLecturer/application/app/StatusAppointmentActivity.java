package example.com.FindYourLecturer.application.app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import example.com.FindYourLecturer.R;
import example.com.FindYourLecturer.application.model.UserInfo;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class StatusAppointmentActivity extends AppCompatActivity{

    private String booked;
    private String datetime;
    private String key;
    private String matric;
    private String name;
    private String phone;
    private String reason;
    private String sequence;
    private String status;
    private String token;
    private Boolean show;

    private TextView bookedOn;
    private TextView dateTime;
    private TextView matricNumber;
    private TextView patientName;
    private TextView phoneNumber;
    private TextView reasonAppointment;
    private TextView sequenceGiven;
    private TextView currentStatus;
    private TextView viewDetails;
    private Button approve;
    private Button reject;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference updateAppointment;

    private ProgressDialog progressDialog;
    private HashMap<String, Object> updateMap = new HashMap<String, Object>();

    private JSONArray jsonArray = new JSONArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statusappointment);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Appointment Detail");

        firebaseDatabase = FirebaseDatabase.getInstance();
        updateAppointment = firebaseDatabase.getReference("Appointment");

        bookedOn = (TextView) findViewById(R.id.bookedOn2);
        dateTime = (TextView) findViewById(R.id.appointmentDateTime2);
        matricNumber = (TextView) findViewById(R.id.patientMatric2);
        patientName = (TextView) findViewById(R.id.patientName2);
        phoneNumber = (TextView) findViewById(R.id.patientPhone2);
        reasonAppointment = (TextView) findViewById(R.id.patientReason2);
        sequenceGiven = (TextView) findViewById(R.id.patientSequence2);
        currentStatus = (TextView) findViewById(R.id.appointmentStatus2);
        viewDetails = (TextView) findViewById(R.id.viewDetails);
        approve = (Button) findViewById(R.id.approveButton);
        reject = (Button) findViewById(R.id.rejectButton);

        if (getIntent() != null && getIntent().getExtras() != null) {
            if (getIntent().getStringExtra("booked") != null) {
                booked = getIntent().getStringExtra("booked");
                bookedOn.setText(booked);
            }
            if (getIntent().getStringExtra("datetime") != null) {
                datetime = getIntent().getStringExtra("datetime");
                dateTime.setText(datetime);
            }
            if (getIntent().getStringExtra("matric") != null) {
                matric = getIntent().getStringExtra("matric");
                matricNumber.setText(matric);
            }
            if (getIntent().getStringExtra("name") != null) {
                name = getIntent().getStringExtra("name");
                patientName.setText(name);
            }
            if (getIntent().getStringExtra("phone") != null) {
                phone = getIntent().getStringExtra("phone");
                phoneNumber.setText(phone);
            }
            if (getIntent().getStringExtra("reason") != null) {
                reason = getIntent().getStringExtra("reason");
                reasonAppointment.setText(reason);
            }
            if (getIntent().getStringExtra("sequence") != null) {
                sequence = getIntent().getStringExtra("sequence");
                sequenceGiven.setText(sequence);
            }
            if (getIntent().getStringExtra("status") != null) {
                status = getIntent().getStringExtra("status");
                currentStatus.setText(status);

                if (status.equalsIgnoreCase("Pending")) {
                    currentStatus.setTextColor(getResources().getColor(R.color.colorGrey));
                } else if (status.equalsIgnoreCase("Approved")) {
                    currentStatus.setTextColor(getResources().getColor(R.color.colorGreen));
                } else if (status.equalsIgnoreCase("Rejected")) {
                    currentStatus.setTextColor(getResources().getColor(R.color.colorRed));
                }
            }
            if (getIntent().getStringExtra("key") != null) {
                key = getIntent().getStringExtra("key");
            }
            if (getIntent().getStringExtra("token") != null) {
                token = getIntent().getStringExtra("token");
                jsonArray.put(token);
                viewDetails.setVisibility(View.VISIBLE);
            }

            show = getIntent().getExtras().getBoolean("show");

            if (!show) {
                approve.setVisibility(View.GONE);
                reject.setVisibility(View.GONE);
            }
        }

        viewDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference users = firebaseDatabase.getReference("Users");

                View view = getLayoutInflater().inflate(R.layout.drawer_details, null);

                final TextView userFaculty = (TextView) view.findViewById(R.id.userFaculty);
                final TextView userMatric = (TextView) view.findViewById(R.id.userMatric);
                final TextView userName = (TextView) view.findViewById(R.id.userName);
                final CircleImageView userImage = (CircleImageView) view.findViewById(R.id.userImage);

                users.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            UserInfo userInfo = snapshot.getValue(UserInfo.class);
                            if (userInfo != null) {
                                if (userInfo.MatricNumber.equals(matric)) {
                                    userFaculty.setText(userInfo.Faculty);
                                    userMatric.setText(userInfo.MatricNumber);
                                    userName.setText(userInfo.Name);
                                    Picasso.get().load(userInfo.ProfilePicture).placeholder(R.drawable.user_placeholder).into(userImage);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(StatusAppointmentActivity.this, "Failed to Connect Database", Toast.LENGTH_LONG).show();
                    }
                });

                AlertDialog.Builder dialog = new AlertDialog.Builder(StatusAppointmentActivity.this);
                dialog.setView(view);
                dialog.show();
            }
        });

        approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                approve.setEnabled(false);
                reject.setEnabled(false);

                updateMap.put("Sequence", String.valueOf(System.currentTimeMillis()));
                updateMap.put("Status", "Approved");

                progressDialog = ProgressDialog.show(new ContextThemeWrapper(StatusAppointmentActivity.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert), "", "Please Wait...", true, false);

                updateAppointment.child(key).updateChildren(updateMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isComplete()) {
                            progressDialog.dismiss();
                            sendNotification(token, "Your appointment on " + datetime + " has been approved", "Appointment Approved");
                            finish();
                        } else {
                            approve.setEnabled(true);
                            reject.setEnabled(true);
                            progressDialog.dismiss();
                            Toast.makeText(StatusAppointmentActivity.this, "Failed to Connect Database", Toast.LENGTH_LONG).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        approve.setEnabled(true);
                        reject.setEnabled(true);
                        progressDialog.dismiss();
                        Toast.makeText(StatusAppointmentActivity.this, "Failed to Connect Database", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                approve.setEnabled(false);
                reject.setEnabled(false);

                progressDialog = ProgressDialog.show(new ContextThemeWrapper(StatusAppointmentActivity.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert), "", "Please Wait...", true, false);

                updateAppointment.child(key).child("Status").setValue("Rejected").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isComplete()) {
                            progressDialog.dismiss();
                            sendNotification(token, "Your appointment on " + datetime + " has been rejected", "Appointment Rejected");
                            finish();
                        } else {
                            approve.setEnabled(true);
                            reject.setEnabled(true);
                            progressDialog.dismiss();
                            Toast.makeText(StatusAppointmentActivity.this, "Failed to Connect Database", Toast.LENGTH_LONG).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        approve.setEnabled(true);
                        reject.setEnabled(true);
                        progressDialog.dismiss();
                        Toast.makeText(StatusAppointmentActivity.this, "Failed to Connect Database", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    public static void sendNotification(final String token, final String body, final String title) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    OkHttpClient client = new OkHttpClient();
                    JSONObject json = new JSONObject();
                    JSONObject dataJson = new JSONObject();
                    dataJson.put("text", body);
                    dataJson.put("title", title);
                    dataJson.put("priority", "high");
                    json.put("notification", dataJson);
                    json.put("to", token);
                    final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                    RequestBody body = RequestBody.create(JSON, json.toString());
                    Request request = new Request.Builder()
                            .header("Authorization", "key=AAAA9VP0Udc:APA91bH24VSHEpj_gw-KP8CrAdeEJE2TrCbKtbNHObRG0S6XFDDwdg6hhninpE55mKdnPBbQpnyYY0iq5sdXk6vk9MRHCX_3ZWB0Ca0ZIDkZVps_GEYq_5WUn9_dXVOUDSbASJi65-c_")
                            .url("https://fcm.googleapis.com/fcm/send")
                            .post(body)
                            .build();
                    Response response = client.newCall(request).execute();
                    String finalResponse = response.body().string();
                    Log.i("xxx", finalResponse);
                } catch (Exception e) {
                    Log.i("yyy",e.getMessage());
                }
                return null;
            }
        }.execute();
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
