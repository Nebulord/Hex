package Strategy;

import Model.Cell;
import Model.HexModel;

public class LinkStrategy extends Strategy {
	private Cell headLink;
	private Cell endLink;
	private String direction;
	
	public LinkStrategy(Cell head, Cell end, String d) {
		headLink = head;
		endLink = end;
		direction = d;
	}
	
	public Cell getHeadLink() {
		return headLink;
	}

	public void setHeadLink(Cell headLink) {
		this.headLink = headLink;
	}

	public Cell getEndLink() {
		return endLink;
	}

	public void setEndLink(Cell endLink) {
		this.endLink = endLink;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public Cell play(HexModel model) {
		return endLink;
	}
}
