package com.example.e440.menu.wally_original;

import java.io.Serializable;
import java.util.List;

public class ItemsBank implements Serializable {

    public int id;
    public String name;
    public String instruction;
    public List<WallyOriginalItem> items;

}
