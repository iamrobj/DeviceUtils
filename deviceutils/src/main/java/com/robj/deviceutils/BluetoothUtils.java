package com.robj.deviceutils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import io.reactivex.Observable;

/**
 * Created by Rob J on 15/06/17.
 */

public class BluetoothUtils {

    @SuppressLint("MissingPermission")
    public static Observable<List<Device>> getPairedDevices(Context context) {
        return Observable.create(subscriber -> {
            if(PermissionsUtil.hasPermission(context, Manifest.permission.BLUETOOTH)) {
                BluetoothAdapter mBluetoothAdapter = getDefaultAdapter(context);
                if(mBluetoothAdapter == null) {
                    subscriber.onError(new RuntimeException("Bluetooth is not supported on this device"));
                }
                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                List<Device> devices = new ArrayList();
                if (pairedDevices.size() > 0)
                    for (BluetoothDevice bt : pairedDevices) {
                        Device device = new Device(bt);
                        devices.add(device);
                    }
                subscriber.onNext(devices);
            } else
                subscriber.onError(new RuntimeException("Bluetooth permission is missing"));
        });
    }

    private static BluetoothAdapter getDefaultAdapter(Context context) {
        BluetoothAdapter bluetoothAdapter;
        if(Build.VERSION.SDK_INT >= 18) {
            BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            bluetoothAdapter = bluetoothManager.getAdapter();
        } else
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter == null)
            Log.d(BluetoothUtils.class.getSimpleName(), "Bluetooth is not supported on this device, no adapter found..");
        return bluetoothAdapter;
    }

    @SuppressLint("MissingPermission")
    public static String getDeviceAliasName(Context context, String btAddress) {
        BluetoothAdapter mBluetoothAdapter = getDefaultAdapter(context);
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(btAddress);
        String deviceAlias = null;
        if(device != null)
            try {
                Method method = device.getClass().getMethod("getAliasName");
                if(method != null)
                    deviceAlias = (String) method.invoke(device);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        if(TextUtils.isEmpty(deviceAlias))
            return device.getName();
        else
            return deviceAlias;
    }

    public static boolean isBluetoothSupported(Context context) {
        return getDefaultAdapter(context) != null;
    }

    public static boolean isBluetoothAvailable(Context context) {
        final BluetoothAdapter bluetoothAdapter = getDefaultAdapter(context);
        return (bluetoothAdapter != null && bluetoothAdapter.isEnabled());
    }

    public static void getConnectedBluetoothDevice(Context context, OnBluetoothConnection onBluetoothConnection) {
        getConnectedBluetoothDevice(context, onBluetoothConnection, BluetoothProfile.A2DP);
    }

    private static void getConnectedBluetoothDevice(Context context, final OnBluetoothConnection onBluetoothConnection, int profileType) {
        if (isBluetoothAvailable(context)) {
            BluetoothProfile.ServiceListener mProfileListener = new BluetoothProfile.ServiceListener() {
                public void onServiceConnected(int profileType_, BluetoothProfile proxy) {
                    if (profileType_ == profileType) {
                        List<BluetoothDevice> connectedDevices = proxy.getConnectedDevices();
                        for (BluetoothDevice device : connectedDevices) {
                            if(device == null || device.getAddress() == null)
                                continue;
                            onBluetoothConnection.onConnected(device);
                            return;
                        }
                        if (profileType != BluetoothProfile.HEADSET)
                            getConnectedBluetoothDevice(context, onBluetoothConnection, BluetoothProfile.HEADSET);
                        else
                            onBluetoothConnection.onNoConnection();
                        getDefaultAdapter(context).closeProfileProxy(profileType, proxy);
                    }
                }

                public void onServiceDisconnected(int profile) {
                    onBluetoothConnection.onNoConnection();
                }
            };
            BluetoothAdapter.getDefaultAdapter().getProfileProxy(context, mProfileListener, profileType);
        } else
            onBluetoothConnection.onNoConnection();
    }

    public static Observable<Optional<BluetoothDevice>> getConnectedBluetoothDevice(Context context) {
        return Observable.create(subscriber -> {
            getConnectedBluetoothDevice(context, new OnBluetoothConnection() {
                @Override
                public void onConnected(BluetoothDevice device) {
                    if(!subscriber.isDisposed())
                        subscriber.onNext(new Optional(device));
                }
                @Override
                public void onNoConnection() {
                    if(!subscriber.isDisposed())
                        subscriber.onNext(new Optional(null));
                }
            });
        });
    }

    public static class Device {

        private final String uniqueIdentifier;
        private final String name;

        public Device(BluetoothDevice bt) {
            uniqueIdentifier = bt.getAddress();
            name = bt.getName();
        }

        public Device(WifiConfiguration wifiConfiguration) {
            uniqueIdentifier = String.valueOf(wifiConfiguration.networkId);
            name = wifiConfiguration.SSID;
        }

        public String getUniqueIdentifier() {
            return uniqueIdentifier;
        }

        public String getName() {
            return name;
        }

    }

    public interface OnBluetoothConnection {
        void onConnected(BluetoothDevice profile);
        void onNoConnection();
    }

    public static class NoBluetoothConnectedException extends Throwable { }

}
