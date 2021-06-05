package com.example.e440.menu.wally_original;

import java.io.Serializable;
import java.util.List;

public class ItemsBank implements Serializable {

    public int id;

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
