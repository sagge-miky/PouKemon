/*
 * Copyright (c) 2014, Miguel Angel Astor Romero
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * Read the LICENSE file for more details.
 */
package com.gamejolt.mikykr5.ceidecpong.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.gamejolt.mikykr5.ceidecpong.GameCore;
import com.gamejolt.mikykr5.ceidecpong.GameCore.game_states_t;
import com.gamejolt.mikykr5.ceidecpong.effects.ScrollingBackground;
import com.gamejolt.mikykr5.ceidecpong.interfaces.AssetsLoadedListener;
import com.gamejolt.mikykr5.ceidecpong.utils.AsyncAssetLoader;
import com.gamejolt.mikykr5.ceidecpong.utils.managers.CachedFontManager;

/**
 * A state in charge of rendering and handling the main menu of the game.
 * 
 * @author Miguel Astor
 */
public class MainMenuState extends BaseState implements AssetsLoadedListener{
	/**
	 * An {@link AsyncAssetLoader} instance.
	 */
	private AsyncAssetLoader  loader;

	/**
	 * An instance of the {@link CachedFontManager} used to render the buttons.
	 */
	private CachedFontManager fontManager;

	/**
	 * A flag to indicate that all assets have been loaded.
	 */
	private boolean           assetsLoaded;

	// Buttons and other GUI components.
	private TextButton          startButton;
	private Rectangle           startButtonBBox;
	private TextButton          quitButton;
	private Rectangle           quitButtonBBox;
	private Texture             menuButtonEnabledTexture;
	private Texture             menuButtonDisabledTexture;
	private Texture             menuButtonPressedTexture;
	private NinePatch           menuButtonEnabled9p;
	private NinePatch           menuButtonDisabled9p;
	private NinePatch           menuButtonPressed9p;
	private BitmapFont          buttonFont;

	/**
	 * A {@link ScrollingBackground} effect to make things livelier.
	 */
	private ScrollingBackground scrollingBckg;

	/**
	 * A flag to indicate that the start button is pressed.
	 */
	private boolean startButtonTouched;

	/**
	 * A holder to identify the finger that is touching the start button in multitouch screens.
	 */
	private int     startButtonTouchPointer;

	/**
	 * A flag to indicate that the quit button is pressed.
	 */
	private boolean quitButtonTouched;

	/**
	 * A holder to identify the finger that is touching the quit button in multitouch screens.
	 */
	private int     quitButtonTouchPointer;

	/**
	 * Creates the menu and loads all related assets.
	 * 
	 * @param core A game core. See {@link BaseState#BaseState(GameCore)} for details.
	 * @throws IllegalArgumentException If core is null.
	 */
	public MainMenuState(final GameCore core) throws IllegalArgumentException{
		super(core);

		loader = AsyncAssetLoader.getInstance();
		fontManager = CachedFontManager.getInstance();
		assetsLoaded = false;

		// Load graphic resources.
		loader.addAssetToLoad("data/gfx/gui/Anonymous_Pill_Button_Yellow.png", Texture.class);
		loader.addAssetToLoad("data/gfx/gui/Anonymous_Pill_Button_Cyan.png", Texture.class);
		loader.addAssetToLoad("data/gfx/gui/Anonymous_Pill_Button_Blue.png", Texture.class);
		buttonFont = fontManager.loadFont("data/fonts/d-puntillas-B-to-tiptoe.ttf");

		// Set up the background.
		scrollingBckg = new ScrollingBackground("data/gfx/textures/grass.png");

		startButtonTouched = false;
		startButtonTouchPointer = -1;
		quitButtonTouched = false;
		quitButtonTouchPointer = -1;

		stateEnabled = false;
	}

	@Override
	public void render(float delta) throws IllegalStateException{
		if(!assetsLoaded)
			throw new IllegalStateException("Attempted to render before assets were loaded.");

		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		core.batch.setProjectionMatrix(pixelPerfectCamera.combined);
		core.batch.begin();{
			// Render the background.
			scrollingBckg.render(core.batch);

			// Render the buttons.
			startButton.draw(core.batch, 1.0f);
			quitButton.draw(core.batch, 1.0f);

		}core.batch.end();
	}

	@Override
	public void dispose(){
		menuButtonEnabledTexture.dispose();
		menuButtonDisabledTexture.dispose();
		menuButtonPressedTexture.dispose();
		scrollingBckg.dispose();
		CachedFontManager.freeInstance();
	}

	/*;;;;;;;;;;;;;;;;;;;;;;;;;;
	  ; INPUT LISTENER METHODS ;
	  ;;;;;;;;;;;;;;;;;;;;;;;;;;*/

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button){
		unprojectTouch(screenX, screenY);

		// If either button is enabled, has been touched and the opposite button is not touched then mark it as
		// activated and save the id of the pointer that touched it.
		if(!startButton.isDisabled() && startButtonBBox.contains(touchPointWorldCoords) && !quitButtonTouched){
			startButton.setChecked(true);
			startButtonTouched = true;
			startButtonTouchPointer = pointer;
		}else if(!quitButton.isDisabled() && quitButtonBBox.contains(touchPointWorldCoords) && !startButtonTouched){
			quitButton.setChecked(true);
			quitButtonTouched = true;
			quitButtonTouchPointer = pointer;
		}

		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button){
		unprojectTouch(screenX, screenY);

		// If either button was activated and touched but has been released by the user then mark it as inactive
		// and execute it's respective state change.
		if(!startButton.isDisabled() && startButtonBBox.contains(touchPointWorldCoords) && startButtonTouched){
			startButton.setChecked(false);
			startButtonTouched = false;
			startButtonTouchPointer = -1;
			core.nextState = game_states_t.IN_GAME;
		}else if(!quitButton.isDisabled() && quitButtonBBox.contains(touchPointWorldCoords) && quitButtonTouched){
			quitButton.setChecked(false);
			quitButtonTouched = false;
			quitButtonTouchPointer = -1;
			core.nextState = game_states_t.QUIT;
		}

		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer){
		unprojectTouch(screenX, screenY);

		// If either button was touched but the user dragged the pointer away from the button without releasing it then
		// mark the button as inactive.
		if(!startButton.isDisabled() && startButtonTouched && pointer == startButtonTouchPointer && !startButtonBBox.contains(touchPointWorldCoords)){
			startButtonTouchPointer = -1;
			startButtonTouched = false;
			startButton.setChecked(false);
		}else if(!quitButton.isDisabled() && quitButtonTouched && pointer == quitButtonTouchPointer && !quitButtonBBox.contains(touchPointWorldCoords)){
			quitButtonTouchPointer = -1;
			quitButtonTouched = false;
			quitButton.setChecked(false);
		}

		return true;
	}

	@Override
	public boolean keyDown(int keycode){
		// If the user pressed the escape key or the back button in Android then quit.
		if(keycode == Input.Keys.BACK || keycode == Input.Keys.ESCAPE){
			Gdx.app.exit();
			return true;
		}
		return false;
	}

	@Override
	public void onAssetsLoaded() {
		TextButtonStyle textButtonStyle;

		// Create the button backgrounds.
		menuButtonEnabledTexture = loader.getAsset("data/gfx/gui/Anonymous_Pill_Button_Yellow.png", Texture.class);
		menuButtonDisabledTexture = loader.getAsset("data/gfx/gui/Anonymous_Pill_Button_Cyan.png", Texture.class);
		menuButtonPressedTexture = loader.getAsset("data/gfx/gui/Anonymous_Pill_Button_Blue.png", Texture.class);

		menuButtonEnabled9p = new NinePatch(new TextureRegion(menuButtonEnabledTexture, 0, 0, menuButtonEnabledTexture.getWidth(), menuButtonEnabledTexture.getHeight()), 49, 49, 45, 45);
		menuButtonDisabled9p = new NinePatch(new TextureRegion(menuButtonDisabledTexture, 0, 0, menuButtonDisabledTexture.getWidth(), menuButtonDisabledTexture.getHeight()), 49, 49, 45, 45);
		menuButtonPressed9p = new NinePatch(new TextureRegion(menuButtonPressedTexture, 0, 0, menuButtonPressedTexture.getWidth(), menuButtonPressedTexture.getHeight()), 49, 49, 45, 45);

		// Create the buttons.
		textButtonStyle = new TextButtonStyle();
		textButtonStyle.font = buttonFont;
		textButtonStyle.up = new NinePatchDrawable(menuButtonEnabled9p);
		textButtonStyle.checked = new NinePatchDrawable(menuButtonPressed9p);
		textButtonStyle.disabled = new NinePatchDrawable(menuButtonDisabled9p);
		textButtonStyle.fontColor = new Color(Color.BLACK);
		textButtonStyle.downFontColor = new Color(Color.WHITE);
		textButtonStyle.disabledFontColor = new Color(Color.BLACK);

		startButton = new TextButton("Start game", textButtonStyle);
		startButton.setText("Start game");
		startButtonBBox = new Rectangle(0, 0, startButton.getWidth(), startButton.getHeight());

		quitButton = new TextButton("Quit", textButtonStyle);
		quitButton.setText("quit");
		quitButtonBBox = new Rectangle(0, 0, quitButton.getWidth(), quitButton.getHeight());

		// Set buttons.
		startButton.setPosition(-(startButton.getWidth() / 2), -(startButton.getHeight() / 2));
		startButtonBBox.setPosition(startButton.getX(), startButton.getY());
		quitButton.setPosition(-(quitButton.getWidth() / 2), (startButton.getY() - startButton.getHeight()) - 10);
		quitButtonBBox.setPosition(quitButton.getX(), quitButton.getY());

		assetsLoaded = true;
		AsyncAssetLoader.freeInstance();
	}
}
