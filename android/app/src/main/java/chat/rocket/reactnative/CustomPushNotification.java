package chat.rocket.reactnative;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings.System;
import android.media.RingtoneManager;

import android.util.Log;
import java.util.Random;
import com.google.gson.*;
import androidx.core.app.NotificationCompat;

import com.wix.reactnativenotifications.core.AppLaunchHelper;
import com.wix.reactnativenotifications.core.AppLifecycleFacade;
import com.wix.reactnativenotifications.core.JsIOHelper;
import com.wix.reactnativenotifications.core.notification.PushNotification;

public class CustomPushNotification extends PushNotification {
    public CustomPushNotification(Context context, Bundle bundle, AppLifecycleFacade appLifecycleFacade, AppLaunchHelper appLaunchHelper, JsIOHelper jsIoHelper) {
        super(context, bundle, appLifecycleFacade, appLaunchHelper, jsIoHelper);
    }

    private int smallIconResId = mContext.getResources().getIdentifier("ic_notification", "mipmap", mContext.getPackageName());
    private NotificationManager notificationManager;
    private NotificationCompat.Builder groupBuilder;
    private String notificationId;
    private String title;
    private String message;
    private String group;
    private Ejson ejson;

    @Override
    protected Notification buildNotification(PendingIntent intent) {
        Bundle bundle = mNotificationProps.asBundle();

        this.title = bundle.getString("title");
        this.message = bundle.getString("message");
        this.notificationId = bundle.getString("notId");
        this.notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        Gson gson = new Gson();
        this.ejson = gson.fromJson(bundle.getString("ejson", "{}"), Ejson.class);
        this.group = this.ejson.host + this.ejson.rid;

        this.groupBuilder = new NotificationCompat.Builder(mContext, this.ejson.host)
            .setContentTitle(this.title)
            .setContentText(this.message)
            .setSmallIcon(this.smallIconResId)
            // .setStyle(NotificationCompat.InboxStyle().setSummaryText(title))
            .setContentIntent(intent)
            .setGroup(this.group)
            .setGroupSummary(true);

        return getNotificationBuilder(intent).build();
    }

    @Override
    protected Notification.Builder getNotificationBuilder(PendingIntent intent) {
        final Notification.Builder notification = new Notification.Builder(mContext)
            .setContentTitle(this.title)
            .setContentText(this.message)
            .setSmallIcon(this.smallIconResId)
            .setContentIntent(intent)
            .setGroup(this.group);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            notification.setColor(mContext.getColor(R.color.notification_text));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(this.ejson.host, this.ejson.host, NotificationManager.IMPORTANCE_DEFAULT);
            this.notificationManager.createNotificationChannel(channel);

            notification.setChannelId(this.ejson.host);
        }

        return notification;
    }

    @Override
    protected void postNotification(int id, Notification notification) {
        this.notificationManager.notify(new Random().nextInt(), notification);
        this.notificationManager.notify(Integer.parseInt(notificationId), groupBuilder.build());
    }
}
