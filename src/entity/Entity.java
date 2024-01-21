package entity;

import java.awt.Color;

import main.GameFrame;
import templates.GameObject;

public class Entity extends GameObject{

	public boolean onPath = false;
    public int speed = 4;
	private int movingAngle = -1;
    private double speedX = 0;
    private double speedY = 0;
    private int damage = 15;
    private int health = 100; 
    private int centerX; 
	private int centerY; 
	private int width;
	private int height;
	private boolean collidingWall = false;
    private int shotDelay = 10;
    private int projectileSpeed = 7;
    private int shotTimer = 0;
    private double turretAngle = 90;
	private double drawInterval = 1000000000/12; // for tank hit animation
	private double delta = 0;
	private long lastTime = System.nanoTime();
	private long currentTime;
	private int timer = 0;
	private boolean inSight = false;
	public int closestAngleToPlayer = 0;
	public int closestAngleToPlayerValue = 0;
	private int turretLength = 20;
	private int turretMargin = 10; // shrinks turret radius
	private GameFrame game;	

	public  Entity(int x, int y, int width, int height, GameFrame g) {
		this.setSize((int)(width*0.9), (int)(height*0.9));
		this.setColor(Color.BLUE);  
		this.setX(x - width/2);
		this.setY(y - height/2);
		this.width = width;
		this.height = height;
		game = g;
	}
	public void setAction() {
		
	}
	public void searchPath(int goalCol, int goalRow) {
		int startCol = (this.getX() + width)/game.TILE_SIZE;
		int startRow = (this.getY()+ height)/game.TILE_SIZE;
		
		game.pFinder.setNodes(startCol, startRow, goalCol, goalRow);
		
		// COPIED CODE FROM THE TUTORIAL
		 if(game.pFinder.search() == true)
	        {
	            //Next WorldX and WorldY
	            int nextX = game.pFinder.pathList.get(0).col * game.tileSize;
	            int nextY = game.pFinder.pathList.get(0).row * game.tileSize;

	            //Entity's solidArea position
	            int enLeftX = worldX + solidArea.x;
	            int enRightX = worldX + solidArea.x + solidArea.width;
	            int enTopY = worldY + solidArea.y;
	            int enBottomY = worldY + solidArea.y + solidArea.height;

	            // TOP PATH
	            if(enTopY > nextY && enLeftX >= nextX && enRightX < nextX + game.tileSize)
	            {
	                direction = "up";
	            }
	            // BOTTOM PATH
	            else if(enTopY < nextY && enLeftX >= nextX && enRightX < nextX + game.tileSize)
	            {
	                direction = "down";
	            }
	            // RIGHT - LEFT PATH
	            else if(enTopY >= nextY && enBottomY < nextY + game.tileSize)
	            {
	                //either left or right
	                // LEFT PATH
	                if(enLeftX > nextX)
	                {
	                    direction = "left";
	                }
	                // RIGHT PATH
	                if(enLeftX < nextX)
	                {
	                    direction = "right";
	                }
	            }
	            //OTHER EXCEPTIONS
	            else if(enTopY > nextY && enLeftX > nextX)
	            {
	                // up or left
	                direction = "up";
	                checkCollision();
	                if(collisionOn == true)
	                {
	                    direction = "left";
	                }
	            }
	            else if(enTopY > nextY && enLeftX < nextX)
	            {
	                // up or right
	                direction = "up";
	                checkCollision();
	                if(collisionOn == true)
	                {
	                    direction = "right";
	                }
	            }
	            else if(enTopY < nextY && enLeftX > nextX)
	            {
	                // down or left
	                direction = "down";
	                checkCollision();
	                if(collisionOn == true)
	                {
	                    direction = "left";
	                }
	            }
	            else if(enTopY < nextY && enLeftX < nextX)
	            {
	                // down or right
	                direction = "down";
	                checkCollision();
	                if(collisionOn == true)
	                {
	                    direction = "right";
	                }
	            }
	            // for following player, disable this. It should be enabled when npc walking to specified location
//	            int nextCol = gp.pFinder.pathList.get(0).col;
//	            int nextRow = gp.pFinder.pathList.get(0).row;
//	            if(nextCol == goalCol && nextRow == goalRow)
//	            {
//	                onPath = false;
//	            }
	        }
	}

	public void getCoordinates() {
		
	}
	
	@Override
	public void act() {
		// TODO Auto-generated method stub
		
	}
	
}
