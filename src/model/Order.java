package model;

import java.util.*;
import java.util.Map.Entry;

public class Order {

    private final Map<Item, Integer> order;
    private final String orderId;
    private static int ordercount = 1;

    // Public Konstruktor zum Anlegen von Testfaellen
    public Order(Map<Item, Integer> order) {
    	orderId = "A" + ordercount;
    	ordercount++;
        this.order = order;
    }   
    
    public boolean isEmpty(){
        return order.isEmpty();
    }

    // Zum Testen
    public Map<Item, Integer> getMap() {
        return order;
    }
 
    public String getOrderId(){
    	return this.orderId;
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
