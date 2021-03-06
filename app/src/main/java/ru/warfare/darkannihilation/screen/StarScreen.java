package ru.warfare.darkannihilation.screen;

import ru.warfare.darkannihilation.hub.ImageHub;
import ru.warfare.darkannihilation.base.BaseScreen;

import static ru.warfare.darkannihilation.Constants.NUMBER_STAR_SCREEN_IMAGES;

public class StarScreen extends BaseScreen {
    public StarScreen() {
        super(ImageHub.screenImage);
    }

    @Override
    public void update() {
        frame++;
        if (frame == NUMBER_STAR_SCREEN_IMAGES) {
            frame = 0;
        }
    }
}
