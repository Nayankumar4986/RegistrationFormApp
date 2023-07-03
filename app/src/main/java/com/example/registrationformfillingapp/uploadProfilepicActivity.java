package com.example.registrationformfillingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class uploadProfilepicActivity extends AppCompatActivity {

    private ProgressBar progbar;
    private ImageView imgview;
    private FirebaseAuth authprofile;
    private StorageReference storageref;
    private FirebaseUser firebaseuser;

    private static final int PICK_IMAGE_REQUEST=1;

    private  Uri uriImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_profilepic);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Upload Profile Picture");
        }

        // Get references to the UI elements

        Button  btnupload_choose = findViewById(R.id.upload_pic_choose_button);
        Button btnuploadpic_choose = findViewById(R.id.upload_pic_button);
        progbar = findViewById(R.id.progressBar2);
        imgview = findViewById(R.id.imageView_profile_dp);

        // Initialize Firebase components
        authprofile = FirebaseAuth.getInstance();
        firebaseuser = authprofile.getCurrentUser();
        storageref = FirebaseStorage.getInstance().getReference("DisplayPics");

        // Get the user's photo URL from FirebaseUser
        Uri uri = firebaseuser.getPhotoUrl();

        // Set user's current profile picture in the ImageView using Picasso library
        Picasso.get().load(uri).into(imgview);

        btnupload_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFilechooser();
            }
        });


        btnuploadpic_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progbar.setVisibility(View.VISIBLE);
                uploadpic();
            }
        });
    }

    private void uploadpic() {
        if (uriImage != null) {
            StorageReference fileref = storageref.child(authprofile.getCurrentUser().getUid() + "." + getFileextension(uriImage));

            // Upload image to storage
            fileref.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Get the download URL of the uploaded image
                    fileref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri downloaduri = uri;
                            firebaseuser = authprofile.getCurrentUser();

                            // Update the user's profile with the downloaded image URL
                            UserProfileChangeRequest profileupdate = new UserProfileChangeRequest.Builder().setPhotoUri(downloaduri).build();
                            firebaseuser.updateProfile(profileupdate);
                        }
                    });

                    progbar.setVisibility(View.GONE);
                    Toast.makeText(uploadProfilepicActivity.this, "Upload successful!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(uploadProfilepicActivity.this, userProfileActivity.class);
                    startActivity(intent);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(uploadProfilepicActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            progbar.setVisibility(View.GONE);
            Toast.makeText(uploadProfilepicActivity.this, "No file selected!", Toast.LENGTH_SHORT).show();
        }
    }



    private String getFileextension(Uri uriImage) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return  mime.getExtensionFromMimeType(cr.getType(uriImage));
    }


    private void openFilechooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode ==PICK_IMAGE_REQUEST && resultCode ==RESULT_OK && data.getData() != null){
            uriImage = data.getData();
            imgview.setImageURI(uriImage);
        }
    }
}
