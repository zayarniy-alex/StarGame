package ru.geekbrains.stargame.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;

import java.util.List;

import ru.geekbrains.stargame.base.ActionListener;
import ru.geekbrains.stargame.base.Base2DScreen;
import ru.geekbrains.stargame.base.Font;
import ru.geekbrains.stargame.math.Rect;
import ru.geekbrains.stargame.screen.gamescreen.Bullet;
import ru.geekbrains.stargame.screen.gamescreen.ButtonNewGame;
import ru.geekbrains.stargame.screen.gamescreen.Enemy;
import ru.geekbrains.stargame.screen.gamescreen.MainShip;
import ru.geekbrains.stargame.screen.gamescreen.MessageGameOver;
import ru.geekbrains.stargame.screen.gamescreen.TrackingStar;
import ru.geekbrains.stargame.screen.pool.BulletPool;
import ru.geekbrains.stargame.screen.pool.EnemyPool;
import ru.geekbrains.stargame.screen.pool.ExplosionPool;
import ru.geekbrains.stargame.screen.sprites.Background;
import ru.geekbrains.stargame.screen.sprites.Star;
import ru.geekbrains.stargame.utils.EnemyEmitter;


public class GameScreen extends Base2DScreen implements ActionListener {

    private enum State {PLAYING, GAME_OVER}

    private static final int STAR_COUNT = 56;
    private static final float FONT_SIZE = 0.03f;

    private static final String FRAGS = "Frags: ";
    private static final String HP = "HP: ";
    private static final String LEVEL = "Level: ";

    private Background background;
    private Texture bgTexture;
    private TextureAtlas atlas;

    private TrackingStar star[];
    private MainShip mainShip;

    private BulletPool bulletPool = new BulletPool();

    private Music music;
    private Sound bulletSound;
    private Sound laserSound;
    private Sound explosionSound;
    private EnemyPool enemyPool;
    private ExplosionPool explosionPool;

    private EnemyEmitter enemyEmitter;

    private State state;

    private MessageGameOver messageGameOver;
    private ButtonNewGame buttonNewGame;

    private Font font;
    private StringBuilder sbFrags = new StringBuilder();
    private StringBuilder sbHP = new StringBuilder();
    private StringBuilder sbLevel = new StringBuilder();

    int frags;

    public GameScreen(Game game) {
        super(game);
    }

    @Override
    public void show() {
        super.show();
        music = Gdx.audio.newMusic(Gdx.files.internal("sounds/game.mp3"));
        music.setLooping(true);
        music.play();
        bgTexture = new Texture("textures/bg.png");
        background = new Background(new TextureRegion(bgTexture));
        atlas = new TextureAtlas("textures/mainAtlas.tpack");
        star = new TrackingStar[STAR_COUNT];
        bulletSound = Gdx.audio.newSound(Gdx.files.internal("sounds/bulletEnemy.wav"));
        laserSound = Gdx.audio.newSound(Gdx.files.internal("sounds/bulletHero.wav"));
        explosionSound = Gdx.audio.newSound(Gdx.files.internal("sounds/explosion.wav"));
        explosionPool = new ExplosionPool(atlas, explosionSound);
        mainShip = new MainShip(atlas, bulletPool, laserSound, explosionPool);
        enemyPool = new EnemyPool(bulletPool, explosionPool, worldBounds, mainShip, bulletSound);
        enemyEmitter = new EnemyEmitter(atlas, worldBounds, enemyPool);
        messageGameOver = new MessageGameOver(atlas);
        buttonNewGame = new ButtonNewGame(atlas, this);
        for (int i = 0; i < star.length; i++) {
            star[i] = new TrackingStar(atlas, mainShip.getV());
        }
        font = new Font("font/font.fnt", "font/font.png");
        font.setWorldSize(FONT_SIZE);
        startNewGame();
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        update(delta);
        checkCollisions();
        deleteAllDestroyed();
        draw();
    }

    public void draw() {
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        background.draw(batch);
        for (int i = 0; i < star.length; i++) {
            star[i].draw(batch);
        }
        mainShip.draw(batch);
        bulletPool.drawActiveSprites(batch);
        explosionPool.drawActiveSprites(batch);
        enemyPool.drawActiveSprites(batch);
        if (state == State.GAME_OVER) {
            messageGameOver.draw(batch);
            buttonNewGame.draw(batch);
        }
        printInfo();
        batch.end();
    }

    public void printInfo() {
        sbFrags.setLength(0);
        sbHP.setLength(0);
        sbLevel.setLength(0);
        font.draw(batch, sbFrags.append(FRAGS).append(frags), worldBounds.getLeft(), worldBounds.getTop());
        font.draw(batch, sbHP.append(HP).append(mainShip.getHp()), worldBounds.pos.x, worldBounds.getTop(), Align.center);
        font.draw(batch, sbLevel.append(LEVEL).append(enemyEmitter.getLevel()), worldBounds.getRight(), worldBounds.getTop(),  Align.right);
    }

    public void update(float delta) {
        if (mainShip.isDestroyed()) {
            state = State.GAME_OVER;
        }
        for (int i = 0; i < star.length; i++) {
            star[i].update(delta);
        }
        explosionPool.updateActiveSprites(delta);
        switch (state) {
            case PLAYING:
                mainShip.update(delta);
                bulletPool.updateActiveSprites(delta);
                enemyPool.updateActiveSprites(delta);
                enemyEmitter.generateEnemies(delta, frags);
                break;
            case GAME_OVER:
                break;
        }
    }

    public void checkCollisions() {
        List<Enemy> enemyList = enemyPool.getActiveObjects();
        for (Enemy enemy : enemyList) {
            if (enemy.isDestroyed()) {
                continue;
            }
            float minDist = enemy.getHalfWidth() + mainShip.getHalfWidth();
            if (enemy.pos.dst2(mainShip.pos) < minDist * minDist) {
                enemy.destroy();
                mainShip.destroy();
                state = State.GAME_OVER;
                return;
            }
        }

        List<Bullet> bulletList = bulletPool.getActiveObjects();
        for (Enemy enemy : enemyList) {
            if (enemy.isDestroyed()) {
                continue;
            }
            for (Bullet bullet : bulletList) {
                if (bullet.getOwner() != mainShip || bullet.isDestroyed()) {
                    continue;
                }
                if (enemy.isBulletCollision(bullet)) {
                    enemy.damage(bullet.getDamage());
                    bullet.destroy();
                    if (enemy.isDestroyed()) {
                        frags++;
                        break;
                    }
                }
            }
        }

        for (Bullet bullet : bulletList) {
            if (bullet.isDestroyed() || bullet.getOwner() == mainShip) {
                continue;
            }
            if (mainShip.isBulletCollision(bullet)) {
                mainShip.damage(bullet.getDamage());
                bullet.destroy();
                if (mainShip.isDestroyed()) {
                    state = State.GAME_OVER;
                }
            }
        }
    }

    public void deleteAllDestroyed() {
        bulletPool.freeAllDestroyedActiveSprites();
        explosionPool.freeAllDestroyedActiveSprites();
        enemyPool.freeAllDestroyedActiveSprites();
    }

    @Override
    public void resize(Rect worldBounds) {
        super.resize(worldBounds);
        background.resize(worldBounds);
        for (int i = 0; i < star.length; i++) {
            star[i].resize(worldBounds);
        }
        mainShip.resize(worldBounds);
    }

    @Override
    public void dispose() {
        super.dispose();
        bgTexture.dispose();
        atlas.dispose();
        bulletPool.dispose();
        explosionPool.dispose();
        enemyPool.dispose();
        bulletSound.dispose();
        music.dispose();
        font.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        if (state == State.PLAYING) {
            mainShip.keyDown(keycode);
        }
        return super.keyDown(keycode);
    }

    @Override
    public boolean keyUp(int keycode) {
        if (state == State.PLAYING) {
            mainShip.keyUp(keycode);
        }
        return super.keyUp(keycode);
    }

    @Override
    public boolean touchDown(Vector2 touch, int pointer) {
        if (state == State.PLAYING) {
            mainShip.touchDown(touch, pointer);
        } else {
            buttonNewGame.touchDown(touch, pointer);
        }
        return super.touchDown(touch, pointer);
    }

    @Override
    public boolean touchUp(Vector2 touch, int pointer) {
        if (state == State.PLAYING) {
            mainShip.touchUp(touch, pointer);
        } else {
            buttonNewGame.touchUp(touch, pointer);
        }
        return super.touchUp(touch, pointer);
    }

    private void startNewGame() {
        state = State.PLAYING;

        frags = 0;

        mainShip.startNewGame();
        enemyEmitter.startNewGame();

        bulletPool.freeAllActiveObjects();
        enemyPool.freeAllActiveObjects();
        explosionPool.freeAllActiveObjects();
    }

    @Override
    public void actionPerformed(Object src) {
        if (src == buttonNewGame) {
            startNewGame();
        }
    }


}
