package User;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat_android.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserListViewHolder> {
    private ArrayList<UserObject> userlist;

    public UserListAdapter(ArrayList<UserObject> userlist){
        this.userlist=userlist;
    }

    @NonNull
    @Override
    public UserListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutview= LayoutInflater.from(parent.getContext()).inflate(R.layout.itemuser,null,false);
        RecyclerView.LayoutParams lp=new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutview.setLayoutParams(lp);
        UserListViewHolder vholder=new UserListViewHolder(layoutview);
        return vholder;

    }

    @Override
    public void onBindViewHolder(@NonNull UserListViewHolder holder, int position) {
        holder.name.setText(userlist.get(position).getName());
        holder.phone.setText(userlist.get(position).getPhone());
        //createChat(holder.getAdapterPosition());

        holder.mAdd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                userlist.get(holder.getAdapterPosition()).setSelected(b);
            }
        });

    }

    @Override
    public int getItemCount() {
        return userlist.size();
    }




    public class UserListViewHolder extends  RecyclerView.ViewHolder {
        TextView name,phone;
        LinearLayout mlayout;
        CheckBox mAdd;
        public UserListViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.u_name);
            phone=itemView.findViewById(R.id.u_phone);
            mAdd=itemView.findViewById(R.id.add);
            mlayout=itemView.findViewById(R.id.layout);
        }
    }
}
