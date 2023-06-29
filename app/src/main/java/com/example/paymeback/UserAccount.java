package com.example.paymeback;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.paymeback.utils.Constants;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserAccount extends AppCompatActivity {


    private TextInputEditText phoneNoEdt;
    private TextView createAccTxt;

    private Button loginBtb;


    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    public static final String TAG = "loginAcc";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account);

        phoneNoEdt = findViewById(R.id.phoneNoEdtLgn);
        createAccTxt = findViewById(R.id.createAccTxt);
        loginBtb = findViewById(R.id.loginBtn);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

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

                if(s.length() == 10){

                    loginBtb.setBackgroundTintList(getResources().getColorStateList(R.color.blue));

                }
                else{
                    loginBtb.setBackgroundTintList(getResources().getColorStateList(R.color.offblue));
                }

            }
        });


        loginBtb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(phoneNoEdt.length()==10){

                    String phoneNo = phoneNoEdt.getText().toString().trim();

                    Log.d(TAG, "User Phone No is: " + phoneNo);

                    databaseReference.child(Constants.USER_ACCOUNT).child(phoneNo).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if(snapshot.exists()){
                                // login
                               startActivity(new Intent(UserAccount.this,AddFriendListActivity.class));

                            }
                            else{
                                // Account Doesn't exists
                                Snackbar.make(loginBtb,"Account Doesn't Exits",Snackbar.LENGTH_SHORT).show();

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Snackbar.make(loginBtb,"Please Try Again",Snackbar.LENGTH_SHORT).show();
                        }
                    });

                }

            }
        });


        createAccTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserAccount.this,CreateAccount.class));
                finish();
            }
        });

    }
}