import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;


/**
 * Created 2021-04-27
 *
 * @author
 */

public class GAME extends Canvas implements Runnable {

    private Thread thread;
    int fps = 60;
    private boolean isRunning;

    private BufferStrategy bs;

    private int Score,Highscore, i, Pipecount;
    private double BirdX, BirdY, BirdSpeed;
    private ArrayList<Pipe> Pipes = new ArrayList<Pipe>();
    private Boolean Going, Gameover;

    public GAME() {
        JFrame frame = new JFrame("Breakout");
        this.setSize(800,950);
        frame.add(this);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.addKeyListener(new KL());
        frame.setVisible(true);

        BirdX = 100;
        BirdY = 400;
        Going = false;
        Gameover = false;
    }

    public void update() {
        if (Going) {
            if (BirdY+BirdSpeed>0) {
                BirdY += BirdSpeed;
            }
            if (BirdSpeed<6.5) {
                BirdSpeed+=0.2;
            }

            if (BirdY+45>=850) {
                GameOver();
            }

            Rectangle Bird = new Rectangle( (int) BirdX, (int) BirdY, 60, 45);
            for (i=0; i<Pipes.size(); i++) {
                Pipes.get(i).X -= 2;
                if (Pipes.get(i).X<=-120){
                    Pipes.remove(i);
                    Score++;
                }
                if (Pipes.get(i).getX()>=0) {
                    Rectangle PipeTop = new Rectangle(Pipes.get(i).getX(), 0, 120, Pipes.get(i).getY());
                    Rectangle PipeBot = new Rectangle(Pipes.get(i).getX(), Pipes.get(i).getY() + 120, 120, 700-Pipes.get(i).getY());

                    if (Bird.intersects(PipeTop) || Bird.intersects(PipeBot)) {
                        GameOver();
                    }
                }
            }

            if (Pipecount==200) {
                SpawnPipe();
                Pipecount=0;
            } else {
                Pipecount++;
            }
        }
    }

    public void draw() {
        bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(3);
            return;
        }
        Graphics g = bs.getDrawGraphics();
        update();

        g.setColor(new Color(58, 116, 177));
        g.fillRect(0,0, 800, 950);
        g.setColor(new Color(86, 147, 210));
        g.fillRect(0,500, 800, 450);
        g.setColor(new Color(117, 169, 229));
        g.fillRect(0,650, 800, 300);
        g.setColor(new Color(173, 213, 255));
        g.fillRect(0,750, 800, 200);


        g.setColor(new Color(201, 173, 71));
        g.fillRect(0,870,800,100);
        g.setColor(new Color(46, 139, 23));
        g.fillRect(0,850,800,20);
        g.setColor(new Color(53, 170, 24));
        g.fillRect(0,855,800,10);

        g.setColor(Color.red);
        g.fillRect((int) BirdX, (int) BirdY, 60, 45);

        for (i=0; i<Pipes.size(); i++) {
            g.setColor(new Color(20, 198, 42));
            g.fillRect(Pipes.get(i).getX(),Pipes.get(i).getY()-30,120,30);
            g.fillRect( Pipes.get(i).getX()+10, 0,100, Pipes.get(i).getY()-30);

            g.fillRect(Pipes.get(i).getX(),Pipes.get(i).getY()+120,120,30);
            g.fillRect( Pipes.get(i).getX()+10, Pipes.get(i).getY()+150,100, 700-Pipes.get(i).getY());


            g.setColor(new Color(2,240,2));
            g.fillRect(Pipes.get(i).getX()+4,Pipes.get(i).getY()-26,112,22);
            g.fillRect( Pipes.get(i).getX()+14, 4,92, Pipes.get(i).getY()-38);

            g.fillRect(Pipes.get(i).getX()+4,Pipes.get(i).getY()+124,112,22);
            g.fillRect( Pipes.get(i).getX()+14, Pipes.get(i).getY()+154,92, 700-Pipes.get(i).getY()-8);
        }

        if (!Going) {
            g.setColor(new Color(187, 119, 54));
            g.fillRect(100,100,600, 200);
            g.setColor(new Color(222, 144, 68));
            g.fillRect(108,108,584, 184);
            g.setColor(new Color(255, 192, 0));
            g.setFont(new Font("Monospaced", Font.BOLD, 60));
            g.drawString("Press Space",200,170);
            g.drawString(  "To Start", 240,250);
        } else {
            g.setColor(new Color(255, 192, 0));
            g.setFont(new Font("Monospaced", Font.BOLD, 60));
            g.drawString(Score+"",300,70);
        }

        if (Gameover) {
            g.setColor(new Color(187, 119, 54));
            g.fillRect(100,400,600, 200);
            g.setColor(new Color(222, 144, 68));
            g.fillRect(108,408,584, 184);
            g.setColor(new Color(255, 192, 0));
            g.setFont(new Font("Monospaced", Font.BOLD, 72));
            g.drawString("Highscore: " + Highscore,140,470);
            g.drawString(  "Score: " + Score, 240,550);
        }


        g.dispose();
        bs.show();
    }

    private void RestartGame(){
        Pipes.clear();
        Gameover = false;
        BirdY=400;
        Score=0;
    }

    private void SpawnPipe(){
        Pipes.add(new Pipe(900, (int) (Math.random()*500)+100));
    }

    private void GameOver(){
        Going=false;
        Gameover=true;
        if (Score>Highscore){
            Highscore=Score;
        }
    }


    public static void main(String[] args) {
        GAME painting = new GAME();
        painting.start();
    }

    public synchronized void start() {
        thread = new Thread(this);
        isRunning = true;
        thread.start();
    }

    public synchronized void stop() {
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        double deltaT = 1000.0 / fps;
        long lastTime = System.currentTimeMillis();

        while (isRunning) {
            long now = System.currentTimeMillis();
            if (now - lastTime > deltaT) {
                draw();
                lastTime = now;
            }
        }
        stop();
    }

    private class KL implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {
            if (e.getKeyChar() == ' ' || e.getKeyChar() == ' ') {
                if (Gameover) {
                    RestartGame();
                    Going=true;
                }
                else if (!Going) {
                    Going=true;
                }
                BirdSpeed = -4;
            }

            if (e.getKeyChar() == 'p' || e.getKeyChar() == 'P') {
                SpawnPipe();
            }
        }

        @Override
        public void keyPressed(KeyEvent keyEvent) {

        }

        @Override
        public void keyReleased(KeyEvent keyEvent) {

        }

    }

}
