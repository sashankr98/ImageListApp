package com.example.sashank.imagelistapp;

/**
 * Created by sashank on 19/6/17.
 */

public interface Communicator {

    void sendCaptionMessage(String caption);

    void sendDeleteMessage(int position);

}
