package com.Secret_Labs.secret_projectv10122;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class RecyclerAdapter_AccSelection extends RecyclerView.Adapter<RecyclerAdapter_AccSelection.RecyclerviewHolder> {

    //Defining the list and the context
    private Context mCtx;
    private List<Obj_AccountInfo> acc_List;

    public RecyclerAdapter_AccSelection (Context mCtx, List<Obj_AccountInfo> acc_List){
        this.mCtx = mCtx;
        this.acc_List = acc_List;
    }

    @Override
    public RecyclerviewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.accinfo_layout, parent, false);
        return new RecyclerviewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerviewHolder holder, int position){
        Obj_AccountInfo acc_item = acc_List.get(position);

        holder.acc_name.setText(acc_item.getAcc_Username());
        holder.acc_last_login.setText(acc_item.getAcc_Last_Login());
    }

    @Override
    public int getItemCount(){
        return acc_List.size();
    }

    class RecyclerviewHolder extends RecyclerView.ViewHolder{

        //Put the to fill layout items over here
        TextView acc_name, acc_last_login;

        public RecyclerviewHolder(View itemView){
            super(itemView);

            acc_name = itemView.findViewById(R.id.acc_name);
            acc_last_login = itemView.findViewById(R.id.acc_last_login);
        }
    }
}
