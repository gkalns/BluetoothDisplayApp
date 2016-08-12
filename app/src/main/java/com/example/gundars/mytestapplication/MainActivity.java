package com.example.gundars.mytestapplication;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothSPP.OnDataReceivedListener;
import app.akexorcist.bluetotohspp.library.BluetoothSPP.BluetoothConnectionListener;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;


public class MainActivity extends AppCompatActivity {
    BluetoothSPP bt;
    TextView textStatus;

    private DrawingView dv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textStatus = (TextView)findViewById(R.id.textStatus);

        bt = new BluetoothSPP(this);
        if(!bt.isBluetoothAvailable()) {
            Toast.makeText(getApplicationContext(), "Bluetooth is not available", Toast.LENGTH_SHORT).show();
            finish();
        }

        bt.setOnDataReceivedListener(new OnDataReceivedListener() {
            public void onDataReceived(byte[] data, String message) {
                // process received msg
            }
        });

        bt.setBluetoothConnectionListener(new BluetoothConnectionListener() {
            public void onDeviceDisconnected() {
                textStatus.setText("Status : Not connected");
            }

            public void onDeviceConnectionFailed() {
                textStatus.setText("Status : Connection failed");
            }

            public void onDeviceConnected(String name, String address) {
                textStatus.setText("Status : Connected to " + name);
            }
        });
    }

    public void onDestroy() {
        super.onDestroy();
        bt.stopService();
    }

    public void onStart() {
        super.onStart();
        if (!bt.isBluetoothEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            if(!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_ANDROID);
//                setup();
                dv = (DrawingView) findViewById(R.id.view);
                dv.setBluetoothHandle(bt);

            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if(resultCode == Activity.RESULT_OK)
                bt.connect(data);
        } else if(requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if(resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_ANDROID);
//                setup();

                dv = (DrawingView) findViewById(R.id.view);
                dv.setBluetoothHandle(bt);
            } else {
                Toast.makeText(getApplicationContext()
                        , "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    public void connect(View v){
        bt.setDeviceTarget(BluetoothState.DEVICE_OTHER);

        Intent intent = new Intent(getApplicationContext(), DeviceList.class);
        startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
    }

    public void clearGrid(View view) {
        dv = (DrawingView) findViewById(R.id.view);
        dv.clear();
    }


//    public class DrawingView extends View {
//
//        public int width;
//        public  int height;
//        private Bitmap mBitmap;
//        private Canvas mCanvas;
//        private Paint mBitmapPaint;
//        Context context;
//
//        private Paint mPaint;
//        private boolean[][] valueMatrix=new boolean[8][8];
//        private boolean[][] touched_piece=new boolean[8][8];
//
//
//        public DrawingView(Context c, AttributeSet attrs) {
//            super(c, attrs);
//            context=c;
//            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
//
//            mPaint = new Paint();
//
//            mPaint.setColor(Color.BLACK);
//            mPaint.setStyle(Paint.Style.STROKE);
//        }
//
//        @Override
//        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//            super.onSizeChanged(w, h, oldw, oldh);
//
//            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
//            mCanvas = new Canvas(mBitmap);
//
//            for(int i = 1; i < 9; i++){
//                for(int j = 1; j < 9; j++){
//                    mCanvas.drawRect(50+80*(i-1), 50+80*(j-1), 50+80*i, 50+80*j, mPaint);
//                }
//            }
//        }
//
//        @Override
//        protected void onDraw(Canvas canvas) {
//            super.onDraw(canvas);
//
//            canvas.drawBitmap( mBitmap, 0, 0, mBitmapPaint);
//        }
//
//        private void touch_move(int x, int y) {
//            x = x-50;
//            y = y-50;
//            if(x > 0 && y > 0 && x < 640 && y < 640){
//                int rect_x = x/80;
//                int rect_y = y/80;
//                if(touched_piece[rect_x][rect_y]) return;
//                if(valueMatrix[rect_x][rect_y]){
//                    mPaint.setColor(Color.WHITE);
//                    mCanvas.drawRect(50+80*(rect_x), 50+80*(rect_y), 50+80*(rect_x+1), 50+80*(rect_y+1), mPaint);
//                    mPaint.setColor(Color.BLACK);
//                    mPaint.setStyle(Paint.Style.STROKE);
//                    mCanvas.drawRect(50+80*(rect_x), 50+80*(rect_y), 50+80*(rect_x+1), 50+80*(rect_y+1), mPaint);
//                    mPaint.setStyle(Paint.Style.FILL);
//                    valueMatrix[rect_x][rect_y] = false;
//                } else{
//                    mPaint.setStyle(Paint.Style.FILL);
//                    mCanvas.drawRect(50+80*(rect_x), 50+80*(rect_y), 50+80*(rect_x+1), 50+80*(rect_y+1), mPaint);
//                    valueMatrix[rect_x][rect_y] = true;
//                }
//                touched_piece[rect_x][rect_y] = true;
//                // send line here
//            }
//        }
//
//        @Override
//        public boolean onTouchEvent(MotionEvent event) {
//            int x = Math.round(event.getX());
//            int y = Math.round(event.getY());
//
//            switch (event.getAction()) {
//                case MotionEvent.ACTION_DOWN:
//                    invalidate();
//                    break;
//                case MotionEvent.ACTION_MOVE:
//                    touch_move(x, y);
//                    invalidate();
//                    break;
//                case MotionEvent.ACTION_UP:
//                    for (boolean[] row: touched_piece)
//                        Arrays.fill(row, false);
//                    invalidate();
//                    break;
//            }
//            return true;
//        }
//    }







//    private boolean validTextflag = false;
//
//    public void setup() {
//        Button btnSend = (Button)findViewById(R.id.button_send);
//        btnSend.setOnClickListener(new View.OnClickListener(){
//            public void onClick(View v){
//                if(validTextflag){
//                    TextView textview = (TextView)findViewById(R.id.textHex);
//                    String t = textview.getText().toString();
//                    String tosend = "$0" +t +t +t +t +t +t +t +t;
////                    String tosend = "$0" + textview.getText() + "000000000000000000000";
//                    textview.setText(tosend);
//                    bt.send(tosend, false);
//                }
//            }
//        });
//
//        EditText input = (EditText)findViewById(R.id.number_input);
//        input.addTextChangedListener(new TextWatcher() {
//
//            public void afterTextChanged(Editable s) {}
//
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                TextView text = (TextView)findViewById(R.id.textHex);
////                    text.setText(s);
//                int number;
//                try {
//                    number = Integer.parseInt(s.toString());
//                } catch (Exception e) {
//                    number = 0;
//                }
//                if(number < 4096){
//                    String hex=Integer.toHexString(number);
//                    if(hex.length() == 1) hex = "0"+hex;
//                    if(hex.length() == 2) hex = "0"+hex;
//                    text.setText(hex);
//                    validTextflag = true;
//                } else{
//                    text.setText(R.string.notvalid);
//                    validTextflag = false;
//                }
//            }
//        });
//    }
}
