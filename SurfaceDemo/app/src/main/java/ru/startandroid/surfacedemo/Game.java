package ru.startandroid.surfacedemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Random;

public class Game extends SurfaceView implements Runnable, SurfaceHolder.Callback {
    public static SurfaceHolder holder;
    public static Thread thread;
    public Canvas canvas;
    public Context context;

    public int screenWidth;
    public int screenHeight;
    private static final Paint fpsPaint = new Paint();
    private static final Paint startPaint = new Paint();
    private static final Paint gameoverPaint = new Paint();
    private static final Paint scorePaint = new Paint();
    public volatile boolean playing = false;
    public int fps;
    private static final int MILLIS_IN_SECOND = 1_000_000_000;
    private long timeFrame;
    public Player player;
    public AI ai;
    public Vader[] vaders = new Vader[10];
    public ArrayList<Bullet> bullets = new ArrayList<>(0);
    public ArrayList<TripleFighter> tripleFighters = new ArrayList<>(0);
    public ArrayList<BulletEnemy> bulletEnemies = new ArrayList<>(0);
    public Heart[] hearts = new Heart[5];
    public Screen screen;
    public Button buttonStart;
    public Button buttonQuit;
    public int vaderNumbers = vaders.length;
    public int numberBullets = 0;
    public int numberTripleFighters;
    public int numberBulletsEnemy = 0;
    public int gameStatus = 1;
    public int count = 0;
    public int score = 0;
    public int lastMax = 0;
    public AudioPlayer audioPlayer;
    public int pointerCount;
    public static final Random random = new Random();
    public StringBuilder scoreBuilder = new StringBuilder();

    public Game(Context cont, AttributeSet attrs) {
        super(cont, attrs);
        context = cont;
    }

    public void initGame(int width, int height) {
        audioPlayer = new AudioPlayer(this);

        screenWidth = width;
        screenHeight = height;

        holder = getHolder();

        fpsPaint.setColor(Color.RED);
        fpsPaint.setTextSize(40);
        startPaint.setColor(Color.WHITE);
        startPaint.setTextSize(300);
        gameoverPaint.setColor(Color.WHITE);
        gameoverPaint.setTextSize(50);
        scorePaint.setColor(Color.WHITE);
        scorePaint.setTextSize(40);

        screen = new Screen(this);
        ai = new AI(this);
        vaderNumbers *= 2;
        vaders = new Vader[vaderNumbers];
        for (int i = 0; i < vaderNumbers; i++) {
            vaders[i] = new Vader(this);
        }
        buttonStart = new Button(this, "Start", screenWidth / 2, screenHeight - 70, "start");
        buttonQuit = new Button(this, "Quit", screenWidth / 2 - 300, screenHeight - 70, "quit");

        audioPlayer.menuMusic.start();
    }

    public void gameplay() {
        timeFrame = System.nanoTime();

        if (holder.getSurface().isValid()) {
            canvas = holder.lockCanvas();

            screen.update();
            screen.x -= player.speedX / 3;

            for (int i = 0; i < numberBullets; i++) {
                bullets.get(i).x -= player.speedX / 3;
                bullets.get(i).update();
                if (bullets.get(i).y < -50) {
                    bullets.get(i).bulletImage.recycle();
                    bullets.remove(i);
                    numberBullets -= 1;
                }
            }

            for (int i = 0; i < vaderNumbers; i++) {
                for (int j = 0; j < numberBullets; j++) {
                    vaders[i].check_intersectionBullet(bullets.get(j));
                }
                vaders[i].check_intersectionPlayer();
                vaders[i].x -= player.speedX / 3;
                vaders[i].update();
            }

            for (int i = 0; i < numberTripleFighters; i++) {
                for (int j = 0; j < numberBullets; j++) {
                    tripleFighters.get(i).check_intersectionBullet(bullets.get(j));
                }
                tripleFighters.get(i).check_intersectionPlayer();
                tripleFighters.get(i).x -= player.speedX / 3;
                tripleFighters.get(i).update();
            }

            for (int i = 0; i < bulletEnemies.size(); i++) {
                bulletEnemies.get(i).x -= player.speedX / 3;
                bulletEnemies.get(i).update();
                if (bulletEnemies.get(i).y > screenHeight | bulletEnemies.get(i).x < -100 | bulletEnemies.get(i).x > screenWidth) {
                    bulletEnemies.get(i).bulletImage.recycle();
                    bulletEnemies.remove(i);
                    numberBulletsEnemy -= 1;
                } else {
                    player.check_intersectionBullet(bulletEnemies.get(i));
                }
            }

            player.update();
            if (player.health <= 0) {
                if (score > lastMax) {
                    scoreBuilder.append(" ").append(score);
                }

                audioPlayer.gameoverSnd.start();
                screen.gameover = true;
                gameStatus = 3;
            }
            if (gameStatus == 2) {
                timerStart();
            }

            fps = (int) (MILLIS_IN_SECOND / (System.nanoTime() - timeFrame));
            canvas.drawText("FPS: " + String.valueOf(fps), screenWidth - 250, 50, fpsPaint);
            canvas.drawText("Current score: " + String.valueOf(score), screenWidth / 2 - 200, 50, scorePaint);
            canvas.drawText("Max score: " + String.valueOf(lastMax), screenWidth / 2 - 170, 150, scorePaint);

            holder.unlockCanvasAndPost(canvas);
        }
        timeFrame = System.nanoTime();
    }

    @Override
    public void run() {
        while(playing) {
            switch (gameStatus)
            {
                case 1:
                    preview();
                    break;
                case 2:
                case 0:
                    gameplay();
                    break;
                case 3:
                    gameover();
                    break;
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        pointerCount = event.getPointerCount();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                if (buttonStart != null) {
                    buttonStart.mouseX = (int) event.getX();
                    buttonStart.mouseY = (int) event.getY();
                    buttonQuit.mouseX = (int) event.getX();
                    buttonQuit.mouseY = (int) event.getY();
                }
                if (player != null) {
                    player.endX = (int) (event.getX() - player.width / 2);
                    player.endY = (int) (event.getY() - player.height / 2);
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (player != null) {
                    player.endX = (int) (event.getX() - player.width / 2);
                    player.endY = (int) (event.getY() - player.height / 2);
                }
                break;
            }
//            case MotionEvent.ACTION_UP: {
//                break;
//            }
//            case MotionEvent.ACTION_CANCEL: {
//                break;
//            }
//            case MotionEvent.ACTION_POINTER_UP: {
//                break;
//            }
        }
        return true;
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {}

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder){}

    public void pause() {
        playing = false;
        if (gameStatus == 1) {
            audioPlayer.menuMusic.pause();
        } else {
            audioPlayer.pirateMusic.pause();
        }
        try {
            thread.join();
        } catch(Exception e) {
            Log.e("Error ", "Thread join " + e);
        }
    }

    public void resume() {
        checkMaxScore();
        thread = new Thread(this);
        thread.start();
        playing = true;
        if (gameStatus == 1) {
            audioPlayer.menuMusic.seekTo(0);
            audioPlayer.menuMusic.start();
        } else {
            audioPlayer.pirateMusic.seekTo(0);
            audioPlayer.pirateMusic.start();
        }
    }


    public void generateNewGame() {
        saveScore();
        checkMaxScore();
        count = 0;
        score = 0;
        screen.gameover = false;
        ai = null;
        player = new Player(this);
        player.lock = true;
        vaders = new Vader[12];
        tripleFighters = new ArrayList<>(0);
        vaderNumbers = 12;
        for (int i = 0; i < 12; i++) {
            if (random.nextFloat() <= 0.15) {
                tripleFighters.add(new TripleFighter(this));
            }
            vaders[i] = new Vader(this);
            vaders[i].lock = true;
        }
        numberTripleFighters = tripleFighters.size();
        bullets = new ArrayList<>(0);
        bulletEnemies = new ArrayList<>(0);
        numberBullets = 0;
        buttonStart = null;
        int c = 370;
        for (int i = 0; i < 5; i++) {
            Heart heart = new Heart(this, c, 10);
            hearts[i] = heart;
            c -= 90;
        }

        if (gameStatus == 1) {
            audioPlayer.menuMusic.pause();
        } else {
            audioPlayer.pirateMusic.pause();
        }
        audioPlayer.pirateMusic.seekTo(0);
        audioPlayer.pirateMusic.start();

        gameStatus = 2;
        audioPlayer.readySnd.start();
    }

    public void gameover() {
        timeFrame = System.nanoTime();

        if (holder.getSurface().isValid()) {
            canvas = holder.lockCanvas();

            screen.update();

            if (pointerCount >= 4) {
                generateNewGame();
            }

            canvas.drawText("Tap this screen with four or more fingers to restart",
                    (screenWidth - gameoverPaint.measureText("Tap this screen with four or more fingers to restart")) / 2,
                    (float) (screenHeight * 0.7), gameoverPaint);

            fps = (int) (MILLIS_IN_SECOND / (System.nanoTime() - timeFrame));
            canvas.drawText("FPS: " + String.valueOf(fps), screenWidth - 250, 50, fpsPaint);
            canvas.drawText("Current score: " + String.valueOf(score), screenWidth / 2 - 200, 50, scorePaint);
            canvas.drawText("Max score: " + String.valueOf(lastMax), screenWidth / 2 - 170, 150, scorePaint);

            holder.unlockCanvasAndPost(canvas);
        }
        timeFrame = System.nanoTime();
    }

    public void timerStart() {
        count += 1;
        if (0 <= count & count < 70) {
            canvas.drawText("1", screenWidth / 2 - startPaint.measureText("1") / 2, screenHeight / 2 + startPaint.getTextSize() / 2, startPaint);
        } else {
            if (70 <= count & count < 140) {
                canvas.drawText("2", screenWidth / 2 - startPaint.measureText("2") / 2, screenHeight / 2 + startPaint.getTextSize() / 2, startPaint);
            } else {
                if (140 <= count & count < 210) {
                    canvas.drawText("3", screenWidth / 2 - startPaint.measureText("3") / 2, screenHeight / 2 + startPaint.getTextSize() / 2, startPaint);
                } else {
                    if (210 <= count & count < 280) {
                        canvas.drawText("SHOOT!", screenWidth / 2 - startPaint.measureText("SHOOT!") / 2, screenHeight / 2 + startPaint.getTextSize() / 2, startPaint);
                    } else {
                        if (count >= 280) {
                            gameStatus = 0;
                            for (int i = 0; i < vaderNumbers; i++) {
                                vaders[i].lock = false;
                            }
                            for (int i = 0; i < numberTripleFighters; i++) {
                                tripleFighters.get(i).lock = false;
                            }
                            player.lock = false;
                        }
                    }
                }
            }
        }
    }

    public void preview() {
        timeFrame = System.nanoTime();
        if (holder.getSurface().isValid()) {
            canvas = holder.lockCanvas();
            screen.update();
            ai.update();

            for (int i = 0; i < numberBullets; i++) {
                bullets.get(i).update();
                if (bullets.get(i).y < -50) {
                    bullets.get(i).bulletImage.recycle();
                    bullets.remove(i);
                    numberBullets -= 1;
                }
            }

            for (int i = 0; i < vaderNumbers; i++) {
                for (int j = 0; j < numberBullets; j++) {
                    vaders[i].check_intersectionBullet(bullets.get(j));
                }
                vaders[i].check_intersectionAI();
                vaders[i].update();
            }

            buttonStart.update();
            buttonQuit.update();

            fps = (int) (MILLIS_IN_SECOND / (System.nanoTime() - timeFrame));
            canvas.drawText("FPS: " + String.valueOf(fps), screenWidth - 250, 50, fpsPaint);
            canvas.drawText("Max score: " + String.valueOf(lastMax), screenWidth / 2 - 170, 50, scorePaint);

            holder.unlockCanvasAndPost(canvas);
        }
        timeFrame = System.nanoTime();
    }


    public void saveScore() {
//        Log.e("s", "s");
        if (scoreBuilder.length() > 0) {
            try {
                FileOutputStream writer = context.getApplicationContext().openFileOutput("SCORE.txt", Context.MODE_PRIVATE);
                OutputStreamWriter writer_str = new OutputStreamWriter(writer);

                writer_str.write(scoreBuilder.toString());
                writer_str.close();
            } catch (IOException e) {
                Log.e("Error ", "Can't save SCORE " + e);
            }
        }
    }

    public void checkMaxScore() {
        try {
            InputStreamReader reader_cooler = new InputStreamReader(context.getApplicationContext().openFileInput("SCORE.txt"));
            BufferedReader reader_buffer = new BufferedReader(reader_cooler);
            String line;
            StringBuilder builder = new StringBuilder();

            while ((line = reader_buffer.readLine()) != null) {
                builder.append(line);
            }
            String[] scoreList = builder.toString().split(" ");
            for (int i = 1; i < scoreList.length; i++) {
                int j = Integer.parseInt(scoreList[i]);
                if (j > lastMax) {
                    lastMax = j;
                }
            }
//            Log.e("Recovery", builder.toString() + " max: " + lastMax);

            reader_cooler.close();
        } catch (IOException e) {
            Log.e("Error ", "Can't recovery SCORE: " + e);
        }
    }
}
