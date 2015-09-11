package com.submarine.game.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.submarine.game.Main;
import com.submarine.game.screens.Mainmenu;

public class OptionsDialog extends Dialog{
	
	private Main game;
	
	public OptionsDialog(String title, Skin skin, Main game, final Mainmenu menu) {
		super(title, skin);
		this.game = game;
		
		TextButton applyButton = new TextButton("Apply", skin, "optionsbutton");
		applyButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				menu.applySettings(true);
				hide();
			}
		});
		
		TextButton cancelButton = new TextButton("Cancel", skin, "optionsbutton");
		cancelButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				hide();
			}
		});
		
		
		add();
		add(applyButton);
		add(cancelButton);
		setResizable(false);
	}
	
}
