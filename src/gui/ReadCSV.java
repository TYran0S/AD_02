package gui;

import java.io.*;
import java.util.*;

import model.*;

public class ReadCSV {
	private File configFile;
	private File itemsFile;
	private String delimiter;
	private List<Item> itemList;
	private ArrayList<String> liste;
	private File orderFile;
	private ArrayList orderArr;
	private Set<Item> itemSet = new TreeSet<Item>();
	private String zeile = null;
	private int counter = 0;

	// Konstruktor
	// bekommt als parameter den pfad der ini datei als string �bergeben.
	public ReadCSV(String inipfad) throws FileNotFoundException, IOException {
		// liste initialisieren
		liste = new ArrayList<String>();
		// ini datei einlesen und die einzelnen pfade speichern,.
		try (BufferedReader bReader = new BufferedReader(
				new FileReader(inipfad))) {
			String line = null;
			while ((line = bReader.readLine()) != null) {
				liste.add(line);
			}

		}

		// orderpfad hollen und in einem bufferedReader speichern.
		orderFile = new File(liste.get(2).split("=")[1].trim());
		BufferedReader tmpFile = new BufferedReader(new FileReader(orderFile));

		orderArr = new ArrayList<String>();

		// Header �berspringen
		tmpFile.readLine();

		// Zeilen einlesen und in einem liste speichern.
		while ((zeile = tmpFile.readLine()) != null) {
			// if(zeile.length()!=1)
			orderArr.add(zeile);
		}
	}

	// config.csv laden und die Variablen initialisieren
	public void readConfig() throws FileNotFoundException, IOException {
		/* liste inhalt zugeh�rigen variable zuweisen */
		// pfade von config und Delimeter wird gespeichert
		this.configFile = new File(liste.get(0).split("=")[1].trim());
		this.delimiter = liste.get(3).split(" ")[2].trim();

		BufferedReader tmpFile = new BufferedReader(new FileReader(configFile));

		// header �berspringen
		tmpFile.readLine();

		// die Zeile mit den einstellungen von der config einlesen und in zeile
		// speichern
		String zeile = tmpFile.readLine();

		// Variable zeile splitten und die statics initialisieren
		Simulation.N = Integer.parseInt(zeile.split(delimiter)[0]);
		Simulation.NUMBOXINGPLANTS = Simulation.N;
		Simulation.ORDERMAXSIZE = Integer.parseInt(zeile.split(delimiter)[1]);
		Simulation.MAXCAPACITY = Simulation.ORDERMAXSIZE;
		Simulation.NUMROBOTS = Simulation.N;
		Simulation.CLTIME = Integer.parseInt(zeile.split(delimiter)[2]);
		Simulation.PPTIME = Integer.parseInt(zeile.split(delimiter)[3]);
	}

	// generiert eine Liste mit der vorgegeben lagergr��e
	public void writeItems() throws IOException {

		File outfile = new File(liste.get(4).split("=")[1].trim());

		int maxSize;

		int id = 1;

		// Schreibt die items in die csv datei
		try (PrintWriter erzeugteDatei = new PrintWriter(new BufferedWriter(
				new FileWriter(outfile)));) {
			erzeugteDatei.println("item_id" + ";" + "productPosX" + ";"
					+ "productPosY" + ";" + "productSize");

			for (int y = 0; y < Simulation.N - 1; y++) {
				for (int x = 0; x < Simulation.N; x++) {

					if ((x >= Simulation.N - Simulation.NUMBOXINGPLANTS)
							&& y == Simulation.N - 1) {
						break;
					}

					maxSize = (int) ((Math.random()) * Simulation.ORDERMAXSIZE + 1);

					erzeugteDatei.println(id + ";" + x + ";" + y + ";"
							+ maxSize);
					id++;

				}
			}
		}
	}

	// Item liste einlesen
	public List<Item> readItems() throws IOException {

		itemList = new ArrayList<Item>();
		// items.csv pfad speichern
		itemsFile = new File(liste.get(1).split("=")[1].trim());

		BufferedReader tmpFile = new BufferedReader(new FileReader(itemsFile));

		String zeile = null;

		// header �berspringen
		tmpFile.readLine();

		// solange zeile vorhanden...
		while ((zeile = tmpFile.readLine()) != null) {

			// wenn zeilenl�nge gr��e als 0
			if (zeile.length() > 0) {

				String[] arr = zeile.split(delimiter);

				int id = Integer.parseInt(arr[0]);
				int x = Integer.parseInt(arr[1]);
				int y = Integer.parseInt(arr[2]);
				int size = Integer.parseInt(arr[3]);

				// speicher items von der CSV datein in liste
				itemList.add(new Item(x, y, size, id));

			}

		}

		return itemList;

	}

	// Order einlesen
	public List<Order> readOrder(List<Item> item) {

		int tmpId = 0;
		int menge = 0;

		Map<Item, Integer> retMap = new TreeMap<Item, Integer>();

		// flag zum setzten ob order fertig oder nicht
		boolean orderComplete = false;

		Item tempItem = null;
		// den maximal gewicht in einer tmp variable speichern (kapazit�t)
		int currentMaxSize = Simulation.ORDERMAXSIZE;
		int countSameItem; // z�hlt wie oft das selbe item beladen werden muss.
		int gw = 0; // gewicht wird hier addiert und darf nicht �ber kapazit�t
					// sein
//		String tmp1;
//		String tmp2;
		
		//Diese Liste beinhaltet Listenpaare von den ids und Mengen der items
		//index 0 und 1 zusammen,Index 2 und 3 zusammen ... etc. pp.
		List<List<Integer>> listpairs = new ArrayList<List<Integer>>();
		//Jonas und philipp reworked start
		for(int i = 0;i<orderArr.size();i++){
			List<Integer> tmpids = new ArrayList<Integer>();
			List<Integer> mengen = new ArrayList<Integer>();
			zeile = (String) orderArr.get(i);
			
			String ids = (zeile.split(";")[0]);
			String mengenstrings = (zeile.split(";")[1]);
			
			for(int j = 0;j<ids.split(",").length;j++){
				tmpids.add(Integer.parseInt(ids.split(",")[j]));
			}
			
			for(int k = 0;k<mengenstrings.split(",").length;k++){
				mengen.add(Integer.parseInt(mengenstrings.split(",")[k]));
			}
			listpairs.add(tmpids);
			listpairs.add(mengen);
			
			
		}
		
		for(int i = 0;i<listpairs.size();i++){
			int gewichtgesamt = 0;
			for(int j = 0;j<listpairs.get(i).size();j++){
				for(int k = 0;k<item.size();k++){
					if(item.get(k).id() == listpairs.get(i).get(j)){
						gewichtgesamt += item.get(k).size() * listpairs.get(i+1).get(j);
					}
				}
		
			
		}
			if(gewichtgesamt > Simulation.ORDERMAXSIZE){
				//Hier wird "ein Auftrag entfernt" wenn das Gewicht zu hoch ist
				String idlisten = "";
				String mengenausgabe = "";
				for(int y = 0;y<listpairs.get(i).size();y++){
					idlisten = idlisten + listpairs.get(i).get(y) + ",";
				}
				for(int y = 0;y<listpairs.get(i+1).size();y++){
					mengenausgabe = mengenausgabe + listpairs.get(i+1).get(y) + ",";
				}
				System.out.println("Auftrag mit den Items: " + idlisten + "und den Mengen: " + mengenausgabe + "ist zu schwer!   Gewicht" + gewichtgesamt + "/" + Simulation.ORDERMAXSIZE);
				listpairs.remove(i);
				listpairs.remove(i);
				i--;
			} else {
				i++;
			}
		}
		
		//Alle relevanten Items dem ItemSet hinzuf�gen,diese sind sp�ter auf dem Field sichtbar
		for(int i = 0;i<listpairs.size();i=i+2){
			for(int j = 0;j<listpairs.get(i).size();j++){
				for(int k = 0;k<item.size();k++){
				if(listpairs.get(i).get(j) == item.get(k).id()) {
					itemSet.add(item.get(k));
				}
				}
			}
		}
		
		
		//Jonas und Philipp reworked ende

		// solange order "pro" robot noch nicht abgearbeitet wurde
//		while (!orderComplete) {
//
//			countSameItem = 0;
//
//			// counter = anzahl an items die aus der orderliste abgearbeitet
//			// wurden sind.
//			// wenn ich nicht beim item letzten item bin....
//			if (counter < (orderArr.size())) {
//
//				// holt die zeile der orderliste (n�chstes item)
//				zeile = (String) orderArr.get(counter);
//				// hollt die Item id und die menge in tmp variable
//				tmpId = Integer.parseInt(zeile.split(";")[0]);
//				menge = Integer.parseInt(zeile.split(";")[1]);
//				int gesamtgewicht = 0;
//				
//				for(int i = 0;i<item.size();i++){
//					if(item.get(i).id() == tmpId){
//						gesamtgewicht += item.get(i).size()*menge;
//					}
//				}
//				
//				
//				
//				
//
//				// Solange das selbe item noch eine menge hat und in kapazit�t
//				// noch gewicht passt...
//				for (int i = 0; i < menge; i++) {
//
//					// items durch laufen
//					for (Item element : item) {
//
//						// �berpr�ft item id vom jetzigen mit der von der Item
//						// liste
//						if (element.id() == tmpId) {
//							// speicher elemet (das item ) in die tmp
//							tempItem = element;
//							this.itemSet.add(element);
//							// gewicht zu addieren
//							gw = gw + tempItem.size();
//							// wie oft wurde das selbe item abgearbeitet.
//							countSameItem++;
//							// verl�sst die schleifen wenn item gefunden
//							break;
//						}
//					}
//
//					//falls das selbe item nicht mehr rein passt
//					// dann..
//					if ((gw + tempItem.size()) > currentMaxSize) {
//						// entfernt item aus mit der Alten menge und f�gt das
//						// item mit der neuen menge hinzu
//						orderArr.remove(counter);
//
//						orderArr.add(counter, tmpId + ";"
//								+ (menge - countSameItem));
//						retMap.put(tempItem, countSameItem);
//						if ((menge - countSameItem) == 0) {
//							// counter z�hlen um n�chstes item zu hollen
//							counter++;
//						}
//						orderComplete = true;
//						break;
//					}
//
//				}
//
//			
//				if (!orderComplete) {
//					// aktuelle item hat komplett gepasst und wird in die orderMap
//					// gespeichert
//					retMap.put(tempItem, menge);
//
//					int i = counter + 1;
//					counter++;
//
//					// �berpr�fe ob das n�chste item noch vom robot zu tragen
//					// w�re, ansonsten return die orderMap
//					if (i < orderArr.size()) {
//						zeile = (String) orderArr.get(i);
//						tmpId = Integer.parseInt(zeile.split(";")[0]);
//
//						for (Item element : item) {
//
//							if (element.id() == tmpId) {
//								tempItem = element;
//								break;
//							}
//						}
//
//						if ((gw + tempItem.size()) > currentMaxSize) {
//							orderComplete = true;
//						}
//					} else {
//						orderComplete = true;
//					}
//
//				}
//
//			} else {
//				orderComplete = true;
//			}
//
//		}
		//retMap.clear();
		//Edited by j and p
		List<TreeMap<Item,Integer>> retmap = new ArrayList<TreeMap<Item,Integer>>();
		for(int i = 0;i<listpairs.size();i=i+2){
			TreeMap<Item,Integer> tmp = new TreeMap<Item,Integer>();
			for(int j = 0;j<listpairs.get(i).size();j++){
				for(int k = 0;k<item.size();k++){
					if(item.get(k).id() == listpairs.get(i).get(j)){
						tmp.put(item.get(k), listpairs.get(i+1).get(j));
					}
			}
		}
			retmap.add(tmp);
		}
	
		List<Order> orderList = new ArrayList<Order>();
		
		for(int i = 0;i<retmap.size();i++){
			orderList.add(new Order(retmap.get(i)));
		}
		
		return orderList;
	}
	
	public Set<Item> getItemSet()
	{
		return this.itemSet;
	}

}
