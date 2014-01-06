package uva.verspeek.hearthstone.controllers;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import org.andengine.engine.Engine.EngineLock;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.sprite.Sprite;
import org.json.JSONException;
import org.json.JSONObject;

import uva.verspeek.hearthstone.activities.GameActivity;
import uva.verspeek.hearthstone.instances.CardSprite;
import uva.verspeek.hearthstone.models.Decks;
import uva.verspeek.hearthstone.tools.Utils;
import uva.verspeek.hearthstone.tools.createSprites;


import android.util.Log;

public class CardsController {
	static GameActivity gameScreen;

	public CardSprite card1;
	public CardSprite card2;
	public CardSprite card3;
	public CardSprite card4;

	public CardSprite card1p2;
	public CardSprite card2p2;
	public CardSprite card3p2;
	public CardSprite card4p2;
	
	public static Sprite placeholder1;
	public static Sprite placeholder2;
	public static Sprite placeholder3;
	public static Sprite placeholder4;
	
	public static int widthCard, heightCard;

	public CardSprite selectedCard;
	public CardSprite moveSprite;

	public int selectedCardIdEnemy = -1;
	public int selectedCardId = -1;

	public int card1Id = -1;
	public int card2Id = -1;
	public int card3Id = -1;
	public int card4Id = -1;

	public boolean[] usedCards;
	public boolean selectedFromField = false;
	public List<Integer> idsAttacked = new ArrayList<Integer>();
	public HashMap<String, CardSprite> objectMap = new HashMap<String, CardSprite>();

	public int card1FieldId;
	public int card2FieldId;
	public int card3FieldId;
	public int card4FieldId;

	private int card1FieldIdEnemy;
	private int card2FieldIdEnemy;
	private int card3FieldIdEnemy;
	private int card4FieldIdEnemy;

	private CardSprite objectSprite;

	public CardsController(GameActivity gameScreen) {
		CardsController.gameScreen = gameScreen;
	}

	public void checkForCardMove(float x, float y) {
		Log.d("FUNCTION", "19S");
		Log.d("CHECKCARDMOVE", "" + selectedCardId + "/" + selectedCardIdEnemy
				+ selectedFromField);
		if (selectedCardIdEnemy != -1 && selectedFromField) {
			int pos = 0;
			String position = "";
			if (selectedCardId == card1FieldId){
				pos = 1;
			}
			if (selectedCardId == card2FieldId){
				pos = 2;
			}
			if (selectedCardId == card3FieldId){
				pos = 3;
			}
			if (selectedCardId == card4FieldId){
				pos = 4;
			}
			if (selectedCardIdEnemy == card1FieldIdEnemy){
				if (gameScreen.secondPlayer){
					position = "card1";
				} else {
					position = "card1p2";
				}
			}
			if (selectedCardIdEnemy == card2FieldIdEnemy){
				if (gameScreen.secondPlayer){
					position = "card2";
				} else {
					position = "card2p2";
				}
			}
			if (selectedCardIdEnemy == card3FieldIdEnemy){
				if (gameScreen.secondPlayer){
					position = "card3";
				} else {
					position = "card3p2";
				}
			}
			if (selectedCardIdEnemy == card4FieldIdEnemy){
				if (gameScreen.secondPlayer){
					position = "card4";
				} else {
					position = "card4p2";
				}
			}
			Log.d("ATTACK", "" + pos + "/" + selectedCardId + ":"
					+ card1FieldId + ":" + card2FieldId + ":" + card3FieldId
					+ ":" + card4FieldId + ":");

			playCard(selectedCardId, selectedCardIdEnemy, position,
					null, true, pos);
		} else if (selectedCardId != -1 && !selectedFromField) {
			playCard(selectedCardId, -1, getPosition(x, y), null, true, 0);
		} else if (selectedCardId != -1 && selectedFromField) {
			String destination = getPosition(x, y);
			if (destination.equals("attackp1")
					|| destination.equals("attackp2")) {
				playCard(selectedCardId, -1, destination, null, true, 0);
			}
		}
		gameScreen.cc.checkHighlight();

		Log.d("FUNCTION", "19E");
	}
	
	public void checkHighlight(){
		String position = "";
		CardSprite card;
		if (card1FieldId != -1){
			if (gameScreen.secondPlayer){
				position = "card1p2";
			} else {
				position = "card1";
			}
			if (objectMap.get(position) != null) {
				card = objectMap.get(position);
				if (!idsAttacked.contains(card1FieldId) && gameScreen.gc.myTurn) {
					card.ShowHighlight();
				} else {
					card.HideHighlight();
				}
			}
		}
		if (card2FieldId != -1){
			if (gameScreen.secondPlayer){
				position = "card2p2";
			} else {
				position = "card2";
			}
			if (objectMap.get(position) != null) {
				card = objectMap.get(position);
				if (!idsAttacked.contains(card2FieldId) && gameScreen.gc.myTurn) {
					card.ShowHighlight();
				} else {
					card.HideHighlight();
				}
			}
		}
		if (card3FieldId != -1){
			if (gameScreen.secondPlayer){
				position = "card3p2";
			} else {
				position = "card3";
			}
			if (objectMap.get(position) != null) {
				card = objectMap.get(position);
				if (!idsAttacked.contains(card3FieldId) && gameScreen.gc.myTurn) {
					card.ShowHighlight();
				} else {
					card.HideHighlight();
				}
			}
		}
		if (card4FieldId != -1){
			if (gameScreen.secondPlayer){
				position = "card4p2";
			} else {
				position = "card4";
			}
			if (objectMap.get(position) != null) {
				card = objectMap.get(position);
				if (!idsAttacked.contains(card4FieldId) && gameScreen.gc.myTurn) {
					card.ShowHighlight();
				} else {
					card.HideHighlight();
				}
			}
		}
	
	}

	public String getPosition(float x, float y) {
		Log.d("FUNCTION", "18S");
		String position = "";

		float height1 = GameActivity.CAMERA_HEIGHT / 2f;
		float height2 = GameActivity.CAMERA_HEIGHT * (1f / 4f);

		if (x > 0 && x < GameActivity.CAMERA_WIDTH * (1f / 4f) && y < height1
				&& y > height2) {
			position = "card1p2";
		} else if (x > GameActivity.CAMERA_WIDTH * (1f / 4f)
				&& x < GameActivity.CAMERA_WIDTH * (2f / 4f) && y < height1
				&& y > height2) {
			position = "card2p2";
		} else if (x > GameActivity.CAMERA_WIDTH * (2f / 4f)
				&& x < GameActivity.CAMERA_WIDTH * (3f / 4f) && y < height1
				&& y > height2) {
			position = "card3p2";
		} else if (x > GameActivity.CAMERA_WIDTH * (3f / 4f)
				&& x < GameActivity.CAMERA_WIDTH && y < height1 && y > height2) {
			position = "card4p2";
		}

		height2 = GameActivity.CAMERA_HEIGHT * (3f / 4f);
		if (x > 0 && x < GameActivity.CAMERA_WIDTH * (1f / 4f) && y < height2
				&& y > height1) {
			position = "card1";
		} else if (x > GameActivity.CAMERA_WIDTH * (1f / 4f)
				&& x < GameActivity.CAMERA_WIDTH * (2f / 4f) && y < height2
				&& y > height1) {
			position = "card2";
		} else if (x > GameActivity.CAMERA_WIDTH * (2f / 4f)
				&& x < GameActivity.CAMERA_WIDTH * (3f / 4f) && y < height2
				&& y > height1) {
			position = "card3";
		} else if (x > GameActivity.CAMERA_WIDTH * (3f / 4f)
				&& x < GameActivity.CAMERA_WIDTH && y < height2 && y > height1) {
			position = "card4";
		}

		if (x < 50 && y < 50) {
			position = "attackp2";
		} else if (x < 50 && y > GameActivity.CAMERA_HEIGHT - 50) {
			position = "attackp1";
		}
		Log.d("FUNCTION", "18E");
		return position;
	}

	public synchronized void playCard(final int selectedObjectId,
			final int selectedObjectIdEnemy, final String destination,
			final String userName, boolean updateProperty, int pos) {
		gameScreen.cc.checkHighlight();
		Log.d("FUNCTION", "21S");
		Log.d("PLACEOBJECT", "" + selectedObjectId + "/"
				+ selectedObjectIdEnemy + selectedFromField + destination);
		
		if (destination.equals("") || destination == null)
			return;
		
		if (updateProperty){
			if (Decks.getType(selectedObjectId, gameScreen.secondPlayer) == "s"){
				playSpellCard(selectedObjectId, destination, userName, updateProperty);
				return;
			}
		}else{
			if (Decks.getType(selectedObjectId, gameScreen.secondPlayer) == "s"){
				playSpellCard(selectedObjectId, destination, userName, updateProperty);
				return;
			}
		}
		
		if ((destination == "attackp1" || destination == "attackp2") && selectedFromField) {
			if (gameScreen.secondPlayer && updateProperty
					&& destination == "attackp2")
				return;
			if (!gameScreen.secondPlayer && updateProperty
					&& destination == "attackp1")
				return;
			attackCharacter(selectedObjectId, updateProperty);
			return;
		}
		


		if (selectedObjectIdEnemy != -1
				&& (!updateProperty || selectedFromField)) {
			attackCard(selectedObjectId, selectedObjectIdEnemy, destination,
					userName, updateProperty, pos);
			return;
		}	
	

		if (destination.charAt(destination.length() - 2) == 'p') {
			if (!gameScreen.secondPlayer && updateProperty)
				return;
		} else {
			if (gameScreen.secondPlayer && updateProperty)
				return;
		}



		if (objectMap.get(destination) != null) {
			return;
		}

		if (!gameScreen.gc.checkMana(selectedObjectId, updateProperty)) {
			gameScreen.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Utils.showToastAlert(gameScreen, "not enough mana");
				}
			});
			return;
		}

		// create new sprite with new ontouch options
		CardSprite sprite = null;
		if (gameScreen.secondPlayer) {
			if (!updateProperty) { // handle move of other user
				sprite = createSprites.newCardSpriteOtherField(
						selectedObjectId, GameActivity.CAMERA_WIDTH / 2,
						GameActivity.CAMERA_HEIGHT - 100);
			} else { // handle move of current user
				sprite = createSprites.newCardSpriteOwnField(selectedObjectId,
						GameActivity.CAMERA_WIDTH / 2, 0 + 100);
			}
		} else {
			if (!updateProperty) {
				sprite = createSprites
						.newCardSpriteOtherField(selectedObjectId,
								GameActivity.CAMERA_WIDTH / 2, 0 + 70);
			} else {
				sprite = createSprites.newCardSpriteOwnField(selectedObjectId,
						GameActivity.CAMERA_WIDTH / 2,
						GameActivity.CAMERA_HEIGHT - CardsController.heightCard
								- 70);
			}
		}
		if (!updateProperty) {
			if (gameScreen.secondPlayer) {
				if (pos == 1) {
					gameScreen.removeSprite(card1);
					card1 = null;
				} else if (pos == 2) {
					gameScreen.removeSprite(card2);
					card2 = null;
				} else if (pos == 3) {
					gameScreen.removeSprite(card3);
					card3 = null;
				} else if (pos == 4) {
					gameScreen.removeSprite(card4);
					card4 = null;
				}
			} else {
				if (pos == 1) {
					gameScreen.removeSprite(card1p2);
					card1p2 = null;
				} else if (pos == 2) {
					gameScreen.removeSprite(card2p2);
					card2p2 = null;
				} else if (pos == 3) {
					gameScreen.removeSprite(card3p2);
					card3p2 = null;
				} else if (pos == 4) {
					gameScreen.removeSprite(card4p2);
					card4p2 = null;
				}
			}

		} else if (selectedObjectId == card1Id) {
			card1Id = -1;
			pos = 1;
			if (gameScreen.secondPlayer) {
				gameScreen.removeSprite(card1p2);
				card1p2 = null;
			} else {
				gameScreen.removeSprite(card1);
				card1 = null;
			}

		} else if (selectedObjectId == card2Id) {
			card2Id = -1;
			pos = 2;
			if (gameScreen.secondPlayer) {
				gameScreen.removeSprite(card2p2);
				card2p2 = null;
			} else {
				gameScreen.removeSprite(card2);
				card2 = null;
			}
		} else if (selectedObjectId == card3Id) {
			card3Id = -1;
			pos = 3;
			if (gameScreen.secondPlayer) {
				gameScreen.removeSprite(card3p2);
				card3p2 = null;
			} else {
				gameScreen.removeSprite(card3);
				card3 = null;
			}
		} else if (selectedObjectId == card4Id) {
			card4Id = -1;
			pos = 4;
			if (gameScreen.secondPlayer) {
				gameScreen.removeSprite(card4p2);
				card4p2 = null;
			} else {
				gameScreen.removeSprite(card4);
				card4 = null;
			}
		} else {
			return;
		}
		sprite.setSize(CardsController.widthCard, CardsController.heightCard);

		float xDest = 0;
		float yDest = 0;
		float padding = (GameActivity.CAMERA_WIDTH - (CardsController.widthCard * 4)) / 5.0f;
		float heightp1 = (GameActivity.CAMERA_HEIGHT / 2)
				+ ((GameActivity.CAMERA_HEIGHT / 4 - 35) - CardsController.heightCard)
				/ 2;
		float heightp2 = (GameActivity.CAMERA_HEIGHT / 2)
				- ((GameActivity.CAMERA_HEIGHT / 4 - 35) - CardsController.heightCard)
				/ 2 - CardsController.heightCard;
		if (destination.equals("card1")) {
			xDest = padding;
			yDest = heightp1;
			if (updateProperty) {
				card1FieldId = selectedObjectId;
			} else {
				card1FieldIdEnemy = selectedObjectId;
			}
		} else if (destination.equals("card2")) {
			xDest = padding * 2 + CardsController.widthCard;
			yDest = heightp1;
			if (updateProperty) {
				card2FieldId = selectedObjectId;
			} else {
				card2FieldIdEnemy = selectedObjectId;
			}
		} else if (destination.equals("card3")) {
			xDest = padding * 3 + (CardsController.widthCard * 2);
			yDest = heightp1;
			if (updateProperty) {
				card3FieldId = selectedObjectId;
			} else {
				card3FieldIdEnemy = selectedObjectId;
			}
		} else if (destination.equals("card4")) {
			xDest = padding * 4 + (CardsController.widthCard * 3);
			yDest = heightp1;
			if (updateProperty) {
				card4FieldId = selectedObjectId;
			} else {
				card4FieldIdEnemy = selectedObjectId;
			}
		} else if (destination.equals("card1p2")) {
			xDest = padding;
			yDest = heightp2;
			if (updateProperty) {
				card1FieldId = selectedObjectId;
			} else {
				card1FieldIdEnemy = selectedObjectId;
			}
		} else if (destination.equals("card2p2")) {
			xDest = padding * 2 + CardsController.widthCard;
			yDest = heightp2;
			if (updateProperty) {
				card2FieldId = selectedObjectId;
			} else {
				card2FieldIdEnemy = selectedObjectId;
			}
		} else if (destination.equals("card3p2")) {
			xDest = padding * 3 + (CardsController.widthCard * 2);
			yDest = heightp2;
			if (updateProperty) {
				card3FieldId = selectedObjectId;
			} else {
				card3FieldIdEnemy = selectedObjectId;
			}
		} else if (destination.equals("card4p2")) {
			xDest = padding * 4 + (CardsController.widthCard * 3);
			yDest = heightp2;
			if (updateProperty) {
				card4FieldId = selectedObjectId;
			} else {
				card4FieldIdEnemy = selectedObjectId;
			}
		} else {
			return;
		}

		selectedCard = null;
		selectedCardId = -1;

		objectMap.put(destination, sprite);
		gameScreen.mMainScene.attachChild(sprite);
		gameScreen.mMainScene.registerTouchArea(sprite);
		sprite.registerEntityModifier(new MoveModifier(1, sprite.getX(), xDest,
				sprite.getY(), yDest));
		if (updateProperty) {
			gameScreen
					.updateProperty(destination, selectedObjectId + ":" + pos);
			idsAttacked.add(selectedObjectId);
		}

		gameScreen.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (userName != null) {
					Utils.showToastAlert(gameScreen, userName
							+ " has played a card at position " + destination);
				}
			}
		});
		Log.d("FUNCTION", "21E");
	}
	
	public void playSpellCard(final int selectedObjectId, final String destination,
			final String userName, boolean updateProperty) {
		
		gameScreen.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Utils.showToastAlert(gameScreen,
						"Spell cards not implemented yet");
			}
		});
		return;
		
	}

	public void drawCard(String card, boolean updateProperty) {
		CardSprite sprite = null;
		Log.d("DRAWCARD", card + updateProperty);
		int id = -1;
		float width = -1, height = -1;
		float padding = (GameActivity.CAMERA_WIDTH - (CardsController.widthCard * 4)) / 5.0f;
		if (card.equals("card1")) {
			if (!updateProperty)
				id = 0;
			else {
				card1Id = pickCardId();
				id = card1Id;
			}
			width = padding;
			
			height = GameActivity.CAMERA_HEIGHT - CardsController.heightCard - 70;
			card1 = createSprites.newCardSprite(id, GameActivity.CAMERA_WIDTH, GameActivity.CAMERA_HEIGHT);
			sprite = card1;
		} else if (card.equals("card2")) {
			if (!updateProperty)
				id = 0;
			else {
				card2Id = pickCardId();
				id = card2Id;
			}
			width = padding * 2 + (CardsController.widthCard * 1);
			height = GameActivity.CAMERA_HEIGHT - CardsController.heightCard - 70;
			card2 = createSprites.newCardSprite(id, GameActivity.CAMERA_WIDTH, GameActivity.CAMERA_HEIGHT);
			sprite = card2;
		} else if (card.equals("card3")) {
			if (!updateProperty)
				id = 0;
			else {
				card3Id = pickCardId();
				id = card3Id;
			}
			width = padding * 3 + (CardsController.widthCard * 2);
			height = GameActivity.CAMERA_HEIGHT - CardsController.heightCard - 70;
			card3 = createSprites.newCardSprite(id, GameActivity.CAMERA_WIDTH, GameActivity.CAMERA_HEIGHT);
			sprite = card3;
		} else if (card.equals("card4")) {
			if (!updateProperty)
				id = 0;
			else {
				card4Id = pickCardId();
				id = card4Id;
			}
			width = padding * 4 + (CardsController.widthCard * 3);
			height = GameActivity.CAMERA_HEIGHT - CardsController.heightCard - 70;
			card4 = createSprites.newCardSprite(id, GameActivity.CAMERA_WIDTH, GameActivity.CAMERA_HEIGHT);
			sprite = card4;
		} else if (card.equals("card1p2")) {
			if (!updateProperty)
				id = 0;
			else {
				card1Id = pickCardId();
				id = card1Id;
			}
			width = padding;
			height = 0 + 70;
			card1p2 = createSprites.newCardSprite(id, GameActivity.CAMERA_WIDTH, -CardsController.heightCard);
			sprite = card1p2;
		} else if (card.equals("card2p2")) {
			if (!updateProperty)
				id = 0;
			else {
				card2Id = pickCardId();
				id = card2Id;
			}
			width = padding * 2 + (CardsController.widthCard * 1);
			height = 0 + 70;
			card2p2 = createSprites.newCardSprite(id, GameActivity.CAMERA_WIDTH, -CardsController.heightCard);
			sprite = card2p2;
		} else if (card.equals("card3p2")) {
			if (!updateProperty)
				id = 0;
			else {
				card3Id = pickCardId();
				id = card3Id;
			}
			width = padding * 3 + (CardsController.widthCard * 2);
			height = 0 + 70;
			card3p2 = createSprites.newCardSprite(id, GameActivity.CAMERA_WIDTH, -CardsController.heightCard);
			sprite = card3p2;
		} else if (card.equals("card4p2")) {
			if (!updateProperty)
				id = 0;
			else {
				card4Id = pickCardId();
				id = card4Id;
			}
			width = padding * 4 + (CardsController.widthCard * 3);
			height = 0 + 70;
			card4p2 = createSprites.newCardSprite(id, GameActivity.CAMERA_WIDTH, -CardsController.heightCard);
			sprite = card4p2;
		}
		if (id != -1) {
			MoveModifier drawCardAnimation = new MoveModifier(0.7f,
					sprite.getX(), width, sprite.getY(),
					height){
				@Override
				protected void onModifierFinished(IEntity pItem) {
					super.onModifierFinished(pItem);
					gameScreen.cc.checkHighlight();
				}
			};
			sprite.registerEntityModifier(drawCardAnimation);
			sprite.setSize(CardsController.widthCard, CardsController.heightCard);
			if (updateProperty) {
				gameScreen.mMainScene.registerTouchArea(sprite);

				JSONObject object = new JSONObject();
				try {
					object.put("drawCard", card);
					gameScreen.theClient.sendChat(object.toString());
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				sprite.ChangeAttack("");
				sprite.ChangeHealth("");
				sprite.ChangeMana("");
			}
			gameScreen.mMainScene.attachChild(sprite);

		}
	}

	public int pickCardId() {
		Log.d("FUNCTION", "12S");
		int id;
		if (Utils.allElementsTheSame(usedCards))
			return -1;
		while (usedCards[id = (int) Math.floor(Math.random()
				* Decks.cardsp1.length)] == true)
			;

		usedCards[id] = true;
		Log.d("FUNCTION", "12E");
		return id;
	}

	public synchronized void attackCard(final int selectedObject,
			final int selectedObjectIdEnemy, final String destination,
			final String userName, boolean updateProperty, int pos) {
		Log.d("FUNCTION", "20S");
		objectSprite = null;

		if (objectMap.get(destination) != null) {
			objectSprite = objectMap.get(destination);
		} else {
			Log.d("ERROR", "NO CARD AT SELECTED DESTINATION");
		}
		boolean destroyAttacked = false;
		boolean destroyAttacker = false;
		int health;
		String attackerDestination = null;

		Log.d("POSITION", "" + pos);

		if ((gameScreen.secondPlayer && !updateProperty)
				|| (!gameScreen.secondPlayer && updateProperty)) {
			if (pos == 1) {
				attackerDestination = "card1";
			} else if (pos == 2) {
				attackerDestination = "card2";
			} else if (pos == 3) {
				attackerDestination = "card3";
			} else if (pos == 4) {
				attackerDestination = "card4";
			}
		} else {
			if (pos == 1) {
				attackerDestination = "card1p2";
			} else if (pos == 2) {
				attackerDestination = "card2p2";
			} else if (pos == 3) {
				attackerDestination = "card3p2";
			} else if (pos == 4) {
				attackerDestination = "card4p2";
			}
		}

		// you attack opponent
		if (updateProperty) {
			if (idsAttacked.contains(selectedObject)) {
				gameScreen.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Utils.showToastAlert(gameScreen,
								"this card can't attack right now");
					}
				});
				return;
			}
			idsAttacked.add(selectedObject);

			health = Decks.getHealth(selectedObjectIdEnemy,
					!gameScreen.secondPlayer);
			Decks.setHealth(
					selectedObjectIdEnemy,
					health
							- Decks.getAttack(selectedObject,
									gameScreen.secondPlayer),
					!gameScreen.secondPlayer);
			objectSprite.ShowDamage("-"
					+ Decks.getAttack(selectedObject, gameScreen.secondPlayer));
			objectSprite.ChangeHealth(""
					+ Decks.getHealth(selectedObjectIdEnemy,
							!gameScreen.secondPlayer));
			if (Decks
					.getHealth(selectedObjectIdEnemy, !gameScreen.secondPlayer) <= 0) {
				destroyAttacked = true;
			}

			health = Decks.getHealth(selectedObject, gameScreen.secondPlayer);
			Decks.setHealth(
					selectedObject,
					health
							- Decks.getAttack(selectedObjectIdEnemy,
									!gameScreen.secondPlayer),
					gameScreen.secondPlayer);
			selectedCard.ShowDamage("-"
					+ Decks.getAttack(selectedObjectIdEnemy,
							!gameScreen.secondPlayer));
			selectedCard.ChangeHealth(""
					+ Decks.getHealth(selectedObject, gameScreen.secondPlayer));
			if (Decks.getHealth(selectedObject, gameScreen.secondPlayer) <= 0) {
				destroyAttacker = true;
			}
			
			// you get attacked by opponent
		} else {
			health = Decks.getHealth(selectedObjectIdEnemy,
					gameScreen.secondPlayer);
			Decks.setHealth(
					selectedObjectIdEnemy,
					health
							- Decks.getAttack(selectedObject,
									!gameScreen.secondPlayer),
					gameScreen.secondPlayer);
			objectSprite
					.ShowDamage("-"
							+ Decks.getAttack(selectedObject,
									!gameScreen.secondPlayer));
			objectSprite.ChangeHealth(""
					+ Decks.getHealth(selectedObjectIdEnemy,
							gameScreen.secondPlayer));

			if (Decks.getHealth(selectedObjectIdEnemy, gameScreen.secondPlayer) <= 0) {
				destroyAttacked = true;
			}

			if (objectMap.get(attackerDestination) != null) {
				selectedCard = objectMap.get(attackerDestination);
			} else {
				Log.d("ERROR", "NO ATTACK DESTINATON");
				return;
			}
			health = Decks.getHealth(selectedObject, !gameScreen.secondPlayer);
			Decks.setHealth(
					selectedObject,
					health
							- Decks.getAttack(selectedObjectIdEnemy,
									gameScreen.secondPlayer),
					!gameScreen.secondPlayer);
			selectedCard.ShowDamage("-"
					+ Decks.getAttack(selectedObjectIdEnemy,
							gameScreen.secondPlayer));
			selectedCard
					.ChangeHealth(""
							+ Decks.getHealth(selectedObject,
									!gameScreen.secondPlayer));
			if (Decks.getHealth(selectedObject, !gameScreen.secondPlayer) <= 0) {
				destroyAttacker = true;
			}
		}
		float startx = selectedCard.getX();
		float starty = selectedCard.getY();
		float height = CardsController.heightCard * (2 / 3f);
		if ((gameScreen.secondPlayer && updateProperty)
				|| (!gameScreen.secondPlayer && !updateProperty)) {
			height = -CardsController.heightCard * (2 / 3f);
		}
		final MoveModifier moveBack = new MoveModifier(0.7f,
				objectSprite.getX(), startx, objectSprite.getY() + height,
				starty){
			@Override
			protected void onModifierFinished(IEntity pItem) {
				super.onModifierFinished(pItem);
				gameScreen.cc.checkHighlight();
			}
		};

		moveSprite = selectedCard;

		MoveModifier moveForward = new MoveModifier(0.2f, startx,
				objectSprite.getX(), starty, objectSprite.getY() + height) {
			@Override
			protected void onModifierFinished(IEntity pItem) {
				super.onModifierFinished(pItem);
				if (moveSprite != null) {
					moveSprite.registerEntityModifier(moveBack);
					moveSprite = null;
				}
			}
		};
		moveSprite.registerEntityModifier(moveForward);

		if (destroyAttacked) {
			if (!updateProperty) {
				if (destination.equals("card1"))
					card1FieldId = -1;
				else if (destination.equals("card2"))
					card2FieldId = -1;
				else if (destination.equals("card3"))
					card3FieldId = -1;
				else if (destination.equals("card4"))
					card4FieldId = -1;
				else if (destination.equals("card1p2"))
					card1FieldId = -1;
				else if (destination.equals("card2p2"))
					card2FieldId = -1;
				else if (destination.equals("card3p2"))
					card3FieldId = -1;
				else if (destination.equals("card4p2"))
					card4FieldId = -1;
			}
			final EngineLock engineLock = gameScreen.getEngine()
					.getEngineLock();
			engineLock.lock();
			IEntityModifier destroyAnimation = new AlphaModifier(1, 1, 0);
			objectSprite.registerEntityModifier(destroyAnimation);
   			gameScreen.mMainScene.detachChild(objectSprite);
			gameScreen.mMainScene.unregisterTouchArea(objectSprite);
			objectSprite = null;
			objectMap.remove(destination);
			engineLock.unlock();
		}
		if (destroyAttacker) {
			if (updateProperty) {
				if (attackerDestination.equals("card1"))
					card1FieldId = -1;
				else if (attackerDestination.equals("card2"))
					card2FieldId = -1;
				else if (attackerDestination.equals("card3"))
					card3FieldId = -1;
				else if (attackerDestination.equals("card4"))
					card4FieldId = -1;
				if (attackerDestination.equals("card1p2"))
					card1FieldId = -1;
				else if (attackerDestination.equals("card2p2"))
					card2FieldId = -1;
				else if (attackerDestination.equals("card3p2"))
					card3FieldId = -1;
				else if (attackerDestination.equals("card4p2"))
					card4FieldId = -1;
			}
			final EngineLock engineLock = gameScreen.getEngine()
					.getEngineLock();
			engineLock.lock();
			gameScreen.mMainScene.detachChild(selectedCard);
			gameScreen.mMainScene.unregisterTouchArea(selectedCard);

			selectedCard = null;
			objectMap.remove(attackerDestination);
			engineLock.unlock();
		}
		if (updateProperty) {
			selectedCardIdEnemy = -1;
			gameScreen.updateProperty(destination, selectedObject + "/"
					+ selectedObjectIdEnemy + "/" + pos);

		}
		selectedCardId = -1;
		selectedFromField = false;
		if (selectedCard != null) {
			selectedCard.setScale(1, 1);
			selectedCard = null;
		}
		Log.d("FUNCTION", "20E");
	}

	public void attackCharacter(final int id, boolean updateProperty) {
		final CardSprite attacker;
		String position = "";
		if (updateProperty) {

			if (id == card1FieldId){
				if (!gameScreen.secondPlayer){
					position = "card1";
				} else {
					position = "card1p2";
				}
			}
			if (id == card2FieldId){
				if (!gameScreen.secondPlayer){
					position = "card2";
				} else {
					position = "card2p2";
				}
			}
			if (id == card3FieldId){
				if (!gameScreen.secondPlayer){
					position = "card3";
				} else {
					position = "card3p2";
				}
			}
			if (id == card4FieldId){
				if (!gameScreen.secondPlayer){
					position = "card4";
				} else {
					position = "card4p2";
				}
			}
			

			if (idsAttacked.contains(id)) {
				gameScreen.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Utils.showToastAlert(gameScreen,
								"this card can't attack right now");
					}
				});
				return;
			}
			
			idsAttacked.add(id);

			if (gameScreen.secondPlayer) {
				gameScreen.gc.setHealth(
						gameScreen.gc.healthp1
								- Decks.getAttack(id, gameScreen.secondPlayer),
						false);
			} else {
				gameScreen.gc.setHealth(
						gameScreen.gc.healthp2
								- Decks.getAttack(id, gameScreen.secondPlayer),
						true);
			}
			JSONObject object = new JSONObject();
			try {
				object.put("attackCharacter", id);
				gameScreen.theClient.sendChat(object.toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			if (id == card1FieldIdEnemy){
				if (gameScreen.secondPlayer){
					position = "card1";
				} else {
					position = "card1p2";
				}
			}
			if (id == card2FieldIdEnemy){
				if (gameScreen.secondPlayer){
					position = "card2";
				} else {
					position = "card2p2";
				}
			}
			if (id == card3FieldIdEnemy){
				if (gameScreen.secondPlayer){
					position = "card3";
				} else {
					position = "card3p2";
				}
			}
			if (id == card4FieldIdEnemy){
				if (gameScreen.secondPlayer){
					position = "card4";
				} else {
					position = "card4p2";
				}
			}
			if (gameScreen.secondPlayer) {
				gameScreen.gc
						.setHealth(
								gameScreen.gc.healthp2
										- Decks.getAttack(id,
												!gameScreen.secondPlayer), true);
			} else {
				gameScreen.gc
						.setHealth(
								gameScreen.gc.healthp1
										- Decks.getAttack(id,
												!gameScreen.secondPlayer),
								false);
			}
		}
		if (objectMap.get(position) != null) {
			attacker = objectMap.get(position);
		} else {
			return;
		}
		float yDest = 0;
		if (position.charAt(position.length() - 2) == 'p') {
			yDest = GameActivity.CAMERA_HEIGHT - CardsController.heightCard;
		} else {
			yDest = 0;
		}
		gameScreen.cc.checkHighlight();
		
		final MoveModifier moveBack = new MoveModifier(1, 0, attacker.getX(),
				yDest, attacker.getY());
		
		attacker.registerEntityModifier(new MoveModifier(0.25f, attacker.getX(), 0,
				attacker.getY(), yDest){
			@Override
			protected void onModifierFinished(IEntity pItem) {
				super.onModifierFinished(pItem);
				if (attacker != null)
					attacker.registerEntityModifier(moveBack);

			}
		});
	}

}
