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
 * Panel game utama yang menangani semua logika game, rendering, dan input.
 * Fitur: multiple game state, sistem nyawa, progresi level,
 * multi-bola, dan power-up.
 */
public class GamePanel extends JPanel implements KeyListener, ActionListener {
    // State game
    private GameState gameState = GameState.START;
    private int score = 0;
    private int lives = 3;
    private int level = 1;

    // Konstanta game
    private static final int PANEL_WIDTH = 692;
    private static final int PANEL_HEIGHT = 592;
    private static final int MAX_LEVEL = 5;

    // Timer untuk game loop
    private Timer timer;
    private int delay = 8;

    // Objek-objek game
    private Paddle paddle;
    private ArrayList<Ball> balls;
    private MapGenerator map;
    private ArrayList<PowerUp> powerUps;
    private Random random = new Random();

    // Timer efek power-up pada paddle
    private int paddleEffectTimer = 0;
    private static final int EFFECT_DURATION = 500; // ~4 detik pada delay 8ms

    // Notifikasi power-up
    private String powerUpMessage = "";
    private int messageTimer = 0;

    // Warna untuk background gradient
    private static final Color BG_TOP = new Color(15, 15, 50);
    private static final Color BG_BOTTOM = new Color(0, 0, 0);

    // Font
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
     * Inisialisasi atau reset game ke state awal
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
        // Arah random
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

        // Gambar background gradient
        GradientPaint bgGradient = new GradientPaint(0, 0, BG_TOP, 0, PANEL_HEIGHT, BG_BOTTOM);
        g2d.setPaint(bgGradient);
        g2d.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);

        // Gambar berdasarkan state game
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
        // Judul
        g.setFont(TITLE_FONT);
        g.setColor(new Color(255, 200, 50));
        String title = "BRICK BLAST";
        FontMetrics fm = g.getFontMetrics();
        int titleX = (PANEL_WIDTH - fm.stringWidth(title)) / 2;
        g.drawString(title, titleX, 180);

        // Subjudul
        g.setFont(SUBTITLE_FONT);
        g.setColor(new Color(150, 200, 255));
        String subtitle = "Power-ups Edition";
        fm = g.getFontMetrics();
        int subX = (PANEL_WIDTH - fm.stringWidth(subtitle)) / 2;
        g.drawString(subtitle, subX, 220);

        // Instruksi
        g.setFont(INSTRUCTION_FONT);
        g.setColor(Color.WHITE);
        String[] instructions = {
                "CARA BERMAIN:",
                "",
                "<- -> Tombol Panah - Gerakkan Paddle",
                "P - Pause Game",
                "Enter - Mulai / Restart",
                "",
                "POWER-UP:",
                "* Brick - Jatuhkan power-up!",
                "x3 = Multi Bola | + = Paddle Lebar",
                "S = Bola Lambat | Hati = Nyawa Extra"
        };

        int startY = 280;
        for (String line : instructions) {
            fm = g.getFontMetrics();
            int lineX = (PANEL_WIDTH - fm.stringWidth(line)) / 2;
            g.drawString(line, lineX, startY);
            startY += 25;
        }

        // Prompt tekan Enter
        if ((System.currentTimeMillis() / 500) % 2 == 0) {
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.setColor(new Color(100, 255, 100));
            String prompt = ">> Tekan ENTER untuk Mulai <<";
            fm = g.getFontMetrics();
            int promptX = (PANEL_WIDTH - fm.stringWidth(prompt)) / 2;
            g.drawString(prompt, promptX, 550);
        }
    }

    private void drawGame(Graphics2D g) {
        // Gambar brick
        map.draw(g);

        // Gambar border dengan gradient
        GradientPaint borderGradient = new GradientPaint(0, 0, new Color(255, 200, 50), 0, PANEL_HEIGHT,
                new Color(255, 100, 50));
        g.setPaint(borderGradient);
        g.fillRect(0, 0, 4, PANEL_HEIGHT);
        g.fillRect(0, 0, PANEL_WIDTH, 4);
        g.fillRect(PANEL_WIDTH - 4, 0, 4, PANEL_HEIGHT);

        // Gambar HUD
        drawHUD(g);

        // Gambar paddle
        paddle.draw(g);

        // Gambar semua bola
        for (Ball ball : balls) {
            ball.draw(g);
        }

        // Gambar power-up
        for (PowerUp pu : powerUps) {
            if (pu.isActive()) {
                pu.draw(g);
            }
        }

        // Gambar pesan power-up
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

        // Skor
        g.setColor(Color.WHITE);
        g.drawString("Skor: " + score, 20, 30);

        // Level
        g.setColor(new Color(150, 200, 255));
        g.drawString("Level: " + level, 280, 30);

        // Jumlah bola
        if (balls.size() > 1) {
            g.setColor(new Color(255, 200, 100));
            g.drawString("Bola: " + balls.size(), 420, 30);
        }

        // Nyawa
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.setColor(new Color(255, 80, 80));
        g.drawString("Nyawa: " + lives, 560, 30);
    }

    private void drawPauseOverlay(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);

        g.setFont(MESSAGE_FONT);
        g.setColor(Color.WHITE);
        String msg = "PAUSE";
        FontMetrics fm = g.getFontMetrics();
        int x = (PANEL_WIDTH - fm.stringWidth(msg)) / 2;
        g.drawString(msg, x, 280);

        g.setFont(INSTRUCTION_FONT);
        String prompt = "Tekan P untuk Lanjutkan";
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
        String scoreMsg = "Skor Akhir: " + score;
        fm = g.getFontMetrics();
        x = (PANEL_WIDTH - fm.stringWidth(scoreMsg)) / 2;
        g.drawString(scoreMsg, x, 310);

        g.setFont(INSTRUCTION_FONT);
        g.setColor(new Color(150, 255, 150));
        String prompt = "Tekan ENTER untuk Main Lagi";
        fm = g.getFontMetrics();
        x = (PANEL_WIDTH - fm.stringWidth(prompt)) / 2;
        g.drawString(prompt, x, 360);
    }

    private void drawLevelComplete(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);

        g.setFont(MESSAGE_FONT);
        g.setColor(new Color(100, 255, 100));
        String msg = "LEVEL " + level + " SELESAI!";
        FontMetrics fm = g.getFontMetrics();
        int x = (PANEL_WIDTH - fm.stringWidth(msg)) / 2;
        g.drawString(msg, x, 280);

        g.setFont(INSTRUCTION_FONT);
        g.setColor(Color.WHITE);
        String prompt = "Tekan ENTER untuk Level Berikutnya";
        fm = g.getFontMetrics();
        x = (PANEL_WIDTH - fm.stringWidth(prompt)) / 2;
        g.drawString(prompt, x, 330);
    }

    private void drawWonScreen(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);

        g.setFont(MESSAGE_FONT);
        g.setColor(new Color(255, 215, 0));
        String msg = "SELAMAT!";
        FontMetrics fm = g.getFontMetrics();
        int x = (PANEL_WIDTH - fm.stringWidth(msg)) / 2;
        g.drawString(msg, x, 240);

        g.setColor(new Color(100, 255, 100));
        String msg2 = "ANDA MENANG!";
        fm = g.getFontMetrics();
        x = (PANEL_WIDTH - fm.stringWidth(msg2)) / 2;
        g.drawString(msg2, x, 290);

        g.setFont(SCORE_FONT);
        g.setColor(Color.WHITE);
        String scoreMsg = "Skor Akhir: " + score;
        fm = g.getFontMetrics();
        x = (PANEL_WIDTH - fm.stringWidth(scoreMsg)) / 2;
        g.drawString(scoreMsg, x, 340);

        g.setFont(INSTRUCTION_FONT);
        g.setColor(new Color(150, 255, 150));
        String prompt = "Tekan ENTER untuk Main Lagi";
        fm = g.getFontMetrics();
        x = (PANEL_WIDTH - fm.stringWidth(prompt)) / 2;
        g.drawString(prompt, x, 390);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameState == GameState.PLAYING) {
            // Update timer efek power-up
            if (paddleEffectTimer > 0) {
                paddleEffectTimer--;
                if (paddleEffectTimer == 0) {
                    paddle.resetSize();
                }
            }

            // Update timer pesan
            if (messageTimer > 0) {
                messageTimer--;
            }

            // Update power-up (jatuh)
            Iterator<PowerUp> puIterator = powerUps.iterator();
            while (puIterator.hasNext()) {
                PowerUp pu = puIterator.next();
                if (!pu.isActive()) {
                    puIterator.remove();
                    continue;
                }

                pu.fall();

                // Cek apakah ditangkap paddle
                if (pu.getRect().intersects(paddle.getRect())) {
                    applyPowerUp(pu.getType());
                    pu.deactivate();
                    puIterator.remove();
                }

                // Hapus jika jatuh ke luar layar
                if (pu.getY() > PANEL_HEIGHT) {
                    puIterator.remove();
                }
            }

            // Proses setiap bola
            Iterator<Ball> ballIterator = balls.iterator();
            ArrayList<Ball> newBalls = new ArrayList<>();

            while (ballIterator.hasNext()) {
                Ball ball = ballIterator.next();

                // Tabrakan Bola - Paddle
                if (ball.getRect().intersects(paddle.getRect())) {
                    ball.reverseY();
                    // Sesuaikan arah berdasarkan posisi hit di paddle
                    int paddleCenter = paddle.getX() + paddle.getWidth() / 2;
                    int ballCenter = ball.getX() + ball.getSize() / 2;
                    double diff = (ballCenter - paddleCenter) / 10.0;
                    ball.setDirX(ball.getDirX() + diff);
                    // Jaga bola tetap di atas paddle
                    ball.setY(paddle.getRect().y - ball.getSize());
                }

                // Tabrakan Bola - Brick
                brickCollision: for (int i = 0; i < map.getRows(); i++) {
                    for (int j = 0; j < map.getCols(); j++) {
                        if (map.getBrickValue(i, j) > 0) {
                            int brickX = j * map.getBrickWidth() + 80;
                            int brickY = i * map.getBrickHeight() + 50;
                            int brickWidth = map.getBrickWidth();
                            int brickHeight = map.getBrickHeight();

                            Rectangle brickRect = new Rectangle(brickX, brickY, brickWidth, brickHeight);

                            if (ball.getRect().intersects(brickRect)) {
                                // Pukul brick
                                boolean dropPowerUp = map.hitBrick(i, j);

                                // Tambah skor
                                if (map.getBrickValue(i, j) == 0) {
                                    score += 10 * level;
                                } else {
                                    score += 5 * level; // Poin parsial untuk brick kuat
                                }

                                // Spawn power-up jika applicable
                                if (dropPowerUp) {
                                    spawnPowerUp(brickX + brickWidth / 2 - 15, brickY);
                                }

                                // Tentukan arah tabrakan
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

                // Tabrakan Dinding
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

                // Bola jatuh ke bawah layar
                if (ball.getY() > PANEL_HEIGHT) {
                    ballIterator.remove();
                }
            }

            // Tambah bola baru dari multi-ball
            balls.addAll(newBalls);

            // Cek apakah semua bola hilang
            if (balls.isEmpty()) {
                lives--;
                if (lives <= 0) {
                    gameState = GameState.GAME_OVER;
                } else {
                    // Reset untuk nyawa berikutnya
                    balls.add(createBall());
                    paddle.reset();
                    powerUps.clear();
                    paddleEffectTimer = 0;
                }
            }

            // Cek level selesai
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
        messageTimer = 60; // Tampilkan pesan selama ~0.5 detik

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
