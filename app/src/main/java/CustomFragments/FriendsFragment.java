package CustomFragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.appradar.viper.jhakkas.MainActivity;
import com.appradar.viper.jhakkas.R;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import customadapters.FriendsAdapter;
import models.User;

/**
 * Created by viper on 17/09/16.
 */
public class FriendsFragment extends Fragment implements RecyclerView.OnItemTouchListener{

    private OnFriendClickListener mOnFriendClickListener;
    GestureDetector mGestureDetector;

    public interface OnFriendClickListener {
        public void onFriendClick(View view, int position);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_friend_list, container, false);

        initViews(inflater.getContext(), v);

        return v;
    }

    private void initViews(final Context context, View v){
        mGestureDetector = new GestureDetector(context, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }

        });
        RecyclerView recyclerView = (RecyclerView)v.findViewById(R.id.friend_recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        final ArrayList<User> friendsList = new ArrayList<User>();
        final FriendsAdapter adapter = new FriendsAdapter(context, friendsList);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(this);

        MainActivity.ref.child("users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);
                if (FirebaseAuth.getInstance().getCurrentUser().getEmail().equalsIgnoreCase(user.getEmail()))
                    return;
                friendsList.add(user);
                adapter.notifyDataSetChanged();
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

    public static FriendsFragment newInstance(String text) {

        FriendsFragment f = new FriendsFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mOnFriendClickListener = (OnFriendClickListener) context;
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        View childView = view.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && mOnFriendClickListener != null && mGestureDetector.onTouchEvent(e) ) {
            mOnFriendClickListener.onFriendClick(childView, view.getChildAdapterPosition(childView));
            return true;
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}
