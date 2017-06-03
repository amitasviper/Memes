package customadapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appradar.viper.jhakkas.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import models.User;
import utils.CircleTransform;

/**
 * Created by viper on 17/09/16.
 */
public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendDetailHolder> {

    private List<User> userList;
    private Context context;

    public FriendsAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @Override
    public FriendDetailHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_friend_row, parent, false);

        return new FriendDetailHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FriendDetailHolder holder, int position) {
        User friend = userList.get(position);
        holder.tv_friend_name.setText(friend.getName());
        Picasso.with(context)
                .load(friend.getProfile_pic_url())
                .transform(new CircleTransform())
                .into(holder.iv_friend_photo);
        holder.tv_friend_name.setTag(friend);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class FriendDetailHolder extends RecyclerView.ViewHolder
    {
        private ImageView iv_friend_photo;
        private TextView tv_friend_name;

        public FriendDetailHolder(View itemView) {
            super(itemView);

            iv_friend_photo = (ImageView) itemView.findViewById(R.id.iv_friend_photo);
            tv_friend_name = (TextView) itemView.findViewById(R.id.tv_friend_name);
        }
    }
}
