package gui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import java.io.File;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.IconUIResource;

import model.*;
import model.Robot;

public class HauptFrame extends JFrame implements HauptFrame_interface {

    /* --------- Variablen ------------ */
    private Simulation controller;
    private JMenuItem startEntry;
    private JPanel hauptpane;
    JLabel[][] feld;
    textFenster textFenster;
    private JToggleButton pauseButton;
    private boolean paused = false;
    static HauptFrame myMainWindow;
    int[] RobotPosX;
    int[] RobotPosY;
    private int fields;
    // Festlegen der max Fenstergr��e
    private Dimension screen = new Dimension((int) ((Toolkit
                    .getDefaultToolkit().getScreenSize().width) * 0.75),
            (int) ((Toolkit.getDefaultToolkit().getScreenSize().height) * 0.75));

    public HauptFrame(Simulation controller) {
        this.controller = controller;
        myMainWindow = this;

        setTitle("Warehouse 13.37");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocation(200, 200);
        setMinimumSize(new Dimension(400, 400));
        setMaximumSize(screen);
        erzeugeMenus();
        erzeugeAnzeige();
        pack();

        setVisible(true);
        // aendereParameter();
    }

    // Erzeugt die Men�leiste und weist ActionListener zu
    private void erzeugeMenus() {
        /* Men�leiste erzeugen */
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        /* Men� Datei erzeugen */
        JMenu menuDatei = new JMenu("Men�");
        menuBar.add(menuDatei);

        /* Ende */
        JMenuItem endeEntry = new JMenuItem("Beenden");
        endeEntry.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        menuDatei.add(endeEntry);
    }

    //  // neu Einlesen der CSV Datei.
    //  public void aendereParameter() {
    //      JFileChooser fc = new JFileChooser();
    //      // Filter damit nur CSV Dateien angezeigt werden
    //      fc.setFileFilter(new FileFilter() {
    //          @Override
    //          public boolean accept(File f) {
    //              return f.isDirectory()
    //                      || f.getName().toLowerCase().endsWith(".ini");
    //          }
    //
    //          @Override
    //          public String getDescription() {
    //              return "INI";
    //          }
    //      });
    //
    //      int state = fc.showOpenDialog(null);
    //      // Wenn "�ffnen" gedr�ckt
    //      if (state == JFileChooser.APPROVE_OPTION) {
    //          File file = fc.getSelectedFile();
    //          controller.readCSV(file.getAbsolutePath());
    //      }
    //      // Wenn "Abbrechen" gedr�ckt
    //      else
    //          System.out.println("Auswahl abgebrochen");
    //
    //  }

    public void erzeugeAnzeige() {
        getContentPane().setLayout(new GridLayout(1, 0, 0, 0));

        hauptpane = new JPanel();
        getContentPane().add(hauptpane);
        hauptpane.setLayout(new BorderLayout(0, 0));

        JPanel buttonPanel = new JPanel();
        hauptpane.add(buttonPanel, BorderLayout.NORTH);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

        JButton btnStart = new JButton("Start");
        buttonPanel.add(btnStart);
        btnStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.run_simuation();
            }
        });

        JButton btnNchsterTakt = new JButton("N\u00E4chster Takt");
        buttonPanel.add(btnNchsterTakt);
        btnNchsterTakt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.next_simuation();
            }
        });
        //Enterbutton funktioniert ohne Fokus
        this.getRootPane().setDefaultButton(btnNchsterTakt);



        pauseButton = new JToggleButton();
        pauseButton.setText("Pause");       
        pauseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(paused)
        {
            paused = false;
            pauseButton.setText("Pause");
            controller.run_simuation();                 
        }
        else
        {
            paused = true;
            pauseButton.setText("Fortfahren");                  
            controller.pause_simuation();
        }
            }
        });
        buttonPanel.add(pauseButton);

    }

    /* Erstellt Ansicht f�r neues Lager */
    public void newWarehouse(int fields, int stations, int robots, Set<Item> item) {
        this.fields = fields;


        RobotPosX = new int[robots+1];
        RobotPosY = new int[robots+1];
        JPanel gridPane = new JPanel();
        gridPane.setLayout(new GridLayout(fields, fields));
        hauptpane.add(gridPane, BorderLayout.CENTER);

        feld = new JLabel[fields][fields];

        for (int y = 0; y < fields; y++) {
            for (int x = 0; x < fields; x++) {
                feld[x][y] = (new JLabel("", JLabel.CENTER));
                feld[x][y].setBorder(BorderFactory.createLineBorder(Color.black));
                if (y == fields - 1 && x < stations) {
                    feld[x][y].setText("");
                    feld[x][y].setBorder(BorderFactory.createLineBorder(Color.orange, 3));
                    feld[x][y].setIcon(new ImageIcon(getClass().getResource(("/paket_klein.jpg"))));
                }
                gridPane.add(feld[x][y]);
            }

        }

        for (Item elem : item) {
            feld[elem.productPosX()][elem.productPosY()].setText(String.valueOf(elem.id()));
        }

        pack();
        textFenster = new textFenster(robots);
        textFenster.setBounds(myMainWindow.getX() + myMainWindow.getWidth(), myMainWindow.getY(), 100, myMainWindow.getHeight());

    };


    /* Aktualisiert Warenhaus ansicht */
    //  public void showRobotState(final int robotName, final int xPos, final int yPos,
    //  Item[] ladung, final int xZiel, final int yZiel, final Status status, final Set<Item> item, final int loadTime) {
    public void showRobotState( final Warehouse whouse, final Robot rob, final Set<Item> item, 
            final int loadTime, final int xZiel, final int yZiel, final int packingTime ) {
        String output = new String();
        output = "OrderId   ItemId   Gewicht   Menge   Robot\n";
        //�bersicht aktualisieren
        if (!whouse.getOrder().isEmpty()) {
            //System.out.println("-----------------------Auftraege-------------------");
            for ( Order map : whouse.getOrder()  ) {
                for (Entry<Item, Integer> entry : map.getMap().entrySet()) {
                    String id = "na";
                	for(int i = 0; i < whouse.getBplants().length; i++)
                	{
                		if((whouse.getBplants()[i].getRobot().getOrder() != null) && (whouse.getBplants()[i].getRobot().getOrder().getOrderId() == map.getOrderId()))
                		{
                			id = String.valueOf(whouse.getBplants()[i].getRobot().id());
                		}
                	}
                	//System.out.println(entry.getKey().id() + "#####"+ entry.getValue().toString());
                    output += String.format("%4s %8s %8s %8s %8s" , map.getOrderId() , entry.getKey().id() , entry.getKey().size() , entry.getValue().toString(), id);
                    output += "\n";
                }
            }
        }

        //System.out.println("-----------------------End Auftraege-------------------");
        if(rob.getOrder() != null) if (!rob.getOrder().getMap().keySet().isEmpty()) {

            Item realitem = (rob.getOrder().getMap().keySet()).iterator().next();
            int menge   = rob.getOrder().getMap().values().iterator().next();
            int gewicht = realitem.size();
            textFenster.refresh(rob.id(), rob.getCurrentPosX(),
                    rob.getCurrentPosY(), xZiel, yZiel,
                    String.valueOf(realitem.id()), String.valueOf(gewicht),String.valueOf(menge), output);
        } 
        else
        {
            textFenster.refresh(rob.id(), rob.getCurrentPosX(),
                    rob.getCurrentPosY(), xZiel, yZiel,
                    "","","",  output);

        }
        for (Item elem : item) {
            feld[elem.productPosX()][elem.productPosY()].setText(String.valueOf(elem.id()));
        }
        
        // Anzeige aktualisieren
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                // Alte Position l�schen
                if (RobotPosY[rob.id()] != fields-1) {
                    feld[RobotPosX[rob.id()]][RobotPosY[rob.id()]].setIcon(null);
                }
                feld[RobotPosX[rob.id()]][RobotPosY[rob.id()]].setText("");
                feld[RobotPosX[rob.id()]][RobotPosY[rob.id()]].setForeground(Color.BLACK);


                // Ziel? --> verpacken Bildchen
                if (rob.getCurrentPosX() == xZiel && rob.getCurrentPosY() == yZiel && yZiel != fields-1) {
                    feld[RobotPosX[rob.id()]][RobotPosY[rob.id()]].setText("");
                    feld[rob.getCurrentPosX()][rob.getCurrentPosY()].setText("R" + rob.id() + ":" + loadTime );
                }else if(rob.getCurrentPosX() == rob.getStartPosX() && rob.getCurrentPosY() == rob.getStartPosY() )
        {
            feld[RobotPosX[rob.id()]][RobotPosY[rob.id()]].setText("");
            feld[rob.getCurrentPosX()][rob.getCurrentPosY()].setText("R" + rob.id() + ":" + packingTime );
        }else{
            // Neue Position eintragen
            feld[rob.getCurrentPosX()][rob.getCurrentPosY()].setText("R" + rob.id());
        }

        switch (rob.getStatus()) {
            case IDLE:
                feld[rob.getCurrentPosX()][rob.getCurrentPosY()].setForeground(Color.GREEN);
                break;
            case BUSY:
                feld[rob.getCurrentPosX()][rob.getCurrentPosY()].setForeground(Color.YELLOW);
                break;
            case BOXING:
                feld[rob.getCurrentPosX()][rob.getCurrentPosY()].setForeground(Color.BLUE);
                break;
            case MOVING:
                feld[rob.getCurrentPosX()][rob.getCurrentPosY()].setForeground(Color.WHITE);
                break;
            case LOADING:
                feld[rob.getCurrentPosX()][rob.getCurrentPosY()].setForeground(Color.RED);
                break;
            default:
                feld[rob.getCurrentPosX()][rob.getCurrentPosY()].setForeground(Color.PINK);
                break;
        }


        // Position zum sp�teren l�lschen zwischenspeichern
        RobotPosX[rob.id()] = rob.getCurrentPosX();
        RobotPosY[rob.id()] = rob.getCurrentPosY();             

            }
        });
        
        //pack();

    }

    /* Zeigt Fehlermeldung bei Fehlverhalten */
    public void abbruch(String fehlermeldung) {
        textFenster.abbruch(fehlermeldung);
    };

    /* Zeigt �bersicht nach erfolgreichem Abarbeiten der Auftraege */
    public void beendet(int benoetigteTakte, int summeAuftraege) {
        textFenster.beendet(benoetigteTakte, summeAuftraege);
    };
    }
