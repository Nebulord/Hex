package Controller;

import Model.Cell;
import Model.HexModel;
import Strategy.Strategy;
import View.HexView;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

import Mind.Mind;

/**
 * Chloé HETYEI
 * Matthieu CABANES
 * S4A
 */

public class HyperHex implements ActionListener,MouseListener{

	private HexModel model;
	private HexView view;
	private int playerblue = 0;
	private int playerred = 0;
	private Mind brain;
	private Cell lastEnemyCell; //dernière cellule jouée par l'ennemi
	private Cell lastCell; //dernière cellule jouée par HyperHex

	/** ************
	 *
	 *  Constructeur
	 *
	 *  ************
	 */

	public HyperHex(HexModel model, HexView view,int playerblue,int playerred){
		this.model = model;
		this.view = view;
		this.playerred = playerred;
		this.playerblue = playerblue;
		view.pMenu.bPlay.addActionListener(this);
		view.pMenu.bReset.addActionListener(this);
		view.pMenu.bQuit.addActionListener(this);

		view.pGame.addMouseListener(this);
		view.pGame.bReturn.addActionListener(this);

		view.pVictory.panel.bReturn.addActionListener(this);
		brain = new Mind(model, this);
	}

	public HyperHex(HexModel model, HexView view){
		this.model = model;
		this.view = view;

		view.pMenu.bPlay.addActionListener(this);
		view.pMenu.bReset.addActionListener(this);
		view.pMenu.bQuit.addActionListener(this);

		view.pGame.addMouseListener(this);
		view.pGame.bReturn.addActionListener(this);

		view.pVictory.panel.bReturn.addActionListener(this);
		brain = new Mind(model, this);
	}

	public Cell getLastCell() {
		return lastCell;
	}

	public void setLastCell(Cell lastCell) {
		this.lastCell = lastCell;
	}
	
	public Cell getLastEnemyCell() {
		return lastEnemyCell;
	}

	public void setLastEnemyCell(Cell lastCell) {
		this.lastEnemyCell = lastCell;
	}


	public Mind getBrain() {
		return brain;
	}

	public void setBrain(Mind brain) {
		this.brain = brain;
	}

	/** ******************
	 *
	 *  Action des boutons
	 *
	 *  Les attributs du model enJeu et enCours permettent de connaitre l'Ã©tat du jeu.
	 *  L'attribut enJeu permet de diffÃ©rencier le panel du menu et du jeu et l'attribut enCours permet de savoir si une partie est en cours.
	 *  GrÃ¢ce Ã  l'attribut enCours on peut proposer diffÃ©rente action dans le menu, comme une action de reset si une partie est en cours.
	 *
	 *  ******************
	 */

	@Override
	public void actionPerformed(ActionEvent e) {
		/**
		 * Action dans la fenÃªtre du menu sans que le jeu sois lancÃ©
		 */
		if (!model.getInGame() && !model.getCurrentGame()) {
			// Si on clique sur le bouton play le jeu se lance
			if (e.getSource() == view.pMenu.bPlay) {
				model.setInGame(true);
				model.setCurrentGame(true);
			}
			else if (e.getSource() == view.pMenu.bQuit) {
				// Si on clique sur le bouton quitter on quitte le jeu
				view.dispose();
			}
		}

		/**
		 * Action dans la fenêtre du menu avec le jeu lancé
		 */
		else if (!model.getInGame() && model.getCurrentGame()){
			// Si on clique sur le bouton play on retourne au jeu et on continue la partie en cours
			if (e.getSource() == view.pMenu.bPlay) {
				model.setInGame(true);
				model.setCurrentGame(true);
			}
			else if (e.getSource() == view.pMenu.bReset){
				// Si on clique sur le bouton reset on relance une nouvelle partie
				model.rebuild();
				model.setInGame(true);
				model.setCurrentGame(true);
			}
			else if (e.getSource() == view.pMenu.bQuit) {
				// Si on clique sur le bouton quitter on quitte le jeu
				model.setFinished(true);
				view.dispose();
			}
		}

		/**
		 * Action dans la fenêtre de jeu
		 */
		else if (model.getInGame() && model.getCurrentGame()) {
			// Si on clique sur le bouton retour on retourne au menu et la partie est toujours en cours, c'est Ã  dire qu'on pourra y revenir
			// en appuyant sur play dans le menu
			if (e.getSource() == view.pGame.bReturn) {
				model.setInGame(false);
				model.setCurrentGame(true);
				view.pVictory.setVisible(false);
			}
			// Si on clique sur le bouton retour de la fenÃªtre de victoire on retourne au menu, ce qui arrÃªte la partie en cours puisqu'on a eu un gagant
			if(e.getSource() == view.pVictory.panel.bReturn) {
				view.pVictory.setVisible(false);
				model.rebuild();
			}
		}
	}
	
	//HyperHex joue
	public Cell play() {
		Strategy s = brain.defineStrategy(); //HyperHex recherche la meilleure stratégie à adopter
		return s.play(model); //HyperHex joue
	}
	
	/** **************************
	 *
	 *  Action de la souris en jeu
	 *
	 *  **************************
	 */

	@Override
	public void mouseClicked(MouseEvent e) {
		if (model.getInGame())
		{
			model.setTour(model.getTour() + 1);
			// On recupere les coordonnees du clique de la souris
			float x = e.getX();
			float y = e.getY();
			/**
			 * Pour toutes les cellules de la grid si les coordonnÃ©es de la souris sont comprises dans une cellule de la grid alors on change la couleur
			 * de cette cellule avec la couleur du joueur en courant.
			 * On appel aussi la mÃ©thode victoire du model aprÃ¨s chaque clique du joueur. On test la victoire par rapport a la couleur du joueur.
			 */
			for (Cell c : model.grid) {
				if (c.contains(x,y) && c.getColor() == Color.WHITE){
					if(model.getPlayer() == Color.BLUE){
						
						if (playerblue ==0){
							System.out.println("Bleu humain : Cell("+c.getX()+" "+c.getY()+")");
							c.setColor(Color.BLUE);
							setLastEnemyCell(c);
						
							// On test la victoire pour le joueur bleu, c'est Ã  dire en partant de la cellule bleu en 0,1
						
							model.setPlayer(Color.RED);
							model.researchVictory(0,1);   
						}
					}
					else{ 
						
						if(playerred == 0){
							System.out.println("Rouge humain : Cell("+c.getX()+" "+c.getY()+")");
							c.setColor(Color.RED);
							setLastEnemyCell(c);
							model.setPlayer(Color.BLUE);
							// On test la victoire pour le joueur rouge, c'est Ã  dire en partant de la cellule rouge en 1,0
							model.researchVictory(1,0);
						}
					}
				}

			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}
}
