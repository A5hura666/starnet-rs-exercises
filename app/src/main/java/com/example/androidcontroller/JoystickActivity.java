package com.example.androidcontroller;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.Random;

public class JoystickActivity extends AppCompatActivity {
    static String playerName, playerColor, playerBGM;
    TextView playerInfo, angleOffsetLabel;
    static float baseAngleOffset = 0.5F;
    static boolean running = true;
    static String shootMode = "Normal";
    static String[] shootModes;
    static String displayMode, rgbString;

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joystick);
        final ConstraintLayout joystickLayout = (ConstraintLayout) findViewById(R.id.joystick);
        shootModes = getResources().getStringArray(R.array.shootModes);
        Bundle data = getIntent().getExtras();
        playerName = data.getString("playerName");
        playerColor = data.getString("playerColor");
        playerBGM = data.getString("playerBGM");
        final Button exitButton = (Button) findViewById(R.id.exitButton);
        playerInfo = (TextView) findViewById(R.id.playerInfo);
        angleOffsetLabel = (TextView) findViewById(R.id.angleOffsetLabel);
        angleOffsetLabel.setText(getString(R.string.angle_offset_label) + " 0°");
        final Button shootButton = (Button) findViewById(R.id.shootButton);
        @SuppressLint("UseSwitchCompatOrMaterialCode") final Switch autoShoot = (Switch) findViewById(R.id.autoShootSwitch);
        final SeekBar angleOffsetBar = (SeekBar) findViewById(R.id.angleOffsetBar);
        final RadioGroup shootingOptions = (RadioGroup) findViewById(R.id.shootingOptions);
        final RadioGroup displayOptions = (RadioGroup) findViewById(R.id.playerDisplayOptions);
        playerInfo.setText(playerInfo.getText() + " " + playerName);

        new Thread(new Runnable() {
            public void run() {
                GameMessageManager.sendMessage("NAME=" + playerName + "#COL=" + playerColor);
            }
        }).start();
        joystickLayout.addView(new JoystickView(this, new JoystickView.ValueChangedHandler() {
            @Override
            public void onValueChanged(int Vg, int Vd) {
                float motL = (Vg + 100) / 200F;
                float motR = (Vd + 100) / 200F;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        GameMessageManager.sendMessage("MotL=" + motL + "#MotR=" + motR);
                    }
                }).start();
            }
        }));

        Thread sendLiveMsg = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.interrupted() && running) {
                    GameMessageManager.sendMessage("LIVE");
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        sendLiveMsg.start();

        Thread rainbowColorGenerator = new Thread(new Runnable() {
            @Override
            public void run() {
                float i = 0;
                int color;
                while (!Thread.interrupted() && running) {
                    color = Color.HSVToColor(new float[]{i, 1.0F, 1.0F});
                    rgbString = Color.red(color) + "=" + Color.green(color) + "=" + Color.blue(color);
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    i++;
                    if (i > 360) i = 0;
                }
            }
        });
        rainbowColorGenerator.start();

        Thread changePlayerColor = new Thread(new Runnable() {
            @Override
            public void run() {
                while (running) {
                    if (displayMode.equals(getString(R.string.rainbow_player_display))) {
                        GameMessageManager.sendMessage("COL=" + rgbString);
                    } else if (displayMode.equals(getString(R.string.hide_player_display))) {
                        GameMessageManager.sendMessage("COL=0=0=0");
                    } else if (displayMode.equals(getString(R.string.color_player_display))) {
                        GameMessageManager.sendMessage("COL=" + playerColor);
                    }
                }
            }
        });

        autoShoot.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        GameMessageManager.sendMessage("GunTrav=" + baseAngleOffset);
                        if (b) GameMessageManager.sendMessage("GunTrig=1");
                        else GameMessageManager.sendMessage("GunTrig=0");
                    }
                }).start();
            }
        });

        shootButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        GameMessageManager.sendMessage("GunTrav=" + baseAngleOffset);
                        if (motionEvent.getAction() == MotionEvent.ACTION_UP) { // Unpressed
                            GameMessageManager.sendMessage("GunTrig=0");
                        }
                        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) { // Pressed
                            GameMessageManager.sendMessage("GunTrig=1");
                        }
                    }
                }).start();
                return true;
            }
        });

        shootingOptions.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton checkedRadio = (RadioButton) radioGroup.findViewById(i);
                shootMode = checkedRadio.getText().toString();
            }
        });

        displayOptions.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton checkedRadio = (RadioButton) radioGroup.findViewById(i);
                displayMode = checkedRadio.getText().toString();
                if (!changePlayerColor.isAlive()) changePlayerColor.start();
            }
        });

        angleOffsetBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                // i = [0, 36] -> [-36, 0] -> [36, 0] -> [0.5, 0] -> [0.75, 0.25]
                // baseAngleOffset = [0.75, 0.25]
                // baseAngleOffsetLabel = [-90, +90]
                baseAngleOffset = (-(i - 36)) / 76F + 0.25F;
                angleOffsetLabel.setText(getString(R.string.angle_offset_label) + " " + (i - 18) * 5 + "°");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        Thread shoot = new Thread(new Runnable() {
            @Override
            public void run() {
                Random r = new Random();
                boolean rightDirectionTrav = true;
                float secondaryAngleOffset;
                while (!Thread.interrupted() && running) {
                    secondaryAngleOffset = 0.0F;
                    //switch case has some problem here
                    while ((shootMode.equals(shootModes[3]) || shootMode.equals(shootModes[4])) && running) {
                        //Front & Back scan
                        secondaryAngleOffset = rightDirectionTrav ? secondaryAngleOffset + 0.01F : secondaryAngleOffset - 0.01F;
                        if (secondaryAngleOffset > 0.05F) rightDirectionTrav = false;
                        if (secondaryAngleOffset < -0.05F) rightDirectionTrav = true;
                        if (shootMode.equals(shootModes[4])){
                            GameMessageManager.sendMessage("GunTrav=" + ((secondaryAngleOffset +  + 1.0F) % 1.0F));
                        } else {
                            GameMessageManager.sendMessage("GunTrav=" + ((secondaryAngleOffset + baseAngleOffset) % 1.0F));
                        }
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    while (shootMode.equals(shootModes[0]) && running) {
                        //Defense mode
                        secondaryAngleOffset += 0.01;
                        GameMessageManager.sendMessage("GunTrav=" + secondaryAngleOffset);
                        if (secondaryAngleOffset >= 1.0F) secondaryAngleOffset = 0.0F;
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    while (shootMode.equals(shootModes[1]) && running) {
                        //Spread mode
                        secondaryAngleOffset = (r.nextInt(1000)-500) / 10000F;
                        GameMessageManager.sendMessage("GunTrav=" + (secondaryAngleOffset + baseAngleOffset));
                        try {
                            Thread.sleep(30);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    while (shootMode.equals(shootModes[2]) && running) {
                        //Normal mode
                        GameMessageManager.sendMessage("GunTrav=" + baseAngleOffset);
                        try {
                            Thread.sleep(30);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        shoot.start();

        exitButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                GameMessageManager.sendMessage("EXIT");
                            }
                        }).start();
                        sendLiveMsg.interrupt();
                        shoot.interrupt();
                        running = false;
                        finish();
                    }
                }
        );
    }

}