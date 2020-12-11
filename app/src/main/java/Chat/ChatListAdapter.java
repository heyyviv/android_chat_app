package Chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat_android.ChatActivity;
import com.example.chat_android.R;

import java.util.ArrayList;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder> {
    private ArrayList<ChatObject> chatlist;

    public ChatListAdapter(ArrayList<ChatObject> chatlist){
        this.chatlist=chatlist;
    }

    @NonNull
    @Override
    public ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutview= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat,null,false);
        RecyclerView.LayoutParams lp=new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutview.setLayoutParams(lp);
        ChatListViewHolder vholder=new ChatListViewHolder(layoutview);
        return vholder;

    }

    @Override
    public void onBindViewHolder(@NonNull final ChatListViewHolder holder, int position) {
        holder.mtitle.setText(chatlist.get(position).getChatID());
        holder.mlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ChatActivity.class);

                intent.putExtra("chatObject",chatlist.get(holder.getAdapterPosition()));
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatlist.size();
    }


    public class ChatListViewHolder extends  RecyclerView.ViewHolder {
        TextView mtitle;
        LinearLayout mlayout;
        public ChatListViewHolder(@NonNull View itemView) {
            super(itemView);
            mtitle=itemView.findViewById(R.id.title);
            mlayout=itemView.findViewById(R.id.layout);
        }
    }
}


