package daniel.photogallery;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.util.List;

/**
 * Created by daniel with assistance from Big Nerd Ranch on 7/15/16.
 */
public class PollService extends android.app.IntentService {

    private static final String TAG = "PollService";
    private static final long POLL_INTERVAL = 60*1000;

    public static final String SHOW_ACTION_NOTIFICATION = "daniel.photogallery.SHOW_NOTIFICATION";
    public static final String PERM_PRIVATE ="daniel.photogallery.PRIVATE";

    public static final String REQUEST_CODE = "REQUEST_CODE";
    public static final String NOTIFICATION = "NOTIFICATION";

    public static Intent newIntent(Context context){
        return new Intent(context,PollService.class);
    }

    public static void setServiceAlarm(Context context, boolean isOn){
        Intent i = new PollService().newIntent(context);
        PendingIntent pi = PendingIntent.getService(context,0,i,0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if(isOn){
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(),POLL_INTERVAL,pi);
        }else {
            alarmManager.cancel(pi);
            pi.cancel();
        }

        QueryPreferences.setAlarmOn(context,isOn);
    }

    /**
     * Constructor
     */
    public PollService(){
        super(TAG);
    }

    /**
     * Handles the intent to poll images
     * @param intent
     */
    @Override
    protected void onHandleIntent(Intent intent){
        if(!networkIsAvailableAndConnected()){
            return;
        }
        Log.i(TAG, "onHandleIntent: Recieved an intent" +intent);
        //Get the query string
        String query = QueryPreferences.getStoredQuery(this);
        // Get the last known id
        String lastResultId = QueryPreferences.getLastResultId(this);
        List<GalleryItem> items;


        //If the query is null, get recent photos. Otherwise search the photos with the query
        if(query == null){
            items = new FlickrFetchr().fetchRecentPhotos();
        }else{
            items = new FlickrFetchr().searchPhotos(query);
        }

        if(items.size()==0){
            return;
        }

        String resultId = items.get(0).getId();

        if(resultId.equals(lastResultId)){
            Log.i(TAG, "onHandleIntent: Got old result "+resultId);
        }else {
            Resources resources = getResources();
            Intent i = PhotoGalleryActivity.newIntent(this);
            PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);
            Notification notification = new NotificationCompat.Builder(this)
                    .setTicker(resources.getString(R.string.new_pictures_title))
                    .setSmallIcon(android.R.drawable.ic_menu_report_image)
                    .setContentTitle(resources.getString(R.string.new_pictures_title))
                    .setContentText(resources.getString(R.string.new_pictures_text))
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .build();

            showBackgroundNotification(0, notification);
            Log.i(TAG, "onHandleIntent: Got new result"+resultId);

        }


        QueryPreferences.setPrefLastResultId(this,resultId);


    }

    /**
     * Checks to see if a safe connection can be established.Will return true if available, and false if not,
     * such as if the user has turned on a power saving mode so that background services do not run
     * @return whether the network is available and connected
     */
    private boolean networkIsAvailableAndConnected(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;

        // Refactored from source to allow for lazy evaluation

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    /**
     * returns whether the network service alarm is on or not
     * @param context
     * @return
     */
    public static boolean isServiceAlarmOn(Context context) {
        Intent i = PollService.newIntent(context);
        PendingIntent pi = PendingIntent
                .getService(context, 0, i, PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }

    private void showBackgroundNotification(int requestCode, Notification notification) {
        Intent i = new Intent(SHOW_ACTION_NOTIFICATION);
        i.putExtra(REQUEST_CODE, requestCode);
        i.putExtra(NOTIFICATION, notification);
        sendOrderedBroadcast(i, PERM_PRIVATE, null, null,
                Activity.RESULT_OK, null, null);
    }
}
