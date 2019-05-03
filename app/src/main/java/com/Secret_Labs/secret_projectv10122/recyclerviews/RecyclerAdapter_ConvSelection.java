package com.Secret_Labs.secret_projectv10122.recyclerviews;

import android.content.Context;
import android.graphics.BitmapFactory;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.Secret_Labs.secret_projectv10122.R;
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
    public RecyclerviewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.convinfo_layout, parent, false);
        return new RecyclerviewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerviewHolder holder, final int position){
        Obj_ConvInfo conv_item = conv_List.get(position);

        holder.convsel_partner_name.setText(conv_item.getConvPartner_Username());

        //If the user has a custom picture now placing this image there
        if(conv_item.getConvPartner_ProfilePic() != null){
            holder.convsel_profilePicture.setImageBitmap(BitmapFactory.decodeByteArray(conv_item.getConvPartner_ProfilePic(), 0, conv_item.getConvPartner_ProfilePic().length));
        }

        //Formatting the text to put in the last message with sender
        String tempLastMessage = conv_item.getConvLast_Message();
        if(tempLastMessage.length() > 30){
            tempLastMessage = tempLastMessage.substring(0, 31) + "...";
        }
        holder.convsel_last_message.setText(conv_item.getConvLast_MessageSender() + ": " + tempLastMessage);

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
        ImageView convsel_profilePicture;

        public RecyclerviewHolder(View itemView){
            super(itemView);

            convsel_partner_name = itemView.findViewById(R.id.convsel_partner_name);
            convsel_last_message = itemView.findViewById(R.id.convsel_last_message);
            convsel_profilePicture = itemView.findViewById(R.id.convSel_PartnerProfilePic);
        }
    }

}
