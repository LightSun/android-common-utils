/*
 * Created by Storm Zhang, Feb 13, 2014.
 */

package com.android.volley.data;

import java.util.HashMap;

public class ApiParams extends HashMap<String, String> {
	private static final long serialVersionUID = 8112047472727256876L;

	public ApiParams with(String key, String value) {
		put(key, value);
		return this;
	}
}
