package com.example.chat_android;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import Chat.ChatObject;
import Media.MediaAdapter;
import Message.MessageAdapter;
import Message.MessageObject;
import User.UserObject;

public class ChatActivity extends AppCompatActivity {

    Button mSend,maddMedia;
    ChatObject mChatObject;
    DatabaseReference mchatmessageDB;
    EditText mText;

    private RecyclerView mChat,mMedia;
    private RecyclerView.Adapter mChatadapter,mMediaadapter;
    private RecyclerView.LayoutManager mchat_layoutmanager,mMedia_layoutmanager;
    private ArrayList<MessageObject> messageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mChatObject=(ChatObject) getIntent().getSerializableExtra("chatObject");
        mchatmessageDB=FirebaseDatabase.getInstance().getReference().child("chat")
                .child(mChatObject.getChatID()).child("messages");

        mSend=(Button)findViewById(R.id.send_btn);
        maddMedia=findViewById(R.id.addMedia);

        maddMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        initializeRecyclerView();
        initializeMedia();
        getchatMessage();
    }


    ArrayList<String> mMediaIDList=new ArrayList<>();
    int totalupload=0;
    private void sendMessage(){
         mText=(EditText)findViewById(R.id.message_typed);

            String messageID=mchatmessageDB.push().getKey();
            DatabaseReference newMessageDB = mchatmessageDB.child(messageID);
            Map newMessageMap=new HashMap<>();
            newMessageMap.put("creator", FirebaseAuth.getInstance().getUid());
            if(!mText.getText().toString().isEmpty()){
                newMessageMap.put("text",mText.getText().toString());
            }
            newMessageDB.updateChildren(newMessageMap);

            if(!MediaURI.isEmpty()){
                for(String mediaUri : MediaURI){
                    String mediaID=newMessageDB.child("media").push().getKey();
                    final StorageReference filepath=FirebaseStorage.getInstance().getReference().child("chat").
                            child(mChatObject.getChatID()).child(messageID).child(mediaID);
                    UploadTask uploadTask=filepath.putFile(Uri.parse(mediaUri));
                    mMediaIDList.add(mediaUri);
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    newMessageMap.put("/media/"+mMediaIDList.get(totalupload)+"/",uri.toString());
                                    totalupload++;
                                    if(totalupload==MediaURI.size()){
                                        updateDatabasewithNewmessage(newMessageDB,newMessageMap);
                                    }
                                }
                            });
                        }
                    });

                }
            }else{
                if(!mText.getText().toString().isEmpty()){
                    updateDatabasewithNewmessage(newMessageDB,newMessageMap);
                }
            }

        mText.setText(null);

    }

    private  void updateDatabasewithNewmessage(DatabaseReference newMessageDB,Map newMessageMap){
        newMessageDB.updateChildren(newMessageMap);
        mText.setText(null);
        MediaURI.clear();
        mMediaIDList.clear();
        mMediaadapter.notifyDataSetChanged();

        String message;
        if(newMessageMap.get("text")!=null){
            message=newMessageMap.get("text").toString();
        }else{
            message="Media received";
        }

        for(UserObject mUser:mChatObject.getUserObjectArrayList()){
            if(!mUser.getUid().equals(FirebaseAuth.getInstance().getUid())){
                new SendNotification(message,"New Message",mUser.getNotificationKey());
            }
        }

    }

    private  void getchatMessage(){
        mchatmessageDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists()){
                    String text="", creatorID="";
                    ArrayList<String> mediaUrlList=new ArrayList<>();
                    if(snapshot.child("text").exists()){
                        text=snapshot.child("text").getValue().toString();
                    }
                    if(snapshot.child("creator").exists()){
                        creatorID=snapshot.child("creator").getValue().toString();
                    }
                    if(snapshot.child("media").getChildrenCount() >0){
                        for(DataSnapshot mediaSnapshot : snapshot.child("media").getChildren()){
                            mediaUrlList.add(mediaSnapshot.getValue().toString());
                        }
                    }

                    MessageObject mMessage=new MessageObject(snapshot.getKey(),text,creatorID,mediaUrlList);
                    messageList.add(mMessage);
                    mchat_layoutmanager.scrollToPosition(messageList.size()-1);
                    mChatadapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) { }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

    }

    private  void initializeRecyclerView(){
        messageList=new ArrayList<>();
        mChat=findViewById(R.id.chat_recycler);
        mChat.setNestedScrollingEnabled(false);
        mChat.setHasFixedSize(true);
        mchat_layoutmanager= new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);
        mChat.setLayoutManager(mchat_layoutmanager);
        mChatadapter=new MessageAdapter(messageList);
        mChat.setAdapter(mChatadapter);
    }



    int PICK_IMAGE=1;
    ArrayList<String> MediaURI=new ArrayList<>();
    private void openGallery(){
        Intent intent=new Intent();
        intent.setType("Image/*");
        intent.putExtra(intent.EXTRA_ALLOW_MULTIPLE,true);
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture(s)"),PICK_IMAGE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode==PICK_IMAGE){
                if(data.getClipData()==null){
                    MediaURI.add(data.getData().toString());
                }else{
                    for(int i=0;i<data.getClipData().getItemCount();i++){
                        MediaURI.add(data.getClipData().getItemAt(i).getUri().toString());
                    }
                }
                mMediaadapter.notifyDataSetChanged();

            }
        }
    }

    private  void initializeMedia(){
        MediaURI=new ArrayList<>();
        mMedia=findViewById(R.id.mediaList);
        mMedia.setNestedScrollingEnabled(false);
        mMedia.setHasFixedSize(true);
        mMedia_layoutmanager= new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false);
        mMedia.setLayoutManager(mMedia_layoutmanager);
        mMediaadapter=new MediaAdapter(getApplicationContext(),MediaURI);
        mMedia.setAdapter(mMediaadapter);
    }


}