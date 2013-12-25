package com.verspeek.hearthstone;

import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.HorizontalAlign;

public class CardSprite extends Sprite {
	
	private Text textAttack;
	private Text textHealth;
	private Text textMana;

	public CardSprite(TiledTextureRegion pTextureRegion, Font pFont, VertexBufferObjectManager vb) {
		super(0, 0, pTextureRegion, vb);
		this.setScaleCenter(GameActivity.widthCard/2, GameActivity.heightCard/2);

		
		textAttack = new Text(0, 0, pFont, "--", new TextOptions(HorizontalAlign.LEFT), vb);
		textAttack.setPosition(5, this.getY() + this.getHeight() - textAttack.getHeight());
		//textAttack.setColor(0.2f, 0.063f, 0.063f);
		this.attachChild(textAttack);
		
		textHealth = new Text(0, 0, pFont, "--" , new TextOptions(HorizontalAlign.RIGHT), vb);
		textHealth.setPosition(this.getX() + this.getWidth() - textAttack.getWidth()-5, this.getY() + this.getHeight() - textHealth.getHeight());		
		//textHealth.setColor(0, 1f, 0);
		this.attachChild(textHealth);
		
		textMana = new Text(0, 0, pFont, "--" , new TextOptions(HorizontalAlign.LEFT), vb);
		textMana.setPosition(5, 5);		
		//textMana.setColor(0, 0, 1f);
		this.attachChild(textMana);
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
		this.setZIndex(GameActivity.zindex);
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
		this.setZIndex(GameActivity.zindex);
	}
	
}
