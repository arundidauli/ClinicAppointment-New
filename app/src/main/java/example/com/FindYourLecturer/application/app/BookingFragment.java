package example.com.FindYourLecturer.application.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import example.com.FindYourLecturer.R;

public class BookingFragment extends Fragment {

    private View view;
    private Button newAppointment;
    private Button upcomingAppointment;
    private Button passAppointment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (view != null) {
            return view;
        } else {
            view = inflater.inflate(R.layout.fragment_booking, container, false);

            newAppointment = (Button) view.findViewById(R.id.newAppointment);

            newAppointment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), NewBookingActivity.class);
                    startActivity(intent);
                }
            });

            upcomingAppointment = (Button) view.findViewById(R.id.upcomingAppointment);

            upcomingAppointment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), UpcomingAppointmentActivity.class);
                    startActivity(intent);
                }
            });

            passAppointment = (Button) view.findViewById(R.id.passAppointment);

            passAppointment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), PassAppointmentActivity.class);
                    startActivity(intent);
                }
            });

            return view;
        }
    }
}
