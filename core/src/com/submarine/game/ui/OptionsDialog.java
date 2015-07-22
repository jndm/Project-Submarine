package com.submarine.game.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.submarine.game.Main;
import com.submarine.game.screens.Mainmenu;
import com.submarine.game.utils.Constants;

public class OptionsDialog extends Dialog{
	
	private Main game;
	
	public OptionsDialog(String title, Skin skin, Main game, final Mainmenu menu) {
		super(title, skin);
		this.game = game;
		
		createThemeChoosingCheckBoxGroup(skin);
		
		TextButton applyButton = new TextButton("Apply", skin);
		applyButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				menu.applySettings(true);
				hide();
			}
		});
		
		TextButton cancelButton = new TextButton("Cancel", skin);
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

	private void createThemeChoosingCheckBoxGroup(Skin skin) {
		final CheckBox red = new CheckBox("Red", skin);
		final CheckBox blue = new CheckBox("Blue", skin);
		final CheckBox green = new CheckBox("Green", skin);
		
		checkCurrentTheme(red, blue, green);
		
		ChangeListener changeListener = new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(red.isChecked()) {
					game.theme = Constants.Theme.RED;
				} else if(blue.isChecked()) {
					game.theme = Constants.Theme.BLUE;
				} else if(green.isChecked()) {
					game.theme = Constants.Theme.GREEN;
				}
			}
			
		};
		
		red.addListener(changeListener);
		blue.addListener(changeListener);
		green.addListener(changeListener);
		
		ButtonGroup<CheckBox> themeCheckboxGroup = new ButtonGroup<CheckBox>(red, blue, green);
		themeCheckboxGroup.setMaxCheckCount(1);
		themeCheckboxGroup.setMinCheckCount(1);
		themeCheckboxGroup.setUncheckLast(true);
		themeCheckboxGroup.setChecked("Status");
		
		add(red).left();
		add(blue).left();
		add(green).left();
		row();
	}

	private void checkCurrentTheme(CheckBox red, CheckBox blue, CheckBox green) {
		if(game.theme == Constants.Theme.RED) {
			red.setChecked(true);
		} else if(game.theme == Constants.Theme.BLUE) {
			blue.setChecked(true);
		} else if(game.theme == Constants.Theme.GREEN) {
			green.setChecked(true);
		}
	}
	
}
