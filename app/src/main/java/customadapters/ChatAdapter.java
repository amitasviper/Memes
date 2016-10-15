package customadapters;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appradar.viper.jhakkas.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import models.ChatMessage;

/**
 * Created by viper on 17/09/16.
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageHolder> {

    private ArrayList<ChatMessage> chatMessages;
    private int MY_MESSAGE_VIEW = 1, FRIEND_MESSAGE_VIEW = 2;

    public ChatAdapter(ArrayList<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
    }

    @Override
    public MessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == MY_MESSAGE_VIEW)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_my_chat_row, parent, false);
        }
        else
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_friend_chat_row, parent, false);
        }

        return new MessageHolder(view);
    }


    @Override
    public void onBindViewHolder(MessageHolder holder, int position) {
        ChatMessage chatMessage = chatMessages.get(position);
        if (chatMessage.getSender().equalsIgnoreCase(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()))
        {
            holder.tv_message.setText(Html.fromHtml( chatMessage.body + " &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;" +
                    "&#160;&#160;&#160;&#160;&#160;&#160;&#160;"));
        }
        else
        {
            holder.tv_message.setText(Html.fromHtml(chatMessage.body + " &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;"));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (chatMessages.get(position).getSender().equalsIgnoreCase(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()))
        {
            return MY_MESSAGE_VIEW;
        }

        return FRIEND_MESSAGE_VIEW;
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    public class MessageHolder extends RecyclerView.ViewHolder {
        TextView tv_message;
        public MessageHolder(View itemView) {
            super(itemView);
            tv_message = (TextView) itemView.findViewById(R.id.tv_message);
        }
    }
}
