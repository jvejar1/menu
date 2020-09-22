package com.example.e440.menu;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by e440 on 01-05-18.
 */
@Entity
public class Ace {

    final static int HAPPY=2;
    final static int SCARED=4;
    final static int ANGRY=1;
    final static int SAD=3;

    final static HashMap<Integer,String> male_feelings_by_number =new HashMap<Integer, String>() {{
        put(HAPPY,"FELIZ");
        put(SCARED,"ASUSTADO");
        put(SAD,"TRISTE");
        put(ANGRY,"ENOJADO");
    }};

    final static HashMap<Integer,String> female_feelings_by_number=new HashMap<Integer, String>() {{
        put(HAPPY,"FELIZ");
        put(SCARED,"ASUSTADA");
        put(SAD,"TRISTE");
        put(ANGRY,"ENOJADA");
    }};

    final static HashMap<String,Integer> feelings_by_name=new HashMap<String, Integer>(){{
        put("FELIZ",HAPPY);
        put("ASUSTADO",SCARED);
        put("ASUSTADA",SCARED);
        put("TRISTE",SAD);
        put("ENOJADO",ANGRY);
        put("ENOJADA",ANGRY);
    }};
    @PrimaryKey(autoGenerate = true)
    int id;

    public Ace(float version, int server_id) {
        this.version = version;
        this.server_id = server_id;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public float getVersion() {
        return version;
    }
    public void setVersion(float version) {
        this.version = version;
    }

    public int getServer_id() {
        return server_id;
    }

    public void setServer_id(int server_id) {
        this.server_id = server_id;
    }

    float version;
    int server_id;


}
