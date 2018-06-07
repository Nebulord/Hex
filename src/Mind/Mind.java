package Mind;

import java.awt.Color;
import java.util.ArrayList;

import Controller.HyperHex;
import Model.Cell;
import Model.HexModel;
import Strategy.*;

public class Mind {
	private int numPlayer;
	private HyperHex hyperhex;
	private HexModel model;
	private boolean chaineFormée = false;
	private LinkStrategy link;
	private Cell linkToJoinOne;
	private Cell linkToJoinTwo;
	private int victory = 0;
	
	public Mind(HexModel m, HyperHex hh) {
		model = m;
		hyperhex = hh;
	}
	
	public Strategy defineStrategy() {
		if(model.getTour() <= 1) //Si HyperHex n'a pas encore joué
			return gameStarts();
		if(!chaineFormée)//On créé la chaine
			createLink();	
		if(linkToComplete()) //Si la chaine est en danger
			return new CompleteLinkStrategy(linkToJoinOne, linkToJoinTwo);
		else if(victory < 2 && rotateLink()){//On vérifie s'il faut inverser la chaine
			if(victory == 1) {
				buildLink();
				return link;
			}
			else
				return finishlink();
		}
		else if(victory == 2) //S'il ne reste plus qu'à compléter la chaine
			return finishlink();
		else if(brokenLinkdetected()) { //Si on ne peut pas créer de nouveaux maillons
			buildNeighbours();
			return new BreakLinkStrategy(link.getEndLink());
		}
		needToRotate();//Vérifie si l'ennemi change de côté pour jouer
		buildLink();//Sinon, on continue de former nos maillons		
		return link;
	}

	private void needToRotate() {
		if(victory == 0) { //Si aucun bout de la chaine n'est encore bouclé
			if(numPlayer == 1) {
				if(hyperhex.getLastEnemyCell().getX() <= Math.round(model.grid.getNbLines() / 2)) { //HyperHex attaque là où se trouve l'ennemi
					Cell c = link.getEndLink();
					link.setEndLink(link.getHeadLink());
					link.setHeadLink(c);
					link.setDirection("TOP");
				}
				else {
					Cell c = link.getEndLink();
					link.setEndLink(link.getHeadLink());
					link.setHeadLink(c);
					link.setDirection("BOTTOM");
				}
			}
			else {
				if(hyperhex.getLastEnemyCell().getY() <= Math.round(model.grid.getNbColumns() / 2)) {
					Cell c = link.getEndLink();
					link.setEndLink(link.getHeadLink());
					link.setHeadLink(c);
					link.setDirection("LEFT");
				}
				else {
					Cell c = link.getEndLink();
					link.setEndLink(link.getHeadLink());
					link.setHeadLink(c);
					link.setDirection("RIGHT");
				}
			}
		}
	}

	private boolean rotateLink() {
		ArrayList<Cell> views = viewLink();
		int i = 0;
		while(i < views.size() && !(views.get(i).getColor().equals(link.getHeadLink().getColor()))) {
			i++;
		}
		if(!(i == views.size()) && victory < 2) {
			victory++;
			Cell c = link.getHeadLink();
			link.setHeadLink(link.getEndLink());
			link.setEndLink(c);
			switch (link.getDirection()) {
			case "TOP":
				link.setDirection("BOTTOM");
				break;
			case "BOTTOM":
				link.setDirection("TOP");
				break;
			case "LEFT":
				link.setDirection("RIGHT");
				break;
			case "RIGHT":
				link.setDirection("LEFT");
				break;
			}
			return true;
		}
		else
			return false;
	}
	
	
	private boolean enemyDestroyLink(Cell c) {
		ArrayList<Cell> comNeighbours = getCommonNeighbours(c, link.getEndLink());
		if(numPlayer == 1) {
			if(comNeighbours.get(0).getColor().equals(Color.RED) || comNeighbours.get(1).getColor().equals(Color.RED))
				return true;
		}
		else {
			if(comNeighbours.get(0).getColor().equals(Color.BLUE) || comNeighbours.get(1).getColor().equals(Color.BLUE))
				return true;
		}
		return false;
	}

	private boolean brokenLinkdetected() {
		int detection = 0;
		ArrayList<Cell> views = viewLink();
		for (Cell c : views) {
			if((numPlayer == 1 && (c.getColor().equals(Color.BLUE) || c.getColor().equals(Color.RED)) 
			|| (numPlayer == 2 && c.getColor().equals(Color.RED) || c.getColor().equals(Color.BLUE))))
				detection++;
			else if(enemyDestroyLink(c))
					detection++;
		}
		if(detection == views.size())
			return true;
		else
			return false;
	}

	private boolean linkToComplete() {
		for (Cell c : model.grid) {
			if(c.getColor().equals(link.getEndLink().getColor())) {//On cherche les cellules de notre couleur
				ArrayList<Cell> views = getAllLinkView(c);
				for (Cell c2 : views) {
					if(c2.getColor().equals(c.getColor())) {//On cherche nos maillons
						ArrayList<Cell> commonNeighbours = getCommonNeighbours(c, c2);//On récupère les voisins communs de nos maillons
						if(numPlayer == 1) { //On vérifie si on est attaqué
							if(commonNeighbours.get(0).getColor().equals(Color.WHITE) && commonNeighbours.get(1).getColor().equals(Color.RED))
								return true;
							else if(commonNeighbours.get(0).getColor().equals(Color.RED) && commonNeighbours.get(1).getColor().equals(Color.WHITE))
								return true;
						}
						else {
							if(commonNeighbours.get(0).getColor().equals(Color.WHITE) && commonNeighbours.get(1).getColor().equals(Color.BLUE))
								return true;
							else if(commonNeighbours.get(0).getColor().equals(Color.BLUE) && commonNeighbours.get(1).getColor().equals(Color.WHITE))
								return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	private Strategy finishlink() {
		for (Cell c : model.grid) {
			if(c.getColor().equals(link.getEndLink().getColor())) {//On cherche les cellules de notre couleur
				ArrayList<Cell> views = getAllLinkView(c);
				for (Cell c2 : views) {
					if(c2.getColor().equals(c.getColor())) {//On cherche nos maillons
						ArrayList<Cell> commonNeighbours = getCommonNeighbours(c, c2);//On récupère les voisins communs de nos maillons
						if(commonNeighbours.get(0).getColor().equals(Color.WHITE) && commonNeighbours.get(1).getColor().equals(Color.WHITE))
							return new CompleteLinkStrategy(linkToJoinOne, linkToJoinTwo);
					}
				}
			}
		}
		return new CompleteLinkStrategy(linkToJoinOne, linkToJoinTwo);
	}

	private Strategy gameStarts() {
		if(model.getTour() == 0) {
			numPlayer = 1;
			return new FirstMoveStrategy();
		}
		else {
			numPlayer = 2;
			return new MirrorMoveStrategy(hyperhex.getLastEnemyCell());
		}
	}
	
	private void createLink() { 
		if(numPlayer == 1) {
			if(hyperhex.getLastEnemyCell().getX() < hyperhex.getLastCell().getX()) //HyperHex attaque là où se trouve l'ennemi
				link = new LinkStrategy(hyperhex.getLastCell(), hyperhex.getLastCell(), "TOP");
			else
				link = new LinkStrategy(hyperhex.getLastCell(), hyperhex.getLastCell(), "BOTTOM");
		}
		else {
			if(hyperhex.getLastEnemyCell().getY() < hyperhex.getLastCell().getY())
				link = new LinkStrategy(hyperhex.getLastCell(), hyperhex.getLastCell(), "LEFT");
			else
				link = new LinkStrategy(hyperhex.getLastCell(), hyperhex.getLastCell(), "RIGHT");
		}
		chaineFormée = true;
	}
	
	private ArrayList<Cell> viewLink() {
		ArrayList<Cell> views = new ArrayList<Cell>();
		switch(link.getDirection()) {
		case "TOP":
			views = getTopLinkView(link.getEndLink());
			break;
		case "BOTTOM":
			views = getBottomLinkView(link.getEndLink());
			break;
		case "LEFT":
			views = getLeftLinkView(link.getEndLink());
			break;
		case "RIGHT":
			views = getRightLinkView(link.getEndLink());
			break;
		}
		return views;
	}
	
	private void buildLink() {
		ArrayList<Cell> links = new ArrayList<Cell>();
		switch(link.getDirection()) {
		case "TOP":
			links = getTopLinks(link.getEndLink());
			break;
		case "BOTTOM":
			links = getBottomLinks(link.getEndLink());
			break;
		case "LEFT":
			links = getLeftLinks(link.getEndLink());
			break;
		case "RIGHT":
			links = getRightLinks(link.getEndLink());
			break;
		}
		link.setEndLink(links.get(0));
	}
	
	private void buildNeighbours() {
		ArrayList<Cell> neighbours = new ArrayList<Cell>();
		switch(link.getDirection()) {
		case "TOP":
			neighbours = getTopNeighbours(link.getEndLink());
			break;
		case "BOTTOM":
			neighbours = getBottomNeighbours(link.getEndLink());
			break;
		case "LEFT":
			neighbours = getLeftNeighbours(link.getEndLink());
			break;
		case "RIGHT":
			neighbours = getRightNeighbours(link.getEndLink());
			break;
		}
		link.setEndLink(neighbours.get(0));
	}
	
	private ArrayList<Cell> getRightNeighbours(Cell c) {
		ArrayList<Cell> neighbours = new ArrayList<Cell>();
		int x = c.getX() - 1;
		int y = c.getY() + 1;
		if(!(model.grid.getCell(x, y) == null) && model.grid.getCell(x, y).getColor().equals(Color.WHITE))
			neighbours.add(model.grid.getCell(x, y));
		x = c.getX();
		y = c.getY() + 1;
		if(!(model.grid.getCell(x, y) == null) && model.grid.getCell(x, y).getColor().equals(Color.WHITE))
			neighbours.add(model.grid.getCell(x, y));
		x = c.getX() + 1;
		y = c.getY();
		if(!(model.grid.getCell(x, y) == null) && model.grid.getCell(x, y).getColor().equals(Color.WHITE))
			neighbours.add(model.grid.getCell(x, y));
		return neighbours;
	}

	private ArrayList<Cell> getLeftNeighbours(Cell c) {
		ArrayList<Cell> neighbours = new ArrayList<Cell>();
		int x = c.getX() - 1;
		int y = c.getY();
		if(!(model.grid.getCell(x, y) == null) && model.grid.getCell(x, y).getColor().equals(Color.WHITE))
			neighbours.add(model.grid.getCell(x, y));
		x = c.getX();
		y = c.getY() - 1;
		if(!(model.grid.getCell(x, y) == null) && model.grid.getCell(x, y).getColor().equals(Color.WHITE))
			neighbours.add(model.grid.getCell(x, y));
		x = c.getX() + 1;
		y = c.getY() - 1;
		if(!(model.grid.getCell(x, y) == null) && model.grid.getCell(x, y).getColor().equals(Color.WHITE))
			neighbours.add(model.grid.getCell(x, y));
		return neighbours;
	}

	private ArrayList<Cell> getBottomNeighbours(Cell c) {
		ArrayList<Cell> neighbours = new ArrayList<Cell>();
		int x = c.getX() + 1;
		int y = c.getY() - 1;
		if(!(model.grid.getCell(x, y) == null) && model.grid.getCell(x, y).getColor().equals(Color.WHITE))
			neighbours.add(model.grid.getCell(x, y));
		x = c.getX() + 1;
		y = c.getY();
		if(!(model.grid.getCell(x, y) == null) && model.grid.getCell(x, y).getColor().equals(Color.WHITE))
			neighbours.add(model.grid.getCell(x, y));
		return neighbours;
	}

	private ArrayList<Cell> getTopNeighbours(Cell c) {
		ArrayList<Cell> neighbours = new ArrayList<Cell>();
		int x = c.getX() - 1;
		int y = c.getY();
		if(!(model.grid.getCell(x, y) == null) && model.grid.getCell(x, y).getColor().equals(Color.WHITE))
			neighbours.add(model.grid.getCell(x, y));
		x = c.getX() - 1;
		y = c.getY() + 1;
		if(!(model.grid.getCell(x, y) == null) && model.grid.getCell(x, y).getColor().equals(Color.WHITE))
			neighbours.add(model.grid.getCell(x, y));
		return neighbours;
	}

	private ArrayList<Cell> getCommonNeighbours(Cell c1, Cell c2) {
		ArrayList<Cell> neighboursC1 = new ArrayList<Cell>();
		ArrayList<Cell> neighboursC2 = new ArrayList<Cell>();
		ArrayList<Cell> commonNeighbours = new ArrayList<Cell>();
		int x = c1.getX() - 1;
		int y = c1.getY();
		if(!(model.grid.getCell(x, y) == null))
			neighboursC1.add(model.grid.getCell(x, y));
		x = c1.getX() - 1;
		y = c1.getY() + 1;
		if(!(model.grid.getCell(x, y) == null))
			neighboursC1.add(model.grid.getCell(x, y));
		x = c1.getX();
		y = c1.getY() - 1;
		if(!(model.grid.getCell(x, y) == null))
			neighboursC1.add(model.grid.getCell(x, y));
		x = c1.getX();
		y = c1.getY() + 1;
		if(!(model.grid.getCell(x, y) == null))
			neighboursC1.add(model.grid.getCell(x, y));
		x = c1.getX() + 1;
		y = c1.getY() - 1;
		if(!(model.grid.getCell(x, y) == null))
			neighboursC1.add(model.grid.getCell(x, y));
		x = c1.getX() + 1;
		y = c1.getY();
		if(!(model.grid.getCell(x, y) == null))
			neighboursC1.add(model.grid.getCell(x, y));
		
		x = c2.getX() - 1;
		y = c2.getY();
		if(!(model.grid.getCell(x, y) == null))
			neighboursC2.add(model.grid.getCell(x, y));
		x = c2.getX() - 1;
		y = c2.getY() + 1;
		if(!(model.grid.getCell(x, y) == null))
			neighboursC2.add(model.grid.getCell(x, y));
		x = c2.getX();
		y = c2.getY() - 1;
		if(!(model.grid.getCell(x, y) == null))
			neighboursC2.add(model.grid.getCell(x, y));
		x = c2.getX();
		y = c2.getY() + 1;
		if(!(model.grid.getCell(x, y) == null))
			neighboursC2.add(model.grid.getCell(x, y));
		x = c2.getX() + 1;
		y = c2.getY() - 1;
		if(!(model.grid.getCell(x, y) == null))
			neighboursC2.add(model.grid.getCell(x, y));
		x = c2.getX() + 1;
		y = c2.getY();
		if(!(model.grid.getCell(x, y) == null))
			neighboursC2.add(model.grid.getCell(x, y));
		
		for (Cell ce1 : neighboursC1) {
			for (Cell ce2 : neighboursC2) {
				if(ce1.equals(ce2))
					commonNeighbours.add(ce1);
			}
		}
		linkToJoinOne = commonNeighbours.get(0);
		if(!(commonNeighbours.get(1) == null))
			linkToJoinTwo = commonNeighbours.get(1);
		return commonNeighbours;
	}
	
	private ArrayList<Cell> getAllLinkView(Cell c) {
		ArrayList<Cell> links = new ArrayList<Cell>();
		int x = c.getX() - 2;
		int y = c.getY() + 1;
		if(!(model.grid.getCell(x, y) == null))
			links.add(model.grid.getCell(x, y));
		x = c.getX() - 1;
		y = c.getY() - 1;
		if(!(model.grid.getCell(x, y) == null))
			links.add(model.grid.getCell(x, y));
		x = c.getX() - 1;
		y = c.getY() + 2;
		if(!(model.grid.getCell(x, y) == null))
			links.add(model.grid.getCell(x, y));
		x = c.getX() + 1;
		y = c.getY() - 2;
		if(!(model.grid.getCell(x, y) == null))
			links.add(model.grid.getCell(x, y));
		x = c.getX() + 1;
		y = c.getY() + 1;
		if(!(model.grid.getCell(x, y) == null))
			links.add(model.grid.getCell(x, y));
		x = c.getX() + 2;
		y = c.getY() - 1;
		if(!(model.grid.getCell(x, y) == null))
			links.add(model.grid.getCell(x, y));
		
		return links;
	}
	
	private ArrayList<Cell> getTopLinkView(Cell c) {
		ArrayList<Cell> links = new ArrayList<Cell>();
		int x = c.getX() - 2;
		int y = c.getY() + 1;
		if(!(model.grid.getCell(x, y) == null))
			links.add(model.grid.getCell(x, y));
		x = c.getX() - 1;
		y = c.getY() - 1;
		if(!(model.grid.getCell(x, y) == null))
			links.add(model.grid.getCell(x, y));
		x = c.getX() - 1;
		y = c.getY() + 2;
		if(!(model.grid.getCell(x, y) == null))
			links.add(model.grid.getCell(x, y));
		return links;
	}
	
	private ArrayList<Cell> getRightLinkView(Cell c) {
		ArrayList<Cell> links = new ArrayList<Cell>();
		int x = c.getX() - 1;
		int y = c.getY() + 2;
		if(!(model.grid.getCell(x, y) == null))
			links.add(model.grid.getCell(x, y));
		x = c.getX() + 1;
		y = c.getY() + 1;
		if(!(model.grid.getCell(x, y) == null))
			links.add(model.grid.getCell(x, y));
		
		return links;
	}
	
	private ArrayList<Cell> getLeftLinkView(Cell c) {
		ArrayList<Cell> links = new ArrayList<Cell>();
		int x = c.getX() - 1;
		int y = c.getY() - 1;
		if(!(model.grid.getCell(x, y) == null))
			links.add(model.grid.getCell(x, y));
		x = c.getX() + 1;
		y = c.getY() - 2;
		if(!(model.grid.getCell(x, y) == null))
			links.add(model.grid.getCell(x, y));
		
		return links;
	}
	
	private ArrayList<Cell> getBottomLinkView(Cell c) {
		ArrayList<Cell> links = new ArrayList<Cell>();
		int x = c.getX() + 1;
		int y = c.getY() - 2;
		if(!(model.grid.getCell(x, y) == null))
			links.add(model.grid.getCell(x, y));
		x = c.getX() + 1;
		y = c.getY() + 1;
		if(!(model.grid.getCell(x, y) == null))
			links.add(model.grid.getCell(x, y));
		x = c.getX() + 2;
		y = c.getY() - 1;
		if(!(model.grid.getCell(x, y) == null))
			links.add(model.grid.getCell(x, y));
		
		return links;
	}
	
	private ArrayList<Cell> getTopLinks(Cell c) {
		ArrayList<Cell> links = new ArrayList<Cell>();
		int x = c.getX() - 2;
		int y = c.getY() + 1;
		if(!(model.grid.getCell(x, y) == null) && model.grid.getCell(x, y).getColor().equals(Color.WHITE) && !enemyDestroyLink(model.grid.getCell(x, y)))
			links.add(model.grid.getCell(x, y));
		x = c.getX() - 1;
		y = c.getY() - 1;
		if(!(model.grid.getCell(x, y) == null) && model.grid.getCell(x, y).getColor().equals(Color.WHITE) && !enemyDestroyLink(model.grid.getCell(x, y)))
			links.add(model.grid.getCell(x, y));
		x = c.getX() - 1;
		y = c.getY() + 2;
		if(!(model.grid.getCell(x, y) == null) && model.grid.getCell(x, y).getColor().equals(Color.WHITE) && !enemyDestroyLink(model.grid.getCell(x, y)))
			links.add(model.grid.getCell(x, y));
		return links;
	}
	
	private ArrayList<Cell> getRightLinks(Cell c) {
		ArrayList<Cell> links = new ArrayList<Cell>();
		int x = c.getX() - 1;
		int y = c.getY() + 2;
		if(!(model.grid.getCell(x, y) == null) && model.grid.getCell(x, y).getColor().equals(Color.WHITE) && !enemyDestroyLink(model.grid.getCell(x, y)))
			links.add(model.grid.getCell(x, y));
		x = c.getX() + 1;
		y = c.getY() + 1;
		if(!(model.grid.getCell(x, y) == null) && model.grid.getCell(x, y).getColor().equals(Color.WHITE) && !enemyDestroyLink(model.grid.getCell(x, y)))
			links.add(model.grid.getCell(x, y));
		
		return links;
	}
	
	private ArrayList<Cell> getLeftLinks(Cell c) {
		ArrayList<Cell> links = new ArrayList<Cell>();
		int x = c.getX() - 1;
		int y = c.getY() - 1;
		if(!(model.grid.getCell(x, y) == null) && model.grid.getCell(x, y).getColor().equals(Color.WHITE) && !enemyDestroyLink(model.grid.getCell(x, y)))
			links.add(model.grid.getCell(x, y));
		x = c.getX() + 1;
		y = c.getY() - 2;
		if(!(model.grid.getCell(x, y) == null) && model.grid.getCell(x, y).getColor().equals(Color.WHITE) && !enemyDestroyLink(model.grid.getCell(x, y)))
			links.add(model.grid.getCell(x, y));
		
		return links;
	}
	
	private ArrayList<Cell> getBottomLinks(Cell c) {
		ArrayList<Cell> links = new ArrayList<Cell>();
		int x = c.getX() + 2;
		int y = c.getY() - 1;
		if(!(model.grid.getCell(x, y) == null) && model.grid.getCell(x, y).getColor().equals(Color.WHITE) && !enemyDestroyLink(model.grid.getCell(x, y)))
			links.add(model.grid.getCell(x, y));
		x = c.getX() + 1;
		y = c.getY() + 1;
		if(!(model.grid.getCell(x, y) == null) && model.grid.getCell(x, y).getColor().equals(Color.WHITE) && !enemyDestroyLink(model.grid.getCell(x, y)))
			links.add(model.grid.getCell(x, y));
		x = c.getX() + 1;
		y = c.getY() - 2;
		if(!(model.grid.getCell(x, y) == null) && model.grid.getCell(x, y).getColor().equals(Color.WHITE) && !enemyDestroyLink(model.grid.getCell(x, y)))
			links.add(model.grid.getCell(x, y));
		
		System.out.println(links.size());
		return links;
	}
}
