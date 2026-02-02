package com.example.androidcontroller;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ControllerActivity extends AppCompatActivity {

    private float motL = 0.5F;
    private float motR = 0.5F;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller);

        new Thread(new Runnable() {
            public void run() {
                GameMessageManager.sendMessage("NAME=BerthillotMosleh#COL=66=245=227");
            }
        }).start();

        final Button forwardButton = (Button) findViewById(R.id.forwardButton);
        final Button backButton = (Button) findViewById(R.id.backButton);
        final Button leftButton = (Button) findViewById(R.id.leftButton);
        final Button rightButton = (Button) findViewById(R.id.rightButton);
        final Button stopButton = (Button) findViewById(R.id.stopButton);
        final Button shootButton = (Button) findViewById(R.id.shootButton1);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    GameMessageManager.sendMessage("LIVE#MSG=LiveSent");
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        forwardButton.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        new Thread(new Runnable() {
                            public void run() {
                                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                    motL = 1.0F;
                                    motR = 1.0F;
                                    GameMessageManager.sendMessage("MotL=" + motL + "#MotR=" + motR);
                                }
                                if (event.getAction() == MotionEvent.ACTION_UP) {
                                    motL = 0.5F;
                                    motR = 0.5F;
                                    GameMessageManager.sendMessage("MotL=" + motL + "#MotR=" + motR);
                                }
                            }
                        }).start();
                        return true;
                    }
                }
        );

        leftButton.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        new Thread(new Runnable() {
                            public void run() {
                                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                    motL = 0.0F;
                                    motR = 1.0F;
                                    GameMessageManager.sendMessage("MotL=" + motL + "#MotR=" + motR);
                                }
                                if (event.getAction() == MotionEvent.ACTION_UP) {
                                    motL = 0.5F;
                                    motR = 0.5F;
                                    GameMessageManager.sendMessage("MotL=" + motL + "#MotR=" + motR);
                                }
                            }
                        }).start();
                        return true;
                    }
                }
        );

        rightButton.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        new Thread(new Runnable() {
                            public void run() {
                                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                    motL = 1.0F;
                                    motR = 0.0F;
                                    GameMessageManager.sendMessage("MotL=" + motL + "#MotR=" + motR);
                                }
                                if (event.getAction() == MotionEvent.ACTION_UP) {
                                    motL = 0.5F;
                                    motR = 0.5F;
                                    GameMessageManager.sendMessage("MotL=" + motL + "#MotR=" + motR);
                                }
                            }
                        }).start();
                        return true;
                    }
                }
        );

        backButton.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        new Thread(new Runnable() {
                            public void run() {
                                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                    motL = 0.0F;
                                    motR = 0.0F;
                                    GameMessageManager.sendMessage("MotL=" + motL + "#MotR=" + motR);
                                }
                                if (event.getAction() == MotionEvent.ACTION_UP) {
                                    motL = 0.5F;
                                    motR = 0.5F;
                                    GameMessageManager.sendMessage("MotL=" + motL + "#MotR=" + motR);
                                }
                            }
                        }).start();
                        return true;
                    }
                }
        );

        stopButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                GameMessageManager.sendMessage("EXIT");
                            }
                        }).start();
                        finish();
                    }
                }
        );

        shootButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                GameMessageManager.sendMessage("GunTrig=1");
                            }
                        }).start();
                    }
                }
        );

    }

    private void running(){

    }
}