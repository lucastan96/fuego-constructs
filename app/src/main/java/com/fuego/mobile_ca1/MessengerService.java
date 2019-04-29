package com.fuego.mobile_ca1;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.widget.Toast;

import com.fuego.mobile_ca1.Classes.Event;
import com.google.firebase.firestore.FirebaseFirestore;

public class MessengerService extends Service {
    public static final int MSG_SAY_HELLO = 1;

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_SAY_HELLO) {
                Event event = (Event) msg.obj;
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("events").add(event);
                Toast.makeText(getApplicationContext(), "Hello!", Toast.LENGTH_SHORT).show();
            } else {
                super.handleMessage(msg);
            }
        }
    }

    final Messenger mMessenger = new Messenger(new IncomingHandler());

    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(getApplicationContext(), "Binding", Toast.LENGTH_SHORT).show();
        return mMessenger.getBinder();
    }
}
