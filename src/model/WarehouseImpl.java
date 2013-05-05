package model;

import java.text.DecimalFormat;
import java.util.*;

public class WarehouseImpl implements Warehouse {
    private Field[][] warehouse;
    private Queue<Order> orderQueue;
    private List<Order> orderCopy = new ArrayList<Order>(); // Liste mit den Orders vom Anfang, dient zur Darstellung im TextFrame
    private BoxingPlant[] bplants;
    private boolean done;
    private final DecimalFormat df = new DecimalFormat("00");
    boolean firstcall = true;

    public WarehouseImpl() {
        new WarehouseImpl(Item.factory());
    }

    public WarehouseImpl(List<Item> itemList) {
        int temp_N = Simulation.N;
        int temp_NUMBOXINGPLANTS = Simulation.NUMBOXINGPLANTS;

        warehouse = new Field[temp_N][temp_N];
        orderQueue = new LinkedList<>();
        bplants = new BoxingPlant[temp_NUMBOXINGPLANTS];
        done = false;

        // Alle vorgesehene Fields mit StorageAreaImpl initialisieren und Items zuweisen
        for(Item i : itemList) {
            warehouse[i.productPosY()][i.productPosX()] = new FieldImpl(i);
        }

        // Alle vorgesehene Fields mit BoxingPlantImpl initialisieren und RobotImpl zuweisen
        Robot tmpBot;
        int count = 1;

        // temp_N-1 = Letzte Zeile
        for (int i = 0; i < warehouse[temp_N - 1].length; i++) {

            // Nur Zuweisungen bei null vornehmen
            if (warehouse[temp_N - 1][i] == null) {

                if (i < temp_NUMBOXINGPLANTS) {

                    // Robot erstellen
                    tmpBot = new RobotImpl(count, i, temp_N - 1, warehouse);

                    // boxingplant erstellen
                    warehouse[temp_N - 1][i] = new BoxingPlantImpl(count, i, temp_N - 1, tmpBot);

                    // Extra Referenz fuer schnelleren Zugriff auf boxingplant erstellen
                    bplants[count - 1] = (BoxingPlant) warehouse[temp_N - 1][i];

                } else {

                    // Fake BoxingPlant erstellen (unterste Zeile nur fuer BoxingPlants reservieren)
                    warehouse[temp_N - 1][i] = new FieldImpl(count, i, temp_N - 1);
                }
                count++;
            }
        }
    }

    public void action() {
    	if(firstcall){
    		firstcall = false;
    		for(int i = 0;i<orderQueue.size();i++){
    			orderCopy.add((Order) orderQueue.toArray()[i]);
    		}
    	}
    	
        // Index fuer eine bPlant, die idle ist
        int idle;

        // Wenn alle bPlants fertig sind und keine weiteren Bestelungen vorliegen
        if (bPlantsDone() && orderQueue.isEmpty()) {
            done = true;
        }

        // Wenn Bestellungen vorliegen
        for (BoxingPlant bplant : bplants) {
            if (!orderQueue.isEmpty()) {
                // freie bPlant suchen
                idle = findIdleBPlant();

                // dieser die Bestellung zuweisen
                if (idle != 0) {
                    bplants[idle - 1].receiveOrder(orderQueue.remove());

                }
            }

            // allen bplants nacheinander ein action-Signal geben
            bplant.action();
        }
    }

    public void takeOrder(Order order) {
        orderQueue.add(order);
        done = false;
    }

    /*
     * Der return wert ist der index + 1
     * Der return wert ist 0, falls es keine idle bplant gibt
     */
    private int findIdleBPlant() {
        int ret = 0;

        for (int i = 0; i < bplants.length; i++) {
            if (!bplants[i].isBusy()) {
                return i + 1;
            }
        }

        return ret;
    }

    /*
     * Kontrolliert ob alle bplants fertig sind
     */
    private boolean bPlantsDone() {
        for (BoxingPlant bplant : bplants) {
            if (bplant.isBusy()) {
                return false;
            }
        }

        return true;
    }

    public boolean notDone() {
        return !done;
    }

    String toStringMini() {
        int temp_N = Simulation.N;

        StringBuilder ret = new StringBuilder();

        for (int i = 0; i < temp_N + 2; i++) {
            ret.append('#');
        }
        ret.append('\n');

        for (Field[] aWarehouse : warehouse) {
            ret.append('#');

            for (int x = 0; x < warehouse.length; x++) {
                if (aWarehouse[x].hasRobots() > 1) {
                    ret.append('X');
                } else if (aWarehouse[x].hasRobots() == 1) {
                    ret.append(aWarehouse[x].robotID());
                } else {
                    if (aWarehouse[x].isBoxingPlant()) {
                        ret.append('B');
                    } else {
                        ret.append('.');
                    }
                }
            }

            ret.append("#\n");
        }

        for (int i = 0; i < temp_N + 2; i++) {
            ret.append('#');
        }
        ret.append('\n');

        return ret.toString();
    }

    String toStringMaxi() {

        int border;
        
        StringBuilder output = new StringBuilder();
        StringBuilder xFrame = new StringBuilder("####");
        StringBuilder frame;

        for (int y = 0; y < warehouse.length; y++) {

            // Y-Koordinaten anzeigen
            output.append('#').append(df.format(y)).append("#");

            for (int x = 0; x < warehouse.length; x++) {

                // Einmalig das X-Koodinaten Frame erstellen
                if(y == 0) {
                    xFrame.append('#').append(df.format(x)).append("#"); // X-Koordinate erzeugen
                }
     
                
                if (warehouse[y][x].hasRobots() > 1) {
                    output.append("[XX]"); // Robot Crash
                } else if (warehouse[y][x].hasRobots() == 1) {
                    output.append("[").append(df.format(warehouse[y][x].robotID())).append("]");
                } else {
                    if (warehouse[y][x].isBoxingPlant()) {
                        output.append("[BX]"); // BoxingPlant
                    } else {
                        output.append("[  ]"); // Field
                    } // else
                } // else
            } // for
            output.append("#\n"); // Zeilenumbruch an Y-Zeile anhaengen
        } // for

        border = output.indexOf("\n"); // Zeilenbreite in border speichern
        frame = new StringBuilder(""); // Leeren StringBuilder initialisieren

        // String mit der Laenge einer Zeilenbreite mit Rauten fuellen
        for (int i = 0; i < border; i++) {
            frame.append("#");
        }

        frame.append("\n"); // Zeilenumbruch in Rauten-String anhaengen
        xFrame.append("#\n"); // Zeilenumbruch mit Raute xFrame anhaengen
        output.insert(0, frame); // Rauten-Abgrenzung fuer X-Koordinaten einfuegen
        output.insert(0, xFrame); // X-Koordinaten einfuegen
        output.insert(0, frame); // Erste Zeile mit Rauten befuellen
        output.append(frame); // Letzte Zeile mit Rauten befuellen

        return output.toString();
    }

    @Override
    public String toString() {
        int tmp = Simulation.N;

        if (tmp >= 10) {
            return toStringMaxi();
        } else {
            return toStringMini();
        }
    }

    @Override
    public Field[][] getWarehouseArr() {
        return this.warehouse;
    }

    @Override
    public Queue<Order> getOrderQueue() {
        return this.orderQueue;
    }
    
    public List<Order> getOrder() {
    	return this.orderCopy;
    }

    @Override
    public BoxingPlant[] getBplants() {
        return this.bplants;
    }



}
