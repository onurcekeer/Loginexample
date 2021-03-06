package com.project.onur.playerx.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.onur.playerx.CustomItemClickListener;
import com.project.onur.playerx.R;
import com.project.onur.playerx.model.Event;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import im.delight.android.location.SimpleLocation;

/**
 * Created by onur on 6.10.2017 at 00:13.
 */
public class SimpleRecyclerAdapter extends RecyclerView.Adapter<SimpleRecyclerAdapter.ViewHolder> {

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView text_title;
        TextView text_description;
        TextView text_category;
        TextView text_username;
        TextView text_distance;
        ImageView categoryImage;
        CircleImageView image_profile;
        CardView card_view;


        ViewHolder(View view) {
            super(view);

            card_view = view.findViewById(R.id.card_view);
            image_profile = view.findViewById(R.id.profile_image);
            text_username = view.findViewById(R.id.text_username);
            text_category = view.findViewById(R.id.text_category);
            categoryImage = view.findViewById(R.id.categoryImage);
            text_title = view.findViewById(R.id.text_title);
            text_description = view.findViewById(R.id.text_description);
            text_distance = view.findViewById(R.id.distance);



        }
    }

    private List<Event> list_event;
    private CustomItemClickListener listener;
    public SimpleRecyclerAdapter(List<Event> list_event, CustomItemClickListener listener) {

        this.list_event= list_event;
        this.listener = listener;
    }


    @Override
    public SimpleRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_layout, parent, false);
        final ViewHolder view_holder = new ViewHolder(v);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, view_holder.getPosition());
            }
        });


        return view_holder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.text_username.setText(list_event.get(position).getUsername());
        holder.text_title.setText(list_event.get(position).getTitle());
        holder.text_description.setText(list_event.get(position).getDescription());

        SimpleLocation simpleLocation = new SimpleLocation(holder.itemView.getContext());

        double distance = SimpleLocation.calculateDistance(simpleLocation.getLatitude(), simpleLocation.getLongitude(),list_event.get(position).getLocation().getLatitude(),list_event.get(position).getLocation().getLongitude());
        int int_distance = (int) distance/1000;
        if(int_distance==0){
            int_distance = 1;
        }

        holder.text_distance.setText(String.valueOf(int_distance)+" km");



        Picasso.with(holder.itemView.getContext())
                .load(list_event.get(position).getProfileURL())
                .resize(30,30)
                .centerCrop()
                .placeholder(R.drawable.ic_default_user)
                .error(R.drawable.ic_default_user)
                .into(holder.image_profile);

        switch (list_event.get(position).getCategory()){
            case 0:
                holder.text_category.setText(R.string.sport_text);
                holder.categoryImage.setImageResource(R.drawable.ic_football_mini);
                break;
            case 1:
                holder.text_category.setText(R.string.table_games_text);
                holder.categoryImage.setImageResource(R.drawable.ic_table_games_mini);
                break;
            case 2:
                holder.text_category.setText(R.string.concert_text);
                holder.categoryImage.setImageResource(R.drawable.ic_concert_mini);
                break;
            case 3:
                holder.text_category.setText(R.string.pc_games_text);
                holder.categoryImage.setImageResource(R.drawable.ic_pc_games_mini);
                break;
            case 4:
                holder.text_category.setText(R.string.cinema_text);
                holder.categoryImage.setImageResource(R.drawable.ic_cinema_mini);
                break;
            case 5:
                holder.text_category.setText(R.string.other_text);
                holder.categoryImage.setImageResource(R.drawable.ic_other_mini);
                break;
            default:
                break;

        }

    }

    @Override
    public int getItemCount() {
        return list_event.size();
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


}