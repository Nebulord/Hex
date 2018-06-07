package Strategy;


import java.awt.Color;

import Model.Cell;
import Model.HexModel;

public class CompleteLinkStrategy extends Strategy {
	private Cell linkOne;
	private Cell linkTwo;
	
	public CompleteLinkStrategy(Cell one, Cell two) {
		linkOne = one;
		linkTwo = two;
	}

	public Cell play(HexModel model) {
		if(linkOne.getColor().equals(Color.WHITE))
			return linkOne;
		else
			return linkTwo;
	}

}
