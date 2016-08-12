package com.example.gundars.mytestapplication;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.renderscript.Sampler;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.Arrays;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;

public class DrawingView extends View {

//    public int width;
//    public  int height;
    private Bitmap mBitmap;
    private Canvas  mCanvas;
    private Paint   mBitmapPaint;
    Context context;

    private Paint mPaint;
    private boolean[][] valueMatrix=new boolean[8][8];
    private boolean[][] touched_piece=new boolean[8][8];
    int grid_size;

    BluetoothSPP bt;


    public DrawingView(Context c, AttributeSet attrs) {
        super(c, attrs);
        context=c;
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);

        mPaint = new Paint();

        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
    }

    public void setBluetoothHandle(BluetoothSPP bt_h){
        bt = bt_h;
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);

        if(w < h){ grid_size = w; } else{ grid_size = h; }

        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                mCanvas.drawRect(grid_size/8*i, grid_size/8*j, grid_size/8*(i+1), grid_size/8*(j+1), mPaint);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap( mBitmap, 0, 0, mBitmapPaint);
    }

    private void touch_move(int x, int y) {

        if(x > 0 && y > 0 && x < grid_size && y < grid_size){
            int rect_x = x*8/grid_size;
            int rect_y = y*8/grid_size;
            if(touched_piece[rect_x][rect_y]) return;
            if(valueMatrix[rect_x][rect_y]){
                mPaint.setColor(Color.WHITE);
                mCanvas.drawRect(grid_size/8*rect_x, grid_size/8*rect_y,
                        grid_size/8*(rect_x+1), grid_size/8*(rect_y+1), mPaint);
                mPaint.setColor(Color.BLACK);
                mPaint.setStyle(Paint.Style.STROKE);
                mCanvas.drawRect(grid_size/8*rect_x, grid_size/8*rect_y,
                        grid_size/8*(rect_x+1), grid_size/8*(rect_y+1), mPaint);
                mPaint.setStyle(Paint.Style.FILL);
                valueMatrix[rect_x][rect_y] = false;
            } else{
                mPaint.setStyle(Paint.Style.FILL);
                mCanvas.drawRect(grid_size/8*rect_x, grid_size/8*rect_y,
                        grid_size/8*(rect_x+1), grid_size/8*(rect_y+1), mPaint);
                valueMatrix[rect_x][rect_y] = true;
            }
            touched_piece[rect_x][rect_y] = true;
            // send line here
            StringBuilder s = new StringBuilder(25);
            s.append("$" + String.valueOf(rect_y));
            for(int i = 0; i < 8; i++){
                if(valueMatrix[i][rect_y]){
                    s.append("ff0");
                } else{
                    s.append("000");
                }
            }
            String tosend = s.toString();
            bt.send(tosend, false);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = Math.round(event.getX());
        int y = Math.round(event.getY());

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                for (boolean[] row: touched_piece)
                    Arrays.fill(row, false);
                invalidate();
                break;
        }
        return true;
    }

    public void clear() {
        mBitmap.eraseColor(Color.TRANSPARENT);
        mPaint.setStyle(Paint.Style.STROKE);
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                mCanvas.drawRect(grid_size/8*i, grid_size/8*j, grid_size/8*(i+1), grid_size/8*(j+1), mPaint);
            }
        }

        for (boolean[] row: valueMatrix)
            Arrays.fill(row, false);

        for(int i = 0; i < 8; i++){
            String tosend = "$" + String.valueOf(i) + "000000000000000000000000";
            bt.send(tosend, false);
        }

        invalidate();
    }
}
