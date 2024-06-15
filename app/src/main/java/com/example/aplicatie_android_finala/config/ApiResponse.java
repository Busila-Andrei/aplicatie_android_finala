package com.example.aplicatie_android_finala.config;


import android.util.Log;

public class ApiResponse {

    private boolean success;
    private String message;
    private Object data;
    private static final String TAG = "ApiResponse";

    public ApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public ApiResponse(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public boolean isSuccess() {
        Log.d(TAG, "isSuccess called: " + success);
        return success;
    }

    public void setSuccess(boolean success) {
        Log.d(TAG, "setSuccess called with: " + success);
        this.success = success;
    }

    public String getMessage() {
        Log.d(TAG, "getMessage called: " + message);
        return message;
    }

    public void setMessage(String message) {
        Log.d(TAG, "setMessage called with: " + message);
        this.message = message;
    }

    public Object getData() {
        Log.d(TAG, "getData called: " + data);
        return data;
    }

    public void setData(Object data) {
        Log.d(TAG, "setData called with: " + data);
        this.data = data;
    }
}
