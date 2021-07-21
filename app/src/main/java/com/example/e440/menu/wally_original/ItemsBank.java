package com.example.e440.menu.wally_original;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ItemsBank implements Serializable {

    public int id;
    @SerializedName(value="nameInitials", alternate={"name_initials"})
    public String nameInitials;

    public String getNameInitials(){
        return String.format("%s",this.nameInitials);
    }

    public String getName() {
        if (name == null){
            return "nulo";
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String name;

    public String getInstruction() {
        if (instruction==null){
            return "";
        }
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public String instruction;
    public List<WallyOriginalItem> items;

}
