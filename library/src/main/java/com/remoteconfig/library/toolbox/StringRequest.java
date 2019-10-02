/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.remoteconfig.library.toolbox;

import androidx.annotation.GuardedBy;
import androidx.annotation.Nullable;
import com.remoteconfig.library.NetworkResponse;
import com.remoteconfig.library.Request;
import com.remoteconfig.library.Response;
import com.remoteconfig.library.Response.ErrorListener;
import com.remoteconfig.library.Response.Listener;

import java.io.UnsupportedEncodingException;

/** A canned request for retrieving the response body at a given URL as a String. */

public class StringRequest extends Request<String> {

    private final Object mLock = new Object();
    private String url;

    @Nullable
    @GuardedBy("mLock")
    private Listener<String> mListener;

    /**
     * Creates a new request with the given method.
     *
     */

    public StringRequest(
            @Nullable Listener<String> listener,
            @Nullable ErrorListener errorListener) {
        super(0,  "https://raw.githubusercontnt.com/gayankuruppu/android-remote-config-library/master/remote-config.json", errorListener);
        mListener = listener;
    }

    @Override
    public void cancel() {
        super.cancel();
        synchronized (mLock) {
            mListener = null;
        }
    }

    @Override
    protected void deliverResponse(String response) {
        Listener<String> listener;
        synchronized (mLock) {
            listener = mListener;
        }
        if (listener != null) {
            listener.onComplete(response);
        }
    }

    @Override
    @SuppressWarnings("DefaultCharset")
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String parsed;
        try {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }
        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
    }
}