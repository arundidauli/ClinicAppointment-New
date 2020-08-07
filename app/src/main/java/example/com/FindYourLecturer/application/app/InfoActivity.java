package example.com.FindYourLecturer.application.app;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import example.com.FindYourLecturer.R;

public class InfoActivity extends AppCompatActivity{

    private String infoDescription;
    private String infoImage;
    private String infoTitle;
    private TextView descriptionInfo;
    private ImageView imageInfo;
    private TextView titleInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Information");

        descriptionInfo = (TextView) findViewById(R.id.infoDescriptionActivity);
        imageInfo = (ImageView) findViewById(R.id.infoImageActivity);
        titleInfo = (TextView) findViewById(R.id.infoTitleActivity);

        if (getIntent() != null && getIntent().getExtras() != null) {
            if (getIntent().getStringExtra("infoDescription") != null) {
                infoDescription = getIntent().getStringExtra("infoDescription");
                descriptionInfo.setText(infoDescription);
            }
            if (getIntent().getStringExtra("infoImage") != null) {
                infoImage = getIntent().getStringExtra("infoImage");
                Picasso.get().load(infoImage).placeholder(R.drawable.image_not_found).into(imageInfo);
            }
            if (getIntent().getStringExtra("infoTitle") != null) {
                infoTitle = getIntent().getStringExtra("infoTitle");
                titleInfo.setText(infoTitle);
            }
        }

        imageInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(InfoActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.item_expand, null);
                PhotoView photoView = mView.findViewById(R.id.expandImage);
                Picasso.get().load(infoImage).placeholder(R.drawable.image_not_found).into(photoView);
                mBuilder.setView(mView);
                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
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
