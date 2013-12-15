package com.appwarp.multiplayer.tutorial;

import java.util.HashMap;
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

	private BitmapTextureAtlas fruitBitmapTextureAtlas1;
	private BitmapTextureAtlas fruitBitmapTextureAtlas2;
	private BitmapTextureAtlas fruitBitmapTextureAtlas3;
	private BitmapTextureAtlas fruitBitmapTextureAtlas4;

	private TiledTextureRegion mPlayerTiledTextureRegion1;
	private TiledTextureRegion mPlayerTiledTextureRegion2;
	private TiledTextureRegion mPlayerTiledTextureRegion3;
	private TiledTextureRegion mPlayerTiledTextureRegion4;

	private TiledTextureRegion mFruitTiledTextureRegion1;
	private TiledTextureRegion mFruitTiledTextureRegion2;
	private TiledTextureRegion mFruitTiledTextureRegion3;
	private TiledTextureRegion mFruitTiledTextureRegion4;

	private RepeatingSpriteBackground mGrassBackground;

	private WarpClient theClient;
	private EventHandler eventHandler = new EventHandler(this);
	private Random ramdom = new Random();

	private HashMap<String, User> userMap = new HashMap<String, User>();

	private HashMap<String, Sprite> objectMap = new HashMap<String, Sprite>();

	private String roomId = "";

	private Sprite card1, card2, card3, card4, selectedFruit;
	private Sprite card1p2, card2p2, card3p2, card4p2;

	private int selectedFruitId = -1;

	private Sprite card1Field, card2Field, card3Field, card4Field;
	private Sprite card1p2Field, card2p2Field, card3p2Field, card4p2Field;

	private int selectedFruitIdEnemy = -1;

	private boolean secondPlayer = false;
	private boolean initialize = false;
	private boolean selectedFromField = false;

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

		this.fruitBitmapTextureAtlas1 = new BitmapTextureAtlas(
				this.getTextureManager(), 128, 128);
		this.fruitBitmapTextureAtlas2 = new BitmapTextureAtlas(
				this.getTextureManager(), 128, 128);
		this.fruitBitmapTextureAtlas3 = new BitmapTextureAtlas(
				this.getTextureManager(), 128, 128);
		this.fruitBitmapTextureAtlas4 = new BitmapTextureAtlas(
				this.getTextureManager(), 128, 128);

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

		this.mFruitTiledTextureRegion1 = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(this.fruitBitmapTextureAtlas1, this,
						"card" + Constants.card1Id + ".png", 0, 0, 1, 1);
		this.mFruitTiledTextureRegion2 = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(this.fruitBitmapTextureAtlas2, this,
						"card" + Constants.card2Id + ".png", 0, 0, 1, 1);
		this.mFruitTiledTextureRegion3 = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(this.fruitBitmapTextureAtlas3, this,
						"card" + Constants.card3Id + ".png", 0, 0, 1, 1);
		this.mFruitTiledTextureRegion4 = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(this.fruitBitmapTextureAtlas4, this,
						"card" + Constants.card4Id + ".png", 0, 0, 1, 1);

		this.mBitmapTextureAtlas1.load();
		this.mBitmapTextureAtlas2.load();
		this.mBitmapTextureAtlas3.load();
		this.mBitmapTextureAtlas4.load();

		this.fruitBitmapTextureAtlas1.load();
		this.fruitBitmapTextureAtlas2.load();
		this.fruitBitmapTextureAtlas3.load();
		this.fruitBitmapTextureAtlas4.load();

		Intent intent = getIntent();
		roomId = intent.getStringExtra("roomId");
		init(roomId);
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
			// Adding fruit here
			card1 = new Sprite(CAMERA_WIDTH / 2 - 50 * 2, CAMERA_HEIGHT - 100,
					mFruitTiledTextureRegion1,
					this.getVertexBufferObjectManager()) {
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
						float pTouchAreaLocalX, float pTouchAreaLocalY) {
					selectedFruitId = Constants.card1Id;
					selectedFromField = false;
					if (selectedFruit != null) {
						selectedFruit.setSize(50, 50);
					}
					this.setSize(65, 65);
					selectedFruit = this;
					selectedFruitIdEnemy = -1;
					return super.onAreaTouched(pSceneTouchEvent,
							pTouchAreaLocalX, pTouchAreaLocalY);
				}
			};
			card1.setSize(50, 50);
			if (!secondPlayer)
				this.mMainScene.registerTouchArea(card1);
			this.mMainScene.attachChild(card1);
			card2 = new Sprite(CAMERA_WIDTH / 2 - 50, CAMERA_HEIGHT - 100,
					mFruitTiledTextureRegion2,
					this.getVertexBufferObjectManager()) {
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
						float pTouchAreaLocalX, float pTouchAreaLocalY) {
					selectedFruitId = Constants.card2Id;
					selectedFromField = false;
					if (selectedFruit != null) {
						selectedFruit.setSize(50, 50);
					}
					this.setSize(65, 65);
					selectedFruit = this;
					selectedFruitIdEnemy = -1;
					return super.onAreaTouched(pSceneTouchEvent,
							pTouchAreaLocalX, pTouchAreaLocalY);
				}
			};
			card2.setSize(50, 50);
			if (!secondPlayer)
				this.mMainScene.registerTouchArea(card2);
			this.mMainScene.attachChild(card2);

			card3 = new Sprite(CAMERA_WIDTH / 2, CAMERA_HEIGHT - 100,
					mFruitTiledTextureRegion3,
					this.getVertexBufferObjectManager()) {
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
						float pTouchAreaLocalX, float pTouchAreaLocalY) {
					selectedFruitId = Constants.card3Id;
					selectedFromField = false;
					if (selectedFruit != null) {
						selectedFruit.setSize(50, 50);
					}
					this.setSize(65, 65);
					selectedFruit = this;
					selectedFruitIdEnemy = -1;
					return super.onAreaTouched(pSceneTouchEvent,
							pTouchAreaLocalX, pTouchAreaLocalY);
				}
			};
			card3.setSize(50, 50);
			if (!secondPlayer)
				this.mMainScene.registerTouchArea(card3);
			this.mMainScene.attachChild(card3);
			card4 = new Sprite(CAMERA_WIDTH / 2 + 50, CAMERA_HEIGHT - 100,
					mFruitTiledTextureRegion4,
					this.getVertexBufferObjectManager()) {
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
						float pTouchAreaLocalX, float pTouchAreaLocalY) {
					selectedFruitId = Constants.card4Id;
					selectedFromField = false;
					if (selectedFruit != null) {
						selectedFruit.setSize(50, 50);
					}
					this.setSize(65, 65);
					selectedFruit = this;
					selectedFruitIdEnemy = -1;
					return super.onAreaTouched(pSceneTouchEvent,
							pTouchAreaLocalX, pTouchAreaLocalY);
				}
			};
			card4.setSize(50, 50);
			if (!secondPlayer)
				this.mMainScene.registerTouchArea(card4);
			this.mMainScene.attachChild(card4);
			// Adding fruit here for player 2

			card1p2 = new Sprite(CAMERA_WIDTH / 2 - 50 * 2, 0 + 100,
					mFruitTiledTextureRegion1,
					this.getVertexBufferObjectManager()) {
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
						float pTouchAreaLocalX, float pTouchAreaLocalY) {
					selectedFruitId = Constants.card1Id;
					selectedFromField = false;
					if (selectedFruit != null) {
						selectedFruit.setSize(50, 50);
					}
					this.setSize(65, 65);
					selectedFruit = this;
					selectedFruitIdEnemy = -1;
					return super.onAreaTouched(pSceneTouchEvent,
							pTouchAreaLocalX, pTouchAreaLocalY);
				}
			};
			card1p2.setSize(50, 50);
			if (secondPlayer)
				this.mMainScene.registerTouchArea(card1p2);
			this.mMainScene.attachChild(card1p2);
			card2p2 = new Sprite(CAMERA_WIDTH / 2 - 50, 0 + 100,
					mFruitTiledTextureRegion2,
					this.getVertexBufferObjectManager()) {
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
						float pTouchAreaLocalX, float pTouchAreaLocalY) {
					selectedFruitId = Constants.card2Id;
					selectedFromField = false;
					if (selectedFruit != null) {
						selectedFruit.setSize(50, 50);
					}
					this.setSize(65, 65);
					selectedFruit = this;
					selectedFruitIdEnemy = -1;
					return super.onAreaTouched(pSceneTouchEvent,
							pTouchAreaLocalX, pTouchAreaLocalY);
				}
			};
			card2p2.setSize(50, 50);
			if (secondPlayer)
				this.mMainScene.registerTouchArea(card2p2);
			this.mMainScene.attachChild(card2p2);

			card3p2 = new Sprite(CAMERA_WIDTH / 2, 0 + 100,
					mFruitTiledTextureRegion3,
					this.getVertexBufferObjectManager()) {
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
						float pTouchAreaLocalX, float pTouchAreaLocalY) {
					selectedFruitId = Constants.card3Id;
					selectedFromField = false;
					if (selectedFruit != null) {
						selectedFruit.setSize(50, 50);
					}
					this.setSize(65, 65);
					selectedFruit = this;
					selectedFruitIdEnemy = -1;
					return super.onAreaTouched(pSceneTouchEvent,
							pTouchAreaLocalX, pTouchAreaLocalY);
				}
			};
			card3p2.setSize(50, 50);
			if (secondPlayer)
				this.mMainScene.registerTouchArea(card3p2);
			this.mMainScene.attachChild(card3p2);
			card4p2 = new Sprite(CAMERA_WIDTH / 2 + 50, 0 + 100,
					mFruitTiledTextureRegion4,
					this.getVertexBufferObjectManager()) {
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
						float pTouchAreaLocalX, float pTouchAreaLocalY) {
					selectedFruitId = Constants.card4Id;
					selectedFromField = false;
					if (selectedFruit != null) {
						selectedFruit.setSize(50, 50);
					}
					this.setSize(65, 65);
					selectedFruit = this;
					selectedFruitIdEnemy = -1;
					return super.onAreaTouched(pSceneTouchEvent,
							pTouchAreaLocalX, pTouchAreaLocalY);
				}
			};
			card4p2.setSize(50, 50);
			if (secondPlayer)
				this.mMainScene.registerTouchArea(card4p2);
			this.mMainScene.attachChild(card4p2);

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
			}
			initObjects();
			Log.d("INIT", "INIT");
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
		// theClient.getLiveRoomInfo(roomId);
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
		if (pSceneTouchEvent.isActionUp()) {
			float x = pSceneTouchEvent.getX();
			float y = pSceneTouchEvent.getY();
			checkForFruitMove(x, y);
			sendUpdateEvent(x, y);
			updateMove(false, Utils.userName, x, y);
		}
		return false;
	}

	private void checkForFruitMove(float x, float y) {
		Log.d("selectedFromField/selectedFruitIdEnemy", "" + selectedFromField
				+ "/" + selectedFruitIdEnemy);
		if (selectedFruitIdEnemy != -1 && selectedFromField) {
			if (!secondPlayer) {
				if (x > 0 && x < CAMERA_WIDTH * (1f / 4f)
						&& y < CAMERA_HEIGHT / 2f
						&& y > (CAMERA_HEIGHT * (1f / 4f))) {
					placeObject(selectedFruitId, selectedFruitIdEnemy,
							"card1p2", null, true);
				} else if (x > CAMERA_WIDTH * (1f / 4f)
						&& x < CAMERA_WIDTH * (2f / 4f)
						&& y < CAMERA_HEIGHT / 2f
						&& y > (CAMERA_HEIGHT * (1f / 4f))) {
					placeObject(selectedFruitId, selectedFruitIdEnemy,
							"card2p2", null, true);
				} else if (x > CAMERA_WIDTH * (2f / 4f)
						&& x < CAMERA_WIDTH * (3f / 4f)
						&& y < CAMERA_HEIGHT / 2f
						&& y > (CAMERA_HEIGHT * (1f / 4f))) {
					placeObject(selectedFruitId, selectedFruitIdEnemy,
							"card3p2", null, true);
				} else if (x > CAMERA_WIDTH * (3f / 4f) && x < CAMERA_WIDTH
						&& y < CAMERA_HEIGHT / 2f
						&& y > (CAMERA_HEIGHT * (1f / 4f))) {
					placeObject(selectedFruitId, selectedFruitIdEnemy,
							"card4p2", null, true);
				}
			} else {
				if (x > 0 && x < CAMERA_WIDTH * (1f / 4f)
						&& y > CAMERA_HEIGHT / 2f
						&& y < CAMERA_HEIGHT - (CAMERA_HEIGHT * (1f / 4f))) {
					placeObject(selectedFruitId, selectedFruitIdEnemy, "card1",
							null, true);
				} else if (x > CAMERA_WIDTH * (1f / 4f)
						&& x < CAMERA_WIDTH * (2f / 4f)
						&& y > CAMERA_HEIGHT / 2f
						&& y < CAMERA_HEIGHT - (CAMERA_HEIGHT * (1f / 4f))) {
					placeObject(selectedFruitId, selectedFruitIdEnemy, "card2",
							null, true);
				} else if (x > CAMERA_WIDTH * (2f / 4f)
						&& x < CAMERA_WIDTH * (3f / 4f)
						&& y > CAMERA_HEIGHT / 2f
						&& y < CAMERA_HEIGHT - (CAMERA_HEIGHT * (1f / 4f))) {
					placeObject(selectedFruitId, selectedFruitIdEnemy, "card3",
							null, true);
				} else if (x > CAMERA_WIDTH * (3f / 4f) && x < CAMERA_WIDTH
						&& y > CAMERA_HEIGHT / 2f
						&& y < CAMERA_HEIGHT - (CAMERA_HEIGHT * (1f / 4f))) {
					placeObject(selectedFruitId, selectedFruitIdEnemy, "card4",
							null, true);
				}
			}
		} else if (selectedFruitId != -1 && !selectedFromField) {
			if (!secondPlayer) {
				if (x > 0 && x < CAMERA_WIDTH * (1f / 4f)
						&& y > CAMERA_HEIGHT / 2f
						&& y < CAMERA_HEIGHT - (CAMERA_HEIGHT * (1f / 4f))) {
					placeObject(selectedFruitId, -1, "card1", null, true);
				} else if (x > CAMERA_WIDTH * (1f / 4f)
						&& x < CAMERA_WIDTH * (2f / 4f)
						&& y > CAMERA_HEIGHT / 2f
						&& y < CAMERA_HEIGHT - (CAMERA_HEIGHT * (1f / 4f))) {
					placeObject(selectedFruitId, -1, "card2", null, true);
				} else if (x > CAMERA_WIDTH * (2f / 4f)
						&& x < CAMERA_WIDTH * (3f / 4f)
						&& y > CAMERA_HEIGHT / 2f
						&& y < CAMERA_HEIGHT - (CAMERA_HEIGHT * (1f / 4f))) {
					placeObject(selectedFruitId, -1, "card3", null, true);
				} else if (x > CAMERA_WIDTH * (3f / 4f) && x < CAMERA_WIDTH
						&& y > CAMERA_HEIGHT / 2f
						&& y < CAMERA_HEIGHT - (CAMERA_HEIGHT * (1f / 4f))) {
					placeObject(selectedFruitId, -1, "card4", null, true);
				}
			} else {
				if (x > 0 && x < CAMERA_WIDTH * (1f / 4f)
						&& y < CAMERA_HEIGHT / 2f
						&& y > (CAMERA_HEIGHT * (1f / 4f))) {
					placeObject(selectedFruitId, -1, "card1p2", null, true);
				} else if (x > CAMERA_WIDTH * (1f / 4f)
						&& x < CAMERA_WIDTH * (2f / 4f)
						&& y < CAMERA_HEIGHT / 2f
						&& y > (CAMERA_HEIGHT * (1f / 4f))) {
					placeObject(selectedFruitId, -1, "card2p2", null, true);
				} else if (x > CAMERA_WIDTH * (2f / 4f)
						&& x < CAMERA_WIDTH * (3f / 4f)
						&& y < CAMERA_HEIGHT / 2f
						&& y > (CAMERA_HEIGHT * (1f / 4f))) {
					placeObject(selectedFruitId, -1, "card3p2", null, true);
				} else if (x > CAMERA_WIDTH * (3f / 4f) && x < CAMERA_WIDTH
						&& y < CAMERA_HEIGHT / 2f
						&& y > (CAMERA_HEIGHT * (1f / 4f))) {
					placeObject(selectedFruitId, -1, "card4p2", null, true);
				}
			}
		}
	}

	public synchronized void playObject(final int selectedObject,
			final int selectedObjectIdEnemy, final String destination,
			final String userName, boolean updateProperty) {
		Sprite objectSprite = null;

		if (selectedObjectIdEnemy == Constants.card1Id) {
			if (secondPlayer) {
				if (updateProperty) {
					objectSprite = card1Field;
				} else {
					objectSprite = card1p2Field;
				}
			} else {
				if (updateProperty) {
					objectSprite = card1p2Field;
				} else {
					objectSprite = card1Field;
				}
			}
		}
		final EngineLock engineLock = this.mEngine.getEngineLock();
		engineLock.lock();
		this.mMainScene.detachChild(objectSprite);
		this.mMainScene.unregisterTouchArea(objectSprite);
		objectSprite = null;
		engineLock.unlock();
		if (updateProperty) {
			selectedFruitIdEnemy = -1;
			updateProperty("selectedObjectIdEnemy", selectedObjectIdEnemy + "");

		}

	}

	public synchronized void placeObject(final int selectedObjectId,
			final int selectedObjectIdEnemy, final String destination,
			final String userName, boolean updateProperty) {
		Log.d("selectedObjectId/selectedFruitIdEnemy", "" + selectedObjectId
				+ "/" + selectedObjectIdEnemy);

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

		if (objectMap.get(destination) != null) {
			// Sprite objectSprite = objectMap.get(destination);
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
		Sprite sprite = null;
		if (selectedObjectId == Constants.card1Id) {
			if (secondPlayer) {
				// handle move of other user
				if (!updateProperty) {
					card1Field = new Sprite(CAMERA_WIDTH / 2 + 50,
							CAMERA_HEIGHT - 100, mFruitTiledTextureRegion1,
							this.getVertexBufferObjectManager()) {
						@Override
						public boolean onAreaTouched(
								TouchEvent pSceneTouchEvent,
								float pTouchAreaLocalX, float pTouchAreaLocalY) {
							selectedFruitIdEnemy = Constants.card1Id;
							return super.onAreaTouched(pSceneTouchEvent,
									pTouchAreaLocalX, pTouchAreaLocalY);
						}
					};
					sprite = card1Field;
					selectedFruit = card1;

					// handle move of current user
				} else {
					card1p2Field = new Sprite(CAMERA_WIDTH / 2 + 50, 0 + 100,
							mFruitTiledTextureRegion1,
							this.getVertexBufferObjectManager()) {
						@Override
						public boolean onAreaTouched(
								TouchEvent pSceneTouchEvent,
								float pTouchAreaLocalX, float pTouchAreaLocalY) {
							selectedFruitId = Constants.card1Id;
							selectedFromField = true;
							if (selectedFruit != null) {
								selectedFruit.setSize(50, 50);
							}
							this.setSize(65, 65);
							selectedFruit = this;
							selectedFruitIdEnemy = -1;
							return super.onAreaTouched(pSceneTouchEvent,
									pTouchAreaLocalX, pTouchAreaLocalY);
						}
					};
					sprite = card1p2Field;
					selectedFruit = card1p2;
				}
			} else {
				if (!updateProperty) {
					card1p2Field = new Sprite(CAMERA_WIDTH / 2 + 50, 0 + 100,
							mFruitTiledTextureRegion1,
							this.getVertexBufferObjectManager()) {
						@Override
						public boolean onAreaTouched(
								TouchEvent pSceneTouchEvent,
								float pTouchAreaLocalX, float pTouchAreaLocalY) {
							selectedFruitIdEnemy = Constants.card1Id;
							return super.onAreaTouched(pSceneTouchEvent,
									pTouchAreaLocalX, pTouchAreaLocalY);
						}
					};
					sprite = card1p2Field;
					selectedFruit = card1p2;
				} else {
					card1Field = new Sprite(CAMERA_WIDTH / 2 + 50,
							CAMERA_HEIGHT - 100, mFruitTiledTextureRegion1,
							this.getVertexBufferObjectManager()) {
						@Override
						public boolean onAreaTouched(
								TouchEvent pSceneTouchEvent,
								float pTouchAreaLocalX, float pTouchAreaLocalY) {
							selectedFruitId = Constants.card1Id;
							selectedFromField = true;
							if (selectedFruit != null) {
								selectedFruit.setSize(50, 50);
							}
							this.setSize(65, 65);
							selectedFruit = this;
							selectedFruitIdEnemy = -1;
							return super.onAreaTouched(pSceneTouchEvent,
									pTouchAreaLocalX, pTouchAreaLocalY);
						}
					};
					sprite = card1Field;
					selectedFruit = card1;
				}
			}
		} else if (selectedObjectId == Constants.card2Id) {
			if (secondPlayer) {
				if (!updateProperty) {
					sprite = new Sprite(CAMERA_WIDTH / 2 + 50,
							CAMERA_HEIGHT - 100, mFruitTiledTextureRegion2,
							this.getVertexBufferObjectManager());
					selectedFruit = card2;
				} else {
					sprite = new Sprite(CAMERA_WIDTH / 2 + 50, 0 + 100,
							mFruitTiledTextureRegion2,
							this.getVertexBufferObjectManager());
					selectedFruit = card2p2;
				}
			} else {
				if (!updateProperty) {
					sprite = new Sprite(CAMERA_WIDTH / 2 + 50, 0 + 100,
							mFruitTiledTextureRegion2,
							this.getVertexBufferObjectManager());
					selectedFruit = card2p2;
				} else {
					sprite = new Sprite(CAMERA_WIDTH / 2 + 50,
							CAMERA_HEIGHT - 100, mFruitTiledTextureRegion2,
							this.getVertexBufferObjectManager());
					selectedFruit = card2;
				}
			}
		} else if (selectedObjectId == Constants.card3Id) {
			if (secondPlayer) {
				if (!updateProperty) {
					sprite = new Sprite(CAMERA_WIDTH / 2 + 50,
							CAMERA_HEIGHT - 100, mFruitTiledTextureRegion3,
							this.getVertexBufferObjectManager());
					selectedFruit = card3;
				} else {
					sprite = new Sprite(CAMERA_WIDTH / 2 + 50, 0 + 100,
							mFruitTiledTextureRegion3,
							this.getVertexBufferObjectManager());
					selectedFruit = card3p2;
				}
			} else {
				if (!updateProperty) {
					sprite = new Sprite(CAMERA_WIDTH / 2 + 50, 0 + 100,
							mFruitTiledTextureRegion3,
							this.getVertexBufferObjectManager());
					selectedFruit = card3p2;
				} else {
					sprite = new Sprite(CAMERA_WIDTH / 2 + 50,
							CAMERA_HEIGHT - 100, mFruitTiledTextureRegion3,
							this.getVertexBufferObjectManager());
					selectedFruit = card3;
				}
			}
		} else if (selectedObjectId == Constants.card4Id) {
			if (secondPlayer) {
				if (!updateProperty) {
					sprite = new Sprite(CAMERA_WIDTH / 2 + 50,
							CAMERA_HEIGHT - 100, mFruitTiledTextureRegion4,
							this.getVertexBufferObjectManager());
					selectedFruit = card4;
				} else {
					sprite = new Sprite(CAMERA_WIDTH / 2 + 50, 0 + 100,
							mFruitTiledTextureRegion4,
							this.getVertexBufferObjectManager());
					selectedFruit = card4p2;
				}
			} else {
				if (!updateProperty) {
					sprite = new Sprite(CAMERA_WIDTH / 2 + 50, 0 + 100,
							mFruitTiledTextureRegion4,
							this.getVertexBufferObjectManager());
					selectedFruit = card4p2;
				} else {
					sprite = new Sprite(CAMERA_WIDTH / 2 + 50,
							CAMERA_HEIGHT - 100, mFruitTiledTextureRegion4,
							this.getVertexBufferObjectManager());
					selectedFruit = card4;
				}
			}
		} else {
			return;
		}
		sprite.setSize(50, 50);

		// remove old sprite
		final EngineLock engineLock = this.mEngine.getEngineLock();
		engineLock.lock();
		this.mMainScene.detachChild(selectedFruit);
		this.mMainScene.unregisterTouchArea(selectedFruit);
		selectedFruit = null;
		selectedFruitId = -1;
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
							+ " has changed " + destination + " object");
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
		super.onBackPressed();
	}

	public void clearResources() {
		this.mBitmapTextureAtlas1.unload();
		this.mBitmapTextureAtlas2.unload();
		this.mBitmapTextureAtlas3.unload();
		this.mBitmapTextureAtlas4.unload();
		this.fruitBitmapTextureAtlas1.unload();
		this.fruitBitmapTextureAtlas2.unload();
		this.fruitBitmapTextureAtlas3.unload();
		this.fruitBitmapTextureAtlas4.unload();
		this.mMainScene.dispose();
	}
}