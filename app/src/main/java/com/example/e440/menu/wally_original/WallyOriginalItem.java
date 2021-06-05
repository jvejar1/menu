package com.example.e440.menu.wally_original;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.e440.menu.fonotest.Item;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "item")
public class WallyOriginalItem implements Serializable {

    @PrimaryKey
    private int id;

    public List<ItemChoice> getChoices() {
        if (this.choices==null){
            this.choices= new ArrayList<>(0);
        }
        return choices;
    }
    public ItemChoice getChoice(int which){

        return this.choices.get(which);
    }

    public void setChoices(List<ItemChoice> choices) {
        this.choices = choices;
    }

    public List<ItemChoice> choices;

    private String text;
    private String encoded_image;
    public int pictureId;

    public Integer getItemTypeId() {
        return itemTypeId;
    }

    public void setItemTypeId(Integer itemTypeId) {
        this.itemTypeId = itemTypeId;
    }

    public Integer itemTypeId;
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private String description;

    public String getImagePath() {
        return ImagePath;
    }

    public void setImagePath(String imagePath) {
        ImagePath = imagePath;
    }

    private String ImagePath;
    private int server_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return getDescription();
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getEncoded_image() {
        return encoded_image;
    }

    public void setEncoded_image(String encoded_image) {
        this.encoded_image = encoded_image;
    }

    public int getServer_id() {
        return server_id;
    }

    public void setServer_id(int server_id) {
        this.server_id = server_id;
    }


    public WallyOriginalItem(String text, String encoded_image, int server_id, int itemTypeId){
        this.text = text;
        this.description = text;
        this.encoded_image = encoded_image;
        this.server_id = server_id;
        this.itemTypeId = itemTypeId;
    }
}
