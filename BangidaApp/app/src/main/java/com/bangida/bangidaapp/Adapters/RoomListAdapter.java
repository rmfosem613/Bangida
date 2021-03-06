package com.bangida.bangidaapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bangida.bangidaapp.MainActivity2;
import com.bangida.bangidaapp.R;
import com.bangida.bangidaapp.interfaces.RecyclerViewClickListener;
import com.bangida.bangidaapp.model.AnimalModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class RoomListAdapter extends RecyclerView.Adapter<RoomListAdapter.MyViewHolder> {
    ArrayList<AnimalModel> roomList;
    Context context;
    final private RecyclerViewClickListener clickListener;

    public RoomListAdapter(Context context, ArrayList<AnimalModel> roomList, RecyclerViewClickListener clickListener) {
        this.roomList = roomList;
        this.context = context;
        this.clickListener = clickListener;

    }


    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.room_list_item, parent, false);

        final MyViewHolder myViewHolder = new MyViewHolder(view);
        context = parent.getContext();

        myViewHolder.roomlist_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myViewHolder.accordian_body.getVisibility() == View.VISIBLE) {
                    myViewHolder.accordian_body.setVisibility(View.GONE);
                } else {
                    myViewHolder.accordian_body.setVisibility(View.VISIBLE);
                }
            }
        });

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RoomListAdapter.MyViewHolder holder, int position) {
        final String petname = roomList.get(position).getPetname();
        final String id = roomList.get(position).getId();


        // ??? ???????????? db?????? ????????? ?????? ?????? ??????????????? ????????? ????????? ???????????? ??????.
        holder.petnameTv.setText(petname);

        holder.arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity2.class);
                intent.putExtra("key", id);
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
        ImageView arrow, editBtn, deleteBtn;
        RelativeLayout roomlist_bg, accordian_body;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            petnameTv = (TextView) itemView.findViewById(R.id.pename_title);
            accordian_title = (CardView) itemView.findViewById(R.id.accordian_title);
            accordian_body = (RelativeLayout) itemView.findViewById(R.id.accordian_body);
            arrow = (ImageView) itemView.findViewById(R.id.arrow);
            editBtn = (ImageView) itemView.findViewById(R.id.editBtn);
            deleteBtn = (ImageView) itemView.findViewById(R.id.deleteBtn);
            roomlist_bg = (RelativeLayout) itemView.findViewById(R.id.roolist_bg);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onItemClick(getAdapterPosition());
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    clickListener.onLongItemClick(getAdapterPosition());
                    return true;
                }
            });

            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onEditButtonClick(getAdapterPosition());
                }
            });

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onDeleteButtonClick(getAdapterPosition());
                }
            });
        }
    }
}
