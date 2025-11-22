package com.tron_master.demo.model.player;

import java.util.ArrayList;

import com.tron_master.demo.model.GameData;
import com.tron_master.demo.model.Intersection;
import com.tron_master.demo.view.Shape;

import javafx.animation.Timeline;
import javafx.scene.paint.Color;

/**
 * Player model class - responsible only for storing player state and basic properties
 */
public abstract class Player extends GameObject {

    private final GameData gameData = GameData.getInstance();
    
    // Player properties
    final Color color;
    boolean alive = true;
    boolean jumping = false;
    boolean boosting = false;
    int startVelocity = 0;
    int boostLeft = gameData.getBoostCount();
    
    // Static constants
    public static final int WIDTH = 5;
    public static final int HEIGHT = 5;
    public static final int VELBOOST = 5;
    public static final int JUMPHEIGHT = 16;
    
    // Boost timer
    private final Timeline boostTimer;
    
    // Player path
    private final ArrayList<Shape> path = new ArrayList<>();
    
    // Constructor
    public Player(int x, int y, int velocityX, int velocityY, Color color) {
        super(x, y, velocityX, velocityY, WIDTH, HEIGHT);
        this.startVelocity = Math.max(Math.abs(velocityX), Math.abs(velocityY));
        this.color = color;
        
        // Initialize boost timer
        this.boostTimer = createBoostTimer();
    }
    
    // Create boost timer
    private Timeline createBoostTimer() {
        Timeline timer = new Timeline(new javafx.animation.KeyFrame(
            javafx.util.Duration.millis(300), 
            event -> boosting = false
        ));
        timer.setCycleCount(1);
        return timer;
    }

    // changes state of Player if it exits the bounds
    public void accelerate() {
        if (x < 0 || x > rightBound) {
            velocityX = 0;
            alive = false;
        }
        if (y < 0 || y > bottomBound) {
            velocityY = 0;
            alive = false;
        }
    }

    // changes state of Player to boosting
    public void startBoost() {
        if (boostLeft > 0) {
            boosting = true;
            boostTimer.play();
            boostLeft--;
            if (isHuman()) {
                gameData.setBoostCount(boostLeft);
            }
        }
    }

    // changes velocity for boosting
    public void boost() {
        if (boosting) {
            if (velocityX > 0) {
                velocityX = VELBOOST;
            } else if (velocityX < 0) {
                velocityX = -VELBOOST;
            } else if (velocityY > 0) {
                velocityY = VELBOOST;
            } else if (velocityY < 0) {
                velocityY = -VELBOOST;
            }
        } else {
            if (velocityX > 0) {
                velocityX = startVelocity;
            } else if (velocityX < 0) {
                velocityX = -startVelocity;
            } else if (velocityY > 0) {
                velocityY = startVelocity;
            } else if (velocityY < 0) {
                velocityY = -startVelocity;
            }
        }
    }

    // checks if the Player has crashed with a path
    public void crash(Intersection i) {
        if (alive && i == Intersection.UP) {
            velocityX = 0;
            velocityY = 0;
            System.out.println("Boom");
            alive = false;
        }
    }

    public abstract Boolean isHuman();
    
    // Getters and Setters
    public Color getColor() { return color; }
    public boolean getAlive() { return alive; }
    public void setAlive(boolean alive) { this.alive = alive; }
    public boolean isJumping() { return jumping; }
    public void jump() {
        jumping = true;
    } // changes state of Player to jumping
    public boolean isBoosting() { return boosting; }
    public int getStartVelocity() { return startVelocity; }
    public ArrayList<Shape> getPath() { return path; }
	public int getX() { return x; }
    public int getY() { return y; }
    public int getVelocityX() { return velocityX; }
    public int getVelocityY() { return velocityY; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    
    // Abstract methods
    public abstract void move();
    public abstract void addPlayers(Player[] players);
}
