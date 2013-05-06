package model;

public interface Robot {

    /*
     * Eingabe:  eine Map die Item und Iteger enthalt
     *
     * Ausgabe:  keine
     *
     * Funktion: Wenn der Robot eine Order hat Arbeitet er alle Items in der liste ab und 
     * 	         wenn die Order leer ist fahrt er zuruck zu seiner Startposition
     * 	    	 Setzt busy auf true wenn er eine Order hat und auf false wenn er
     * 	    	 seine Startposition wieder erreicht hat
     */
    public char action();

    /*
     * Eingabe:  eine Map die Item und Iteger enthalt
     *
     * Ausgabe:  keine
     *
     * Funktion: gibt dem Robot eine neue Order
     */
    public void receiveOrder(Order order);

    /**
     * Ergaenzug team2
     * Gibt die Item map des Robots nach aussen um seine Auftraege abgreifbar zu machen
     * @return Item MAP
     */
    public Order getOrder();
    
    //getter
    public int getStartPosX();
    public int getStartPosY();
    public int getCurrentPosX();
    public int getCurrentPosY();
    public Status getStatus();
    public boolean isBusy();
    public int id();
    
    /**
     * getter fuer die momentane Destination des Robots
     * @return Zielkoordinaten (gedreht [Y][X])
     */
    public int[] getTarget();
    
    public String getOrderInfos();

    public int getItemLoadTime();
}
