package model;

import java.text.DecimalFormat;
import java.util.*;
import java.util.Map.Entry;

public class RobotImpl implements Robot {
    private final int id;
    private final int startPosX;
    private final int startPosY;
    private int currentPosX;
    private int currentPosY;
    private boolean busy;
    private final Status status;
    private final Field[][] field;
    private int blockCounter = 0;
    
    private int[] target;
    
    private Map<Item, Integer> order;
    private final DecimalFormat df = new DecimalFormat("00");

    public RobotImpl(int id, int startPosX, int startPosY, Field[][] field) {
        this.id = id;
        this.startPosX = startPosX;
        this.startPosY = startPosY;
        this.currentPosX = startPosX;
        this.currentPosY = startPosY;
        this.field = field;
        this.status = Status.IDLE;
        busy = false;
    }

    public void receiveOrder(Map<Item, Integer> order) {
        this.order = order;
    }

    /**
     * Bewegt den Robot zum naechsten Ziel und wenn die Order leer ist zu seiner BoxingPlant zurueck.
     */
    public void action() {
        char direction = 0;

        // Wenn die Order nicht leer ist, wird das naechste Feld gesucht
        if (order != null && !order.isEmpty()) {
            busy = true;
            // Speichert in target das naechste Feld
            this.target = destination();
            
        } else {
            // Wenn die Order leer ist, bewegt sich der Robot zurueck zu
            // seiner BoxingPlant
            target = new int[2];
            target[0] = startPosY;
            target[1] = startPosX; 
        }

        direction = findWay(this.target[0], this.target[1]);
        
        // Bewegung zum naechsten Feld
        switch (direction) {
        case 'N':
            moveTo(currentPosY - 1, currentPosX);
            break;
        case 'S':
            moveTo(currentPosY + 1, currentPosX);
            break;
        case 'W':
            moveTo(currentPosY, currentPosX - 1);
            break;
        case 'E':
            moveTo(currentPosY, currentPosX + 1);
            break;
        case 'A':
            if(order != null && !order.isEmpty()){
                load(); // Load Ausgabe auf der Konsole
                remove(); // Eintrag entfernen, nachdem der Robot angekommen ist
            }else{
                busy = false; // Robot ist bei seiner BoxingPlant angekommen
            }
            break;
        default:
            break;
        }
    }

    /**
     * Speichert die Koordinaten des ersten Elements aus der Map
     * und die Startposition des Robots.
     * @return Wenn die Liste leer ist, wird die Startposition zurueckgegeben,
     *         ansonsten die Koordinaten des ersten Elements aus der Liste.
     */
    int[] destination() {

        int[] result;
        int[] startPos = { startPosY, startPosX };
        int[] itemPos = { ((TreeMap<Item, Integer>) order).firstKey().productPosY(),
                ((TreeMap<Item, Integer>) order).firstKey().productPosX() };

        // Solange noch Items uebrig sind, hole das erste Item aus der TreeMap
        if (!order.isEmpty()) {
            result = itemPos;

        // Wenn keine Items mehr da sind, kehre zur Startposition zurueck
        } else {
            result = startPos;
        }
        return result;
    }

    /**
     * Findet die Richtung, in die sich der Robot auf dem Weg zum Item bewegt
     * Der Robot bewegt sich zu erst auf der x Achse wenn m�glich danach erst
     * auf der y Achse in Richtung ziel 
     * Wenn kein Zug mehr m�glich ist p�rft er ob die Robots auf den Feld bzw. den Felder auf die er normalerweise ziehen w�rde 
     * auf das Feld wollen auf den er steht wenn ja weicht er aus
     * Wenn der Robot sich zulange nicht bewegen kann weicht er ebenfalls aus 
     * 
     * @param positionY
     *            Y-Ziel-Koordinate des uebergebenen Items
     * @param positionX
     *            X-Ziel-Koordinate des uebergebenen Items
     * @return Gefundene Richtung (N, S, W, E) oder A (arrived at item)
     */
    private char findWay(int destinationY, int destinationX) {
        if (destinationX != currentPosX && fieldFree(currentPosY, currentPosX + Integer.compare(destinationX, currentPosX))) {
            if (destinationX < currentPosX) {
                return 'W';
            } else if (destinationX > currentPosX) {
                return 'E';
            }
        } else if (destinationY != currentPosY && fieldFree(currentPosY + Integer.compare(destinationY, currentPosY), currentPosX)) {
            if (destinationY < currentPosY) {
                return 'N';
            } else if(destinationY > currentPosY){
                return 'S';
            }
        } else if (destinationY == currentPosY && destinationX == currentPosX) {
            return 'A';
        }else if((currentPosY == destinationY 
                || field[currentPosY + Integer.compare(destinationY, currentPosY)][currentPosX].getTarget()[0] == currentPosY 
                && field[currentPosY + Integer.compare(destinationY, currentPosY)][currentPosX].getTarget()[1] == currentPosX)
                && (currentPosX == destinationX 
                || field[currentPosY][currentPosX + Integer.compare(destinationX, currentPosX)].getTarget()[0] == currentPosY 
                && field[currentPosY][currentPosX + Integer.compare(destinationX, currentPosX)].getTarget()[1] == currentPosX)){
            return evade(destinationY, destinationX);
        }
        if (blockCounter >= 5) {
            blockCounter = 0;
            return evade(destinationY, destinationX);
        } else {
            blockCounter++;
        }
        return 0;
    }


    /**
     * Prueft, ob ein Feld frei ist, oder sich ein Robot darauf befindet
     * 
     * @param positionY Y-Koordinate des Feldes, welches geprueft werden soll
     * @param positionX X-Koordinate des Feldes, welches geprueft werden soll
     * @return TRUE bei freiem Feld, sonst FALSE
     */
    private boolean fieldFree(int positionY, int positionX) {
        return positionY < Simulation.N && positionX < Simulation.N
                && positionY > -1 && positionX > -1
                && (!field[positionY][positionX].isBoxingPlant()
                || (positionY == startPosY && positionX == startPosX)) && field[positionY][positionX].robotID() == 0;

    }

    /**
     * Bewegt den Robot auf die Position der uebergebenen XY-Koordinaten
     * 
     * @param positionY Y-Koordinate des uebergebenen Feldes
     * @param positionX X-Koordinate des uebergebenen Feldes
     * @return Anzahl der Robots auf dem uebergebenem Feld
     */
    private int moveTo(int positionY, int positionX) {

        // Robot vom aktuellen Feld abmelden
        field[this.currentPosY][this.currentPosX].unReg();


        // Uebergebene Koordinaten zuweisen
        this.currentPosY = positionY;
        this.currentPosX = positionX;

        // Robot auf uebergebenem Feld anmelden
        field[positionY][positionX].reg(this);
        System.out.println("Robot [" + df.format(this.id()) + "]: " + "Gehe zu Position Y: " + df.format(currentPosY) + " X: " + df.format(currentPosX));
        return field[positionY][positionX].hasRobots();
    }


    /**
     * Ausweichroutine fuer Robot
     * 
     * Prueft je nachdem in welche Richtung der Robot sich bewegen wuerde, wenn
     * kein umliegendes Feld blockiert waere, alle umliegende Felder in einer
     * festen Reihenfolge, wenn kein freies Feld gefunden wurde wird 0
     * zurueckgegeben 
     * Reihenfolge x-Achse N,S,W,E  
     * Reihenfolge y-Achse W,E,N,S
     */
    private char evade(int destinationY, int destinationX) {
        if (currentPosX != destinationX) {
            if (fieldFree(currentPosY - 1, currentPosX)) {
                return 'N';
            } else if (fieldFree(currentPosY + 1, currentPosX)) {
                return 'S';
            } else if (fieldFree(currentPosY, currentPosX - 1)) {
                return 'W';
            } else if (fieldFree(currentPosY, currentPosX + 1)) {
                return 'E';
            } else {
                return 0;
            }
        } else {
            if (fieldFree(currentPosY, currentPosX - 1)) {
                return 'W';
            } else if (fieldFree(currentPosY, currentPosX + 1)) {
                return 'E';
            } else if (fieldFree(currentPosY - 1, currentPosX)) {
                return 'N';
            } else if (fieldFree(currentPosY + 1, currentPosX)) {
                return 'S';
            } else {
                return 0;
            }
        }
    }

    /**
     * Entfernt den ersten Eintrag aus der Map, nachdem das Item geholt wurde.
     * @return Gibt das entfernte Item zurueck.
     */
    Entry<Item, Integer> remove() {
        return ((TreeMap<Item, Integer>) order).pollFirstEntry();
    }

    private void load() {
        System.out.println("Robot [" + df.format(this.id()) + "]: Lade Item bei Y: " + df.format(currentPosY) + " X: " + df.format(currentPosX));
    }

    public int id() {
        return id;
    }

    public boolean isBusy() {
        return busy;
    }

    @Override
    public int getStartPosX() {
        return this.startPosX;
    }

    @Override
    public int getStartPosY() {
        return this.startPosY;
    }

    @Override
    public int getCurrentPosX() {
        return this.currentPosX;
    }

    @Override
    public int getCurrentPosY() {
        return this.currentPosY;
    }

    @Override
    public Status getStatus() {
        return this.status;
    }
    
    public String getOrderInfos(){
    	//System.out.println(order.toString());
    	return order.toString();
    }

	@Override
	public Map<Item, Integer> getOrder() {
		return this.order;
	}

    @Override
    public int[] getTarget() {
        return this.target;
    }



}
