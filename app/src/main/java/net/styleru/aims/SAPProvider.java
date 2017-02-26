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

import org.json.JSONObject;

import ru.aimsproject.connectionwithbackend.RequestMethods;
import ru.aimsproject.data.DataStorage;

import static android.R.id.message;

/**
 * Created by LeonidL on 19.02.17.
 */

public class SAPProvider extends SAAgent{

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
        Toast.makeText(getBaseContext(), "Протокол SAP подключен", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getBaseContext(), "Запрос на соединение принят", Toast.LENGTH_SHORT).show();
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
        Toast.makeText(getBaseContext(), "Ошибка подключения", Toast.LENGTH_SHORT).show();
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

            final String reqMessage = new String(data);
            if(reqMessage.equals("request_profile") || reqMessage.equals("request_friends") || reqMessage.equals("request_news")) {
                new Thread(new Runnable() {
                    public void run() {
                        if (reqMessage.equals("request_profile")) {

                            try {
                                String json;
                                synchronized (DataStorage.getToken()) {
                                    json = RequestMethods.getProfile();
                                }
                                JSONObject js = new JSONObject(json);
                                js.put("TypeOfRequest", "request_profile");
                                sendJSONInfo(js.toString());
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (reqMessage.equals("request_friends")) {
                            try {
                                String json;
                                synchronized (DataStorage.getToken()) {
                                    json = RequestMethods.getFriends(DataStorage.getMe());
                                }
                                JSONObject js = new JSONObject(json);
                                js.put("TypeOfRequest", "request_friends");
                                sendJSONInfo(js.toString());
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (reqMessage.equals("request_news")) {
                            try {
                                String json;
                                synchronized (DataStorage.getToken()) {
                                    json = RequestMethods.getNews(new Date(115, 12, 20));
                                }
                                JSONObject js = new JSONObject(json);
                                js.put("TypeOfRequest", "request_news");
                                sendJSONInfo(js.toString());
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
                Toast.makeText(getBaseContext(), "Данные отправлены", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getBaseContext(), "Соединение потеряно", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
