/*
 * Copyright 2023 Atelier Misono, Inc. @ https://misono.app/
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

import androidx.annotation.NonNull;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class Date422 {
	private Date422() {
	}

	public static int getDayOfWeek(@NonNull String date422) {
		return getDayOfWeek(Calendar.getInstance(), date422);
	}

	public static int getDayOfWeek(@NonNull TimeZone zone, @NonNull String date422) {
		Calendar cal = Calendar.getInstance(zone);
		cal.setTimeInMillis(0);
		cal.set(Calendar.YEAR, Integer.parseInt(date422.substring(0, 4)));
		cal.set(Calendar.MONTH, Integer.parseInt(date422.substring(4, 6)) - 1);
		cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date422.substring(6, 8)));
		return cal.get(Calendar.DAY_OF_WEEK);
	}

	public static int getDayOfWeek(@NonNull Calendar tmp, @NonNull String date422) {
		return getDayOfWeek(tmp, Integer.parseInt(date422));
	}

	public static int getDayOfWeek(@NonNull Calendar tmp, int date422) {
		tmp.setTimeInMillis(0);
		tmp.set(Calendar.YEAR, date422 / 10000);
		tmp.set(Calendar.MONTH, ((date422 / 100) % 100) - 1);
		tmp.set(Calendar.DAY_OF_MONTH, date422 % 100);
		return tmp.get(Calendar.DAY_OF_WEEK);
	}

	public static long getDate422Time(int date422) {
		return getDate422Time(Calendar.getInstance(), date422);
	}

	public static long getDate422Time(@NonNull Calendar tmp, int date422) {
		tmp.setTimeInMillis(0);
		tmp.set(Calendar.YEAR, date422 / 10000);
		tmp.set(Calendar.MONTH, ((date422 / 100) % 100) - 1);
		tmp.set(Calendar.DAY_OF_MONTH, date422 % 100);
		return tmp.getTimeInMillis();
	}

	public static int getDate422(@NonNull Calendar cal) {
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		return year * 10000 + month * 100 + day;
	}

	public static int getDate422(@NonNull Calendar tmp, long tick) {
		tmp.setTimeInMillis(tick);
		return getDate422(tmp);
	}

	public static int getDate422(long tick) {
		return getDate422(Calendar.getInstance(), tick);
	}

	public static int getPresentDate422() {
		return getDate422(Calendar.getInstance());
	}

	@NonNull
	public static String getDate422String(int date422) {
		int yy = date422 / 10000;
		int mm = (date422 / 100) % 100;
		int dd = date422 % 100;
		return String.format(Locale.US, "%04d/%02d/%02d", yy, mm, dd);
	}

	@NonNull
	public static String getDate42String(int date42) {
		int yy = date42 / 100;
		int mm = date42 % 100;
		return String.format(Locale.US, "%04d/%02d", yy, mm);
	}

	private static final String[] DOWS = new String[] {
		"???", "SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT",
	};

	@NonNull
	public static String getDayOfWeekString(int dow) {
		return DOWS[dow];
	}

}
