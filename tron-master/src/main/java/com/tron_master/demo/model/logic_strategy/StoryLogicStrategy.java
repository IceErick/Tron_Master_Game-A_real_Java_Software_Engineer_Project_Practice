package com.tron_master.demo.model.logic_strategy;

import com.tron_master.demo.model.GameData;
import com.tron_master.demo.model.GameState;
import com.tron_master.demo.model.player.*;

import javafx.animation.AnimationTimer;

public class StoryLogicStrategy extends GameLogic {
    private final GameData gameData;
    private final int currentLevel;

    public StoryLogicStrategy(int p) {
        super(p);
        // Initialize players
        initializePlayers();
        // Initialize game data
        gameData = GameData.getInstance();
        currentLevel = gameData.getStoryLevel();
    }

    /**
     * Initialize players
     */
    private void initializePlayers() {
        // create player human
        int[] start = getRandomStart();
        player = new PlayerHuman(start[0], start[1], start[2], start[3], colors[0]);
        players[0] = player;

        // create player ai
        for (int i = 1; i < players.length; i++) {
            start = getRandomStart();
            players[i] = new PlayerAI(start[0], start[1], start[2], start[3], colors[i % colors.length]);
        }
        for (Player p: players) {
            p.addPlayers(players);
        }
    }

    @Override
    public void tick(AnimationTimer timer) {
        updateGame(players);
        if (!player.getAlive()) {
            timer.stop();
            isGameRunning = false;
            gameData.setStoryState(addScore()); // lose
        } else {
            int check = 0;
            for (Player k: players) {
                if (!k.getAlive()) {
                    check++;
                }
            }
            if (check == players.length - 1) {
                isGameRunning = false;
                timer.stop();
                gameData.setStoryState(addScore()); // win
            } else {
                isGameRunning = true;
                gameData.setStoryState(GameState.PLAYING);
            }
        }
    }

    @Override
    public void reset() {
        // reset story mode data
        gameData.setStoryLevel(1);
        scores = 0;
        gameData.setStoryState(GameState.PLAYING);
        isGameRunning = true;
        gameData.resetStoryData();
        // reset players
        initializePlayers();
    }

    @Override
    public GameState addScore() {
        if (player.getAlive()) {
            scores += 50 * (players.length - 1);
            gameData.setStoryScore(scores);
            if (currentLevel == 9) {
                return GameState.VICTORY;
            }
            else {
                return GameState.LEVEL_COMPLETE;
            }
        } else {
            return GameState.GAME_OVER;
        }
    }


    public Player[] getPlayers() {
        return players;
    }

    public PlayerHuman getPlayer() {
        return player;
    }

    public boolean isRunning() {
        return isGameRunning;
    }

    public int getStoryScore() {return gameData.getStoryScore();}

    public int getStoryLevel() {return gameData.getStoryLevel();}

    public int getBoostCount() {return gameData.getBoostCount();}

    public GameState getStoryState() {return gameData.getStoryState();}
}