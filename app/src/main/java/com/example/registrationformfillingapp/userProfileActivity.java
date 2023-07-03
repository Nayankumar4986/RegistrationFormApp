package com.example.registrationformfillingapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class userProfileActivity extends AppCompatActivity {

    private TextView textViewwelcome, textViewfullname, textViewemail, textViewdob, showgen, textViewmobile;
    private ProgressBar progreess;
    private String fullname, email, dob, gender, mobile;
    private ImageView imgview;
    private FirebaseAuth authprofile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userprofile);


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Home");
        }

        textViewwelcome = findViewById(R.id.textView_show_welcome);
        textViewfullname = findViewById(R.id.textView_show_full_name);
        textViewemail = findViewById(R.id.textView_show_email);
        textViewdob = findViewById(R.id.textView_show_dob);
        showgen = findViewById(R.id.show_gender);
        textViewmobile = findViewById(R.id.textView_show_mobile);


        progreess = findViewById(R.id.progress_bar);


        //set imgview to upload profile picture
        imgview = findViewById(R.id.imageView_profile_dp);
        imgview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(userProfileActivity.this,uploadProfilepicActivity.class);
                startActivity(intent);
            }
        });

        authprofile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authprofile.getCurrentUser();
        if (firebaseUser == null) {
            Toast.makeText(userProfileActivity.this, "Something went go wrong user detail are not available at the moment", Toast.LENGTH_SHORT).show();

        } else {
            checkemailverfied(firebaseUser);
            progreess.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);
        }

        imgview = findViewById(R.id.imageView_profile_dp);


    }




    private void checkemailverfied(FirebaseUser firebaseUser) {

        if (!firebaseUser.isEmailVerified()) {
            showAlertDialog();
        }
    }

    private void showAlertDialog() {
        //setup
        AlertDialog.Builder builder = new AlertDialog.Builder(userProfileActivity.this);
        builder.setTitle("Email Not Verified");
        builder.setMessage("Please verify your email now. you cannot login without email verification next time");

        //open email appp continue
        builder.setPositiveButton("CONTINUE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        //create alert dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void showUserProfile(FirebaseUser firebaseUser) {
        String userId = firebaseUser.getUid();


        //extracting user reference drom db
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
        referenceProfile.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readuserdetails = snapshot.getValue(ReadWriteUserDetails.class);
                if (readuserdetails != null) {

                    fullname = firebaseUser.getDisplayName();
                    email = firebaseUser.getEmail();
                    dob = readuserdetails.dob;
                    mobile = readuserdetails.mobile;
                    gender = readuserdetails.gender;
                    fullname = readuserdetails.fullname;
                    textViewwelcome.setText("😉Welcome, " + fullname + "!");
                    textViewfullname.setText(fullname);
                    textViewemail.setText(email);
                    textViewdob.setText(dob);
                    textViewmobile.setText(mobile);
                    showgen.setText(gender);


                    //set user dp after uplaod
                    Uri uri = firebaseUser.getPhotoUrl();

                    Picasso.get().load(uri).into(imgview);
                }else {
                    Toast.makeText(userProfileActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
                progreess.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(userProfileActivity.this, "Something Went Wrong!", Toast.LENGTH_LONG).show();
                progreess.setVisibility(View.GONE);

            }
        });
    }


    ///creating action bar menu
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.common_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //whwn any menu items selected
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menuRefresh) {
            //refresh the page
            startActivity(getIntent());
            finish();
            overridePendingTransition(0,0);
//        } else if (id == R.id.updateProfile) {
//            Intent intent = new Intent(userProfileActivity.this,userProfileActivity.class);
//            startActivity(intent);
//
//        } else if (id == R.id.updateemail) {
//            Intent intent = new Intent(userProfileActivity.this,userProfileActivity.class);
//            startActivity(intent);

        } else if (id == R.id.settings) {
            Toast.makeText(this, "Menu_settings", Toast.LENGTH_SHORT).show();

//        } else if (id== R.id.changepass) {
//            Intent intent = new Intent(userProfileActivity.this,ChngepassActivity.class);
//            startActivity(intent);

//        } else if (id==R.id.delteprof) {
//
//            Intent intent = new Intent(userProfileActivity.this,DeleteProfileActivity.class);
//            startActivity(intent);
        } else if (id==R.id.logout) {
            authprofile.signOut();
            Toast.makeText(this, "User Has been Signed Out!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(userProfileActivity.this,MainActivity.class);

//            CHECK STACK TO PREVENT USER COMEback to userprofile on pass back press btn after login

            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }else {
            Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}




