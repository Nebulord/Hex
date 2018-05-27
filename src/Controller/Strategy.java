package Controller;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import Model.Cell;
import Model.HexModel;

public class Strategy {

	public static Cell firstMoveStrategy(HexModel model){ //Joue sur une cellule aléatoire de la diagonale courte
		ArrayList<Cell> free = new ArrayList<Cell>();
		Random rand = new Random();
		int i = 2;
		int j = model.grid.getNbColumns() - 3;
		while(i < model.grid.getNbLines() && j > 1) {
			Cell c = model.grid.getCell(i, j);
			if(c.getColor() == Color.WHITE){
				free.add(c);
			}
			i++;
			j--;
		}
		Cell c = free.get(rand.nextInt(free.size()));	
         
		return c;
	}
	
	public static Cell secondMoveStrategy(HexModel model){ //Bloque maillons adverses et forme les siens
		ArrayList<Cell> free = new ArrayList<Cell>();
		Random rand = new Random();
		int i = 2;
		int j = model.grid.getNbColumns() - 3;
		while(i < model.grid.getNbLines() && j > 1) {
			Cell c = model.grid.getCell(i, j);
			if(c.getColor() == Color.WHITE){
				free.add(c);
			}
			i++;
			j--;
		}
		Cell c = free.get(rand.nextInt(free.size()));	
         
		return c;
	}
	
	public static Cell randomPlay(HexModel model, Cell enemyCell){
		ArrayList<Cell>  free = new ArrayList<Cell>();
		Random rand = new Random();
		for (Cell c : model.grid) {
			if(c.getColor() == Color.WHITE){
				free.add(c);
			}
		}
		Cell c = free.get(rand.nextInt(free.size()));	
         
		return c;
	}
}
