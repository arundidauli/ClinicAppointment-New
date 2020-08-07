package example.com.FindYourLecturer.application.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import example.com.FindYourLecturer.R;
import example.com.FindYourLecturer.application.adapter.MainActivityFragmentAdapter;
import example.com.FindYourLecturer.application.model.UserInfo;

public class MainActivity extends AppCompatActivity {

    private ViewPager mainViewPager;
    private View view;
    private Drawer drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isNetworkConnectionAvailable();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        view = getLayoutInflater().inflate(R.layout.drawer_user, null);

        loadDrawer();

        drawer = new DrawerBuilder()
                .withActivity(this)
                .withHeader(view)
                .withToolbar(toolbar)
                .withDisplayBelowStatusBar(false)
                .withTranslucentStatusBar(false)
                .withActionBarDrawerToggleAnimated(true)
                .withHeaderDivider(false)
                .build();

        mainViewPager = (ViewPager) findViewById(R.id.container);
        MainActivityFragmentAdapter mainActivityFragmentAdapter = new MainActivityFragmentAdapter(getSupportFragmentManager());
        mainActivityFragmentAdapter.addFragment(new InfoFragment(), "Tab1");
        mainActivityFragmentAdapter.addFragment(new BookingFragment(), "Tab2");
        mainActivityFragmentAdapter.addFragment(new ClinicFragment(), "Tab3");
        mainViewPager.setAdapter(mainActivityFragmentAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mainViewPager);

        tabLayout.getTabAt(0).setText("Info");
        tabLayout.getTabAt(1).setText("Booking");
        tabLayout.getTabAt(2).setText("Clinic");
    }

    public void loadDrawer() {

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference users = firebaseDatabase.getReference("Users");

        final String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Button getHelp = (Button) view.findViewById(R.id.getHelpButton);
        Button signOutButton = (Button) view.findViewById(R.id.signOutButton);
        final TextView userFaculty = (TextView) view.findViewById(R.id.userFaculty);
        final TextView userMatric = (TextView) view.findViewById(R.id.userMatric);
        final TextView userName = (TextView) view.findViewById(R.id.userName);
        final CircleImageView userImage = (CircleImageView) view.findViewById(R.id.userImage);

        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserInfo userInfo = dataSnapshot.child(currentUserID).getValue(UserInfo.class);
                if (userInfo != null) {
                    userFaculty.setText(userInfo.Faculty);
                    userMatric.setText(userInfo.MatricNumber);
                    userName.setText(userInfo.Name);
                    Picasso.get().load(userInfo.ProfilePicture).placeholder(R.drawable.user_placeholder).into(userImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Failed to Connect Database", Toast.LENGTH_LONG).show();
            }
        });

        getHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GetHelpActivity.class);
                startActivity(intent);
            }
        });

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Confirm to sign out from the application?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    public void checkNetworkConnection() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Internet Connection");
        builder.setMessage("Please turn on internet connection to continue");
        builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isNetworkConnectionAvailable();
            }
        });
        builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    public boolean isNetworkConnectionAvailable() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnected();
        if (isConnected) {
            return true;
        } else {
            checkNetworkConnection();
            return false;
        }
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen()) {
            drawer.closeDrawer();
        } else {
            finish();
        }
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
}
