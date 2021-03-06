package com.settingsmanager;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.settingsmanager.constants.IntentExtras;

import java.util.List;

public class WifiSettingsManagerActivity extends AppCompatActivity {
    Bundle extras;
    String tag = WifiSettingsManagerActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        extras = getIntent().getExtras();
        if (extras != null) {
            changeSettings();
        }
        finish();
    }

    private void changeSettings() {
        if (extras.containsKey(IntentExtras.DISABLE_WIFI)) {
            setWifiEnabledState(false);
        }
        if (extras.containsKey(IntentExtras.ENABLE_WIFI)) {
            setWifiEnabledState(true);
        }
        if (extras.containsKey(IntentExtras.CONNECT) && extras.containsKey(IntentExtras.SSID)) {
            connectToNetwork();
        }
        if (extras.containsKey(IntentExtras.NEW_CONNECTION)) {
            if (extras.containsKey(IntentExtras.SSID) && extras.containsKey(IntentExtras.PASSWORD)) {
                connectToNewNetwork();
            }
        }
    }

    private void setWifiEnabledState(boolean wifiEnabledState) {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(wifiEnabledState);
    }

    private void connectToNewNetwork() {
        String networkSSID = extras.getString(IntentExtras.SSID);
        String networkPassword = extras.getString(IntentExtras.PASSWORD);

        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        wifiConfiguration.SSID = String.format("\"%s\"", networkSSID);
        wifiConfiguration.preSharedKey = String.format("\"%s\"", networkPassword);

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.addNetwork(wifiConfiguration);
        connectToNetwork();
    }

    private void connectToNetwork() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        String networkSSID = extras.getString(IntentExtras.SSID);
        if (wifiManager.isWifiEnabled()) {
            List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
            Log.d(tag, "Configured network ids = " + configuredNetworks);

            for (WifiConfiguration configuredNetwork : configuredNetworks) {
                if (configuredNetwork.SSID != null && configuredNetwork.SSID.equals(String.format("\"%s\"", networkSSID))) {
                    Log.d(tag, "Connecting to network with networkSSID = " + networkSSID);
                    wifiManager.disconnect();
                    wifiManager.enableNetwork(configuredNetwork.networkId, true);
                    wifiManager.reconnect();
                    break;
                }
            }
        }
    }
}
