package CustomFragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appradar.viper.jhakkas.MainActivity;
import com.appradar.viper.jhakkas.R;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

import Temp.Person;
import customadapters.ImagePostsAdapter;
import models.ImageDetails;

public class SecondFragment extends Fragment {

    private final String images_description[] = {
            "Donut",
            "Eclair",
            "Froyo",
            "Gingerbread",
            "Honeycomb",
            "Ice Cream Sandwich",
            "Jelly Bean",
            "KitKat",
            "Lollipop",
            "Marshmallow"
    };

    private final String images_url[] = {
            "http://api.learn2crack.com/android/images/donut.png",
            "http://api.learn2crack.com/android/images/eclair.png",
            "http://api.learn2crack.com/android/images/froyo.png",
            "http://api.learn2crack.com/android/images/ginger.png",
            "http://api.learn2crack.com/android/images/honey.png",
            "http://api.learn2crack.com/android/images/icecream.png",
            "http://api.learn2crack.com/android/images/jellybean.png",
            "http://api.learn2crack.com/android/images/kitkat.png",
            "http://api.learn2crack.com/android/images/lollipop.png",
            "http://api.learn2crack.com/android/images/marshmallow.png"
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_second, container, false);

        /*ImageView iv_image_one = (ImageView) v.findViewById(R.id.iv_image_one);
        ImageView iv_image_two = (ImageView) v.findViewById(R.id.iv_image_two);

        ImageView iv_image_three = (ImageView) v.findViewById(R.id.iv_image_three);
        ImageView iv_image_four = (ImageView) v.findViewById(R.id.iv_image_four);

        Glide.with(getContext())
                .load("http://inthecheesefactory.com/uploads/source/glidepicasso/cover.jpg")
                .placeholder(R.drawable.default_image)
                .into(iv_image_one);

        Glide.with(getContext())
                .load("http://phandroid.s3.amazonaws.com/wp-content/uploads/2016/04/htc_10_wallpapers_00.jpg")
                .placeholder(R.drawable.default_image)
                .into(iv_image_two);

        Picasso.with(getContext())
                .load("http://inthecheesefactory.com/uploads/source/glidepicasso/cover.jpg")
                .placeholder(R.drawable.default_image)
                .into(iv_image_three);

        Picasso.with(getContext())
                .load("http://phandroid.s3.amazonaws.com/wp-content/uploads/2016/04/htc_10_wallpapers_00.jpg")
                .placeholder(R.drawable.default_image)
                .into(iv_image_four);*/

        initViews(getContext(), v);

        return v;
    }

    private void initViews(Context context, View v){
        RecyclerView recyclerView = (RecyclerView)v.findViewById(R.id.card_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context,2);
        recyclerView.setLayoutManager(layoutManager);

        final ArrayList<ImageDetails> imagesList = prepareData();
        final ImagePostsAdapter adapter = new ImagePostsAdapter(context, imagesList);
        recyclerView.setAdapter(adapter);

        MainActivity.ref.child("images").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String string = "";
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //Getting the data from snapshot
                    Person person = postSnapshot.getValue(Person.class);

                    String name = person.getName();
                    String url = person.getAddress();
                    imagesList.add(new ImageDetails(url, name));
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    private ArrayList<ImageDetails> prepareData(){

        ArrayList<ImageDetails> imageList = new ArrayList<>();
        for(int i=0;i<images_url.length;i++){
            ImageDetails image = new ImageDetails(images_url[i], images_description[i]);
            imageList.add(image);
        }
        return imageList;
    }

    public static SecondFragment newInstance(String text) {

        SecondFragment f = new SecondFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }
}