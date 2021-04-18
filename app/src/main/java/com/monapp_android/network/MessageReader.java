package com.monapp_android.network;

import android.util.Log;


import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.monapp_android.DAOs.MessageDAO;
import com.monapp_android.DTOs.MessageDTO;
import com.monapp_android.application.MONAPP;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

public class MessageReader {
    private MessageDAO messageDAO = new MessageDAO();
    final File messagesFile = new File(MONAPP.getAppContext().getFilesDir().getAbsolutePath() + File.pathSeparator + MONAPP.getChild().getToken() + "_messages");

    public void readMessages(List<MessageDTO> messages){
        JSONArray readMessages = new JSONArray();

        for(MessageDTO message : messages){
            if(message.getRead()){
                continue;
            }
            JSONObject readMessage = new JSONObject();
            try {
                readMessage.put("id", message.getId());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            readMessages.put(readMessage);
            messageDAO.markMessageAsRead(messagesFile, message);
        }

        messageDAO.readFile(messagesFile);
        notifyServer(readMessages);
    }

    private void notifyServer(JSONArray messages){
        if (messages.length() == 0){
            Log.i("", "All messages were read!");
            return;
        }
        AndroidNetworking.put(MONAPP.ADDRESS + "/rest/message/")
                .addBodyParameter("token", MONAPP.getChild().getToken())
                .addBodyParameter("messages", messages.toString())
                .setTag("information")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                       Log.i("INFO", "MessageReader finished!");
                    }

                    @Override
                    public void onError(ANError error) {
                        Log.e("ERROR", error.toString());
                    }
                });
    }
}
