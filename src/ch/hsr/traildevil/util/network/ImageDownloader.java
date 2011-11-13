package ch.hsr.traildevil.util.network;

import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import ch.hsr.traildevil.util.persistence.StorageHandler;

public enum ImageDownloader {
	Instance;

	private final Map<String, SoftReference<Drawable>> cacheMap = new HashMap<String, SoftReference<Drawable>>();
	private final LinkedList<Drawable> cacheController = new LinkedList<Drawable>();
	private ExecutorService threadpool;
	private final Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());

	public static int MAX_CACHE_SIZE = 80;
	public int THREAD_POOL_SIZE = 3;

	ImageDownloader() {
		threadpool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
	}

	/**
	 * Clears all instance data and stops running threads.
	 */
	public void Reset() { // TODO insert in onDestroy() of list activity
		ExecutorService oldThreadPool = threadpool;
		threadpool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
		oldThreadPool.shutdownNow();
		cacheController.clear();
		cacheMap.clear();
		imageViews.clear();
	}

	/**
	 * Searches the cache if the Drawable already exists in the HashMap. If yes,
	 * then it will be set on the view. Else a default image is set to the view
	 * and a thread starts do download the image from the web in the background.
	 * 
	 * @param url
	 *            the url to the image
	 * @param imageView
	 *            the view to set the image
	 * @param placeHolder
	 *            a default image
	 */
	public void loadDrawable(final String url, final ImageView imageView, int placeHolder) {
		imageViews.put(imageView, url);
		Drawable drawable = getDrawableFromCache(url);

		// check in UI thread, so no concurrency issues
		if (drawable != null) {
			imageView.setImageDrawable(drawable);
		} else {
			imageView.setImageResource(placeHolder);
			if (url != null) {
				queueJob(url, imageView, placeHolder);
			}
		}
	}

	private Drawable getDrawableFromCache(String url) {
		if (cacheMap.containsKey(url)) {
			return cacheMap.get(url).get();
		}
		return null;
	}

	/**
	 * Creates a handler in the UI thread and sets the images when the
	 * background thread notifies his download as completed. Pay attention on
	 * the THREAD_POOL_SIZE and regulate it if required.
	 * 
	 * @param url
	 *            the url to the image
	 * @param imageView
	 *            the view to set the image
	 * @param placeHolder
	 *            a default image
	 */
	private void queueJob(final String url, final ImageView imageView, final int placeholder) {
		/* Create handler in UI thread. */
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				String tag = imageViews.get(imageView);
				if (tag != null && url.equals(tag)) {
					if (imageView.isShown())
						if (msg.obj != null) {
							imageView.setImageDrawable((Drawable) msg.obj);
						} else {
							imageView.setImageResource(placeholder);
						}
				}
			}
		};

		threadpool.submit(new Runnable() {
			public void run() {
				String filename = getFilename(url);
				StorageHandler storage = new StorageHandler();
				Drawable drawable = storage.getDrawableFromStorage(filename);

				if (drawable != null) {
					putDrawableInCache(url, drawable);
				} else {
					drawable = HttpHandler.getHttpImage(url);
					putDrawableInCache(url, drawable);
					storage.storeDrawableToStorage(drawable, filename);
				}

				// if the view is not visible anymore, the image will be ready
				// for next time in cache
				if (imageView.isShown()) {
					Message message = Message.obtain();
					message.obj = drawable;
					handler.sendMessage(message);
				}
			}
		});
	}

	private String getFilename(String url) {
		String[] pieces = url.split("/");
		return pieces[pieces.length - 1];
	}

	/**
	 * Saves a drawable in the cache with a soft reference. If systems gets out
	 * of memory, those references will be deleted quickly. Additional it
	 * monitors the list size of drawables and reduces it if necessary to avoid
	 * an overload of drawables.
	 * 
	 * @param url
	 *            the url to the image
	 * @param drawable
	 *            the image to save
	 */
	private synchronized void putDrawableInCache(String url, Drawable drawable) {
		int chacheControllerSize = cacheController.size();
		if (chacheControllerSize > MAX_CACHE_SIZE) {
			cacheController.subList(0, MAX_CACHE_SIZE / 2).clear();
		}
		cacheController.addLast(drawable);
		cacheMap.put(url, new SoftReference<Drawable>(drawable));
	}

}
