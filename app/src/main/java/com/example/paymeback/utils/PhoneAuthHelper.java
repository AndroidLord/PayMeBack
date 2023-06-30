package com.example.paymeback.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.paymeback.AddFriendListActivity;
import com.example.paymeback.CreateAccount;
import com.example.paymeback.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneAuthHelper {

    public static final String TAG = "loginAcc";
   // public static String verificationId;
    // Initialize a FirebaseAuth instance
    private FirebaseAuth firebaseAuth;
    private Context context;


    // Constructor
    public PhoneAuthHelper(Context context) {
        firebaseAuth = FirebaseAuth.getInstance();
        this.context = context;
    }

    // Function to send a verification code to a phone number
    public PhoneAuthOptions.Builder sendVerificationCode(String phoneNumber) {

        PhoneAuthOptions.Builder builder = PhoneAuthOptions.newBuilder()
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS);

        return builder;
    }

    public int signingInWithCredential(PhoneAuthCredential phoneAuthCredential) {

        final int[] resultAuth = new int[1];

        firebaseAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    resultAuth[0] = 1;
                } else {
                    resultAuth[0] = 0;
                }

            }
        });

        return resultAuth[0];
    }

    public int signingInWithCredential(String smsCode,String verificationId, UserModel user) {

        int[] resultSms = {1};
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, smsCode);

        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    Log.d(TAG, "Result1 val== " + resultSms[0]);
                    resultSms[0] = 1;
                    Log.d(TAG, "Result2 val== " + resultSms[0]);
                    UserPersistence userPersistence = UserPersistence.getInstance();
                    userPersistence.saveUserData(user, context);

                    Log.d(TAG, "(from PhoneAuthHelper)Login is success" + task.isSuccessful());


                } else if (task.isCanceled()){
                    Log.d(TAG, "Result3 val== " + resultSms[0]);
                    resultSms[0] = 0;
                    Log.d(TAG, "Result4 val== " + resultSms[0]);
                    Log.d(TAG, "(from PhoneAuthHelper)Login Exception: " + task.getException());
                    Log.d(TAG, "(from PhoneAuthHelper)Login is: " + task.isCanceled());
                }
                else{
                    Log.d(TAG, "Result5 val== " + resultSms[0]);
                    Log.d(TAG, "(from PhoneAuthHelper) Some Error Occurred and task is success" +task.isSuccessful() + "Cancell " + task.isCanceled() +"Complete "+task.isComplete() );
                }

            }
        });

        Log.d(TAG, "Result6 val== " + resultSms[0]);

        return resultSms[0];
    }


    // Function to verify the verification code
    public void verifyVerificationCode(String verificationCode, String smsCode) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, smsCode);

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // User is signed in
                    } else {
                        // Sign in failed
                    }
                });
    }


}
