package com.example.chat_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
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

public class MainActivity extends AppCompatActivity {
    //LOGINACTIVITY.CLASS
    private EditText phone_num,ver_num;
    private Button send_code;
    String mverification_id;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);
        userisLoggedIn();

        phone_num=findViewById(R.id.phone_num);
        ver_num=findViewById(R.id.otp_num);
        send_code=findViewById(R.id.send_btn);

        send_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mverification_id != null){
                    verifyPhoneNumberwithcode();
                }else {
                    start_phone_number_verification();
                }
            }
        });

        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                mverification_id=s;
                send_code.setText("Verify Code");
            }
        };

    }

    public void  verifyPhoneNumberwithcode(){
        PhoneAuthCredential credential= PhoneAuthProvider.getCredential(mverification_id,ver_num.getText().toString());
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user =FirebaseAuth.getInstance().getCurrentUser();
                    if(user!=null){
                    final DatabaseReference muserDB= FirebaseDatabase.getInstance().getReference().child("user").child(user.getUid());
                    muserDB.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(!snapshot.exists()){
                                Map<String,Object> usermap=new HashMap<>();
                                usermap.put("phone",user.getPhoneNumber());
                                if(user.getPhoneNumber()==null){
                                    usermap.put("phone",phone_num.getText().toString());
                                }
                                usermap.put("name",user.getDisplayName());
                                if(user.getDisplayName()==null){
                                    usermap.put("name",phone_num.getText().toString());
                                }
                                muserDB.updateChildren(usermap);

                            }
                            userisLoggedIn();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    }
                    userisLoggedIn();
                }
            }
        });
    }

    private void userisLoggedIn() {
        FirebaseUser user =FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            startActivity(new Intent(getApplicationContext(),MainPageActivity.class));
            finish();
            return;
        }
    }

    private  void start_phone_number_verification(){
        /* old method
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone_num.getText().toString(),
                60,
                TimeUnit.SECONDS,
                this,
                mCallback
        );*/

        FirebaseAuth mauth=FirebaseAuth.getInstance();
        PhoneAuthOptions option= PhoneAuthOptions.newBuilder(mauth)
                .setPhoneNumber(phone_num.getText().toString())
                .setTimeout(60L,TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallback)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(option);
    }
}