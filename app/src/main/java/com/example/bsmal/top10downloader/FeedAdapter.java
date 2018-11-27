package com.example.bsmal.top10downloader;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class FeedAdapter extends ArrayAdapter {
    private static final String TAG = "FeedAdapter";
    private final int layoutResource;
    private final LayoutInflater layoutInflater;  // inflating the XML resource
    private List<FeedEntry> applications;

    public FeedAdapter(@NonNull Context context, int resource, List<FeedEntry> applications) {
        super(context, resource);
        this.layoutResource = resource;
        this.layoutInflater = LayoutInflater.from(context); // context: Interface to global information about an application environment. This is an abstract class whose
        // implementation is provided by the Android system. It allows access to application-specific resources and classes,
        // as well as up-calls for application-level operations such as launching activities, broadcasting and receiving intents, etc.
        this.applications = applications;
    }

    @Override
    public int getCount() {
        return applications.size();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = layoutInflater.inflate(layoutResource, parent, false);  // creating a view toa inflate the layout resource
        TextView tvName = (TextView) view.findViewById(R.id.tvName); // using the view. because of the constraint layout, from the list_record, where I have the 3 text views
        TextView tvArtist = (TextView) view.findViewById(R.id.tvArtist); // finding the id that comes with the view, for all 3 of these views
        TextView tvSummary = (TextView) view.findViewById(R.id.tvSummary);

        FeedEntry currentApp = applications.get(position);

        tvName.setText(currentApp.getName());
        tvArtist.setText(currentApp.getArtist());
        tvSummary.setText(currentApp.getSummary());

        return view;
    }
}
