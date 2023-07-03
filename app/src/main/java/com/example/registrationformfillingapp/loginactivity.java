package com.example.registrationformfillingapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class loginactivity extends AppCompatActivity {

    private EditText editTextloginemail, editTextloginpassword;
    private ProgressBar progressbar;
    private FirebaseAuth authprofile;


    private static final String TAG = "loginactivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginactivity);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("LOGIN");
        }

        editTextloginemail = findViewById(R.id.editText_login_email);
        editTextloginpassword = findViewById(R.id.editText_login_pwd);
        progressbar = findViewById(R.id.progressBar);


        authprofile = FirebaseAuth.getInstance();



        //show hide pwd image icon

        ImageView imageViewShowHidePwd = findViewById(R.id.imageView_show_hide_pwd);
        imageViewShowHidePwd.setImageResource(R.drawable.baseline_remove_red_eye_24);

        imageViewShowHidePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTextloginpassword.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())) {
                    editTextloginpassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    //change icon eye
                    imageViewShowHidePwd.setImageResource(R.drawable.baseline_remove_red_eye_24);
                } else {
                    editTextloginpassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

                    //change icon eye
                    imageViewShowHidePwd.setImageResource(R.drawable.baseline_hide_source_24);
                }
            }
        });



        TextView forgetpss = findViewById(R.id.edittextForget_Pwd);
        forgetpss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(loginactivity.this, "You can reset the password now!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(loginactivity.this,forgetActivity.class));
            }
        });

        TextView textView = findViewById(R.id.textView_register_link);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(loginactivity.this, "You can register now", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(loginactivity.this,Registeractivity.class));
            }
        });




        //login user
        Button buttonLogin = findViewById(R.id.button_login);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textemail = editTextloginemail.getText().toString();
                String textpwd = editTextloginpassword.getText().toString();

                if (TextUtils.isEmpty(textemail)) {
                    Toast.makeText(loginactivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    editTextloginemail.setError("Email is required");
                    editTextloginemail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textemail).matches()) {
                    Toast.makeText(loginactivity.this, "Please re-enter Your email", Toast.LENGTH_LONG).show();
                    editTextloginemail.setError("Valid Email is required");
                    editTextloginemail.requestFocus();
                } else if (TextUtils.isEmpty(textpwd)) {
                    Toast.makeText(loginactivity.this, "Please enter your Password.", Toast.LENGTH_LONG).show();
                    editTextloginpassword.setError("Password is required");
                    editTextloginpassword.requestFocus();
                } else {
                    progressbar.setVisibility(View.VISIBLE);
                    loginUser(textemail, textpwd);
                }
            }
        });

    }

    private void loginUser(String email , String pwd) {
        authprofile.signInWithEmailAndPassword(email, pwd).addOnCompleteListener( loginactivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    ///get instance of the current user
                    FirebaseUser firebaseUser = authprofile.getCurrentUser();

                    //check user  is verfied email address or not
                    if (firebaseUser.isEmailVerified()) {
                        Toast.makeText(loginactivity.this, "You are Logged in now", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(loginactivity.this,userProfileActivity.class));
                        finish();

                    } else {
                        firebaseUser.sendEmailVerification();
                        authprofile.signOut();
                        showAlertDialog();
                    }

                } else {

                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e) {
                        editTextloginemail.setError("User does not exists or no longer valid. please register again");
                        editTextloginemail.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        editTextloginemail.setError("Invalid Credential. Kindly, Check and re-enter.");
                        editTextloginemail.requestFocus();
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(loginactivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                }
                progressbar.setVisibility(View.GONE);
            }
        });
    }


    //email verify
    private void showAlertDialog() {

        //setup
        AlertDialog.Builder builder = new AlertDialog.Builder(loginactivity.this);
        builder.setTitle("Email Not Verified");
        builder.setMessage("Please verify your email now. you cannot login without email verification");

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


///check if user is already logged in then staringway take the user to user profile

//    protected void onstart() {
//        super.onStart();
//        if (authprofile.getCurrentUser() != null) {
//            Toast.makeText(loginactivity.this, "Already logged in", Toast.LENGTH_SHORT).show();
//
//            startActivity(new Intent(loginactivity.this,userProfileActivity.class));
//            finish();
//        }else {
//            Toast.makeText(loginactivity.this, "You can login now!", Toast.LENGTH_SHORT).show();
//        }
//    }
}
