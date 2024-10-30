package app.misono.unit206.debug;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import app.misono.unit206.callback.CallbackString;
import app.misono.unit206.misc.Utils;
import app.misono.unit206.task.Taskz;

import com.google.android.gms.tasks.Task;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LogTcp {
	private static final SimpleDateFormat form = new SimpleDateFormat("HH:mm:ss.SSS", Locale.US);

	/**
	 * server function.
	 * @return	server port number.
	 */
	@NonNull
	public static Task<Integer> startLogServer(
		@NonNull File logFile,
		int portServerStart,
		@WorkerThread @Nullable CallbackString callback
	) {
		return Taskz.call(Taskz.getExecutor(), () -> {
			int portServer = portServerStart;
			boolean success = false;
			for (int i = 0; i < 10; i++) {
				try {
					ServerSocket serverSocket = new ServerSocket(portServer);
					Taskz.call(Taskz.getExecutor(), () -> {
						for ( ; ; ) {
							Socket socket = serverSocket.accept();
							Taskz.call(Taskz.getExecutor(), () -> {
								byte[] b = new byte[1024 * 1024];
								try (
									InputStream is = socket.getInputStream();
									BufferedInputStream bis = new BufferedInputStream(is);
									FileOutputStream fos = new FileOutputStream(logFile, true);
								) {
									for ( ; ; ) {
										int len = bis.read(b);
										fos.write(b, 0, len);
										if (callback != null) {
											String s = new String(b, 0, len);	// TODO: \nで callbackにする
											callback.callback(s);
										}
									}
								} finally {
									Utils.closeSafely(socket);
								}
//								return null;
							}).addOnFailureListener(Taskz::printStackTrace2);
						}
//						return null;
					}).addOnFailureListener(Taskz::printStackTrace2);
				} catch (Exception e) {
					portServer++;
					continue;
				}
				success = true;
				break;
			}
			if (success) {
				return portServer;
			} else {
				return -1;
			}
		});
	}

	private static Socket soc = null;
	private static String hostServer = null;
	private static boolean firstTime = true;
	private static int portServer = 0;

	public static void setServerHost(@NonNull String hostServer, int portServer) {
		LogTcp.hostServer = hostServer;
		LogTcp.portServer = portServer;
	}

	/**
	 * Connect to the log server.
	 * client function.
	 */
	@WorkerThread
	public static void connect() throws Exception {
		try {
			if (soc != null) {
				Utils.closeSafely(soc);
			}
			soc = new Socket();
			SocketAddress server = new InetSocketAddress(hostServer, portServer);
			soc.connect(server, 1000);
		} catch (Exception e) {
			Utils.closeSafely(soc);
			soc = null;
			throw e;
		}
	}

	public static boolean isConnectedToServer() {
		return soc != null;
	}

	/**
	 * log output.
	 * client function.
	 */
	@WorkerThread
	public static synchronized void e(@NonNull String tag, @NonNull String msg) {
		if (firstTime) {
			firstTime = false;
			try {
				connect();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (soc != null) {
			try {
				OutputStream os = soc.getOutputStream();
				String s = form.format(new Date()) + " : " + tag + " : " + msg + "\n";
				os.write(s.getBytes());
			} catch (Exception e) {
				e.printStackTrace();
				Utils.closeSafely(soc);
				soc = null;
			}
		}
		Log2.e(tag, msg);
	}

}
