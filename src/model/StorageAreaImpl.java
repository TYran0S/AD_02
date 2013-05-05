//package model;
//
//public class StorageAreaImpl implements StorageArea {
//    private int amountOfRobots;
//    private final int coordinateX;
//    private final int coordinateY;
//    private final Item item;
//    private Robot robot;
//
//    public StorageAreaImpl(Item item) {
//        amountOfRobots = 0;
//
//        coordinateX = item.productPosX();
//        coordinateY = item.productPosY();
//
//        this.item = item;
//    }
//
//    public int hasRobots() {
//        return amountOfRobots;
//    }
//
//    public int coordinateX() {
//        return coordinateX;
//    }
//
//    public int coordinateY() {
//        return coordinateY;
//    }
//
//    public boolean isBoxingPlant() {
//        return false;
//    }
//
//    public Item item() {
//        return item;
//    }
//
//    @Override
//    public String toString() {
//        System.out.println("toString StorageImpl");
//        return "StorageAreaImpl: XY-Koordinaten: " + coordinateX + "/" + coordinateY + " Item ID: " + item;
//    }
//
//    public void reg(Robot bot) {
//        amountOfRobots++;
//        robot = bot;
//    }
//
//    public void unReg() {
//        amountOfRobots--;
//        robot = null;
//    }
//
//    public int robotID() {
//        if (robot == null) {
//            return 0;
//        }
//        return robot.id();
//    }
//
//    public int[] getTarget() {
//        if (robot != null) {
//            return robot.getTarget();
//        } else {
//            int i[] = { -1, -1 };
//            return i;
//        }
//    }
//
//}
