package com.example.chat_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import User.UserListAdapter;
import User.UserObject;

public class FindUserActivity extends AppCompatActivity {
    private RecyclerView muser_list;
    private RecyclerView.Adapter muser_listadapter;
    private RecyclerView.LayoutManager muser_layoutmanager;
    private ArrayList<UserObject> userlist,contactlist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_user);
        userlist=new ArrayList<>();
        contactlist=new ArrayList<>();
        Button create_room=findViewById(R.id.create);
        create_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createChat();
            }
        });

        initialize_recyclerview();
        get_contact();  
    }

    private void  createChat(){
        String key= FirebaseDatabase.getInstance().getReference().child("chat").push().getKey();
        DatabaseReference userDB=FirebaseDatabase.getInstance().getReference().child("user");
        DatabaseReference chatDB=FirebaseDatabase.getInstance().getReference().child("chat").child(key).child("info");

        HashMap ChatMap=new HashMap();
        ChatMap.put("id",key);
        ChatMap.put("users/"+ FirebaseAuth.getInstance().getUid(),true);
        boolean validChat=  false;
        for(UserObject mUser : userlist){
            if(mUser.getSelected()){
                validChat=true;
                ChatMap.put("users/"+mUser.getUid(),true);
                userDB.child(mUser.getUid()).child("chat").child(key).setValue(true);
            }
        }


        if(validChat) {
            chatDB.updateChildren(ChatMap);
            userDB.child(FirebaseAuth.getInstance().getUid()).child("chat").child(key).setValue(true);
        }


    }

    private void get_contact(){
        Cursor phones =getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);
        while(phones.moveToNext()){
            String prefix=getISO();
            String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String number=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            number=number.replace(" ","");
            number=number.replace("-","");
            number=number.replace("(","");
            number=number.replace(")","");
            if(!String.valueOf(number.charAt(0)).equals("+")){
                number=prefix+number;
            }
            UserObject contact=new UserObject("",name,number);
            contactlist.add(contact);
            //muser_listadapter.notifyDataSetChanged();
            getuserDetail(contact);
        }
    }

    private void getuserDetail(UserObject contact) {
        DatabaseReference mdb= FirebaseDatabase.getInstance().getReference().child("user");
        Query query= mdb.orderByChild("phone").equalTo(contact.getPhone());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String phone="",name="";
                    for(DataSnapshot Child: snapshot.getChildren()){
                        if(Child.child("phone").getValue()!=null){
                            phone=Child.child("phone").getValue().toString();
                        }
                        if(Child.child("name").getValue()!=null){
                            name=Child.child("name").getValue().toString();
                        }

                        if(name.equals(phone)){
                            for(UserObject mcontact_iterator : contactlist){
                                if(mcontact_iterator.getPhone().equals(contact.getPhone())){
                                    contact.setName(mcontact_iterator.getName());
                                }
                            }
                        }

                        UserObject muser=new UserObject(snapshot.getKey(),name,phone);
                        userlist.add(muser);
                        muser_listadapter.notifyDataSetChanged();
                        return;

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private String getISO(){
        String iso=null;

        TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        if(tm.getNetworkCountryIso() != null) {
            iso = tm.getSimCountryIso().toString();
        }
        return ISO2phone.getPhone(iso);
    }


    private void initialize_recyclerview(){
        muser_list=findViewById(R.id.recycler_view);
        muser_list.setNestedScrollingEnabled(false);
        muser_list.setHasFixedSize(true);
        muser_layoutmanager= new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);
        muser_list.setLayoutManager(muser_layoutmanager);
        muser_listadapter=new UserListAdapter(userlist);
        muser_list.setAdapter(muser_listadapter);

    }
}