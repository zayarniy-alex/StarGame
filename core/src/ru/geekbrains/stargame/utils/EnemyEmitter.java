package ru.geekbrains.stargame.utils;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import ru.geekbrains.stargame.math.Rect;
import ru.geekbrains.stargame.math.Rnd;
import ru.geekbrains.stargame.screen.gamescreen.Enemy;
import ru.geekbrains.stargame.screen.pool.EnemyPool;

public class EnemyEmitter {

    private static final float ENEMY_SMALL_HEIGHT = 0.1f;
    private static final float ENEMY_SMALL_BULLET_HEIGHT = 0.01f;
    private static final float ENEMY_SMALL_BULLET_VY = -0.3f;
    private static final int ENEMY_SMALL_BULLET_DAMAGE = 1;
    private static final float ENEMY_SMALL_RELOAD_INTERVAL = 3f;
    private static final int ENEMY_SMALL_HP = 1;

    private static final float ENEMY_MIDDLE_HEIGHT = 0.2f;
    private static final float ENEMY_MIDDLE_BULLET_HEIGHT = 0.02f;
    private static final float ENEMY_MIDDLE_BULLET_VY = -0.2f;
    private static final int ENEMY_MIDDLE_BULLET_DAMAGE = 5;
    private static final float ENEMY_MIDDLE_RELOAD_INTERVAL = 4f;
    private static final int ENEMY_MIDDLE_HP = 3;

    private static final float ENEMY_BIG_HEIGHT = 0.3f;
    private static final float ENEMY_BIG_BULLET_HEIGHT = 0.03f;
    private static final float ENEMY_BIG_BULLET_VY = -0.1f;
    private static final int ENEMY_BIG_BULLET_DAMAGE = 10;
    private static final float ENEMY_BIG_RELOAD_INTERVAL = 5f;
    private static final int ENEMY_BIG_HP = 5;

    private Rect worldBounds;


    private float generateInterval = 20f;
    private float generateTimer;

    private TextureRegion[] enemySmallRegion;
    private TextureRegion[] enemyMiddleRegion;
    private TextureRegion[] enemyBigRegion;

    private Vector2 enemySmallV = new Vector2(0f, -0.2f);
    private Vector2 enemyMiddleV = new Vector2(0f, -0.1f);
    private Vector2 enemyBigV = new Vector2(0f, -0.05f);

    private TextureRegion bulletRegion;

    private EnemyPool enemyPool;

    public EnemyEmitter(TextureAtlas atlas, Rect worldBounds, EnemyPool enemyPool) {
        this.worldBounds = worldBounds;
        this.enemyPool = enemyPool;

        TextureRegion textureRegion0 = atlas.findRegion("enemy0");
        this.enemySmallRegion = Regions.split(textureRegion0, 1, 2,2);
        TextureRegion textureRegion1 = atlas.findRegion("enemy1");
        this.enemyMiddleRegion = Regions.split(textureRegion1, 1, 2,2);
        TextureRegion textureRegion2 = atlas.findRegion("enemy2");
        this.enemyBigRegion = Regions.split(textureRegion2, 1, 2,2);
        this.bulletRegion = atlas.findRegion("bulletEnemy");
    }

    public void generateSmallEnemies(float delta) {
        generateTimer += delta;
        if (generateTimer >= generateInterval) {
            generateTimer = 0f;
            Enemy enemy = enemyPool.obtain();
            enemy.set(
                    enemySmallRegion,
                    enemySmallV,
                    bulletRegion,
                    ENEMY_SMALL_BULLET_HEIGHT,
                    ENEMY_SMALL_BULLET_VY,
                    ENEMY_SMALL_BULLET_DAMAGE,
                    ENEMY_SMALL_RELOAD_INTERVAL,
                    ENEMY_SMALL_HEIGHT,
                    ENEMY_SMALL_HP
            );
            enemy.pos.x = Rnd.nextFloat(worldBounds.getLeft() + enemy.getHalfWidth(), worldBounds.getRight() - enemy.getHalfWidth());
            enemy.setBottom(worldBounds.getTop());
        }
    }
    public void generateMiddleEnemies(float delta){
        generateTimer += delta;
        if (generateTimer >= generateInterval) {
            generateTimer = 0f;
            Enemy enemy = enemyPool.obtain();
            enemy.set(enemyMiddleRegion,
                    enemyMiddleV,
                    bulletRegion,
                    ENEMY_MIDDLE_BULLET_HEIGHT,
                    ENEMY_MIDDLE_BULLET_VY,
                    ENEMY_MIDDLE_BULLET_DAMAGE,
                    ENEMY_MIDDLE_RELOAD_INTERVAL,
                    ENEMY_MIDDLE_HEIGHT,
                    ENEMY_MIDDLE_HP
                    );
            enemy.pos.x = Rnd.nextFloat(worldBounds.getLeft() + enemy.getHalfWidth(), worldBounds.getRight() - enemy.getHalfWidth());
            enemy.setBottom(worldBounds.getTop());
        }
    }
    public void generateBigEnemies(float delta){
        generateTimer += delta;
        if (generateTimer >= generateInterval) {
            generateTimer = 0f;
            Enemy enemy = enemyPool.obtain();
            enemy.set(enemyBigRegion,
                    enemyBigV,
                    bulletRegion,
                    ENEMY_BIG_BULLET_HEIGHT,
                    ENEMY_BIG_BULLET_VY,
                    ENEMY_BIG_BULLET_DAMAGE,
                    ENEMY_BIG_RELOAD_INTERVAL,
                    ENEMY_BIG_HEIGHT,
                    ENEMY_BIG_HP
            );
            enemy.pos.x = Rnd.nextFloat(worldBounds.getLeft() + enemy.getHalfWidth(), worldBounds.getRight() - enemy.getHalfWidth());
            enemy.setBottom(worldBounds.getTop());
        }
    }
}
