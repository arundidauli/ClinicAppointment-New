package example.com.FindYourLecturer.application.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import example.com.FindYourLecturer.R;
import example.com.FindYourLecturer.application.model.LatestInfo;

public class LatestInfoAdapter extends BaseAdapter {

    private ArrayList<LatestInfo> latestInfoList;
    private Context context;
    private LayoutInflater layoutInflater;

    public LatestInfoAdapter(Context context, ArrayList<LatestInfo> latestInfoList) {
        this.context = context;
        this.latestInfoList = latestInfoList;
        layoutInflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return latestInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return latestInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_info, null);
        }

        TextView infoDescription = (TextView) convertView.findViewById(R.id.infoDescription);
        ImageView infoImage = (ImageView) convertView.findViewById(R.id.infoImage);
        TextView infoTitle = (TextView) convertView.findViewById(R.id.infoTitle);

        if (latestInfoList.size() != 0) {
            infoDescription.setText(latestInfoList.get(position).Description);
            Picasso.get().load(latestInfoList.get(position).Image).placeholder(R.drawable.image_not_found).into(infoImage);
            infoTitle.setText(latestInfoList.get(position).Title);
        }

        return convertView;
    }
}
