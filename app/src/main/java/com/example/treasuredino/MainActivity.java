package com.example.treasuredino;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    private FrameLayout mainLayout;
    private GameView gameView;
    private View startScreen, endScreen;
    private Button startButton, restartButton;
    private TextView scoreTextView;
    private MediaPlayer backgroundMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainLayout = findViewById(R.id.mainLayout);
        startScreen = findViewById(R.id.startScreen);
        endScreen = findViewById(R.id.endScreen);
        startButton = findViewById(R.id.startButton);
        restartButton = findViewById(R.id.restartButton);
        scoreTextView = findViewById(R.id.scoreTextView);

        startButton.setOnClickListener(v -> startGame());
        restartButton.setOnClickListener(v -> restartGame());

        backgroundMusic = MediaPlayer.create(this, R.raw.game);
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.02f, 0.02f);

        showStartScreen();
    }

    private void showStartScreen() {
        startScreen.setVisibility(View.VISIBLE);
        endScreen.setVisibility(View.INVISIBLE);
        backgroundMusic.start();
    }

    private void startGame() {
        startScreen.setVisibility(View.INVISIBLE);  // Hacer invisible la pantalla de inicio
        endScreen.setVisibility(View.INVISIBLE);    // Asegurarse de que la pantalla de fin esté oculta
        gameView = new GameView(this, this);
        mainLayout.addView(gameView);
    }

    public void endGame(int score) {
        gameView.stopGameThread();
        mainLayout.removeView(gameView);
        scoreTextView.setText("Puntos Totales: " + score);
        endScreen.setVisibility(View.VISIBLE);
    }

    private void restartGame() {
        endScreen.setVisibility(View.INVISIBLE);
        startGame();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (backgroundMusic != null) {
            backgroundMusic.stop();
            backgroundMusic.release();
        }
    }
}
