package rbadia.voidspace.main;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;

import javax.swing.JLabel;
import javax.swing.JPanel;

import rbadia.voidspace.graphics.GraphicsManager;
import rbadia.voidspace.model.Asteroid;
import rbadia.voidspace.model.Bullet;
import rbadia.voidspace.model.Ship;
import rbadia.voidspace.sounds.SoundManager;
import rbadia.voidspace.model.EnemyShip;//TODO

/**
 * Main game screen. Handles all game graphics updates and some of the game logic.
 */
public class GameScreen extends JPanel {
	private static final long serialVersionUID = 1L;

	private BufferedImage backBuffer;
	private Graphics2D g2d;

	private static final int NEW_SHIP_DELAY = 500;
	private static final int NEW_ASTEROID_DELAY = 500;
	private static final int NEW_ENEMYSHIP_DELAY = 500;//TODO

	private long lastShipTime;
	private long lastAsteroidTime;
	private long lastEnemyShipTime;//TODO

	private Rectangle asteroidExplosion;
	private Rectangle shipExplosion;

	private JLabel shipsValueLabel;
	private JLabel destroyedValueLabel;
	private JLabel levelValueLabel;//TODO
	
	private JLabel scorePointsValueLabel;
	private JLabel destroyedEnemyValueLabel;
	
	private long asteroidsToDestroyValue = 5;//TODO

	private Random rand;

	private Font originalFont;
	private Font bigFont;
	private Font biggestFont;

	private GameStatus status;
	private SoundManager soundMan;
	private GraphicsManager graphicsMan;
	private GameLogic gameLogic;

	/**
	 * This method initializes 
	 * 
	 */
	public GameScreen() {
		super();
		// initialize random number generator
		rand = new Random();

		initialize();

		// init graphics manager
		graphicsMan = new GraphicsManager();

		// init back buffer image
		backBuffer = new BufferedImage(500, 400, BufferedImage.TYPE_INT_RGB);
		g2d = backBuffer.createGraphics();
	}

	/**
	 * Initialization method (for VE compatibility).
	 */
	private void initialize() {
		// set panel properties
		this.setSize(new Dimension(500, 400));
		this.setPreferredSize(new Dimension(500, 400));
		this.setBackground(Color.BLACK);
	}

	/**
	 * Update the game screen.
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		// draw current backbuffer to the actual game screen
		g.drawImage(backBuffer, 0, 0, this);
	}

	/**
	 * Update the game screen's backbuffer image.
	 */
	public void updateScreen(){
		Ship ship = gameLogic.getShip();
		Asteroid asteroid = gameLogic.getAsteroid();
		EnemyShip enemyShip = gameLogic.getEnemyShip();//TODO
		List<Bullet> bullets = gameLogic.getBullets();
		List<Asteroid> asteroids = gameLogic.getAsteroids();//TODO

		// set orignal font - for later use
		if(this.originalFont == null){
			this.originalFont = g2d.getFont();
			this.bigFont = originalFont;
		}

		// erase screen
		g2d.setPaint(new Color(0x1a001a)); //DarkBlue Color by Albert
		g2d.fillRect(0, 0, getSize().width, getSize().height);

		// draw 50 random stars
		drawStars(50);

		// if the game is starting, draw "Get Ready" message
		if(status.isGameStarting()){
			drawGetReady();
			return;
		}

		// if the game is over, draw the "Game Over" message
		if(status.isGameOver()){
			// draw the message
			drawGameOver();

			long currentTime = System.currentTimeMillis();
			// draw the explosions until their time passes
			if((currentTime - lastAsteroidTime) < NEW_ASTEROID_DELAY){
				graphicsMan.drawAsteroidExplosion(asteroidExplosion, g2d, this);
			}

			if((currentTime - lastEnemyShipTime) < NEW_ENEMYSHIP_DELAY){//TODO
				graphicsMan.drawShipExplosion(shipExplosion, g2d, this);
			}

			if((currentTime - lastShipTime) < NEW_SHIP_DELAY){
				graphicsMan.drawShipExplosion(shipExplosion, g2d, this);
			}
			return;
		}

		// the game has not started yet
		if(!status.isGameStarted()){
			// draw game title screen
			initialMessage();
			return;
		}

//		// draw asteroid
//		if(!status.isNewAsteroid()){
//			// draw the asteroid until it reaches the bottom of the screen
//			if(asteroid.getY() + asteroid.getSpeed() < this.getHeight()){
//				asteroid.translate(asteroid.getDirection(), asteroid.getSpeed());//TODO
//				graphicsMan.drawAsteroid(asteroid, g2d, this);
//			}
//			else{
//				asteroid.setLocation(rand.nextInt(getWidth() - asteroid.width), 0);
//			}
//		}
//		else{
//			long currentTime = System.currentTimeMillis();
//			if((currentTime - lastAsteroidTime) > NEW_ASTEROID_DELAY){
//				// draw a new asteroid
//				lastAsteroidTime = currentTime;
//				status.setNewAsteroid(false);
//				asteroid.setLocation(rand.nextInt(getWidth() - asteroid.width), 0);
//				asteroid.generateDirection();
//			}
//			else{
//				// draw explosion
//				graphicsMan.drawAsteroidExplosion(asteroidExplosion, g2d, this);
//			}
//		}
		
		// draw asteroids
		for(Asteroid n: asteroids){//TODO
			// draw asteroid
			if(!status.isNewAsteroid()){
				// draw the asteroid until it reaches the bottom of the screen
				if(n.getY() + n.getSpeed() < this.getHeight()){
					n.translate(n.getDirection(), n.getSpeed());//TODO
					graphicsMan.drawAsteroid(n, g2d, this);
				}
				else{
					n.setLocation(rand.nextInt(getWidth() - n.width), 0);
				}
			}
			else{
				long currentTime = System.currentTimeMillis();
				if((currentTime - lastAsteroidTime) > NEW_ASTEROID_DELAY){
					// draw a new asteroid
					lastAsteroidTime = currentTime;
					status.setNewAsteroid(false);
					n.setLocation(rand.nextInt(getWidth() - n.width), 0);
					n.generateDirection();
				}
				else{
					// draw explosion
					graphicsMan.drawAsteroidExplosion(asteroidExplosion, g2d, this);
				}
			}
		}

		// draw enemy ship
		if(!status.isNewEnemyShip()){//TODO
			// draw the enemy ship until it reaches the bottom of the screen
			if(enemyShip.getY() + enemyShip.getSpeed() < this.getHeight()){
				enemyShip.translate(0, enemyShip.getSpeed());
				graphicsMan.drawEnemyShip(enemyShip, g2d, this);
			}
			else{
				enemyShip.setLocation(rand.nextInt(getWidth() - enemyShip.width), 0);
			}
		}
		else{
			long currentTime = System.currentTimeMillis();
			if((currentTime - lastEnemyShipTime) > NEW_ENEMYSHIP_DELAY){
				// draw a new enemy ship
				lastEnemyShipTime = currentTime;
				status.setNewEnemyShip(false);
				enemyShip.setLocation(rand.nextInt(getWidth() - enemyShip.width), 0);
			}
			else{
				// draw explosion
				graphicsMan.drawShipExplosion(shipExplosion , g2d, this);
			}
		}

		// draw bullets
		for(int i=0; i<bullets.size(); i++){
			Bullet bullet = bullets.get(i);
			graphicsMan.drawBullet(bullet, g2d, this);

			boolean remove = gameLogic.moveBullet(bullet);
			if(remove){
				bullets.remove(i);
				i--;
			}
		}

//		// check bullet-asteroid collisions
//		for(int i=0; i<bullets.size(); i++){
//			Bullet bullet = bullets.get(i);
//			if(asteroid.intersects(bullet)){
//				// increase asteroids destroyed count
//				status.setAsteroidsDestroyed(status.getAsteroidsDestroyed() + 1);
//				status.setScorePoints(status.getScorePoints() + 500);
//
//				// "remove" asteroid
//				asteroidExplosion = new Rectangle(
//						asteroid.x,
//						asteroid.y,
//						asteroid.width,
//						asteroid.height);
//				asteroid.setLocation(-asteroid.width, -asteroid.height);
//				status.setNewAsteroid(true);
//				lastAsteroidTime = System.currentTimeMillis();
//
//				// play asteroid explosion sound
//				soundMan.playAsteroidExplosionSound();
//
//				// remove bullet
//				bullets.remove(i);
//				break;
//			}
//		}

		// check bullet-asteroids collisions
		for(Asteroid n: asteroids){
			for(int i=0; i<bullets.size(); i++){
				Bullet bullet = bullets.get(i);
				if(n.intersects(bullet)){
					// increase asteroids destroyed count
					status.setAsteroidsDestroyed(status.getAsteroidsDestroyed() + 1);
					status.setScorePoints(status.getScorePoints() + 500);

					// "remove" asteroid
					asteroidExplosion = new Rectangle(
							n.x,
							n.y,
							n.width,
							n.height);
					n.setLocation(-n.width, -n.height);
					//status.setNewAsteroid(true);//TODO
					lastAsteroidTime = System.currentTimeMillis();

					// play asteroid explosion sound
					soundMan.playAsteroidExplosionSound();

					// remove bullet
					bullets.remove(i);
					break;
				}
			}
		}
		
		// check bullet-enemyShip collisions
		for(int i=0; i<bullets.size(); i++){//TODO
			Bullet bullet = bullets.get(i);
			if(enemyShip.intersects(bullet)){
				// increase enemyShips destroyed count
				status.setEnemyShipsDestroyed(status.getEnemyShipsDestroyed() + 1);
				status.setScorePoints(status.getScorePoints() + 1000);

				// "remove" enemyShip
				shipExplosion = new Rectangle(
						enemyShip.x,
						enemyShip.y,
						enemyShip.width,
						enemyShip.height);
				enemyShip.setLocation(-enemyShip.width, -enemyShip.height);
				status.setNewEnemyShip(true);
				lastEnemyShipTime = System.currentTimeMillis();

				// play enemyShip explosion sound
				soundMan.playShipExplosionSound();

				// remove bullet
				bullets.remove(i);
				break;
			}
		}

		// draw ship
		if(!status.isNewShip()){
			// draw it in its current location
			graphicsMan.drawShip(ship, g2d, this);
		}
		else{
			// draw a new one
			long currentTime = System.currentTimeMillis();
			if((currentTime - lastShipTime) > NEW_SHIP_DELAY){
				lastShipTime = currentTime;
				status.setNewShip(false);
				ship = gameLogic.newShip(this);
			}
			else{
				// draw explosion
				graphicsMan.drawShipExplosion(shipExplosion, g2d, this);
			}
		}

//		// check ship-asteroid collisions
//		if(asteroid.intersects(ship)){
//			// decrease number of ships left
//			status.setShipsLeft(status.getShipsLeft() - 1);
//			status.setAsteroidsDestroyed(status.getAsteroidsDestroyed() + 1);
//
//			// "remove" asteroid
//			asteroidExplosion = new Rectangle(
//					asteroid.x,
//					asteroid.y,
//					asteroid.width,
//					asteroid.height);
//			asteroid.setLocation(-asteroid.width, -asteroid.height);
//			status.setNewAsteroid(true);
//			lastAsteroidTime = System.currentTimeMillis();
//
//			// "remove" ship
//			shipExplosion = new Rectangle(
//					ship.x,
//					ship.y,
//					ship.width,
//					ship.height);
//			ship.setLocation(this.getWidth() + ship.width, -ship.height);
//			status.setNewShip(true);
//			lastShipTime = System.currentTimeMillis();
//
//			// play ship explosion sound
//			soundMan.playShipExplosionSound();
//			// play asteroid explosion sound
//			soundMan.playAsteroidExplosionSound();
//		}
		
		// check ship-asteroids collisions
		for(Asteroid n: asteroids){
			if(n.intersects(ship)){
				// decrease number of ships left
				status.setShipsLeft(status.getShipsLeft() - 1);
				status.setAsteroidsDestroyed(status.getAsteroidsDestroyed() + 1);

				// "remove" asteroid
				asteroidExplosion = new Rectangle(
						n.x,
						n.y,
						n.width,
						n.height);
				n.setLocation(-n.width, -n.height);
				status.setNewAsteroid(true);//TODO
				lastAsteroidTime = System.currentTimeMillis();

				// "remove" ship
				shipExplosion = new Rectangle(
						ship.x,
						ship.y,
						ship.width,
						ship.height);
				ship.setLocation(this.getWidth() + ship.width, -ship.height);
				status.setNewShip(true);
				lastShipTime = System.currentTimeMillis();

				// play ship explosion sound
				soundMan.playShipExplosionSound();
				// play asteroid explosion sound
				soundMan.playAsteroidExplosionSound();
			}
		}
		
		// check ship-enemyShip collisions
		if(enemyShip.intersects(ship)){//TODO
			// decrease number of ships left
			status.setShipsLeft(status.getShipsLeft() - 1);

			status.setEnemyShipsDestroyed(status.getEnemyShipsDestroyed() + 1);

			// "remove" enemyShip
			shipExplosion = new Rectangle(
					enemyShip.x,
					enemyShip.y,
					enemyShip.width,
					enemyShip.height);
			enemyShip.setLocation(-enemyShip.width, -enemyShip.height);
			status.setNewEnemyShip(true);
			lastEnemyShipTime = System.currentTimeMillis();

			// "remove" ship
			shipExplosion = new Rectangle(
					ship.x,
					ship.y,
					ship.width,
					ship.height);
			ship.setLocation(this.getWidth() + ship.width, -ship.height);
			status.setNewShip(true);
			lastShipTime = System.currentTimeMillis();

			// play ship explosion sound
			soundMan.playShipExplosionSound();
			// play enemyShip explosion sound
			soundMan.playShipExplosionSound();
		}

		// update asteroids destroyed label
		destroyedValueLabel.setText(Long.toString(status.getAsteroidsDestroyed()) + " / " + asteroidsToDestroyValue);//TODO
		
		// update ships left label
		shipsValueLabel.setText(Integer.toString(status.getShipsLeft()));
		
		//update score points label
		scorePointsValueLabel.setText(Long.toString(status.getScorePoints()));
		
		//update enemy ships destroyed label
		destroyedEnemyValueLabel.setText(Long.toString(status.getEnemyShipsDestroyed()));
		
		// update level label
		levelValueLabel.setText(Integer.toString(status.getCurrentLevel()));//TODO
	}

	/**
	 * Draws the "Game Over" message.
	 */
	private void drawGameOver() {
		String gameOverStr = "GAME OVER";
		Font currentFont = biggestFont == null? bigFont : biggestFont;
		float fontSize = currentFont.getSize2D();
		bigFont = currentFont.deriveFont(fontSize + 1).deriveFont(Font.BOLD);
		FontMetrics fm = g2d.getFontMetrics(bigFont);
		int strWidth = fm.stringWidth(gameOverStr);
		if(strWidth > this.getWidth() - 10){
			biggestFont = currentFont;
			bigFont = biggestFont;
			fm = g2d.getFontMetrics(bigFont);
			strWidth = fm.stringWidth(gameOverStr);
		}
		int ascent = fm.getAscent();
		int strX = (this.getWidth() - strWidth)/2;
		int strY = (this.getHeight() + ascent)/2;
		g2d.setFont(bigFont);
		g2d.setPaint(Color.WHITE);
		g2d.drawString(gameOverStr, strX, strY);
	}

	/**
	 * Draws the initial "Get Ready!" message.
	 */
	private void drawGetReady() {
		String readyStr = "Get Ready!";
		g2d.setFont(originalFont.deriveFont(originalFont.getSize2D() + 1));
		FontMetrics fm = g2d.getFontMetrics();
		int ascent = fm.getAscent();
		int strWidth = fm.stringWidth(readyStr);
		int strX = (this.getWidth() - strWidth)/2;
		int strY = (this.getHeight() + ascent)/2;
		g2d.setPaint(Color.WHITE);
		g2d.drawString(readyStr, strX, strY);
	}

	/**
	 * Draws the specified number of stars randomly on the game screen.
	 * @param numberOfStars the number of stars to draw
	 */
	private void drawStars(int numberOfStars) {
		g2d.setColor(Color.WHITE);
		for(int i=0; i<numberOfStars; i++){
			int x = (int)(Math.random() * this.getWidth());
			int y = (int)(Math.random() * this.getHeight());
			g2d.drawLine(x, y, x, y);
		}
	}

	/**
	 * Display initial game title screen.
	 */
	private void initialMessage() {
		String gameTitleStr = "BattleStar X";

		Font currentFont = biggestFont == null? bigFont : biggestFont;
		float fontSize = currentFont.getSize2D();
		bigFont = currentFont.deriveFont(fontSize + 1).deriveFont(Font.BOLD).deriveFont(Font.ITALIC);
		FontMetrics fm = g2d.getFontMetrics(bigFont);
		int strWidth = fm.stringWidth(gameTitleStr);
		if(strWidth > this.getWidth() - 10){
			bigFont = currentFont;
			biggestFont = currentFont;
			fm = g2d.getFontMetrics(currentFont);
			strWidth = fm.stringWidth(gameTitleStr);
		}
		g2d.setFont(bigFont);
		int ascent = fm.getAscent();
		int strX = (this.getWidth() - strWidth)/2;
		int strY = (this.getHeight() + ascent)/2 - ascent;
		g2d.setPaint(Color.GREEN);
		g2d.drawString(gameTitleStr, strX, strY);

		g2d.setFont(originalFont);
		fm = g2d.getFontMetrics();
		String newGameStr = "Press <Space> to Start a New Game.";
		strWidth = fm.stringWidth(newGameStr);
		strX = (this.getWidth() - strWidth)/2;
		strY = (this.getHeight() + fm.getAscent())/2 + ascent + 16;
		g2d.setPaint(Color.WHITE);
		g2d.drawString(newGameStr, strX, strY);

		fm = g2d.getFontMetrics();
		String exitGameStr = "Press <Esc> to Exit the Game.";
		strWidth = fm.stringWidth(exitGameStr);
		strX = (this.getWidth() - strWidth)/2;
		strY = strY + 16;
		g2d.drawString(exitGameStr, strX, strY);
	}

	/**
	 * Prepare screen for game over.
	 */
	public void doGameOver(){
		shipsValueLabel.setForeground(new Color(128, 0, 0));
	}

	/**
	 * Prepare screen for a new game.
	 */
	public void doNewGame(){		
		lastAsteroidTime = -NEW_ASTEROID_DELAY;
		lastShipTime = -NEW_SHIP_DELAY;

		bigFont = originalFont;
		biggestFont = null;

		// set labels' text
		shipsValueLabel.setForeground(Color.BLACK);
		shipsValueLabel.setText(Integer.toString(status.getShipsLeft()));
		destroyedValueLabel.setText(Long.toString(status.getAsteroidsDestroyed()));
		destroyedValueLabel.setText(Long.toString(status.getAsteroidsDestroyed()) + " / " + asteroidsToDestroyValue);//TODO
		levelValueLabel.setText(Integer.toString(status.getCurrentLevel()));//TODO
		scorePointsValueLabel.setText(Long.toString(status.getScorePoints()));
	}

	/**
	 * Sets the game graphics manager.
	 * @param graphicsMan the graphics manager
	 */
	public void setGraphicsMan(GraphicsManager graphicsMan) {
		this.graphicsMan = graphicsMan;
	}

	/**
	 * Sets the game logic handler
	 * @param gameLogic the game logic handler
	 */
	public void setGameLogic(GameLogic gameLogic) {
		this.gameLogic = gameLogic;
		this.status = gameLogic.getStatus();
		this.soundMan = gameLogic.getSoundMan();
	}

	/**
	 * Sets the label that displays the value for asteroids destroyed.
	 * @param destroyedValueLabel the label to set
	 */
	public void setDestroyedValueLabel(JLabel destroyedValueLabel) {
		this.destroyedValueLabel = destroyedValueLabel;
	}

	/**
	 * Sets the label that displays the value for ship (lives) left
	 * @param shipsValueLabel the label to set
	 */
	public void setShipsValueLabel(JLabel shipsValueLabel) {
		this.shipsValueLabel = shipsValueLabel;
	}
	
	/**
	 * Sets the label that displays the value of enemy ships destroyed.
	 * @param destroyedEnemyShipsValueLabel the label to set
	 */
	public void setDestroyedEnemyShipsValueLabel(JLabel destroyedEnemyShipsValueLabel) {
		this.destroyedEnemyValueLabel = destroyedEnemyShipsValueLabel;
	}
	
	public void setLevelValueLabel(JLabel levelValueLabel) {//TODO comments
		this.levelValueLabel = levelValueLabel;
	}
	
	/**
	 * Sets the label that displays the total score.
	 * @param scorePointsValueLabel the label to set
	 */
	public void setScorePointsValueLabel(JLabel scorePointsValueLabel) {
		this.scorePointsValueLabel = scorePointsValueLabel;
	}
	
	public void setAsteroidValue(long asteroidToDestroyValue){//TODO remove
		this.asteroidsToDestroyValue = asteroidToDestroyValue;
	}
}
