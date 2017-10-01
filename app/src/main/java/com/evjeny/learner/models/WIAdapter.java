package com.evjeny.learner.models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.evjeny.learner.R;

import java.util.ArrayList;

/**
 * Created by Evjeny on 27.07.2017 11:16.
 */

public class WIAdapter extends BaseAdapter{

    private Context context;
    private LayoutInflater inflater;

    private TextView textView;
    private ImageView imageView, attachment;

    private ArrayList<LearnItem> objects;

    public WIAdapter(Context context, ArrayList<LearnItem> items) {
        this.context = context;
        this.objects = items;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(view==null) {
            view = inflater.inflate(R.layout.write_list_item, parent, false);
        }
        LearnItem current = (LearnItem) getItem(position);

        textView = (TextView) view.findViewById(R.id.write_li_textView);
        imageView = (ImageView) view.findViewById(R.id.write_li_imageView);
        attachment = (ImageView) view.findViewById(R.id.write_li_imageAttachment);

        textView.setText(current.getName());
        if(!current.getContent().equals("")) imageView.setImageResource(R.drawable.ic_radio_button_checked);
        else imageView.setImageResource(R.drawable.ic_radio_button_unchecked);

        if(!current.getHtml().equals("")) attachment.setImageResource(R.drawable.ic_attachment_active);
        else attachment.setImageResource(R.drawable.ic_attachment);

        return view;
    }


    public ArrayList<LearnItem> getObjects() {
        return objects;
    }
}
