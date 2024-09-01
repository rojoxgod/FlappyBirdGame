import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import javax.swing.*;


public class FlappyBird extends JPanel implements ActionListener, KeyListener {

    int boardWidth = 360;
    int boardHeight = 640;

    //Images
    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;

    //Bird
    int birdX = boardWidth/8;
    int birdY = boardHeight/2;
    int birdWidth = 34;
    int birdHeight = 24;

    class Bird {
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        Bird(Image img) {
            this.img = img;
        }
    }

    //Pipes
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;

    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;

        Pipe(Image img) {
            this.img = img;
        }
    }

    //Game Logic
    Bird bird;
    int velocityX = -4; //move pipes to the left speed
    int velocityY = 0; //move bird up/down speed
    int gravity = 1;

    ArrayList<Pipe> pipes;
    Random random = new Random();
    Timer gameLoop;
    Timer placePipesTimer;
    boolean gameOver = false;
    int result = 0;

    FlappyBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);

        //Load Images
        backgroundImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/flappybirdbg.png"))).getImage();
        birdImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/flappybird.png"))).getImage();
        topPipeImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/toppipe.png"))).getImage();
        bottomPipeImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/bottompipe.png"))).getImage();

        //Bird
        bird = new Bird(birdImg);
        pipes = new ArrayList<Pipe>();

        //Place Pipes Timer
        placePipesTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        placePipesTimer.start();

        //Game Timer
        gameLoop = new Timer(1000/144, this);
        gameLoop.start();

    }


    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        //Background
        g.drawImage(backgroundImg, 0,0, boardWidth, boardHeight, null);
        //Bird
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);
        //Pipes
        for(int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        //score
        Font font = new Font("Helvetica Neue", Font.BOLD, 18);
        g.setFont(font);
        g.setColor(Color.white);
        g.drawString("Score: " + result, 10, 20);

        //Game Over
        if (gameOver) {
            Font gameOverFont = new Font("Helvetica Neue", Font.BOLD, 36);
            g.setFont(gameOverFont);
            g.setColor(Color.red);
            g.drawString("Game Over!", boardWidth / 4, boardHeight / 2);
            g.setFont(font);
            g.setColor(Color.white);
            g.drawString("Press 'R' to Restart", boardWidth / 4 + 15, boardHeight / 2 + 40);
        }

    }

    public void move() {
        //score
        result += 1;

        //bird
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0);

        //pipes
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            //gameOver scenario 1
            if(collision(bird, pipe)) {
                gameOver = true;
            }
        }

        //gameOver scenario 2
        if (bird.y > boardHeight) {
            gameOver = true;
        }

    }

    private void resetGame() {
        // Reset Bird
        bird = new Bird(birdImg);
        bird.x = birdX;
        bird.y = birdY;
        velocityY = 0;

        // Reset Pipes
        pipes = new ArrayList<>();
        result = 0;
        gameOver = false;
    }

    public boolean collision(Bird a, Pipe b) {
        return  a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    public void placePipes() {
        int randomPipeY = (int) (pipeY - (double) pipeHeight /4 - Math.random()*((double) pipeHeight /2));
        int openingSpace = boardHeight/4;

        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_UP){
            velocityY = -12;
        }
        if (e.getKeyCode() == KeyEvent.VK_R && gameOver) {
            resetGame();
            placePipesTimer.start();
            gameLoop.start();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            placePipesTimer.stop();
            gameLoop.stop();
        }
    }





    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {}

}
