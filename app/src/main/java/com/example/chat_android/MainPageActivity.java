package com.example.chat_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;

import java.util.ArrayList;

import Chat.ChatListAdapter;
import Chat.ChatObject;
import User.UserObject;

public class MainPageActivity extends AppCompatActivity {
    private Button log_out,find_user;

    private RecyclerView mchat_list;
    private RecyclerView.Adapter mchat_listadapter;
    private RecyclerView.LayoutManager mchat_layoutmanager;
    private ArrayList<ChatObject> chatlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);


        OneSignal.startInit(this).init();
        OneSignal.setSubscription(true);
        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                FirebaseDatabase.getInstance().getReference().child("user")
                        .child(FirebaseAuth.getInstance().getUid()).child("notification").setValue(userId);
            }
        });
        OneSignal.setInFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification);

        Fresco.initialize(this);

        log_out=findViewById(R.id.logout);
        find_user=findViewById(R.id.f_user);
        find_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),FindUserActivity.class));
            }
        });
        log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OneSignal.setSubscription(false);
                FirebaseAuth.getInstance().signOut();
                Intent intent =new Intent(getApplicationContext(),MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                return;
            }
        });
        request_permisson();
        initialize_recyclerview();
        getUserChat();
    }

    private void request_permisson() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS}, 1);
        }
    }
    private void getUserChat(){
        DatabaseReference muserchat= FirebaseDatabase.getInstance().getReference().child("user").
                child(FirebaseAuth.getInstance().getUid()).child("chat");

        muserchat.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot childsnapshot : snapshot.getChildren()){
                        ChatObject mchat=new ChatObject(childsnapshot.getKey());
                        boolean exist=false;
                        for( ChatObject mchatIterator : chatlist){
                            if(mchatIterator.getChatID().equals(mchat.getChatID())){
                                exist=true;
                            }
                        }
                        if(exist){continue;}
                        chatlist.add(mchat);
                        getChatData(mchat.getChatID());
                        mchat_listadapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getChatData(String chatID) {
        DatabaseReference mchatDB= FirebaseDatabase.getInstance().getReference().child("chat").
                child(chatID).child("info");
        mchatDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String chatId="";
                    if(snapshot.child("id").getChildren()!=null){
                        chatId=snapshot.child("id").getValue().toString();
                    }

                    for(DataSnapshot userSnapshot : snapshot.child("user").getChildren()){
                        for(ChatObject mChat : chatlist){
                            if(mChat.getChatID().equals(chatID)){
                                UserObject mUser=new UserObject(userSnapshot.getKey());
                                mChat.addtoArrayList(mUser);
                                getUserData(mUser);
                            }
                        }
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getUserData(UserObject mUser) {
        DatabaseReference mUserDB=FirebaseDatabase.getInstance().getReference().child("user").child(mUser.getUid());
        mUserDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserObject mUser=new UserObject(snapshot.getKey());
                if(snapshot.child("notificationKey").getChildren()!=null){
                    mUser.setNotificationKey(snapshot.child("notificationKey").getValue().toString());
                }
                for(ChatObject mChat : chatlist){
                    for(UserObject mUserIt: mChat.getUserObjectArrayList()){
                        if(mUserIt.getUid().equals(mUser.getUid())){
                            mUserIt.setNotificationKey(mUser.getNotificationKey());
                        }
                    }
                }

                mchat_listadapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initialize_recyclerview(){
        chatlist=new ArrayList<>();
        mchat_list=findViewById(R.id.chatlist);
        mchat_list.setNestedScrollingEnabled(false);
        mchat_list.setHasFixedSize(true);
        mchat_layoutmanager= new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);
        mchat_list.setLayoutManager(mchat_layoutmanager);
        mchat_listadapter=new ChatListAdapter(chatlist);
        mchat_list.setAdapter(mchat_listadapter);

    }
}