package net.styleru.aims;

import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.accessory.SA;
import com.samsung.android.sdk.accessory.SAAgent;
import com.samsung.android.sdk.accessory.SAPeerAgent;
import com.samsung.android.sdk.accessory.SASocket;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by LeonidL on 19.02.17.
 */

public class SAPProvider extends SAAgent{

    public final static  String TAG = "SAPServiceProvider";

    public final static int SAP_SERVICE_CHANNEL_ID = 124;

    private final IBinder mIBinder = new LocalBinder();

    HashMap<Integer, SAPProviderConnection> connectionMap = null;

    public SAPProvider() {
        super(TAG, SAPProviderConnection.class);
    }

    @Override
    protected void onServiceConnectionResponse(SASocket thisConnection, int result) {
        if(result == CONNECTION_SUCCESS) {
            if(thisConnection != null) {
                SAPProviderConnection myConnection = (SAPProviderConnection) thisConnection;

                if(connectionMap != null) {
                    connectionMap = new HashMap<>();
                }

                myConnection.connectionID = (int) (System.currentTimeMillis() & 255);

                Log.d(TAG, "onSecriceConnection connectionID= " + myConnection.connectionID);

                connectionMap.put(myConnection.connectionID, myConnection);

                Toast.makeText(getBaseContext(), "CONNECTION ESTABLISHED", Toast.LENGTH_LONG).show();
            } else {
                Log.e(TAG, "SASocket object is null");
            }
        } else if(result == CONNECTION_ALREADY_EXIST) {
            Log.e(TAG, "onServiceConnectionResponse, CONNECTION_ALREADY_EXIST");
        } else {
            Log.e(TAG, "onServiceConnectionResponse result error =" + result);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mIBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        SA mAccessory = new SA();
        try{
            mAccessory.initialize(this);
            Log.d("SAP PROVIDER", "ON CREATE TRY BLOCK");
        } catch (SsdkUnsupportedException e) {
            Log.d("SAP PROVIDER", "ON CREATE TRY BLOCK ERROR UNSUPPORTED SDK");
        } catch (Exception ex) {
            Log.e(TAG, "Cannot initialize Accessory package.");
            ex.printStackTrace();
            stopSelf();
        }
    }

    @Override
    protected void onServiceConnectionRequested(SAPeerAgent peerAgent) {
        if (peerAgent != null) {
            Toast.makeText(getBaseContext(), "Connection", Toast.LENGTH_SHORT).show();
            acceptServiceConnectionRequest(peerAgent);
        }
    }

    public  String getDeviceInfo() {
        String manufacture = Build.MANUFACTURER;
        String model = Build.MODEL;

        return  manufacture + " " + model;
    }

    public class LocalBinder extends Binder {
        public SAPProvider getService() {
            return SAPProvider.this;
        }
    }

    public class SAPProviderConnection extends SASocket{

        private int connectionID;

        protected SAPProviderConnection() {
            super(SAPProviderConnection.class.getName());
        }

        @Override
        public void onError(int channelID, String errorString, int error) {
            Log.e(TAG, "ERROR: " + errorString + "|" + error);
        }

        @Override
        public void onReceive(int channelID, byte[] data) {
            final  String message;
            Time time = new Time();

            time.set(System.currentTimeMillis());

            String timeStr = " " + String.valueOf(time.minute) + ":" + String.valueOf(time.second);
            String strToUpdateUI = new String(data);

            message = getDeviceInfo() + strToUpdateUI.concat(timeStr);

            Log.d("SAP MESSAGE", message);

            final SAPProviderConnection uHandler = connectionMap.get(Integer.parseInt(String.valueOf(connectionID)));

            if(uHandler == null) {
                Log.e(TAG, "Error, can not get SAPProviderConnection handler");
                return;
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        uHandler.send(SAP_SERVICE_CHANNEL_ID, message.getBytes());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });

        }

        @Override
        protected void onServiceConnectionLost(int i) {
            if(connectionMap != null) {
                connectionMap.remove(connectionID);
            }
        }
    }
}
