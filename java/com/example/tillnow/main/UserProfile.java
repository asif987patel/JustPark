package com.example.tillnow.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.example.tillnow.Login;
import com.example.tillnow.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserProfile extends AppCompatActivity {
    //TextView
    TextView TfullName;
    TextView TuserName;

    //EditText
    EditText FullName;
    EditText UserName;
    EditText Email;
    EditText PhoneNum;

    //context
    Context context=this;

    //classes
    FirebaseDatabase database;
    DatabaseReference reference;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        //setting up bottom menu
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListner);
        Menu menu =bottomNav.getMenu();
        MenuItem menuItem = menu.getItem(4);
        menuItem.setChecked(true);

        //hooks
        //TextView
        TfullName = findViewById(R.id.Tfullname);
        TuserName = findViewById(R.id.Tusername);

        //Edittext
        FullName = findViewById(R.id.profile_fullname);
        UserName = findViewById(R.id.profile_username);
        Email = findViewById(R.id.profile_email);
        PhoneNum = findViewById(R.id.profile_phonenum);

        sharedPreferences = getSharedPreferences("UserData",MODE_PRIVATE);
        boolean isLogedin = sharedPreferences.getBoolean("isLogedin",false);
        String uid = sharedPreferences.getString("uid",null);
        String fullName = sharedPreferences.getString("fullName",null);
        String userName = sharedPreferences.getString("userName",null);
        String email = sharedPreferences.getString("email",null);
        String phoneNum = sharedPreferences.getString("phoneNum",null);

        if(isLogedin){
            TfullName.setText(fullName);
            TuserName.setText(userName);
            FullName.setText(fullName);
            UserName.setText(userName);
            Email.setText(email);
            PhoneNum.setText(phoneNum);
        }else{
            Intent intent = new Intent(context, Login.class);
            startActivity(intent);
            finish();
        }
    }

    //setting up the bottom nav
    private BottomNavigationView.OnNavigationItemSelectedListener navListner =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    Intent intent;
                    switch (item.getItemId()){
                        case R.id.nav_home:
                            intent = new Intent(context,Home.class);
                            startActivity(intent);
                            overridePendingTransition(0,0);
                            finish();
                            break;
                        case R.id.nav_search:
                            intent = new Intent(context,Search.class);
                            startActivity(intent);
                            overridePendingTransition(0,0);
                            finish();
                            break;
                        case R.id.nav_scane:
                            intent = new Intent(context,Scan.class);
                            startActivity(intent);
                            overridePendingTransition(0,0);
                            finish();
                            break;
                        case R.id.nav_booked:
                            intent = new Intent(context,Booked.class);
                            startActivity(intent);
                            overridePendingTransition(0,0);
                            finish();
                            break;
                        case R.id.nav_profile:
                            intent = new Intent(context,UserProfile.class);
                            startActivity(intent);
                            overridePendingTransition(0,0);
                            finish();
                            break;
                    }
                    return true;
                }
            };
}
