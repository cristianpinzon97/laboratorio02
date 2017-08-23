package snakepackage;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

import enums.GridSize;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * @author jd-
 *
 */
public class SnakeApp {

    private static SnakeApp app;
    public static final int MAX_THREADS = 8;
    Snake[] snakes = new Snake[MAX_THREADS];
    private static final Cell[] spawn = {
        new Cell(1, (GridSize.GRID_HEIGHT / 2) / 2),
        new Cell(GridSize.GRID_WIDTH - 2,
        3 * (GridSize.GRID_HEIGHT / 2) / 2),
        new Cell(3 * (GridSize.GRID_WIDTH / 2) / 2, 1),
        new Cell((GridSize.GRID_WIDTH / 2) / 2, GridSize.GRID_HEIGHT - 2),
        new Cell(1, 3 * (GridSize.GRID_HEIGHT / 2) / 2),
        new Cell(GridSize.GRID_WIDTH - 2, (GridSize.GRID_HEIGHT / 2) / 2),
        new Cell((GridSize.GRID_WIDTH / 2) / 2, 1),
        new Cell(3 * (GridSize.GRID_WIDTH / 2) / 2,
        GridSize.GRID_HEIGHT - 2)};
    private JFrame frame;
    private static Board board;
    int nr_selected = 0;
    Thread[] thread = new Thread[MAX_THREADS];
    private JButton Iniciar, Pausar, Reanudar;
    public String estado="", mejor = "Mejor(s): ", peor = "Peor Serpiente : ";
    boolean primera = true;
    public static Object o = new Object();
    private JTextArea j1;
    private JScrollPane j2;

    public SnakeApp() {

        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        frame = new JFrame("The Snake Race");
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //frame.setSize(618, 640);
        frame.setSize(GridSize.GRID_WIDTH * GridSize.WIDTH_BOX + 17,
                GridSize.GRID_HEIGHT * GridSize.HEIGH_BOX + 130);
        frame.setLocation(dimension.width / 2 - frame.getWidth() / 2,
                dimension.height / 2 - frame.getHeight() / 2);
        board = new Board();
        frame.add(board, BorderLayout.CENTER);
        JPanel info = new JPanel(new GridLayout(1, 2));

        JLabel t1 = new JLabel();
        JLabel t2 = new JLabel();
        t2.setLayout(new BorderLayout());
        t1.setLayout(new BorderLayout());

        Iniciar = new JButton("Iniciar");
        Pausar = new JButton("Pausar");
        Reanudar = new JButton("Renaudar");
        JPanel actionsBPabel = new JPanel();
        actionsBPabel.add(Iniciar);
        actionsBPabel.add(Pausar);
        actionsBPabel.add(Reanudar);
        info.add(actionsBPabel);
        j1 = new JTextArea(mejor + "\n" + peor);
        j1.setEditable(false);
        j2 = new JScrollPane(j1);
        info.add(j2);
        frame.add(info, BorderLayout.SOUTH);
        frame.setVisible(true);

    }

    public static void main(String[] args) {
        app = new SnakeApp();
        app.prepareAcciones();
        app.init();

    }

    private void prepareAcciones() {
        Iniciar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                iniciar();
            }
        });
        Pausar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pausar();
            }
        });
        Reanudar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                reanudar();
            }
        });
    }

    private void init() {

        for (int i = 0; i != MAX_THREADS; i++) {
            snakes[i] = new Snake(i + 1, spawn[i], i + 1, this.o);
            snakes[i].addObserver(board);
            thread[i] = new Thread(snakes[i]);
        }
        
        

        System.out.println("Thread (snake) status:");
        for (int i = 0; i != MAX_THREADS; i++) {
            System.out.println("[" + i + "] :" + thread[i].getState());
        }

    }

    public void pausar() {
        if (!estado.equals("pausado")) {
            estado = "pausado";

            Thread.State st1 = Thread.State.WAITING;
            Thread.State st2 = Thread.State.TERMINATED;
            for (int i = 0; i < MAX_THREADS; i++) {
                while (!thread[i].getState().equals(st1) && !thread[i].getState().equals(st2)) {
                }
            }
            int tamaño = 0;
            ArrayList<Snake> tmp = new ArrayList<Snake>();
            for (Snake s : snakes) {
                if (s.getBody().size() >= tamaño) {
                    tamaño = s.getBody().size();
                }
            }
            mejor = "Mejor(s) : ";
            for (Snake s : snakes) {
                if (s.getBody().size() == tamaño && !s.isSnakeEnd()) {
                    mejor += "\n Serpiente #" + s.getIdt() + " con tamaño " + s.getBody().size();
                }
            }
            this.j1.setText(mejor + "\n" + peor);

        }

    }

    public void reanudar() {
        if (estado.equals("pausado")) {
            estado = "jugando";
            synchronized (o) {
                o.notifyAll();
            }

        }

    }

    public void iniciar(){
        if (!estado.equals("jugando")) {
            estado = "jugando";
        for (int i = 0; i != MAX_THREADS; i++) {
            thread[i].start();
        }

        }
        
    
    }
    
    public static SnakeApp getApp() {
        return app;
    }

    public synchronized void mori(Snake s) {
        if (primera) {
            peor = " Peor :" + "Serpiente #" + s.getIdt() + " con tamaño " + s.getBody().size();
            primera = false;
        }

    }

}
