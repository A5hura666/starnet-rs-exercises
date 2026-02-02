package com.example.androidcontroller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity {
    TextView nameString, addressString;
    Spinner spinnerIP, spinnerName;
    ArrayList<String> ipList = new ArrayList<>();
    ArrayList<String> nameList = new ArrayList<>();
    SharedPreferences mainPrefs;
    int playerColor = Color.parseColor("#FFC800");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainPrefs = this.getPreferences(Context.MODE_PRIVATE);
        final Button connectionButton = (Button) findViewById(R.id.connectionButton);
        nameString = (TextView) findViewById(R.id.nameString);
        addressString = (TextView) findViewById(R.id.addressString);
        spinnerIP = (Spinner) findViewById(R.id.spinnerIP);
        spinnerName = (Spinner) findViewById(R.id.spinnerName);

        nameString.setEnabled(false);
        addressString.setEnabled(false);

        String savedIP = mainPrefs.getString("IPs", null);
        String savedName = mainPrefs.getString("Names", null);

        if (savedIP != null) ipList = new ArrayList<>(Arrays.asList(savedIP.split(",")));
        if (savedName != null) nameList = new ArrayList<>(Arrays.asList(savedName.split(",")));
        ipList.remove("");
        nameList.remove("");
        nameList.add(getString(R.string.other_selection_label));
        ipList.add(getString(R.string.other_selection_label));
        ArrayAdapter<CharSequence> nameAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, nameList.toArray(new String[0]));
        ArrayAdapter<CharSequence> ipAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, ipList.toArray(new String[0]));
        spinnerIP.setAdapter(ipAdapter);
        spinnerIP.setSelection(ipAdapter.getPosition(getString(R.string.other_selection_label)));
        spinnerName.setAdapter(nameAdapter);
        spinnerName.setSelection(nameAdapter.getPosition(getString(R.string.other_selection_label)));

        spinnerIP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getSelectedItem().toString();
                if (item.equals(getString(R.string.other_selection_label))) {
                    addressString.setEnabled(true);
                } else {
                    addressString.setEnabled(false);
                    addressString.setText(item);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getSelectedItem().toString();
                if (item.equals(getString(R.string.other_selection_label))) {
                    nameString.setEnabled(true);
                } else {
                    nameString.setEnabled(false);
                    nameString.setText(item);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        connectionButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        String name = nameString.getText().toString();
                        if (name.contains(",") || name.contains(";") || name.contains("#") || name.contains("=") || name.contains("'") || name.contains("\"")) {
                            Snackbar.make(findViewById(R.id.baseLayout), getString(R.string.error_name), 5000).show();
                            return;
                        }
                        String ip = addressString.getText().toString();
                        if (ip.contains(",") || ip.contains(";") || ip.contains("#") || ip.contains("=") || ip.contains("'") || ip.contains("\"")) {
                            Snackbar.make(findViewById(R.id.baseLayout), getString(R.string.error_ip), 5000).show();
                            return;
                        }
                        int connexionTries = 0;

                        GameMessageManager.connect(addressString.getText().toString());
                        while (!GameMessageManager.isConnected() && connexionTries < 20) {
                            connexionTries++;
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        if (GameMessageManager.isConnected()) {
                            startGameActivity(MainActivity.super.getCurrentFocus());
                        } else {
                            Snackbar.make(findViewById(R.id.baseLayout), getString(R.string.error_connexion), 3000).show();
                        }

                    }
                });
    }

    public void startGameActivity(View view) {
        String ipSelected = spinnerIP.getSelectedItem().toString();
        String newIP = addressString.getText().toString().trim();
        if (ipSelected.equals(getString(R.string.other_selection_label))) {
            if (!ipList.contains(newIP)) {
                ipList.add(newIP);
            }
        }
        String nameSelected = spinnerName.getSelectedItem().toString();
        String newName = nameString.getText().toString().trim();
        if (nameSelected.equals(getString(R.string.other_selection_label))) {
            if (!nameList.contains(newName)) {
                nameList.add(newName);
            }
        }
        SharedPreferences.Editor mainPrefEditor = mainPrefs.edit();
        ipList.remove(getString(R.string.other_selection_label));
        nameList.remove(getString(R.string.other_selection_label));
        mainPrefEditor.putString("IPs", ipList.toString().replace("[", "").replace("]", "").replace(" ", ""));
        mainPrefEditor.putString("Names", nameList.toString().replace("[", "").replace("]", "").replace(" ", ""));
        mainPrefEditor.apply();
        Intent intent = new Intent(this, JoystickActivity.class);
        intent.putExtra("playerName", newName);
        intent.putExtra("playerColor", Color.red(playerColor) + "=" + Color.green(playerColor) + "=" + Color.blue(playerColor));
        startActivity(intent);
    }
}