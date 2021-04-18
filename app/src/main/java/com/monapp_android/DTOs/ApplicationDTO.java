package com.monapp_android.DTOs;

import org.json.JSONException;
import org.json.JSONObject;

public class ApplicationDTO {
    private String name;
    private String blocked;


    public ApplicationDTO(){}

    public ApplicationDTO(JSONObject jsonObject){
        try {
            name = jsonObject.getString("name");
            blocked = jsonObject.getString("blocked");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBlocked() {
        return blocked;
    }

    public void setBlocked(String blocked) {
        this.blocked = blocked;
    }

    @Override
    public String toString() {
        return "ApplicationDTO{" +
                "name='" + name + '\'' +
                ", blocked='" + blocked + '\'' +
                '}';
    }



}
