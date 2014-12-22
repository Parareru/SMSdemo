package com.example.MessageManager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Lkx on 2014/12/22.
 */
public class ContectViewAdapter extends ArrayAdapter<ContectView> {
    private int resourceId;
    public ContectViewAdapter(Context context, int textViewResourceId, List<ContectView> objects){
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ContectView contectView = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        TextView personName = (TextView) view.findViewById(R.id.person);
        TextView date = (TextView) view.findViewById(R.id.date);
        TextView body = (TextView) view.findViewById(R.id.text);

        personName.setText(contectView.getName());
        date.setText(contectView.getDate());
        body.setText(contectView.getBody());

        return view;
    }
}
