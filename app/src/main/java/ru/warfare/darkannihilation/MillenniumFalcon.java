package ru.warfare.darkannihilation;

public class MillenniumFalcon extends BaseCharacter {
    public MillenniumFalcon(Game g) {
        super(g, ImageHub.playerImage.getWidth(), ImageHub.playerImage.getHeight());
        health = 50;
        speedX = randInt(3, 7);
        speedY = randInt(3, 7);

        x = Game.halfScreenWidth;
        y = Game.halfScreenHeight;
        endX = x;
        endY = y;

        recreateRect(x + 20, y + 25, right() - 20, bottom() - 20);

        shootTime = 110;
        shotgunTime = 535;
        lastShoot = System.currentTimeMillis();
    }

//    @Override
//    public void PLAYER() {
//        baseSetting();
//
//        shootTime = 110;
//        shotgunTime = 535;
//        switch (Game.level)
//        {
//            case 1:
//                gun = "gun";
//                break;
//            case 2:
//                shootTime = 55;
//                shotgunTime = 335;
//                break;
//        }
//    }

    @Override
    public void shoot() {
        now = System.currentTimeMillis();
        if (gun.equals("shotgun")) {
            if (now - lastShoot > shotgunTime) {
                lastShoot = now;
                AudioHub.playShotgun();
                int centerX = centerX();
                for (int i = -4; i <= 4; i+=2) {
                    Buckshot buckshot = new Buckshot(centerX, y, i);
                    Game.bullets.add(buckshot);
                    Game.allSprites.add(buckshot);
                }
            }
        } else {
            if (now - lastShoot > shootTime) {
                lastShoot = now;
                AudioHub.playShoot();
                Bullet bullet = new Bullet(centerX() - 6, y);
                Game.bullets.add(bullet);
                Game.allSprites.add(bullet);

                bullet = new Bullet(centerX(), y);
                Game.bullets.add(bullet);
                Game.allSprites.add(bullet);
            }
        }
    }

    @Override
    public Sprite getRect() {
        return goTO(x + 20, y + 25);
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
        y += speedY;

        if (!ai) {
            speedX = (endX - x) / 5;
            speedY = (endY - y) / 5;
        } else {
            if (x < 30 | x > screenWidthWidth - 30) {
                speedX = -speedX;
            }
            if (y < 30 | y > screenHeightHeight - 30) {
                speedY = -speedY;
            }
        }
    }

    @Override
    public void render () {
        if (!ai) {
            renderHearts();
        }
        Game.canvas.drawBitmap(ImageHub.playerImage, x, y, null);
    }
}
