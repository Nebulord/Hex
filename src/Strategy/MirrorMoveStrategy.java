package Strategy;

import Model.Cell;
import Model.HexModel;

public class MirrorMoveStrategy extends Strategy {
	private Cell lastEnemyCell;
	
	public MirrorMoveStrategy(Cell c) {
		lastEnemyCell = c;
	}

	public Cell play(HexModel model) { //Bloque maillons adverses et forme les siens
		if(lastEnemyCell.getX() >= 5)
			return model.grid.getCell(lastEnemyCell.getX() - 2, lastEnemyCell.getY() + 1);
		else
			return model.grid.getCell(lastEnemyCell.getX() + 2, lastEnemyCell.getY() - 1);
	}

}
