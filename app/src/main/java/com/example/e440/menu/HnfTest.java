package com.example.e440.menu;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Created by e440 on 17-05-18.
 */
@Entity
public class HnfTest {
    final static int HEARTS_PRACTICE_MODE=0;
    final static int HEARTS_TEST_MODE=1;
    final static int FLOWERS_PRACTICE_MODE=2;
    final static int FLOWERS_TEST_MODE=3;
    final static int HEARTS_AND_FLOWERS_TEST_MODE=4;

    final static int HEARTS_TEST_TYPE=0;
    final static int FLOWERS_TEST_TYPE=1;
    final static int HEARTS_AND_FLOWERS_TEST_TYPE=2;

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int server_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getServer_id() {
        return server_id;
    }

    public void setServer_id(int server_id) {
        this.server_id = server_id;
    }

    public int getHnf_type() {
        return hnf_type;
    }

    public void setHnf_type(int hnf_type) {
        this.hnf_type = hnf_type;
    }

    public HnfTest(int server_id, int hnf_type) {
        this.server_id = server_id;
        this.hnf_type = hnf_type;
    }

    private int hnf_type;

}
