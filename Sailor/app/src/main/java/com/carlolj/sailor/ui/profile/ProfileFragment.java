package com.carlolj.sailor.ui.profile;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.gesture.GestureLibraries;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.carlolj.sailor.BitmapScaler;
import com.carlolj.sailor.R;
import com.carlolj.sailor.activities.LoginActivity;
import com.carlolj.sailor.activities.MainActivity;
import com.carlolj.sailor.activities.RegisterActivity;
import com.carlolj.sailor.activities.StartActivity;
import com.carlolj.sailor.adapters.ProfileAdapter;
import com.carlolj.sailor.controllers.CameraHelper;
import com.carlolj.sailor.databinding.FragmentProfileBinding;
import com.carlolj.sailor.models.Post;
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

    ParseFile newProfileImage;

    ParseUser currentUser;
    private FragmentProfileBinding binding;
    ImageView ivProfilePicture, ivSelectionBox;
    TextView tvUsername;
    Button btnFollow;
    RecyclerView rvProfilePosts;

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

        allPosts = new ArrayList<>();
        adapter = new ProfileAdapter(getContext(), allPosts);

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

        tvUsername.setText(currentUser.getUsername());
        Glide.with(getContext()).load(currentUser.getParseFile("profilePicture").getUrl()).circleCrop().into(ivProfilePicture);
        if (!ParseUser.getCurrentUser().getUsername().equals(currentUser.getUsername())) {
            btnFollow.setVisibility(View.VISIBLE);
            btnFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //onFollow();
                }
            });
        } else {
            btnFollow.setVisibility(View.INVISIBLE);
        }
        rvProfilePosts.setAdapter(adapter);
        rvProfilePosts.setLayoutManager(new GridLayoutManager(getContext(), 2));
        queryPosts();
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
            if (cameraPermission == PackageManager.PERMISSION_DENIED){
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