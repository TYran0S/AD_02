package model;

public class FieldImpl implements Field {
    private int amountOfRobots;
    private Robot robot;
    private final int coordinateX;
    private final int coordinateY;
    private int id;

    /**
     * 
     * @param amountOfRobots
     * @param robotId
     * @param coordinateX
     * @param coordinateY
     */
    public FieldImpl(int amountOfRobots, int robotId, int coordinateX, int coordinateY) {
        this.amountOfRobots = amountOfRobots;
        // this.robotId = robotId;
        this.coordinateX = coordinateX;
        this.coordinateY = coordinateY;
    }

    public FieldImpl(int id, int coordinateX, int coordinateY) {
        amountOfRobots = 0;
        this.id = id;
        this.coordinateX = coordinateX;
        this.coordinateY = coordinateY;
    }
    
    public FieldImpl(Item item) {
      amountOfRobots = 0;

      coordinateX = item.productPosX();
      coordinateY = item.productPosY();
  }

    public int hasRobots() {
        return amountOfRobots;
    }

    public boolean isBoxingPlant() {
        return this.id != 0;
    }

    public void reg(Robot bot) {
        amountOfRobots++;
        robot = bot;
    }

    public void unReg() {
        amountOfRobots--;
        robot = null;
    }

    public int robotID() {
        if (robot == null) {
            return 0;
        }
        return robot.id();
    }

    public int[] getTarget() {
        if (robot != null) {
            return robot.getTarget();
        } else {
            int i[] = { -1, -1 };
            return i;
        }
    }

    @Override
    public int coordinateX() {
        return this.coordinateX;
    }

    @Override
    public int coordinateY() {
        return this.coordinateY;
    }

    public int getId() {
        return this.id;
    }

}
