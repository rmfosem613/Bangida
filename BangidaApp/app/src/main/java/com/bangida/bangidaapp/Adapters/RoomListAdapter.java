package com.bangida.bangidaapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bangida.bangidaapp.MainActivity2;
import com.bangida.bangidaapp.R;
import com.bangida.bangidaapp.model.RoomListModel;
import com.bangida.bangidaapp.ui.cal.CalFragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class RoomListAdapter extends RecyclerView.Adapter<RoomListAdapter.MyViewHolder> {
    ArrayList<RoomListModel> roomList;
    Context context;

    public RoomListAdapter(Context context, ArrayList<RoomListModel> roomList) {
        this.roomList = roomList;
        this.context = context;


    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.room_list_item, parent, false);

        final MyViewHolder myViewHolder = new MyViewHolder(view);
        context = parent.getContext();

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RoomListAdapter.MyViewHolder holder, int position) {
        final String petname = roomList.get(position).getPetname();
        final String id = roomList.get(position).getId();

        holder.petnameTv.setText(petname);

        holder.arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity2.class);
                String key = id;
                intent.putExtra("key", key);
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        CardView accordian_title;
        TextView petnameTv, member_countTv;
        ImageView arrow;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            petnameTv = (TextView) itemView.findViewById(R.id.pename_title);
            accordian_title = (CardView) itemView.findViewById(R.id.accordian_title);
            arrow = (ImageView) itemView.findViewById(R.id.arrow);

        }
    }
}
