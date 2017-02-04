package net.styleru.aims.myviews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by LeonidL on 24.11.16.
 * Один из способов реализации круглый картинок (нативный)
 */

public class RoundedImageView extends ImageView {
    public RoundedImageView(Context context) {
        super(context);
    }

    public RoundedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RoundedImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RoundedImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();

        Bitmap b = ((BitmapDrawable) drawable).getBitmap();
        Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);

        int w = getWidth();
        int h = getHeight();

        Bitmap roundBitmap = getCircleMaskedBitmap(bitmap, h/2);
        canvas.drawBitmap(roundBitmap, 0, 0, null);
    }

    //not radius - diametr
    private Bitmap getCroppedBitmap(Bitmap bmp, int radius) {
        Bitmap sbpm;

        if(bmp.getWidth() != radius || bmp.getHeight() != radius) {
            float smallest = Math.min(bmp.getWidth(), bmp.getHeight());
            float factor = smallest/radius;
            sbpm = Bitmap.createScaledBitmap(bmp, (int)(bmp.getWidth()/factor), (int)(bmp.getHeight()/factor), false);
        }
        else {
            sbpm = bmp;
        }

        Bitmap output = Bitmap.createBitmap(radius, radius, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xffa19774;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, radius, radius);

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#BAB399"));
        canvas.drawCircle(radius/2 + 0.7f, radius/2+0.7f, radius/2+0.1f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(sbpm, rect, rect, paint);

        return output;
    }

    public Bitmap getCircleMaskedBitmap(Bitmap source, int radius) {
        int diam = radius << 1;

        Bitmap scaledBitmap = scaleTo(source, diam);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        final Shader shader = new BitmapShader(scaledBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        paint.setShader(shader);

        Bitmap targetBitmap = Bitmap.createBitmap(diam, diam, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(targetBitmap);

        canvas.drawCircle(radius, radius, radius, paint);

        return targetBitmap;

    }

    private  Bitmap scaleTo(Bitmap source, int size) {
        int desWidh = source.getWidth();

        int desHeight = source.getHeight();


        desHeight = desHeight *size/desWidh;
        desWidh = size;

        if(desHeight < size) {
            desWidh = desWidh *size/desHeight;
            desHeight = size;
        }

        Bitmap destBitmap = Bitmap.createBitmap(desWidh, desHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(destBitmap);
        canvas.drawBitmap(source, new Rect(0, 0, source.getWidth(), source.getHeight()), new Rect(0, 0, desWidh, desHeight), new Paint(Paint.ANTI_ALIAS_FLAG));
        return  destBitmap;
    }
}
