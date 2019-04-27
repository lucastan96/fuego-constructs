package com.fuego.mobile_ca1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;


public class ConstructsBroadcastReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        String intentAction = intent.getAction();

        if (intentAction != null) {

            switch (intentAction){
                case Intent.ACTION_BOOT_COMPLETED:
                    Toast.makeText(context, "BOOT COMPLETED", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
