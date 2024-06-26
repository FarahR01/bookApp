package com.example.booknest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //init firebase auth
        firebaseAuth=FirebaseAuth.getInstance();
        //start main screen after 2secs
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkUser();
            }
        },2000);
    }
    private void checkUser(){
        //get current user if logged in
        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
        if(firebaseUser==null){
            //user not logged in
            //start main screen
            startActivity(new Intent(SplashActivity.this,MainActivity.class));
            finish();
        }else {
            //user logged in check user type
            //check in db
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseUser.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            //get user Type
                            String userType =""+snapshot.child("userType").getValue();
                            //check user Type
                            if (userType.equals("user")){
                                //userdashboard
                                startActivity(new Intent(SplashActivity.this,DashboardUserActivity.class));
                                finish();
                            } else if (userType.equals("admin")) {

                                //admindashboard
                                startActivity(new Intent(SplashActivity.this, DashboardAdminActivity.class));
                                finish();

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
    }
}