package daniel.photogallery;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by daniel on 7/18/16.
 */
public abstract class VisibleFragment extends Fragment {
    private static final String TAG = "VisibleFragment";

    /**
     * Starts the broadcast service
     */
    @Override
    public void onStart(){
        super.onStart();
        IntentFilter filter = new IntentFilter(PollService.SHOW_ACTION_NOTIFICATION);
        getActivity().registerReceiver(mOnShowNotification, filter,PollService.PERM_PRIVATE,null);
    }

    /**
     * Stops the broadcast service
     */
    @Override
    public void onStop(){
        super.onStop();
        getActivity().unregisterReceiver(mOnShowNotification);
    }

    /**
     * This is the reciever
     */
    private BroadcastReceiver mOnShowNotification = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "canceling notification");
            setResultCode(Activity.RESULT_CANCELED);
        }
    };


}
