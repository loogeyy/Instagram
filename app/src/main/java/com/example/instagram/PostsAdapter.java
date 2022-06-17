package com.example.instagram;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram.fragments.DetailsProfileFragment;
import com.example.instagram.fragments.PostDetailsFragment;
import com.example.instagram.fragments.ProfileFragment;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Date;
import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private Context context;
    private List<Post> posts;
    private FragmentManager fragmentManager;

    public PostsAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
        this.fragmentManager = ((MainActivity) context).getSupportFragmentManager();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        //Log.d("Post Count: ", Integer.toString(posts.size()));
        return posts.size();
    }

    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Post> list) {
        posts.addAll(list);
        notifyDataSetChanged();
    }



    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvAuthor;
        private ImageView ivImgPost;
        private TextView tvDescPost;
        private TextView tvTimestamp;
        private ImageView ivProfileImage;
        private ImageButton btnLike;
        private ImageButton btnComment;
        private TextView tvDescUser;
        private TextView tvLikeCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAuthor = itemView.findViewById(R.id.tvAuthorDet);
            ivImgPost = itemView.findViewById(R.id.ivImgPostDet);
            tvDescPost = itemView.findViewById(R.id.tvDescPostDet);
            tvTimestamp = itemView.findViewById(R.id.tvTimestampDet);
            ivProfileImage = itemView.findViewById(R.id.ivChangeProfilePic);
            btnLike = itemView.findViewById(R.id.btnLikeDet);
            btnComment = itemView.findViewById(R.id.btnCommentDet);
            tvDescUser = itemView.findViewById(R.id.tvDescUser);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCountDet);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = new PostDetailsFragment();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("post", posts.get(getAdapterPosition()));
                    fragment.setArguments(bundle);
                    fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                }
            });
        }

        public void bind(Post post) {
            // Bind the post data to the view elements
            tvDescPost.setText(post.getDescription());
            tvAuthor.setText(post.getUser().getUsername());
            tvDescUser.setText(post.getUser().getUsername());
            ParseFile image = post.getImage();
            if (image != null) {
                Glide.with(context).load(image.getUrl()).into(ivImgPost);
            }
            ParseFile profilePic = (ParseFile) post.getUser().get("profilePic");

            if (profilePic != null) {
                Glide.with(context).load(profilePic.getUrl()).into(ivProfileImage);
            } else {
                ivProfileImage.setImageResource(R.drawable.nopfp);
            }

            Date createdAt = post.getCreatedAt();
            String timeAgo = Post.calculateTimeAgo(createdAt);
            tvTimestamp.setText(timeAgo);

            tvAuthor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Clicked profile!", Toast.LENGTH_SHORT).show();
                    Fragment fragment;
                    if (post.getUser().getUsername().equals(ParseUser.getCurrentUser().getUsername())) {
                        fragment = new ProfileFragment(post.getUser());
                    } else {
                        fragment = new DetailsProfileFragment(post.getUser());
                    }
                    fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                }
            });

            likeInitialize(post);

            btnLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    like(post);
                }
            });

            btnComment.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

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
                        tvLikeCount.setText(Integer.toString(query.count()) + " likes");
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
                        btnLike.setBackgroundResource(R.drawable.ufi_heart);
                    }
                    else {
                        btnLike.setBackgroundResource(R.drawable.ufi_heart_active);
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
                        btnLike.setBackgroundResource(R.drawable.ufi_heart_active);
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
                        btnLike.setBackgroundResource(R.drawable.ufi_heart);
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

}