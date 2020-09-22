package com.example.e440.menu.wally_original;

public class WallyOriginalItem {
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
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

    private String text;
    private String encoded_image;
    private int server_id;
    public WallyOriginalItem(String text, String encoded_image, int server_id){
        this.text = text;
        this.encoded_image = encoded_image;
        this.server_id = server_id;
    }
}
