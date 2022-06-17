package com.example.instagram.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.instagram.Post;
import com.example.instagram.R;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.Date;

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

        /*
        if (getActivity().getIntent().getExtras() != null) {
            post = (Post) getActivity().getIntent().getParcelableExtra("post");
        } */

        tvAuthorDet = view.findViewById(R.id.tvAuthorDet);
        ivImgPostDet = view.findViewById(R.id.ivImgPostDet);
        tvDescPostDet = view.findViewById(R.id.tvDescPostDet);
        tvTimestampDet = view.findViewById(R.id.tvTimestampDet);
        ivProfileImageDet = view.findViewById(R.id.ivChangeProfilePic);
        btnLikeDet = view.findViewById(R.id.btnLikeDet);
        btnCommentDet = view.findViewById(R.id.btnCommentDet);
        tvDescUserDet = view.findViewById(R.id.tvDescUserDet);
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

        btnLikeDet.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                btnLikeDet.setBackgroundResource(R.drawable.ufi_heart_active);
//            if (isLiked) {
//                btnLike.setImageResource(R.drawable.ufi_heart);
//                //isLiked = false;
//            } else {
//                btnLike.setImageResource(R.drawable.ufi_heart_active);
//                //isLiked = true;
//            }

            }
        });
    }

}