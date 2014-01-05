package uva.verspeek.hearthstone.tools;



import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.json.JSONException;
import org.json.JSONObject;

import uva.verspeek.hearthstone.activities.GameActivity;
import uva.verspeek.hearthstone.controllers.CardsController;
import uva.verspeek.hearthstone.instances.CardSprite;
import uva.verspeek.hearthstone.models.Constants;
import uva.verspeek.hearthstone.models.Decks;



import android.util.Log;

public class createSprites {
	static GameActivity gameScreen;

	public static int textureCount = 0;
	public static int zindex = 1;
	


	public createSprites(GameActivity gameScreen) {
		createSprites.gameScreen = gameScreen;
	}

	public void endTurnSprite() {
		BitmapTextureAtlas cardBitmapTextureAtlas1;
		TiledTextureRegion mCardTiledTextureRegion1;

		cardBitmapTextureAtlas1 = new BitmapTextureAtlas(
				gameScreen.getTextureManager(), 200, 100);

		mCardTiledTextureRegion1 = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(cardBitmapTextureAtlas1, gameScreen,
						"endTurn.png", 0, 0, 1, 1);

		cardBitmapTextureAtlas1.load();
		gameScreen.textures.put(textureCount, cardBitmapTextureAtlas1);
		textureCount++;

		int height = 0;
		if (!gameScreen.secondPlayer) {
			height = (int) (GameActivity.CAMERA_HEIGHT - (CardsController.widthCard / 2.3f));
		}

		Sprite sprite = new Sprite(GameActivity.CAMERA_WIDTH
				- CardsController.widthCard, (float) height,
				mCardTiledTextureRegion1,
				gameScreen.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
					if (gameScreen.gc.myTurn) {
						JSONObject object = new JSONObject();
						try {
							object.put("turn", "over");
							gameScreen.theClient.sendChat(object.toString());
						} catch (JSONException e) {
							e.printStackTrace();
						}

						gameScreen.gc.endTurn();
					}
				}
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX,
						pTouchAreaLocalY);

			}
		};
		sprite.setSize(CardsController.widthCard, CardsController.widthCard / 2);
		gameScreen.mMainScene.registerTouchArea(sprite);
		gameScreen.mMainScene.attachChild(sprite);
	}

	public Sprite newCardSpriteCardPlaceholder(float x, float y) {
		BitmapTextureAtlas cardBitmapTextureAtlas;
		TiledTextureRegion mCardTiledTextureRegion;

		cardBitmapTextureAtlas = new BitmapTextureAtlas(
				gameScreen.getTextureManager(), 350, 350);

		mCardTiledTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(cardBitmapTextureAtlas, gameScreen,
						"placeholder.png", 0, 0, 1, 1);

		cardBitmapTextureAtlas.load();
		gameScreen.textures.put(textureCount, cardBitmapTextureAtlas);
		textureCount++;

		Sprite spriteCardPlaceholder = new Sprite(x, y,
				mCardTiledTextureRegion,
				gameScreen.getVertexBufferObjectManager());
		return spriteCardPlaceholder;
	}

	public static Sprite SpriteDamage() {
		BitmapTextureAtlas cardBitmapTextureAtlas;
		TiledTextureRegion mCardTiledTextureRegion;

		cardBitmapTextureAtlas = new BitmapTextureAtlas(
				gameScreen.getTextureManager(), 128, 128);

		mCardTiledTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(cardBitmapTextureAtlas, gameScreen,
						"damage.png", 0, 0, 1, 1);

		cardBitmapTextureAtlas.load();
		gameScreen.textures.put(textureCount, cardBitmapTextureAtlas);
		textureCount++;

		return new Sprite(0, 0, mCardTiledTextureRegion,
				gameScreen.getVertexBufferObjectManager());
	}

	public static CardSprite newCardSprite(final int id, float x, float y) {
		Log.d("FUNCTION", "6START");
		BitmapTextureAtlas cardBitmapTextureAtlas;
		TiledTextureRegion mCardTiledTextureRegion;

		cardBitmapTextureAtlas = new BitmapTextureAtlas(
				gameScreen.getTextureManager(), 350, 350);

		mCardTiledTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(cardBitmapTextureAtlas, gameScreen,
						Decks.getName(id, gameScreen.secondPlayer) + ".png", 0,
						0, 1, 1);

		cardBitmapTextureAtlas.load();
		gameScreen.textures.put(textureCount, cardBitmapTextureAtlas);
		textureCount++;

		CardSprite spriteCard = new CardSprite(mCardTiledTextureRegion,
				gameScreen.mFont, gameScreen.getVertexBufferObjectManager(),
				SpriteDamage()) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
					if (gameScreen.cc.selectedCardId == id) {
						gameScreen.cc.selectedCardId = -1;
						gameScreen.cc.selectedCard.setScale(1, 1);
					} else {
						gameScreen.cc.selectedCardId = id;
						gameScreen.cc.selectedFromField = false;
						if (gameScreen.cc.selectedCard != null) {
							gameScreen.cc.selectedCard.setScale(1, 1);
						}
						zindex++;
						this.setScale(Constants.scale, Constants.scale);
						gameScreen.mMainScene.sortChildren();

						gameScreen.cc.selectedCard = this;
					}
					gameScreen.cc.selectedCardIdEnemy = -1;
				}
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX,
						pTouchAreaLocalY);

			}
		};
		spriteCard.ChangeAttack(""
				+ Decks.getAttack(id, gameScreen.secondPlayer));
		spriteCard.ChangeHealth(""
				+ Decks.getHealth(id, gameScreen.secondPlayer));
		spriteCard.ChangeMana("" + Decks.getMana(id, gameScreen.secondPlayer));
		spriteCard.setPosition(x, y);
		Log.d("FUNCTION", "6END");
		return spriteCard;
	}

	public static CardSprite newCardSpriteOtherField(final int id, float x, float y) {
		Log.d("FUNCTION", "7START");
		BitmapTextureAtlas cardBitmapTextureAtlas1;
		TiledTextureRegion mCardTiledTextureRegion1;

		cardBitmapTextureAtlas1 = new BitmapTextureAtlas(
				gameScreen.getTextureManager(), 350, 350);

		mCardTiledTextureRegion1 = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(cardBitmapTextureAtlas1, gameScreen,
						Decks.getName(id, !gameScreen.secondPlayer) + ".png",
						0, 0, 1, 1);

		cardBitmapTextureAtlas1.load();
		gameScreen.textures.put(textureCount, cardBitmapTextureAtlas1);
		textureCount++;

		CardSprite spriteCard = new CardSprite(mCardTiledTextureRegion1,
				gameScreen.mFont, gameScreen.getVertexBufferObjectManager(),
				SpriteDamage()) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
					gameScreen.cc.selectedCardIdEnemy = id;
				}
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX,
						pTouchAreaLocalY);
			}
		};
		spriteCard.ChangeAttack(""
				+ Decks.getAttack(id, !gameScreen.secondPlayer));
		spriteCard.ChangeHealth(""
				+ Decks.getHealth(id, !gameScreen.secondPlayer));
		spriteCard.ChangeMana("" + Decks.getMana(id, !gameScreen.secondPlayer));
		spriteCard.setPosition(x, y);
		Log.d("FUNCTION", "7E");
		return spriteCard;

	}

	public static CardSprite newCardSpriteOwnField(final int id, float x, float y) {
		Log.d("FUNCTION", "8S");
		BitmapTextureAtlas cardBitmapTextureAtlas1;
		TiledTextureRegion mCardTiledTextureRegion1;

		cardBitmapTextureAtlas1 = new BitmapTextureAtlas(
				gameScreen.getTextureManager(), 350, 350);

		mCardTiledTextureRegion1 = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(cardBitmapTextureAtlas1, gameScreen,
						Decks.getName(id, gameScreen.secondPlayer) + ".png", 0,
						0, 1, 1);

		cardBitmapTextureAtlas1.load();
		gameScreen.textures.put(textureCount, cardBitmapTextureAtlas1);
		textureCount++;

		CardSprite spriteCard = new CardSprite(mCardTiledTextureRegion1,
				gameScreen.mFont, gameScreen.getVertexBufferObjectManager(),
				SpriteDamage()) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
					gameScreen.cc.selectedCardId = id;
					gameScreen.cc.selectedFromField = true;
					if (gameScreen.cc.selectedCard != null) {
						gameScreen.cc.selectedCard.setScale(1, 1);
					}
					zindex++;
					this.setScale(Constants.scale, Constants.scale);
					gameScreen.mMainScene.sortChildren();
					gameScreen.cc.selectedCard = this;
					gameScreen.cc.selectedCardIdEnemy = -1;
				}
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX,
						pTouchAreaLocalY);
			}
		};
		spriteCard.ChangeAttack(""
				+ Decks.getAttack(id, gameScreen.secondPlayer));
		spriteCard.ChangeHealth(""
				+ Decks.getHealth(id, gameScreen.secondPlayer));
		spriteCard.ChangeMana("" + Decks.getMana(id, gameScreen.secondPlayer));
		spriteCard.setPosition(x, y);
		Log.d("FUNCTION", "8E");
		return spriteCard;
	}
}
