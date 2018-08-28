package ru.geekbrains.stargame.screen.pool;


import com.badlogic.gdx.audio.Sound;

import ru.geekbrains.stargame.base.SpritesPool;
import ru.geekbrains.stargame.math.Rect;
import ru.geekbrains.stargame.screen.gamescreen.Enemy;
import ru.geekbrains.stargame.screen.gamescreen.MainShip;

public class EnemyPool extends SpritesPool<Enemy> {

    private BulletPool bulletPool;
    private ExplosionPool explosionPool;
    private Rect worldBounds;
    private MainShip mainShip;
    private Sound sound;

    public EnemyPool(BulletPool bulletPool, ExplosionPool explosionPool, Rect worldBounds, MainShip mainShip, Sound sound) {
        this.bulletPool = bulletPool;
        this.explosionPool = explosionPool;
        this.worldBounds = worldBounds;
        this.mainShip = mainShip;
        this.sound = sound;
    }

    @Override
    protected Enemy newObject() {
        return new Enemy(bulletPool, explosionPool, sound, mainShip, worldBounds);
    }
}
