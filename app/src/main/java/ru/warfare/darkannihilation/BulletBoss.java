package ru.warfare.darkannihilation;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class BulletBoss extends Sprite {
    private Bitmap img;

    public BulletBoss(Game g, int X, int Y, int type) {
        super(g, ImageHub.laserImage.getWidth(), ImageHub.laserImage.getHeight());
        speedY = 6;
        damage = 5;
        status = "bulletEnemy";
        isBullet = true;

        Matrix matrix = new Matrix();
        switch (type)
        {
            case 1:
                img = ImageHub.laserImage;
                break;
            case 2:
                speedX = 4;
                matrix.postRotate(30);
                img = Bitmap.createBitmap(ImageHub.laserImage, 0, 0, width, height, matrix, ImageHub.isFilter);
                break;
            case 3:
                speedX = -4;
                matrix.postRotate(330);
                img = Bitmap.createBitmap(ImageHub.laserImage, 0, 0, width, height, matrix, ImageHub.isFilter);
                break;
        }

        x = X;
        y = Y;
    }

    @Override
    public void intersectionPlayer() {
        for (int i = numberMediumExplosionsTriple; i < numberSmallExplosionsTriple; i++) {
            if (game.allExplosions[i].lock) {
                game.allExplosions[i].start(x + halfWidth, y + halfHeight);
                break;
            }
        }
        game.allSprites.remove(this);
    }

    @Override
    public void update() {
        y += speedY;
        x -= speedX;

        if (y > screenHeight | x < -100 | x > screenWidth) {
            game.allSprites.remove(this);
        }
    }

    @Override
    public void render () {
        game.canvas.drawBitmap(img, x, y, null);
    }
}
