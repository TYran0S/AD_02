package model;

public interface BoxingPlant extends Field {

    /*
     * Eingabe:  keine
     *
     * Ausgabe:  die ID der BoxingPlant
     */
    public int id();

    /*
     * Eingabe:  keine
     * 
     * Ausgabe:  ob die BoxingPlant busy ist
     */
    public boolean isBusy();

    /*
     * Eingabe:  eine Map mit Objekten des Typ Item als Schlüssel 
     *           und ihrer Anzahl(Integer) als zugehöriger Value
     * 
     * Ausgabe:  keine
     *
     * Funktion: weist der BoxingPlant eine Bestellung zu, 
     *           setzt sie auf busy und berechnet die gesamte Packzeit 
     *           abhängig von der Anzahl an Items
     */
    public void receiveOrder(Order order);

    /*
     * Funktion:  prueft, ob die eine Bestellung vorhanden ist 
     *            und der zugehörige Robot nicht beschäftigt ist
     *            
     *            ja: uebergibt dem Robot die Order und setzt die order wieder auf null
     *            wenn der Robot unterwegs ist, wird  nach Ablauf des timeCounter eine action() ausgeführt
     *            wenn keine Bestellung vorliegt, der Robot nicht mehr busy ist, aber noch Packingtime uebrig ist,
     *            muss noch eine Bestellung verpackt werden
     *            wenn die Order leer ist, der Robot nicht busy und keine Packingtime uebrig ist, ist die Abarbeitung
     *            der Bestellung fertig
     */
    public void action();

    //JUnit
    public Robot getRobot();

    public Status getStatus();
    public int coordinateX();
    public int coordinateY();
    public int getTemp_CLTIME_cnt();
    public int getPackingTime();
    public int getLoadTime();
}
