package com.project.onur.playerx.adapter;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.project.onur.playerx.R;
import com.project.onur.playerx.SQLiteUser;
import com.project.onur.playerx.model.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.project.onur.playerx.model.GetChat;
import com.project.onur.playerx.model.User;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_ME = 1;
    private static final int VIEW_TYPE_OTHER = 2;

    private List<GetChat> mChats;
    public User user, mOtherUser;
    private SQLiteUser sqLiteUser;

    public ChatRecyclerAdapter(List<GetChat> chats, User otherUser) {
        mChats = chats;
        mOtherUser = otherUser;
    }

    public void add(GetChat chat) {
        mChats.add(chat);
        notifyItemInserted(mChats.size() - 1);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        sqLiteUser = new SQLiteUser(parent.getContext());
        Cursor cursor = sqLiteUser.query();
        user = sqLiteUser.getUserFromSQLite(cursor);

        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case VIEW_TYPE_ME:
                View viewChatMine = layoutInflater.inflate(R.layout.item_chat_mine, parent, false);
                viewHolder = new MyChatViewHolder(viewChatMine);
                break;
            case VIEW_TYPE_OTHER:
                View viewChatOther = layoutInflater.inflate(R.layout.item_chat_other, parent, false);
                viewHolder = new OtherChatViewHolder(viewChatOther);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (TextUtils.equals(mChats.get(position).senderUid,
                FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            configureMyChatViewHolder((MyChatViewHolder) holder, position);
        } else {
            configureOtherChatViewHolder((OtherChatViewHolder) holder, position);
        }
    }

    private void configureMyChatViewHolder(MyChatViewHolder myChatViewHolder, int position) {
        GetChat chat = mChats.get(position);

        Log.e("mesaj",chat.message);
        myChatViewHolder.txtChatMessage.setText(chat.message);
        Picasso.with(myChatViewHolder.circleImageView.getContext())
                .load(user.getProfilURL())
                .resize(40,40)
                .centerCrop()
                .placeholder(R.drawable.ic_default_user)
                .error(R.drawable.ic_default_user)
                .into(myChatViewHolder.circleImageView);
    }

    private void configureOtherChatViewHolder(OtherChatViewHolder otherChatViewHolder, int position) {
        GetChat chat = mChats.get(position);

        otherChatViewHolder.txtChatMessage.setText(chat.message);
        Picasso.with(otherChatViewHolder.circleImageView.getContext())
                .load(mOtherUser.getProfilURL())
                .resize(40,40)
                .centerCrop()
                .placeholder(R.drawable.ic_default_user)
                .error(R.drawable.ic_default_user)
                .into(otherChatViewHolder.circleImageView);
    }

    @Override
    public int getItemCount() {
        if (mChats != null) {
            return mChats.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (TextUtils.equals(mChats.get(position).senderUid,
                FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            return VIEW_TYPE_ME;
        } else {
            return VIEW_TYPE_OTHER;
        }
    }

    private static class MyChatViewHolder extends RecyclerView.ViewHolder {
        TextView txtChatMessage;
        CircleImageView circleImageView;

        public MyChatViewHolder(View itemView) {
            super(itemView);

            txtChatMessage = itemView.findViewById(R.id.text_view_chat_message);
            circleImageView = itemView.findViewById(R.id.profile_image);
        }
    }

    private static class OtherChatViewHolder extends RecyclerView.ViewHolder {
        TextView txtChatMessage;
        CircleImageView circleImageView;


        public OtherChatViewHolder(View itemView) {
            super(itemView);
            txtChatMessage = itemView.findViewById(R.id.text_view_chat_message);
            circleImageView = itemView.findViewById(R.id.profile_image);
        }
    }
}
