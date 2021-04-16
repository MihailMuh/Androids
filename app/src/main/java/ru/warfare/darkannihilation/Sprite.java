package ru.warfare.darkannihilation;

import android.graphics.Rect;

public class Sprite {
    public int x = 0;
    public int y = 0;
    public int speedX = 0;
    public int speedY = 0;
    public int width;
    public int height;
    public int halfWidth;
    public int halfHeight;
    public final Game game;
    public boolean lock = false;
    public int health = 0;
    public int damage = 0;
    public int screenWidth;
    public int screenHeight;
    public int halfScreenWidth;
    public int halfScreenHeight;
    public double resizeK;
    public int numberSmallExplosionsTriple;
    public int numberLargeExplosions;
    public int numberMediumExplosionsTriple;
    public int numberSmallExplosionsDefault;
    public int numberMediumExplosionsDefault;
    public boolean isPassive = false;
    public boolean isBullet = false;

    public Sprite(Game g, int w, int h) {
        game = g;
        width = w;
        height = h;
        halfWidth = width / 2;
        halfHeight = height / 2;
        screenHeight = game.screenHeight;
        screenWidth = game.screenWidth;
        halfScreenWidth = game.halfScreenWidth;
        halfScreenHeight = game.halfScreenHeight;
        resizeK = game.resizeK;
        numberMediumExplosionsTriple = game.numberMediumExplosionsTriple;
        numberSmallExplosionsTriple = game.numberSmallExplosionsTriple;
        numberLargeExplosions = game.numberExplosionsALL;
        numberSmallExplosionsDefault = game.numberSmallExplosionsDefault;
        numberMediumExplosionsDefault = game.numberMediumExplosionsDefault;

    }

    public void check_intersectionBullet(BulletBase bullet) {}
    public void update() {}
    public void render() {}
    public void intersection() {}
    public void intersectionPlayer() {}

    public Rect getRect() {
        return new Rect(x, y, x + width, y + height);
    }

    public static int randInt(int min, int max) {
        max -= min;
        return (int) (Math.random() * ++max) + min;
    }
}
