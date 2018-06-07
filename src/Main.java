import java.awt.Color;

import Controller.HexController;
import Controller.HyperHex;
import Model.HexModel;
import Strategy.Strategy;
import View.HexView;
import Model.Cell;

/**
 * Abomnes Gauthier
 * Bretheau Yann
 * S3C
 */

public class Main {

	public Main(){
		// Poue les valeur de playerblue et playerred :0 = Joueur humain au clic de souris et 1 = joueur aléatoire
		// Le joueur bleu commence
		int playerblue = 0;
		int playerred = 1;
		int size = 9;
		//Creation du model
		HexModel model = new HexModel(size);
		//Creation de la vue
		HexView view = new HexView(model,"Jeu de Hex");
		
		//Creation du controller
		//HexController controller = new HexController(model,view,playerblue,playerred);
		HyperHex controller = new HyperHex(model, view, playerblue, playerred);
		
		while(!model.getFinished()){
			System.out.print("");
			if(!model.getVictory()){
				if(playerblue==1 && model.getPlayer() == Color.BLUE && model.getCurrentGame()){
					Cell c = controller.play();
					System.out.println("Bleu HyperHex : Cell("+c.getX()+" "+c.getY()+")");
					c.setColor(Color.BLUE);
					model.setPlayer(Color.RED);
					controller.setLastCell(c);
					model.setTour(model.getTour() + 1);
					model.researchVictory(0,1);
				}
				else if(playerred==1 && model.getPlayer() == Color.RED && model.getCurrentGame()){
					Cell c = controller.play();
					System.out.println("Rouge HyperHex : Cell("+c.getX()+" "+c.getY()+")");
					c.setColor(Color.RED);
					model.setPlayer(Color.BLUE);
					controller.setLastCell(c);
					model.setTour(model.getTour() + 1);
					model.researchVictory(1,0);
				}
			}
		}
	}

	public static void main(String[] args) {
		new Main();
	}
}
