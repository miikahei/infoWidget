package layout;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import com.example.miika.myapplication.R;

import java.net.URISyntaxException;
import java.util.Random;

/**
 * Implementation of App Widget functionality.
 */
public class MyAppWidgetActivity extends AppWidgetProvider {

    private Socket socket;
    {
        try {
            socket = IO.socket("http://ec2-52-209-142-0.eu-west-1.compute.amazonaws.com:3000/");
        } catch (URISyntaxException e) {}
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        Log.w("miika", "updateAppWidget");

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.my_app_widget_activity);
        views.setTextViewText(R.id.appwidget_text, widgetText);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        Log.w("miika", "xonUpdate");

        final int count = appWidgetIds.length;

        for (int i = 0; i < count; i++) {
            int widgetId = appWidgetIds[i];
            String number = String.format("%03d", (new Random().nextInt(900) + 100));

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.my_app_widget_activity);
            remoteViews.setTextViewText(R.id.appwidget_text, number);

            Intent intent = new Intent(context, MyAppWidgetActivity.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.button, pendingIntent);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }

    @Override
    public void onEnabled(Context context) {
        Log.w("miika", "xonEnabled");
        // Enter relevant functionality for when the first widget is created

        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
              //  socket.emit("foo", "hi");
               // socket.disconnect();
            }

        }).on("chat message", new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                Log.w("miika", "xon chat message!!");
            }

        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {}

        });
        socket.connect();
    }

    @Override
    public void onDisabled(Context context) {
        Log.w("miika", "xonDisabled");
        socket.close();
        // Enter relevant functionality for when the last widget is disabled
    }
}

