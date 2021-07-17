package com.newsblur.util;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.graphics.Bitmap;

public class MemoryCache {

	private Map<String, Bitmap> cache = Collections.synchronizedMap(new LinkedHashMap<String, Bitmap>(32, 1.5f, true));
	private long size = 0; //current allocated size
	private long limit; //max memory in bytes

	public MemoryCache(long limitBytes) {
        this.limit = limitBytes;
	}

	public Bitmap get(String url){
		try {
			if (cache == null || !cache.containsKey(url)) {
				return null;
			} else {
				return cache.get(url);
			}
		} catch (NullPointerException ex){
			return null;
		}
	}

	public void put(String url, Bitmap bitmap) {
        synchronized (this) {
			if (cache.containsKey(url)) {
                size -= getSizeInBytes(cache.get(url));
            }
            cache.put(url, bitmap);
            size += getSizeInBytes(bitmap);
            checkSize();
        }
	}

	private void checkSize() {
		if (size > limit) {
			final Iterator<Entry<String, Bitmap>> iter = cache.entrySet().iterator();  
			while (iter.hasNext()) {
				final Entry<String, Bitmap> entry = iter.next();
				size -= getSizeInBytes(entry.getValue());
				iter.remove();
				if (size <= limit) {
					break;
				}
			}
		}
	}

	private long getSizeInBytes(Bitmap bitmap) {
		if (bitmap == null) {
			return 0;
		} else {
			return (bitmap.getAllocationByteCount());
		}
	}
}
