package daniel.photogallery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by daniel on 7/18/16. This class is a broadcast reciever for PhotoGallery.It listens for BOOT_COMPLETED
 */
public class StartupReciever extends BroadcastReceiver{
    private static final String TAG = "StartupReciever";

    /**
     * This is where intents will be recieved. It will be looking for BOOT_COMPLETED
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent){
        Log.i(TAG, "onRecieve: Recieved broadcast intent "+intent.getAction());

        boolean isOn = QueryPreferences.isAlarmOn(context);
        PollService.setServiceAlarm(context,isOn);
    }
}
