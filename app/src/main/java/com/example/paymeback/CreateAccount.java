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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.paymeback.models.UserModel;
import com.example.paymeback.utils.Constants;
import com.example.paymeback.utils.PhoneAuthHelper;
import com.example.paymeback.utils.UserPersistence;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
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

public class CreateAccount extends AppCompatActivity {


    private TextView loginAccTxt;
    private TextInputEditText nameEdt,phoneNoEdt,mailEdt;
    private String name,phoneNo,mail,verificationId;
    String inputOTP1;
    String inputOTP2;
    String inputOTP3;
    String inputOTP4;
    String inputOTP5;
    String inputOTP6;


    AppCompatEditText otp1,otp2,otp3,otp4,otp5,otp6;

    private MaterialButton createAccountBtn;
    private LinearLayout llOTP;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    public static final String TAG = "createAcc";

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    PhoneAuthHelper phoneAuthHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        loginAccTxt = findViewById(R.id.loginAccTxt);
        nameEdt = findViewById(R.id.nameEdt);
        phoneNoEdt = findViewById(R.id.phoneNoEdtCtn);
        mailEdt = findViewById(R.id.mailEdt);
        createAccountBtn = findViewById(R.id.createAccBtn);
        llOTP = findViewById(R.id.llOtp_CreateAcc);

        otp1 = findViewById(R.id.inputOTP1);
        otp2 = findViewById(R.id.inputOTP2);
        otp3 = findViewById(R.id.inputOTP3);
        otp4 = findViewById(R.id.inputOTP4);
        otp5 = findViewById(R.id.inputOTP5);
        otp6 = findViewById(R.id.inputOTP6);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        databaseReference = firebaseDatabase.getReference().child(Constants.USER_ACCOUNT);
        phoneAuthHelper = new PhoneAuthHelper(this);

        moveOtpNumber();

        loginAccTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CreateAccount.this,UserAccount.class));
                finish();
            }
        });


        createAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name = nameEdt.getText().toString().trim();
                phoneNo = phoneNoEdt.getText().toString().trim();
                mail = mailEdt.getText().toString().trim();

                if(phoneNo.isEmpty()){
                    Toast.makeText(CreateAccount.this, "Please Enter Your Phone No.", Toast.LENGTH_SHORT).show();
                }
                else if(!phoneNo.isEmpty() && llOTP.getVisibility()==View.GONE){

                    registeringPhoneNumber();


                }
                else if(llOTP.getVisibility()==View.VISIBLE){

                    verifyingOTP();


                }


            }


        });


    }


    private void verifyingOTP() {

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
                !inputOTP6.isEmpty()){


            String smsCode = inputOTP1.trim() +
                    inputOTP2.trim() +
                    inputOTP3.trim() +
                    inputOTP4.trim() +
                    inputOTP5.trim() +
                    inputOTP6.trim();

            Log.d(TAG, "The Sms is " + smsCode);

            // Creating Account
            databaseReference.child(phoneNo).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if(snapshot.exists()){
                        // Account already exists
                        Snackbar.make(createAccountBtn,"Account Already Exits",Snackbar.LENGTH_SHORT).show();
                    }
                    else{
                        // Create Account as it doesn't exits

                        Map<String, String> account = new HashMap<>();

                        if(!name.isEmpty())
                            account.put("Name",name);

                        if(!mail.isEmpty())
                            account.put("Mail",mail);

                        if(!phoneNo.isEmpty())
                            account.put("PhoneNo",phoneNo);

                        databaseReference.child(phoneNo).setValue(account).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {


                                UserModel user = new UserModel(name,phoneNo,mail);
                                int result = phoneAuthHelper.signingInWithCredential(smsCode,verificationId,user);

                                if(result==1){

                                    Toast.makeText(CreateAccount.this, "Logged In.", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(CreateAccount.this, AddFriendListActivity.class));
                                    finishAffinity();

                                }
                                else{
                                    Toast.makeText(CreateAccount.this, "Login Failed", Toast.LENGTH_SHORT).show();

                                }

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Snackbar.make(createAccountBtn,"Please Try Again",Snackbar.LENGTH_SHORT).show();
                            }
                        });

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                    Snackbar.make(createAccountBtn,"Try Again Later",Snackbar.LENGTH_SHORT).show();

                }
            });





        }
        else{

            Toast.makeText(CreateAccount.this, "OTP not filled", Toast.LENGTH_SHORT).show();

        }
    }

    private void registeringPhoneNumber() {

        progressDialog = new ProgressDialog(CreateAccount.this);
        progressDialog.setMessage("Sending Message...");
        progressDialog.setCancelable(false);
        progressDialog.show();


        phoneNo = "+91" + phoneNo;

        Log.d(TAG, "User Phone No. " + phoneNo);


        PhoneAuthOptions phoneAuthOptions = phoneAuthHelper.sendVerificationCode(phoneNo)
                .setActivity(CreateAccount.this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                        progressDialog.dismiss();

                        Log.d(TAG, "onVerificationCompleted: ");

                        int result = phoneAuthHelper.signingInWithCredential(phoneAuthCredential);

                        if(result==1){
                            Toast.makeText(CreateAccount.this, "Logged In.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(CreateAccount.this,AddFriendListActivity.class));
                            finishAffinity();
                        }
                        else if(result==0){
                            Toast.makeText(CreateAccount.this, "Login Failed", Toast.LENGTH_SHORT).show();
                        }



                    }
                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {

                        progressDialog.dismiss();
                        Toast.makeText(CreateAccount.this, "Verification Failed", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onVerificationFailed: " + e.getLocalizedMessage());


                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);

                        progressDialog.dismiss();
                        createAccountBtn.setText("Verify OTP");
                        verificationId = s;
                        llOTP.setVisibility(View.VISIBLE);

                        Log.d(TAG, "Verification Id: " + verificationId);
                    }
                })
                .build();

        PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions);
    }


    private void moveOtpNumber() {

        otp1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(!s.toString().isEmpty()){
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

                if(!s.toString().isEmpty()){
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

                if(!s.toString().isEmpty()){
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

                if(!s.toString().isEmpty()){
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

                if(!s.toString().isEmpty()){
                    otp6.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

}