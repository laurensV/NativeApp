package com.verspeek.hearthstone;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.andengine.engine.Engine.EngineLock;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.SmoothCamera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.modifier.MoveModifier;
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
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.util.Log;

import com.shephertz.app42.gaming.multiplayer.client.WarpClient;

public class GameActivity extends SimpleBaseGameActivity implements
		IOnSceneTouchListener, IPinchZoomDetectorListener, IScrollDetectorListener{

	public static int CAMERA_WIDTH = 480;
	public static int CAMERA_HEIGHT = 800;
	
	public static int zindex = 1;

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
	
	SmoothCamera mSmoothCamera;

	private Font mFont, mFont2;

	private RepeatingSpriteBackground mGrassBackground;

	private WarpClient theClient;
	private EventHandler eventHandler = new EventHandler(this);

	private HashMap<String, User> userMap = new HashMap<String, User>();

	private Map<Integer, BitmapTextureAtlas> textures = new HashMap<Integer, BitmapTextureAtlas>();

	private HashMap<String, CardSprite> objectMap = new HashMap<String, CardSprite>();

	private String roomId = "";

	private CardSprite card1, card2, card3, card4, selectedCard;
	private CardSprite card1p2, card2p2, card3p2, card4p2;

	private int selectedCardId = -1;

	private CardSprite card1Field, card2Field, card3Field, card4Field;
	private CardSprite card1p2Field, card2p2Field, card3p2Field, card4p2Field;

	private int textureCount = 0;

	private int healthp1 = 100;
	private int healthp2 = 100;
	private int manap1 = 0;
	private int manap2 = 0;
	private int manap1Max = 0;
	private int manap2Max = 0;
	
	public static int widthCard, heightCard;

	private Text manaTextp1;
	private Text manaTextp2;
	private Text healthTextp1;
	private Text healthTextp2;

	private int selectedCardIdEnemy = -1;

	private int card1Id, card2Id, card3Id, card4Id;
	private boolean[] usedCards;
	public boolean myTurn = false;

	private boolean secondPlayer = false;
	private boolean initialize = false;
	private boolean selectedFromField = false;

	List<Integer> idsAttacked = new ArrayList<Integer>();

	/* variable for counting two successive up-down events */
	private int clickCount = 0;
	/* variable for storing the time of first click */
	private long startTime;
	private PinchZoomDetector mPinchZoomDetector;
	private float mInitialTouchZoomFactor;
	private SurfaceScrollDetector mScrollDetector;

	@Override
	public EngineOptions onCreateEngineOptions() {
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
		this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		Log.d("FUNCTION", "1END");
		mSmoothCamera = new SmoothCamera(0, 0, CAMERA_WIDTH,
                CAMERA_HEIGHT, 4000f,
                4000f, 1f);
mSmoothCamera.setBounds(0f, 0f, CAMERA_WIDTH,
                CAMERA_HEIGHT);
mSmoothCamera.setBoundsEnabled(true);
	    
		return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED,
				new FillResolutionPolicy(), this.mSmoothCamera);
	}

	@Override
	protected void onCreateResources() {
		Log.d("FUNCTION", "2START");

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
		
		widthCard = (int) ((CAMERA_WIDTH-25)/4f);
		heightCard = (int) (widthCard * Constants.ratio);
		
		this.mFont = FontFactory.create(this.getFontManager(),
				this.getTextureManager(), 256, 256,
				Typeface.create(Typeface.DEFAULT, Typeface.BOLD), widthCard/3, true,
				Color.WHITE);
		this.mFont.load();
		
		this.mFont2 = FontFactory.create(this.getFontManager(),
				this.getTextureManager(), 256, 256,
				Typeface.create(Typeface.DEFAULT, Typeface.BOLD), widthCard/6, true,
				Color.WHITE);
		this.mFont2.load();

		usedCards = new boolean[Cards.cardsp1.length];
		Arrays.fill(usedCards, false);
		usedCards[0] = true;

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
		if (intent.getStringExtra("secondPlayer").equals("true")) {
			secondPlayer = true;
			manap1++;
			manap1Max++;
		}
		init(roomId);

		Log.d("FUNCTION", "2END");
	}

	private void endTurnSprite() {
		Log.d("FUNCTION", "3START");
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

		int height = 0;
		if (!secondPlayer) {
			height = CAMERA_HEIGHT - 45;
		}

		Sprite sprite = new Sprite(CAMERA_WIDTH - 102, (float) height,
				mCardTiledTextureRegion1, this.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
					if (myTurn) {
						JSONObject object = new JSONObject();
						try {
							object.put("turn", "over");
							theClient.sendChat(object.toString());
						} catch (JSONException e) {
							e.printStackTrace();
						}

						endTurn();
					}
				}
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX,
						pTouchAreaLocalY);

			}
		};
		sprite.setSize(102, 45);
		this.mMainScene.registerTouchArea(sprite);
		this.mMainScene.attachChild(sprite);
		Log.d("FUNCTION", "3END");
	}

	public void startTurn() {
		Log.d("FUNCTION", "4START");
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Utils.showToastAlert(GameActivity.this, "Its your turn");
			}
		});

		myTurn = true;
		if (!secondPlayer) {
			manap1Max++;
			setMana(manap1Max, secondPlayer);
			if (card1Id == -1)
				drawCard("card1", true);
			else if (card2Id == -1)
				drawCard("card2", true);
			else if (card3Id == -1)
				drawCard("card3", true);
			else if (card4Id == -1)
				drawCard("card4", true);
		} else {
			manap2Max++;
			setMana(manap2Max, secondPlayer);
			if (card1Id == -1)
				drawCard("card1p2", true);
			else if (card2Id == -1)
				drawCard("card2p2", true);
			else if (card3Id == -1)
				drawCard("card3p2", true);
			else if (card4Id == -1)
				drawCard("card4p2", true);
		}
		Log.d("FUNCTION", "4END");
	}

	public void drawCard(String card, boolean updateProperty) {
		CardSprite sprite = null;
		Log.d("DRAWCARD", card + updateProperty);
		int id = -1, width = -1, height = -1;
		if (card.equals("card1")) {
			if (!updateProperty)
				id = 0;
			else {
				card1Id = pickCardId();
				id = card1Id;
			}
			width = CAMERA_WIDTH / 2 - (widthCard + 5) * 2;
			height = CAMERA_HEIGHT - heightCard - 70;
			card1 = newSprite(id, width, height);
			sprite = card1;
		} else if (card.equals("card2")) {
			if (!updateProperty)
				id = 0;
			else {
				card2Id = pickCardId();
				id = card2Id;
			}
			width = CAMERA_WIDTH / 2 - (widthCard + 5);
			height = CAMERA_HEIGHT - heightCard - 70;
			card2 = newSprite(id, width, height);
			sprite = card2;
		} else if (card.equals("card3")) {
			if (!updateProperty)
				id = 0;
			else {
				card3Id = pickCardId();
				id = card3Id;
			}
			width = CAMERA_WIDTH / 2;
			height = CAMERA_HEIGHT - heightCard - 70;
			card3 = newSprite(id, width, height);
			sprite = card3;
		} else if (card.equals("card4")) {
			if (!updateProperty)
				id = 0;
			else {
				card4Id = pickCardId();
				id = card4Id;
			}
			width = CAMERA_WIDTH / 2 + (widthCard + 5);
			height = CAMERA_HEIGHT - heightCard - 70;
			card4 = newSprite(id, width, height);
			sprite = card4;
		} else if (card.equals("card1p2")) {
			if (!updateProperty)
				id = 0;
			else {
				card1Id = pickCardId();
				id = card1Id;
			}
			width = CAMERA_WIDTH / 2 - (widthCard + 5) * 2;
			height = 0 + 70;
			card1p2 = newSprite(id, width, height);
			sprite = card1p2;
		} else if (card.equals("card2p2")) {
			if (!updateProperty)
				id = 0;
			else {
				card2Id = pickCardId();
				id = card2Id;
			}
			width = CAMERA_WIDTH / 2 - (widthCard + 5);
			height = 0 + 70;
			card2p2 = newSprite(id, width, height);
			sprite = card2p2;
		} else if (card.equals("card3p2")) {
			if (!updateProperty)
				id = 0;
			else {
				card3Id = pickCardId();
				id = card3Id;
			}
			width = CAMERA_WIDTH / 2;
			height = 0 + 70;
			card3p2 = newSprite(id, width, height);
			sprite = card3p2;
		} else if (card.equals("card4p2")) {
			if (!updateProperty)
				id = 0;
			else {
				card4Id = pickCardId();
				id = card4Id;
			}
			width = CAMERA_WIDTH / 2 + (widthCard + 5);
			height = 0 + 70;
			card4p2 = newSprite(id, width, height);
			sprite = card4p2;
		}
		if (id != -1) {
			sprite.setSize(widthCard, heightCard);
			if (updateProperty) {
				this.mMainScene.registerTouchArea(sprite);

				JSONObject object = new JSONObject();
				try {
					object.put("drawCard", card);
					theClient.sendChat(object.toString());
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				sprite.ChangeAttack("");
				sprite.ChangeHealth("");
				sprite.ChangeMana("");
			}
			this.mMainScene.attachChild(sprite);

		}
	}

	private void endTurn() {
		Log.d("FUNCTION", "5START");
		float x, y;
		x = 10;
		if (secondPlayer) {
			y = 10;
			manap1Max++;
			setMana(manap1Max, !secondPlayer);
		} else {
			y = CAMERA_HEIGHT - 40;
			manap2Max++;
			setMana(manap2Max, !secondPlayer);
		}
		sendUpdateEvent(x, y);
		updateMove(false, Utils.userName, x, y);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Utils.showToastAlert(GameActivity.this, "Opponent's turn");
			}
		});
		myTurn = false;
		idsAttacked.clear();
		Log.d("FUNCTION", "5END");
	}

	private CardSprite newSprite(final int id, float x, float y) {
		Log.d("FUNCTION", "6START");
		BitmapTextureAtlas cardBitmapTextureAtlas1;
		TiledTextureRegion mCardTiledTextureRegion1;

		cardBitmapTextureAtlas1 = new BitmapTextureAtlas(
				this.getTextureManager(), 350, 350);

		mCardTiledTextureRegion1 = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(cardBitmapTextureAtlas1, this,
						Cards.getName(id, secondPlayer) + ".png", 0, 0, 1, 1);

		cardBitmapTextureAtlas1.load();
		this.textures.put(textureCount, cardBitmapTextureAtlas1);
		textureCount++;

		CardSprite playButton = new CardSprite(mCardTiledTextureRegion1, mFont,
				this.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
					selectedCardId = id;
					selectedFromField = false;
					if (selectedCard != null) {
						selectedCard.setScale(1, 1);
					}
					zindex++;
					this.setScale(Constants.scale, Constants.scale);
					mMainScene.sortChildren();
					
					selectedCard = this;
					selectedCardIdEnemy = -1;
				}
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX,
						pTouchAreaLocalY);

			}
		};
		playButton.ChangeAttack("" + Cards.getAttack(id, secondPlayer));
		playButton.ChangeHealth("" + Cards.getHealth(id, secondPlayer));
		playButton.ChangeMana("" + Cards.getMana(id, secondPlayer));
		playButton.setPosition(x, y);
		Log.d("FUNCTION", "6END");
		return playButton;
	}

	private CardSprite newSpriteOtherField(final int id, float x, float y) {
		Log.d("FUNCTION", "7START");
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

		CardSprite playButton = new CardSprite(mCardTiledTextureRegion1, mFont,
				this.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
					selectedCardIdEnemy = id;
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
		playButton.ChangeAttack("" + Cards.getAttack(id, secondPlayer));
		playButton.ChangeHealth("" + Cards.getHealth(id, secondPlayer));
		playButton.ChangeMana("" + Cards.getMana(id, secondPlayer));
		playButton.setPosition(x, y);
		Log.d("FUNCTION", "7E");
		return playButton;

	}

	private CardSprite newSpriteOwnField(final int id, float x, float y) {
		Log.d("FUNCTION", "8S");
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

		CardSprite playButton = new CardSprite(mCardTiledTextureRegion1, mFont,
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
						selectedCard.setScale(1, 1);
					}
					zindex++;
					this.setScale(Constants.scale, Constants.scale);
					mMainScene.sortChildren();
					selectedCard = this;
					selectedCardIdEnemy = -1;
				}
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX,
						pTouchAreaLocalY);
			}
		};
		playButton.ChangeAttack("" + Cards.getAttack(id, secondPlayer));
		playButton.ChangeHealth("" + Cards.getHealth(id, secondPlayer));
		playButton.ChangeMana("" + Cards.getMana(id, secondPlayer));
		playButton.setPosition(x, y);
		Log.d("FUNCTION", "8E");
		return playButton;
	}

	@Override
	protected Scene onCreateScene() {
		Log.d("FUNCTION", "9S");
		/* Create Scene */
		this.mMainScene = new Scene();
		this.mMainScene.setBackground(mGrassBackground);
		this.mMainScene.setOnSceneTouchListener(this);
		
		/* Create and set the zoom detector to listen for 
		 * touch events using this activity's listener */
		mPinchZoomDetector = new PinchZoomDetector(this);
		mScrollDetector = new SurfaceScrollDetector(this);
		    
		// Enable the zoom detector
		mPinchZoomDetector.setEnabled(true);
		
		Log.d("FUNCTION", "9E");
		initObjects();
		
		
	    
		return this.mMainScene;
	}
	
	private void initObjects() {
		Log.d("FUNCTION", "10S");
		if (!initialize) {
			int id = 0;
			if (!secondPlayer)
				id = card1Id;
			// Adding card here
			card1 = newSprite(id, CAMERA_WIDTH / 2 - (widthCard + 5) * 2,
					CAMERA_HEIGHT - heightCard - 70);
			card1.setSize(widthCard, heightCard);
			if (!secondPlayer)
				this.mMainScene.registerTouchArea(card1);
			else {
				card1.ChangeAttack("");
				card1.ChangeHealth("");
				card1.ChangeMana("");
			}
			this.mMainScene.attachChild(card1);
			if (!secondPlayer)
				id = card2Id;
			card2 = newSprite(id, CAMERA_WIDTH / 2 - (widthCard + 5), CAMERA_HEIGHT - heightCard - 70);
			card2.setSize(widthCard, heightCard);
			if (!secondPlayer)
				this.mMainScene.registerTouchArea(card2);
			else {
				card2.ChangeAttack("");
				card2.ChangeHealth("");
				card2.ChangeMana("");
			}
			this.mMainScene.attachChild(card2);
			if (!secondPlayer)
				id = card3Id;
			card3 = newSprite(id, CAMERA_WIDTH / 2, CAMERA_HEIGHT - heightCard - 70);
			card3.setSize(widthCard, heightCard);
			if (!secondPlayer)
				this.mMainScene.registerTouchArea(card3);
			else {
				card3.ChangeAttack("");
				card3.ChangeHealth("");
				card3.ChangeMana("");
			}
			this.mMainScene.attachChild(card3);
			if (!secondPlayer)
				id = card4Id;
			card4 = newSprite(id, CAMERA_WIDTH / 2 + (widthCard + 5), CAMERA_HEIGHT - heightCard - 70);
			card4.setSize(widthCard, heightCard);
			if (!secondPlayer)
				this.mMainScene.registerTouchArea(card4);
			else {
				card4.ChangeAttack("");
				card4.ChangeHealth("");
				card4.ChangeMana("");
			}
			this.mMainScene.attachChild(card4);

			healthTextp1 = new Text(60, CAMERA_HEIGHT - 30, this.mFont2,
					"health: " + healthp1,
					new TextOptions(HorizontalAlign.LEFT),
					this.getVertexBufferObjectManager());
			this.mMainScene.attachChild(healthTextp1);

			manaTextp1 = new Text(170, CAMERA_HEIGHT - 30, this.mFont2, "mana: "
					+ manap1, new TextOptions(HorizontalAlign.LEFT),
					this.getVertexBufferObjectManager());
			this.mMainScene.attachChild(manaTextp1);

			// Adding card here for player 2
			id = 0;
			if (secondPlayer)
				id = card1Id;
			card1p2 = newSprite(id, CAMERA_WIDTH / 2 - (widthCard + 5) * 2, 0 + 70);
			card1p2.setSize(widthCard, heightCard);

			if (secondPlayer)
				this.mMainScene.registerTouchArea(card1p2);
			else {
				card1p2.ChangeAttack("");
				card1p2.ChangeHealth("");
				card1p2.ChangeMana("");
			}
			this.mMainScene.attachChild(card1p2);
			if (secondPlayer)
				id = card2Id;
			card2p2 = newSprite(id, CAMERA_WIDTH / 2 - (widthCard + 5), 0 + 70);
			card2p2.setSize(widthCard, heightCard);
			if (secondPlayer)
				this.mMainScene.registerTouchArea(card2p2);
			else {
				card2p2.ChangeAttack("");
				card2p2.ChangeHealth("");
				card2p2.ChangeMana("");
			}
			this.mMainScene.attachChild(card2p2);
			if (secondPlayer)
				id = card3Id;
			card3p2 = newSprite(id, CAMERA_WIDTH / 2, 0 + 70);
			card3p2.setSize(widthCard, heightCard);
			if (secondPlayer)
				this.mMainScene.registerTouchArea(card3p2);
			else {
				card3p2.ChangeAttack("");
				card3p2.ChangeHealth("");
				card3p2.ChangeMana("");
			}
			this.mMainScene.attachChild(card3p2);
			if (secondPlayer)
				id = card4Id;
			card4p2 = newSprite(id, CAMERA_WIDTH / 2 + (widthCard + 5), 0 + 70);
			card4p2.setSize(widthCard, heightCard);
			if (secondPlayer)
				this.mMainScene.registerTouchArea(card4p2);
			else {
				card4p2.ChangeAttack("");
				card4p2.ChangeHealth("");
				card4p2.ChangeMana("");
			}
			this.mMainScene.attachChild(card4p2);

			healthTextp2 = new Text(60, 10, this.mFont2, "health: " + healthp2,
					new TextOptions(HorizontalAlign.LEFT),
					this.getVertexBufferObjectManager());
			this.mMainScene.attachChild(healthTextp2);

			manaTextp2 = new Text(170, 10, this.mFont2, "mana: " + manap2,
					new TextOptions(HorizontalAlign.LEFT),
					this.getVertexBufferObjectManager());
			this.mMainScene.attachChild(manaTextp2);

			endTurnSprite();

			initialize = true;
		}
		Log.d("FUNCTION", "10E");
	}

	private void init(String roomId) {
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

	private int pickCardId() {
		Log.d("FUNCTION", "12S");
		int id;
		if (Utils.allElementsTheSame(usedCards))
			return -1;
		while (usedCards[id = (int) Math.floor(Math.random()
				* Cards.cardsp1.length)] == true)
			;

		usedCards[id] = true;
		Log.d("FUNCTION", "12E");
		return id;
	}

	public void addMorePlayer(boolean isMine, String userName,
			boolean secondPlayer) {

		Log.d("FUNCTION", "13S");
		Log.d("NEWUSER", userName + ": isMine: " + isMine + "sp: "
				+ secondPlayer);
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
		int height;
		Log.d("NEWUSER", Utils.userName);
		if (isMine == false)
			height = CAMERA_HEIGHT - 40;
		else if (userName.equals(Utils.userName) && !secondPlayer)
			height = CAMERA_HEIGHT - 40;
		else {
			height = 10;
		}
		final Sprite face = new Sprite(10, height, tiledTextureRegion,
				this.getVertexBufferObjectManager());
		face.setScale(1.5f);
		this.mMainScene.attachChild(face);
		User user = new User(face.getX(), face.getY(), face);
		userMap.put(userName, user);

		if (height == 10 && !secondPlayer) {
			startTurn();
		}
		Log.d("FUNCTION", "13E");
	}

	private void sendUpdateEvent(float xCord, float yCord) {
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

	private void updateProperty(String position, String objectType) {
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

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		Log.d("FUNCTION", "17S");
		this.mPinchZoomDetector.onTouchEvent(pSceneTouchEvent);
		  if (this.mPinchZoomDetector.isZooming()) {
		        this.mScrollDetector.setEnabled(false);
		    } else {
		        if (pSceneTouchEvent.isActionDown()) {
		            this.mScrollDetector.setEnabled(true);
		        }
		        this.mScrollDetector.onTouchEvent(pSceneTouchEvent);
				if (pSceneTouchEvent.isActionUp() && myTurn) {
					float x = pSceneTouchEvent.getX();
					float y = pSceneTouchEvent.getY();
					checkForCardMove(x, y);
					sendUpdateEvent(x, y);
					updateMove(false, Utils.userName, x, y);
				}
		    }

		Log.d("FUNCTION", "17E");
		return false;
	}

	private String getPosition(float x, float y) {
		Log.d("FUNCTION", "18S");
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

		if (x < 50 && y < 50) {
			position = "attackp2";
		} else if (x < 50 && y > CAMERA_HEIGHT - 50) {
			position = "attackp1";
		}
		Log.d("FUNCTION", "18E");
		return position;
	}

	private void checkForCardMove(float x, float y) {
		Log.d("FUNCTION", "19S");
		Log.d("CHECKCARDMOVE", "" + selectedCardId + "/" + selectedCardIdEnemy
				+ selectedFromField);
		if (selectedCardIdEnemy != -1 && selectedFromField) {
			int pos = 0;
			if (selectedCard == card1Field || selectedCard == card1p2Field) pos = 1;
			if (selectedCard == card2Field || selectedCard == card2p2Field) pos = 2;
			if (selectedCard == card3Field || selectedCard == card3p2Field) pos = 3;
			if (selectedCard == card4Field || selectedCard == card4p2Field) pos = 4;
			Log.d("ATTACK", ""+pos);
			placeObject(selectedCardId, selectedCardIdEnemy, getPosition(x, y),
					null, true, pos);
		} else if (selectedCardId != -1 && !selectedFromField) {
			placeObject(selectedCardId, -1, getPosition(x, y), null, true, 0);
		} else if (selectedCardId != -1 && selectedFromField) {
			String destination = getPosition(x, y);
			if (destination.equals("attackp1")
					|| destination.equals("attackp2")){
				placeObject(selectedCardId, -1, destination, null, true, 0);
			}
		}
		Log.d("FUNCTION", "19E");
	}

	public synchronized void attackCard(final int selectedObject,
			final int selectedObjectIdEnemy, final String destination,
			final String userName, boolean updateProperty, int pos) {
		Log.d("FUNCTION", "20S");
		CardSprite objectSprite = null;

		if (objectMap.get(destination) != null) {
			objectSprite = objectMap.get(destination);
		}
		boolean destroyAttacked = false;
		boolean destroyAttacker = false;
		int health;

		// you attack opponent
		if (updateProperty) {
			if (idsAttacked.contains(selectedObject)) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Utils.showToastAlert(GameActivity.this,
								"this card can't attack right now");
					}
				});
				return;
			}
			idsAttacked.add(selectedObject);

			health = Cards.getHealth(selectedObjectIdEnemy, !secondPlayer);
			Cards.setHealth(selectedObjectIdEnemy,
					health - Cards.getAttack(selectedObject, secondPlayer),
					!secondPlayer);
			objectSprite.ChangeHealth(""
					+ Cards.getHealth(selectedObjectIdEnemy, !secondPlayer));
			if (Cards.getHealth(selectedObjectIdEnemy, !secondPlayer) <= 0) {
				destroyAttacked = true;
			}
			
			health = Cards.getHealth(selectedObject, secondPlayer);
			Cards.setHealth(selectedObject,
					health - Cards.getAttack(selectedObjectIdEnemy, !secondPlayer),
					secondPlayer);
			selectedCard.ChangeHealth(""
					+ Cards.getHealth(selectedObject, secondPlayer));
			if (Cards.getHealth(selectedObject, secondPlayer) <= 0) {
				destroyAttacker = true;
			}
			// you get attacked by opponent
		} else {
			health = Cards.getHealth(selectedObjectIdEnemy, secondPlayer);
			Cards.setHealth(selectedObjectIdEnemy,
					health - Cards.getAttack(selectedObject, !secondPlayer),
					secondPlayer);
			objectSprite.ChangeHealth(
			"" + Cards.getHealth(selectedObjectIdEnemy, secondPlayer));

			if (Cards.getHealth(selectedObjectIdEnemy, secondPlayer) <= 0) {
				destroyAttacked = true;
			}
			Log.d("ATTACKED", ""+pos);
			if (secondPlayer) {
				if (pos == 1) {
					selectedCard = card1Field;
				} else if (pos == 2) {
					selectedCard = card2Field;
				} else if (pos == 3) {
					selectedCard = card3Field;
					Log.d("ATTACKED", "SET");
				} else if (pos == 4) {
					selectedCard = card4Field;
				}
			} else {
				if (pos == 1) {
					selectedCard = card1p2Field;
				} else if (pos == 2) {
					selectedCard = card2p2Field;
				} else if (pos == 3) {
					selectedCard = card3p2Field;
				} else if (pos == 4) {
					selectedCard = card4p2Field;
				}
			}

			
			health = Cards.getHealth(selectedObject, !secondPlayer);
			Cards.setHealth(selectedObject,
					health - Cards.getAttack(selectedObjectIdEnemy, secondPlayer),
					!secondPlayer);
			selectedCard.ChangeHealth(""
					+ Cards.getHealth(selectedObject, !secondPlayer));
			if (Cards.getHealth(selectedObject, !secondPlayer) <= 0) {
				destroyAttacker = true;
			}
		}
		if (destroyAttacked) {
			final EngineLock engineLock = this.mEngine.getEngineLock();
			engineLock.lock();
			this.mMainScene.detachChild(objectSprite);
			this.mMainScene.unregisterTouchArea(objectSprite);
			objectSprite = null;
			objectMap.remove(destination);
			engineLock.unlock();
		}
		if (destroyAttacker) {
			final EngineLock engineLock = this.mEngine.getEngineLock();
			engineLock.lock();
			this.mMainScene.detachChild(selectedCard);
			this.mMainScene.unregisterTouchArea(selectedCard);
			selectedCard = null;
			engineLock.unlock();
		}
		if (updateProperty) {
			selectedCardIdEnemy = -1;
			updateProperty(destination, selectedObject + "/"
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
		if (updateProperty) {

			if (idsAttacked.contains(id)) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Utils.showToastAlert(GameActivity.this,
								"this card can't attack right now");
					}
				});
				return;
			}
			idsAttacked.add(id);

			if (secondPlayer) {
				setHealth(healthp1 - Cards.getAttack(id, secondPlayer), false);
			} else {
				setHealth(healthp2 - Cards.getAttack(id, secondPlayer), true);
			}
			JSONObject object = new JSONObject();
			try {
				object.put("attackCharacter", id);
				theClient.sendChat(object.toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			if (secondPlayer) {
				setHealth(healthp2 - Cards.getAttack(id, !secondPlayer), true);
			} else {
				setHealth(healthp1 - Cards.getAttack(id, !secondPlayer), false);
			}
		}
	}

	public synchronized void placeObject(final int selectedObjectId,
			final int selectedObjectIdEnemy, final String destination,
			final String userName, boolean updateProperty, int pos) {
		Log.d("FUNCTION", "21S");
		Log.d("PLACEOBJECT", "" + selectedObjectId + "/"
				+ selectedObjectIdEnemy + selectedFromField + destination);

		if (destination == "attackp1" || destination == "attackp2") {
			if (secondPlayer && updateProperty && destination == "attackp2")
				return;
			if (!secondPlayer && updateProperty && destination == "attackp1")
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

		float xDest = 0;
		float yDest = 0;
		float padding = (CAMERA_WIDTH - 360) / 5.0f;
		if (destination.equals("card1")) {
			xDest = padding;
			yDest = CAMERA_HEIGHT * (1f / 2f) + 10;
		} else if (destination.equals("card2")) {
			xDest = padding * 2 + 90;
			yDest = CAMERA_HEIGHT * (1f / 2f) + 10;
		} else if (destination.equals("card3")) {
			xDest = padding * 3 + 180;
			yDest = CAMERA_HEIGHT * (1f / 2f) + 10;
		} else if (destination.equals("card4")) {
			xDest = padding * 4 + 270;
			yDest = CAMERA_HEIGHT * (1f / 2f) + 10;
		} else if (destination.equals("card1p2")) {
			xDest = padding;
			yDest = CAMERA_HEIGHT * (1f / 2f) - 138;
		} else if (destination.equals("card2p2")) {
			xDest = padding * 2 + 90;
			yDest = CAMERA_HEIGHT * (1f / 2f) - 138;
		} else if (destination.equals("card3p2")) {
			xDest = padding * 3 + 180;
			yDest = CAMERA_HEIGHT * (1f / 2f) - 138;
		} else if (destination.equals("card4p2")) {
			xDest = padding * 4 + 270;
			yDest = CAMERA_HEIGHT * (1f / 2f) - 138;
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

		if (!checkMana(selectedObjectId, updateProperty))
			return;

		// create new sprite with new ontouch options
		CardSprite sprite = null;
		if (secondPlayer) {
			if (!updateProperty) { // handle move of other user
				sprite = newSpriteOtherField(selectedObjectId,
						CAMERA_WIDTH / 2, CAMERA_HEIGHT - 100);
			} else { // handle move of current user
				sprite = newSpriteOwnField(selectedObjectId, CAMERA_WIDTH / 2,
						0 + 100);
			}
		} else {
			if (!updateProperty) {
				sprite = newSpriteOtherField(selectedObjectId,
						CAMERA_WIDTH / 2, 0 + 70);
			} else {
				sprite = newSpriteOwnField(selectedObjectId, CAMERA_WIDTH / 2,
						CAMERA_HEIGHT - heightCard - 70);
			}
		}
		if (!updateProperty) {
			if (secondPlayer) {
				if (pos == 1) {
					card1Field = sprite;
					removeSprite(card1);
					card1 = null;
				} else if (pos == 2) {
					card2Field = sprite;
					removeSprite(card2);
					card2 = null;
				} else if (pos == 3) {
					card3Field = sprite;
					removeSprite(card3);
					card3 = null;
				} else if (pos == 4) {
					card4Field = sprite;
					removeSprite(card4);
					card4 = null;
				}
			} else {
				if (pos == 1) {
					card1p2Field = sprite;
					removeSprite(card1p2);
					card1p2 = null;
				} else if (pos == 2) {
					card2p2Field = sprite;
					removeSprite(card2p2);
					card2p2 = null;
				} else if (pos == 3) {
					card3p2Field = sprite;
					removeSprite(card3p2);
					card3p2 = null;
				} else if (pos == 4) {
					card4p2Field = sprite;
					removeSprite(card4p2);
					card4p2 = null;
				}
			}

		} else if (selectedObjectId == card1Id) {
			card1Id = -1;
			pos = 1;
			if (secondPlayer) {
				card1p2Field = sprite;
				removeSprite(card1p2);
				card1p2 = null;
			} else {
				card1Field = sprite;
				removeSprite(card1);
				card1 = null;
			}

		} else if (selectedObjectId == card2Id) {
			card2Id = -1;
			pos = 2;
			if (secondPlayer) {
				card2p2Field = sprite;
				removeSprite(card2p2);
				card2p2 = null;
			} else {
				card2Field = sprite;
				removeSprite(card2);
				card2 = null;
			}
		} else if (selectedObjectId == card3Id) {
			card3Id = -1;
			pos = 3;
			if (secondPlayer) {
				card3p2Field = sprite;
				removeSprite(card3p2);
				card3p2 = null;
			} else {
				card3Field = sprite;
				removeSprite(card3);
				card3 = null;
			}
		} else if (selectedObjectId == card4Id) {
			card4Id = -1;
			pos = 4;
			if (secondPlayer) {
				card4p2Field = sprite;
				removeSprite(card4p2);
				card4p2 = null;
			} else {
				card4Field = sprite;
				removeSprite(card4);
				card4 = null;
			}
		} else {
			return;
		}
		sprite.setSize(widthCard, heightCard);

		selectedCard = null;
		selectedCardId = -1;

		objectMap.put(destination, sprite);
		this.mMainScene.attachChild(sprite);
		this.mMainScene.registerTouchArea(sprite);
		sprite.registerEntityModifier(new MoveModifier(1, sprite.getX(), xDest,
				sprite.getY(), yDest));
		if (updateProperty) {
			updateProperty(destination, selectedObjectId + ":" + pos);
			idsAttacked.add(selectedObjectId);
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
		Log.d("FUNCTION", "21E");
	}

	private boolean checkMana(int id, boolean updateProperty) {
		int manaCost;
		if (updateProperty) {
			manaCost = Cards.getMana(id, secondPlayer);
			if (secondPlayer) {
				if (manap2 - manaCost >= 0) {
					setMana(manap2 - manaCost, secondPlayer);
					return true;
				} else
					return false;
			} else {
				if (manap1 - manaCost >= 0) {
					setMana(manap1 - manaCost, secondPlayer);
					return true;
				} else
					return false;
			}
		} else {
			manaCost = Cards.getMana(id, !secondPlayer);
			if (secondPlayer) {
				setMana(manap1 - manaCost, !secondPlayer);
			} else {
				setMana(manap2 - manaCost, !secondPlayer);
			}
			return true;
		}
	}

	private void setMana(int mana, boolean secondPlayer) {
		if (secondPlayer) {
			manap2 = mana;
			this.mMainScene.detachChild(manaTextp2);
			manaTextp2 = new Text(170, 10, this.mFont2, "mana: " + manap2,
					new TextOptions(HorizontalAlign.LEFT),
					this.getVertexBufferObjectManager());
			this.mMainScene.attachChild(manaTextp2);
		} else {
			manap1 = mana;
			this.mMainScene.detachChild(manaTextp1);
			manaTextp1 = new Text(170, CAMERA_HEIGHT - 30, this.mFont2, "mana: "
					+ manap1, new TextOptions(HorizontalAlign.LEFT),
					this.getVertexBufferObjectManager());
			this.mMainScene.attachChild(manaTextp1);
		}
	}

	private void setHealth(int health, boolean secondPlayer) {
		if (secondPlayer) {
			healthp2 = health;
			this.mMainScene.detachChild(healthTextp2);
			healthTextp2 = new Text(60, 10, this.mFont2, "health: " + healthp2,
					new TextOptions(HorizontalAlign.LEFT),
					this.getVertexBufferObjectManager());
			this.mMainScene.attachChild(healthTextp2);
		} else {
			healthp1 = health;
			this.mMainScene.detachChild(healthTextp1);
			healthTextp1 = new Text(60, CAMERA_HEIGHT - 30, this.mFont2,
					"health: " + healthp1,
					new TextOptions(HorizontalAlign.LEFT),
					this.getVertexBufferObjectManager());
			this.mMainScene.attachChild(healthTextp1);
		}
		checkGameOver();
	}

	private void checkGameOver() {
		int gameOver = 0;
		if (healthp2 < 0) {
			if (secondPlayer)
				gameOver = 1;
			else
				gameOver = 2;
		} else if (healthp1 < 0) {
			if (secondPlayer)
				gameOver = 2;
			else
				gameOver = 1;
		}
		if (gameOver == 1) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Utils.showToastAlert(GameActivity.this, "You are Defeated");
				}
			});
		} else if (gameOver == 2) {
			endTurn();
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Utils.showToastAlert(GameActivity.this, "Victory");
				}
			});
			

		}
	}

	private void removeSprite(CardSprite sprite) {
		final EngineLock engineLock = this.mEngine.getEngineLock();
		engineLock.lock();
		this.mMainScene.detachChild(sprite);
		this.mMainScene.unregisterTouchArea(sprite);
		engineLock.unlock();
	}

	public void updateMove(boolean isRemote, String userName, float xPer,
			float yPer) {
		Log.d("FUNCTION", "22S");
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
			if (distance > 0) {
				sprite.registerEntityModifier(new MoveModifier(time, sprite
						.getX(), xPer, sprite.getY(), yPer));
			}
		}
		Log.d("FUNCTION", "22E");
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
		// android.os.Process.killProcess(android.os.Process.myPid());
	}

	@Override
	protected void onStop() {
		Log.d("FUNCTION", "24S");
		super.onStop();
		Log.d("FUNCTION", "24E");
		// android.os.Process.killProcess(android.os.Process.myPid());
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
		// android.os.Process.killProcess(android.os.Process.myPid());
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

	@Override
	public void onPinchZoom(PinchZoomDetector pPinchZoomDetector,
	    TouchEvent pTouchEvent, float pZoomFactor) {
	    
	  /* On every sub-sequent touch event (after the initial touch) we offset
	  * the initial camera zoom factor by the zoom factor calculated by
	  * pinch-zooming */
	  final float newZoomFactor = mInitialTouchZoomFactor * pZoomFactor;
	    
	  // If the camera is within zooming bounds
	  if(newZoomFactor < 2.5f && newZoomFactor > 1.0f){
	    // Set the new zoom factor
	    mSmoothCamera.setZoomFactor(newZoomFactor);
	  }
	}
	@Override
	public void onPinchZoomFinished(PinchZoomDetector pPinchZoomDetector,
	    TouchEvent pTouchEvent, float pZoomFactor) {
	    
	  /* On every sub-sequent touch event (after the initial touch) we offset
	  * the initial camera zoom factor by the zoom factor calculated by
	  * pinch-zooming */
	  final float newZoomFactor = mInitialTouchZoomFactor * pZoomFactor;
	    
	  // If the camera is within zooming bounds
	  if(newZoomFactor < 2.5f && newZoomFactor > 1.0f){
	    // Set the new zoom factor
	    mSmoothCamera.setZoomFactor(newZoomFactor);
	  }
	}

	@Override
	public void onPinchZoomStarted(PinchZoomDetector pPinchZoomDetector,
	    TouchEvent pSceneTouchEvent) {
	  // On first detection of pinch zooming, obtain the initial zoom factor
	  mInitialTouchZoomFactor = mSmoothCamera.getZoomFactor();
	}
	
	@Override
	public void onScrollStarted(final ScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY) {
	    final float zoomFactor = this.mSmoothCamera.getZoomFactor();
	    this.mSmoothCamera.offsetCenter(-pDistanceX / zoomFactor, -pDistanceY / zoomFactor);
	}

	@Override
	public void onScroll(final ScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY) {
	    final float zoomFactor = this.mSmoothCamera.getZoomFactor();
	    this.mSmoothCamera.offsetCenter(-pDistanceX / zoomFactor, -pDistanceY / zoomFactor);
	}

	@Override
	public void onScrollFinished(final ScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY) {
	    final float zoomFactor = this.mSmoothCamera.getZoomFactor();
	    this.mSmoothCamera.offsetCenter(-pDistanceX / zoomFactor, -pDistanceY / zoomFactor);
	}

}