package ru.warfare.darkannihilation;

public class XWing extends Sprite {
    private static final int shootTripleTime = 200;
    private long lastShoot;

    public XWing() {
        super(ImageHub.XWingImg.getWidth(), ImageHub.XWingImg.getHeight());
        health = 5;
        damage = 10;

        x = randInt(0, Game.screenWidth);
        y = -height;
        speedX = randInt(-3, 3);
        speedY = randInt(1, 8);

        recreateRect(x + 15, y + 15, x + width - 15, y + height - 15);

        lastShoot = System.currentTimeMillis();
    }

    public void shoot() {
        long now = System.currentTimeMillis();
        if (now - lastShoot > shootTripleTime) {
            if (HardThread.job == 0) {
                HardThread.x = centerX();
                HardThread.y = centerY();
                HardThread.job = 9;
                lastShoot = now;
            }
        }
    }

    public void newStatus() {
        if (Game.bosses.size() != 0) {
            lock = true;
        }
        health = 5;
        x = randInt(0, Game.screenWidth);
        y = -height;
        speedX = randInt(-3, 3);
        speedY = randInt(1, 8);

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
        createLargeTripleExplosion();
        AudioHub.playBoom();
        Game.score += 10;
        newStatus();
    }

    @Override
    public void intersectionPlayer() {
        AudioHub.playMetal();
        createSmallExplosion();
        newStatus();
    }

    @Override
    public void check_intersectionBullet(Sprite bullet) {
        if (getRect().intersect(bullet.getRect())) {
            health -= bullet.damage;
            bullet.intersection();
            if (health <= 0) {
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
        shoot();

        x += speedX;
        y += speedY;

        if (x < -width | x > Game.screenWidth | y > Game.screenHeight) {
            newStatus();
        }
    }

    @Override
    public void render() {
        Game.canvas.drawBitmap(ImageHub.XWingImg, x, y, Game.alphaPaint);
    }
}