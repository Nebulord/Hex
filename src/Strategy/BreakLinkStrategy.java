package Strategy;

import Model.Cell;
import Model.HexModel;

public class BreakLinkStrategy extends Strategy {
	private Cell broken;
	
	public BreakLinkStrategy(Cell c) {
		broken = c;
	}

	public Cell play(HexModel model) {
		return broken;
	}

}
