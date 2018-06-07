package Strategy;

import Model.Cell;
import Model.HexModel;

public abstract class Strategy {
	
	public abstract Cell play(HexModel model);
}
