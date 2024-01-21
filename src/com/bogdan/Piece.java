package com.bogdan;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class Piece {
	private List<Coord> piecePartsCoords;

	public Piece(List<Coord> piecePartsCoords) {
		this.piecePartsCoords = piecePartsCoords;
	}
	List<Coord> getPieceParts() {
		return piecePartsCoords;
	}
	public List<Piece> getAllAbsolutePlacementsToFill(Map map, Coord emptyMapPosition) {
		List<Piece> toReturn = new ArrayList<>();
		for (Coord piecePartCoord : piecePartsCoords) {

			List<Coord> absolutePieceCoords = piecePartsCoords.stream().map(piecePart -> {
				return piecePart.relative(piecePartCoord.invert()).relative(emptyMapPosition);
			}).collect(Collectors.toList());


			boolean isValid = absolutePieceCoords.stream().allMatch(map::isValid);
			if (!isValid) {
				continue;
			}

			boolean coversGivenCoord = absolutePieceCoords.stream().anyMatch(emptyMapPosition::equal);
			if (!coversGivenCoord) {
				continue;
			}

			boolean canPlacePieces = absolutePieceCoords.stream().allMatch(map::isEmpty);
			if (canPlacePieces) {
				toReturn.add(new Piece(absolutePieceCoords));
			}	
		}
		return toReturn;
	}
}