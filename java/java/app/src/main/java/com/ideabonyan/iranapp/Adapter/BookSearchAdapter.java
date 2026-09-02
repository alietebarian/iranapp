package com.ideabonyan.iranapp.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ideabonyan.iranapp.Models.BookSearchModel;
import com.ideabonyan.iranapp.R;

import java.util.List;


public class BookSearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<BookSearchModel> datas;
    Context context;

    public BookSearchAdapter(List<BookSearchModel> datas, Context context) {
        this.datas = datas;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.rv_pishkhan_book, parent, false);

        return new BookSearchAdapter.CellFeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final BookSearchAdapter.CellFeedViewHolder holder = (BookSearchAdapter.CellFeedViewHolder) viewHolder;

        holder.title.setText(datas.get(position).getBookName());
        holder.writer.setText(datas.get(position).getWriterName());
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    private class CellFeedViewHolder extends RecyclerView.ViewHolder {

        TextView title, writer;

        CellFeedViewHolder(View view) {
            super(view);

            title = (TextView) view.findViewById(R.id.pishkhanBookRvName);
            writer = (TextView) view.findViewById(R.id.pishkhanBookRvWriter);
        }
    }
}
