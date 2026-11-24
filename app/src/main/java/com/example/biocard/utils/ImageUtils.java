package com.example.biocard.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.TypedValue;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import com.example.biocard.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Handles profile photos. Picked images are copied (downscaled, EXIF-rotated) into the app's
 * private storage so the photo stays available even if the original is deleted, and is never
 * exposed to other apps.
 */
public final class ImageUtils {

    private static final String PHOTO_DIR = "photos";
    private static final int MAX_PHOTO_SIZE = 1024;
    private static final int DISPLAY_SIZE = 512;

    private ImageUtils() {
    }

    /** Copies the picked image into private storage and returns its file:// URI string. */
    @WorkerThread
    @NonNull
    public static String importPhoto(@NonNull Context context, @NonNull Uri source) throws IOException {
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        try (InputStream in = context.getContentResolver().openInputStream(source)) {
            BitmapFactory.decodeStream(in, null, bounds);
        }
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) {
            throw new IOException("Unsupported image");
        }

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = sampleSize(bounds.outWidth, bounds.outHeight, MAX_PHOTO_SIZE);
        Bitmap bitmap;
        try (InputStream in = context.getContentResolver().openInputStream(source)) {
            bitmap = BitmapFactory.decodeStream(in, null, opts);
        }
        if (bitmap == null) throw new IOException("Unable to decode image");

        bitmap = applyExifRotation(context, source, bitmap);
        bitmap = scaleDown(bitmap, MAX_PHOTO_SIZE);

        File dir = new File(context.getFilesDir(), PHOTO_DIR);
        if (!dir.exists() && !dir.mkdirs()) throw new IOException("Cannot create photo directory");
        File out = new File(dir, "photo_" + System.currentTimeMillis() + ".jpg");
        try (OutputStream os = new FileOutputStream(out)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, os);
        } finally {
            bitmap.recycle();
        }
        return Uri.fromFile(out).toString();
    }

    /** Deletes every stored photo except {@code keepUri} (may be null to delete all). */
    @WorkerThread
    public static void deleteUnusedPhotos(@NonNull Context context, @Nullable String keepUri) {
        File dir = new File(context.getFilesDir(), PHOTO_DIR);
        File[] files = dir.listFiles();
        if (files == null) return;
        String keepPath = keepUri == null ? null : Uri.parse(keepUri).getPath();
        for (File f : files) {
            if (!f.getAbsolutePath().equals(keepPath)) {
                //noinspection ResultOfMethodCallIgnored
                f.delete();
            }
        }
    }

    /** Shows the photo, or a tinted placeholder avatar if there is none. */
    public static void loadProfilePhoto(@NonNull ImageView view, @Nullable String photoUri) {
        Bitmap bitmap = decodeForDisplay(photoUri);
        if (bitmap != null) {
            view.setImageTintList(null);
            view.setImageBitmap(bitmap);
        } else {
            view.setImageResource(R.drawable.placeholder_avatar);
            view.setImageTintList(ColorStateList.valueOf(resolveColor(view.getContext(),
                    com.google.android.material.R.attr.colorOnPrimaryContainer)));
        }
    }

    @Nullable
    private static Bitmap decodeForDisplay(@Nullable String photoUri) {
        if (photoUri == null || photoUri.isEmpty()) return null;
        String path = Uri.parse(photoUri).getPath();
        if (path == null || !new File(path).exists()) return null;
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bounds);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = sampleSize(bounds.outWidth, bounds.outHeight, DISPLAY_SIZE);
        return BitmapFactory.decodeFile(path, opts);
    }

    private static int sampleSize(int width, int height, int target) {
        int sample = 1;
        while (width / (sample * 2) >= target && height / (sample * 2) >= target) {
            sample *= 2;
        }
        return sample;
    }

    private static Bitmap scaleDown(Bitmap src, int max) {
        int w = src.getWidth();
        int h = src.getHeight();
        if (w <= max && h <= max) return src;
        float ratio = Math.min((float) max / w, (float) max / h);
        Bitmap scaled = Bitmap.createScaledBitmap(src, Math.round(w * ratio), Math.round(h * ratio), true);
        if (scaled != src) src.recycle();
        return scaled;
    }

    private static Bitmap applyExifRotation(Context context, Uri source, Bitmap bitmap) {
        int degrees = 0;
        try (InputStream in = context.getContentResolver().openInputStream(source)) {
            if (in != null) {
                int orientation = new ExifInterface(in).getAttributeInt(
                        ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                if (orientation == ExifInterface.ORIENTATION_ROTATE_90) degrees = 90;
                else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) degrees = 180;
                else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) degrees = 270;
            }
        } catch (IOException | RuntimeException ignored) {
            // Not a JPEG or no EXIF data: keep as is.
        }
        if (degrees == 0) return bitmap;
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        Bitmap rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        if (rotated != bitmap) bitmap.recycle();
        return rotated;
    }

    private static int resolveColor(Context context, int attr) {
        TypedValue tv = new TypedValue();
        context.getTheme().resolveAttribute(attr, tv, true);
        return tv.data;
    }
}

