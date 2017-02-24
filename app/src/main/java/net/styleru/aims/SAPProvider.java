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
import java.util.Date;
import java.util.HashMap;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.os.Handler;

import com.samsung.android.sdk.accessory.*;

import ru.aimsproject.connectionwithbackend.RequestMethods;
import ru.aimsproject.data.DataStorage;

import static android.R.id.message;

/**
 * Created by LeonidL on 19.02.17.
 */

public class SAPProvider extends SAAgent{

//    public final static  String TAG = "SAPServiceProvider";
//
//    public final static int SAP_SERVICE_CHANNEL_ID = 124;
//
//    private final IBinder mIBinder = new LocalBinder();
//
//    HashMap<Integer, SAPProviderConnection> connectionMap = null;
//
//    public SAPProvider() {
//        super(TAG, SAPProviderConnection.class);
//    }
//
//    @Override
//    protected void onServiceConnectionResponse(SASocket thisConnection, int result) {
//        if(result == CONNECTION_SUCCESS) {
//            if(thisConnection != null) {
//                SAPProviderConnection myConnection = (SAPProviderConnection) thisConnection;
//
//                if(connectionMap != null) {
//                    connectionMap = new HashMap<>();
//                }
//
//                myConnection.connectionID = (int) (System.currentTimeMillis() & 255);
//
//                Log.d(TAG, "onSecriceConnection connectionID= " + myConnection.connectionID);
//
//                connectionMap.put(myConnection.connectionID, myConnection);
//
//                Toast.makeText(getBaseContext(), "CONNECTION ESTABLISHED", Toast.LENGTH_LONG).show();
//            } else {
//                Log.e(TAG, "SASocket object is null");
//            }
//        } else if(result == CONNECTION_ALREADY_EXIST) {
//            Log.e(TAG, "onServiceConnectionResponse, CONNECTION_ALREADY_EXIST");
//        } else {
//            Log.e(TAG, "onServiceConnectionResponse result error =" + result);
//        }
//    }
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return mIBinder;
//    }
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//
//        SA mAccessory = new SA();
//        try{
//            mAccessory.initialize(this);
//            Log.d("SAP PROVIDER", "ON CREATE TRY BLOCK");
//        } catch (SsdkUnsupportedException e) {
//            Log.d("SAP PROVIDER", "ON CREATE TRY BLOCK ERROR UNSUPPORTED SDK");
//        } catch (Exception ex) {
//            Log.e(TAG, "Cannot initialize Accessory package.");
//            ex.printStackTrace();
//            stopSelf();
//        }
//    }
//
//    @Override
//    protected void onServiceConnectionRequested(SAPeerAgent peerAgent) {
//        if (peerAgent != null) {
//            Toast.makeText(getBaseContext(), "Connection", Toast.LENGTH_SHORT).show();
//            acceptServiceConnectionRequest(peerAgent);
//        }
//    }
//
//    public  String getDeviceInfo() {
//        String manufacture = Build.MANUFACTURER;
//        String model = Build.MODEL;
//
//        return  manufacture + " " + model;
//    }
//
//    public class LocalBinder extends Binder {
//        public SAPProvider getService() {
//            return SAPProvider.this;
//        }
//    }
//
//    public class SAPProviderConnection extends SASocket{
//
//        private int connectionID;
//
//        protected SAPProviderConnection() {
//            super(SAPProviderConnection.class.getName());
//        }
//
//        @Override
//        public void onError(int channelID, String errorString, int error) {
//            Log.e(TAG, "ERROR: " + errorString + "|" + error);
//        }
//
//        @Override
//        public void onReceive(int channelID, byte[] data) {
//            final  String message;
//            Time time = new Time();
//
//            time.set(System.currentTimeMillis());
//
//            String timeStr = " " + String.valueOf(time.minute) + ":" + String.valueOf(time.second);
//            String strToUpdateUI = new String(data);
//
//            message = getDeviceInfo() + strToUpdateUI.concat(timeStr);
//
//            Log.d("SAP MESSAGE", message);
//
//            final SAPProviderConnection uHandler = connectionMap.get(Integer.parseInt(String.valueOf(connectionID)));
//
//            if(uHandler == null) {
//                Log.e(TAG, "Error, can not get SAPProviderConnection handler");
//                return;
//            }
//
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        uHandler.send(SAP_SERVICE_CHANNEL_ID, message.getBytes());
//                    } catch (IOException ex) {
//                        ex.printStackTrace();
//                    }
//                }
//            });
//
//        }
//
//        @Override
//        protected void onServiceConnectionLost(int i) {
//            if(connectionMap != null) {
//                connectionMap.remove(connectionID);
//            }
//        }
//    }

    private static final String TAG = "HelloAccessory(P)";
    private static final Class<ServiceConnection> SASOCKET_CLASS = ServiceConnection.class;
    private final IBinder mBinder = new LocalBinder();
    private ServiceConnection mConnectionHandler = null;
    Handler mHandler = new Handler();

    public SAPProvider() {
        super(TAG, SASOCKET_CLASS);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(getBaseContext(), "Connect to App", Toast.LENGTH_SHORT).show();
        SA mAccessory = new SA();
        try {
            mAccessory.initialize(this);
            //findPeerAgents();
        } catch (SsdkUnsupportedException e) {
            // try to handle SsdkUnsupportedException
            if (processUnsupportedException(e)) {
                return;
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            /*
             * Your application can not use Samsung Accessory SDK. Your application should work smoothly
             * without using this SDK, or you may want to notify user and close your application gracefully
             * (release resources, stop Service threads, close UI thread, etc.)
             */
            stopSelf();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    protected void onFindPeerAgentsResponse(SAPeerAgent[] peerAgents, int result) {
        Log.d(TAG, "onFindPeerAgentResponse : result =" + result);
    }

    @Override
    protected void onServiceConnectionRequested(SAPeerAgent peerAgent) {
        if (peerAgent != null) {
            Toast.makeText(getBaseContext(), "Connection", Toast.LENGTH_SHORT).show();
            acceptServiceConnectionRequest(peerAgent);
        }
    }

    @Override
    protected void onServiceConnectionResponse(SAPeerAgent peerAgent, SASocket socket, int result) {
        if (result == SAAgent.CONNECTION_SUCCESS) {
            if (socket != null) {
                mConnectionHandler = (ServiceConnection) socket;
            }
        } else if (result == SAAgent.CONNECTION_ALREADY_EXIST) {
            Log.e(TAG, "onServiceConnectionResponse, CONNECTION_ALREADY_EXIST");
        } else {
            Log.e(TAG, "onServiceConnectionResponse: HZ");
        }
    }

    @Override
    protected void onAuthenticationResponse(SAPeerAgent peerAgent, SAAuthenticationToken authToken, int error) {
        /*
         * The authenticatePeerAgent(peerAgent) API may not be working properly depending on the firmware
         * version of accessory device. Please refer to another sample application for Security.
         */
    }

    @Override
    protected void onError(SAPeerAgent peerAgent, String errorMessage, int errorCode) {
        super.onError(peerAgent, errorMessage, errorCode);
    }

    private boolean processUnsupportedException(SsdkUnsupportedException e) {
        e.printStackTrace();
        int errType = e.getType();
        if (errType == SsdkUnsupportedException.VENDOR_NOT_SUPPORTED
                || errType == SsdkUnsupportedException.DEVICE_NOT_SUPPORTED) {
            /*
             * Your application can not use Samsung Accessory SDK. You application should work smoothly
             * without using this SDK, or you may want to notify user and close your app gracefully (release
             * resources, stop Service threads, close UI thread, etc.)
             */
            stopSelf();
        } else if (errType == SsdkUnsupportedException.LIBRARY_NOT_INSTALLED) {
            Log.e(TAG, "You need to install Samsung Accessory SDK to use this application.");
        } else if (errType == SsdkUnsupportedException.LIBRARY_UPDATE_IS_REQUIRED) {
            Log.e(TAG, "You need to update Samsung Accessory SDK to use this application.");
        } else if (errType == SsdkUnsupportedException.LIBRARY_UPDATE_IS_RECOMMENDED) {
            Log.e(TAG, "We recommend that you update your Samsung Accessory SDK before using this application.");
            return false;
        }
        return true;
    }

    public class LocalBinder extends Binder {
        public SAPProvider getService() {
            return SAPProvider.this;
        }
    }

    public class ServiceConnection extends SASocket {
        public ServiceConnection() {
            super(ServiceConnection.class.getName());
        }

        @Override
        public void onError(int channelId, String errorMessage, int errorCode) {
        }

        @Override
        public void onReceive(int channelId, byte[] data) {
            if (mConnectionHandler == null) {
                Log.e(TAG, "Lost connection !!");
                return;
            }
//            Calendar calendar = new GregorianCalendar();
//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd aa hh:mm:ss.SSS");
//            String timeStr = " " + dateFormat.format(calendar.getTime());
//            String strToUpdateUI = new String(data);
//            final String message = strToUpdateUI.concat(timeStr);

            String reqMessage = new String(data);
            if (reqMessage.equals("request_profile")) {
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            String json = RequestMethods.getProfile();
                            sendJSONInfo(json);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }

            if (reqMessage.equals("request_friends")) {
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            String json = RequestMethods.getFriends(DataStorage.getMe());
                            sendJSONInfo(json);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }

            if (reqMessage.equals("request_news")) {
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            String json = RequestMethods.getNews(new Date(115, 12, 20));
                            sendJSONInfo(json);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            }
        }

        private void sendJSONInfo(final String json) throws IOException {
            mConnectionHandler.send(getServiceChannelId(0), json.getBytes());
        }

        @Override
        protected void onServiceConnectionLost(int reason) {
            mConnectionHandler = null;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getBaseContext(), "Terminate", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
