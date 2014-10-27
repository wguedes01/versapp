package com.versapp;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.model.StorageObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.util.Collections;
import java.util.UUID;

/**
 * Created by william on 22/09/14.
 */
public class GCSManager {

    private static final String GCS_BUCKET_NAME = "msgpics";
    private static final String SERVICE_ACCOUNT_EMAIL = "630369039619-emp8sp0r0gcee9vsla7o8lm2kspuppe4@developer.gserviceaccount.com";
    private static final String STORAGE_SCOPE = "https://www.googleapis.com/auth/devstorage.read_write";

    private Context context;
    private static GCSManager instance;
    private Storage storage;

    private GCSManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public static GCSManager getInstance(Context context){

        if (instance == null){
            instance = new GCSManager(context);
        }

        return instance;
    }

    private Storage initializeStorage() throws GeneralSecurityException {

        JsonFactory JSON_FACTORY = new JacksonFactory();// JacksonFactory.getDefaultInstance();

        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
        KeyStore keystore;
        PrivateKey key = null;

        try {
            keystore = KeyStore.getInstance("PKCS12");
            keystore.load(context.getResources().openRawResource(R.raw.gcs_privatekey), "notasecret".toCharArray());
            key = (PrivateKey) keystore.getKey("privatekey", "notasecret".toCharArray());

            GoogleCredential credential = new GoogleCredential.Builder().setTransport(httpTransport).setJsonFactory(JSON_FACTORY)
                    .setServiceAccountPrivateKey(key).setServiceAccountId(SERVICE_ACCOUNT_EMAIL)
                    .setServiceAccountScopes(Collections.singleton(STORAGE_SCOPE))
                            // .setServiceAccountUser("willbrazil.usa@gmail.com")
                            // .setClientSecrets(CLIENT_ID, CLIENT_SECRET)
                    .build();

            credential.refreshToken();

            String URI = "https://storage.googleapis.com/msgpics";
            com.google.api.client.http.HttpRequestFactory requestFactory = httpTransport.createRequestFactory(credential);
            GenericUrl url = new GenericUrl(URI);
            com.google.api.client.http.HttpRequest request = requestFactory.buildGetRequest(url);
            com.google.api.client.http.HttpResponse response = request.execute();
            String content = response.parseAsString();
            Logger.log("testing", "response content is: " + content);

            return new Storage.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName("appname").build();

        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        }

        return null;

    }

    public String upload(Bitmap bitmap) throws IOException {

        if (storage == null) {
            try {

                storage = initializeStorage();

            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }
        }

        if (storage != null){

            // Compress image
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0 /* ignored for PNG */, bos);
            byte[] bitmapdata = bos.toByteArray();
            ByteArrayInputStream bs = new ByteArrayInputStream(bitmapdata);

            int quality = 100;
            while (bs.available() > 40000 && quality > 50) {
                quality -= 10;
                bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
                bitmapdata = bos.toByteArray();
            }
            bs = new ByteArrayInputStream(bitmapdata);

            String imageUrl = generateImageId();

            // Bucket newBucket =
            // storage.buckets().insert("versappkalamazoo-2014", new
            // Bucket().setName("msgpics2").setLocation("US").setStorageClass("DURABLE_REDUCED_AVAILABILITY")).execute();

            InputStreamContent mediaContent = new InputStreamContent("image/png", bs);

            // Knowing the stream length allows server-side
            // optimization, and client-side progress
            // reporting with a MediaHttpUploaderProgressListener.
            mediaContent.setLength(bs.available());

            StorageObject objectMetadata = null;

            Storage.Objects.Insert insertObject = this.storage.objects().insert(GCS_BUCKET_NAME, objectMetadata, mediaContent);

            insertObject.setName(imageUrl);
            // For small files, you may wish to call
            // setDirectUploadEnabled(true), to
            // reduce the number of HTTP requests made to the server.
            if (mediaContent.getLength() > 0 && mediaContent.getLength() <= 2 * 1000 * 1000 /*
																						 * 2
																						 * MB
																						 */) {
                insertObject.getMediaHttpUploader().setDirectUploadEnabled(true);
            }

            insertObject.execute();


            return imageUrl;
        }

        return null;
    }

    public Bitmap downloadImage(final String imageUrl, int reqWidth, int reqHeight) {

        if (storage == null) {
            try {

                storage = initializeStorage();

            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }
        }

        if (storage != null){


            Bitmap image = null;

            Storage.Objects.Get getObject;
            try {

                getObject = storage.objects().get(GCS_BUCKET_NAME, imageUrl);

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                // If you're not in AppEngine, download the whole thing in
                // one request, if possible.
                getObject.getMediaHttpDownloader().setDirectDownloadEnabled(true);
                getObject.executeMediaAndDownloadTo(out);

                return decodeSampledBitmapFromByteArray(out.toByteArray(), reqWidth, reqHeight);

            } catch (IOException e) {
                e.printStackTrace();
            }


        }

        return null;

    }

    private Bitmap decodeSampledBitmapFromByteArray(byte[] byteArray, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap image = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, options);

        return image;
    }

    private String generateImageId() {
        return UUID.randomUUID().toString().replace("-", "");
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

}
