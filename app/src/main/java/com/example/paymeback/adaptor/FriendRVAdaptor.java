package com.example.paymeback.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.paymeback.AddFriendListActivity;
import com.example.paymeback.R;
import com.example.paymeback.models.FriendModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class FriendRVAdaptor extends RecyclerView.Adapter<FriendRVAdaptor.ViewHolder> {

    private Context context;
    private ArrayList<FriendModel> friendModelArrayList;
    private OnFriendItemClickListener onFriendItemClickListener;

    public FriendRVAdaptor(Context context, ArrayList<FriendModel> friendModelArrayList){
        this.context = context;
        this.friendModelArrayList = friendModelArrayList;
    }

    @NonNull
    @Override
    public FriendRVAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.friend_item,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendRVAdaptor.ViewHolder holder, int position) {

        FriendModel friendModel = friendModelArrayList.get(position);

        holder.name.setText(friendModel.getName());
        holder.phoneNo.setText(friendModel.getPhoneNo());

        final boolean[] isVisible = {friendModel.isVisible()};
        holder.checkIV.setVisibility(isVisible[0] ? View.VISIBLE : View.INVISIBLE);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isVisible[0] = !isVisible[0];
                friendModel.setVisible(isVisible[0]);
                holder.checkIV.setVisibility(isVisible[0] ? View.VISIBLE : View.INVISIBLE);

                if (!isVisible[0]) {

                    onFriendItemClickListener.onRemoveFriendItemClick(friendModel);
                } else {
                    onFriendItemClickListener.onFriendItemClick(friendModel);
                }
            }
        });



    }

    @Override
    public int getItemCount() {
        return friendModelArrayList.size();
    }

    public void setFilteredData(ArrayList<FriendModel> filteredList) {
        this.friendModelArrayList = filteredList;
        notifyDataSetChanged();
    }

    public void setOnFriendItemClickListener(AddFriendListActivity addFriendListActivity) {
        onFriendItemClickListener = addFriendListActivity;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name,phoneNo;
        ImageView checkIV;
        FloatingActionButton nextFab;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name_item);
            phoneNo = itemView.findViewById(R.id.phone_item);
            checkIV = itemView.findViewById(R.id.check_item);
            nextFab = itemView.findViewById(R.id.nextFabBtn);

        }
    }
}
