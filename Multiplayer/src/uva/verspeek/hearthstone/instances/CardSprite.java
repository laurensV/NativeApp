package uva.verspeek.hearthstone.instances;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.modifier.IModifier;

import uva.verspeek.hearthstone.controllers.CardsController;
import uva.verspeek.hearthstone.tools.createSprites;


public class CardSprite extends Sprite {
	
	private Text textAttack;
	private Text textHealth;
	private Text textMana;
	private Text textDamage;
	
	private Sprite damageSprite;
	private Sprite highlightSprite;
	private final AlphaModifier fadeout = new AlphaModifier(3, 1, 0);


	public CardSprite(TiledTextureRegion pTextureRegion, Font pFont, VertexBufferObjectManager vb, Sprite damage, Sprite highlight) {
		super(0, 0, pTextureRegion, vb);
		this.setScaleCenter(CardsController.widthCard/2, CardsController.heightCard/2);

		
		textAttack = new Text(0, 0, pFont, "--", new TextOptions(HorizontalAlign.LEFT), vb);
		textAttack.setPosition(5, this.getY() + this.getHeight() - textAttack.getHeight());
		//textAttack.setColor(0.2f, 0.063f, 0.063f);
		
		textHealth = new Text(0, 0, pFont, "--" , new TextOptions(HorizontalAlign.RIGHT), vb);
		textHealth.setPosition(this.getX() + this.getWidth() - textAttack.getWidth()-5, this.getY() + this.getHeight() - textHealth.getHeight());		
		//textHealth.setColor(0, 1f, 0);
		
		textMana = new Text(0, 0, pFont, "--" , new TextOptions(HorizontalAlign.LEFT), vb);
		textMana.setPosition(5, 5);		
		//textMana.setColor(0, 0, 1f);
	
		textDamage = new Text(0, 0, pFont, "---", new TextOptions(HorizontalAlign.LEFT), vb);
		textDamage.setPosition(this.getX()+CardsController.widthCard/2-textDamage.getWidth()/2, this.getY()+CardsController.heightCard/2-textDamage.getHeight()/2);
		textDamage.setAlpha(0);
		
		damageSprite = damage;
		damageSprite.setSize(CardsController.heightCard/2, CardsController.heightCard/2);
		damageSprite.setPosition(this.getX()+CardsController.widthCard/2-damageSprite.getWidth()/2, this.getY()+CardsController.heightCard/2-damageSprite.getHeight()/2);
		damageSprite.setAlpha(0);
		
		highlightSprite = highlight;
		highlightSprite.setSize(CardsController.widthCard, CardsController.heightCard);
		//highlightSprite.setPosition(this.getX()+CardsController.widthCard/2-damageSprite.getWidth()/2, this.getY()+CardsController.heightCard/2-damageSprite.getHeight()/2);
		highlightSprite.setAlpha(0);
		
		this.attachChild(textAttack);
		this.attachChild(textHealth);
		this.attachChild(textMana);
		this.attachChild(damageSprite);
		this.attachChild(textDamage);
		this.attachChild(highlightSprite);
	}
	
	public void ChangeAttack(String pTextAttack){
		textAttack.setText(pTextAttack);
	}
	public void ChangeHealth(String pTextHealth){
		textHealth.setText(pTextHealth);
	}
	public void ChangeMana(String pTextMana){
		textMana.setText(pTextMana);
	}
	
	public void ShowDamage(String pTextDamage){
		textDamage.setText(pTextDamage);
		//damageSprite.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		fadeout.reset();

	    IEntityModifier fadein = new AlphaModifier(3, 1, 1, new IEntityModifierListener(){
	        @Override
	        public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
	        	
	        }

	        @Override
	        public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
	    	    textDamage.registerEntityModifier(fadeout);
	    	    damageSprite.registerEntityModifier(fadeout);
	        }
	    });

	    damageSprite.registerEntityModifier(fadein);
	    textDamage.registerEntityModifier(fadein);
	}
	
	public void ShowHighlight(){
	    IEntityModifier fadein = new AlphaModifier(3, 0, 1);
	    highlightSprite.registerEntityModifier(fadein);
	}
	
	public void HideHighlight(){
		 highlightSprite.setAlpha(0);
	}

	
	@Override
	public void setSize(float pWidth, float pHeight){
		super.setSize(pWidth, pHeight);
		textAttack.setScaleCenter(textAttack.getWidth()/2, textAttack.getHeight()/2);
		textAttack.setPosition(pWidth*(1f/14f), pHeight*(9.5f/12f));
		textAttack.setScale(1/2f);
		textHealth.setScaleCenter(textHealth.getWidth()/2, textHealth.getHeight()/2);
		textHealth.setPosition(pWidth*(9.5f/12f), pHeight*(9.5f/12f));		
		textHealth.setScale(1/2f);
		textMana.setScaleCenter(textMana.getWidth()/2, textMana.getHeight()/2);
		textMana.setPosition(pWidth*(1f/20f), pHeight*(1f/35f));
		textMana.setScale(1/2f);
		this.setZIndex(createSprites.zindex);
	}
	@Override
	public void setScale(float sWidth, float sHeight){
		super.setScale(sWidth, sHeight);
		textAttack.setScaleCenter(textAttack.getWidth()/2, textAttack.getHeight()/2);
		textAttack.setPosition(this.getWidth()*(1f/14f), this.getHeight()*(9.5f/12f));
		textAttack.setScale(sWidth/2f);
		textHealth.setScaleCenter(textHealth.getWidth()/2, textHealth.getHeight()/2);
		textHealth.setPosition(this.getWidth()*(9.5f/12f), this.getHeight()*(9.5f/12f));	
		textHealth.setScale(sWidth/2f);
		textMana.setScaleCenter(textMana.getWidth()/2, textMana.getHeight()/2);
		textMana.setPosition(this.getWidth()*(1f/20f), this.getHeight()*(1f/35f));
		textMana.setScale(sWidth/2f);
		this.setZIndex(createSprites.zindex);
	}
	
}
