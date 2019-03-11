package com.Secret_Labs.secret_projectv10122.recyclerviews;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.Secret_Labs.secret_projectv10122.R;
import com.Secret_Labs.secret_projectv10122.models.Obj_AccountInfo;
import com.Secret_Labs.secret_projectv10122.models.Obj_ConvInfo;

import java.util.List;

public class RecyclerAdapter_ConvSelection extends RecyclerView.Adapter<RecyclerAdapter_ConvSelection.RecyclerviewHolder> {

    //Defining the list and the context
    private Context mCtx;
    private List<Obj_ConvInfo> conv_List;

    //Defining the interface for the onclicklistener
    public OnclickListener_ConvSelection mListener;

    public RecyclerAdapter_ConvSelection (Context mCtx, List<Obj_ConvInfo> conv_List, OnclickListener_ConvSelection mListener){
        this.mCtx = mCtx;
        this.conv_List = conv_List;
        this.mListener = mListener;
    }

    @Override
    public RecyclerAdapter_ConvSelection.RecyclerviewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.convinfo_layout, parent, false);
        return new RecyclerAdapter_ConvSelection.RecyclerviewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerAdapter_ConvSelection.RecyclerviewHolder holder, final int position){
        Obj_ConvInfo conv_item = conv_List.get(position);

        holder.convsel_partner_name.setText(conv_item.getConvPartner_Username());
        holder.convsel_last_message.setText(conv_item.getConvLast_Message());

        //Setting the onclicklistener for the entire conv entry
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClicked(position);
            }
        });
    }

    @Override
    public int getItemCount(){
        return conv_List.size();
    }

    class RecyclerviewHolder extends RecyclerView.ViewHolder{

        //Put the to fill layout items over here
        TextView convsel_partner_name, convsel_last_message;

        public RecyclerviewHolder(View itemView){
            super(itemView);

            convsel_partner_name = itemView.findViewById(R.id.convsel_partner_name);
            convsel_last_message = itemView.findViewById(R.id.convsel_last_message);
        }
    }

}
