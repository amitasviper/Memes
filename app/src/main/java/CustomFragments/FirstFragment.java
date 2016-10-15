package CustomFragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.appradar.viper.jhakkas.MainActivity;
import com.appradar.viper.jhakkas.R;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;

import Temp.Person;
import customadapters.TextPostsAdapter;
import models.TextPost;

public class FirstFragment extends Fragment {

    private EditText editTextName;
    private EditText editTextAddress;
    private TextView textViewPersons;
    private Button buttonSave, btn_next_act;
    ProgressDialog nDialog;

    private StoreInDatabaseListener mStoreInDatabaseListener;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_first, container, false);

        //ShowLoading(inflater.getContext());
        InitViews(inflater.getContext(), v);
        return v;
    }


    private void InitViews(Context context, View v){
        RecyclerView recyclerView = (RecyclerView)v.findViewById(R.id.rv_text_posts);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);

        final ArrayList<TextPost> textPosts = new ArrayList<TextPost>();
        final TextPostsAdapter adapter = new TextPostsAdapter(context, textPosts);
        recyclerView.setAdapter(adapter);

        MainActivity.ref.child("posts").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                TextPost textPost = dataSnapshot.getValue(TextPost.class);
                if (textPost != null)
                    textPosts.add(textPost);
                adapter.notifyDataSetChanged();
                HideLoading();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    public static FirstFragment newInstance(String text) {

        FirstFragment f = new FirstFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mStoreInDatabaseListener = (StoreInDatabaseListener) context;
    }

    public interface StoreInDatabaseListener {
        public void onStoreRequestGenerated(Person person);
    }

    private void ShowLoading(Context context)
    {
        if(nDialog == null)
            Loading(context);
        nDialog.show();
    }

    private void HideLoading()
    {
        if(nDialog == null)
            return;
        nDialog.hide();
    }
    private void Loading(Context context)
    {
        nDialog = new ProgressDialog(context);
        nDialog.setMessage("Please wait");
        nDialog.setTitle("Fetching posts");
        nDialog.setIndeterminate(false);
        nDialog.setCancelable(false);
    }
}
