package ru.geekbrains.stargame.screen.pool;

import ru.geekbrains.stargame.base.SpritesPool;
import ru.geekbrains.stargame.screen.gamescreen.Bullet;

/**
 * Пул пуль
 */

public class BulletPool extends SpritesPool<Bullet> {

    @Override
    protected Bullet newObject() {
        return new Bullet();
    }

    @Override
    protected void debugLog() {
        System.out.println("active/free:" + activeObjects.size() + "/" + freeObjects.size());
    }
}
