package com.project.onur.playerx.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.project.onur.playerx.CustomItemClickListener;
import com.project.onur.playerx.R;
import com.project.onur.playerx.model.User;
import com.squareup.picasso.Picasso;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by onur on 8.11.2017 at 17:19.
 */

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder>{

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView text_username;
        CircleImageView image_profile;

        ViewHolder(View view) {
            super(view);

            image_profile = view.findViewById(R.id.profile_image);
            text_username = view.findViewById(R.id.text_username);

        }
    }


    private List<User> userList;
    private CustomItemClickListener listener;
    public UsersAdapter(List<User> userList, CustomItemClickListener listener) {

        this.userList= userList;
        this.listener = listener;
    }

    @Override
    public UsersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_row, parent, false);
        final UsersAdapter.ViewHolder view_holder = new UsersAdapter.ViewHolder(v);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, view_holder.getPosition());
            }
        });


        return view_holder;
    }


    @Override
    public void onBindViewHolder(UsersAdapter.ViewHolder holder, int position) {

        holder.text_username.setText(userList.get(position).getUsername());

        Picasso.with(holder.itemView.getContext())
                .load(userList.get(position).getProfilURL())
                .resize(40,40)
                .centerCrop()
                .placeholder(R.drawable.ic_default_user)
                .error(R.drawable.ic_default_user)
                .into(holder.image_profile);


    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


}
