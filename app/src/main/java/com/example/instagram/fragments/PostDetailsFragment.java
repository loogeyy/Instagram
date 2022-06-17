package com.example.instagram.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.instagram.Like;
import com.example.instagram.Post;
import com.example.instagram.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Date;
import java.util.List;

public class PostDetailsFragment extends Fragment {
    private TextView tvAuthorDet;
    private ImageView ivImgPostDet;
    private TextView tvDescPostDet;
    private TextView tvTimestampDet;
    private ImageView ivProfileImageDet;
    private ImageButton btnLikeDet;
    private ImageButton btnCommentDet;
    private Post post;
    private TextView tvDescUserDet;
    private TextView tvLikeCountDet;

    public PostDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            post = bundle.getParcelable("post");
        }

        tvAuthorDet = view.findViewById(R.id.tvAuthorDet);
        ivImgPostDet = view.findViewById(R.id.ivImgPostDet);
        tvDescPostDet = view.findViewById(R.id.tvDescPostDet);
        tvTimestampDet = view.findViewById(R.id.tvTimestampDet);
        ivProfileImageDet = view.findViewById(R.id.ivChangeProfilePic);
        btnLikeDet = view.findViewById(R.id.btnLikeDet);
        btnCommentDet = view.findViewById(R.id.btnCommentDet);
        tvDescUserDet = view.findViewById(R.id.tvDescUserDet);
        tvLikeCountDet = view.findViewById(R.id.tvLikeCountDet);

        tvDescPostDet.setText(post.getDescription());
        tvAuthorDet.setText(post.getUser().getUsername());
        ParseFile image = post.getImage();
        if (image != null) {
            Glide.with(getContext()).load(image.getUrl()).into(ivImgPostDet);
        }

        ParseFile profilePic = (ParseFile) post.getUser().get("profilePic");
        if (profilePic != null) {
            Glide.with(getContext()).load(profilePic.getUrl()).into(ivProfileImageDet);
        } else {
            ivProfileImageDet.setImageResource(R.drawable.nopfp);
        }

        Date createdAt = post.getCreatedAt();
        String timeAgo = Post.calculateTimeAgo(createdAt);
        tvTimestampDet.setText(timeAgo);

        tvAuthorDet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                Fragment fragment;
                if (post.getUser().getUsername().equals(ParseUser.getCurrentUser().getUsername())) {
                    fragment = new ProfileFragment(post.getUser());
                } else {
                    fragment = new DetailsProfileFragment(post.getUser());
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
            }
        });
        tvDescUserDet.setText(post.getUser().getUsername());

        likeInitialize(post);

        btnLikeDet.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
               like(post);
            }
        });
    }

    private void updateLikeCount(Post post) {
        ParseQuery<Like> query = ParseQuery.getQuery(Like.class);
        query.whereEqualTo(Like.KEY_POST, post);
        query.findInBackground(new FindCallback<Like>() {
            @Override
            public void done(List<Like> objects, ParseException e) {
                if (e != null) {
                    Log.e("likes", "Issue with getting likes", e);
                }
                try {
                    tvLikeCountDet.setText(Integer.toString(query.count()) + " likes");
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void likeInitialize(Post post) {
        ParseQuery<Like> query = ParseQuery.getQuery(Like.class);
        query.whereEqualTo(Like.KEY_USER, ParseUser.getCurrentUser());
        query.whereEqualTo(Like.KEY_POST, post);
        query.findInBackground(new FindCallback<Like>() {
            @Override
            public void done(List<Like> likes, ParseException e) {
                if (e != null) {
                    Log.e("likes", "Issue with getting likes", e);
                }
                if (likes.size() == 0) {
                    btnLikeDet.setBackgroundResource(R.drawable.ufi_heart);
                }
                else {
                    btnLikeDet.setBackgroundResource(R.drawable.ufi_heart_active);
                }
                updateLikeCount(post);
            }
        });
    }

    private void like(Post post) {
        ParseQuery<Like> query = ParseQuery.getQuery(Like.class);
        query.whereEqualTo(Like.KEY_USER, ParseUser.getCurrentUser());
        query.whereEqualTo(Like.KEY_POST, post);
        query.findInBackground(new FindCallback<Like>() {
            @Override
            public void done(List<Like> likes, ParseException e) {
                if (e != null) {
                    Log.e("likes", "Issue with getting likes", e);
                }
                // liking
                if (likes.size() == 0) {
                    btnLikeDet.setBackgroundResource(R.drawable.ufi_heart_active);
                    Like like = new Like();
                    like.setPost(post);
                    like.setUser(ParseUser.getCurrentUser());
                    like.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            Log.e("Liking", "Error Liking: ", e);
                        }
                    });
                }
                //unliking
                else {
                    btnLikeDet.setBackgroundResource(R.drawable.ufi_heart);
                    query.findInBackground(new FindCallback<Like>() {
                        @Override
                        public void done(List<Like> like, ParseException e) {
                            if(!like.isEmpty()){
                                like.get(0).deleteInBackground();
                            }
                        }
                    });
                }
                updateLikeCount(post);
            }
        });
        updateLikeCount(post);
    }


}