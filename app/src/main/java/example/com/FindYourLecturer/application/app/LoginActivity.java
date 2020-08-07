package example.com.FindYourLecturer.application.app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import example.com.FindYourLecturer.R;

public class LoginActivity extends AppCompatActivity{

    private TextView needHelp;
    private EditText emailLogin;
    private EditText passwordLogin;
    private Button signInButton;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            if (firebaseAuth.getCurrentUser().getUid().equals("g9OGld7Ya9OceQDtOKa6mCWwNKu1")) {
                startActivity(new Intent(LoginActivity.this, AdminPanelActivity.class));
                finish();
            } else {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }
        }

        setContentView(R.layout.activity_login);

        isNetworkConnectionAvailable();

        emailLogin = (EditText) findViewById(R.id.emailLogin);
        passwordLogin = (EditText) findViewById(R.id.passwordLogin);
        signInButton = (Button) findViewById(R.id.signInButton);
        needHelp = (TextView) findViewById(R.id.needHelp);
        progressDialog = new ProgressDialog(this);

        needHelp.setPaintFlags(needHelp.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        needHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, GetHelpActivity.class);
                startActivity(intent);
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();

     /*   passwordLogin.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    passwordLogin.clearFocus();
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(passwordLogin.getWindowToken(), 0);
                }
                return false;
            }
        });*/

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog.show();
                progressDialog.setMessage("Please wait...");
                if (emailLogin.getText().toString().trim().equals("admin")) {
                    email = emailLogin.getText().toString().trim() + "@unimas.my";
                } else {
                    email = emailLogin.getText().toString().trim() + "@siswa.unimas.my";
                }
                password = passwordLogin.getText().toString().trim();

                if (TextUtils.isEmpty(emailLogin.getText().toString().trim())) {
                    Toast.makeText(getApplicationContext(), "Email address cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Password cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }


                emailLogin.setEnabled(false);
                passwordLogin.setEnabled(false);
                signInButton.setEnabled(false);


                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressDialog.dismiss();
                                if (!task.isSuccessful()) {
                                    emailLogin.setEnabled(true);
                                    passwordLogin.setEnabled(true);
                                    signInButton.setEnabled(true);
                                    Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                } else {
                                    if (email.equalsIgnoreCase("admin@unimas.my") && password.equalsIgnoreCase("admin123")) {
                                        progressDialog.dismiss();

                                        Intent intent = new Intent(LoginActivity.this, AdminPanelActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        progressDialog.dismiss();

                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            }
                        });
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
                    ((InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((this.getWindow().getDecorView().getApplicationWindowToken()), 0);
                } }
        return super.dispatchTouchEvent(ev);
    }

    /////Dear net is too slow i awnt to help you but how?
    //You can contact me any time just search on google Arun android. can you help me please i will change my line
    //of course i will help you if i feel free. right now ? atlease can you help me to settle my login please
    //Of course dear i want to help you how can i do that Net is too slow :(:( my line is slow or your line slow ? if me i will change
    //Dear when you have proper connection you can contactr me. last but not least, try this network if slow i will contact you later
//is it my line still slow :( yes pleaseeeee login only :(( okk dear tqqqqq :)


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
}
