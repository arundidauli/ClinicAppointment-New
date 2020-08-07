package example.com.FindYourLecturer.application.app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import example.com.FindYourLecturer.R;

public class ClinicFragment extends Fragment {

    private View view;
    private Button viewOperatingHour;
    private RelativeLayout emergencyArea;
    private TextView emergencyContact;
    private Button viewDoctor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (view != null) {
            return view;
        } else {
            view = inflater.inflate(R.layout.fragment_clinic, container, false);

            viewOperatingHour = (Button) view.findViewById(R.id.timeTable);
            emergencyArea = (RelativeLayout) view.findViewById(R.id.emergencyArea);
            emergencyContact = (TextView) view.findViewById(R.id.emergencyNumber);
            viewDoctor = (Button) view.findViewById(R.id.doctorButton);

            emergencyArea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE},1);
                    }
                    else
                    {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + emergencyContact.getText().toString()));
                        startActivity(callIntent);
                    }
                }
            });

            viewOperatingHour.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setData(Uri.parse("https://wiki.unimas.my/unimaswiki/bin/view/About+UNIMAS/Health+Centre"));
                    startActivity(intent);
                }
            });

            viewDoctor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), DoctorActivity.class);
                    startActivity(intent);
                }
            });

            return view;
        }
    }
}
