package com.example.tillnow;

import android.util.Log;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.CountDownLatch;

import androidx.annotation.NonNull;

public class Helper {
    //classes
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private boolean[] isExists = {false};

    public boolean isUserExist(String userName){
        Log.d("myTag", "helper class");
        CountDownLatch done = new CountDownLatch(1);
        Runnable runnable = new DatabaseCheck(done,userName,isExists);
        new Thread(runnable).start();
        try{
            Log.d("myTag", "thread called");
            done.await();
        }catch (InterruptedException ex){}

        return isExists[0];
    }

    class DatabaseCheck implements Runnable{
        //classes
        private CountDownLatch done;
        private FirebaseDatabase database;
        private DatabaseReference reference;
        private String userName;
        private boolean[] isExists = {false};

        DatabaseCheck(CountDownLatch done,String userName,boolean[] isExists){
            this.done=done;
            this.userName=userName;
            this.isExists = isExists;
        }
        @Override
        public void run() {
            database = FirebaseDatabase.getInstance();
            reference = database.getReference("users");

            Log.d("myTag", "inside run");
            Query checkUserExits= reference.orderByChild("username").equalTo(userName);
            Log.d("myTag", "after query build");
            checkUserExits.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        Log.d("myTag", "user exits");
                        isExists[0]=true;
                        done.countDown();
                    }else{
                        Log.d("myTag", "user not exits");
                        isExists[0]=false;
                        done.countDown();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("myTag", "on cancled");
                    isExists[0]=false;
                    done.countDown();
                }
            });

        }
    }

}
