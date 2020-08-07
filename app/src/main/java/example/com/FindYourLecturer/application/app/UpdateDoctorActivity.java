package example.com.FindYourLecturer.application.app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import example.com.FindYourLecturer.R;

public class UpdateDoctorActivity extends AppCompatActivity{

    private CircleImageView imageInfo;
    private EditText imageLink;
    private Button imageCheck;
    private EditText doctorID;
    private EditText doctorName;
    private Button addButton;
    private Button updateButton;
    private Button deleteButton;
    private Button generateID;

    private String id;
    private String image;
    private String key;
    private String name;
    private Boolean action;
    private int size;

    private String uniqueKey;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference doctor;

    private HashMap<String, String> newMap = new HashMap<String, String>();
    private HashMap<String, Object> updateMap = new HashMap<String, Object>();

    private ProgressDialog progressDialogAdd;
    private ProgressDialog progressDialogDelete;
    private ProgressDialog progressDialogUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updatedoctor);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Doctor Details");

        firebaseDatabase = FirebaseDatabase.getInstance();
        doctor = firebaseDatabase.getReference("Doctor");

        imageInfo = (CircleImageView) findViewById(R.id.updateImage);
        imageLink = (EditText) findViewById(R.id.updateImageLink);
        imageCheck = (Button) findViewById(R.id.checkImage);
        doctorID = (EditText) findViewById(R.id.updateDoctor);
        doctorName = (EditText) findViewById(R.id.updateName);
        addButton = (Button) findViewById(R.id.addButton);
        updateButton = (Button) findViewById(R.id.updateButton);
        deleteButton = (Button) findViewById(R.id.deleteButton);
        generateID = (Button) findViewById(R.id.generateID);

        if (getIntent() != null && getIntent().getExtras() != null) {
            if (getIntent().getStringExtra("doctorImage") != null) {
                image = getIntent().getStringExtra("doctorImage");
                imageLink.setText(image);
                Picasso.get().load(image).placeholder(R.drawable.user_placeholder).into(imageInfo);
            }
            if (getIntent().getStringExtra("doctorID") != null) {
                id = getIntent().getStringExtra("doctorID");
                doctorID.setText(id);
            }
            if (getIntent().getStringExtra("doctorName") != null) {
                name = getIntent().getStringExtra("doctorName");
                doctorName.setText(name);
            }
            if (getIntent().getStringExtra("doctorKey") != null) {
                key = getIntent().getStringExtra("doctorKey");
            }

            action = getIntent().getExtras().getBoolean("doctorAction");

            if (!action) {
                updateButton.setVisibility(View.GONE);
                deleteButton.setVisibility(View.GONE);
            } else {
                addButton.setVisibility(View.GONE);
                generateID.setVisibility(View.GONE);
            }

            if (getIntent().getExtras().getInt("doctorSize") != 0) {
                size = getIntent().getExtras().getInt("doctorSize");
            }
        }

        imageCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageLink.getText().toString().trim().length() > 0) {
                    Picasso.get().load(imageLink.getText().toString().trim()).placeholder(R.drawable.user_placeholder).into(imageInfo);
                }
            }
        });

        generateID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random random = new Random();
                int randomNum = random.nextInt(900) + 100;
                String randomNumber = String.valueOf(randomNum);
                String currentTime = Long.toString(System.currentTimeMillis()).substring(Long.toString(System.currentTimeMillis()).length() - 3);
                doctorID.setText("D" + randomNumber + currentTime);
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageLink.setEnabled(false);
                doctorName.setEnabled(false);
                imageCheck.setEnabled(false);
                addButton.setEnabled(false);

                uniqueKey = FirebaseDatabase.getInstance().getReference("Doctor").push().getKey();

                if (imageLink.getText().toString().trim().length() > 0) {
                    newMap.put("DoctorImage", imageLink.getText().toString().trim());
                } else {
                    Toast.makeText(UpdateDoctorActivity.this, "Please Enter Image Link", Toast.LENGTH_LONG).show();
                    imageLink.setEnabled(true);
                    doctorName.setEnabled(true);
                    imageCheck.setEnabled(true);
                    addButton.setEnabled(true);
                    return;
                }

                if (doctorName.getText().toString().trim().length() > 0) {
                    newMap.put("DoctorName", doctorName.getText().toString().trim());
                } else {
                    Toast.makeText(UpdateDoctorActivity.this, "Please Enter Doctor Name", Toast.LENGTH_LONG).show();
                    imageLink.setEnabled(true);
                    doctorName.setEnabled(true);
                    imageCheck.setEnabled(true);
                    addButton.setEnabled(true);
                    return;
                }

                if (doctorID.getText().toString().trim().length() > 0) {
                    newMap.put("DoctorID", doctorID.getText().toString().trim());
                } else {
                    Toast.makeText(UpdateDoctorActivity.this, "Please Generate Doctor ID", Toast.LENGTH_LONG).show();
                    imageLink.setEnabled(true);
                    doctorName.setEnabled(true);
                    imageCheck.setEnabled(true);
                    addButton.setEnabled(true);
                    return;
                }

                newMap.put("DoctorKey", uniqueKey);

                addNewInfo();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageLink.setEnabled(false);
                doctorName.setEnabled(false);
                imageCheck.setEnabled(false);
                updateButton.setEnabled(false);
                deleteButton.setEnabled(false);

                if (size > 1) {

                    progressDialogDelete = ProgressDialog.show(new ContextThemeWrapper(UpdateDoctorActivity.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert), "", "Please Wait...", true, false);

                    doctor.child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isComplete()) {
                                progressDialogDelete.dismiss();
                                finish();
                            } else {
                                imageLink.setEnabled(true);
                                doctorName.setEnabled(true);
                                imageCheck.setEnabled(true);
                                updateButton.setEnabled(true);
                                deleteButton.setEnabled(true);
                                progressDialogDelete.dismiss();
                                Toast.makeText(UpdateDoctorActivity.this, "Failed to Connect Database", Toast.LENGTH_LONG).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            imageLink.setEnabled(true);
                            doctorName.setEnabled(true);
                            imageCheck.setEnabled(true);
                            updateButton.setEnabled(true);
                            deleteButton.setEnabled(true);
                            progressDialogDelete.dismiss();
                            Toast.makeText(UpdateDoctorActivity.this, "Failed to Connect Database", Toast.LENGTH_LONG).show();
                        }
                    });
                } else if (size == 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(UpdateDoctorActivity.this);
                    builder.setTitle("Cannot Delete");
                    builder.setMessage("At least one doctor should be kept inside the database!");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            imageLink.setEnabled(true);
                            doctorName.setEnabled(true);
                            imageCheck.setEnabled(true);
                            updateButton.setEnabled(true);
                            deleteButton.setEnabled(true);
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageLink.setEnabled(false);
                doctorName.setEnabled(false);
                imageCheck.setEnabled(false);
                updateButton.setEnabled(false);
                deleteButton.setEnabled(false);

                if (imageLink.getText().toString().trim().length() > 0) {
                    updateMap.put("DoctorImage", imageLink.getText().toString().trim());
                } else {
                    Toast.makeText(UpdateDoctorActivity.this, "Please Enter Image Link", Toast.LENGTH_LONG).show();
                    imageLink.setEnabled(true);
                    doctorName.setEnabled(true);
                    imageCheck.setEnabled(true);
                    updateButton.setEnabled(true);
                    deleteButton.setEnabled(true);
                    return;
                }

                if (doctorName.getText().toString().trim().length() > 0) {
                    updateMap.put("DoctorName", doctorName.getText().toString().trim());
                } else {
                    Toast.makeText(UpdateDoctorActivity.this, "Please Enter Doctor Name", Toast.LENGTH_LONG).show();
                    imageLink.setEnabled(true);
                    doctorName.setEnabled(true);
                    imageCheck.setEnabled(true);
                    updateButton.setEnabled(true);
                    deleteButton.setEnabled(true);
                    return;
                }

                if (doctorID.getText().toString().trim().length() > 0) {
                    updateMap.put("DoctorID", doctorID.getText().toString().trim());
                } else {
                    Toast.makeText(UpdateDoctorActivity.this, "Please Generate Doctor ID", Toast.LENGTH_LONG).show();
                    imageLink.setEnabled(true);
                    doctorName.setEnabled(true);
                    imageCheck.setEnabled(true);
                    updateButton.setEnabled(true);
                    deleteButton.setEnabled(true);
                    return;
                }

                updateInfo();
            }
        });
    }

    public void addNewInfo() {

        progressDialogAdd = ProgressDialog.show(new ContextThemeWrapper(UpdateDoctorActivity.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert), "", "Please Wait...", true, false);

        doctor.child(uniqueKey).setValue(newMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isComplete()) {
                    progressDialogAdd.dismiss();
                    finish();
                } else {
                    imageLink.setEnabled(true);
                    doctorName.setEnabled(true);
                    imageCheck.setEnabled(true);
                    addButton.setEnabled(true);
                    progressDialogAdd.dismiss();
                    Toast.makeText(UpdateDoctorActivity.this, "Failed to Connect Database", Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                imageLink.setEnabled(true);
                doctorName.setEnabled(true);
                imageCheck.setEnabled(true);
                addButton.setEnabled(true);
                progressDialogAdd.dismiss();
                Toast.makeText(UpdateDoctorActivity.this, "Failed to Connect Database", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void updateInfo() {

        progressDialogUpdate = ProgressDialog.show(new ContextThemeWrapper(UpdateDoctorActivity.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert), "", "Please Wait...", true, false);

        doctor.child(key).updateChildren(updateMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isComplete()) {
                    progressDialogUpdate.dismiss();
                    finish();
                } else {
                    imageLink.setEnabled(true);
                    doctorName.setEnabled(true);
                    imageCheck.setEnabled(true);
                    updateButton.setEnabled(true);
                    deleteButton.setEnabled(true);
                    progressDialogUpdate.dismiss();
                    Toast.makeText(UpdateDoctorActivity.this, "Failed to Connect Database", Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                imageLink.setEnabled(true);
                doctorName.setEnabled(true);
                imageCheck.setEnabled(true);
                updateButton.setEnabled(true);
                deleteButton.setEnabled(true);
                progressDialogUpdate.dismiss();
                Toast.makeText(UpdateDoctorActivity.this, "Failed to Connect Database", Toast.LENGTH_LONG).show();
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
