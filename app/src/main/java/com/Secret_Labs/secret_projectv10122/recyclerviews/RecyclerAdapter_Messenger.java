package com.Secret_Labs.secret_projectv10122.recyclerviews;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.Secret_Labs.secret_projectv10122.R;
import com.Secret_Labs.secret_projectv10122.models.Obj_Message;

import java.util.List;

public class RecyclerAdapter_Messenger extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //Defining the list to use and the context
    private Context context;
    private List<Obj_Message> messageList;

    public RecyclerAdapter_Messenger(Context context, List<Obj_Message> messageList){
        this.context = context;
        this.messageList = messageList;
    }

    @Override
    public int getItemViewType(int position){
        if(messageList.get(position).isYourself()){
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(context);
        switch (viewType){
            case 0: return new UserMessageViewholder(inflater.inflate(R.layout.user_message_layout, parent, false));
            default: return new PartnerMessageViewholder(inflater.inflate(R.layout.partner_message_layout, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        Obj_Message message_item = messageList.get(position);
        Log.d("loggiesss", Integer.toString(viewHolder.getItemViewType()));

        //Checking which layout to use
        switch (viewHolder.getItemViewType()){
            case 0:
                UserMessageViewholder currentUserViewholder = (UserMessageViewholder)viewHolder;

                //Setting the fields
                currentUserViewholder.yourUsername.setText(message_item.getSender());
                currentUserViewholder.yourDatetime.setText(message_item.getDatetime());
                currentUserViewholder.yourMessage.setText(message_item.getMessage());

                break;

            case 1:
                PartnerMessageViewholder currentPartnerViewholder = (PartnerMessageViewholder)viewHolder;

                //Setting the fields
                currentPartnerViewholder.partnerUsername.setText(message_item.getSender());
                currentPartnerViewholder.partnerDatetime.setText(message_item.getDatetime());
                currentPartnerViewholder.partnerMessage.setText(message_item.getMessage());

                break;
        }
    }


    //Defining the two viewholders
    class UserMessageViewholder extends RecyclerView.ViewHolder{

        //Defining the textfields to be filled
        TextView yourUsername, yourDatetime, yourMessage;

        public UserMessageViewholder(View itemView){
            super(itemView);

            yourUsername = itemView.findViewById(R.id.userMessageUsernameTV);
            yourDatetime = itemView.findViewById(R.id.userMessageUserDatetimeTV);
            yourMessage = itemView.findViewById(R.id.userMessageUserMessageTV);
        }

    }

    class PartnerMessageViewholder extends RecyclerView.ViewHolder{

        //Defining the textfields to be filled
        TextView partnerUsername, partnerDatetime, partnerMessage;

        public PartnerMessageViewholder(View itemView){
            super(itemView);

            partnerUsername = itemView.findViewById(R.id.partnerMessagePartnernameTV);
            partnerDatetime = itemView.findViewById(R.id.partnerMessagePartnerDatetimeTV);
            partnerMessage = itemView.findViewById(R.id.partnerMessagePartnerMessageTV);
        }

    }

}
