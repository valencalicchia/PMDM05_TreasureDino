package com.example.treasuredino;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Random;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private GameThread thread;
    private MainActivity activity;
    private float dinoX, dinoY, dinoWidth, dinoHeight;
    private Bitmap dino, ground, background;
    private float screenWidth, screenHeight;
    private int score = 0;
    private ArrayList<FallingObject> fallingObjects;
    private boolean gameRunning = true;


    public GameView(Context context, MainActivity activity) {
        super(context);
        this.activity = activity;

        getHolder().addCallback(this);
        thread = new GameThread(getHolder(), this);
        fallingObjects = new ArrayList<>();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        screenWidth = getWidth();
        screenHeight = getHeight();

        setZOrderOnTop(true); // 🔹 Poner SurfaceView sobre todo
        getHolder().setFormat(PixelFormat.TRANSPARENT);

        ground = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.suelo), (int) screenWidth, (int) screenHeight, false);
        background = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.fondo), (int) screenWidth, (int) screenHeight, false);

        // Escalar dinosaurio
        Bitmap dinoOriginal = BitmapFactory.decodeResource(getResources(), R.drawable.spinosaurus_x);
        dinoWidth = screenWidth * 0.20f;
        dinoHeight = screenHeight * 0.10f;
        dino = Bitmap.createScaledBitmap(dinoOriginal, (int) dinoWidth, (int) dinoHeight, false);

        // Posición inicial en el centro
        dinoX = (screenWidth - dinoWidth) / 2;
        dinoY = ground.getHeight() - dinoHeight * 1.5f;


        for (int i = 0; i < 7; i++) {
            fallingObjects.add(new FallingObject(getResources(), screenWidth, screenHeight));
        }

        startGameTimer();
        thread.setRunning(true);
        if (thread.getState() == Thread.State.NEW) {
            thread.start();
        }
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        stopGameThread();
    }

    public void update() {
        if (gameRunning) {
            for (FallingObject obj : fallingObjects) {
                obj.update();
                if (obj.y + obj.height >= dinoY && obj.y <= dinoY + dinoHeight &&
                        obj.x + obj.width >= dinoX && obj.x <= dinoX + dinoWidth) {
                    score += obj.getValue();
                    obj.reset();
                }
                if (obj.y > screenHeight) {
                    obj.reset();
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gameRunning && event.getAction() == MotionEvent.ACTION_MOVE) {
            dinoX = event.getX() - (dinoWidth / 2);
            if (dinoX < 0) dinoX = 0;
            if (dinoX + dinoWidth > screenWidth) dinoX = screenWidth - dinoWidth;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (canvas != null) {
            canvas.drawBitmap(background, 0, 0, null);
            canvas.drawBitmap(dino, dinoX, dinoY, null);
            // Dibujar objetos cayendo
            for (FallingObject obj : fallingObjects) {
                obj.draw(canvas);
            }

            // Dibujar el puntaje
            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            paint.setTextSize(80);
            canvas.drawText("Puntos: " + score, 50, 100, paint);
        }
    }

    private void startGameTimer() {
        new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                gameRunning = false;
                thread.setRunning(false);
                activity.runOnUiThread(() -> activity.endGame(score)); // Notifica a MainActivity
            }
        }.start();
    }

    public void stopGameThread() {
        thread.setRunning(false);
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
