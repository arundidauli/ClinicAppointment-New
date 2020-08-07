package example.com.FindYourLecturer.application.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import example.com.FindYourLecturer.R;
import example.com.FindYourLecturer.application.model.AppointmentInfo;

public class UpcomingAppointmentAdapter extends BaseAdapter {

    private ArrayList<AppointmentInfo> upcomingAppointmentList;
    private Context context;
    private LayoutInflater layoutInflater;
    private Animation animation;

    public UpcomingAppointmentAdapter(Context context, ArrayList<AppointmentInfo> upcomingAppointmentList) {
        this.context = context;
        this.upcomingAppointmentList = upcomingAppointmentList;
        layoutInflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return upcomingAppointmentList.size();
    }

    @Override
    public Object getItem(int position) {
        return upcomingAppointmentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_upcomingappointment, null);
        }

        animation = AnimationUtils.loadAnimation(context, R.anim.listview_animation);

        RelativeLayout itemUpcoming = (RelativeLayout) convertView.findViewById(R.id.itemUpcomingAppointment);
        TextView patientSequence = (TextView) convertView.findViewById(R.id.patientSequence);
        TextView bookedOn = (TextView) convertView.findViewById(R.id.bookedOn);
        TextView appointmentDateTime = (TextView) convertView.findViewById(R.id.appointmentDateTime);
        TextView appointmentStatus = (TextView) convertView.findViewById(R.id.appointmentStatus);

        if (upcomingAppointmentList.size() != 0) {
            patientSequence.setText(upcomingAppointmentList.get(position).Sequence);
            bookedOn.setText(upcomingAppointmentList.get(position).Booked);
            appointmentDateTime.setText(upcomingAppointmentList.get(position).DateTime);
            appointmentStatus.setText(upcomingAppointmentList.get(position).Status);

            if (upcomingAppointmentList.get(position).Status.equalsIgnoreCase("Pending")) {
                appointmentStatus.setTextColor(context.getResources().getColor(R.color.colorGrey));
            } else if (upcomingAppointmentList.get(position).Status.equalsIgnoreCase("Approved")) {
                appointmentStatus.setTextColor(context.getResources().getColor(R.color.colorGreen));
            } else if (upcomingAppointmentList.get(position).Status.equalsIgnoreCase("Rejected")) {
                appointmentStatus.setTextColor(context.getResources().getColor(R.color.colorRed));
            }
        }

        itemUpcoming.startAnimation(animation);

        return convertView;
    }
}