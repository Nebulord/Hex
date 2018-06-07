package Strategy;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import Model.Cell;
import Model.HexModel;

//Joue sur une cellule de la diagonale courte disposée à faire un maillon
public class FirstMoveStrategy extends Strategy {

	public Cell play(HexModel model){ 
		return model.grid.getCell(Math.round(model.grid.getNbLines() / 2), Math.round(model.grid.getNbColumns() / 2));
	}

}
