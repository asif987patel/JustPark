package com.example.tillnow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.tillnow.main.Home;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {
    private String UserName;
    private String Password;

    //context
    Context context=this;
    //EditText
    private TextInputEditText userName;
    private TextInputEditText password;
    //Buttons
    private Button forgotPass;
    private Button login;

    //other Views
    private ProgressBar progressBar;

    //classes
    FirebaseDatabase database;
    DatabaseReference reference;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //hooks
        userName = findViewById(R.id.lusername);
        password = findViewById(R.id.lpassword);
        forgotPass = findViewById(R.id.forgot_pass);
        login = findViewById(R.id.login);
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        forgotPass.setVisibility(View.GONE);

    }

    public void loginUser(View view){
        if(checkLoginForm()){
            progressBar.setVisibility(View.VISIBLE);

            UserName = userName.getText().toString();
            Password = password.getText().toString();

            database = FirebaseDatabase.getInstance();
            Log.d("myTag", "database "+database.toString());
            reference = database.getReference("users");
            Log.d("myTag", "refrence "+reference.toString());

            Query checkUserExits= reference.orderByChild("username").equalTo(UserName);
            Log.d("myTag", "cheacking for user "+UserName+" with password "+Password);
            checkUserExits.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        Log.d("myTag", "inside snapshot"+snapshot.getChildrenCount());
                        if(snapshot.getChildrenCount()==1){
                            for (DataSnapshot user : snapshot.getChildren()) {
                                Log.d("myTag", "inside for loop");
                                String uidFromDB = user.getKey();
                                String fullnameFromDB = user.child("name").getValue(String.class);
                                String usernameFromDB = user.child("username").getValue(String.class);
                                String emailFromDB = user.child("email").getValue(String.class);
                                String phoneFromDB =user.child("phoneNo").getValue(String.class);
                                String passwordFromDB = user.child("password").getValue(String.class);
                                if(passwordFromDB.equals(Password)){
                                    sharedPreferences = getSharedPreferences("UserData",MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putBoolean("isLogedin",true);
                                    editor.putString("uid",uidFromDB);
                                    editor.putString("fullName",fullnameFromDB);
                                    editor.putString("userName",usernameFromDB);
                                    editor.putString("email",emailFromDB);
                                    editor.putString("phoneNum",phoneFromDB);
                                    editor.commit();

                                    Intent intent = new Intent(context, Home.class);
                                    startActivity(intent);
                                    finish();
                                }else{
                                    progressBar.setVisibility(View.GONE);
                                    password.setError("Wrong password, please try again");
                                }
                            }
                        }else{
                            Log.d("myTag", "more then 1 user with same username, please check");
                        }
                    }else{
                        progressBar.setVisibility(View.GONE);
                        userName.setError("User Not Exist,please cheack the username.");
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressBar.setVisibility(View.GONE);
                    userName.setError("User Not Exist,please cheack the username and try again");
                }
            });
            Log.d("myTag", "finished()");
        }
    }

    public void backLogin(View view){
        finish();
    }

    public boolean checkLoginForm(){
        boolean isOk=true;
        UserName = userName.getText().toString();
        Password = password.getText().toString();

        //checking username
        if(UserName.length()>=4 && UserName.length()<=15){
            //add more checks if you want
        }else{
            userName.setError("Username to be between 4-15 characters.");
            isOk=false;
        }

        //checking password
        String passwordPattern = "^"+"(?=.*[a-zA-z])"+"(?=.*[@#$%^&+=])"+"(?=\\s+$)"+".(4,)"+"$";
        if(Password.length()>=5){
            //add more checks if you want
        }else{
            password.setError("must have atleast 5 characters");
            isOk=false;
        }

        return isOk;
    }
}
