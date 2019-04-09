package com.Secret_Labs.secret_projectv10122.recyclerviews;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.Secret_Labs.secret_projectv10122.R;
import com.Secret_Labs.secret_projectv10122.models.Obj_AccountInfo;

import java.util.List;

public class RecyclerAdapter_AccSelection extends RecyclerView.Adapter<RecyclerAdapter_AccSelection.RecyclerviewHolder> {

    //Defining the list and the context
    private Context mCtx;
    private List<Obj_AccountInfo> acc_List;

    //Defining the interface for the onclicklistener
    public OnclickListener_AccSelection mListener;

    public RecyclerAdapter_AccSelection (Context mCtx, List<Obj_AccountInfo> acc_List, OnclickListener_AccSelection mListener){
        this.mCtx = mCtx;
        this.acc_List = acc_List;
        this.mListener = mListener;
    }

    @Override
    public RecyclerviewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.accinfo_layout, parent, false);
        return new RecyclerviewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerviewHolder holder, final int position){
        Obj_AccountInfo acc_item = acc_List.get(position);

        holder.acc_name.setText(acc_item.getAcc_Username());
        holder.acc_last_login.setText(acc_item.getAcc_Last_Login());

        if(acc_item.getAcc_ProfilePic() != null){
            holder.acc_ProfilePic.setImageBitmap(BitmapFactory.decodeByteArray(acc_item.getAcc_ProfilePic(), 0, acc_item.getAcc_ProfilePic().length));
        }

        //Setting the onclickListener for the whole view
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClicked(position);
            }
        });

        //Setting the onclicklistener for the remove button
        holder.acc_Remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemRemoveClicked(position);
            }
        });
    }

    @Override
    public int getItemCount(){
        return acc_List.size();
    }

    class RecyclerviewHolder extends RecyclerView.ViewHolder{

        //Put the to fill layout items over here
        TextView acc_name, acc_last_login;
        ImageView acc_ProfilePic, acc_Remove;

        public RecyclerviewHolder(View itemView){
            super(itemView);

            acc_name = itemView.findViewById(R.id.acc_name);
            acc_last_login = itemView.findViewById(R.id.acc_last_login);
            acc_ProfilePic = itemView.findViewById(R.id.profilePic);
            acc_Remove = itemView.findViewById(R.id.acc_Remove);
        }
    }
}
