package com.monapp_android.DTOs;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MessageDTO {
    private String id;
    private Date creationDate;
    private String text;
    private Boolean read;

    public MessageDTO(JSONObject jsonObject) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
            creationDate = formatter.parse(jsonObject.getString("creation_time"));
            text = jsonObject.getString("text");
            id = jsonObject.getString("id");
            read = false;
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public MessageDTO(String text, Date date) {
        this.text = text;
        this.creationDate = date;
        this.id = id;
        this.read = false;
    }

    public MessageDTO(String messageDTO){
        String[] splitted = messageDTO.split("<:>");
        this.text = splitted[0];

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        try {
            this.creationDate = formatter.parse(splitted[1]);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        this.id = splitted[2];
        this.read = Boolean.valueOf(splitted[3]);
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public String getCreationDateString(){
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        return formatter.format(creationDate);
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getId() {return id;}

    public void setId(String id) {this.id = id;}

    public Boolean getRead() {return read;}

    public void setRead(Boolean read) {this.read = read;}

    @Override
    public String toString() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        return text + "<:>" + formatter.format(creationDate) + "<:>" + id + "<:>" + String.valueOf(read);
    }
}
