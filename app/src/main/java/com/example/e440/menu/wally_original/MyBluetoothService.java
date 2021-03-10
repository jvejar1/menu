package com.example.e440.menu.wally_original;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.e440.menu.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MyBluetoothService<Contex> {
    private static final String TAG = "MY_BLUETOOTH_SERVICE";
    private Handler handler; // handler that gets info from Bluetooth service
    private ConnectedThread connectedThread;
    private static MyBluetoothService instance;
    private Context mContext;

    // Defines several constants used when transmitting messages between the
    // service and the UI.
    private interface MessageConstants {
        public static final int MESSAGE_READ = 0;
        public static final int MESSAGE_WRITE_SUCEESS = 1;
        public static final int MESSAGE_WRITE_FAILED = 2;
        public static final int CONNECTED_THREAD = 3;
        public static final int MESSAGE_CONNECTED_SOCKET = 4;
        public static final int MESSAGE_DISCONNECTED_SOCKET=5;
        public static final int MESSAGE_WRITE_ACTION = 6;
        // ... (Add other message types here as needed.)
    }

    public static synchronized  MyBluetoothService GetInstance(Context context){

        if (instance == null){
            instance = new MyBluetoothService(context);
        }

        return instance;
    }
    private MyBluetoothService(Context context){

        mContext = context;
        handler = new Handler(Looper.getMainLooper()){

            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                ImageTask imageTask;
                switch (msg.what){
                    case MessageConstants.MESSAGE_CONNECTED_SOCKET:
                        Log.d(TAG, "CONNECTED SOCKET");

                        BluetoothSocket bluetoothSocket = (BluetoothSocket) msg.obj;
                        connectedThread = new ConnectedThread(bluetoothSocket);
                        connectedThread.start();
                        break;

                    case MessageConstants.MESSAGE_WRITE_ACTION:
                        imageTask = (ImageTask) msg.obj;
                        connectedThread.write(imageTask);
                        break;

                    case MessageConstants.MESSAGE_WRITE_SUCEESS:
                        Log.d(TAG, "Message written");
                        imageTask = (ImageTask) msg.obj;
                        imageTask.localView.setAlpha(1f);
                        break;

                    case MessageConstants.MESSAGE_WRITE_FAILED:
                        Log.d(TAG, "Write msg failed");
                        imageTask = (ImageTask) msg.obj;
                        imageTask.localView.setAlpha(0.2f);
                        break;
                    case MessageConstants.MESSAGE_READ:
                        Log.d(TAG, "Message readed");
                        //display the image in the view;
                        break;

                }
            }
        };
    }

    ArrayList<String> GetBondedDevicesNames(){

        ArrayList deviceList = new ArrayList() ;
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            Log.d(TAG, "Device does not support Bluetooth");
            return null;
        }
        Set<BluetoothDevice> all_devices = bluetoothAdapter.getBondedDevices();
        if (all_devices.size() > 0) {
            for (BluetoothDevice currentDevice : all_devices) {
                deviceList.add(currentDevice.getName());
            }
        }
        return deviceList;
    }

    ArrayList<String> GetBondedDevicesAddresses(){

        ArrayList deviceList = new ArrayList() ;
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            Log.d(TAG, "Device does not support Bluetooth");
            return null;
        }
        Set<BluetoothDevice> all_devices = bluetoothAdapter.getBondedDevices();
        if (all_devices.size() > 0) {
            for (BluetoothDevice currentDevice : all_devices) {
                deviceList.add(currentDevice.getAddress());
            }
        }
        return deviceList;
    }

    void connectToBondedDevice(int selectedIndex){

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            Log.d(TAG, "Device does not support Bluetooth");
            return;
        }
        Set<BluetoothDevice> all_devices = bluetoothAdapter.getBondedDevices();

        int index = 0;
        BluetoothDevice bluetoothDevice = null;

        for (BluetoothDevice currentDevice:all_devices ){
            if (index == selectedIndex ){
                bluetoothDevice = currentDevice;
                break;
            }
            index++;
        }

        if(bluetoothDevice == null){
            return;
        }

        ConnectThread connectThread = new ConnectThread(bluetoothDevice, null);
        connectThread.start();
    }


    void sendMessage(ImageTask imageTask){
        boolean messageWasWritten = false;

        if(connectedThread!=null){
            messageWasWritten = connectedThread.write(imageTask);
        }

        if (!messageWasWritten){
            try{connectedThread.cancel();}
            catch (NullPointerException e){
                Log.d(TAG, e.getMessage());
            }
            startAsClient(imageTask);
        }


    }


    public void startAsClient(ImageTask imageTask){

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            Log.d(TAG, "Device does not support Bluetooth");
            return;
        }
        BluetoothDevice bluetoothDevice;
        try{
            bluetoothDevice = bluetoothAdapter.getRemoteDevice(getPreferedDeviceAddress());
        }catch (IllegalArgumentException e){

            Log.d(TAG, "Invalid remote bluetooth address" ,e);
            return;
        }

        ConnectThread connectThread = new ConnectThread(bluetoothDevice, imageTask);
        connectThread.start();

    }

    public String getPreferedDeviceAddress(){

        SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager.getInstance(mContext);
        String preferedBtDeviceAddress = sharedPreferencesManager.getSlaveBluetoothDeviceAdress();
        return preferedBtDeviceAddress;
    };

    public void startAsServer(){

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            Log.d(TAG, "Device does not support Bluetooth");
        }

        AcceptThread acceptThread = new AcceptThread();
        acceptThread.start();


    }

    boolean isConnected(){
        if (this.connectedThread !=null){
            if(connectedThread.mmSocket.isConnected()){
                return true;
            }
        }

        return false;
    }

    void finish(){
        if (this.connectedThread !=null){
            connectedThread.cancel();
            connectedThread = null;
                return;
        }

        return;
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private byte[] mmBuffer; // mmBuffer store for the stream

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams; using temp objects because
            // member streams are final.
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating input stream", e);
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating output stream", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            mmBuffer = new byte[1024*1024];
            String message ="";
            int numBytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                try {
                    // Read from the InputStream.
                    numBytes = mmInStream.read(mmBuffer);


                    // Send the obtained bytes to the UI activity.

                } catch (IOException e) {
                    Log.d(TAG, "Input stream was disconnected", e);
                    break;
                }
            }
        }

        // Call this from the main activity to send data to the remote device.
        public boolean write(ImageTask imageTask) {
            try {

                byte[] b = (imageTask.getMessageToSend()+"#").getBytes();
                mmOutStream.write(b);

                // Share the sent message with the UI activity.
                Message writtenMsg = handler.obtainMessage(
                        MessageConstants.MESSAGE_WRITE_SUCEESS, -1, -1, imageTask);
                writtenMsg.sendToTarget();
                return true;
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when sending data", e);

                // Send a failure message back to the activity.
                Message writeErrorMsg =
                        handler.obtainMessage(MessageConstants.MESSAGE_WRITE_FAILED, imageTask);
                Bundle bundle = new Bundle();
                bundle.putString("toast",
                        "Couldn't send data to the other device");
                writeErrorMsg.setData(bundle);
                handler.sendMessage(writeErrorMsg);
                return false;
            }
        }

        // Call this method from the main activity to shut down the connection.
        public void cancel() {
            try {
                mmSocket.close();
                Log.e(TAG, "Socket successfully closed");

            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        private ImageTask imageTask;


        public ConnectThread(BluetoothDevice device, ImageTask imageTask) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            this.imageTask = imageTask;
            BluetoothSocket tmp = null;
            mmDevice = device;

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                UUID uuid = UUID.fromString("7d1ae8f9-e331-4aa0-82dd-1881c32d87d1");
                tmp = device.createRfcommSocketToServiceRecord(uuid);
            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            byte[] mmBuffer = new byte[1024];

            // Cancel discovery because it otherwise slows down the connection.
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            bluetoothAdapter.cancelDiscovery();

            if(mmSocket ==null){

                Log.e(TAG, "Null Socket");
                return;

            }
            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.

                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }
                return;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            manageMyConnectedSocket(mmSocket);
        }

        void manageMyConnectedSocket(BluetoothSocket mmSocket){
            Message writtenMsg = handler.obtainMessage(
                    MessageConstants.MESSAGE_CONNECTED_SOCKET, -1, -1, mmSocket);
            writtenMsg.sendToTarget();

            Message writtenMsgImageTask = handler.obtainMessage(
                    MessageConstants.MESSAGE_WRITE_ACTION, -1, -1, imageTask);
            writtenMsgImageTask.sendToTarget();
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();

            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
        }
    }

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            // Use a temporary object that is later assigned to mmServerSocket
            // because mmServerSocket is final.
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code.
                UUID uuid = UUID.fromString("7d1ae8f9-e331-4aa0-82dd-1881c32d87d1");
                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord("AppName", uuid);
            } catch (IOException e) {
                Log.e(TAG, "Socket's listen() method failed", e);
            }
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned.
            while (true) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.e(TAG, "Socket's accept() method failed", e);
                    break;
                }

                if (socket != null) {
                    // A connection was accepted. Perform work associated with
                    // the connection in a separate thread.
                    manageMyConnectedSocket(socket);
                    try{

                        mmServerSocket.close();

                    }catch (IOException e){

                        Log.d(TAG, "Cant close Bluetooth Server Socket");
                    }
                    break;
                }
            }
        }

        void manageMyConnectedSocket(BluetoothSocket socket){
            Message writtenMsg = handler.obtainMessage(
                    MessageConstants.MESSAGE_CONNECTED_SOCKET, -1, -1, socket);
            writtenMsg.sendToTarget();

        }

        // Closes the connect socket and causes the thread to finish.
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }}

}
