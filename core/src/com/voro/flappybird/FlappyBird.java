package com.voro.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {
	final float TUBE_VELOCITY = 8;
	final int NUMBER_TUBES = 4;
	final float GAP = 400f ;
	final float GRAVITY = 1;

	SpriteBatch batch;
	Texture background;
	Texture gameOver;
	Texture[] birds;
	Texture topTube;
	Texture bottomTube;
	BitmapFont font;

	int wingState = 0;
	float birdY = 0;
	float velocity = 0;
	int score = 0;
	int scoringTube = 0;
	int gameState = 0;
	int soundState = 0;

	Random randomGenerator;

	float tubeX[] = new float[NUMBER_TUBES];
	float tubeOffset[] = new float[NUMBER_TUBES];
	float distanceBetweenTubes;

	Circle birdCircle;
	Rectangle[] topTubeRectangles;
	Rectangle[] bottomTubeRectangles;

	Sound wing ;
	Sound hit;

    void startGame(){
        birdY = Gdx.graphics.getHeight() / 2 - birds[wingState].getHeight() / 2;
        for (int i = 0; i < NUMBER_TUBES; i++){
            tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) *  (Gdx.graphics.getHeight() - GAP -200);
            tubeX[i]= Gdx.graphics.getWidth()/2 - bottomTube.getWidth()/2 + Gdx.graphics.getWidth()/2 + i * distanceBetweenTubes;

            topTubeRectangles[i] = new Rectangle();
            bottomTubeRectangles[i] = new Rectangle();
        }
        score = 0;
        scoringTube = 0;
        velocity = 0;
        soundState = 0;
    }

	@Override
	public void create() {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		gameOver = new Texture("flappy.png");
		topTubeRectangles = new Rectangle[NUMBER_TUBES];
		bottomTubeRectangles = new Rectangle[NUMBER_TUBES];
		birdCircle  = new Circle();

		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);

		birds = new Texture[2];
		birds[0] = new Texture("bird.png");
		birds[1] = new Texture("bird2.png");

		topTube = new Texture("toptube.png");
		bottomTube = new Texture("bottomtube.png");
		randomGenerator = new Random();
		distanceBetweenTubes = Gdx.graphics.getWidth()/2;


		wing = Gdx.audio.newSound(Gdx.files.internal("sfx_wing.ogg"));
		hit = Gdx.audio.newSound(Gdx.files.internal("sfx_hit.ogg"));

        startGame();
	}

	@Override
	public void render() {
		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		if (gameState == 1) {
			if(tubeX[scoringTube] < Gdx.graphics.getWidth()/4 ) {
				score++;
				Gdx.app.log("Score", String.valueOf(score));
				if (scoringTube < NUMBER_TUBES - 1) {
					scoringTube++;
				} else {
					scoringTube = 0;
				}
			}

			for (int i = 0; i < NUMBER_TUBES; i++) {
				if (tubeX[i] < - topTube.getWidth()){
					tubeX[i] += NUMBER_TUBES * distanceBetweenTubes;
					tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) *  (Gdx.graphics.getHeight() - GAP -200);

				}
				tubeX[i] -= TUBE_VELOCITY;

				batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + GAP / 2 + tubeOffset[i]);
				batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - GAP / 2 - bottomTube.getHeight() + tubeOffset[i]);

				topTubeRectangles[i] = new Rectangle(tubeX[i],Gdx.graphics.getHeight() / 2 + GAP / 2 + tubeOffset[i],topTube.getWidth(),topTube.getHeight());
				bottomTubeRectangles[i] =  new Rectangle(tubeX[i],Gdx.graphics.getHeight() / 2 - GAP / 2 - bottomTube.getHeight() + tubeOffset[i],topTube.getWidth(),topTube.getHeight());
			}

			if (Gdx.input.justTouched()) {
				velocity = -15;
				wing.play();

			}
			if (birdY > 0 ) {
				velocity += GRAVITY;
				birdY -= velocity;
			}else{
				gameState = 2;
			}
			if (wingState == 0) {
				wingState = 1;
			} else {
				wingState = 0;
			}

		} else if (gameState== 0){
			if (Gdx.input.justTouched()) {
				gameState = 1;

			}
		}else if(gameState == 2){
			batch.draw(gameOver,Gdx.graphics.getWidth()/2 - gameOver.getWidth()/2,Gdx.graphics.getHeight()/2 - gameOver.getHeight()/2);
			if (Gdx.input.justTouched()) {
				gameState = 1;
			    startGame();
			}
		}
		if (gameState == 2 && soundState == 0){
			hit.play();
			soundState = 1;
		}
		batch.draw(birds[wingState], Gdx.graphics.getWidth() / 4 - birds[wingState].getWidth() / 2, birdY);
		font.draw(batch, String.valueOf(score),100,200);
		batch.end();

		birdCircle.set( Gdx.graphics.getWidth() / 4, birdY + birds[wingState].getHeight()/2,birds[wingState].getWidth()/2 );

		for (int i = 0; i < NUMBER_TUBES; i++){

			if (Intersector.overlaps(birdCircle,topTubeRectangles[i]) || Intersector.overlaps(birdCircle,bottomTubeRectangles[i]))
			{
				gameState = 2;
			}
		}
	}

}