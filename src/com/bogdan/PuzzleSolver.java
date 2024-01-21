package com.bogdan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;


public class PuzzleSolver {
	
	private static List<Piece> pieces = Arrays.asList(new Piece[]{
		new Piece(Arrays.asList(new Coord(0,0,0), new Coord(1,0,0), new Coord(2,0,0), new Coord(1,1,0))),
		new Piece(Arrays.asList(new Coord(0,0,0), new Coord(0,1,0), new Coord(0,2,0), new Coord(1,1,0))),
		new Piece(Arrays.asList(new Coord(-1,1,0), new Coord(0,1,0), new Coord(1,1,0), new Coord(0,0,0))),
		new Piece(Arrays.asList(new Coord(1,-1,0), new Coord(1,0,0), new Coord(1,1,0), new Coord(0,0,0))),
		new Piece(Arrays.asList(new Coord(-1,0,1), new Coord(0,0,1), new Coord(1,0,1), new Coord(0,0,0))),
		new Piece(Arrays.asList(new Coord(0,-1,1), new Coord(0,0,1), new Coord(0,1,1), new Coord(0,0,0))),
		new Piece(Arrays.asList(new Coord(0,0,0), new Coord(0,0,1), new Coord(0,0,2), new Coord(1,0,1))),
		new Piece(Arrays.asList(new Coord(0,0,0), new Coord(0,0,1), new Coord(0,0,2), new Coord(0,1,1))),
		new Piece(Arrays.asList(new Coord(1,0,0), new Coord(1,0,1), new Coord(1,0,2), new Coord(0,0,1))),
		new Piece(Arrays.asList(new Coord(0,1,0), new Coord(0,1,1), new Coord(0,1,2), new Coord(0,0,1)))
	});
	

	public static void main(String[] args) {
		MapRenderer mapRenderer = new MapRenderer();
		
		Map emptyMap = new Map();
		List<Map> explortionFrontier = new ArrayList<>();
		explortionFrontier.add(emptyMap);
		
		Set<Integer> exploredMapsHashesSet = new HashSet<>();
		exploredMapsHashesSet.add(emptyMap.hashCodeSimetric());

		while(!explortionFrontier.isEmpty()) {
			Map map = explortionFrontier.remove(explortionFrontier.size() - 1);

			Optional<Coord> firstEmptyPositionOpt = map.findEmptyPosition();
			if (firstEmptyPositionOpt.isEmpty()) {
				continue;
			}

			for (Piece piece : pieces) {
				List<Piece> piecesToFill = piece.getAllAbsolutePlacementsToFill(map, firstEmptyPositionOpt.get());
				//Collections.shuffle(placementsToFit);
				for (Piece pieceToFill : piecesToFill) {
					Map newMap = map.duplicate();
					newMap.placePieces(pieceToFill);
					
					int hash = newMap.hashCodeSimetric();

					if (exploredMapsHashesSet.contains(hash)) {
						// go on.
					} else if (newMap.isDone()) {
						mapRenderer.render(newMap);
						System.out.println(newMap);
						return;
					} else if (newMap.isDoable(pieces)) {
						mapRenderer.render(newMap);
						exploredMapsHashesSet.add(hash);
						explortionFrontier.add(newMap);
					}
				}
			}
		}
	}
}
