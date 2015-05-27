package vandy.mooc.services;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import vandy.mooc.utils.ReplyMessage;
import vandy.mooc.utils.RequestMessage;
import vandy.mooc.utils.Utils;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

/**
 * This class handles messages sent from an Activity in a pool of
 * threads managed by the Java ExecutorService.
 */
class RequestHandler extends Handler {
	
	/**
     * String constant used to extract the Messenger "extra" from an
     * intent.
     */
    private static final String MESSENGER = "MESSENGER";

    /**
     * String constant used to extract the pathname to a downloaded
     * image from a Bundle.
     */
    private static final String IMAGE_PATHNAME = "IMAGE_PATHNAME";

    /**
     * String constant used to extract the request code.
     */
    private static final String REQUEST_CODE = "REQUEST_CODE";

    /**
     * String constant used to extract the URL to an image from a
     * Bundle.
     */
    private static final String IMAGE_URL = "IMAGE_URL";

    /**
     * String constant used to extract the directory pathname to use
     * to store a downloaded image.
     */
    private static final String DIRECTORY_PATHNAME = "DIRECTORY_PATHNAME";
    
    /**
     * Debugging tag used by the Android logger.
     */
    private final String TAG = getClass().getSimpleName();

    /**
     * Store a WeakReference to the Service to enable garbage
     * collection.
     */
    WeakReference<DownloadImagesBoundService> mService;
    
    /**
     * Reference to the ExecutorService that manages a pool of
     * threads.
     */
    private ExecutorService mExecutorService;

    /**
     * Constructor initializes the WeakReference and ExecutorService.
     */
    public RequestHandler(DownloadImagesBoundService service) {
        // Store a WeakReference to the DownloadImageService.
        mService = new WeakReference<>(service);

        // Create an ExecutorService that manages a pool of threads.
        mExecutorService = Executors.newCachedThreadPool();
    }

    /**
     * Hook method called back when a request message arrives from an
     * Activity.  The Message it receives contains the Messenger used
     * to reply to the Activity and the URL of the image to download.
     * This image is stored in a local file on the local device and
     * image file's URI is sent back to the MainActivity via the
     * Messenger passed with the message.
     */
    public void handleMessage(Message message) {
        // Convert the Message into a RequestMessage.
        final RequestMessage requestMessage =
            RequestMessage.makeRequestMessage(message);

        // Get the reply Messenger.
        // TODO -- you fill in here.
        final Messenger msg = requestMessage.getMessenger();

        // Get the URL associated with the Intent data.
        // TODO -- you fill in here.
        final Uri url = requestMessage.getImageURL();

        // Get the directory pathname where the image will be stored.
        // TODO -- you fill in here.
        final String pathName = requestMessage.getImagePathname();
        
        // Get the requestCode for the operation that was invoked by
        // the Activity.
        // TODO -- you fill in here.
        final int requestCode = requestMessage.getRequestCode();

        // A Runnable that downloads the image, stores it in a file,
        // and sends the path to the file back to the Activity.
        final Runnable downloadImageAndReply = 
            new Runnable() {
                /**
                 * This method runs in a background Thread.
                 */
                @Override
                public void run() {
                    // Download and store the requested image.
                    // TODO -- you fill in here.
                	Uri downloadImgUri = Utils.downloadImage(mService.get(), url, pathName);

                    // Send the path to the image file, url, and
                    // requestCode back to the Activity via the
                    // replyMessenger.
                    // TODO -- you fill in here.
                	sendPath(msg, downloadImgUri, url, requestCode);
                }
            };

        // Execute the downloadImageAndReply Runnable to download the
        // image and reply.
        // TODO -- you fill in here.
        mExecutorService.execute(downloadImageAndReply);
    }

    /**
     * Send the @a pathToImageFile, @a url, and @a requestCode back to
     * the Activity via the @a messenger.
     */
    public void sendPath(Messenger messenger, 
                         Uri pathToImageFile,
                         Uri url,
                         int requestCode) {
        // Call the makeReplyMessage() factory method to create
        // Message.
        // TODO -- you fill in here.
    	ReplyMessage message =  ReplyMessage.makeReplyMessage(pathToImageFile, url, requestCode);

        try {
            Log.d(TAG,
                  "sending "
                  + pathToImageFile
                  + " back to the MainActivity");

            
            // Send the replyMessage back to the Activity.
            // TODO -- you fill in here.
        messenger.send(message.getMessage());
        } catch (Exception e) {
            Log.e(getClass().getName(),
                  "Exception while sending reply message back to Activity.",
                  e);
        }
    }

    /**
     * Shutdown the ExecutorService immediately.
     */
    public void shutdown() {
        // Immediately shutdown the ExecutorService.
        // TODO -- you fill in here.  
    	mExecutorService.shutdownNow();
    }
    
    /**
     * A factory method that creates a Message to return to the
     * MainActivity with the pathname of the downloaded image.
     */
    private ReplyMessage makeReplyMessage(Uri pathToImageFile,
                                     Uri url) {
        // Get a message via the obtain() factory method.
        
        return ReplyMessage.makeReplyMessage(pathToImageFile, url, Activity.RESULT_OK);
    }
}

