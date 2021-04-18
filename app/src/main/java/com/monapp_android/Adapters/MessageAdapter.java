package com.monapp_android.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.monapp_android.DTOs.MessageDTO;
import com.monapp_android.R;
import com.monapp_android.network.MessageReader;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageHolder> {
    private List<MessageDTO> messages = new ArrayList<>();
    private MessageReader reader = new MessageReader();

    @Override
    public MessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
        return new MessageHolder(view);
    }

    @Override
    public void onBindViewHolder(MessageHolder holder, int position) {
        MessageDTO currentMessage = messages.get(position);
        holder.messageDate.setText(currentMessage.getCreationDateString());
        holder.messageText.setText(currentMessage.getText());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void setMessages(List<MessageDTO> messages){
        this.messages = messages;
        reader.readMessages(messages);
        notifyDataSetChanged();
    }

    public MessageDTO getMessageAt(int position){
        return messages.get(position);
    }

    class MessageHolder extends RecyclerView.ViewHolder{
        private TextView messageDate;
        private TextView messageText;

        public MessageHolder(View view){
            super(view);
            messageDate = view.findViewById(R.id.message_date);
            messageText = view.findViewById(R.id.message_text);
        }
    }
}
