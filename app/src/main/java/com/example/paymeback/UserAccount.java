package com.example.paymeback;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.paymeback.models.FriendModel;
import com.example.paymeback.models.UserModel;
import com.example.paymeback.utils.Constants;
import com.example.paymeback.utils.PhoneAuthHelper;
import com.example.paymeback.utils.UserPersistence;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class UserAccount extends AppCompatActivity {


    private TextInputEditText phoneNoEdt;
    private TextView createAccTxt;
    private Button loginBtb;

    private String inputOTP1,
            inputOTP2,
            inputOTP3,
            inputOTP4,
            inputOTP5,
            inputOTP6;

    private AppCompatEditText otp1,
            otp2,
            otp3,
            otp4,
            otp5,
            otp6;
    private LinearLayout llOtp;

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    public static final String TAG = "loginAcc";
    private String verificationId;
    private PhoneAuthHelper phoneAuthHelper;
    private UserModel user;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account);

        phoneNoEdt = findViewById(R.id.phoneNoEdtLgn);
        createAccTxt = findViewById(R.id.createAccTxt);
        loginBtb = findViewById(R.id.loginBtn);
        otp1 = findViewById(R.id.inputOTP1_lgn);
        otp2 = findViewById(R.id.inputOTP2_lgn);
        otp3 = findViewById(R.id.inputOTP3_lgn);
        otp4 = findViewById(R.id.inputOTP4_lgn);
        otp5 = findViewById(R.id.inputOTP5_lgn);
        otp6 = findViewById(R.id.inputOTP6_lgn);
        llOtp = findViewById(R.id.llOtp_loginAcc);


        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        phoneAuthHelper = new PhoneAuthHelper(this);

        moveOtpNumber();
        loginBtb.setBackgroundTintList(getResources().getColorStateList(R.color.offblue));
        phoneNoEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() == 10) {

                    loginBtb.setBackgroundTintList(getResources().getColorStateList(R.color.blue));

                } else {
                    loginBtb.setBackgroundTintList(getResources().getColorStateList(R.color.offblue));
                }

            }
        });

        loginBtb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (phoneNoEdt.length() == 10) {

                    String phoneNo = phoneNoEdt.getText().toString().trim();

                    Log.d(TAG, "User Phone No is: " + phoneNo);

                    if (llOtp.getVisibility() == View.VISIBLE) {
                        // Verify OTP

                        verifyOTP();

                    }

                    else {
                        // Verify Phone Number

                        progressDialog = new ProgressDialog(UserAccount.this);
                        progressDialog.setMessage("Verifying Phone No...");
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                        databaseReference.child(Constants.USER_ACCOUNT).child(phoneNo).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if (snapshot.exists()) {
                                    // login

                                    user = snapshot.getValue(UserModel.class);

                                    assert user != null;
                                    if (user.getPhoneNo().contains(phoneNo)) {

                                        Log.d(TAG, "Before Saving Checking all the content of Data... ");
                                        Log.d(TAG, "Before Saving User detail are ");
                                        Log.d(TAG, "Name: " + user.getName());
                                        Log.d(TAG, "Phone No.: " + user.getPhoneNo());
                                        Log.d(TAG, "Mail: " + user.getEmail());
                                        Log.d(TAG, "User Data: " + user.toString());
                                        saveDataLocally(user);
                                        VeryfyPhoneNumber(phoneNo);


                                    } else {
                                        Log.d(TAG, "Data Saved Failed in Shared Preference");
                                        progressDialog.dismiss();
                                        Snackbar.make(phoneNoEdt, "Please Retry", Snackbar.LENGTH_SHORT).show();

                                    }


                                } else {
                                    // Account Doesn't exists
                                    progressDialog.dismiss();
                                    Snackbar.make(loginBtb, "Account Doesn't Exits", Snackbar.LENGTH_SHORT).show();

                                }

                            }


                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                progressDialog.dismiss();
                                Snackbar.make(loginBtb, "Please Try Again", Snackbar.LENGTH_SHORT).show();
                            }
                        });

                    }


                }

            }
        });


        createAccTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserAccount.this, CreateAccount.class));
                finish();
            }
        });

    }

    private void verifyOTP() {

        inputOTP1 = otp1.getText().toString();
        inputOTP2 = otp2.getText().toString();
        inputOTP3 = otp3.getText().toString();
        inputOTP4 = otp4.getText().toString();
        inputOTP5 = otp5.getText().toString();
        inputOTP6 = otp6.getText().toString();

        Log.d(TAG, "The 1 OTP is: " + inputOTP1);
        Log.d(TAG, "The 2 OTP is: " + inputOTP2);
        Log.d(TAG, "The 3 OTP is: " + inputOTP3);
        Log.d(TAG, "The 4 OTP is: " + inputOTP4);
        Log.d(TAG, "The 5 OTP is: " + inputOTP5);
        Log.d(TAG, "The 6 OTP is: " + inputOTP6);

        if (!inputOTP1.isEmpty() &&
                !inputOTP2.isEmpty() &&
                !inputOTP3.isEmpty() &&
                !inputOTP4.isEmpty() &&
                !inputOTP5.isEmpty() &&
                !inputOTP6.isEmpty()) {

            progressDialog = new ProgressDialog(UserAccount.this);
            progressDialog.setMessage("Verifying OTP...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            String smsCode = inputOTP1.trim() +
                    inputOTP2.trim() +
                    inputOTP3.trim() +
                    inputOTP4.trim() +
                    inputOTP5.trim() +
                    inputOTP6.trim();

            Log.d(TAG, "The Sms is " + smsCode);

            int result = phoneAuthHelper.signingInWithCredential(smsCode,verificationId,user);

            if(result==1){
                // Success

                Log.d(TAG, "Login Success");
                Toast.makeText(UserAccount.this, "Logged In.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(UserAccount.this, AddFriendListActivity.class));
                finishAffinity();

            }
            else if(result==0){
                // Failure
                Log.d(TAG, "Login Failed");
                Toast.makeText(UserAccount.this, "Login Failed", Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();

        } else {

            // OTP not filled
            Log.d(TAG, "OTP Not Filled " );
            Toast.makeText(UserAccount.this, "OTP not filled", Toast.LENGTH_SHORT).show();

        }

    }

    private void VeryfyPhoneNumber(String phone) {


        phone = "+91" + phone;

        Log.d(TAG, "User Phone No. " + phone);

        PhoneAuthOptions phoneAuthOptions = phoneAuthHelper.sendVerificationCode(phone)
                .setActivity(UserAccount.this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {


                        Log.d(TAG, "onVerificationCompleted: ");
                        progressDialog.setMessage("Verifying OTP...");
                        int result = phoneAuthHelper.signingInWithCredential(phoneAuthCredential);

                        if(result==1){
                            progressDialog.dismiss();
                            Toast.makeText(UserAccount.this, "Logged In.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(UserAccount.this,AddFriendListActivity.class));
                            finishAffinity();
                        }
                        else if(result==0){
                            progressDialog.dismiss();
                            Toast.makeText(UserAccount.this, "Login Failed", Toast.LENGTH_SHORT).show();
                        }



                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {

                        progressDialog.dismiss();
                        Toast.makeText(UserAccount.this, "Verification Failed", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onVerificationFailed: " + e.getLocalizedMessage());

                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);

                        progressDialog.dismiss();
                        loginBtb.setText("Verify OTP");
                        verificationId = s;
                        llOtp.setVisibility(View.VISIBLE);

                        Log.d(TAG, "Verification Id: " + verificationId);

                    }
                }).build();

        PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions);
    }

    private void saveDataLocally(UserModel user) {

        // Saving Data in Local Storage
        UserPersistence userPersistence = UserPersistence.getInstance();
        Log.d(TAG, "Data Saved Successfully in Shared Preference");

        try {
            userPersistence.saveUserData(user, getApplicationContext());
        }
        catch (Exception e){

        }



    }

    private void moveOtpNumber() {

        otp1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!s.toString().isEmpty()) {
                    otp2.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        otp2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!s.toString().isEmpty()) {
                    otp3.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        otp3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!s.toString().isEmpty()) {
                    otp4.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        otp4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!s.toString().isEmpty()) {
                    otp5.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        otp5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!s.toString().isEmpty()) {
                    otp6.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

}