package ru.warfare.darkannihilation.enemy;

import ru.warfare.darkannihilation.HardThread;
import ru.warfare.darkannihilation.base.Sprite;
import ru.warfare.darkannihilation.hub.ImageHub;
import ru.warfare.darkannihilation.math.Math;
import ru.warfare.darkannihilation.systemd.Game;

import static ru.warfare.darkannihilation.Constants.BUFFER_DAMAGE;
import static ru.warfare.darkannihilation.Constants.BUFFER_HEALTH;

public class Buffer extends Sprite {
    private boolean startBuff;
    private boolean BOOM;

    public Buffer() {
        super(ImageHub.bufferImg);
        damage = BUFFER_DAMAGE;

        hide();

        recreateRect(x + 70, y + 70, right() - 70, bottom() - 35);
    }

    private void boom() {
        if (!BOOM) {
            BOOM = true;
            HardThread.newJob(() -> {
                if (startBuff) {
                    for (int i = 0; i < Game.allSprites.size(); i++) {
                        Sprite sprite = Game.allSprites.get(i);
                        if (!sprite.isPassive && !sprite.isBullet) {
                            sprite.sB();
                        }
                    }
                }
                inter();
            });
        }
    }

    private void inter() {
        createSkullExplosion();
        hide();
        Game.score += 100;
    }

    public void hide() {
        BOOM = false;
        startBuff = false;
        lock = true;
        health = BUFFER_HEALTH;
        x = Math.randInt(0, screenWidthWidth);
        y = -height;
        speedY = Math.randInt(5, 10);
    }

    @Override
    public Sprite getRect() {
        return newRect(x + 70, y + 70);
    }

    @Override
    public void intersection() {
        health = 0;
    }

    @Override
    public void intersectionPlayer() {
        health = 0;
    }

    @Override
    public void check_intersectionBullet(Sprite bullet) {
        if (intersect(bullet)) {
            bullet.intersection();
            health -= bullet.damage;
        }
    }

    @Override
    public void update() {
        if (y < 35) {
            y += speedY;
        } else {
            if (!startBuff) {
                startBuff = true;
                HardThread.newJob(() -> {
                    for (int i = 0; i < Game.allSprites.size(); i++) {
                        Sprite sprite = Game.allSprites.get(i);
                        if ((!sprite.isPassive && !sprite.isBullet)) {
                            sprite.buff();
                        }
                    }
                });
            }
        }

        if (health <= 0) {
            boom();
        }
    }

    @Override
    public void render() {
        Game.canvas.drawBitmap(image, x, y, Game.alphaEnemy);

        Game.canvas.drawRect(centerX() - 75, y + 50, centerX() + 75, y + 65, Game.scorePaint);
        Game.canvas.drawRect(centerX() - 73, y + 52, centerX() - 77 + (health / (float) BUFFER_HEALTH) * 150, y + 63, Game.fpsPaint);
    }
}

