package com.bangida.bangidaapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bangida.bangidaapp.R;
import com.bangida.bangidaapp.model.RoomListModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class RoomListAdapter extends RecyclerView.Adapter<RoomListAdapter.MyViewHolder> {
    ArrayList<RoomListModel> arrayList;
    Context context;

    public RoomListAdapter(Context context, ArrayList<RoomListModel> arrayList) {
        this.arrayList = arrayList;
        this.context = context;


    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.room_list_item, parent, false);

        final MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RoomListAdapter.MyViewHolder holder, int position) {
        final String petname = arrayList.get(position).getPetname();
        final String id = arrayList.get(position).getId();

        holder.petnameTv.setText(petname);

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        CardView accordian_title;
        TextView petnameTv, member_countTv;
        RelativeLayout accordian_body;
        ImageView arrow, editBtn, deleteBtn, doneBtn;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            petnameTv = (TextView) itemView.findViewById(R.id.pename_title);
            accordian_title = (CardView) itemView.findViewById(R.id.accordian_title);
            accordian_body = (RelativeLayout) itemView.findViewById(R.id.accordian_body);
            arrow = (ImageView) itemView.findViewById(R.id.arrow);
            editBtn = (ImageView) itemView.findViewById(R.id.editBtn);
            deleteBtn = (ImageView) itemView.findViewById(R.id.deleteBtn);
            doneBtn = (ImageView) itemView.findViewById(R.id.doneBtn);


        }
    }
}
