package com.example.registrationformfillingapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ktx.Firebase;

import java.security.PrivateKey;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Registeractivity extends AppCompatActivity {

    private EditText regfullname , regemail , regdob , regmobile , regpass, regconfirmpass;
    private ProgressBar progressbar;
    private RadioGroup radioGroupregister_gender;
    private RadioButton radiobtn_registergender;

    private DatePickerDialog picker;

    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registeractivity);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("REGISTRATION FORM");
        }

        Toast.makeText(Registeractivity.this, "Please Fill Up the Details.." ,Toast.LENGTH_SHORT).show();

        regfullname = findViewById(R.id.registerfullname);
        regemail = findViewById(R.id.registeremail);
        regdob = findViewById(R.id.registerdob);
        regmobile = findViewById(R.id.mobileregister);
        regpass = findViewById(R.id.passregister);
        regconfirmpass = findViewById(R.id.confirmpass);
        regpass = findViewById(R.id.passregister);


        progressbar = findViewById(R.id.progressbar);



        radioGroupregister_gender = findViewById(R.id.readi_grp_register_gender);
        radioGroupregister_gender.clearCheck();


        //setting up datepicker
        regdob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                //DATE PICKER
                picker = new DatePickerDialog(Registeractivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayofMonth) {
                        regdob.setText(dayofMonth + "/" + (month + 1)+ "/" +year);
                    }
                },year,month,day);
                picker.show();
            }
        });

        Button buttonreg = findViewById(R.id.btnregform);
        buttonreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int selectedgender = radioGroupregister_gender.getCheckedRadioButtonId();
                radiobtn_registergender = findViewById(selectedgender);

                //obtain the enter data
                String textfulllname = regfullname.getText().toString();
                String textemail = regemail.getText().toString();
                String textdob = regdob.getText().toString();
                String textmobile = regmobile.getText().toString();
                String pwd = regpass.getText().toString();
                String confrmpwd = regconfirmpass.getText().toString();
                String textgender;


                //validate a mobile no using matches and pattern regular expression
                String mobilereg = "[6-9][0-9]{9}";
                Matcher mobilematch;
                Pattern mobilepattern = Pattern.compile(mobilereg);
                mobilematch = mobilepattern.matcher(textmobile);

                if (TextUtils.isEmpty(textfulllname)){
                    Toast.makeText(Registeractivity.this,"Please Enter Your Full Name",Toast.LENGTH_LONG).show();
                    regfullname.setError("Full Name is required");
                    regfullname.requestFocus();
                } else if (TextUtils.isEmpty(textemail)) {
                    Toast.makeText(Registeractivity.this,"Please Enter Your email",Toast.LENGTH_LONG).show();
                    regemail.setError("Email is required");
                    regemail.requestFocus();
                }else if (!Patterns.EMAIL_ADDRESS.matcher(textemail).matches()){
                    Toast.makeText(Registeractivity.this,"Please re-enter Your email",Toast.LENGTH_LONG).show();
                    regemail.setError("Valid Email is required");
                    regemail.requestFocus();
                } else if (TextUtils.isEmpty(textdob)) {
                    Toast.makeText(Registeractivity.this,"Please enter Your date of birth",Toast.LENGTH_LONG).show();
                    regdob.setError("Date of Birth is required");
                    regdob.requestFocus();

                }else if (radioGroupregister_gender.getCheckedRadioButtonId() == -1){
                    Toast.makeText(Registeractivity.this,"Please select Your Gender",Toast.LENGTH_LONG).show();
                     radiobtn_registergender.setError("Gender is required");
                    radiobtn_registergender.requestFocus();
                } else if (TextUtils.isEmpty(textmobile)) {
                    Toast.makeText(Registeractivity.this,"Please enter Your Mobile no.",Toast.LENGTH_LONG).show();
                    regmobile.setError("Mobile No. is required have to be 10-digits");
                    regmobile.requestFocus();

                } else if (textmobile.length() !=10) {
                    Toast.makeText(Registeractivity.this,"Please re-enter Your Mobile no.",Toast.LENGTH_LONG).show();
                    regmobile.setError("Mobile No. is required");
                    regmobile.requestFocus();

                } else if (!mobilematch.find()) {
                    Toast.makeText(Registeractivity.this,"Please re-enter Your Mobile no.",Toast.LENGTH_LONG).show();
                    regmobile.setError("Mobile No. is not valid!");
                    regmobile.requestFocus();
                }
                else if (TextUtils.isEmpty(pwd)) {
                    Toast.makeText(Registeractivity.this,"Please enter your Password.",Toast.LENGTH_LONG).show();
                    regpass.setError("Mobile No. is required");
                    regconfirmpass.requestFocus();

                } else if (pwd.length()<6) {
                    Toast.makeText(Registeractivity.this,"Password should be at least 6 digits.",Toast.LENGTH_LONG).show();
                    regpass.setError("Password to week");
                    regpass.requestFocus();

                } else if (TextUtils.isEmpty(confrmpwd)) {
                    Toast.makeText(Registeractivity.this,"Please Confirm your password",Toast.LENGTH_LONG).show();
                    regconfirmpass.setError("password confirmation is required");
                    regconfirmpass.requestFocus();

                }else if (!pwd.equals(confrmpwd)){

                    Toast.makeText(Registeractivity.this,"Please enter same password",Toast.LENGTH_LONG).show();
                    regconfirmpass.setError("please enter same password");
                    regconfirmpass.requestFocus();

                    regpass.clearComposingText();
                    regconfirmpass.clearComposingText();
                }else {
                    textgender = radiobtn_registergender.getText().toString();
                    progressbar.setVisibility(View.VISIBLE);
                    registeruser(textfulllname,textemail,textdob,textgender,textmobile,pwd);
                }
            }
        });
    }





    ///REGISTER USER USING GIVEN CREDENTIAL
    private void registeruser(String textfulllname, String textemail, String textdob, String textgender, String textmobile, String pwd) {

        FirebaseAuth auth = FirebaseAuth.getInstance();

        //create user profile
        auth.createUserWithEmailAndPassword(textemail,pwd).addOnCompleteListener(Registeractivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(Registeractivity.this, "User registered Sucessfully", Toast.LENGTH_SHORT).show();
                    FirebaseUser firebaseuser = auth.getCurrentUser();

//                    //update display name of user
//                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(textfulllname).build();
//                    firebaseuser.updateProfile(profileChangeRequest);


                    //ENTER USER DATA INFO THE firebase realtime database
                    ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textfulllname,textdob,textgender,textmobile);



                    //extracting user reference fron a database for "registerd users"
                    DatabaseReference referenceprofile = FirebaseDatabase.getInstance().getReference("Registered Users");
                    referenceprofile.child(firebaseuser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                                //send verification email
                                firebaseuser.sendEmailVerification();

                                Toast.makeText(Registeractivity.this, "User registered successfully. Please Verify your email", Toast.LENGTH_SHORT).show();

                                //open user profile after successfull registration
                                Intent intent = new Intent(Registeractivity.this,loginactivity.class);
                                startActivity(intent);

                                //to prevent user from returning to regactivity on pressing back btn after registration

                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish(); //toclose the register activity
                            }else {
                                Toast.makeText(Registeractivity.this, "User Registered failed. please try again!", Toast.LENGTH_SHORT).show();
                            }
                            progressbar.setVisibility(View.GONE);
                        }
                    });


                }else {
                    try{
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e){
                        regpass.setError("Your password is to weak. Kindly use a mix of Alphabet, number and special Character");
                        regpass.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e){
                        regpass.setError("Yor email is invalid or already in use. Kindly re-enter");
                        regpass.requestFocus();
                    }catch (FirebaseAuthUserCollisionException e){
                        regemail.setError("User is already register with this email. use another email. ");
                        regemail.requestFocus();
                    } catch (Exception e){
                        Log.e(TAG,e.getMessage());
                        Toast.makeText(Registeractivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                    progressbar.setVisibility(View.GONE);
                }
            }
        });
    }
}
