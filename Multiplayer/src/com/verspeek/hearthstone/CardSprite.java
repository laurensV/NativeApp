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

	public CardSprite(TiledTextureRegion pTextureRegion, Font pFont, String pTextAttack, String pTextHealth, VertexBufferObjectManager vb) {
		super(0, 0, pTextureRegion, vb);
		textAttack = new Text(0, 0, pFont, pTextAttack, new TextOptions(HorizontalAlign.LEFT), vb);
		textAttack.setPosition((this.getX() + this.getWidth() - textAttack.getWidth()), (this.getY() + this.getHeight() - textAttack.getHeight()));
		textAttack.setColor(1, 0, 0);
		this.attachChild(textAttack);
		textHealth = new Text(0, 0, pFont, pTextHealth, new TextOptions(HorizontalAlign.RIGHT), vb);
		textHealth.setPosition(0, (this.getY() + this.getHeight() - textHealth.getHeight()));		textHealth.setColor(0, 1, 0);
		this.attachChild(textHealth);
	}
	
	public void ChangeText(String pTextAttack, String pTextHealth){
		textAttack.setText(pTextAttack);
		textHealth.setText(pTextHealth);
	}
}
