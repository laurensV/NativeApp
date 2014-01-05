package uva.verspeek.hearthstone.activities;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


import org.andengine.engine.Engine;
import org.andengine.engine.Engine.EngineLock;
import org.andengine.engine.camera.SmoothCamera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.RepeatingSpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.PinchZoomDetector;
import org.andengine.input.touch.detector.PinchZoomDetector.IPinchZoomDetectorListener;
import org.andengine.input.touch.detector.ScrollDetector;
import org.andengine.input.touch.detector.ScrollDetector.IScrollDetectorListener;
import org.andengine.input.touch.detector.SurfaceScrollDetector;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.source.AssetBitmapTextureAtlasSource;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.HorizontalAlign;
import org.json.JSONObject;

import uva.verspeek.hearthstone.controllers.CardsController;
import uva.verspeek.hearthstone.controllers.EventHandler;
import uva.verspeek.hearthstone.controllers.GameController;
import uva.verspeek.hearthstone.controllers.ZoomScrollController;
import uva.verspeek.hearthstone.instances.CardSprite;
import uva.verspeek.hearthstone.instances.User;
import uva.verspeek.hearthstone.models.Constants;
import uva.verspeek.hearthstone.models.Decks;
import uva.verspeek.hearthstone.tools.Utils;
import uva.verspeek.hearthstone.tools.createSprites;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.util.Log;

import com.shephertz.app42.gaming.multiplayer.client.WarpClient;


public class GameActivity extends SimpleBaseGameActivity implements
		IOnSceneTouchListener {

	public static int CAMERA_WIDTH = 480;
	public static int CAMERA_HEIGHT = 800;

	public Scene mMainScene;

	public BitmapTextureAtlas mBitmapTextureAtlas1;
	public BitmapTextureAtlas mBitmapTextureAtlas2;
	public BitmapTextureAtlas mBitmapTextureAtlas3;
	public BitmapTextureAtlas mBitmapTextureAtlas4;

	public TiledTextureRegion mPlayerTiledTextureRegion1;
	public TiledTextureRegion mPlayerTiledTextureRegion2;
	public TiledTextureRegion mPlayerTiledTextureRegion3;
	public TiledTextureRegion mPlayerTiledTextureRegion4;

	public SmoothCamera mSmoothCamera;

	public Font mFont, mFont2;

	public RepeatingSpriteBackground mGrassBackground;

	public WarpClient theClient;
	public EventHandler eventHandler = new EventHandler(this);

	public HashMap<String, User> userMap = new HashMap<String, User>();

	public Map<Integer, BitmapTextureAtlas> textures = new HashMap<Integer, BitmapTextureAtlas>();

	public String roomId = "";

	public boolean secondPlayer = false;
	public boolean initialize = false;

	public boolean zooming = false;
	
	/* class Objects */
	public CardsController cc;
	public GameController gc;
	public ZoomScrollController zsc;
	public createSprites createSprites;

	@Override
	public EngineOptions onCreateEngineOptions() {
		cc = new CardsController(this);
		createSprites = new createSprites(this);
		gc = new GameController(this);
		zsc = new ZoomScrollController(this);

		Log.d("FUNCTION", "1START");
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
		mSmoothCamera = new SmoothCamera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT,
				Constants.maxVelocityX, Constants.maxVelocityY,
				Constants.maxZoomFactorChange);
		mSmoothCamera.setBounds(0f, 0f, CAMERA_WIDTH, CAMERA_HEIGHT);
		mSmoothCamera.setBoundsEnabled(true);

		return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED,
				new FillResolutionPolicy(), this.mSmoothCamera);
	}

	@Override
	protected void onCreateResources() {
		Log.d("FUNCTION", "2START");

		Decks.initCards();

		/* Load all the textures this game needs. */
		this.mGrassBackground = new RepeatingSpriteBackground(CAMERA_WIDTH,
				CAMERA_HEIGHT, this.getTextureManager(),
				AssetBitmapTextureAtlasSource.create(this.getAssets(),
						"background_sand.png"),
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

		CardsController.widthCard = (int) ((CAMERA_WIDTH - 25) / 4f);
		CardsController.heightCard = (int) (CardsController.widthCard * Constants.ratio);

		this.mFont = FontFactory.create(this.getFontManager(),
				this.getTextureManager(), 256, 256,
				Typeface.create(Typeface.DEFAULT, Typeface.BOLD),
				CardsController.widthCard / 3, true, Color.WHITE);
		this.mFont.load();

		this.mFont2 = FontFactory.create(this.getFontManager(),
				this.getTextureManager(), 256, 256,
				Typeface.create(Typeface.DEFAULT, Typeface.BOLD),
				CardsController.widthCard / 6, true, Color.WHITE);
		this.mFont2.load();

		cc.usedCards = new boolean[Decks.cardsp1.length];
		Arrays.fill(cc.usedCards, false);
		cc.usedCards[0] = true;

		cc.card1Id = cc.pickCardId();
		cc.card2Id = cc.pickCardId();
		cc.card3Id = cc.pickCardId();
		cc.card4Id = cc.pickCardId();

		this.mBitmapTextureAtlas1.load();
		this.mBitmapTextureAtlas2.load();
		this.mBitmapTextureAtlas3.load();
		this.mBitmapTextureAtlas4.load();

		Intent intent = getIntent();
		roomId = intent.getStringExtra("roomId");
		if (intent.getStringExtra("secondPlayer").equals("true")) {
			secondPlayer = true;
			gc.manap1++;
			gc.manap1Max++;
		}
		init(roomId);

	}

	@Override
	protected Scene onCreateScene() {
		Log.d("FUNCTION", "9S");
		/* Create Scene */
		this.mMainScene = new Scene();
		this.mMainScene.setBackground(mGrassBackground);
		this.mMainScene.setOnSceneTouchListener(this);

		Log.d("FUNCTION", "9E");
		initObjects();

		return this.mMainScene;
	}

	public void initObjects() {
		Log.d("FUNCTION", "10S");
		if (!initialize) {
			int id = 0;
			if (!secondPlayer) {
				id = cc.card1Id;
				// adding placeholders for cards on field
				float y = (CAMERA_HEIGHT / 2)
						+ ((CAMERA_HEIGHT / 4 - 35) - CardsController.heightCard)
						/ 2;
				float padding = (CAMERA_WIDTH - (CardsController.widthCard * 4)) / 5.0f;
				CardsController.placeholder1 = createSprites
						.newCardSpriteCardPlaceholder(padding, y);
				CardsController.placeholder1.setSize(CardsController.widthCard,
						CardsController.heightCard);
				this.mMainScene.attachChild(CardsController.placeholder1);
				CardsController.placeholder2 = createSprites
						.newCardSpriteCardPlaceholder(padding * 2
								+ CardsController.widthCard, y);
				CardsController.placeholder2.setSize(CardsController.widthCard,
						CardsController.heightCard);
				this.mMainScene.attachChild(CardsController.placeholder2);
				CardsController.placeholder3 = createSprites
						.newCardSpriteCardPlaceholder(padding * 3
								+ CardsController.widthCard * 2, y);
				CardsController.placeholder3.setSize(CardsController.widthCard,
						CardsController.heightCard);
				this.mMainScene.attachChild(CardsController.placeholder3);
				CardsController.placeholder4 = createSprites
						.newCardSpriteCardPlaceholder(padding * 4
								+ CardsController.widthCard * 3, y);
				CardsController.placeholder4.setSize(CardsController.widthCard,
						CardsController.heightCard);
				this.mMainScene.attachChild(CardsController.placeholder4);

			}
			// Adding card here
			cc.card1 = uva.verspeek.hearthstone.tools.createSprites.newCardSprite(id,
					CAMERA_WIDTH / 2 - (CardsController.widthCard + 5) * 2,
					CAMERA_HEIGHT - CardsController.heightCard - 70);
			cc.card1.setSize(CardsController.widthCard,
					CardsController.heightCard);
			if (!secondPlayer)
				this.mMainScene.registerTouchArea(cc.card1);
			else {
				cc.card1.ChangeAttack("");
				cc.card1.ChangeHealth("");
				cc.card1.ChangeMana("");
			}
			this.mMainScene.attachChild(cc.card1);
			if (!secondPlayer)
				id = cc.card2Id;
			cc.card2 = uva.verspeek.hearthstone.tools.createSprites.newCardSprite(id,
					CAMERA_WIDTH / 2 - (CardsController.widthCard + 5),
					CAMERA_HEIGHT - CardsController.heightCard - 70);
			cc.card2.setSize(CardsController.widthCard,
					CardsController.heightCard);
			if (!secondPlayer)
				this.mMainScene.registerTouchArea(cc.card2);
			else {
				cc.card2.ChangeAttack("");
				cc.card2.ChangeHealth("");
				cc.card2.ChangeMana("");
			}
			this.mMainScene.attachChild(cc.card2);
			if (!secondPlayer)
				id = cc.card3Id;
			cc.card3 = uva.verspeek.hearthstone.tools.createSprites.newCardSprite(id,
					CAMERA_WIDTH / 2, CAMERA_HEIGHT
							- CardsController.heightCard - 70);
			cc.card3.setSize(CardsController.widthCard,
					CardsController.heightCard);
			if (!secondPlayer)
				this.mMainScene.registerTouchArea(cc.card3);
			else {
				cc.card3.ChangeAttack("");
				cc.card3.ChangeHealth("");
				cc.card3.ChangeMana("");
			}
			this.mMainScene.attachChild(cc.card3);
			if (!secondPlayer)
				id = cc.card4Id;
			cc.card4 = uva.verspeek.hearthstone.tools.createSprites.newCardSprite(id,
					CAMERA_WIDTH / 2 + (CardsController.widthCard + 5),
					CAMERA_HEIGHT - CardsController.heightCard - 70);
			cc.card4.setSize(CardsController.widthCard,
					CardsController.heightCard);
			if (!secondPlayer)
				this.mMainScene.registerTouchArea(cc.card4);
			else {
				cc.card4.ChangeAttack("");
				cc.card4.ChangeHealth("");
				cc.card4.ChangeMana("");
			}
			this.mMainScene.attachChild(cc.card4);

			gc.healthTextp1 = new Text(60, CAMERA_HEIGHT - 30, this.mFont2,
					"health: " + gc.healthp1, new TextOptions(
							HorizontalAlign.LEFT),
					this.getVertexBufferObjectManager());
			this.mMainScene.attachChild(gc.healthTextp1);

			gc.manaTextp1 = new Text(gc.healthTextp1.getWidth()
					+ gc.healthTextp1.getX() + 20, CAMERA_HEIGHT - 30,
					this.mFont2, "mana: " + gc.manap1, new TextOptions(
							HorizontalAlign.LEFT),
					this.getVertexBufferObjectManager());
			this.mMainScene.attachChild(gc.manaTextp1);

			// Adding card here for player 2
			id = 0;
			if (secondPlayer) {
				id = cc.card1Id;
				float y = (CAMERA_HEIGHT / 2)
						- ((CAMERA_HEIGHT / 4 - 35) - CardsController.heightCard)
						/ 2 - CardsController.heightCard;
				float padding = (CAMERA_WIDTH - (CardsController.widthCard * 4)) / 5.0f;
				CardsController.placeholder1 = createSprites
						.newCardSpriteCardPlaceholder(padding, y);
				CardsController.placeholder1.setSize(CardsController.widthCard,
						CardsController.heightCard);
				this.mMainScene.attachChild(CardsController.placeholder1);
				CardsController.placeholder2 = createSprites
						.newCardSpriteCardPlaceholder(padding * 2
								+ CardsController.widthCard, y);
				CardsController.placeholder2.setSize(CardsController.widthCard,
						CardsController.heightCard);
				this.mMainScene.attachChild(CardsController.placeholder2);
				CardsController.placeholder3 = createSprites
						.newCardSpriteCardPlaceholder(padding * 3
								+ CardsController.widthCard * 2, y);
				CardsController.placeholder3.setSize(CardsController.widthCard,
						CardsController.heightCard);
				this.mMainScene.attachChild(CardsController.placeholder3);
				CardsController.placeholder4 = createSprites
						.newCardSpriteCardPlaceholder(padding * 4
								+ CardsController.widthCard * 3, y);
				CardsController.placeholder4.setSize(CardsController.widthCard,
						CardsController.heightCard);
				this.mMainScene.attachChild(CardsController.placeholder4);
			}

			cc.card1p2 = uva.verspeek.hearthstone.tools.createSprites.newCardSprite(
					id, CAMERA_WIDTH / 2 - (CardsController.widthCard + 5) * 2,
					0 + 70);
			cc.card1p2.setSize(CardsController.widthCard,
					CardsController.heightCard);

			if (secondPlayer)
				this.mMainScene.registerTouchArea(cc.card1p2);
			else {
				cc.card1p2.ChangeAttack("");
				cc.card1p2.ChangeHealth("");
				cc.card1p2.ChangeMana("");
			}
			this.mMainScene.attachChild(cc.card1p2);
			if (secondPlayer)
				id = cc.card2Id;
			cc.card2p2 = uva.verspeek.hearthstone.tools.createSprites.newCardSprite(
					id, CAMERA_WIDTH / 2 - (CardsController.widthCard + 5),
					0 + 70);
			cc.card2p2.setSize(CardsController.widthCard,
					CardsController.heightCard);
			if (secondPlayer)
				this.mMainScene.registerTouchArea(cc.card2p2);
			else {
				cc.card2p2.ChangeAttack("");
				cc.card2p2.ChangeHealth("");
				cc.card2p2.ChangeMana("");
			}
			this.mMainScene.attachChild(cc.card2p2);
			if (secondPlayer)
				id = cc.card3Id;
			cc.card3p2 = uva.verspeek.hearthstone.tools.createSprites.newCardSprite(
					id, CAMERA_WIDTH / 2, 0 + 70);
			cc.card3p2.setSize(CardsController.widthCard,
					CardsController.heightCard);
			if (secondPlayer)
				this.mMainScene.registerTouchArea(cc.card3p2);
			else {
				cc.card3p2.ChangeAttack("");
				cc.card3p2.ChangeHealth("");
				cc.card3p2.ChangeMana("");
			}
			this.mMainScene.attachChild(cc.card3p2);
			if (secondPlayer)
				id = cc.card4Id;
			cc.card4p2 = uva.verspeek.hearthstone.tools.createSprites.newCardSprite(
					id, CAMERA_WIDTH / 2 + (CardsController.widthCard + 5),
					0 + 70);
			cc.card4p2.setSize(CardsController.widthCard,
					CardsController.heightCard);
			if (secondPlayer)
				this.mMainScene.registerTouchArea(cc.card4p2);
			else {
				cc.card4p2.ChangeAttack("");
				cc.card4p2.ChangeHealth("");
				cc.card4p2.ChangeMana("");
			}
			this.mMainScene.attachChild(cc.card4p2);

			gc.healthTextp2 = new Text(60, 10, this.mFont2, "health: "
					+ gc.healthp2, new TextOptions(HorizontalAlign.LEFT),
					this.getVertexBufferObjectManager());
			this.mMainScene.attachChild(gc.healthTextp2);

			gc.manaTextp2 = new Text(gc.healthTextp2.getWidth()
					+ gc.healthTextp2.getX() + 20, 10, this.mFont2, "mana: "
					+ gc.manap2, new TextOptions(HorizontalAlign.LEFT),
					this.getVertexBufferObjectManager());
			this.mMainScene.attachChild(gc.manaTextp2);

			createSprites.endTurnSprite();

			initialize = true;
		}
		Log.d("FUNCTION", "10E");
	}

	public void init(String roomId) {
		Log.d("FUNCTION", "11S");
		if (theClient != null) {
			try {
				theClient = WarpClient.getInstance();
				theClient.addRoomRequestListener(eventHandler);
				theClient.addNotificationListener(eventHandler);
				Log.d(this.getClass().toString(), "Room Id is: " + roomId);
				theClient.subscribeRoom(roomId);
				theClient.getLiveRoomInfo(roomId);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Log.d("FUNCTION", "11E");
	}

	public void sendUpdateEvent(float xCord, float yCord) {
		Log.d("FUNCTION", "14S");
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
		Log.d("FUNCTION", "14E");
	}

	public void updateProperty(String position, String objectType) {
		Log.d("FUNCTION", "15S");
		HashMap<String, Object> table = new HashMap<String, Object>();
		table.put(position, objectType);
		theClient.updateRoomProperties(roomId, table, null);
		Log.d("FUNCTION", "15E");
	}

	public void handleLeave(String name) {
		Log.d("FUNCTION", "16S");
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
		Log.d("FUNCTION", "16E");
	}

	public Engine getEngine() {
		return this.mEngine;
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		Log.d("FUNCTION", "17S");
		zsc.mPinchZoomDetector.onTouchEvent(pSceneTouchEvent);
		if (zsc.mPinchZoomDetector.isZooming()) {
			zsc.mScrollDetector.setEnabled(false);
			zooming = true;
		} else {
			if (pSceneTouchEvent.isActionDown()) {
				zsc.mScrollDetector.setEnabled(true);
				zooming = false;
			}
			zsc.mScrollDetector.onTouchEvent(pSceneTouchEvent);
			if (pSceneTouchEvent.isActionUp() && gc.myTurn && !zooming) {
				float x = pSceneTouchEvent.getX();
				float y = pSceneTouchEvent.getY();
				cc.checkForCardMove(x, y);
				sendUpdateEvent(x, y);
				gc.updateMove(false, Utils.userName, x, y);
			}
		}

		Log.d("FUNCTION", "17E");
		return false;
	}

	public void removeSprite(CardSprite sprite) {
		final EngineLock engineLock = this.mEngine.getEngineLock();
		engineLock.lock();
		this.mMainScene.detachChild(sprite);
		this.mMainScene.unregisterTouchArea(sprite);
		engineLock.unlock();
	}

	@Override
	public void onBackPressed() {
		Log.d("FUNCTION", "23S");
		super.onBackPressed();
		if (theClient != null) {
			handleLeave(Utils.userName);
			theClient.leaveRoom(roomId);
			theClient.unsubscribeRoom(roomId);
			theClient.removeRoomRequestListener(eventHandler);
			theClient.removeNotificationListener(eventHandler);
		}
		clearResources();
		Log.d("FUNCTION", "23E");
	}

	@Override
	protected void onStop() {
		Log.d("FUNCTION", "24S");
		super.onStop();
		Log.d("FUNCTION", "24E");
	}

	@Override
	protected void onDestroy() {
		Log.d("FUNCTION", "24S");
		super.onDestroy();
		if (theClient != null) {
			handleLeave(Utils.userName);
			theClient.leaveRoom(roomId);
			theClient.unsubscribeRoom(roomId);
			theClient.removeRoomRequestListener(eventHandler);
			theClient.removeNotificationListener(eventHandler);
		}
		clearResources();
		Log.d("FUNCTION", "24E");
	}

	public void clearResources() {
		Log.d("FUNCTION", "25S");
		if (mBitmapTextureAtlas1 != null)
			this.mBitmapTextureAtlas1.unload();
		if (mBitmapTextureAtlas2 != null)
			this.mBitmapTextureAtlas2.unload();
		if (mBitmapTextureAtlas3 != null)
			this.mBitmapTextureAtlas3.unload();
		if (mBitmapTextureAtlas4 != null)
			this.mBitmapTextureAtlas4.unload();
		if (mFont != null)
			this.mFont.unload();
		if (mFont2 != null)
			this.mFont2.unload();

		Iterator<Map.Entry<Integer, BitmapTextureAtlas>> entries = textures
				.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry<Integer, BitmapTextureAtlas> entry = entries.next();
			if (entry.getValue() != null)
				entry.getValue().unload();
		}
		if (mMainScene != null) {
			this.mMainScene.dispose();
			mMainScene = null;
		}
		System.gc();
		Log.d("FUNCTION", "25E");
	}

}