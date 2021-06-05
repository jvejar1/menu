package com.example.e440.menu.wally_original;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Picture implements Serializable {
    static String BASE_DIR="images";
    public String url;
    public Long id;
    public String createdAt;
    public String updatedAt;
    @SerializedName(value="image_file_name",alternate="imageFileName")
    public String imageFileName;

    public Integer getImageFileSize() {
        return imageFileSize;
    }

    public void setImageFileSize(Integer imageFileSize) {
        this.imageFileSize = imageFileSize;
    }

    @SerializedName(value="image_file_size",alternate="imageFileSize")
    public Integer imageFileSize;
    public String imageUpdatedAt;
    public String getFilePath(){
        return String.format("%s/%d/%s",BASE_DIR,id, imageFileName);
    }
}
