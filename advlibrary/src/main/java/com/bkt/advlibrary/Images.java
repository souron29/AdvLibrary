package com.bkt.advlibrary;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.IOException;

public class Images {
    public static Bitmap getBitmap(Context context, Uri image_uri) {
        try {
            return MediaStore.Images.Media.getBitmap(context.getContentResolver(), image_uri);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap getBitmap(Context context, int drawable, int tintColor) {
        try {
            Drawable d = context.getResources().getDrawable(drawable, (Resources.Theme) null);
            if (tintColor > 0) {
                d = setColorTint(d, tintColor);
            }
            return drawableToBitmap(d);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap;
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }
        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static Bitmap getBitmapMarker(Context context, int drawable) {
        Drawable vectorDrawable = getDrawable(context, drawable);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        vectorDrawable.draw(new Canvas(bitmap));
        return bitmap;
    }

    public static Drawable getDrawable(Context context, int id) {
        return context.getDrawable(id);
    }

    public static Drawable getDrawable(Context context, int drawable_id, int color_id) {
        return setColorTint(getDrawable(context, drawable_id), CommonFunctions.Colors.getColor(context, color_id));
    }

    public static Drawable setColorTint(Context context, int drawable_id, int color_id) {
        return setColorTint(getDrawable(context, drawable_id), CommonFunctions.Colors.getColor(context, color_id));
    }

    public static Drawable setColorTint(Drawable drawable, int color) {
        if (drawable != null) {
            drawable.mutate();
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        }
        return drawable;
    }
}
