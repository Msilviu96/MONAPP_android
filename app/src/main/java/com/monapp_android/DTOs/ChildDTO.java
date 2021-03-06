package com.monapp_android.DTOs;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ChildDTO implements Parcelable {
    private Integer id;
    private Integer parent_id;
    private String firstName;
    private String lastName;
    private String token;
    private String gender;
    private Date birthDate;
    private String image;

    protected ChildDTO(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        if (in.readByte() == 0) {
            parent_id = null;
        } else {
            parent_id = in.readInt();
        }
        firstName = in.readString();
        lastName = in.readString();
        token = in.readString();
        gender = in.readString();
    }

    public ChildDTO() {}

    public ChildDTO(String json){
        try {
            JSONObject obj = new JSONObject(json);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.GERMAN);
            this.id = obj.getInt("id");
            this.parent_id = obj.getInt("parent_id");
            this.lastName = obj.getString("lastName");
            this.firstName = obj.getString("firstName");
            this.token = obj.getString("token");
            this.gender = obj.getString("gender");
            this.birthDate = simpleDateFormat.parse(obj.getString("birthDate"));
//            this.image = obj.getString("image");

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    public static final Creator<ChildDTO> CREATOR = new Creator<ChildDTO>() {
        @Override
        public ChildDTO createFromParcel(Parcel in) {
            return new ChildDTO(in);
        }

        @Override
        public ChildDTO[] newArray(int size) {
            return new ChildDTO[size];
        }
    };

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getParent_id() {
        return parent_id;
    }

    public void setParent_id(Integer parent_id) {
        this.parent_id = parent_id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getImage() {return this.image;}

    public void setImage(String image) {this.image = image;}

    public static ChildDTO fromJson(JSONObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }
        System.out.println(jsonObject);
        ChildDTO childDTO = new ChildDTO();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            childDTO.setId((Integer) jsonObject.get("id"));
            childDTO.setBirthDate(format.parse(jsonObject.get("birth_day").toString()));
            childDTO.setFirstName((String) jsonObject.get("first_name"));
            childDTO.setLastName((String) jsonObject.get("last_name"));
            childDTO.setToken((String) jsonObject.get("token"));
            childDTO.setGender((String) jsonObject.get("gender"));
            childDTO.setParent_id((Integer) jsonObject.get("parent"));
            childDTO.setImage((String)jsonObject.get("image"));

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return childDTO;
    }

    @Override
    public String toString() {
        return "ChildDTO{" +
                "id=" + id +
                ", parent_id=" + parent_id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", token='" + token + '\'' +
                ", gender='" + gender + '\'' +
                ", birthDate=" + birthDate +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        if (id == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(id);
        }
        if (parent_id == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(parent_id);
        }
        parcel.writeString(firstName);
        parcel.writeString(lastName);
        parcel.writeString(token);
        parcel.writeString(gender);
    }

    public String toJson() {
        StringBuilder sb = new StringBuilder();

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.GERMAN);

        String SEP = ",";
        sb.append("{\"id\":")      .append("\"").append(Integer.toString(id)).append("\"")        .append(SEP)
          .append("\"parent_id\":").append("\"").append(Integer.toString(parent_id)).append("\"") .append(SEP)
          .append("\"firstName\":").append("\"").append(firstName).append("\"")                   .append(SEP)
          .append("\"lastName\":") .append("\"").append(lastName).append("\"")                    .append(SEP)
          .append("\"token\":")    .append("\"").append(token) .append("\"")                      .append(SEP)
          .append("\"gender\":")   .append("\"").append(gender).append("\"")                      .append(SEP)
          .append("\"birthDate\":").append("\"").append(dateFormat.format(birthDate)).append("\"").append("}");
//          .append("\"image\":")    .append(image)                       .append("}");

        return sb.toString();
    }
}
