package ru.warfare.darkannihilation;

import android.graphics.Bitmap;

public class Vader extends Sprite {
    private Bitmap img;

    public Vader() {
        super(ImageHub.vaderSize, ImageHub.vaderSize);
        health = 2;
        damage = 5;

        switch (Game.level)
        {
            case 1:
                img = ImageHub.vaderImage[randInt(0, 2)];
                break;
            case 2:
                img = ImageHub.vaderOldImage[randInt(0, 2)];
                break;
        }

        x = randInt(0, Game.screenWidth);
        y = -150;
        speedX = randInt(-5, 5);
        speedY = randInt(3, 10);

        recreateRect(x + 15, y + 15, right() - 15, bottom() - 15);
    }

    public void newStatus() {
        if (Game.bosses.size() != 0) {
            lock = true;
        }
        health = 2;
        x = randInt(0, Game.screenWidth);
        y = -height;
        speedX = randInt(-5, 5);
        speedY = randInt(3, 10);

        if (buff) {
            up();
        }
    }

    private void up() {
        speedX *= 2;
        speedY *= 2;
    }

    @Override
    public void buff() {
        buff = true;
        up();
    }

    @Override
    public void stopBuff() {
        speedX /= 2;
        speedY /= 2;
        buff = false;
    }

    @Override
    public Sprite getRect() {
        return goTO(x + 15, y + 15);
    }

    @Override
    public void intersection() {
        createLargeExplosion();
        AudioPlayer.playBoom();
        if (Game.gameStatus == 0) {
            Game.score += 1;
        }
        newStatus();
    }

    @Override
    public void intersectionPlayer() {
        AudioPlayer.playMetal();
        createSmallExplosion();
        AudioPlayer.playBoom();
        newStatus();
    }

    @Override
    public void check_intersectionBullet(Sprite bullet) {
        if (getRect().intersect(bullet.getRect())) {
            if (bullet.damage < health) {
                health -= bullet.damage;
                bullet.intersection();
                if (health <= 0) {
                    intersection();
                }
            } else {
                intersection();
            }
        }
    }

    @Override
    public void empireStart() {
        lock = false;
    }

    @Override
    public void update() {
        x += speedX;
        y += speedY;

        if (x < -width | x > Game.screenWidth | y > Game.screenHeight) {
            newStatus();
        }
    }

    @Override
    public void render() {
        Game.canvas.drawBitmap(img, x, y, Game.alphaPaint);
    }
}
