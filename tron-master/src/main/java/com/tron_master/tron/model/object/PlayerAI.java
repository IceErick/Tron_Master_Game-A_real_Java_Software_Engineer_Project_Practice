package com.tron_master.tron.model.object;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import com.tron_master.tron.model.data.ColorValue;
import com.tron_master.tron.model.data.LineSegment;
import com.tron_master.tron.view.utils.Line;

/**
 * AI-controlled player implementation.
 */
public class PlayerAI extends Player {
	
	// the number of steps before a random turn
	private int time = 40;
	
	// the list of other players on the court
	private Player[] players = new Player[1];
	
	// the list of walls in the game area
	private Wall[] walls = new Wall[0];
	
	private Random rand = new Random();
	
	/**
	 * PlayerAI constructor with start position and velocity.
	 * @param randX x coordinate
	 * @param randY y coordinate
	 * @param velx starting velocity x
	 * @param vely starting velocity y
	 * @param color player color
	 * @param mode game mode ("story", "survival")
	 */
	public PlayerAI(int randX, int randY, int velx, int vely, ColorValue color, String mode) {
		super(randX, randY, velx, vely, color, mode);
		players[0] = this;
	}

	@Override
	public Boolean isHuman() {
		return false;
	}
	
	// must be called so that the AI knows where trails are
	public void addPlayers(Player[] players) {
		this.players = players;
	}
	
	/**
	 * Sets the walls for the AI to detect and avoid.
	 * @param walls Array of walls in the game area
	 */
	public void setWalls(Wall[] walls) {
		this.walls = walls != null ? walls : new Wall[0];
	}
	
	/**
	 * Converts walls to LineSegments so AI can treat them like player trails.
	 * Each wall is converted to 4 edge lines.
	 * @return List of LineSegments representing wall edges
	 */
	private ArrayList<LineSegment> getWallLines() {
		ArrayList<LineSegment> wallLines = new ArrayList<>();
		if (walls == null) {
			return wallLines;
		}
		
		for (Wall wall : walls) {
			if (wall == null) continue;
			
			int left = wall.getX();
			int right = wall.getX() + wall.getWidth();
			int top = wall.getY();
			int bottom = wall.getY() + wall.getHeight();
			
			// Add 4 edges of the wall as line segments
			// Top edge (horizontal)
			wallLines.add(new LineSegment(left, top, right, top));
			// Bottom edge (horizontal)
			wallLines.add(new LineSegment(left, bottom, right, bottom));
			// Left edge (vertical)
			wallLines.add(new LineSegment(left, top, left, bottom));
			// Right edge (vertical)
			wallLines.add(new LineSegment(right, top, right, bottom));
		}
		return wallLines;
	}
	
	// gets the AI's move depending on its surroundings
	private void reactProximity() {
        int velocity = Math.max(Math.abs(velocityX), Math.abs(velocityY));
        
        // Dynamic detection distance based on current speed
        int urgentDistance = Math.max(6, velocity * 3);

        // boosts randomly
        int r = rand.nextInt(100);
        if (r == 1) {
            startBoost();
        }

        // adds all lines of all players to one list (LineSegment is parent of Line)
        ArrayList<LineSegment> lines = new ArrayList<>();
        lines.addAll(this.getPath());
        Collections.reverse(lines);
        for (Player p: players) {
            if (p != this) {
                lines.addAll(p.getPath());
            }
        }
        
        // Add wall edges as LineSegments - AI will treat them like player trails
        lines.addAll(getWallLines());

        for (int i = lines.size() - 1; i > 0; i--) {
            LineSegment l = lines.get(i);
            int maxX = l.getMaxX();
            int minX = l.getMinX();
            int maxY = l.getMaxY();
            int minY = l.getMinY();

            // if there is a line in the path, checks if there is one adjacent
            if (velocityX > 0 && l.isVertical() && y >= minY && y <= maxY) {
                if (l.getStartX() - x < urgentDistance && l.getStartX() - x > 0) {
                    boolean b = false;
                    for (int j = lines.size() - 1; j > 0; j--) {
                        LineSegment k = lines.get(j);
                        if (!k.isVertical() && y - k.getEndY() < urgentDistance &&
                                y - k.getEndY() > 0) {
                            b = true;
                        }
                    }

                    // reacts appropriately
                    if (b) {
                        velocityY = velocity;
                    } else {
                        velocityY = -velocity;
                    }
                    velocityX = 0;
                    time = 40;
                    return;
                }
            }

            // if there is a line in the path, checks if there is one adjacent
            if (velocityX < 0 && l.isVertical() && y >= minY && y <= maxY) {
                if (x - l.getStartX() < urgentDistance && x - l.getStartX() > 0) {
                    boolean b = false;
                    for (int j = lines.size() - 1; j > 0; j--) {
                        LineSegment k = lines.get(j);
                        if (!k.isVertical() && y - k.getEndY() < urgentDistance &&
                                y - k.getEndY() > 0) {
                            b = true;
                        }
                    }

                    // reacts appropriately
                    if (b) {
                        velocityY = velocity;
                    } else {
                        velocityY = -velocity;
                    }
                    velocityX = 0;
                    time = 40;
                    return;
                }
            }

            // if there is a line in the path, checks if there is one adjacent
            if (velocityY > 0 && !l.isVertical() && x >= minX && x <= maxX) {
                if (l.getStartY() - y < urgentDistance && l.getStartY() - y > 0) {
                    boolean b = false;
                    for (int j = lines.size() - 1; j > 0; j--) {
                        LineSegment k = lines.get(j);
                        if (k.isVertical() && x - k.getEndX() < urgentDistance &&
                                x - k.getEndX() > 0) {
                            b = true;
                        }
                    }

                    // reacts appropriately
                    if (b) {
                        velocityX = velocity;
                    } else {
                        velocityX = -velocity;
                    }
                    velocityY = 0;
                    time = 40;
                    return;
                }
            }

            // if there is a line in the path, checks if there is one adjacent
            if (velocityY < 0 && !l.isVertical() && x >= minX && x <= maxX) {
                if (y - l.getStartY() < urgentDistance && y - l.getStartY() > 0) {
                    boolean b = false;
                    for (int j = lines.size() - 1; j > 0; j--) {
                        LineSegment k = lines.get(j);
                        if (k.isVertical() && x - k.getEndX() < urgentDistance &&
                                x - k.getEndX() > 0) {
                            b = true;
                        }
                    }

                    // reacts appropriately
                    if (b) {
                        velocityX = velocity;
                    } else {
                        velocityX = -velocity;
                    }
                    velocityY = 0;
                    time = 40;
                    return;
                }
            }
        }

        // checks if the Player is too close to the edge
        if (x < urgentDistance && velocityX != 0) {
            if (y < 250) {
                velocityY = velocity;
            } else {
                velocityY = -velocity;
            }
            velocityX = 0;
            time = 40;
            return;
        }
        if (rightBound - x < urgentDistance && velocityX != 0) {
            if (y < 250) {
                velocityY = velocity;
            } else {
                velocityY = -velocity;
            }
            velocityX = 0;
            time = 40;
            return;
        }
        if (y < urgentDistance && velocityY != 0) {
            if (x < 250) {
                velocityX = velocity;
            } else {
                velocityX = -velocity;
            }
            velocityY = 0;
            time = 40;
            return;
        }
        if (bottomBound - y < urgentDistance && velocityY != 0) {
            if (x < 250) {
                velocityX = velocity;
            } else {
                velocityX = -velocity;
            }
            velocityY = 0;
            time = 40;
            return;
        }

		// moves randomly if all others do not
		// cause the Player to change direction
		if (time == 0) {
			int rando = rand.nextInt(4);
			if (rando == 0 && velocityX != velocity) {
				if (x > 6) {
					velocityX = -velocity;
					velocityY = 0;
				}
			} else if (rando == 1 && velocityX != -velocity) {
				if (rightBound - x > 6) {
					velocityX = velocity;
					velocityY = 0;
				}
			} else if (rando == 2 && velocityY != velocity) {
				if (y > 6) {
					velocityX = 0;
					velocityY = -velocity;
				}
			} else if (rando == 3 && velocityY != -velocity) {
				if (bottomBound - y > 6) {
					velocityX = 0;
					velocityY = velocity;
				}
			}
			time = 40;
		}
		time--;
	}

	// moves the Player based on its conditions
	public void move() {
		int a = x;
		int b = y;
		boost();
		reactProximity();

		if (!jumping) {
			x += velocityX;
			y += velocityY;
			if (getPath().size() > 1) {
				Line l1 = getPath().get(getPath().size() - 2);
				Line l2 = getPath().get(getPath().size() - 1);
				if (a == l1.getStartX() &&
						l1.getEndY() == l2.getStartY()) {
					getPath().add(new Line(l1.getStartX(), l1.getStartY(),
							l2.getEndX(), l2.getEndY()));
					getPath().remove(getPath().size() - 2);
					getPath().remove(getPath().size() - 2);
				} else if (b == l1.getStartY() &&
						l1.getEndX() == l2.getStartX()) {
					getPath().add(new Line(l1.getStartX(), l1.getStartY(),
							l2.getEndX(), l2.getEndY()));
					getPath().remove(getPath().size() - 2);
					getPath().remove(getPath().size() - 2);
				}
			}
			getPath().add(new Line(a, b, x, y));
		} else {
			if (velocityX > 0) {
				x += JUMPHEIGHT;
			} else if (velocityX < 0) {
				x -= JUMPHEIGHT;
			} else if (velocityY > 0) {
				y += JUMPHEIGHT;
			} else if (velocityY < 0) {
				y -= JUMPHEIGHT;
			}
			jumping = false;
		}
		accelerate();
		clip();
	}
}
