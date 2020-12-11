package Media;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chat_android.R;

import java.util.ArrayList;

public class MediaAdapter extends  RecyclerView.Adapter<MediaAdapter.MediaViewHolder> {
    Context context;
    ArrayList<String> mediaList;

    public MediaAdapter(Context context, ArrayList<String> mediaList){
        this.mediaList=mediaList;
        this.context=context;
    }

    @NonNull
    @Override
    public MediaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutview = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_media,null,false);
        MediaViewHolder mholder=new MediaViewHolder(layoutview);
        return mholder;
    }

    @Override
    public void onBindViewHolder(@NonNull MediaViewHolder holder, int position) {
        Glide.with(context).load(Uri.parse(mediaList.get(position))).into(holder.media);


    }

    @Override
    public int getItemCount() {
        return mediaList.size();
    }

    public class MediaViewHolder extends RecyclerView.ViewHolder{
        ImageView media;
        public MediaViewHolder(@NonNull View itemView) {
            super(itemView);
            media=itemView.findViewById(R.id.media);
        }
    }
}
