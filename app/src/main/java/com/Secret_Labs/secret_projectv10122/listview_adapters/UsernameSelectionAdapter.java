package com.Secret_Labs.secret_projectv10122.listview_adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.Secret_Labs.secret_projectv10122.R;
import com.Secret_Labs.secret_projectv10122.models.Obj_Usersearch;

import java.util.List;

public class UsernameSelectionAdapter extends ArrayAdapter<Obj_Usersearch> {

    Context context;
    int layoutResourceId;
    List<Obj_Usersearch> usernameList = null;

    public UsernameSelectionAdapter(@NonNull Context context, int resource, @NonNull List<Obj_Usersearch> objects) {
        super(context, resource, objects);

        this.context = context;
        this.layoutResourceId = resource;
        this.usernameList = objects;
    }

    static class DataHolder{
        TextView username;
        TextView userQuote;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        DataHolder holder = null;

        if(convertView == null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();

            convertView = inflater.inflate(layoutResourceId, null);

            holder = new DataHolder();
            holder.username = (TextView)convertView.findViewById(R.id.new_conv_username);
            holder.userQuote = (TextView)convertView.findViewById(R.id.new_conv_quote);

            convertView.setTag(holder);
        } else {
            holder = (DataHolder)convertView.getTag();
        }

        Obj_Usersearch tempUserSearchObj = usernameList.get(position);
        holder.username.setText(tempUserSearchObj.getUsername());
        holder.userQuote.setText(tempUserSearchObj.getUserquote());

        return convertView;
    }
}
