package example.com.FindYourLecturer.application.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import example.com.FindYourLecturer.R;
import example.com.FindYourLecturer.application.model.DoctorInfo;

public class DoctorAdapter extends BaseAdapter{

    private ArrayList<DoctorInfo> doctorInfoList;
    private Context context;
    private LayoutInflater layoutInflater;

    public DoctorAdapter(Context context, ArrayList<DoctorInfo> doctorInfoList) {
        this.context = context;
        this.doctorInfoList = doctorInfoList;
        layoutInflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return doctorInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return doctorInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_doctor, null);
        }

        TextView idDoctor = (TextView) convertView.findViewById(R.id.doctorID);
        CircleImageView imageDoctor = (CircleImageView) convertView.findViewById(R.id.doctorImage);
        TextView nameDoctor = (TextView) convertView.findViewById(R.id.doctorName);

        if (doctorInfoList.size() != 0) {
            idDoctor.setText(doctorInfoList.get(position).DoctorID);
            Picasso.get().load(doctorInfoList.get(position).DoctorImage).placeholder(R.drawable.user_placeholder).into(imageDoctor);
            nameDoctor.setText(doctorInfoList.get(position).DoctorName);
        }

        return convertView;
    }
}
