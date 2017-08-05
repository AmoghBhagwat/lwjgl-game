package com.amoghbhagwat.engine;

import com.amoghbhagwat.game.utils.MouseInput;

public class GameEngine implements Runnable {
    public static final int TARGET_FPS = 75;
    public static final int TARGET_UPS = 30;

    private final Window window;
    private final Thread gameLoopThread;
    private final Timer timer;
    private final GameLogic gameLogic;
    private final MouseInput mouseInput;

    public GameEngine(String windowTitle, int width, int height, boolean vSync, GameLogic gameLogic) {
        this.gameLoopThread = new Thread(this, "GAME_LOOP_THREAD");
        this.window = new Window(windowTitle, width, height, vSync);
        this.timer = new Timer();
        this.gameLogic = gameLogic;
        this.mouseInput = new MouseInput();
    }

    public void start() {
        String osName = System.getProperty("os.name");
        if (osName.contains("Mac")) {
            gameLoopThread.run();
        } else {
            gameLoopThread.start();
        }
    }

    protected void init() throws Exception {
        window.init();
        timer.init();
        mouseInput.init(window);
        gameLogic.init(window);
    }

    protected void gameLoop() {
        float elapsedTime;
        float accumulator = 0.0f;
        float interval = 1f / TARGET_UPS;

        boolean running = true;
        while (running && !window.windowShouldClose()) {
            elapsedTime = timer.getElapsedTime();
            accumulator += elapsedTime;

            input();

            while (accumulator > interval) {
                update(interval);
                accumulator -= interval;
            }

            render();

            if (!window.isVSync()) {
                sync();
            }
        }
    }

    private void sync() {
        float loopShot = 1.0f / TARGET_FPS;
        double endTime = timer.getLastLoopTime() + loopShot;

        while (timer.getTime() < endTime) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    protected void cleanUp() {
        gameLogic.cleanUp();
    }

    protected void input() {
        gameLogic.input(window, mouseInput);
    }

    protected void update(float interval) {
        mouseInput.input(window);
        gameLogic.update(interval, mouseInput);
    }

    protected void render() {
        gameLogic.render(window);
        window.update();
    }

    @Override
    public void run() {
        try {
            init();
            gameLoop();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cleanUp();
        }
    }
}
