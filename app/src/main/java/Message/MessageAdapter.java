package Message;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat_android.R;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private ArrayList<MessageObject> MessageList;

    public MessageAdapter(ArrayList<MessageObject> MessageList){
        this.MessageList=MessageList;
    }

    @NonNull
    @Override
    public MessageAdapter.MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutview= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message,null,false);
        RecyclerView.LayoutParams lp=new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutview.setLayoutParams(lp);
        MessageAdapter.MessageViewHolder vholder=new MessageAdapter.MessageViewHolder(layoutview);
        return vholder;

    }

    @Override
    public void onBindViewHolder(@NonNull final MessageAdapter.MessageViewHolder holder, int position) {
        holder.m_sender.setText(MessageList.get(position).getSenderID());
        holder.m_message.setText(MessageList.get(position).getMessage());

        if(MessageList.get(holder.getAdapterPosition()).getMediaUrlList().isEmpty()){
            holder.mviewMedia.setVisibility(View.GONE);
        }

        holder.mviewMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ImageViewer.Builder(view.getContext(), MessageList.get(holder.getAdapterPosition()).getMediaUrlList())
                        .setStartPosition(0)
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return MessageList.size();
    }


    public class MessageViewHolder extends  RecyclerView.ViewHolder {
        TextView m_message,m_sender;
        LinearLayout mlayout;
        Button mviewMedia;
        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            m_message=itemView.findViewById(R.id.message);
            m_sender=itemView.findViewById(R.id.sender);
            mlayout=itemView.findViewById(R.id.layout);
            mviewMedia=itemView.findViewById(R.id.viewMedia);
        }
    }
}
