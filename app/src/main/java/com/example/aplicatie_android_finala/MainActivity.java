package com.example.aplicatie_android_finala;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.aplicatie_android_finala.fragments.*;

public class MainActivity extends AppCompatActivity implements
        SplashScreenFragment.OnSplashScreenListener,
        LoginFragment.OnLoginListener,
        DecisionFragment.OnLoginSelectedListener,
        DecisionFragment.OnRegisterSelectedListener,
        RegisterFragment.OnRegisterListener,
        ConfirmationFragment.OnConfirmationListener{

    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadFragment(new SplashScreenFragment());
    }

    public void loadFragment(Fragment fragment) {
        if (!isFinishing() && !isDestroyed()) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentFrame, fragment)
                    .commitAllowingStateLoss();
        } else {
            Log.e(TAG, "Activity is finishing or destroyed, cannot load fragment.");
        }
    }

    @Override
    public void onSplashScreenComplete(boolean isSuccess) {
        if (isSuccess) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        } else {
            loadFragment(new DecisionFragment());
        }
    }

    @Override
    public void onLoginComplete() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onLoginSelected() {
        loadFragment(new LoginFragment());
    }

    @Override
    public void onRegisterSelected() {
        loadFragment(new RegisterFragment());
    }

    @Override
    public void onRegisterComplete(String req) {
        loadFragment(new ConfirmationFragment(req));
    }

    @Override
    public void onConfirmationComplete() {
        loadFragment(new LoginFragment());
    }

    @Override
    public void onBackToRegister() {
        loadFragment(new RegisterFragment());
    }
}
