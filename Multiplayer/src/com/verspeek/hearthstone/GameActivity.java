package com.verspeek.hearthstone;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.andengine.engine.Engine.EngineLock;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.RepeatingSpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.source.AssetBitmapTextureAtlasSource;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.util.DisplayMetrics;
import android.util.Log;

import com.shephertz.app42.gaming.multiplayer.client.WarpClient;

public class GameActivity extends SimpleBaseGameActivity implements
		IOnSceneTouchListener {

	public static int CAMERA_WIDTH = 480;
	public static int CAMERA_HEIGHT = 800;

	private Camera mCamera;
	private Scene mMainScene;

	private BitmapTextureAtlas mBitmapTextureAtlas1;
	private BitmapTextureAtlas mBitmapTextureAtlas2;
	private BitmapTextureAtlas mBitmapTextureAtlas3;
	private BitmapTextureAtlas mBitmapTextureAtlas4;

	private TiledTextureRegion mPlayerTiledTextureRegion1;
	private TiledTextureRegion mPlayerTiledTextureRegion2;
	private TiledTextureRegion mPlayerTiledTextureRegion3;
	private TiledTextureRegion mPlayerTiledTextureRegion4;

	private RepeatingSpriteBackground mGrassBackground;

	private WarpClient theClient;
	private EventHandler eventHandler = new EventHandler(this);
	private Random ramdom = new Random();

	private HashMap<String, User> userMap = new HashMap<String, User>();

	private Map<Integer, BitmapTextureAtlas> textures = new HashMap<Integer, BitmapTextureAtlas>();

	private HashMap<String, Sprite> objectMap = new HashMap<String, Sprite>();

	private String roomId = "";

	private Sprite card1, card2, card3, card4, selectedCard;
	private Sprite card1p2, card2p2, card3p2, card4p2;

	private int selectedCardId = -1;

	private Sprite card1Field, card2Field, card3Field, card4Field;
	private Sprite card1p2Field, card2p2Field, card3p2Field, card4p2Field;

	private int textureCount = 0;

	private int selectedCardIdEnemy = -1;

	private int card1Id, card2Id, card3Id, card4Id;
	private boolean[] usedCards;
	public boolean myTurn = true;

	private boolean secondPlayer = false;
	private boolean initialize = false;
	private boolean selectedFromField = false;

	/* variable for counting two successive up-down events */
	private int clickCount = 0;
	/* variable for storing the time of first click */
	private long startTime;

	@Override
	public EngineOptions onCreateEngineOptions() {
		try {
			theClient = WarpClient.getInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		userMap.clear();
		final DisplayMetrics displayMetrics = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		CAMERA_WIDTH = displayMetrics.widthPixels;
		CAMERA_HEIGHT = displayMetrics.heightPixels;
		this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED,
				new FillResolutionPolicy(), this.mCamera);
	}

	@Override
	protected void onCreateResources() {

		Cards.initCards();

		/* Load all the textures this game needs. */
		this.mGrassBackground = new RepeatingSpriteBackground(CAMERA_WIDTH,
				CAMERA_HEIGHT, this.getTextureManager(),
				AssetBitmapTextureAtlasSource.create(this.getAssets(),
						"background_grass.png"),
				this.getVertexBufferObjectManager());
		this.mBitmapTextureAtlas1 = new BitmapTextureAtlas(
				this.getTextureManager(), 32, 32);
		this.mBitmapTextureAtlas2 = new BitmapTextureAtlas(
				this.getTextureManager(), 32, 32);
		this.mBitmapTextureAtlas3 = new BitmapTextureAtlas(
				this.getTextureManager(), 32, 32);
		this.mBitmapTextureAtlas4 = new BitmapTextureAtlas(
				this.getTextureManager(), 32, 32);

		this.mPlayerTiledTextureRegion1 = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(this.mBitmapTextureAtlas1, this,
						"monster1.png", 0, 0, 1, 1);
		this.mPlayerTiledTextureRegion2 = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(this.mBitmapTextureAtlas2, this,
						"monster2.png", 0, 0, 1, 1);
		this.mPlayerTiledTextureRegion3 = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(this.mBitmapTextureAtlas3, this,
						"monster3.png", 0, 0, 1, 1);
		this.mPlayerTiledTextureRegion4 = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(this.mBitmapTextureAtlas4, this,
						"monster4.png", 0, 0, 1, 1);

		usedCards = new boolean[Cards.cardsp1.length + 1];
		Arrays.fill(usedCards, false);

		card1Id = pickCardId();
		card2Id = pickCardId();
		card3Id = pickCardId();
		card4Id = pickCardId();

		this.mBitmapTextureAtlas1.load();
		this.mBitmapTextureAtlas2.load();
		this.mBitmapTextureAtlas3.load();
		this.mBitmapTextureAtlas4.load();

		Intent intent = getIntent();
		roomId = intent.getStringExtra("roomId");
		init(roomId);
	}

	private void endTurnSprite() {
		BitmapTextureAtlas cardBitmapTextureAtlas1;
		TiledTextureRegion mCardTiledTextureRegion1;

		cardBitmapTextureAtlas1 = new BitmapTextureAtlas(
				this.getTextureManager(), 102, 45);

		mCardTiledTextureRegion1 = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(cardBitmapTextureAtlas1, this,
						"endTurn.png", 0, 0, 1, 1);

		cardBitmapTextureAtlas1.load();
		this.textures.put(textureCount, cardBitmapTextureAtlas1);
		textureCount++;

		Sprite sprite = new Sprite(0, (float) (CAMERA_HEIGHT / 2.0 - 50),
				mCardTiledTextureRegion1, this.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
					JSONObject object = new JSONObject();
					try {
						object.put("turn", "over");
					} catch (JSONException e) {
						e.printStackTrace();
					}
					theClient.sendChat(object.toString());
					myTurn = false;
				}
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX,
						pTouchAreaLocalY);

			}
		};
		sprite.setSize(102, 45);
		this.mMainScene.registerTouchArea(sprite);
		this.mMainScene.attachChild(sprite);
	}

	private Sprite newSprite(final int id, float x, float y) {
		BitmapTextureAtlas cardBitmapTextureAtlas1;
		TiledTextureRegion mCardTiledTextureRegion1;

		cardBitmapTextureAtlas1 = new BitmapTextureAtlas(
				this.getTextureManager(), 128, 128);

		mCardTiledTextureRegion1 = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(cardBitmapTextureAtlas1, this,
						Cards.getName(id, secondPlayer) + ".png", 0, 0, 1, 1);

		cardBitmapTextureAtlas1.load();
		this.textures.put(textureCount, cardBitmapTextureAtlas1);
		textureCount++;

		return new Sprite(x, y, mCardTiledTextureRegion1,
				this.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
					clickCount++;

					if (clickCount == 1) {
						startTime = System.currentTimeMillis();
					} else if (clickCount == 2) {
						if (System.currentTimeMillis() - startTime <= 400) {
							if (id != 0) {
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										Utils.showToastAlert(
												GameActivity.this,
												Cards.getName(id, secondPlayer)
														+ " - "
														+ "attack: "
														+ Cards.getAttack(id,
																secondPlayer)
														+ ". health: "
														+ Cards.getHealth(id,
																secondPlayer));
									}
								});
							}
							clickCount = 0;
						}
					} else {
						clickCount = 0;
						startTime = System.currentTimeMillis();
					}

					selectedCardId = id;
					selectedFromField = false;
					if (selectedCard != null) {
						selectedCard.setSize(50, 50);
					}
					this.setSize(65, 65);
					selectedCard = this;
					selectedCardIdEnemy = -1;
				}
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX,
						pTouchAreaLocalY);

			}
		};
	}

	private Sprite newSpriteOtherField(final int id, float x, float y) {
		BitmapTextureAtlas cardBitmapTextureAtlas1;
		TiledTextureRegion mCardTiledTextureRegion1;

		cardBitmapTextureAtlas1 = new BitmapTextureAtlas(
				this.getTextureManager(), 128, 128);

		mCardTiledTextureRegion1 = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(cardBitmapTextureAtlas1, this,
						Cards.getName(id, !secondPlayer) + ".png", 0, 0, 1, 1);

		cardBitmapTextureAtlas1.load();
		this.textures.put(textureCount, cardBitmapTextureAtlas1);
		textureCount++;

		cardBitmapTextureAtlas1.load();
		return new Sprite(x, y, mCardTiledTextureRegion1,
				this.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {

					clickCount++;

					if (clickCount == 1) {
						startTime = System.currentTimeMillis();
					} else if (clickCount == 2) {
						if (System.currentTimeMillis() - startTime <= 400) {
							selectedCardIdEnemy = id;
							clickCount = 0;
						}
					} else {
						clickCount = 0;
						startTime = System.currentTimeMillis();
					}

					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Utils.showToastAlert(
									GameActivity.this,
									Cards.getName(id, !secondPlayer)
											+ " - "
											+ "attack: "
											+ Cards.getAttack(id, !secondPlayer)
											+ ". health: "
											+ Cards.getHealth(id, !secondPlayer));
						}
					});

				}
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX,
						pTouchAreaLocalY);
			}
		};

	}

	private Sprite newSpriteOwnField(final int id, float x, float y) {
		BitmapTextureAtlas cardBitmapTextureAtlas1;
		TiledTextureRegion mCardTiledTextureRegion1;

		cardBitmapTextureAtlas1 = new BitmapTextureAtlas(
				this.getTextureManager(), 128, 128);

		mCardTiledTextureRegion1 = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(cardBitmapTextureAtlas1, this,
						Cards.getName(id, secondPlayer) + ".png", 0, 0, 1, 1);

		cardBitmapTextureAtlas1.load();
		this.textures.put(textureCount, cardBitmapTextureAtlas1);
		textureCount++;

		return new Sprite(x, y, mCardTiledTextureRegion1,
				this.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Utils.showToastAlert(
									GameActivity.this,
									Cards.getName(id, secondPlayer) + " - "
											+ "attack: "
											+ Cards.getAttack(id, secondPlayer)
											+ ". health: "
											+ Cards.getHealth(id, secondPlayer));
						}
					});

					selectedCardId = id;
					selectedFromField = true;
					if (selectedCard != null) {
						selectedCard.setSize(50, 50);
					}
					this.setSize(65, 65);
					selectedCard = this;
					selectedCardIdEnemy = -1;
				}
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX,
						pTouchAreaLocalY);
			}
		};
	}

	@Override
	protected Scene onCreateScene() {
		/* Create Scene */
		this.mMainScene = new Scene();
		this.mMainScene.setBackground(mGrassBackground);
		this.mMainScene.setOnSceneTouchListener(this);
		return this.mMainScene;
	}

	private void initObjects() {
		if (!initialize) {
			int id = 0;
			if (!secondPlayer)
				id = card1Id;
			// Adding card here
			card1 = newSprite(id, CAMERA_WIDTH / 2 - 50 * 2,
					CAMERA_HEIGHT - 100);
			card1.setSize(50, 50);
			if (!secondPlayer)
				this.mMainScene.registerTouchArea(card1);
			this.mMainScene.attachChild(card1);
			if (!secondPlayer)
				id = card2Id;
			card2 = newSprite(id, CAMERA_WIDTH / 2 - 50, CAMERA_HEIGHT - 100);
			card2.setSize(50, 50);
			if (!secondPlayer)
				this.mMainScene.registerTouchArea(card2);
			this.mMainScene.attachChild(card2);
			if (!secondPlayer)
				id = card3Id;
			card3 = newSprite(id, CAMERA_WIDTH / 2, CAMERA_HEIGHT - 100);
			card3.setSize(50, 50);
			if (!secondPlayer)
				this.mMainScene.registerTouchArea(card3);
			this.mMainScene.attachChild(card3);
			if (!secondPlayer)
				id = card4Id;
			card4 = newSprite(id, CAMERA_WIDTH / 2 + 50, CAMERA_HEIGHT - 100);
			card4.setSize(50, 50);
			if (!secondPlayer)
				this.mMainScene.registerTouchArea(card4);
			this.mMainScene.attachChild(card4);

			id = 0;

			// Adding card here for player 2
			if (secondPlayer)
				id = card1Id;
			card1p2 = newSprite(id, CAMERA_WIDTH / 2 - 50 * 2, 0 + 100);
			card1p2.setSize(50, 50);
			if (secondPlayer)
				this.mMainScene.registerTouchArea(card1p2);
			this.mMainScene.attachChild(card1p2);
			if (secondPlayer)
				id = card2Id;
			card2p2 = newSprite(id, CAMERA_WIDTH / 2 - 50, 0 + 100);
			card2p2.setSize(50, 50);
			if (secondPlayer)
				this.mMainScene.registerTouchArea(card2p2);
			this.mMainScene.attachChild(card2p2);
			if (secondPlayer)
				id = card3Id;
			card3p2 = newSprite(id, CAMERA_WIDTH / 2, 0 + 100);
			card3p2.setSize(50, 50);
			if (secondPlayer)
				this.mMainScene.registerTouchArea(card3p2);
			this.mMainScene.attachChild(card3p2);
			if (secondPlayer)
				id = card4Id;
			card4p2 = newSprite(id, CAMERA_WIDTH / 2 + 50, 0 + 100);
			card4p2.setSize(50, 50);
			if (secondPlayer)
				this.mMainScene.registerTouchArea(card4p2);
			this.mMainScene.attachChild(card4p2);

			endTurnSprite();

			initialize = true;
		}
	}

	private void init(String roomId) {
		if (theClient != null) {
			theClient.addRoomRequestListener(eventHandler);
			theClient.addNotificationListener(eventHandler);
			Log.d(this.getClass().toString(), "Room Id is: " + roomId);
			theClient.subscribeRoom(roomId);
			theClient.getLiveRoomInfo(roomId);
		}

	}

	private int pickCardId() {
		int id;
		while (usedCards[id = (int) Math.floor(Math.random()
				* Cards.cardsp1.length)] == true
				|| id == 0)
			;
		usedCards[id] = true;
		return id;
	}

	public void addMorePlayer(boolean isMine, String userName,
			boolean secondPlayer) {
		// if already in room
		if (userMap.get(userName) != null) {
			return;
		}

		Log.d("userNameGame", userName);
		char index = userName.charAt(userName.length() - 1);
		TiledTextureRegion tiledTextureRegion = null;
		if (index == '1') {
			tiledTextureRegion = mPlayerTiledTextureRegion1;
		} else if (index == '2') {
			tiledTextureRegion = mPlayerTiledTextureRegion2;
		} else if (index == '3') {
			tiledTextureRegion = mPlayerTiledTextureRegion3;
		} else if (index == '4') {
			tiledTextureRegion = mPlayerTiledTextureRegion4;
		}
		final Sprite face = new Sprite(ramdom.nextInt(CAMERA_WIDTH),
				ramdom.nextInt(CAMERA_HEIGHT), tiledTextureRegion,
				this.getVertexBufferObjectManager());
		face.setScale(1.5f);
		this.mMainScene.attachChild(face);
		User user = new User(face.getX(), face.getY(), face);
		userMap.put(userName, user);
		if (isMine) {
			if (secondPlayer) {
				this.secondPlayer = true;
				this.myTurn = false;
			}
			initObjects();
			this.mMainScene.setOnSceneTouchListener(this);
		}
	}

	private void sendUpdateEvent(float xCord, float yCord) {
		try {
			JSONObject object = new JSONObject();
			float perX = Utils.getPercentFromValue(xCord, CAMERA_WIDTH);
			float perY = Utils.getPercentFromValue(yCord, CAMERA_HEIGHT);
			object.put("X", perX);
			object.put("Y", perY);
			theClient.sendChat(object.toString());
		} catch (Exception e) {
			Log.d("sendUpdateEvent", e.getMessage());
		}
	}

	private void updateProperty(String position, String objectType) {
		HashMap<String, Object> table = new HashMap<String, Object>();
		table.put(position, objectType);
		theClient.updateRoomProperties(roomId, table, null);
	}

	public void handleLeave(String name) {
		if (name.length() > 0 && userMap.get(name) != null) {
			Sprite sprite = userMap.get(name).getSprite();
			final EngineLock engineLock = this.mEngine.getEngineLock();
			engineLock.lock();
			this.mMainScene.detachChild(sprite);
			sprite.dispose();
			sprite = null;
			userMap.remove(name);
			engineLock.unlock();
		}
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		if (pSceneTouchEvent.isActionUp() && myTurn) {
			float x = pSceneTouchEvent.getX();
			float y = pSceneTouchEvent.getY();
			checkForCardMove(x, y);
			sendUpdateEvent(x, y);
			updateMove(false, Utils.userName, x, y);
		}
		return false;
	}

	private String getPosition(float x, float y) {
		String position = "";
		float height1 = CAMERA_HEIGHT / 2f;
		float height2 = CAMERA_HEIGHT * (1f / 4f);

		if (x > 0 && x < CAMERA_WIDTH * (1f / 4f) && y < height1 && y > height2) {
			position = "card1p2";
		} else if (x > CAMERA_WIDTH * (1f / 4f) && x < CAMERA_WIDTH * (2f / 4f)
				&& y < height1 && y > height2) {
			position = "card2p2";
		} else if (x > CAMERA_WIDTH * (2f / 4f) && x < CAMERA_WIDTH * (3f / 4f)
				&& y < height1 && y > height2) {
			position = "card3p2";
		} else if (x > CAMERA_WIDTH * (3f / 4f) && x < CAMERA_WIDTH
				&& y < height1 && y > height2) {
			position = "card4p2";
		}

		height2 = CAMERA_HEIGHT * (3f / 4f);
		if (x > 0 && x < CAMERA_WIDTH * (1f / 4f) && y < height2 && y > height1) {
			position = "card1";
		} else if (x > CAMERA_WIDTH * (1f / 4f) && x < CAMERA_WIDTH * (2f / 4f)
				&& y < height2 && y > height1) {
			position = "card2";
		} else if (x > CAMERA_WIDTH * (2f / 4f) && x < CAMERA_WIDTH * (3f / 4f)
				&& y < height2 && y > height1) {
			position = "card3";
		} else if (x > CAMERA_WIDTH * (3f / 4f) && x < CAMERA_WIDTH
				&& y < height2 && y > height1) {
			position = "card4";
		}

		return position;
	}

	private void checkForCardMove(float x, float y) {
		if (selectedCardIdEnemy != -1 && selectedFromField) {
			placeObject(selectedCardId, selectedCardIdEnemy, getPosition(x, y),
					null, true);
		} else if (selectedCardId != -1 && !selectedFromField) {
			placeObject(selectedCardId, -1, getPosition(x, y), null, true);
		}
	}

	public synchronized void playObject(final int selectedObject,
			final int selectedObjectIdEnemy, final String destination,
			final String userName, boolean updateProperty) {
		Sprite objectSprite = null;

		if (objectMap.get(destination) != null) {
			objectSprite = objectMap.get(destination);
		}
		boolean destroy = false;
		int health;

		// you attack opponent
		if (updateProperty) {
			health = Cards.getHealth(selectedObjectIdEnemy, !secondPlayer);
			Cards.setHealth(selectedObjectIdEnemy,
					health - Cards.getAttack(selectedObject, secondPlayer),
					!secondPlayer);
			if (Cards.getHealth(selectedObjectIdEnemy, !secondPlayer) <= 0) {
				destroy = true;
			}
			// you get attacked by opponent
		} else {
			health = Cards.getHealth(selectedObjectIdEnemy, secondPlayer);
			Cards.setHealth(selectedObjectIdEnemy,
					health - Cards.getAttack(selectedObject, !secondPlayer),
					secondPlayer);
			if (Cards.getHealth(selectedObjectIdEnemy, secondPlayer) <= 0) {
				destroy = true;
			}
		}
		if (destroy) {
			final EngineLock engineLock = this.mEngine.getEngineLock();
			engineLock.lock();
			this.mMainScene.detachChild(objectSprite);
			this.mMainScene.unregisterTouchArea(objectSprite);
			objectSprite = null;
			objectMap.remove(destination);
			engineLock.unlock();
		}
		if (updateProperty) {
			selectedCardIdEnemy = -1;
			updateProperty(destination, selectedObject + "/"
					+ selectedObjectIdEnemy + "/" + health);

		}
		selectedCardId = -1;
		selectedFromField = false;
		if (selectedCard != null) {
			selectedCard.setSize(50, 50);
			selectedCard = null;
		}

	}

	public synchronized void placeObject(final int selectedObjectId,
			final int selectedObjectIdEnemy, final String destination,
			final String userName, boolean updateProperty) {
		Log.d("selectedObjectId/selectedCardIdEnemy", "" + selectedObjectId
				+ "/" + selectedObjectIdEnemy + selectedFromField);

		if (selectedObjectIdEnemy != -1
				&& (!updateProperty || selectedFromField)) {
			playObject(selectedObjectId, selectedObjectIdEnemy, destination,
					userName, updateProperty);
			return;
		}
		float xDest = 0;
		float yDest = 0;
		if (destination.equals("card1")) {
			xDest = CAMERA_WIDTH * (1f / 9f);
			yDest = CAMERA_HEIGHT * (2f / 3f);
		} else if (destination.equals("card2")) {
			xDest = CAMERA_WIDTH * (3f / 9f);
			yDest = CAMERA_HEIGHT * (2f / 3f);
		} else if (destination.equals("card3")) {
			xDest = CAMERA_WIDTH * (5f / 9f);
			yDest = CAMERA_HEIGHT * (2f / 3f);
		} else if (destination.equals("card4")) {
			xDest = CAMERA_WIDTH * (7f / 9f);
			yDest = CAMERA_HEIGHT * (2f / 3f);
		} else if (destination.equals("card1p2")) {
			xDest = CAMERA_WIDTH * (1f / 9f);
			yDest = CAMERA_HEIGHT * (1f / 3f);
		} else if (destination.equals("card2p2")) {
			xDest = CAMERA_WIDTH * (3f / 9f);
			yDest = CAMERA_HEIGHT * (1f / 3f);
		} else if (destination.equals("card3p2")) {
			xDest = CAMERA_WIDTH * (5f / 9f);
			yDest = CAMERA_HEIGHT * (1f / 3f);
		} else if (destination.equals("card4p2")) {
			xDest = CAMERA_WIDTH * (7f / 9f);
			yDest = CAMERA_HEIGHT * (1f / 3f);
		} else {
			return;
		}

		if (destination.charAt(destination.length() - 2) == 'p') {
			if (!secondPlayer && updateProperty)
				return;
		} else {
			if (secondPlayer && updateProperty)
				return;
		}

		if (objectMap.get(destination) != null) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Utils.showToastAlert(GameActivity.this,
							"already a card at position " + destination);
				}
			});
			return;
		}

		// create new sprite with new ontouch options
		boolean p2 = false;
		Sprite sprite = null;
		if (secondPlayer) {
			if (!updateProperty) { // handle move of other user
				sprite = newSpriteOtherField(selectedObjectId,
						CAMERA_WIDTH / 2, CAMERA_HEIGHT - 100);
			} else { // handle move of current user
				sprite = newSpriteOwnField(selectedObjectId, CAMERA_WIDTH / 2,
						0 + 100);
				p2 = true;
			}
		} else {
			if (!updateProperty) {
				sprite = newSpriteOtherField(selectedObjectId,
						CAMERA_WIDTH / 2, 0 + 100);
				p2 = true;
			} else {
				sprite = newSpriteOwnField(selectedObjectId, CAMERA_WIDTH / 2,
						CAMERA_HEIGHT - 100);
			}
		}
		if (selectedObjectId == card1Id) {
			if (p2) {
				card1p2Field = sprite;
				selectedCard = card1p2;
			} else {
				card1Field = sprite;
				selectedCard = card1;
			}

		} else if (selectedObjectId == card2Id) {
			if (p2) {
				card2p2Field = sprite;
				selectedCard = card2p2;
			} else {
				card2Field = sprite;
				selectedCard = card2;
			}
		} else if (selectedObjectId == card3Id) {
			if (p2) {
				card3p2Field = sprite;
				selectedCard = card3p2;
			} else {
				card3Field = sprite;
				selectedCard = card3;
			}
		} else if (selectedObjectId == card4Id) {
			if (p2) {
				card4p2Field = sprite;
				selectedCard = card4p2;
			} else {
				card4Field = sprite;
				selectedCard = card4;
			}
		} else {
			return;
		}
		sprite.setSize(50, 50);

		// remove old sprite
		final EngineLock engineLock = this.mEngine.getEngineLock();
		engineLock.lock();
		this.mMainScene.detachChild(selectedCard);
		this.mMainScene.unregisterTouchArea(selectedCard);
		selectedCard = null;
		selectedCardId = -1;
		engineLock.unlock();

		objectMap.put(destination, sprite);
		this.mMainScene.attachChild(sprite);
		this.mMainScene.registerTouchArea(sprite);
		sprite.registerEntityModifier(new MoveModifier(1, sprite.getX(), xDest,
				sprite.getY(), yDest));
		if (updateProperty) {
			updateProperty(destination, selectedObjectId + "");
		}

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (userName != null) {
					Utils.showToastAlert(GameActivity.this, userName
							+ " has played a card at position " + destination);
				}
			}
		});
	}

	public void updateMove(boolean isRemote, String userName, float xPer,
			float yPer) {
		if (userMap.get(userName) != null) {
			if (isRemote) {
				xPer = Utils.getValueFromPercent(xPer, CAMERA_WIDTH);
				yPer = Utils.getValueFromPercent(yPer, CAMERA_HEIGHT);
			}
			Sprite sprite = userMap.get(userName).getSprite();
			float deltaX = sprite.getX() - xPer;
			float deltaY = sprite.getY() - yPer;
			float distance = (float) Math.sqrt((deltaX * deltaX)
					+ (deltaY * deltaY));
			float time = distance / Constants.MonsterSpeed;
			sprite.registerEntityModifier(new MoveModifier(time, sprite.getX(),
					xPer, sprite.getY(), yPer));
		}
	}

	@Override
	public void onBackPressed() {
		if (theClient != null) {
			handleLeave(Utils.userName);
			theClient.leaveRoom(roomId);
			theClient.unsubscribeRoom(roomId);
			theClient.removeRoomRequestListener(eventHandler);
			theClient.removeNotificationListener(eventHandler);
		}
		clearResources();
		super.onBackPressed();
	}

	protected void onStop() {
		if (theClient != null) {
			handleLeave(Utils.userName);
			theClient.leaveRoom(roomId);
			theClient.unsubscribeRoom(roomId);
			theClient.removeRoomRequestListener(eventHandler);
			theClient.removeNotificationListener(eventHandler);
		}
		clearResources();
		super.onStop();
	}

	protected void onDestroy() {
		if (theClient != null) {
			handleLeave(Utils.userName);
			theClient.leaveRoom(roomId);
			theClient.unsubscribeRoom(roomId);
			theClient.removeRoomRequestListener(eventHandler);
			theClient.removeNotificationListener(eventHandler);
		}
		clearResources();
		super.onDestroy();
	}

	public void clearResources() {
		if (mBitmapTextureAtlas1 != null)
			this.mBitmapTextureAtlas1.unload();
		if (mBitmapTextureAtlas2 != null)
			this.mBitmapTextureAtlas2.unload();
		if (mBitmapTextureAtlas3 != null)
			this.mBitmapTextureAtlas3.unload();
		if (mBitmapTextureAtlas4 != null)
			this.mBitmapTextureAtlas4.unload();
		for (Map.Entry<Integer, BitmapTextureAtlas> e : textures.entrySet()) {
			this.mEngine.getTextureManager().unloadTexture(e.getValue());
			e.getValue().unload();
		}
		if (mMainScene != null) {
			this.mMainScene.dispose();
			mMainScene = null;
		}
		System.gc();
	}
}