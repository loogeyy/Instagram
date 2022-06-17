package com.example.instagram.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.instagram.MainActivity;
import com.example.instagram.Post;
import com.example.instagram.ProfileAdapter;
import com.example.instagram.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailsProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailsProfileFragment extends Fragment {

    private RecyclerView rvPosts;
    public static final String TAG = "PostsFragment";
    protected ProfileAdapter adapter;
    protected List<Post> allPosts;
    private SwipeRefreshLayout swipeContainer;
    private ParseUser user;
    private ImageView ivProfileImg;
    private TextView tvProfileUser;
    private TextView tvNumPosts;
    private TextView tvFollowers;
    private TextView tvFollowing;
    private TextView tvBio;
    private TextView tvName;

    public DetailsProfileFragment() {
        // Required empty public constructor
    }

    public DetailsProfileFragment(ParseUser user) {
        this.user = user;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_details_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ivProfileImg = view.findViewById(R.id.ivChangeProfilePic);
        tvProfileUser = view.findViewById(R.id.tvProfileUser);
        tvNumPosts = view.findViewById(R.id.tvNumPosts);
        tvFollowers = view.findViewById(R.id.tvFollowers);
        tvFollowing = view.findViewById(R.id.tvFollowing);
        tvBio = view.findViewById(R.id.tvBio);
        tvName = view.findViewById(R.id.tvName);

        // swiping refresh
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    fetchTimelineAsync(0);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(
                R.color.ig_blue, R.color.ig_purple,
                R.color.ig_purple_red,
                R.color.ig_orange);

        rvPosts = view.findViewById(R.id.rvPosts);
        allPosts = new ArrayList<>();
        adapter = new ProfileAdapter(getContext(), allPosts);
        rvPosts.setAdapter(adapter);
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 3);
        rvPosts.setLayoutManager(manager);

        ParseFile profilePic = (ParseFile) user.get("profilePic");
        //Log.i("pfp", this.user.get("profilePic").toString());
        if (profilePic != null) {
            Glide.with(getContext()).load(profilePic.getUrl()).into(ivProfileImg);
        } else {
            ivProfileImg.setImageResource(R.drawable.nopfp);
        }
        tvProfileUser.setText(user.getUsername());

        int randomNum = ThreadLocalRandom.current().nextInt(0, 100 + 1);
        int randomNum2 = ThreadLocalRandom.current().nextInt(0, 100 + 1);

        tvFollowing.setText(Integer.toString(randomNum));
        tvFollowers.setText(Integer.toString(randomNum2));

        if (user.get("bio") != null) {
            String bio = (String) user.get("bio");
            tvBio.setText(bio);
        } else {
            tvBio.setText("No bio yet");
        }

        if (user.get("name") != null) {
            String name = (String) user.get("name");
            tvName.setText(name);
        } else {
            tvName.setText(user.getUsername());
        }

        try {
            queryPosts(user);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void fetchTimelineAsync(int i) throws ParseException {
        // Remember to CLEAR OUT old items before appending in the new ones
        adapter.clear();
        // ...the data has come back, add new items to your adapter...
        queryPosts(user);
        adapter.addAll(allPosts);
        // Now we call setRefreshing(false) to signal refresh has finished
        swipeContainer.setRefreshing(false);
    }

    protected void queryPosts(ParseUser user) throws ParseException {
        // specify what type of data we want to query - Post.class
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        // include data referred by user key
        query.include(Post.KEY_USER);
        query.whereEqualTo(Post.KEY_USER, user);
        tvNumPosts.setText(Integer.toString(query.count()));
        // limit query to latest 20 items
        query.setLimit(20);
        // order posts by creation date (newest first)
        query.addDescendingOrder("createdAt");
        // start an asynchronous call for posts
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                // check for errors
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }

                // for debugging purposes let's print every post description to logcat
                for (Post post : posts) {
                    Log.i(TAG, "Post: " + post.getDescription() + ", username: " + post.getUser().getUsername());
                }

                // save received posts to list and notify adapter of new data
                allPosts.addAll(posts);
                adapter.notifyDataSetChanged();
            }
        });
    }
}