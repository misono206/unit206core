/*
 * Copyright 2020 Atelier Misono, Inc. @ https://misono.app/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.misono.unit206.misc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo; // deprecatedですが、Android 6.0バグ対応のため必要
import android.net.NetworkRequest;
import android.net.wifi.WifiManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.WorkerThread;

import app.misono.unit206.debug.Log2;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiresApi(21)
public class NetUtils {
	private static final String TAG = "NetUtil";

	private static final Set<Listener> mListeners = new HashSet<>();

	private static boolean isNetworkAvailable;
	private static boolean isWifiAvailable;
	private static boolean notWorkRequestNetwork;

	public interface Listener {
		@WorkerThread
		void onChangedNetwork(boolean available);
	}

	public static boolean isNetworkAvailable(@NonNull Context context) {
		if (23 <= Build.VERSION.SDK_INT && notWorkRequestNetwork) {
			isNetworkAvailable = isNetworkAvailable23(context);
		}
		return isNetworkAvailable;
	}

	public static boolean isWifiAvailable(@NonNull Context context) {
		if (23 <= Build.VERSION.SDK_INT && notWorkRequestNetwork) {
			isWifiAvailable = isWifiAvailable23(context);
		}
		return isWifiAvailable;
	}

	public static void initNetworkListener(@NonNull Context context) {
		synchronized(mListeners) {
			mListeners.clear();
		}
		ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (manager != null) {
			try {
				NetworkRequest request;

				notWorkRequestNetwork = false;

				// for default network
				request = new NetworkRequest.Builder().build();
				manager.requestNetwork(request, new ConnectivityManager.NetworkCallback() {
					@WorkerThread
					@Override
					public void onAvailable(@NonNull Network network) {
						isNetworkAvailable = true;
						log("NetworkCallback:onAvailable:" + network);
						synchronized(mListeners) {
							for (Listener listener : mListeners) {
								listener.onChangedNetwork(true);
							}
						}
					}

					@WorkerThread
					@Override
					public void onLost(@NonNull Network network) {
						isNetworkAvailable = false;
						log("NetworkCallback:onLost:" + network);
						synchronized(mListeners) {
							for (Listener listener : mListeners) {
								listener.onChangedNetwork(false);
							}
						}
					}
				});

				// for Wi-Fi
				request = new NetworkRequest.Builder().addTransportType(NetworkCapabilities.TRANSPORT_WIFI).build();
				manager.requestNetwork(request, new ConnectivityManager.NetworkCallback() {
					@WorkerThread
					@Override
					public void onAvailable(@NonNull Network network) {
						isWifiAvailable = true;
						log("WifiCallback:onAvailable:" + network);
					}

					@WorkerThread
					@Override
					public void onLost(@NonNull Network network) {
						isWifiAvailable = false;
						log("WifiCallback:onLost:" + network);
					}
				});
			} catch (SecurityException e) {
				//
				//	Android 6.0のバグ対応
				//	https://stackoverflow.com/questions/32185628/connectivitymanager-requestnetwork-in-android-6-0
				//
				notWorkRequestNetwork = true;
				log("DOES NOT WORK ConnectivityManager.requestNetwork() ...");
				e.printStackTrace();
			}
		} else {
			log("ConnectivityManager not supported...");
		}
	}

	public static void registerListener(@NonNull Listener listener) {
		synchronized(mListeners) {
			mListeners.add(listener);
		}
	}

	public static void unregisterListener(@Nullable Listener listener) {
		synchronized(mListeners) {
			mListeners.remove(listener);
		}
	}

	/**
	 * Android 6.0のバグ対応
	 * https://stackoverflow.com/questions/32185628/connectivitymanager-requestnetwork-in-android-6-0
	 *
	 * Require: android.permission.ACCESS_NETWORK_STATE
	 */
	@RequiresApi(23)
	private static boolean isNetworkAvailable23(@NonNull Context context) {
		boolean isAvailable = false;

		ConnectivityManager manager = context.getSystemService(ConnectivityManager.class);
		if (manager != null) {
			@SuppressLint("MissingPermission")
			NetworkInfo networkinfo = manager.getActiveNetworkInfo();
			if (networkinfo != null) {
				if (networkinfo.isAvailable() && networkinfo.isConnected()) {
					isAvailable = true;
				}
			}
		}
		return isAvailable;
	}

	/**
	 * Android 6.0のバグ対応.
	 * https://stackoverflow.com/questions/32185628/connectivitymanager-requestnetwork-in-android-6-0
	 *
	 * Require: android.permission.ACCESS_NETWORK_STATE
	 */
	@RequiresApi(23)
	private static boolean isWifiAvailable23(@NonNull Context context) {
		boolean available = false;

		WifiManager wifiManager = context.getSystemService(WifiManager.class);
		if (wifiManager != null) {
			if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
				ConnectivityManager connectivityManager = context.getSystemService(ConnectivityManager.class);
				if (connectivityManager != null) {
					@SuppressLint("MissingPermission")
					NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
					if (null != networkInfo) {
						if (networkInfo.isAvailable() && networkInfo.isConnected()) {
							int networkType = networkInfo.getType();
							if (networkType == ConnectivityManager.TYPE_WIFI) {
								available = true;
							}
						}
					}
				}
			}
		}
		return available;
	}

	@Nullable
	public static String getWifiIpAddress(boolean ipv4) {
		try {
			List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
			for (NetworkInterface intf : interfaces) {
				List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
				for (InetAddress addr : addrs) {
					if (!addr.isLoopbackAddress()) {
						String sAddr = addr.getHostAddress();
						if (sAddr != null) {
							boolean isIPv4 = sAddr.indexOf(':') < 0;
							if (ipv4) {
								if (isIPv4) {
									return sAddr;
								}
							} else {
								if (!isIPv4) {
									int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
									return delim < 0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
								}
							}
						}
					}
				}
			}
		} catch (Exception ignored) { } // for now eat exceptions
		return null;
	}

	@NonNull
	public static InetAddress getWifiInetAddress(boolean ipv4) {
		try {
			List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
			for (NetworkInterface intf : interfaces) {
				List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
				for (InetAddress addr : addrs) {
					if (!addr.isLoopbackAddress()) {
						String sAddr = addr.getHostAddress();
						if (sAddr != null) {
							boolean isIPv4 = sAddr.indexOf(':') < 0;
							if (ipv4) {
								if (isIPv4) {
									return addr;
								}
							} else {
								if (!isIPv4) {
									return addr;
								}
							}
						}
					}
				}
			}
		} catch (Exception ignored) { } // for now eat exceptions
		return null;
	}

	private static void log(@NonNull String msg) {
		Log2.e(TAG, msg);
	}

}
