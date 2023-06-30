package com.example.paymeback.utils;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.paymeback.models.UserModel;

public class UserPersistence {

    private static UserPersistence instance;
    private Context context;
    private String name,phoneNo,mail;

    SharedPreferences sharedPreferences;

    public void saveUserData(UserModel user, Context context){

    name = user.getName();
    phoneNo = user.getPhoneNo();
    mail = user.getPhoneNo();

    this.context=context;

    storingUserData(user, context);
    }

    private void storingUserData(UserModel user, Context context) {

        sharedPreferences = context.getSharedPreferences(Constants.USER_PREF_ID,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("Name",name);
        editor.putString("PhoneNo",phoneNo);
        if(!mail.isEmpty())
            editor.putString("Mail",mail);

        editor.apply();

    }

    public UserModel getUserData() throws Exception {

        sharedPreferences = context.getSharedPreferences(Constants.USER_PREF_ID,MODE_PRIVATE);

        name = sharedPreferences.getString("Name","No Name");
        phoneNo = sharedPreferences.getString("PhoneNo","No Number");
        mail = sharedPreferences.getString("Mail","No mail");

        UserModel user = new UserModel();

        if(phoneNo.isEmpty()){
            Log.d("persistence", "Shared Pref. Phone No. is not accessible or There is no Phone No Stored To Display");
            throw new Exception("No Data Found");
        }
        else{
            if (!name.isEmpty())
            user.setName(name);
            else Log.d("persistence", "Shared Pref. Name is not accessible or There is no Name Stored To Display");

            user.setPhoneNo(phoneNo);

            if (!mail.isEmpty())
                user.setEmail(mail);
            else Log.d("persistence", "Shared Pref. Mail is not accessible or There is no Mail Stored To Display");
        }

        return user;
    }



    public static synchronized UserPersistence getInstance(){

        if(instance==null)
        {
            instance = new UserPersistence();
        }
        return instance;
    }


}
