package com.ideabonyan.iranapp.Adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ideabonyan.iranapp.Models.NewsData;
import com.ideabonyan.iranapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;


public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {


    List<NewsData> datas;
    Context context;

    public NewsAdapter(List<NewsData> datas, Context context) {
        this.datas = datas;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.rv_news, parent, false);

        return new NewsAdapter.CellFeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final NewsAdapter.CellFeedViewHolder holder = (NewsAdapter.CellFeedViewHolder) viewHolder;

        holder.title.setText(datas.get(position).getTitle());
        holder.time.setText(datas.get(position).getCreated_at());

        if (datas.get(position).getPhotos() != null && datas.get(position).getPhotos().size() > 0) {
            if (datas.get(position).getPhotos().get(0) != null) {
                Picasso.get()
                        .load(datas.get(position).getPhotos().get(0).getName())
                        .fit()
                        .into(holder.imageView);
            }else {
                Picasso.get()
                        .load(R.drawable.place_holder)
                        .resize(200, 200)
                        //.fit()
//                    .resizeDimen(16, 9)
                        .centerCrop()
                        //  .placeholder(R.drawable.placeholder)
                        .into(holder.imageView);

            }
        }else {
            Picasso.get()
                    .load(R.drawable.place_holder)
                    .resize(200, 200)
                    //.fit()
//                    .resizeDimen(16, 9)
                    .centerCrop()
                    //  .placeholder(R.drawable.placeholder)
                    .into(holder.imageView);

        }
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    private class CellFeedViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView title, time;

        CellFeedViewHolder(View view) {
            super(view);

            imageView = (ImageView) view.findViewById(R.id.newsRvImage);
            title = (TextView) view.findViewById(R.id.newsRvTitle);
            time = (TextView) view.findViewById(R.id.newsRvTime);
        }
    }
}
