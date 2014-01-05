package uva.verspeek.hearthstone.controllers;



import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.util.HorizontalAlign;

import uva.verspeek.hearthstone.activities.GameActivity;
import uva.verspeek.hearthstone.instances.User;
import uva.verspeek.hearthstone.models.Constants;
import uva.verspeek.hearthstone.models.Decks;
import uva.verspeek.hearthstone.tools.Utils;


import android.util.Log;

public class GameController {
	static GameActivity gameScreen;
	
	public int healthp1 = 30;
	public int healthp2 = 30;
	public int manap1 = 0;
	public int manap2 = 0;
	public int manap1Max = 0;
	public int manap2Max = 0;
	
	public Text manaTextp1;
	public Text manaTextp2;
	public Text healthTextp1;
	public Text healthTextp2;
	
	public boolean myTurn = false;
	
	public GameController(GameActivity gameScreen) {
		GameController.gameScreen = gameScreen;
	}
	

	public void startTurn() {
		gameScreen.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Utils.showToastAlert(gameScreen, "Its your turn");
			}
		});

		myTurn = true;
		if (!gameScreen.secondPlayer) {
			manap1Max++;
			setMana(manap1Max, gameScreen.secondPlayer);
			if (gameScreen.cc.card1Id == -1)
				gameScreen.cc.drawCard("card1", true);
			else if (gameScreen.cc.card2Id == -1)
				gameScreen.cc.drawCard("card2", true);
			else if (gameScreen.cc.card3Id == -1)
				gameScreen.cc.drawCard("card3", true);
			else if (gameScreen.cc.card4Id == -1)
				gameScreen.cc.drawCard("card4", true);
		} else {
			manap2Max++;
			setMana(manap2Max, gameScreen.secondPlayer);
			if (gameScreen.cc.card1Id == -1)
				gameScreen.cc.drawCard("card1p2", true);
			else if (gameScreen.cc.card2Id == -1)
				gameScreen.cc.drawCard("card2p2", true);
			else if (gameScreen.cc.card3Id == -1)
				gameScreen.cc.drawCard("card3p2", true);
			else if (gameScreen.cc.card4Id == -1)
				gameScreen.cc.drawCard("card4p2", true);
		}
		Log.d("FUNCTION", "4END");
	}



	public void endTurn() {
		gameScreen.cc.selectedCardId = -1;
		gameScreen.cc.selectedFromField = false;
		if (gameScreen.cc.selectedCard != null) {
			gameScreen.cc.selectedCard.setScale(1, 1);
			gameScreen.cc.selectedCard = null;
		}
		Log.d("FUNCTION", "5START");
		float x, y;
		x = 10;
		if (gameScreen.secondPlayer) {
			y = 10;
			manap1Max++;
			setMana(manap1Max, !gameScreen.secondPlayer);
		} else {
			y = GameActivity.CAMERA_HEIGHT - 40;
			manap2Max++;
			setMana(manap2Max, !gameScreen.secondPlayer);
		}
		gameScreen.sendUpdateEvent(x, y);
		updateMove(false, Utils.userName, x, y);
		gameScreen.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Utils.showToastAlert(gameScreen, "Opponent's turn");
			}
		});
		myTurn = false;
		gameScreen.cc.idsAttacked.clear();
		Log.d("FUNCTION", "5END");
	}
	
	public void updateMove(boolean isRemote, String userName, float xPer,
			float yPer) {
		Log.d("FUNCTION", "22S");
		if (gameScreen.userMap.get(userName) != null) {
			if (isRemote) {
				xPer = Utils.getValueFromPercent(xPer, GameActivity.CAMERA_WIDTH);
				yPer = Utils.getValueFromPercent(yPer, GameActivity.CAMERA_HEIGHT);
			}
			Sprite sprite = gameScreen.userMap.get(userName).getSprite();
			float deltaX = sprite.getX() - xPer;
			float deltaY = sprite.getY() - yPer;
			float distance = (float) Math.sqrt((deltaX * deltaX)
					+ (deltaY * deltaY));
			float time = distance / Constants.CharacterSpeed;
			if (distance > 0) {
				sprite.registerEntityModifier(new MoveModifier(time, sprite
						.getX(), xPer, sprite.getY(), yPer));
			}
		}
		Log.d("FUNCTION", "22E");
	}


	boolean checkMana(int id, boolean updateProperty) {
		int manaCost;
		if (updateProperty) {
			manaCost = Decks.getMana(id, gameScreen.secondPlayer);
			if (gameScreen.secondPlayer) {
				if (manap2 - manaCost >= 0) {
					setMana(manap2 - manaCost, gameScreen.secondPlayer);
					return true;
				} else
					return false;
			} else {
				if (manap1 - manaCost >= 0) {
					setMana(manap1 - manaCost, gameScreen.secondPlayer);
					return true;
				} else
					return false;
			}
		} else {
			manaCost = Decks.getMana(id, !gameScreen.secondPlayer);
			if (gameScreen.secondPlayer) {
				setMana(manap1 - manaCost, !gameScreen.secondPlayer);
			} else {
				setMana(manap2 - manaCost, !gameScreen.secondPlayer);
			}
			return true;
		}
	}

	public void setMana(int mana, boolean secondPlayer) {
		if (secondPlayer) {
			manap2 = mana;
			gameScreen.mMainScene.detachChild(manaTextp2);
			manaTextp2 = new Text(healthTextp2.getWidth() + healthTextp2.getX()
					+ 20, 10, gameScreen.mFont2, "mana: " + manap2, new TextOptions(
					HorizontalAlign.LEFT), gameScreen.getVertexBufferObjectManager());
			gameScreen.mMainScene.attachChild(manaTextp2);
		} else {
			manap1 = mana;
			gameScreen.mMainScene.detachChild(manaTextp1);
			manaTextp1 = new Text(healthTextp1.getWidth() + healthTextp1.getX()
					+ 20, GameActivity.CAMERA_HEIGHT - 30, gameScreen.mFont2, "mana: " + manap1,
					new TextOptions(HorizontalAlign.LEFT),
					gameScreen.getVertexBufferObjectManager());
			gameScreen.mMainScene.attachChild(manaTextp1);
		}
	}

	public void setHealth(int health, boolean secondPlayer) {
		if (secondPlayer) {
			healthp2 = health;
			gameScreen.mMainScene.detachChild(healthTextp2);
			healthTextp2 = new Text(60, 10, gameScreen.mFont2, "health: " + healthp2,
					new TextOptions(HorizontalAlign.LEFT),
					gameScreen.getVertexBufferObjectManager());
			gameScreen.mMainScene.attachChild(healthTextp2);
		} else {
			healthp1 = health;
			gameScreen.mMainScene.detachChild(healthTextp1);
			healthTextp1 = new Text(60, GameActivity.CAMERA_HEIGHT - 30, gameScreen.mFont2,
					"health: " + healthp1,
					new TextOptions(HorizontalAlign.LEFT),
					gameScreen.getVertexBufferObjectManager());
			gameScreen.mMainScene.attachChild(healthTextp1);
		}
		checkGameOver();
	}

	public void checkGameOver() {
		int gameOver = 0;
		if (healthp2 < 0) {
			if (gameScreen.secondPlayer)
				gameOver = 1;
			else
				gameOver = 2;
		} else if (healthp1 < 0) {
			if (gameScreen.secondPlayer)
				gameOver = 2;
			else
				gameOver = 1;
		}
		if (gameOver == 1) {
			gameScreen.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Utils.showToastAlert(gameScreen, "You are Defeated");
				}
			});
		} else if (gameOver == 2) {
			endTurn();
			gameScreen.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Utils.showToastAlert(gameScreen, "Victory");
				}
			});

		}
	}
	

	public void addMorePlayer(boolean isMine, String userName,
			boolean secondPlayer) {

		Log.d("FUNCTION", "13S");
		Log.d("NEWUSER", userName + ": isMine: " + isMine + "sp: "
				+ secondPlayer);
		// if already in room
		if (gameScreen.userMap.get(userName) != null) {
			return;
		}

		Log.d("userNameGame", userName);
		char index = userName.charAt(userName.length() - 1);
		TiledTextureRegion tiledTextureRegion = null;
		if (index == '1') {
			tiledTextureRegion = gameScreen.mPlayerTiledTextureRegion1;
		} else if (index == '2') {
			tiledTextureRegion = gameScreen.mPlayerTiledTextureRegion2;
		} else if (index == '3') {
			tiledTextureRegion = gameScreen.mPlayerTiledTextureRegion3;
		} else if (index == '4') {
			tiledTextureRegion = gameScreen.mPlayerTiledTextureRegion4;
		}
		int height;
		Log.d("NEWUSER", Utils.userName);
		if (isMine == false)
			height = GameActivity.CAMERA_HEIGHT - 40;
		else if (userName.equals(Utils.userName) && !secondPlayer)
			height = GameActivity.CAMERA_HEIGHT - 40;
		else {
			height = 10;
		}
		final Sprite face = new Sprite(10, height, tiledTextureRegion,
				gameScreen.getVertexBufferObjectManager());
		face.setScale(1.5f);
		face.setZIndex(99999);
		gameScreen.mMainScene.attachChild(face);
		User user = new User(face.getX(), face.getY(), face);
		gameScreen.userMap.put(userName, user);

		if (height == 10 && !secondPlayer) {
			startTurn();
		}
		Log.d("FUNCTION", "13E");
	}
}
