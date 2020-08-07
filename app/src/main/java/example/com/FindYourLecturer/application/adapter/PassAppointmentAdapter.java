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

public class PassAppointmentAdapter extends BaseAdapter {

    private ArrayList<AppointmentInfo> passAppointmentList;
    private Context context;
    private LayoutInflater layoutInflater;
    private Animation animation;

    public PassAppointmentAdapter(Context context, ArrayList<AppointmentInfo> passAppointmentList) {
        this.context = context;
        this.passAppointmentList = passAppointmentList;
        layoutInflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return passAppointmentList.size();
    }

    @Override
    public Object getItem(int position) {
        return passAppointmentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_passappointment, null);
        }

        animation = AnimationUtils.loadAnimation(context, R.anim.listview_animation);

        RelativeLayout itemPass = (RelativeLayout) convertView.findViewById(R.id.itemPassAppointment);
        TextView patientSequence = (TextView) convertView.findViewById(R.id.patientSequence1);
        TextView bookedOn = (TextView) convertView.findViewById(R.id.bookedOn1);
        TextView appointmentDateTime = (TextView) convertView.findViewById(R.id.appointmentDateTime1);
        TextView appointmentStatus = (TextView) convertView.findViewById(R.id.appointmentStatus1);

        if (passAppointmentList.size() != 0) {
            patientSequence.setText(passAppointmentList.get(position).Sequence);
            bookedOn.setText(passAppointmentList.get(position).Booked);
            appointmentDateTime.setText(passAppointmentList.get(position).DateTime);
            appointmentStatus.setText(passAppointmentList.get(position).Status);

            if (passAppointmentList.get(position).Status.equalsIgnoreCase("Pending")) {
                appointmentStatus.setTextColor(context.getResources().getColor(R.color.colorGrey));
            } else if (passAppointmentList.get(position).Status.equalsIgnoreCase("Approved")) {
                appointmentStatus.setTextColor(context.getResources().getColor(R.color.colorGreen));
            } else if (passAppointmentList.get(position).Status.equalsIgnoreCase("Rejected")) {
                appointmentStatus.setTextColor(context.getResources().getColor(R.color.colorRed));
            }
        }

        itemPass.startAnimation(animation);

        return convertView;
    }
}