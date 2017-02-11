package com.shizhefei.test.view.adapters.multitype.provider;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shizhefei.test.view.adapters.multitype.Message;
import com.shizhefei.view.multitype.ItemViewProvider;
import com.shizhefei.view.mvc.demo.R;

/**
 * Created by LuckyJayce on 2016/8/8.
 */
public class MessageProvider extends ItemViewProvider<Message> {
    public static final int ALIGN_LEFT = 0;
    public static final int ALIGN_RIGHT = 1;
    private int align;

    public MessageProvider(int align) {
        this.align = align;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int providerType) {
        if (align == ALIGN_LEFT) {
            return new ItemViewHolder(inflater.inflate(R.layout.item_message_left, parent, false));
        }
        return new ItemViewHolder(inflater.inflate(R.layout.item_message_right, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, Message message) {
        ItemViewHolder vh = (ItemViewHolder) viewHolder;
        vh.textView.setText(message.text);
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        private final TextView textView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.item_message_textView);
        }
    }
}
