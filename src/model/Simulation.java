package model;

import gui.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;


/**
 *  Diese Klasse stellt im jetzigen zustand einen Prototypen f�r unsere aufgabenzihle dar.
 *  Es soll der orientierung dinen und ist nicht!!! endg�ltig.
 *  
 *  Anmerkungen stehen als kommentar daneben
 *  
 *  Funktuion:
 *  1. Initialisierung der Simulation (Konstuktore ) �ber CSV Benutzerfile + Inidatei 
 *  	--> new ReadCSV("src/aufgabe2_1/info.ini") usw
 *  2. Anlegen der Simulationsrelevanten Strukturen
 *  3. Anzeige der Gui als Userschnuittstelle
 *  4. Das Anbieten der Laufzeitfunktionalit�ten der Simulation  (Step und Video)
 *  
 *  
 *  TODO:
 *  -Gui aktualisierung noch nicht getestet !! 
 *  
 *  Anmerkungen:
 *  -optimierbar ?? for (int i = 0; i < 60 ; i++) {	whouse.takeOrder(rcsv.readOrder(item));	}
 *  
 *  
 *  
 * @author Gruppe 1
 * @version 0.8 
 * 29.4
 * 
 *
 */
public class Simulation  implements Control { //in fassung 1.0 extends Simulation so ben�tigen wir keinen eingriff
	// Diese Umgebungsvariablen werden gr��tenteils durch csv �berschrieben 
	public static  int N = 20;  //  Zeilenanzahl ? 
    public static  int NUMBOXINGPLANTS = 20; //anzahl der waarenfelder --csv
    public static  int ORDERMAXSIZE = 50; //maximale bestellgr��e
    public static  int MAXCAPACITY = ORDERMAXSIZE; //maximale roboter tragekapazit�t
    public static  int NUMROBOTS = NUMBOXINGPLANTS; //Anzahl der robotter
    public static  int CLTIME = 5; //vermutung irgenwas mit der verpackzeit ????
    public static  int PPTIME = 5; //vermutung irgenwas mit der verpackzeit ????
    private static final int refreshtime = 1000  ; // die Zeit bis zum n�chsten schritt in ms 10*1000 = 10 Sekunden
    public static boolean TEST = false; //teststeuerung der vorgruppe
    
    
    private Warehouse whouse; // internes Handle f�r das Warenhaus
    private boolean simstatus; // interne anzeige f�r den Simulations (takte &sim) //true bei sim run
    private final HauptFrame_interface gui;
    private Set<Item> itemSet;  //damit die items die in Keiner bestellung vorkommen nicht angezeigt werden
    
    
	/**
	 * Hier CSV�s Datein auslesen und umgebungsvariablen initialisieren 
	 * und Warehouse mit bestellungen anlegen
	 * und Gui Instanzieren 
	 * ende Init �bergang zum simulationsmodus
	 */
    private Simulation(){
		simstatus=false; //stat ist falste bei sim //true zu testzwecken
		//________________________________________start csv
		
		readCSV("CSV/info.ini");
		gui = new HauptFrame(this);
		gui.newWarehouse(N, NUMBOXINGPLANTS, NUMROBOTS, itemSet);
       
		// // Manuelle Order bsp
		// Order order1 = new Order();
		// order1.addItem(new Item(0, 0, 1, 1), 3);
		// order1.addItem(new Item(0, 1, 1, 6), 3);
		// order1.addItem(new Item(0, 2, 1, 11), 3);
		// wh.takeOrder(order1.getMap());
        
        
		//gui init		
		//---------------------------------------------------
        
		simulation_run(); //beginn des simulationsabschnittes
	}
	
	void readCSV(String pfad){
		ReadCSV rcsv;
		try {
			rcsv = new ReadCSV(pfad);		
			rcsv.readConfig();			
			//rcsv.writeItems();
			
			// CSV datei mit den Items wird hier eingelesen 
            List<Item> items = rcsv.readItems();

			whouse = new WarehouseImpl(items); // Warehouse mit Item Liste
													// befuellen

			//Order holen , tmp beinhaltet die Aufträge
			List<Order> tmp = new ArrayList<>();
			tmp = rcsv.readOrder(items);
			System.out.println("Gesamte Anzahl an gueltigen Auftraegen: " + tmp.size());
			
			// i Order mit Item Liste generieren lassen und an das Warehouse uebergeben
			//Schleife geht solange i kleiner Auftragszahl plus 1
			for (int i = 0; i < 1 ; i++) {	//pauschale erz�ugung ungenau			
				// Liste mit den Items wird �bergeben...
			//do{//while(!rcsv.readOrder(item).isEmpty()){
                for (Order aTmp : tmp) {
                    whouse.takeOrder(aTmp);
                }
				
				
				
			}//while(!rcsv.readOrder(item).isEmpty());
			
			
			
			itemSet = rcsv.getItemSet();
		} catch (IOException e) {
			System.err.println("Fehler bei Initialisierung");
			//e.printStackTrace();
			gui.abbruch("Fehler bei Initialisierung");
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			System.exit(-1); //ret -1 mit fehler aus
		}	
		
		//end_______________csv
	}
	
	/**
	 *  Diese Methode �bernimmt die Taktsteuerung des Systems - Seiteneffekte kommen von 
	 *  next_simuation() ; void pause_simuation(); void run_simuation();
	 *  
	 *  Bei jedem Takt werden duie neuen Daten an die Gui gemeldet  bzw. �bertragen
	 *  Im nicht run modus also takte und step schl�ft der aktualisierungs Funktion (�ber monitor (this))
	 *  
	 */
	private void simulation_run(){
		int takt = 1; //taktz�hler gegen endlosschleife �bernommen
		while(whouse.notDone()&& takt < 30000 /*exit option optional*/){
			if(this.simstatus){//wenn sim true , schlafe
				try {
					Thread.sleep(Simulation.refreshtime); //verz�gerung anhand der refreshtime
				} catch (InterruptedException e) {
					//e.printStackTrace();
				}
			}
			else{//wenn sim false , stoppe bis aufgeweckt - wecken durch gui
				try {
					synchronized(this){
						this.wait(); // l�sst warten bis weiter
					}
					
				} catch (InterruptedException e) {
					//e.printStackTrace();
				}
			}
			
			whouse.action();//f�hre aktion (stepp / schritt) aus
			//System.out.println(takt);
			
			//Informationen f�r das extra GUI Fenster holen
			//erstelleTabelle(whouse.getBplants());
			
			//System.out.println(whouse.toString());
			//sende neue Daten an die Gui etwa so...
			//Field[][] temp = whouse.getWarehouseArr();
			//updateGUI(temp);
			updateDisplay(whouse.getBplants());			
			
			takt++;
		}
		gui.beendet(takt, whouse.getOrderQueue().size());
	}
	/**
	 * Aufruf an die GUI zur Bildaktualisierung mit allen momentanen Robotdaten
	 * (ID, akt.Position, Zielposition, Status)
	 * HINWEIS! : Ladung des Robots kann nicht übergeben werden, da diese nicht implementiert wurde!
	 * @param Array der Boxingplants
	 */
	 private void updateDisplay(BoxingPlant[] temp){
         for (BoxingPlant aTemp : temp) {
             Robot rob = aTemp.getRobot();
             if (rob != null) {
                 int loadTime = aTemp.getLoadTime();
                 int packingTime = aTemp.getPackingTime();
                 int dx = 0;
                 int dy = 0;
//	        	 int id = rob.id();
//	        	 int curentx = rob.getCurrentPosX();
//	        	 int curenty = rob.getCurrentPosY();
                 int[] dest = rob.getTarget();
                 if (rob.getOrder() != null) if (!rob.getOrder().getMap().isEmpty()) {
                     dx = dest[1];
                     dy = dest[0];
                 } else {
                     dx = rob.getStartPosX();
                     dy = rob.getStartPosY();
                 }
                 gui.showRobotState(whouse, rob, itemSet, loadTime, dx, dy, packingTime);

                 //gui.showRobotState(id, curentx, curenty, null, dx, dy, rob.getStatus(), itemSet, loadTime);
             }
         }
	 }
	
	public void starteSimulation(){
		simulation_run(); //beginn des simulationsabschnittes		
	}
	
	/** Kein Funktionaler inhalt nur Startpunkt unseres Programms
	 * @param args
	 */
	public static void main(String[] args) {
		new Simulation();
	}

	
	@Override //siehe Interface
	public void next_simuation() { 
		simstatus=false; //l�uft nicht nur einzelbilder bzw. pause
		synchronized (this) {
			this.notify(); //weckt l�sst schritt machen
		}		
	}

	@Override //siehe Interface
	public void pause_simuation() {
		simstatus=false; //h�llt sim an wird gefangen im wait
		
	}

	@Override //siehe Interface
	public void run_simuation() {
		simstatus=true; //sim auf run daueraktualisierung nach refreshrate
		synchronized (this) {
			this.notify(); //weckt hierzu den aktualisierer
		}		
	}
}


