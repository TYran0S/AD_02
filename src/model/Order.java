package model;

import java.util.*;
import java.util.Map.Entry;

public class Order {

    private final Map<Item, Integer> order;
    private final String orderId;
    private static int ordercount = 1;
   
    public boolean isEmpty(){
        return order.isEmpty();
    }

    // Public Konstruktor zum Anlegen von Testfaellen
    public Order(Map<Item, Integer> order) {
    	orderId = "A" + ordercount;
    	ordercount++;
        this.order = order;
    }

    // Zum Testen
    public void addItem(Item item, int amount) {
        order.put(item, amount);
    }

    // Zum Testen
    public Map<Item, Integer> getMap() {
        return order;
    }
    
  
    public String getOrderId(){
    	return this.orderId;
    }

    /*
     * Eingabe:  Mit Item.factory oder per Hand erstellte Item-Liste.
     * 
     * Ausgabe:  Eine Map mit mehreren Eintraegen, die die Items als Key
     * 			 und die Anzahl eines Items als Value enthaelt.
     * 
     * Funktion: Es wird zufaellig eine Item-Id von einem in der Liste
     *           enthaltenem Item erstellt. Dieses Item wird aus der Liste geholt.
     *           Wenn das Item zu schwer ist wird es verworfen,
     *           ansonsten wird es der Map hinzugefuegt und die Anzahl des Items
     *           auf 1 gesetzt. Falls ein Item doppelt gezogen wird,
     *           wird die Anzahl in der Map erhoeht.
     */
 
    public static Map<Item, Integer> factory(List<Item> items) {
        int temp_ORDERMAXSIZE = Simulation.ORDERMAXSIZE;

        Map<Item, Integer> orderMap = new TreeMap<>();
        boolean orderComplete = false;
        int randomItem;
        int currentOrderSize = temp_ORDERMAXSIZE;
        Item tempItem = null;
        Integer tempQuantity;

        while (!orderComplete) {

            // Zufaelliges Item aus uebergebener List<Item> suchen
            randomItem = (int) (Math.random() * items.size() + 1);

            for (Item element : items) {
                if (element.id() == randomItem) {
                    tempItem = element;
                }
            }

            // Wenn das neue Random Item zu gross ist, wird es nicht mehr hinzugefuegt
            if (tempItem != null) {
                if ((currentOrderSize - tempItem.size()) >= 0) {

                    // Wenn das Item in der Map vorhanden ist, wird die Quantity vom Item erhoeht,
                    // ansonten wird es neu angelegt
                    if (orderMap.containsKey(tempItem)) {

                        tempQuantity = orderMap.get(tempItem);
                        tempQuantity++;
                    } else {
                        tempQuantity = 1;
                    }

                    orderMap.put(tempItem, tempQuantity);
                    currentOrderSize -= tempItem.size();
                } else {
                    orderComplete = true;
                }
            }
        }

        return orderMap;
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        for (Entry<Item, Integer> element : order.entrySet()) {
            output.append("Item ID: ").append(element.getKey().id());
            output.append(" Menge: ").append(element.getValue());
        }

        return output.toString();
    }

}
