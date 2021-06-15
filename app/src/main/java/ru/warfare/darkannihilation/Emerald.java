package ru.warfare.darkannihilation;

public class Emerald extends BaseCharacter {
    public Emerald(Game g) {
        super(g, ImageHub.emeraldImg.getWidth(), ImageHub.emeraldImg.getHeight());
        health = 50;

        recreateRect(x + 25, y + 25, right() - 25, bottom() - 25);

        shootTime = 1_700;
        shotgunTime = 65;
        lastShoot = System.currentTimeMillis();
    }

    @Override
    public void shoot() {
        now = System.currentTimeMillis();
        if (gun.equals("shotgun")) {
            if (now - lastShoot > shotgunTime) {
                lastShoot = now;
                BuckshotSaturn buckshotSaturn = new BuckshotSaturn(game, centerX(), y);
                Game.bullets.add(buckshotSaturn);
                Game.allSprites.add(buckshotSaturn);
            }
        } else {
            if (now - lastShoot > shootTime) {
                lastShoot = now;
                AudioHub.playDynamite();
                BulletDynamite bulletDynamite = new BulletDynamite(centerX(), y);
                Game.bullets.add(bulletDynamite);
                Game.allSprites.add(bulletDynamite);
            }
        }
    }

    @Override
    public Sprite getRect() {
        return goTO(x + 25, y + 25);
    }

    @Override
    public void checkIntersections(Sprite sprite) {
        if (getRect().intersect(sprite.getRect())) {
            damage(sprite.damage);
            sprite.intersectionPlayer();
        }
    }

    @Override
    public void update() {
        if (!lock) {
            shoot();
        }

        x += speedX;
        y += (endY - y) / 12;

        speedX = (endX - x) / 12;
    }

    @Override
    public void render () {
        renderHearts();
        Game.canvas.drawBitmap(ImageHub.emeraldImg, x, y, null);
    }
}
