package info.accolade.fishing_master.utils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SmsDeliveredReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent arg1) {
        switch (getResultCode()) {
            case Activity.RESULT_OK:
//                Toast.makeText(context, "SMS delivered", Toast.LENGTH_SHORT).show();
                break;
            case Activity.RESULT_CANCELED:
//                Toast.makeText(context, "SMS not delivered", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}