package com.hooloovoo.securenotes.object;

import java.io.IOException;
import java.lang.ref.WeakReference;

import com.hooloovoo.securenotes.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

public class ImageLoader  {
	private Context mContext;
	private LruCache<Integer, Bitmap> mCache;
    private boolean mOnlyImage;
	
	public ImageLoader(Context context){
		this.mContext = context;
		setCache();
	}

	public void loadBitmap(Note note, ImageView imageView,boolean onlyImage) {
        Bitmap b = getBitmapFromMemCache(note.getmId());
        boolean imgChanged;
        try{
            imgChanged = (Boolean) SingletonParametersBridge.getInstance().getParameter("nota:"+note.getmId());
        }catch (NullPointerException ex){
            imgChanged = false;
        }

		final Bitmap bitmap;

        if( b!= null && b.getWidth()>200 && !onlyImage){
            bitmap = null;
        }else if(b!= null && b.getWidth()<100 && onlyImage){
            bitmap = null;
        }else if(imgChanged ){
            bitmap = null;
        }else{
            bitmap = b;
        }

        mOnlyImage = onlyImage;
		if(bitmap != null ){
			imageView.setImageBitmap(bitmap);
		}else if (cancelPotentialWork(note, imageView)) {
	        final ImageLoaderTask task = new ImageLoaderTask(imageView);
	        final AsyncDrawable asyncDrawable =
	                new AsyncDrawable(mContext.getResources(), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_load_img), task);
	        imageView.setImageDrawable(asyncDrawable);
	        task.execute(note);
	    }
	}
	
	public void addBitmapToMemoryCache(Integer key, Bitmap bitmap) {
	    if (getBitmapFromMemCache(key) == null) {
            if(key == null) Log.e("HDHDHDHDHHDDH",key.toString());
            if(bitmap == null) Log.e("HDHDHDHDHDHDHDH",bitmap.toString());
            mCache.put(key, bitmap);
	    }
	}

	public Bitmap getBitmapFromMemCache(Integer key) {
	    return mCache.get(key);
	}
	
	public static boolean cancelPotentialWork(Note data, ImageView imageView) {
	    final ImageLoaderTask bitmapWorkerTask = getImageLoaderTask(imageView);

	    if (bitmapWorkerTask != null) {
	        final Note bitmapData = bitmapWorkerTask.nota;
	        if (bitmapData.getmId() != data.getmId()) {
	            // Cancel previous task
	            bitmapWorkerTask.cancel(true);
	        } else {
	            // The same work is already in progress
	            return false;
	        }
	    }
	    // No task associated with the ImageView, or an existing task was cancelled
	    return true;
	}
	
	public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;
	
	    if (height > reqHeight || width > reqWidth) {
	
	        final int halfHeight = height / 2;
	        final int halfWidth = width / 2;
	
	        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
	        // height and width larger than the requested height and width.
	        while ((halfHeight / inSampleSize) > reqHeight
	                && (halfWidth / inSampleSize) > reqWidth) {
	            inSampleSize *= 2;
	        }
	    }
	
	    return inSampleSize;
	}
	
	public static Bitmap decodeSampledBitmapFromResource(Resources res, byte[] img,
	        int reqWidth, int reqHeight) {

	    // First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeByteArray(img, 0, img.length,options);

	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeByteArray(img, 0, img.length,options);
	}
	
	private static ImageLoaderTask getImageLoaderTask(ImageView imageView) {
		   if (imageView != null) {
		       final Drawable drawable = imageView.getDrawable();
		       if (drawable instanceof AsyncDrawable) {
		           final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
		           return asyncDrawable.getBitmapWorkerTask();
		       }
		    }
		    return null;
		}
	
	private void setCache(){
		// Get max available VM memory, exceeding this amount will throw an
	    // OutOfMemory exception. Stored in kilobytes as LruCache takes an
	    // int in its constructor.
	    final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

	    // Use 1/8th of the available memory for this memory cache.
	    final int cacheSize = maxMemory / 16;

	    mCache = new LruCache<Integer, Bitmap>(cacheSize) {
	        @Override
	        protected int sizeOf(Integer key, Bitmap bitmap) {
	            // The cache size will be measured in kilobytes rather than
	            // number of items.
	            return bitmap.getByteCount() / 1024;
	        }
	    };
	}
	
	class ImageLoaderTask extends AsyncTask<Note, Void, Bitmap> {
		private ImageView mImageView;
		private final WeakReference<ImageView> imageViewReference;
        private Encryptor encryptor = (Encryptor) SingletonParametersBridge.getInstance().getParameter("encrypt");
		Note nota;
		
		public ImageLoaderTask(ImageView imageview){
			this.mImageView = imageview;
			this.imageViewReference = new WeakReference<ImageView>(this.mImageView);
		}

		@Override
		protected Bitmap doInBackground(Note... params) {
			nota = params[0];
            byte[] img;
            try{
                byte[] imgEn = nota.getmImage();
                String enimgString = new String(imgEn);
                String imgString = encryptor.decrypt(enimgString,Encryptor.password);
                img = Base64.decode(imgString, Base64.DEFAULT);
                nota.setmImage(img);

            }catch(Exception ex){
                //niente da decriptare
                ex.printStackTrace();
                img = nota.getmImage();
            }

            //////////////
            BitmapRegionDecoder bitmapRegionDecoder;
            Bitmap bitmap = null;
            try {
                BitmapFactory.Options op = new BitmapFactory.Options();
                op.inSampleSize = 1;
                bitmapRegionDecoder = BitmapRegionDecoder.newInstance(img,0,img.length,true);
                Bitmap bit = BitmapFactory.decodeByteArray(img,0,img.length,op);
                int w = bit.getWidth();
                int h = bit.getHeight();
                if(nota.getmDesc().equals("")&& nota.getmName().equals("")){
                    bitmap = bitmapRegionDecoder.decodeRegion(new Rect(0,(h/2)-125, w, (h/2)+125),op);Log.d("caricamento solo img",""+mOnlyImage);
                }else{
                    bitmap = bitmapRegionDecoder.decodeRegion(new Rect((w/2)-100,(h/2)-100, (w/2)+100, (h/2)+100),op);Log.d("caricamento solo img",""+mOnlyImage);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            //Bitmap bitmap = decodeSampledBitmapFromResource(mContext.getResources(), img, 100, 100);

            try {
                addBitmapToMemoryCache(nota.getmId(), bitmap);
            }catch (NullPointerException ex){
                //
            }

			return bitmap;
			
		}
		
		// Once complete, see if ImageView is still around and set bitmap.
	    @Override
	    protected void onPostExecute(Bitmap bitmap) {
	    	 if (isCancelled()) {
	             bitmap = null;
	         }
	    	 
	        if (imageViewReference != null && bitmap != null) {
	            final ImageView imageView = imageViewReference.get();
	            final ImageLoaderTask imageLoaderTask = getImageLoaderTask(imageView);
	            if (imageLoaderTask != null && imageView != null) {
	                imageView.setImageBitmap(bitmap);
	            }
	        }
	    }

	}
	
	static class AsyncDrawable extends BitmapDrawable {
	    private final WeakReference<ImageLoaderTask> bitmapImageLoaderTaskReference;

	    public AsyncDrawable(Resources res, Bitmap bitmap,
	            ImageLoaderTask bitmapImageLoaderTask) {
	        super(res, bitmap);
	        bitmapImageLoaderTaskReference =
	            new WeakReference<ImageLoaderTask>(bitmapImageLoaderTask);
	    }

	    public ImageLoaderTask getBitmapWorkerTask() {
	        return bitmapImageLoaderTaskReference.get();
	    }
	}

}
