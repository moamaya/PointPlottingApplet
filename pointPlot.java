
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.border.*;
import java.awt.geom.Line2D;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.lang.*;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;



public class pointPlot {

    //These are all variablebles used throughout this class, and others below
    private JFrame AppWindow;
    private JRadioButton btn1;
    private JRadioButton btn2;
    private JButton btn3;
    private JButton menuItem1;
    private JPanel rightSide = new JPanel();
    private DefaultListModel lstModel = new DefaultListModel();
    private DefaultListModel lstModel2 = new DefaultListModel();
    private canvas canvas1;
    private ArrayList<Double> xi = new ArrayList<Double>();
    private ArrayList<Double> yj = new ArrayList<Double>();
    private JList list1;
    private JList list2;
    private Boolean btn3Identifier = false;
    private Boolean btn2Identifier = false;
    private ArrayList<Shape> lines = new ArrayList<Shape>();
    private ArrayList<Point> points;


    public static void main(String[] args) {
        // This just initializes my app
        graphApp run = new graphApp();
        run.AppWindow.setVisible(true);

    }
        //initialize all variables using constructors
    private graphApp(){
        setBtn1();
        setBtn2();
        setBtn3();
        setBtns();
        setLists();
        setCanvas();
        setAppWindow();

    }

        //this will setup by window and layout
    private void setAppWindow(){
        AppWindow = new JFrame();
        int HEIGHT = 400;
        int WIDTH = 570;
        AppWindow.setSize(WIDTH, HEIGHT);
        AppWindow.setBackground(Color.GRAY);
        AppWindow.add(rightSide, BorderLayout.EAST);
        AppWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        AppWindow.add(canvas1, FlowLayout.LEFT);
        AppWindow.setMinimumSize(new Dimension(WIDTH,HEIGHT));

        //menu bar isnt complete, but will be used anyway..
        JMenuBar bar = new JMenuBar();
        JMenu menu = new JMenu("File");
        JMenu menu2 = new JMenu("Edit");

        menu2.setMnemonic(KeyEvent.VK_O);
        menu.setMnemonic(KeyEvent.VK_O);

        ButtonGroup group = new ButtonGroup();
        ButtonGroup group2 = new ButtonGroup();

        //purpose of this button is to undo a line, but I couldnt get it to work
        menuItem1 = new JButton("Undo Line");
        menuItem1.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                    }
                }
        );
        group2.add(menuItem1);
        menu2.add(menuItem1);

        JButton menuItem2 = new JButton("Import File");
        group.add(menuItem2);
        menu.add(menuItem2);

            //The export button will export the xi and yj list, which contain points, to the source location
        JButton menuItem3 = new JButton("Export File");
        menuItem3.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        PrintWriter writer = null;
                        try {
                            writer = new PrintWriter("Drawing_Points.txt");
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        for (int c = 0; c<points.size();c++) {
                            writer.println(xi.get(c) + " " + yj.get(c));
                        }
                        writer.close();
                    }
                }
        );
        group.add(menuItem3);
        menu.add(menuItem3);

        bar.add(menu);
        bar.add(menu2);
        AppWindow.setJMenuBar(bar);

        //AppWindow.setResizable(false);
    }

    private void setBtns(){
        rightSide.setLayout(new BoxLayout(rightSide,BoxLayout.Y_AXIS));

        JPanel btns = new JPanel();
        ButtonGroup btns1 = new ButtonGroup();
        btns1.add(btn1);
        btns1.add(btn2);
        btns.setBorder(new EmptyBorder(60,20,30,30));
        btns.setLayout(new BoxLayout(btns,BoxLayout.Y_AXIS));
        btns.add(btn1);
        btns.add(btn2);

        rightSide.add(btns);
    }

    private void setLists(){
        JPanel cont2 = new JPanel();
        cont2.setLayout(new BoxLayout(cont2,BoxLayout.Y_AXIS));
        JPanel lists = new JPanel();
        lists.setLayout(new GridLayout(1,2,10,10));

        list1 = new JList(lstModel);
        list2 = new JList(lstModel2);

        lists.add(list1);
        lists.add(list2);

        cont2.setBorder(BorderFactory.createTitledBorder(new LineBorder(Color.BLACK,1),"Graph Creation"));
        cont2.add(lists);
        cont2.add(btn3);

        rightSide.add(cont2);
    }

    private void setCanvas() {
        canvas1 = new canvas();
    }

    private void setBtn1(){ btn1 = new JRadioButton("Design"); }

    private void setBtn2(){
        btn2 = new JRadioButton("Edit");
        btn2.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        btn2Identifier = true;
                    }
                }
        );
    }

    //this button will add a line between two points
    private void setBtn3(){
        btn3 = new JButton("Add_Edge");
        btn3.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        //System.out.println("the button is pressed");
                        if (!list1.isSelectionEmpty()) {
                            btn3Identifier = true;
                            canvas1.repaint();
                        }
                    }
                }
        );
    }

    //this class creates my drawing canvas
    public class canvas extends JPanel {
        //private static final long serialVersionUID = 1L;
        int j = 0;
        int i = 0;
        Shape line;
        private double screenX = 0;
        private double screenY = 0;
        private double myX = 0;
        private double myY = 0;
        private int whichPoint;

        canvas() {
            setPreferredSize(new Dimension(420,420));
            setBorder(BorderFactory.createMatteBorder(10,10,5,10,Color.GRAY));
            setBackground(Color.WHITE);
            points = new ArrayList<Point>();

            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (btn1.isSelected()) {
                        points.add(new Point(e.getX(), e.getY()));
                        String x = "h" + i;
                        String y = "v" + j;
                        xi.add(points.get(i).getX()); //xi and yj store the points and show as hi and vj on the screen
                        yj.add(points.get(i).getY());
                        lstModel.addElement(x);
                        lstModel2.addElement(y);
                        i = i + 1;
                        j = j + 1;
                        repaint();
                    }
                    else {
                        revalidate();
                        repaint();
                    }
                }
            });
        }

        @Override
        public void paintComponent(Graphics g) {
            if (btn1.isSelected()) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.red);

                for (Point point : points) {
                    g2.fillOval(point.x, point.y, 5, 5);
                }
            }

            if (btn3Identifier) {
                btn2Identifier = false;

                int index_1 = list1.getSelectedIndex();
                int index_2 = list2.getSelectedIndex();

                //System.out.println(index_1 + " " + index_2);

                line = new Line2D.Double(xi.get(index_1), yj.get(index_1), xi.get(index_2), yj.get(index_2));
                lines.add(line);


                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.red);
                int h = lines.size();
                for (Point point : points) {
                    g2.fillOval(point.x, point.y, 5, 5);
                    for (int c =0; c<h;c++) {
                        g2.draw(lines.get(c));
                    }
                }
            }

            if (btn2Identifier) {
                //btn3Identifier = false;
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.red);

                for (Point point : points) {
                    g2.fillOval(point.x, point.y, 5, 5);
                }

                addMouseListener(new MouseListener() {

                    @Override
                    public void mouseClicked(MouseEvent e) { }

                    @Override
                    public void mousePressed(MouseEvent e) {
                        screenX = e.getXOnScreen();
                        screenY = e.getYOnScreen();
                        if (points.size() == 1) {
                            myX = points.get(0).getX();
                            myY = points.get(0).getY();
                        }
                        else {
                            double Xdiff = Math.abs(screenX - points.get(0).getX());
                            double Ydiff = Math.abs(screenY - points.get(0).getY());
                            //System.out.println("Ydiff is " +Ydiff);
                            for (int c = 0; c <= points.size()-1; c++)
                                if (Math.abs(screenX - points.get(c).getX()) < Xdiff) {
                                    Xdiff = Math.abs(screenX - points.get(c).getX());
                                    myX = points.get(c).getX();
                                    whichPoint = c;
                                    //System.out.println(c);
                                }

                            for (int c = 0; c <= points.size()-1; c++) {
                                //System.out.println(screenY + " " + screenX);
                                //System.out.println("Point y " +points.get(c).getY() + "Point c y is "+points.get(0).getY());
                                //System.out.println("For loop test is " +Math.abs(screenY - points.get(c).getY()));
                                if (Math.abs(screenY - points.get(c).getY()) < Ydiff) {
                                    Ydiff = Math.abs(screenY - points.get(c).getY());
                                    myY = points.get(c).getY();
                                    //System.out.println(c);
                                }
                            }
                        }

                    }

                    @Override
                    public void mouseReleased(MouseEvent e) { }

                    @Override
                    public void mouseEntered(MouseEvent e) { }

                    @Override
                    public void mouseExited(MouseEvent e) { }

                });
                addMouseMotionListener(new MouseMotionListener() {

                    @Override
                    public void mouseDragged(MouseEvent e) {

                        double deltaX = e.getXOnScreen() - screenX;
                        double deltaY = e.getYOnScreen() - screenY;

                        xi.set(whichPoint, myX + deltaX);
                        yj.set(whichPoint, myY + deltaY);

                        Point g = new Point(xi.get(whichPoint).intValue(),yj.get(whichPoint).intValue());
                        points.set(whichPoint,g);

                        //Shape line2 = new Line2D.Double(xi.get(whichPoint), yj.get(whichPoint), xi.get(0), yj.get(0));
                        //lines.set(1,line2);

                        repaint();

                    }

                    @Override
                    public void mouseMoved(MouseEvent e) { }

                });

            }
        }

    }

}
