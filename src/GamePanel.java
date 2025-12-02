import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JPanel;
import javax.swing.Timer;

public class GamePanel extends JPanel implements KeyListener, ActionListener {
    private boolean play = false;
    private int score = 0;
    private int totalBricks = 21;

    private Timer timer;
    private int delay = 8;

    private Paddle paddle;
    private Ball ball;
    private MapGenerator map;

    public GamePanel() {
        map = new MapGenerator(3, 7);
        paddle = new Paddle(310);
        ball = new Ball(120, 350, -1, -2);

        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        timer = new Timer(delay, this);
        timer.start();
    }

    public void paint(Graphics g) {
        // Background
        g.setColor(Color.black);
        g.fillRect(1, 1, 692, 592);

        // Drawing map
        map.draw((Graphics2D) g);

        // Borders
        g.setColor(Color.yellow);
        g.fillRect(0, 0, 3, 592);
        g.fillRect(0, 0, 692, 3);
        g.fillRect(691, 0, 3, 592);

        // Scores
        g.setColor(Color.white);
        g.setFont(new Font("serif", Font.BOLD, 25));
        g.drawString("" + score, 590, 30);

        // Paddle
        paddle.draw(g);

        // Ball
        ball.draw(g);

        // Game Over / Won
        if (totalBricks <= 0) {
            play = false;
            ball.setDirX(0);
            ball.setDirY(0);
            g.setColor(Color.red);
            g.setFont(new Font("serif", Font.BOLD, 30));
            g.drawString("You Won: " + score, 260, 300);

            g.setFont(new Font("serif", Font.BOLD, 20));
            g.drawString("Press Enter to Restart", 230, 350);
        }

        if (ball.getY() > 570) {
            play = false;
            ball.setDirX(0);
            ball.setDirY(0);
            g.setColor(Color.red);
            g.setFont(new Font("serif", Font.BOLD, 30));
            g.drawString("Game Over, Scores: " + score, 190, 300);

            g.setFont(new Font("serif", Font.BOLD, 20));
            g.drawString("Press Enter to Restart", 230, 350);
        }

        g.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        timer.start();
        if (play) {
            // Ball - Paddle Collision
            if (new Rectangle(ball.getX(), ball.getY(), 20, 20).intersects(paddle.getRect())) {
                ball.reverseY();
            }

            // Ball - Brick Collision
            A: for (int i = 0; i < map.map.length; i++) {
                for (int j = 0; j < map.map[0].length; j++) {
                    if (map.map[i][j] > 0) {
                        int brickX = j * map.brickWidth + 80;
                        int brickY = i * map.brickHeight + 50;
                        int brickWidth = map.brickWidth;
                        int brickHeight = map.brickHeight;

                        Rectangle rect = new Rectangle(brickX, brickY, brickWidth, brickHeight);
                        Rectangle ballRect = new Rectangle(ball.getX(), ball.getY(), 20, 20);
                        Rectangle brickRect = rect;

                        if (ballRect.intersects(brickRect)) {
                            map.setBrickValue(0, i, j);
                            totalBricks--;
                            score += 5;

                            if (ball.getX() + 19 <= brickRect.x || ball.getX() + 1 >= brickRect.x + brickRect.width) {
                                ball.reverseX();
                            } else {
                                ball.reverseY();
                            }

                            break A;
                        }
                    }
                }
            }

            ball.move();

            // Wall Collision
            if (ball.getX() < 0) {
                ball.reverseX();
            }
            if (ball.getY() < 0) {
                ball.reverseY();
            }
            if (ball.getX() > 670) {
                ball.reverseX();
            }
        }

        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            if (play) {
                paddle.moveRight();
            } else if (!play && totalBricks > 0 && ball.getY() <= 570) {
                // Start game if not started but not game over
                play = true;
                paddle.moveRight();
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            if (play) {
                paddle.moveLeft();
            } else if (!play && totalBricks > 0 && ball.getY() <= 570) {
                play = true;
                paddle.moveLeft();
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (!play) {
                play = true;
                ball.setX(120);
                ball.setY(350);
                ball.setDirX(-1);
                ball.setDirY(-2);
                score = 0;
                totalBricks = 21;
                map = new MapGenerator(3, 7);
                repaint();
            }
        }
    }
}
