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

import java.util.ArrayList;

import models.ImageDetails;

/**
 * Created by viper on 13/09/16.
 */
public class ImagePostsAdapter extends RecyclerView.Adapter<ImagePostsAdapter.ViewHolder> {
    private ArrayList<ImageDetails> android;
    private Context context;

    public ImagePostsAdapter(Context context, ArrayList<ImageDetails> android) {
        this.android = android;
        this.context = context;
    }

    @Override
    public ImagePostsAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_image_row, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ImagePostsAdapter.ViewHolder viewHolder, int i) {

        viewHolder.tv_android.setText(android.get(i).getDescription());
        Picasso.with(context).load(android.get(i).getUrl()).placeholder(R.drawable.default_image).resize(240, 120).into(viewHolder.img_android);
    }

    @Override
    public int getItemCount() {
        return android.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_android;
        private ImageView img_android;
        public ViewHolder(View view) {
            super(view);

            tv_android = (TextView) view.findViewById(R.id.tv_android);
            img_android = (ImageView) view.findViewById(R.id.img_android);
        }
    }



}
