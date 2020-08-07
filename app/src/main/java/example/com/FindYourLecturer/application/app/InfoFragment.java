package example.com.FindYourLecturer.application.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import example.com.FindYourLecturer.R;
import example.com.FindYourLecturer.application.adapter.LatestInfoAdapter;
import example.com.FindYourLecturer.application.model.LatestInfo;

public class InfoFragment extends Fragment {

    private ProgressBar progressBar;
    private View view;
    private EditText search;
    private TextView noResult;
    private LatestInfoAdapter latestInfoAdapter;
    private ListView infoView;
    private ArrayList<LatestInfo> latestInfoList = new ArrayList<LatestInfo>();
    private ArrayList<LatestInfo> filterList = new ArrayList<LatestInfo>();
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference info;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (view != null) {
            return view;
        } else {
            view = inflater.inflate(R.layout.fragment_info, container, false);

            firebaseDatabase = FirebaseDatabase.getInstance();
            info = firebaseDatabase.getReference("Info");

            noResult = (TextView) view.findViewById(R.id.noResult);
            infoView = (ListView) view.findViewById(R.id.infoList);
            search = (EditText) view.findViewById(R.id.searchArea);
            progressBar = (ProgressBar) view.findViewById(R.id.infoProgressBar);
            progressBar.setVisibility(View.VISIBLE);

            search.setEnabled(false);

            search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        hideKeyboard(v);
                    }
                }
            });

            latestInfoAdapter = new LatestInfoAdapter(getContext(), latestInfoList);
            infoView.setAdapter(latestInfoAdapter);
            callDatabase();

            infoView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    search.clearFocus();
                    LatestInfo latestInfo;

                    if (filterList.size() > 0) {
                        latestInfo = filterList.get(position);
                    } else {
                        latestInfo = latestInfoList.get(position);
                    }

                    Intent intent = new Intent(getActivity(), InfoActivity.class);
                    intent.putExtra("infoDescription", latestInfo.Description);
                    intent.putExtra("infoImage", latestInfo.Image);
                    intent.putExtra("infoTitle", latestInfo.Title);
                    startActivity(intent);
                }
            });

            infoView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    hideKeyboard(v);
                    return false;
                }
            });

            search.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    int textLength = s.toString().trim().length();
                    filterList.clear();

                    if (textLength > 0) {
                        for (int i = 0; i < latestInfoList.size(); i++) {
                            if (latestInfoList.get(i).Title.toLowerCase().trim().contains(s.toString().toLowerCase().trim())) {
                                filterList.add(latestInfoList.get(i));
                            }
                        }
                        latestInfoAdapter = new LatestInfoAdapter(getActivity(), filterList);
                        infoView.setAdapter(latestInfoAdapter);
                        if (filterList.size() > 0) {
                            noResult.setVisibility(View.GONE);
                            infoView.setVisibility(View.VISIBLE);
                        } else if (filterList.size() == 0) {
                            noResult.setVisibility(View.VISIBLE);
                            infoView.setVisibility(View.GONE);
                        }
                    } else {
                        noResult.setVisibility(View.GONE);
                        infoView.setVisibility(View.VISIBLE);
                        latestInfoAdapter = new LatestInfoAdapter(getContext(), latestInfoList);
                        infoView.setAdapter(latestInfoAdapter);
                        callDatabase();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            return view;
        }
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void callDatabase() {

        info.orderByChild("Time").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                latestInfoList.clear();
                progressBar.setVisibility(View.GONE);
                search.setEnabled(true);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    LatestInfo latestInfo = snapshot.getValue(LatestInfo.class);
                    if (latestInfo != null) {
                        latestInfoList.add(latestInfo);
                    }
                }
                Collections.reverse(latestInfoList);
                latestInfoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Failed to Connect Database", Toast.LENGTH_LONG).show();
            }
        });
    }
}
