package com.example.e440.menu;

/**
 * Created by e440 on 19-07-18.
 */

public interface ResultsSenderListener {
    void OnSendingFinish(int total_sended_count,int errors_count);
    void onProgressUpdate(int progress);

}
