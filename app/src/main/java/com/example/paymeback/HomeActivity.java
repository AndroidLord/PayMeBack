package com.example.paymeback;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.paymeback.models.UserModel;
import com.example.paymeback.utils.UserPersistence;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        UserPersistence userPersistence = UserPersistence.getInstance();
        try {
            UserModel userModel = userPersistence.getUserData();
            Log.d("home", "User Data is " + userModel.getName());

        } catch (Exception e) {
            Log.d("home", "exception is " + e.getLocalizedMessage());
        }


    }
}