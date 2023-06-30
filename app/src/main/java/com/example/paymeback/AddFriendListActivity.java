package com.example.paymeback;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.paymeback.adaptor.FriendRVAdaptor;
import com.example.paymeback.adaptor.OnFriendItemClickListener;
import com.example.paymeback.models.FriendModel;
import com.example.paymeback.utils.Constants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AddFriendListActivity extends AppCompatActivity implements OnFriendItemClickListener {

    private static final int PERMISSION_REQUEST_CONTACTS = 101;
    private static final String TAG = "addFriends";

    private ArrayList<FriendModel> friendModelArrayList;
    private ArrayList<FriendModel> selectedFriendsList;

    private RecyclerView recyclerView;
    private TextView skipTxt,selectedTxt;
    FriendRVAdaptor friendRVAdaptor;
    private SearchView searchView;

    private FloatingActionButton nextFab;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend_list);

        recyclerView = findViewById(R.id.friendRV_addFriend);
        skipTxt = findViewById(R.id.skipTxt_addFriend);
        searchView = findViewById(R.id.seachView_addFriend);
        nextFab = findViewById(R.id.nextFabBtn);
        selectedTxt = findViewById(R.id.selectedTxt);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child(Constants.CHAT_ROOMS);

        friendModelArrayList = new ArrayList<>();
        selectedFriendsList = new ArrayList<>();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                filterList(newText);


                return true;
            }
        });

        skipTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddFriendListActivity.this,HomeActivity.class));
                finishAffinity();
            }
        });

        friendRVAdaptor = new FriendRVAdaptor(this,friendModelArrayList);
        friendRVAdaptor.setOnFriendItemClickListener(this);
        recyclerView.setAdapter(friendRVAdaptor);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS}, PERMISSION_REQUEST_CONTACTS);
        } else {
            // Permission has already been granted, proceed with accessing contacts
            accessContacts();
        }


        nextFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // adding those list in the Firebase

                for(FriendModel friend: selectedFriendsList){


                   // databaseReference.child()



                }


            }
        });

    }

    private void filterList(String newText) {

        ArrayList<FriendModel> filteredList = new ArrayList<>();

        for(FriendModel friends: friendModelArrayList){

            if(friends.getName().toLowerCase().contains(newText.toLowerCase())){
                filteredList.add(friends);
            }
            else if(friends.getPhoneNo().contains(newText)){
                filteredList.add(friends);
            }

        }
        if(newText==""){
            friendRVAdaptor.setFilteredData(friendModelArrayList);
        }
        else
        if(filteredList.isEmpty()){
           Snackbar.make(searchView,"Data Not Found!",Snackbar.LENGTH_SHORT).show();
        }
        else{
            friendRVAdaptor.setFilteredData(filteredList);
        }


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with accessing contacts
                accessContacts();
            } else {
                // Permission denied, handle accordingly
                Toast.makeText(this, "Permission denied. Cannot access contacts.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void accessContacts() {
        // Define the columns you want to retrieve from the contacts database
        String[] projection = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};

        // Perform a query to retrieve the contacts
        Cursor cursor = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                null,
                null,
                null
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                // Retrieve contact name and phone number
                String name = "";
                String phoneNumber = "";

                int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                if (nameIndex != -1) {
                    String displayName = cursor.getString(nameIndex);
                    if (displayName != null) {
                        name = displayName;
                    }
                }

                int phoneNumberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                if (phoneNumberIndex != -1) {
                    String number = cursor.getString(phoneNumberIndex);
                    if (number != null) {
                        phoneNumber = number;
                    }
                }
                friendModelArrayList.add(new FriendModel(name,phoneNumber));
                // Do something with the contact details
                Log.d(TAG, "Name: " + name + ", Phone: " + phoneNumber);
            }
            friendRVAdaptor.notifyDataSetChanged();
            cursor.close();
        }
    }


    @Override
    public void onFriendItemClick(FriendModel friend) {
        //Snackbar.make(searchView,"Selected: " + friend.getName(),Snackbar.LENGTH_SHORT).show();
        selectedFriendsList.add(friend);

        FabVisibilty();

        showSelectedFriends();
    }

    @Override
    public void onRemoveFriendItemClick(FriendModel friendModel) {
        selectedFriendsList.remove(friendModel);
        showSelectedFriends();
        //Snackbar.make(searchView,"Removed: " + friendModel.getName(),Snackbar.LENGTH_SHORT).show();
        FabVisibilty();

    }

    private void FabVisibilty() {
        if(!selectedFriendsList.isEmpty()){
            nextFab.setVisibility(View.VISIBLE);
        }
        else nextFab.setVisibility(View.GONE);
    }

    public void showSelectedFriends(){
        String friends = "";
        for (FriendModel friend: selectedFriendsList){
            friends = friend.getName() +", "+ friends;
        }
        selectedTxt.setText(friends);
        //Snackbar.make(searchView,"Selected: " + friends,Snackbar.LENGTH_SHORT).show();
    }

}