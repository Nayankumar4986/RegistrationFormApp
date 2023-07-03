package com.example.registrationformfillingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class forgetActivity extends AppCompatActivity {

    private Button btnpassreset;
   private EditText edittextpass;

   private ProgressBar progbar;
   private FirebaseAuth authprofile;
   private final static String TAG = "fogetActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foget);

        btnpassreset= findViewById(R.id.button_password_reset);
        edittextpass = findViewById(R.id.editText_password_reset_email);

        progbar=findViewById(R.id.progressBar1);

        btnpassreset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edittextpass.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(forgetActivity.this, "Please enter your password email", Toast.LENGTH_SHORT).show();
                    edittextpass.setError("Email is required");
                    edittextpass.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

                    Toast.makeText(forgetActivity.this, "Please enter valid email", Toast.LENGTH_SHORT).show();
                    edittextpass.setError("Valid email is required");
                    edittextpass.requestFocus();
                }else {
                    progbar.setVisibility(view.VISIBLE);
                    resetpassword(email);
                }
            }
        });



    }

    private void resetpassword(String email) {
        authprofile = FirebaseAuth.getInstance();
        authprofile.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(forgetActivity.this, "Please check your inbox for password reset link", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(forgetActivity.this,MainActivity.class);

//            CHECK STACK TO PREVENT USER COMEback to userprofile on pass back press btn after login

                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }else {

                    try{
                       throw task.getException();
                    }catch (FirebaseAuthInvalidUserException e){
                        edittextpass.setError("User does not exists or no longer valid. Please register again");
                    }catch (Exception e){
                        Log.e(TAG ,e.getMessage());
                        Toast.makeText(forgetActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }
                progbar.setVisibility(View.GONE);
            }
        });
    }
}