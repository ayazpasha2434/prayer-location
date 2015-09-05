package com.example.root.helloagain;

/**
 * Created by root on 30/8/15.
 */
public interface VolleyTasksListener<T> {

    public void handleResult (T response);
    public void handleError (Exception e);
}
