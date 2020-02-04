package Game;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.Timer;


public class flappyBirdGame implements ActionListener, MouseListener, KeyListener {
    // All the variables that is required for the game such as the bird, pipes, movement, timer, score etc
    public static flappyBirdGame flappyBird;
    public final int WIDTH = 800, HEIGHT = 800;
    public Render render;
    public Rectangle bird;
    public ArrayList<Rectangle> pipes;
    public int skips, movement, points;
    public boolean gameOver, started;
    public Random rand;
    public String highScore = "";
    public ArrayList<Integer> pointss = new ArrayList<>(5);

    public flappyBirdGame() {
        // Creation of the game window and adding all the necessary actions
        JFrame gameWindow = new JFrame();
        Timer timer = new Timer(20, this);
        render = new Render();
        rand = new Random();
        gameWindow.add(render);
        gameWindow.setSize(WIDTH, HEIGHT);
        gameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameWindow.setTitle("FlappyBird#1801954");
        gameWindow.addMouseListener(this);
        gameWindow.addKeyListener(this);
        gameWindow.setResizable(false);
        gameWindow.setVisible(true);

        bird = new Rectangle(WIDTH / 2 - 10, HEIGHT / 2 - 10, 20, 20); // size of the bird
        pipes = new ArrayList<>();

        addPipes(true);
        addPipes(true);
        addPipes(true);
        addPipes(true);
        timer.start();

    }
    //creation of the pipes
    public void addPipes(boolean start) {
        int space = 300;
        int width = 100;
        int height = 50 + rand.nextInt(300);

        if (start) {
            pipes.add(new Rectangle(WIDTH + width + pipes.size() * 300, HEIGHT - height - 120, width, height));
            pipes.add(new Rectangle(WIDTH + width + (pipes.size() - 1) * 300, 0, width, HEIGHT - height - space));

        }

        else {
            pipes.add(new Rectangle(pipes.get(pipes.size() - 1).x + 600, HEIGHT - height - 120, width, height));
            pipes.add(new Rectangle(pipes.get(pipes.size() - 1).x, 0, width, HEIGHT - height - space));

        }
    }
    // setting colour to the pipes
    public void paintPipes(Graphics g, Rectangle column) {
        g.setColor(Color.YELLOW.brighter());
        g.fillRect(column.x, column.y, column.width, column.height);

    }
    // Jump function
    public void jump() {
        if (gameOver) {
            bird = new Rectangle(WIDTH / 2 - 10, HEIGHT / 2 - 10, 20, 20);
            pipes.clear();
            movement = 0;
            points = 0;
            addPipes(true);
            addPipes(true);
            addPipes(true);
            addPipes(true);
            gameOver = false;

        }

        if (!started) {
            started = true;

        }

        else if (!gameOver) {
            if (movement > 0) {
                movement = 0;

            }
            movement -= 10;

        }
    }
    /*
    Down below is the code for the action being performed which is the basic functionality
    of the game. Jumping of the bird and its variables, the speed the different width etc
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        int speed = 10;
        skips++;
        if (started) {
            for (int i = 0; i < pipes.size(); i++) {
                Rectangle column = pipes.get(i);
                column.x -= speed;

            }
            if (skips % 2 == 0 && movement < 15) {
                movement += 2;

            }

            for (int i = 0; i < pipes.size(); i++) {
                Rectangle column = pipes.get(i);
                if (column.x + column.width < 0) {
                    pipes.remove(column);
                    if (column.y == 0) {
                        addPipes(false);

                    }
                }
            }
            bird.y += movement;

            for (Rectangle column : pipes) {
                if (column.y == 0 && bird.x + bird.width / 2 > column.x + column.width / 2 - 10 && bird.x + bird.width / 2 < column.x + column.width / 2 + 10) {
                    points++;

                }

                if (column.intersects(bird)) {
                    gameOver = true;

                    if (bird.x <= column.x) {
                        bird.x = column.x - bird.width;

                    }

                    else {
                        if (column.y != 0) {
                            bird.y = column.y - bird.height;

                        }
                        else if (bird.y < column.height) {
                            bird.y = column.height;

                        }
                    }
                }
            }

            if (bird.y > HEIGHT - 120 || bird.y < 0) {
                gameOver = true;

            }

            if (bird.y + movement >= HEIGHT - 120) {
                bird.y = HEIGHT - 120 - bird.height;
                gameOver = true;

            }
        }

        render.repaint();
    }
    // setting colours to the pipes and the background
    public void repaint(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        g.setColor(Color.cyan);
        g.fillRect(0, HEIGHT - 120, WIDTH, 120);

        g.setColor(Color.green);
        g.fillRect(0, HEIGHT - 120, WIDTH, 20);

        g.setColor(Color.red);
        g.fillRect(bird.x, bird.y, bird.width, bird.height);

        for (Rectangle column : pipes) {
            paintPipes(g, column);

        }

        // base colour, font and the size
        g.setColor(Color.white);
        g.setFont(new Font("Arial", 1, 100));

        // Before the game has started
        if (!started) {
            g.drawString("Click to start!", 75, HEIGHT / 2 - 50);

        }

        if (highScore.equals("")){
            highScore = this.topScores();

        }

        // when the game ends
        if (gameOver) {
            pointsSet();
            g.setColor(Color.red);
            g.drawString("Game Over!", 100, HEIGHT / 2 - 50);
            g.setFont(new Font("", Font.PLAIN, 40));
            g.drawString("High Score: ", HEIGHT/ 2 - 160, 400);
            g.drawString(String.valueOf(highScore), WIDTH / 2 + 50, 400);

        }

        //During the game
        if (!gameOver && started) {
            g.drawString(String.valueOf(points), WIDTH / 2 - 25, 100);
            g.setFont(new Font("", Font.PLAIN, 25));
            g.drawString("High Score " + highScore, 20, HEIGHT/ 2 - 370);

        }
    }

    /*
    The 2 functions below are for the creation and storing of the scores which
    takes place with the help if file reader and arraylist. The best score is retrieved from
    the file during every instance of the game as to know the high score. The top 5 scores
    are saved in the file.
     */
    public void pointsSet(){
        if(points > Integer.parseInt(highScore)){
            highScore = String.valueOf(points);
            File pointsFiles = new File("topScores.txt");
            if(!pointsFiles.exists()){
                try{
                    pointsFiles.createNewFile();

                }
                catch (Exception e){
                    e.printStackTrace();

                }
            }
            FileWriter writeFile;
            BufferedWriter writer = null;
            try{
                writeFile = new FileWriter(pointsFiles, true);
                writer = new BufferedWriter(writeFile, 5);
                writer.write(this.highScore + "\r\n");

            }
            catch(Exception e){

            }
            finally{
                try{
                    if(writer != null){
                        writer.close();

                    }
                }
                catch (Exception e){}

            }
        }
    }

    public String topScores(){

        FileReader readFile = null;
        BufferedReader pointReader = null;
        try{
            readFile = new FileReader("topScores.txt");
            pointReader = new BufferedReader(readFile);
            Scanner scan = new Scanner(readFile);
            ArrayList<String> listOfScores = new ArrayList<>(5);
            while(scan.hasNextLine()){
                String line = scan.nextLine();
                for(int i = 0; i <=5; i++){
                    listOfScores.add(line);

                }
            }
            Collections.sort(listOfScores);
            int max = 0;
            for(int i = 0; i < listOfScores.size(); i++){
                int val = Integer.valueOf(listOfScores.get(i));
                    if(val > max){
                    max = val;

                }
            }
            return(String.valueOf(max));

        }
        catch (Exception e){
            return "0";

        }
        finally{
            try{
                if(pointReader != null){
                    pointReader.close();

                }
            }
            catch (IOException e){
                return "0";

            }
        }
    }

    public static void main(String[] args) {
        flappyBird = new flappyBirdGame();

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        jump();

    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            jump();

        }
    }
    // Below are the functions for ll the mouse events and the keyboard events
    @Override
    public void mousePressed(MouseEvent e) { }

    @Override
    public void mouseReleased(MouseEvent e) { }

    @Override
    public void mouseEntered(MouseEvent e) { }

    @Override
    public void mouseExited(MouseEvent e) { }

    @Override
    public void keyTyped(KeyEvent e) { }

    @Override
    public void keyPressed(KeyEvent e) { }

}