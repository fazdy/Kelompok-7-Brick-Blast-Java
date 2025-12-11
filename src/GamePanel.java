import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Main game panel that handles all game logic, rendering, and input.
 * Features multiple game states, lives system, level progression,
 * multi-ball, and power-ups.
 */
public class GamePanel extends JPanel implements KeyListener, ActionListener {
    // Game state
    private GameState gameState = GameState.START;
    private int score = 0;
    private int lives = 3;
    private int level = 1;

    // Game constants
    private static final int PANEL_WIDTH = 692;
    private static final int PANEL_HEIGHT = 592;
    private static final int MAX_LEVEL = 5;

    // Timer for game loop
    private Timer timer;
    private int delay = 8;

    // Game objects
    private Paddle paddle;
    private ArrayList<Ball> balls;
    private MapGenerator map;
    private ArrayList<PowerUp> powerUps;
    private Random random = new Random();

    // Power-up effect timers
    private int paddleEffectTimer = 0;
    private static final int EFFECT_DURATION = 500; // ~4 seconds at 8ms delay

    // Power-up notification
    private String powerUpMessage = "";
    private int messageTimer = 0;

    // Colors for gradient background
    private static final Color BG_TOP = new Color(15, 15, 50);
    private static final Color BG_BOTTOM = new Color(0, 0, 0);

    // Fonts
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 48);
    private static final Font SUBTITLE_FONT = new Font("Arial", Font.PLAIN, 20);
    private static final Font SCORE_FONT = new Font("Arial", Font.BOLD, 22);
    private static final Font MESSAGE_FONT = new Font("Arial", Font.BOLD, 36);
    private static final Font INSTRUCTION_FONT = new Font("Arial", Font.PLAIN, 18);
    private static final Font POWERUP_FONT = new Font("Arial", Font.BOLD, 16);

    public GamePanel() {
        initGame();

        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        timer = new Timer(delay, this);
        timer.start();
    }

    /**
     * Initialize or reset the game to starting state
     */
    private void initGame() {
        map = new MapGenerator(level);
        paddle = new Paddle(310);
        balls = new ArrayList<>();
        balls.add(createBall());
        powerUps = new ArrayList<>();
        paddleEffectTimer = 0;
    }

    private Ball createBall() {
        double speedX = Ball.getBaseSpeedX(level);
        double speedY = Ball.getBaseSpeedY(level);
        // Random direction
        if (random.nextBoolean())
            speedX = -speedX;
        return new Ball(350, 500, speedX, -speedY);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Draw gradient background
        GradientPaint bgGradient = new GradientPaint(0, 0, BG_TOP, 0, PANEL_HEIGHT, BG_BOTTOM);
        g2d.setPaint(bgGradient);
        g2d.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);

        // Draw based on game state
        switch (gameState) {
            case START:
                drawStartScreen(g2d);
                break;
            case PLAYING:
            case PAUSED:
                drawGame(g2d);
                if (gameState == GameState.PAUSED) {
                    drawPauseOverlay(g2d);
                }
                break;
            case LEVEL_COMPLETE:
                drawGame(g2d);
                drawLevelComplete(g2d);
                break;
            case GAME_OVER:
                drawGame(g2d);
                drawGameOver(g2d);
                break;
            case WON:
                drawGame(g2d);
                drawWonScreen(g2d);
                break;
        }
    }

    private void drawStartScreen(Graphics2D g) {
        // Title
        g.setFont(TITLE_FONT);
        g.setColor(new Color(255, 200, 50));
        String title = "BRICK BLAST";
        FontMetrics fm = g.getFontMetrics();
        int titleX = (PANEL_WIDTH - fm.stringWidth(title)) / 2;
        g.drawString(title, titleX, 180);

        // Subtitle
        g.setFont(SUBTITLE_FONT);
        g.setColor(new Color(150, 200, 255));
        String subtitle = "Power-ups Edition";
        fm = g.getFontMetrics();
        int subX = (PANEL_WIDTH - fm.stringWidth(subtitle)) / 2;
        g.drawString(subtitle, subX, 220);

        // Instructions
        g.setFont(INSTRUCTION_FONT);
        g.setColor(Color.WHITE);
        String[] instructions = {
                "HOW TO PLAY:",
                "",
                "<- -> Arrow Keys - Move Paddle",
                "P - Pause Game",
                "Enter - Start / Restart",
                "",
                "POWER-UPS:",
                "* Bricks - Drop power-ups!",
                "x3 = Multi Ball | + = Wide Paddle",
                "S = Slow Ball | Heart = Extra Life"
        };

        int startY = 280;
        for (String line : instructions) {
            fm = g.getFontMetrics();
            int lineX = (PANEL_WIDTH - fm.stringWidth(line)) / 2;
            g.drawString(line, lineX, startY);
            startY += 25;
        }

        // Press Enter prompt
        if ((System.currentTimeMillis() / 500) % 2 == 0) {
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.setColor(new Color(100, 255, 100));
            String prompt = ">> Press ENTER to Start <<";
            fm = g.getFontMetrics();
            int promptX = (PANEL_WIDTH - fm.stringWidth(prompt)) / 2;
            g.drawString(prompt, promptX, 550);
        }
    }

    private void drawGame(Graphics2D g) {
        // Draw bricks
        map.draw(g);

        // Draw borders with gradient
        GradientPaint borderGradient = new GradientPaint(0, 0, new Color(255, 200, 50), 0, PANEL_HEIGHT,
                new Color(255, 100, 50));
        g.setPaint(borderGradient);
        g.fillRect(0, 0, 4, PANEL_HEIGHT);
        g.fillRect(0, 0, PANEL_WIDTH, 4);
        g.fillRect(PANEL_WIDTH - 4, 0, 4, PANEL_HEIGHT);

        // Draw HUD
        drawHUD(g);

        // Draw paddle
        paddle.draw(g);

        // Draw all balls
        for (Ball ball : balls) {
            ball.draw(g);
        }

        // Draw power-ups
        for (PowerUp pu : powerUps) {
            if (pu.isActive()) {
                pu.draw(g);
            }
        }

        // Draw power-up message
        if (messageTimer > 0) {
            g.setFont(POWERUP_FONT);
            g.setColor(new Color(255, 255, 100, Math.min(255, messageTimer * 5)));
            FontMetrics fm = g.getFontMetrics();
            int msgX = (PANEL_WIDTH - fm.stringWidth(powerUpMessage)) / 2;
            g.drawString(powerUpMessage, msgX, 520);
        }
    }

    private void drawHUD(Graphics2D g) {
        g.setFont(SCORE_FONT);

        // Score
        g.setColor(Color.WHITE);
        g.drawString("Score: " + score, 20, 30);

        // Level
        g.setColor(new Color(150, 200, 255));
        g.drawString("Level: " + level, 280, 30);

        // Ball count
        if (balls.size() > 1) {
            g.setColor(new Color(255, 200, 100));
            g.drawString("Balls: " + balls.size(), 420, 30);
        }

        // Lives
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.setColor(new Color(255, 80, 80));
        g.drawString("Lives: " + lives, 580, 30);
    }

    private void drawPauseOverlay(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);

        g.setFont(MESSAGE_FONT);
        g.setColor(Color.WHITE);
        String msg = "PAUSED";
        FontMetrics fm = g.getFontMetrics();
        int x = (PANEL_WIDTH - fm.stringWidth(msg)) / 2;
        g.drawString(msg, x, 280);

        g.setFont(INSTRUCTION_FONT);
        String prompt = "Press P to Resume";
        fm = g.getFontMetrics();
        x = (PANEL_WIDTH - fm.stringWidth(prompt)) / 2;
        g.drawString(prompt, x, 320);
    }

    private void drawGameOver(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);

        g.setFont(MESSAGE_FONT);
        g.setColor(new Color(255, 80, 80));
        String msg = "GAME OVER";
        FontMetrics fm = g.getFontMetrics();
        int x = (PANEL_WIDTH - fm.stringWidth(msg)) / 2;
        g.drawString(msg, x, 260);

        g.setFont(SCORE_FONT);
        g.setColor(Color.WHITE);
        String scoreMsg = "Final Score: " + score;
        fm = g.getFontMetrics();
        x = (PANEL_WIDTH - fm.stringWidth(scoreMsg)) / 2;
        g.drawString(scoreMsg, x, 310);

        g.setFont(INSTRUCTION_FONT);
        g.setColor(new Color(150, 255, 150));
        String prompt = "Press ENTER to Restart";
        fm = g.getFontMetrics();
        x = (PANEL_WIDTH - fm.stringWidth(prompt)) / 2;
        g.drawString(prompt, x, 360);
    }

    private void drawLevelComplete(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);

        g.setFont(MESSAGE_FONT);
        g.setColor(new Color(100, 255, 100));
        String msg = "LEVEL " + level + " COMPLETE!";
        FontMetrics fm = g.getFontMetrics();
        int x = (PANEL_WIDTH - fm.stringWidth(msg)) / 2;
        g.drawString(msg, x, 280);

        g.setFont(INSTRUCTION_FONT);
        g.setColor(Color.WHITE);
        String prompt = "Press ENTER for Next Level";
        fm = g.getFontMetrics();
        x = (PANEL_WIDTH - fm.stringWidth(prompt)) / 2;
        g.drawString(prompt, x, 330);
    }

    private void drawWonScreen(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);

        g.setFont(MESSAGE_FONT);
        g.setColor(new Color(255, 215, 0));
        String msg = "CONGRATULATIONS!";
        FontMetrics fm = g.getFontMetrics();
        int x = (PANEL_WIDTH - fm.stringWidth(msg)) / 2;
        g.drawString(msg, x, 240);

        g.setColor(new Color(100, 255, 100));
        String msg2 = "YOU WON THE GAME!";
        fm = g.getFontMetrics();
        x = (PANEL_WIDTH - fm.stringWidth(msg2)) / 2;
        g.drawString(msg2, x, 290);

        g.setFont(SCORE_FONT);
        g.setColor(Color.WHITE);
        String scoreMsg = "Final Score: " + score;
        fm = g.getFontMetrics();
        x = (PANEL_WIDTH - fm.stringWidth(scoreMsg)) / 2;
        g.drawString(scoreMsg, x, 340);

        g.setFont(INSTRUCTION_FONT);
        g.setColor(new Color(150, 255, 150));
        String prompt = "Press ENTER to Play Again";
        fm = g.getFontMetrics();
        x = (PANEL_WIDTH - fm.stringWidth(prompt)) / 2;
        g.drawString(prompt, x, 390);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameState == GameState.PLAYING) {
            // Update power-up effect timer
            if (paddleEffectTimer > 0) {
                paddleEffectTimer--;
                if (paddleEffectTimer == 0) {
                    paddle.resetSize();
                }
            }

            // Update message timer
            if (messageTimer > 0) {
                messageTimer--;
            }

            // Update power-ups (falling)
            Iterator<PowerUp> puIterator = powerUps.iterator();
            while (puIterator.hasNext()) {
                PowerUp pu = puIterator.next();
                if (!pu.isActive()) {
                    puIterator.remove();
                    continue;
                }

                pu.fall();

                // Check if caught by paddle
                if (pu.getRect().intersects(paddle.getRect())) {
                    applyPowerUp(pu.getType());
                    pu.deactivate();
                    puIterator.remove();
                }

                // Remove if fell off screen
                if (pu.getY() > PANEL_HEIGHT) {
                    puIterator.remove();
                }
            }

            // Process each ball
            Iterator<Ball> ballIterator = balls.iterator();
            ArrayList<Ball> newBalls = new ArrayList<>();

            while (ballIterator.hasNext()) {
                Ball ball = ballIterator.next();

                // Ball - Paddle Collision
                if (ball.getRect().intersects(paddle.getRect())) {
                    ball.reverseY();
                    // Adjust direction based on paddle hit position
                    int paddleCenter = paddle.getX() + paddle.getWidth() / 2;
                    int ballCenter = ball.getX() + ball.getSize() / 2;
                    double diff = (ballCenter - paddleCenter) / 10.0;
                    ball.setDirX(ball.getDirX() + diff);
                    // Keep ball above paddle
                    ball.setY(paddle.getRect().y - ball.getSize());
                }

                // Ball - Brick Collision
                brickCollision: for (int i = 0; i < map.getRows(); i++) {
                    for (int j = 0; j < map.getCols(); j++) {
                        if (map.getBrickValue(i, j) > 0) {
                            int brickX = j * map.getBrickWidth() + 80;
                            int brickY = i * map.getBrickHeight() + 50;
                            int brickWidth = map.getBrickWidth();
                            int brickHeight = map.getBrickHeight();

                            Rectangle brickRect = new Rectangle(brickX, brickY, brickWidth, brickHeight);

                            if (ball.getRect().intersects(brickRect)) {
                                // Hit the brick
                                boolean dropPowerUp = map.hitBrick(i, j);

                                // Add score
                                if (map.getBrickValue(i, j) == 0) {
                                    score += 10 * level;
                                } else {
                                    score += 5 * level; // Partial points for strong bricks
                                }

                                // Spawn power-up if applicable
                                if (dropPowerUp) {
                                    spawnPowerUp(brickX + brickWidth / 2 - 15, brickY);
                                }

                                // Determine collision direction
                                if (ball.getX() + ball.getSize() - 2 <= brickRect.x ||
                                        ball.getX() + 2 >= brickRect.x + brickRect.width) {
                                    ball.reverseX();
                                } else {
                                    ball.reverseY();
                                }

                                break brickCollision;
                            }
                        }
                    }
                }

                ball.move();

                // Wall Collision
                if (ball.getX() < 5) {
                    ball.setX(5);
                    ball.reverseX();
                }
                if (ball.getY() < 5) {
                    ball.setY(5);
                    ball.reverseY();
                }
                if (ball.getX() > PANEL_WIDTH - ball.getSize() - 5) {
                    ball.setX(PANEL_WIDTH - ball.getSize() - 5);
                    ball.reverseX();
                }

                // Ball fell below screen
                if (ball.getY() > PANEL_HEIGHT) {
                    ballIterator.remove();
                }
            }

            // Add any new balls from multi-ball
            balls.addAll(newBalls);

            // Check if all balls lost
            if (balls.isEmpty()) {
                lives--;
                if (lives <= 0) {
                    gameState = GameState.GAME_OVER;
                } else {
                    // Reset for next life
                    balls.add(createBall());
                    paddle.reset();
                    powerUps.clear();
                    paddleEffectTimer = 0;
                }
            }

            // Check level complete
            if (map.getTotalBricks() <= 0) {
                if (level >= MAX_LEVEL) {
                    gameState = GameState.WON;
                } else {
                    gameState = GameState.LEVEL_COMPLETE;
                }
            }
        }

        repaint();
    }

    private void spawnPowerUp(int x, int y) {
        PowerUpType[] types = PowerUpType.values();
        PowerUpType type = types[random.nextInt(types.length)];
        powerUps.add(new PowerUp(x, y, type));
    }

    private void applyPowerUp(PowerUpType type) {
        powerUpMessage = type.getDescription();
        messageTimer = 60; // Show message for ~0.5 seconds

        switch (type) {
            case MULTI_BALL:
                if (!balls.isEmpty()) {
                    Ball original = balls.get(0);
                    balls.add(original.clone(-2));
                    balls.add(original.clone(2));
                }
                break;

            case WIDE_PADDLE:
                paddle.widen();
                paddleEffectTimer = EFFECT_DURATION;
                break;

            case NARROW_PADDLE:
                paddle.narrow();
                paddleEffectTimer = EFFECT_DURATION;
                break;

            case SPEED_DOWN:
                for (Ball ball : balls) {
                    ball.slowDown();
                }
                break;

            case EXTRA_LIFE:
                lives++;
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        switch (gameState) {
            case START:
                if (key == KeyEvent.VK_ENTER) {
                    gameState = GameState.PLAYING;
                }
                break;

            case PLAYING:
                if (key == KeyEvent.VK_RIGHT) {
                    paddle.moveRight();
                } else if (key == KeyEvent.VK_LEFT) {
                    paddle.moveLeft();
                } else if (key == KeyEvent.VK_P) {
                    gameState = GameState.PAUSED;
                }
                break;

            case PAUSED:
                if (key == KeyEvent.VK_P) {
                    gameState = GameState.PLAYING;
                }
                break;

            case LEVEL_COMPLETE:
                if (key == KeyEvent.VK_ENTER) {
                    level++;
                    initGame();
                    gameState = GameState.PLAYING;
                }
                break;

            case GAME_OVER:
            case WON:
                if (key == KeyEvent.VK_ENTER) {
                    score = 0;
                    lives = 3;
                    level = 1;
                    initGame();
                    gameState = GameState.PLAYING;
                }
                break;
        }

        repaint();
    }
}
