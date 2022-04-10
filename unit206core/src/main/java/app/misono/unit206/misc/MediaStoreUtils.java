/*
 * Copyright 2020-2022 Atelier Misono, Inc. @ https://misono.app/
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

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import app.misono.unit206.task.Taskz;

import com.google.android.gms.tasks.Task;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.Executor;

public class MediaStoreUtils {

	@RequiresApi(29)
	public static Task<Void> insertImage(
		@NonNull Context context,
		@NonNull Executor executor,
		@NonNull String nameDisplay,
		@NonNull String typeMime,
		@NonNull File file
	) {
		return Taskz.call(executor, () -> {
			ContentValues values = new ContentValues();
			values.put(MediaStore.Images.Media.DISPLAY_NAME, nameDisplay);
			values.put(MediaStore.Images.Media.MIME_TYPE, typeMime);
			values.put(MediaStore.Images.Media.IS_PENDING, 1);
			ContentResolver resolver = context.getContentResolver();

			Uri collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
			Uri item = resolver.insert(collection, values);

			BufferedInputStream is = null;
			OutputStream os = null;
			try {
				is = new BufferedInputStream(new FileInputStream(file));
				os = resolver.openOutputStream(item);
				byte[] b = new byte[4096];
				for ( ; ; ) {
					int len = is.read(b);
					if (len < 0) {
						break;
					}
					os.write(b, 0, len);
				}
				os.close();
				os = null;
				is.close();
				is = null;
			} catch (IOException e) {
				Utils.closeSafely(is);
				Utils.closeSafely(os);
				resolver.delete(item, null, null);
				throw e;
			}
			Utils.closeSafely(is);
			Utils.closeSafely(os);
			values.clear();
			values.put(MediaStore.Images.Media.IS_PENDING, 0);
			resolver.update(item, values, null, null);
			return null;
		});
	}

}
