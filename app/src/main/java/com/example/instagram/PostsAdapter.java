package com.example.instagram;

import android.content.Context;
import android.os.Bundle;
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
import com.parse.ParseFile;
import com.parse.ParseUser;

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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAuthor = itemView.findViewById(R.id.tvAuthorDet);
            ivImgPost = itemView.findViewById(R.id.ivImgPostDet);
            tvDescPost = itemView.findViewById(R.id.tvDescPostDet);
            tvTimestamp = itemView.findViewById(R.id.tvTimestampDet);
            ivProfileImage = itemView.findViewById(R.id.ivChangeProfilePic);
            btnLike = itemView.findViewById(R.id.btnLikeDet);
            btnComment = itemView.findViewById(R.id.btnCommentDet);
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

            //boolean isLiked = false;

            btnLike.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    btnLike.setBackgroundResource(R.drawable.ufi_heart_active);
//            if (isLiked) {
//                btnLike.setImageResource(R.drawable.ufi_heart);
//                //isLiked = false;
//            } else {
//                btnLike.setImageResource(R.drawable.ufi_heart_active);
//                //isLiked = true;
//            }

                }
            });

            btnComment.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                }
            });


        }
    }

}