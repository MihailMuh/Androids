package ru.warfare.darkannihilation;

public class HealthKit extends Sprite {
    public HealthKit(Game g) {
        super(g, ImageHub.healthKitImg.getWidth(), ImageHub.healthKitImg.getHeight());
        speedY = 2;
        lock = true;
        isBullet = true;

        x = randInt(0, Game.screenWidth);
        y = -height;
    }

    public void hide() {
        x = randInt(0, Game.screenWidth);
        y = -height;
        lock = true;
    }

    @Override
    public void intersectionPlayer() {
        hide();
        AudioHub.playHealSnd();
        game.player.heal();
    }

    @Override
    public void update() {
        y += speedY;

        if (y > Game.screenHeight) {
            hide();
        }
    }

    @Override
    public void render () {
        Game.canvas.drawBitmap(ImageHub.healthKitImg, x, y, null);
    }
}
