package com.example.tillnow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tillnow.main.Home;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 2000;

    TextView tagline;
    ImageView logo,left,right;

    Animation bottomAnimation, middleAnimation,middleAnimationLeft,middleAnimationRight;
    //classes
    FirebaseDatabase database;
    DatabaseReference reference;
    SharedPreferences firstTime;
    SharedPreferences userData;

    boolean isNew=true;
    boolean isLogedin=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomAnimation = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);
        middleAnimation = AnimationUtils.loadAnimation(this,R.anim.middle_animation);
        middleAnimationLeft = AnimationUtils.loadAnimation(this,R.anim.middle_animation_left);
        middleAnimationRight = AnimationUtils.loadAnimation(this,R.anim.middle_animation_right);

        logo = findViewById(R.id.sign);
        left = findViewById(R.id.left);
        right = findViewById(R.id.right);
        tagline = findViewById(R.id.tagLine);

        left.setAnimation(middleAnimationLeft);
        logo.setAnimation(middleAnimation);
        right.setAnimation(middleAnimationRight);

        tagline.setAnimation(bottomAnimation);

        firstTime = getSharedPreferences("onBoardingScreen",MODE_PRIVATE);
        isNew = firstTime.getBoolean("new",true);

        if(isNew){

        }else{
            userData = getSharedPreferences("UserData",MODE_PRIVATE);
            isLogedin = userData.getBoolean("isLogedin",false);

            if(isLogedin){
                String userName = userData.getString("userName",null);
                database = FirebaseDatabase.getInstance();
                Log.d("myTag", "database "+database.toString());
                reference = database.getReference("users");
                Log.d("myTag", "refrence "+reference.toString());
                Query checkUserExits= reference.orderByChild("username").equalTo(userName);
                Log.d("myTag", "cheacking for user "+userName);
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
                                    SharedPreferences.Editor editor = userData.edit();
                                    editor.putString("uid",uidFromDB);
                                    editor.putString("fullName",fullnameFromDB);
                                    editor.putString("userName",usernameFromDB);
                                    editor.putString("email",emailFromDB);
                                    editor.putString("phoneNum",phoneFromDB);
                                    editor.commit();
                                }
                                Intent intent = new Intent(MainActivity.this,Home.class);
                                startActivity(intent);
                                finish();
                            }else{
                                Log.d("myTag", "more then 1 user with same username, please check");
                            }
                        }else{
                            Log.d("myTag", "User Not Exist,please cheack the username.");
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("myTag", "onCancelled: ");
                    }
                });
                Log.d("myTag", "finished()");
            }else{
                Intent intent = new Intent(MainActivity.this,Welcome.class);
                startActivity(intent);
                finish();
            }
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(isNew){
                    Intent intent = new Intent(MainActivity.this,OnBoarding.class);
                    startActivity(intent);
                    finish();
                }else{
                }
            }
        },SPLASH_TIME_OUT);
    }
}
