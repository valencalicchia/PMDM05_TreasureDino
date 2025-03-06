package com.example.treasuredino;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import java.util.Random;

public class FallingObject {
    private Bitmap object;
    private float screenWidth, screenHeight;
    private Random random;

    public float x, y, width, height;
    private float speed;
    private int value; // Puntos que otorga el objeto

    // Tipos de objetos con valores y probabilidades
    private static final int[][] OBJECTS = {
            {R.drawable.shell, 1, 40},  // Común
            {R.drawable.apple, 2, 20},  // Menos común
            {R.drawable.flor, 3, 10},  // Raro
            {R.drawable.muslo, 5, 4},   // Muy raro
            {R.drawable.golden_steak, 10, 2},   // Extremadamente raro

            // Cosas malas
            {R.drawable.espina, -5, 15},
            {R.drawable. meteorito, -1000, 2}
    };

    public FallingObject(Resources res, float screenWidth, float screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.random = new Random();

        selectObject(res);
        reset();
    }

    private void selectObject(Resources res) {
        int totalWeight = 0;
        for (int[] obj : OBJECTS) {
            totalWeight += obj[2]; // Suma de probabilidades
        }

        int rand = random.nextInt(totalWeight);
        int sum = 0;
        for (int[] obj : OBJECTS) {
            sum += obj[2];
            if (rand < sum) {
                object = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, obj[0]), (int) (screenWidth * 0.07) , (int) (screenHeight * 0.05), false);
                width = object.getWidth();
                height = object.getHeight();
                value = obj[1];
                break;
            }
        }
    }

    public void update() {
        y += speed;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(object, x, y, null);
    }

    public void reset() {
        x = random.nextInt((int) (screenWidth - width));
        y = -height;
        speed = screenHeight * 0.005f + random.nextInt(10);
    }

    public int getValue() {
        return value;
    }
}
