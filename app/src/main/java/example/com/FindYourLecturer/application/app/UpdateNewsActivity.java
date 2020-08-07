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
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import example.com.FindYourLecturer.R;

public class UpdateNewsActivity extends AppCompatActivity{

    private ImageView imageInfo;
    private EditText imageLink;
    private Button imageCheck;
    private EditText titleInfo;
    private EditText descriptionInfo;
    private Button addButton;
    private Button updateButton;
    private Button deleteButton;

    private String description;
    private String image;
    private String key;
    private String title;
    private Boolean action;
    private int size;

    private String uniqueKey;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference info;

    private HashMap<String, String> newMap = new HashMap<String, String>();
    private HashMap<String, Object> updateMap = new HashMap<String, Object>();

    private ProgressDialog progressDialogAdd;
    private ProgressDialog progressDialogDelete;
    private ProgressDialog progressDialogUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updatenews);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Info Details");

        firebaseDatabase = FirebaseDatabase.getInstance();
        info = firebaseDatabase.getReference("Info");

        imageInfo = (ImageView) findViewById(R.id.updateImage);
        imageLink = (EditText) findViewById(R.id.updateImageLink);
        imageCheck = (Button) findViewById(R.id.checkImage);
        titleInfo = (EditText) findViewById(R.id.updateTitle);
        descriptionInfo = (EditText) findViewById(R.id.updateDetail);
        addButton = (Button) findViewById(R.id.addButton);
        updateButton = (Button) findViewById(R.id.updateButton);
        deleteButton = (Button) findViewById(R.id.deleteButton);

        if (getIntent() != null && getIntent().getExtras() != null) {
            if (getIntent().getStringExtra("image") != null) {
                image = getIntent().getStringExtra("image");
                imageLink.setText(image);
                Picasso.get().load(image).placeholder(R.drawable.image_not_found).into(imageInfo);
            }
            if (getIntent().getStringExtra("title") != null) {
                title = getIntent().getStringExtra("title");
                titleInfo.setText(title);
            }
            if (getIntent().getStringExtra("description") != null) {
                description = getIntent().getStringExtra("description");
                descriptionInfo.setText(description);
            }
            if (getIntent().getStringExtra("key") != null) {
                key = getIntent().getStringExtra("key");
            }

            action = getIntent().getExtras().getBoolean("action");

            if (!action) {
                updateButton.setVisibility(View.GONE);
                deleteButton.setVisibility(View.GONE);
            } else {
                addButton.setVisibility(View.GONE);
            }

            if (getIntent().getExtras().getInt("size") != 0) {
                size = getIntent().getExtras().getInt("size");
            }
        }

        imageCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageLink.getText().toString().trim().length() > 0) {
                    Picasso.get().load(imageLink.getText().toString().trim()).placeholder(R.drawable.image_not_found).into(imageInfo);
                }
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageLink.setEnabled(false);
                titleInfo.setEnabled(false);
                descriptionInfo.setEnabled(false);
                imageCheck.setEnabled(false);
                addButton.setEnabled(false);

                uniqueKey = FirebaseDatabase.getInstance().getReference("Info").push().getKey();

                if (imageLink.getText().toString().trim().length() > 0) {
                    newMap.put("Image", imageLink.getText().toString().trim());
                } else {
                    Toast.makeText(UpdateNewsActivity.this, "Please Enter Image Link", Toast.LENGTH_LONG).show();
                    imageLink.setEnabled(true);
                    titleInfo.setEnabled(true);
                    descriptionInfo.setEnabled(true);
                    imageCheck.setEnabled(true);
                    addButton.setEnabled(true);
                    return;
                }

                if (titleInfo.getText().toString().trim().length() > 0) {
                    newMap.put("Title", titleInfo.getText().toString().trim());
                } else {
                    Toast.makeText(UpdateNewsActivity.this, "Please Enter Title", Toast.LENGTH_LONG).show();
                    imageLink.setEnabled(true);
                    titleInfo.setEnabled(true);
                    descriptionInfo.setEnabled(true);
                    imageCheck.setEnabled(true);
                    addButton.setEnabled(true);
                    return;
                }

                if (descriptionInfo.getText().toString().trim().length() > 0) {
                    newMap.put("Description", descriptionInfo.getText().toString().trim());
                } else {
                    Toast.makeText(UpdateNewsActivity.this, "Please Enter Description", Toast.LENGTH_LONG).show();
                    imageLink.setEnabled(true);
                    titleInfo.setEnabled(true);
                    descriptionInfo.setEnabled(true);
                    imageCheck.setEnabled(true);
                    addButton.setEnabled(true);
                    return;
                }

                newMap.put("Key", uniqueKey);
                newMap.put("Time", String.valueOf(System.currentTimeMillis()));

                addNewInfo();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageLink.setEnabled(false);
                titleInfo.setEnabled(false);
                descriptionInfo.setEnabled(false);
                imageCheck.setEnabled(false);
                updateButton.setEnabled(false);
                deleteButton.setEnabled(false);

                if (size > 1) {

                    progressDialogDelete = ProgressDialog.show(new ContextThemeWrapper(UpdateNewsActivity.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert), "", "Please Wait...", true, false);

                    info.child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isComplete()) {
                                progressDialogDelete.dismiss();
                                finish();
                            } else {
                                imageLink.setEnabled(true);
                                titleInfo.setEnabled(true);
                                descriptionInfo.setEnabled(true);
                                imageCheck.setEnabled(true);
                                updateButton.setEnabled(true);
                                deleteButton.setEnabled(true);
                                progressDialogDelete.dismiss();
                                Toast.makeText(UpdateNewsActivity.this, "Failed to Connect Database", Toast.LENGTH_LONG).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            imageLink.setEnabled(true);
                            titleInfo.setEnabled(true);
                            descriptionInfo.setEnabled(true);
                            imageCheck.setEnabled(true);
                            updateButton.setEnabled(true);
                            deleteButton.setEnabled(true);
                            progressDialogDelete.dismiss();
                            Toast.makeText(UpdateNewsActivity.this, "Failed to Connect Database", Toast.LENGTH_LONG).show();
                        }
                    });
                } else if (size == 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(UpdateNewsActivity.this);
                    builder.setTitle("Cannot Delete");
                    builder.setMessage("At least one information should be kept inside the database!");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            imageLink.setEnabled(true);
                            titleInfo.setEnabled(true);
                            descriptionInfo.setEnabled(true);
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
                titleInfo.setEnabled(false);
                descriptionInfo.setEnabled(false);
                imageCheck.setEnabled(false);
                updateButton.setEnabled(false);
                deleteButton.setEnabled(false);

                if (imageLink.getText().toString().trim().length() > 0) {
                    updateMap.put("Image", imageLink.getText().toString().trim());
                } else {
                    Toast.makeText(UpdateNewsActivity.this, "Please Enter Image Link", Toast.LENGTH_LONG).show();
                    imageLink.setEnabled(true);
                    titleInfo.setEnabled(true);
                    descriptionInfo.setEnabled(true);
                    imageCheck.setEnabled(true);
                    updateButton.setEnabled(true);
                    deleteButton.setEnabled(true);
                    return;
                }

                if (titleInfo.getText().toString().trim().length() > 0) {
                    updateMap.put("Title", titleInfo.getText().toString().trim());
                } else {
                    Toast.makeText(UpdateNewsActivity.this, "Please Enter Title", Toast.LENGTH_LONG).show();
                    imageLink.setEnabled(true);
                    titleInfo.setEnabled(true);
                    descriptionInfo.setEnabled(true);
                    imageCheck.setEnabled(true);
                    updateButton.setEnabled(true);
                    deleteButton.setEnabled(true);
                    return;
                }

                if (descriptionInfo.getText().toString().trim().length() > 0) {
                    updateMap.put("Description", descriptionInfo.getText().toString().trim());
                } else {
                    Toast.makeText(UpdateNewsActivity.this, "Please Enter Description", Toast.LENGTH_LONG).show();
                    imageLink.setEnabled(true);
                    titleInfo.setEnabled(true);
                    descriptionInfo.setEnabled(true);
                    imageCheck.setEnabled(true);
                    updateButton.setEnabled(true);
                    deleteButton.setEnabled(true);
                    return;
                }

                updateMap.put("Time", String.valueOf(System.currentTimeMillis()));

                updateInfo();
            }
        });
    }

    public void addNewInfo() {

        progressDialogAdd = ProgressDialog.show(new ContextThemeWrapper(UpdateNewsActivity.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert), "", "Please Wait...", true, false);

        info.child(uniqueKey).setValue(newMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isComplete()) {
                    progressDialogAdd.dismiss();
                    finish();
                } else {
                    imageLink.setEnabled(true);
                    titleInfo.setEnabled(true);
                    descriptionInfo.setEnabled(true);
                    imageCheck.setEnabled(true);
                    addButton.setEnabled(true);
                    progressDialogAdd.dismiss();
                    Toast.makeText(UpdateNewsActivity.this, "Failed to Connect Database", Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                imageLink.setEnabled(true);
                titleInfo.setEnabled(true);
                descriptionInfo.setEnabled(true);
                imageCheck.setEnabled(true);
                addButton.setEnabled(true);
                progressDialogAdd.dismiss();
                Toast.makeText(UpdateNewsActivity.this, "Failed to Connect Database", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void updateInfo() {

        progressDialogUpdate = ProgressDialog.show(new ContextThemeWrapper(UpdateNewsActivity.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert), "", "Please Wait...", true, false);

        info.child(key).updateChildren(updateMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isComplete()) {
                    progressDialogUpdate.dismiss();
                    finish();
                } else {
                    imageLink.setEnabled(true);
                    titleInfo.setEnabled(true);
                    descriptionInfo.setEnabled(true);
                    imageCheck.setEnabled(true);
                    updateButton.setEnabled(true);
                    deleteButton.setEnabled(true);
                    progressDialogUpdate.dismiss();
                    Toast.makeText(UpdateNewsActivity.this, "Failed to Connect Database", Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                imageLink.setEnabled(true);
                titleInfo.setEnabled(true);
                descriptionInfo.setEnabled(true);
                imageCheck.setEnabled(true);
                updateButton.setEnabled(true);
                deleteButton.setEnabled(true);
                progressDialogUpdate.dismiss();
                Toast.makeText(UpdateNewsActivity.this, "Failed to Connect Database", Toast.LENGTH_LONG).show();
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
