package game.states;

import java.util.Random;
import java.util.Vector;

import org.newdawn.slick.*;
import org.newdawn.slick.state.*;
import org.newdawn.slick.state.transition.EmptyTransition;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;
import org.newdawn.slick.state.transition.SelectTransition;

import game.characters.heroes.*;
import game.characters.monsters.Monster;
import game.characters.monsters.Teru;
import game.util.Healthbar;

public class Battle extends BasicGameState {
	Hero hero = Slayer.slayer;
	Image battleMap;
	Music music;
	Sound hitSound;
	static final int DEFAULT_ENEMYCOUNT = 0;
	int enemyCount = 0;
	Vector<Monster> enemies;

	public Battle(int state) {
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		super.enter(container, game);
		hero.stopAttack();
		hero.battleFaceRight();
		int enemyCount = new Random().nextInt(2) + 1;
		this.enemyCount = enemyCount;

		enemies = new Vector<Monster>();

		for (int i = 0; i < enemyCount; i++) {
			addRandomEnemy();
		}

		hero.reset();
		// sound.play();
		music.loop();
	}

	@Override
	public void leave(GameContainer container, StateBasedGame game) throws SlickException {
		super.leave(container, game);
		enemyCount = DEFAULT_ENEMYCOUNT;
		enemies = null;
	}

	public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
		music = new Music("sounds/one/back3.ogg");
		hitSound = new Sound("sounds/one/impact1.wav");
		battleMap = new Image("res/backgrounds800x600/2.png");
		hero.setHealthbar(new Healthbar(hero.getInfo().getMaxHp(), 30,
				500, hero.getInfo().getWidthBattle()));
		hero.battleFaceRight();
	}

	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
		battleMap.draw(0, 0);
		g.drawString("HP: ", hero.getHealthbar().getX() - 40, hero.getHealthbar().getY() - 2);
		hero.render(gc, sbg, g);
		for (Monster m : enemies) {
			m.render(gc, sbg, g);
		}
	}

	public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {
		hero.update(gc, sbg, delta);
		for (Monster m : enemies) {
			m.update(gc, sbg, delta);
		}

		if (hero.hurtboxIsSpawned() == true) {
			for (Monster m : enemies) {
				if (hero.getHurtbox().getBounds().intersects(m.getHitbox().getBounds()) && m.isHit() == false
						&& m.isAlive() == true) {
					hero.attack(m);
					if (m.isAlive() == false) {
						enemyCount--;
					}
					hitSound.play();
				}
			}
		}
		for (Monster m : enemies) {
			if (m.hurtboxIsSpawned() == true) {
				if (m.getHurtbox().getBounds().intersects(hero.getHitbox().getBounds()) && hero.isHit() == false
						&& hero.isAlive() == true) {
					m.attack(hero);
				}
			}
		}
		if(hero.isAlive() == false){
			//TODO flash gameover screen here
		}
		if (enemyCount == 0) {
			sbg.enterState(3, new FadeOutTransition(), new EmptyTransition());
		}

	}

	private void addRandomEnemy() throws SlickException {
		int enemyType = new Random().nextInt(1);
		Monster m = (enemyType == 0) ? new Teru() : new Teru();
		((Teru) m).setMonsterSheets();
		m.battleFaceLeft();
		enemies.add(m);
	}

	public int getID() {
		return 4;
	}
}
