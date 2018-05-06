package com.example.mixture.util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BluetoothConnector {
    private static final String TAG = "BluetoothConnector";
    private BluetoothSocketWrapper bluetoothSocket;
    private BluetoothDevice device;
    private boolean secure;
    private BluetoothAdapter adapter;
    private List<UUID> uuidCandidates;
    private int candidate;
    public static UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    /**
     * @param device         the device
     * @param secure         if connection should be done via a secure socket
     * @param adapter        the Android BT adapter
     * @param uuidCandidates a list of UUIDs. if null or empty, the Serial PP id is used
     */
    public BluetoothConnector(BluetoothDevice device, boolean secure,
                              BluetoothAdapter adapter, List<UUID> uuidCandidates) {
        this.device = device;
        this.secure = secure;
        this.adapter = adapter;
        this.uuidCandidates = uuidCandidates;
        if (this.uuidCandidates == null || this.uuidCandidates.isEmpty()) {
            this.uuidCandidates = new ArrayList<UUID>();
            this.uuidCandidates.add(uuid);
        }
    }

    public BluetoothSocketWrapper connect() throws IOException {
        Log.d(TAG, "begin connect");
        boolean success = false;
        while (selectSocket()) {
            adapter.cancelDiscovery();
            try {
                Log.d(TAG, "bluetoothSocket.connect");
                bluetoothSocket.connect();
                success = true;
                Log.d(TAG, "connect success");
                break;
            } catch (IOException e) {
                try {
                    bluetoothSocket = new FallbackBluetoothSocket(
                            bluetoothSocket.getUnderlyingSocket());
                    Thread.sleep(500);
                    bluetoothSocket.connect();
                    success = true;
                    Log.d(TAG, "retry connect success");
                    break;
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }

        if (!success) {
            throw new IOException("Could not connect to device: " + device.getAddress());
        }

        return bluetoothSocket;
    }

    private boolean selectSocket() throws IOException {
        if (candidate >= uuidCandidates.size()) {
            return false;
        }
        BluetoothSocket tmp;
        UUID uuid = uuidCandidates.get(candidate++);

        Log.d(TAG, "Attempting to connect to Protocol: " + uuid);
        if (secure) {
            tmp = device.createRfcommSocketToServiceRecord(uuid);
        } else {
            tmp = device.createInsecureRfcommSocketToServiceRecord(uuid);
        }
        bluetoothSocket = new NativeBluetoothSocket(tmp);
        return true;
    }

    public interface BluetoothSocketWrapper {
        InputStream getInputStream() throws IOException;
        OutputStream getOutputStream() throws IOException;
        void connect() throws IOException;
        void close() throws IOException;
        BluetoothSocket getUnderlyingSocket();
        BluetoothDevice getRemoteDevice();
    }

    public static class NativeBluetoothSocket implements BluetoothSocketWrapper {
        private BluetoothSocket socket;

        public NativeBluetoothSocket(BluetoothSocket tmp) {
            this.socket = tmp;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return socket.getInputStream();
        }

        @Override
        public OutputStream getOutputStream() throws IOException {
            return socket.getOutputStream();
        }

        @Override
        public void connect() throws IOException {
            socket.connect();
        }

        @Override
        public void close() throws IOException {
            socket.close();
        }

        @Override
        public BluetoothSocket getUnderlyingSocket() {
            return socket;
        }

        @Override
        public BluetoothDevice getRemoteDevice() {
            return socket.getRemoteDevice();
        }

    }

    public class FallbackBluetoothSocket extends NativeBluetoothSocket {
        private BluetoothSocket fallbackSocket;

        public FallbackBluetoothSocket(BluetoothSocket tmp)
                throws FallbackException {
            super(tmp);
            try {
                Class<?> clazz = tmp.getRemoteDevice().getClass();
                Class<?>[] paramTypes = new Class<?>[]{Integer.TYPE};
                Method m = clazz.getMethod("createRfcommSocket", paramTypes);
                Object[] params = new Object[]{1};
                fallbackSocket = (BluetoothSocket) m.invoke(tmp.getRemoteDevice(), params);
            } catch (Exception e) {
                throw new FallbackException(e);
            }
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return fallbackSocket.getInputStream();
        }

        @Override
        public OutputStream getOutputStream() throws IOException {
            return fallbackSocket.getOutputStream();
        }

        @Override
        public void connect() throws IOException {
            fallbackSocket.connect();
        }

        @Override
        public void close() throws IOException {
            fallbackSocket.close();
        }

    }

    public static class FallbackException extends Exception {
        private static final long serialVersionUID = 1L;

        public FallbackException(Exception e) {
            super(e);
        }
    }
}
