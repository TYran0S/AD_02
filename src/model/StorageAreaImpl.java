package model;

public class StorageAreaImpl implements StorageArea {
    private int amountOfRobots;
    private final int coordinateX;
    private final int coordinateY;
    private final Item item;
    private int robotId;

    public StorageAreaImpl(Item item) {
        amountOfRobots = 0;

        coordinateX = item.productPosX();
        coordinateY = item.productPosY();

        this.item = item;
    }

    public int hasRobots() {
        return amountOfRobots;
    }

    public int coordinateX() {
        return coordinateX;
    }

    public int coordinateY() {
        return coordinateY;
    }

    public boolean isBoxingPlant() {
        return false;
    }

    public Item item() {
        return item;
    }

    @Override
    public String toString() {
    	System.out.println("toString StorageImpl");
        return "StorageAreaImpl: XY-Koordinaten: " + coordinateX + "/" + coordinateY + " Item ID: " + item;
    }

    public void reg(Robot bot) {
        amountOfRobots++;
        this.robotId = bot.id();
    }

    public void unReg() {
        amountOfRobots--;
        this.robotId = 0;
    }

    public int robotID() {
        return robotId;
    }

}
