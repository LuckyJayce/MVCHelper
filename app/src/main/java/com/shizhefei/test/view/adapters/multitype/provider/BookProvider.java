package com.shizhefei.test.view.adapters.multitype.provider;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shizhefei.test.models.enties.Book;
import com.shizhefei.view.multitype.ItemViewProvider;
import com.shizhefei.view.mvc.demo.R;

/**
 * Created by LuckyJayce on 2017/2/11.
 */

public class BookProvider extends ItemViewProvider<Book>{

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int providerType) {
        return new BookViewHolder(inflater.inflate(R.layout.item_book,parent,false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, Book book) {
        BookViewHolder holder = (BookViewHolder) viewHolder;
        holder.setData(book);
    }

    private static class BookViewHolder extends RecyclerView.ViewHolder{

        private final TextView textView;
        private Book data;

        public BookViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView;
        }

        public void setData(Book data) {
            this.data = data;
            textView.setText(data.getName());
        }
    }
}
