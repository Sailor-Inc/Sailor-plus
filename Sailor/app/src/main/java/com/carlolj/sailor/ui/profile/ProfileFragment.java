package com.carlolj.sailor.ui.profile;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.carlolj.sailor.BitmapScaler;
import com.carlolj.sailor.R;
import com.carlolj.sailor.activities.StartActivity;
import com.carlolj.sailor.adapters.ProfileAdapter;
import com.carlolj.sailor.controllers.PushNotifications;
import com.carlolj.sailor.databinding.FragmentProfileBinding;
import com.carlolj.sailor.models.Follows;
import com.carlolj.sailor.models.Post;
import com.carlolj.sailor.ui.explore.PinFragment;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private static final int RESULT_PICK = 1;
    private static final int RESULT_TAKE = 2;
    private static final int ACCEPT_CAMERA = 20;
    private static final int CODE_REMOVE = 0;
    private static final int CODE_ADD = 1;
    public static final int CODE_STARTED = 1;
    public static final int CODE_STOPPED = 2;

    ParseFile newProfileImage;

    ParseUser currentUser;
    private FragmentProfileBinding binding;
    ImageView ivProfilePicture, ivSelectionBox;
    TextView tvUsername;
    Button btnFollow;
    RecyclerView rvProfilePosts;
    TextView tvFollowers, tvFollowing;

    protected ProfileAdapter adapter;
    protected List<Post> allPosts;

    public ProfileFragment(ParseUser user){
        currentUser = user;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ivProfilePicture = binding.ivProfilePicture;
        tvUsername = binding.tvUsername;
        btnFollow = binding.btnFollow;
        ivSelectionBox = binding.ivSelectionBox;
        rvProfilePosts = binding.ivProfilePosts;
        tvFollowers = binding.tvFollowers;
        tvFollowing = binding.tvFollowing;

        allPosts = new ArrayList<>();
        adapter = new ProfileAdapter(getContext(), allPosts, this);

        tvUsername.setText(currentUser.getUsername());

        followsUpdate(tvFollowers, tvFollowing, btnFollow, currentUser.getObjectId());

        tvFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new FollowsFragment(currentUser);

                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flContainer, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        tvFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new FollowsFragment(currentUser);

                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flContainer, fragment)
                        .commit();
            }
        });

        Glide.with(getContext()).load(currentUser.getParseFile("profilePicture").getUrl()).circleCrop().into(ivProfilePicture);
        if (!ParseUser.getCurrentUser().getUsername().equals(currentUser.getUsername())) {
            ivSelectionBox.setVisibility(View.GONE);
            btnFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnFollow.setClickable(false);
                    setFollow(currentUser, tvFollowers, btnFollow);
                }
            });
        } else {
            btnFollow.setVisibility(View.GONE);
            ivSelectionBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle(R.string.choose_menu_option)
                            .setItems(R.array.menu_options, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 0:
                                            chooseProfilePicture();
                                            dialog.cancel();
                                            break;
                                        default:
                                            onLogout();
                                            dialog.cancel();
                                            break;
                                    }
                                }
                            });
                    final AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });
            ivProfilePicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chooseProfilePicture();
                }
            });
        }
        rvProfilePosts.setAdapter(adapter);
        rvProfilePosts.setLayoutManager(new GridLayoutManager(getContext(), 2));
        queryPosts();
    }

    /**
     * This method will follow or unfollow a userId
     * @param tvFollowers the Textview of the number of followers
     * @param btnFollow a Button that tells if the user is already followed
     */
    private void setFollow(ParseUser parseUser, TextView tvFollowers, Button btnFollow) {
        ParseQuery<Follows> query = ParseQuery.getQuery(Follows.class);
        query.whereEqualTo(Follows.KEY_USER_ID, parseUser.getObjectId());
        query.findInBackground(new FindCallback<Follows>() {
            @Override
            public void done(List<Follows> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting object", e);
                    btnFollow.setClickable(true);
                    return;
                }
                if (objects != null) {
                    if (objects.get(0).isFollowed()) {
                        doFollow(objects.get(0),parseUser,tvFollowers,btnFollow, CODE_REMOVE);
                    } else {
                        doFollow(objects.get(0), parseUser, tvFollowers, btnFollow, CODE_ADD);
                    }
                }
            }
        });
    }

    /**
     * This method will add/remove a follower and execute a query to save it then it will run a method
     * to update the following list inside the current user following list
     * @param follows a Follows object
     * @param tvFollowers a TextView of the number of followers
     * @param btnFollow a Button that tells if the user is already followed
     * @param code an Integer unique code that tells if the user is already following the user
     */
    private void doFollow(Follows follows, ParseUser parseUser, TextView tvFollowers, Button btnFollow, int code) {
        switch (code) {
            case CODE_ADD:
                follows.addFollower(ParseUser.getCurrentUser());
                break;
            case CODE_REMOVE:
                follows.removeFollower(ParseUser.getCurrentUser());
                break;
        }
        follows.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue updating followers", e);
                    btnFollow.setClickable(true);
                    return;
                }
                switch (code) {
                    case CODE_ADD:
                        doFollowing(parseUser, btnFollow, tvFollowers, follows, CODE_ADD);
                        break;
                    case CODE_REMOVE:
                        doFollowing(parseUser, btnFollow, tvFollowers, follows, CODE_REMOVE);
                        break;
                }
            }
        });
    }

    /**
     * This method will add or remove a following user to the current user account and will update the
     * button and text inside the selected profile, so the user can see if the selected user is already
     * followed
     * @param btnFollow a Button that tells if the user is already followed
     * @param tvFollowers a TextView of the number of followers
     * @param follows a Button that tells if the user is already followed
     * @param code an Integer unique code that tells if the user is already following the user
     */
    private void doFollowing(ParseUser parseUser, Button btnFollow, TextView tvFollowers, Follows follows, int code) {
        ParseQuery<Follows> query = ParseQuery.getQuery(Follows.class);
        query.whereEqualTo(Follows.KEY_USER_ID, ParseUser.getCurrentUser().getObjectId());
        query.findInBackground(new FindCallback<Follows>() {
            @Override
            public void done(List<Follows> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting object", e);
                    btnFollow.setClickable(true);
                    return;
                }
                switch (code) {
                    case CODE_ADD:
                        objects.get(0).addFollowing(parseUser);
                        break;
                    case CODE_REMOVE:
                        objects.get(0).removeFollowing(parseUser);
                        break;
                }
                objects.get(0).saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Issue updating object", e);
                            btnFollow.setClickable(true);
                            return;
                        }
                        switch (code) {
                            case CODE_ADD:
                                PushNotifications.sendNewFollowsNotification(parseUser, CODE_STARTED);
                                tvFollowers.setText(String.valueOf(follows.getFollowersNumber()));
                                btnFollow.setBackgroundColor(getResources().getColor(R.color.white));
                                btnFollow.setTextColor(getResources().getColor(R.color.black));
                                btnFollow.setText("Unfollow");
                                btnFollow.setClickable(true);
                                Toast.makeText(getContext(), "Followed and following process success", Toast.LENGTH_SHORT).show();
                                break;
                            case CODE_REMOVE:
                                PushNotifications.sendNewFollowsNotification(parseUser, CODE_STOPPED);
                                tvFollowers.setText(String.valueOf(follows.getFollowersNumber()));
                                btnFollow.setBackgroundColor(getResources().getColor(R.color.black));
                                btnFollow.setTextColor(getResources().getColor(R.color.white));
                                btnFollow.setText("Follow");
                                btnFollow.setClickable(true);
                                Toast.makeText(getContext(), "Remove followed and following process success", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
            }
        });
    }

    /**
     * This method will check if a selected user is already followed by the current user
     * @param tvFollowers a TextView of the number of followers
     * @param tvFollowing a TextView of the number of following
     * @param btnFollow a Button that tells if the user is already followed
     * @param userId  String of the userId to see if it's followed by the current user
     */
    private void followsUpdate(TextView tvFollowers, TextView tvFollowing, Button btnFollow,String userId) {
        btnFollow.setClickable(false);
        ParseQuery<Follows> query = ParseQuery.getQuery(Follows.class);
        query.whereEqualTo(Follows.KEY_USER_ID, userId);
        query.findInBackground(new FindCallback<Follows>() {
            @Override
            public void done(List<Follows> objects, ParseException e) {
                if (e != null){
                    Log.e(TAG, "Issue with getting object", e);
                    return;
                }
                if (objects.get(0).isFollowed()) {
                    btnFollow.setBackgroundColor(getResources().getColor(R.color.white));
                    btnFollow.setTextColor(getResources().getColor(R.color.black));
                    btnFollow.setText("Unfollow");
                }
                tvFollowers.setText(String.valueOf(objects.get(0).getFollowersNumber()));
                tvFollowing.setText(String.valueOf(objects.get(0).getFollowingNumber()));
                btnFollow.setClickable(true);
            }
        });
    }

    protected void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_AUTHOR);
        query.whereEqualTo(Post.KEY_AUTHOR, currentUser);
        query.setLimit(20);
        query.addDescendingOrder(Post.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                allPosts.addAll(posts);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void onLogout() {
        // forget who's logged in
        ParseUser.logOut();

        // navigate backwards to Login screen
        Intent i = new Intent(getContext(), StartActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // this makes sure the Back button won't work
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // same as above
        startActivity(i);
        getActivity().finish();
    }

    private void chooseProfilePicture() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.choose_camera_option)
                .setItems(R.array.camera_options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                if (checkAndRequestPermissions()) {
                                    takePictureFromCamera();
                                    dialog.cancel();
                                }
                                break;
                            default:
                                takePictureFromGallery();
                                dialog.cancel();
                                break;
                        }
                    }
                });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private boolean checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            int cameraPermission = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA);
            if (cameraPermission == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(getActivity()
                        ,new String[]{Manifest.permission.CAMERA}
                        ,ACCEPT_CAMERA);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACCEPT_CAMERA && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            takePictureFromCamera();
        } else {
            Toast.makeText(getContext(), "Permission not granted", Toast.LENGTH_SHORT).show();
        }
    }

    private void takePictureFromCamera() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePicture.resolveActivity(getContext().getPackageManager()) != null) {
            startActivityForResult(takePicture, RESULT_TAKE);
        }
    }

    private void takePictureFromGallery() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, RESULT_PICK);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == -1) {
                    Uri selectedImageUri = data.getData();

                    Bitmap selectedImage = loadFromUri(selectedImageUri);
                    loadNewPhoto(selectedImage);
                } else { // Result was a failure
                    Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
                }
                break;
            case 2:
                if (resultCode == -1) {
                    Bundle bundle = data.getExtras();
                    Bitmap bitmapImage = (Bitmap) bundle.get("data");

                    loadNewPhoto(bitmapImage);
                }
                break;
        }
    }

    public Bitmap loadFromUri(Uri photoUri) {
        Bitmap image = null;
        try {
            // check version of Android on device
            if (Build.VERSION.SDK_INT > 27) {
                // on newer versions of Android, use the new decodeBitmap method
                ImageDecoder.Source source = ImageDecoder.createSource(getActivity().getContentResolver(), photoUri);
                image = ImageDecoder.decodeBitmap(source);
            } else {
                // support older versions of Android by using getBitmap
                image = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), photoUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public void loadNewPhoto(Bitmap selectedImage) {
        Bitmap resizedBitmap = BitmapScaler.scaleToFitWidth(selectedImage, 800);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

        byte[] byteArray = stream.toByteArray();

        newProfileImage = new ParseFile("updated.png", byteArray);

        newProfileImage.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Profile image wasn't updated", e);
                    return;
                }
                ParseUser.getCurrentUser().put("profilePicture", newProfileImage);
                ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Profile image wasn't updated", e);
                            return;
                        }
                        Toast.makeText(getContext(), "Image updated!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        ivProfilePicture.setBackground(null);
        Glide.with(getContext()).load(byteArray).circleCrop().into(ivProfilePicture);
    }
}