package customadapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.appradar.viper.jhakkas.DisplayImage;
import com.appradar.viper.jhakkas.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import models.TextPost;

/**
 * Created by viper on 18/09/16.
 */
public class TextPostsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<TextPost> postsArrayList;
    private Context context;
    private static TextPostClickListner mTextPostClickListner;

    public static final int TEXT_POST = 1, IMAGE_POST = 2;

    ImageView iv_whatsapp, iv_facebook, iv_twitter, iv_share;

    public TextPostsAdapter(Context context,ArrayList<TextPost> android) {
        this.postsArrayList = android;
        this.context = context;
        mTextPostClickListner = (TextPostClickListner) context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int postType) {
        View view = null;

        switch (postType)
        {
            case TEXT_POST:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_post_text, viewGroup, false);
                return new TextPostHolder(view);

            case IMAGE_POST:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_post_image, viewGroup, false);
                return new ImagePostHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        TextPost post = postsArrayList.get(position);

        switch (getItemViewType(position))
        {
            case TEXT_POST:
                ((TextPostHolder)viewHolder).SetPostContents(post);
                break;

            case IMAGE_POST:
                ((ImagePostHolder)viewHolder).SetPostContents(post);
                break;
        }
        //Picasso.with(context).load(postsArrayList.get(i).getUrl()).placeholder(R.drawable.default_image).resize(240, 120).into(viewHolder.img_android);
    }


    @Override
    public int getItemViewType(int position) {
        if (postsArrayList.get(position).getImageUrl().isEmpty())
        {
            return TEXT_POST;
        }
        else
        {
            return IMAGE_POST;
        }
    }

    @Override
    public int getItemCount() {
        return postsArrayList.size();
    }

    public class TextPostHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView tv_content, tv_time, tv_username, tv_reputations;

        private ImageView iv_userpic;

        public TextPostHolder(View view) {
            super(view);

            iv_userpic = (ImageView) view.findViewById(R.id.iv_user_pic);
            tv_username = (TextView) view.findViewById(R.id.tv_username);
            tv_reputations = (TextView) view.findViewById(R.id.tv_reputation);

            tv_content = (TextView) view.findViewById(R.id.tv_content);
            tv_content.setMovementMethod(LinkMovementMethod.getInstance());
            tv_time = (TextView) view.findViewById(R.id.tv_time);

            iv_whatsapp = (ImageView) itemView.findViewById(R.id.iv_whatsapp);
            iv_facebook = (ImageView) itemView.findViewById(R.id.iv_facebook);
            iv_twitter = (ImageView) itemView.findViewById(R.id.iv_twitter);
            iv_share = (ImageView) itemView.findViewById(R.id.iv_share);

            iv_whatsapp.setOnClickListener(this);
            iv_facebook.setOnClickListener(this);
            iv_twitter.setOnClickListener(this);
            iv_share.setOnClickListener(this);

            //img_android = (ImageView) view.findViewById(R.id.img_android);
        }

        public void SetPostContents(TextPost post)
        {
            Picasso.with(context).load(post.getUserPicUrl()).placeholder(R.drawable.default_image).into(iv_userpic);
            tv_username.setText(post.getPostUser());
            tv_reputations.setText("125");
            tv_content.setText(post.getPostContent());
            tv_time.setText(post.getPostTime());
        }

        public String GetViewContents()
        {
            return this.tv_content.getText().toString();
        }

        @Override
        public void onClick(View v) {
            mTextPostClickListner.onItemClick(this, v, TEXT_POST);
        }
    }


    public class ImagePostHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView tv_content, tv_time, tv_username, tv_reputations;
        private ImageView iv_post, iv_userpic;

        private String url;

        //private ImageView img_android;

        public ImagePostHolder(View view) {
            super(view);

            iv_userpic = (ImageView) view.findViewById(R.id.iv_user_pic);
            tv_username = (TextView) view.findViewById(R.id.tv_username);
            tv_reputations = (TextView) view.findViewById(R.id.tv_reputation);

            tv_content = (TextView) view.findViewById(R.id.tv_content);
            tv_content.setMovementMethod(LinkMovementMethod.getInstance());
            tv_time = (TextView) view.findViewById(R.id.tv_time);

            iv_post = (ImageView) view.findViewById(R.id.iv_post);

            iv_whatsapp = (ImageView) itemView.findViewById(R.id.iv_whatsapp);
            iv_facebook = (ImageView) itemView.findViewById(R.id.iv_facebook);
            iv_twitter = (ImageView) itemView.findViewById(R.id.iv_twitter);
            iv_share = (ImageView) itemView.findViewById(R.id.iv_share);

            iv_whatsapp.setOnClickListener(this);
            iv_facebook.setOnClickListener(this);
            iv_twitter.setOnClickListener(this);
            iv_share.setOnClickListener(this);

            //img_android = (ImageView) view.findViewById(R.id.img_android);
        }

        public void SetPostContents(TextPost post)
        {
            final TextPost tpost = post;

            //.networkPolicy(NetworkPolicy.OFFLINE)
            Picasso.with(context).load(post.getUserPicUrl()).placeholder(R.drawable.default_image).into(iv_userpic);

            url = post.getImageUrl();

            tv_username.setText(post.getPostUser());
            tv_reputations.setText("125");

            tv_content.setText(post.getPostContent());
            tv_time.setText(post.getPostTime());


            //SaveImageInternal.DownloadAndSaveImage(context,post.getImageUrl(),iv_post);

            //Picasso.with(context).load(new File("/data/data/com.appradar.viper.jhakkas/files/jhakkas/2016-10-11_12_11_18_270.jpg")).into(iv_post);


            Picasso.with(context).load(post.getImageUrl()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.default_image).into(iv_post, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(context).load(tpost.getImageUrl()).placeholder(R.drawable.default_image).into(iv_post, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Toast.makeText(context, "Unable to load image", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

            final TextPost tPost = post;

            iv_post.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, DisplayImage.class);
                    intent.putExtra("url", tPost.getImageUrl());
                    context.startActivity(intent);
                }
            });
        }

        public String GetViewContents()
        {
            return this.tv_content.getText().toString() + "\n" + url;
        }

        @Override
        public void onClick(View v) {
            mTextPostClickListner.onItemClick(this, v, IMAGE_POST);
        }
    }

    public interface TextPostClickListner {
        public void onItemClick(RecyclerView.ViewHolder holder, View view, int HolderType);
    }


}
