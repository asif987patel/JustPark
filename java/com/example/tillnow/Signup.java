package com.example.tillnow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;

public class Signup extends AppCompatActivity {

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    //"(?=.*[0-9])" +         //at least 1 digit
                    //"(?=.*[a-z])" +         //at least 1 lower case letter
                    //"(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    "(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{4,}" +               //at least 4 characters
                    "$");
    private final int REQUEST_CODE = 1;
    private boolean isAuthenticated;
    private String uid;

    //Context
    private Context context=this;

    //classes
    private FirebaseDatabase database;
    private DatabaseReference refrence;
    private User user;
    private Helper helper =new Helper();

    //Edit text
    private TextInputEditText fullName;
    private TextInputEditText userName;
    private TextInputEditText email;
    private TextInputEditText phoneNum;
    private TextInputEditText password;
    private TextInputEditText confPassword;

    //Button
    private Button phoneOtp;
    private Button submit;

    //ProgressBars
    private ProgressBar progressBar;
    private ProgressBar phoneCheckBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //hooks
        //Edit text
        fullName = findViewById(R.id.fullname);
        userName = findViewById(R.id.username);
        email = findViewById(R.id.emial);
        phoneNum = findViewById(R.id.phoneNum);
        password = findViewById(R.id.password);
        confPassword = findViewById(R.id.confPassword);

        //Buttons
        phoneOtp =findViewById(R.id.phone_otp);
        submit = findViewById(R.id.submit);
        progressBar = findViewById(R.id.progress_bar);
        phoneCheckBar = findViewById(R.id.number_check_bar);
        phoneCheckBar.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        database = FirebaseDatabase.getInstance();
        Log.d("myTag", "database "+database.toString());
        refrence = database.getReference("users");
        Log.d("myTag", "refrence "+refrence.toString());
        phoneOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneCheckBar.setVisibility(View.VISIBLE);
                if(checkSignupForm()){
                    Query checkPhoneExits= refrence.orderByChild("phoneNo").equalTo(phoneNum.getText().toString());
                    checkPhoneExits.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                phoneCheckBar.setVisibility(View.GONE);
                                phoneNum.setError("PhoneNumber already exist");
                            }else{
                                String num = "+91"+phoneNum.getText().toString();
                                if(num.length()==13 && num.startsWith("+91")){
                                    Log.d("myTag", "sendVerificationCode: " + "code sending TO" + num);
                                    phoneCheckBar.setVisibility(View.GONE);
                                    Intent intent = new Intent(context, PhoneOtp.class);
                                    intent.putExtra("phoneNumber",num);
                                    startActivityForResult(intent,REQUEST_CODE);
                                }else{
                                    phoneNum.setError("wrong number");
                                    phoneCheckBar.setVisibility(View.GONE);
                                    Log.d("myTag", "onClick: wrong number: "+num);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            phoneNum.setError("PhoneNumber already exist");
                        }
                    });
                }else{
                    phoneCheckBar.setVisibility(View.GONE);
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if(checkSignupForm()){
                    if(isAuthenticated){
                        Query checkUserExits= refrence.orderByChild("username").equalTo(userName.getText().toString());
                        checkUserExits.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    progressBar.setVisibility(View.GONE);
                                    userName.setError("UserName already exist");
                                }else{
                                    user = new User(
                                            fullName.getText().toString(),
                                            userName.getText().toString(),
                                            email.getText().toString(),
                                            phoneNum.getText().toString(),
                                            password.getText().toString()
                                    );
                                    refrence.child(uid).setValue(user);
                                    Log.d("myTag", "onClick: user signin");

                                    Intent intent = new Intent(context,Login.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                userName.setError("user name exist");
                            }
                        });
                    }else{
                        progressBar.setVisibility(View.GONE);
                        phoneOtp.setText("You Have to Verify first");
                        phoneOtp.setBackgroundColor(Color.rgb(205, 50, 50));
                    }
                }else{
                    progressBar.setVisibility(View.GONE);
                }
            }
        });


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE){
            if(data!=null){
                isAuthenticated = data.getBooleanExtra("isAuthenticate",false);
                uid = data.getStringExtra("uid");
                if(isAuthenticated) {
                    phoneOtp.setText("Phone Number Verified");
                    phoneOtp.setBackgroundColor(Color.rgb(50, 205, 50));
                }
            }
        }
    }

    public void backSignup(View view){
        finish();
    }

    public boolean checkSignupForm(){
        boolean isOk=true;
        String FullName = fullName.getText().toString().trim();
        String UserName = userName.getText().toString().trim();
        String Email = email.getText().toString().trim();
        String PhoneNum = phoneNum.getText().toString().trim();
        String Password = password.getText().toString().trim();
        String ConfPassword = confPassword.getText().toString().trim();

        //checking firstname
        if(FullName.length()>=4 && FullName.length()<=20){
            //add more checks if you want
        }else{
            fullName.setError("FullName has to be between 4-20 characters.");
            isOk=false;
        }

        //checking username
        if(UserName.length()>=4 && UserName.length()<=15){
            //add more checks if you want
        }else{
            userName.setError("UserName has to be between 4-15 characters.");
            isOk=false;
        }

        //checking E-mail
        if(!Email.isEmpty()){
            if(Patterns.EMAIL_ADDRESS.matcher(Email).matches()){
                //add more checks if you want
            }else{
                email.setError("invalid mail");
                isOk=false;
            }
        }else{
            email.setError("Email can't be empty");
            isOk=false;
        }

        //checking phone number
        String phonePattern ="^(*[0-9])$";
        if(PhoneNum.length()==10){
            //add more checks if you want
        }else{
            phoneNum.setError("Phone Number is invalid");
            isOk=false;
        }

        //checking password
        if(!Password.isEmpty()){
            if(PASSWORD_PATTERN.matcher(Password).matches()){
                //add more checks if you want
            }else{
                password.setError("password must have speacial characterno with no wide space");
                isOk=false;
            }
        }else{
            password.setError("password can't be empty");
            isOk=false;
        }

        //checking confirm password
        if(ConfPassword.equals(Password)){
            //add more checks if you want
        }else{
            confPassword.setError("Password must be same");
            isOk=false;
        }

        return isOk;
    }
}
