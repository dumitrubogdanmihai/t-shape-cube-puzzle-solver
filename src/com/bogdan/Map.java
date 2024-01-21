package com.bogdan;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

class Map {
	private static final int LENGTH = 6;
	private int[][][] map;
	private int lastPieceIndex;

	Map() {
		 this.map = new int[LENGTH + 1][LENGTH + 1][LENGTH + 1];
		 this.lastPieceIndex = 0;
	}

	Map(int[][][] map, int lastPieceIndex) {
		this.map = new int[map.length][map.length][map.length];
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map.length; j++) {
				for (int k = 0; k < map.length; k++) {
					this.map[i][j][k] = map[i][j][k];
				}
			}
		}
		this.lastPieceIndex = lastPieceIndex;
	}

	public Optional<Coord> findEmptyPosition() {
		for (int i = 0; i < LENGTH; i++) {
			for (int j = 0; j < LENGTH; j++) {
				for (int k = 0; k < LENGTH; k++) {
					if (map[k][j][i] == 0) {
						return Optional.of(new Coord(k, j, i));
					}
				}
			}
		}
		return Optional.empty();
	}

	public Map duplicate() {
		return new Map(map, this.lastPieceIndex);
	}


	public boolean isDoable(List<Piece> pieces) {
		for (int i = 0; i < LENGTH; i++) {
			for (int j = 0; j < LENGTH; j++) {
				for (int k = 0; k < LENGTH; k++) {
					Coord coord = new Coord(i,j,k);
					if (isEmpty(coord)) { 
						boolean canBeFilled = false;
						
						for (Piece piece : pieces) {
							List<Piece> absolutePlacementsToFill = piece.getAllAbsolutePlacementsToFill(this, coord);
							if (absolutePlacementsToFill.isEmpty()) {
								continue;
							} else {
								canBeFilled = true;
								break;
							}
						}
						
						if (!canBeFilled) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	public boolean isDone() {
		for (int i = 0; i < LENGTH; i++) {
			for (int j = 0; j < LENGTH; j++) {
				for (int k = 0; k < LENGTH; k++) {
					if (map[i][j][k] == 0) {
						return false;
					}
				}
			}
		}
		return true;
	}

	public boolean isEmpty(Coord coord) {
		return isValid(coord) && map[coord.x][coord.y][coord.z] == 0;
	}

	public boolean isValid(Coord coord) {
		return coord.x >= 0 && coord.y >= 0 && coord.z >= 0
				&& coord.x < LENGTH && coord.y < LENGTH && coord.z < LENGTH;
	}

	public void placePieces(Piece piece) {
		lastPieceIndex++;
		for (Coord chunkCoord : piece.getPieceParts()) {
			map[chunkCoord.x][chunkCoord.y][chunkCoord.z] = lastPieceIndex;
		}
	}

	public void rotate() {
		for (int k = 0; k < LENGTH; k++) {
			for (int i = 0; i < LENGTH / 2; i++) {
				for (int j = i; j < LENGTH - i - 1; j++) {
					int temp = map[i][j][k];
					map[i][j][k] = map[LENGTH - 1 - j][i][k];
					map[LENGTH - 1 - j][i][k] = map[LENGTH - 1 - i][LENGTH - 1 - j][k];
					map[LENGTH - 1 - i][LENGTH - 1 - j][k] = map[j][LENGTH - 1 - i][k];
					map[j][LENGTH - 1 - i][k] = temp;
				}
			}
		}
	}

	public int hashCodeSimetric() {
		Map duplicateMap = this.duplicate();
		int hash1 = duplicateMap.hashCodePieceCodeAgnostic();
	
		duplicateMap.rotate();
		int hash2 = duplicateMap.hashCodePieceCodeAgnostic();
	
		duplicateMap.rotate();
		int hash3 = duplicateMap.hashCodePieceCodeAgnostic();
	
		duplicateMap.rotate();
		int hash4 = duplicateMap.hashCodePieceCodeAgnostic();
	
		Long sum = 0L + hash1 + hash2 + hash3 + hash4;
		return sum.hashCode();
	}

	public int hashCodePieceCodeAgnostic() {
	
		int[][][] mapClone = new int[LENGTH][LENGTH][LENGTH];
		for (int i = 0; i < LENGTH; i++) {
			for (int j = 0; j < LENGTH; j++) {
				for (int k = 0; k < LENGTH; k++) {
					mapClone[i][j][k] = map[i][j][k] == 0 ? 0 : 1;
				}
			}
		}
	
	
		return Arrays.deepHashCode(mapClone);
	}

	@Override
	public String toString() {
		String collector = "";
		
		for (int i = 0; i < LENGTH; i++) {
			for (int j = 0; j < LENGTH; j++) {
				for (int k = 0; k < LENGTH; k++) {
					collector += "map[" + i + "][" + j + "][" + k + "]=" +  map[i][j][k] + ";\n";
				}
			}
		}
	
		collector += "\n";
		collector += "\n";
	
		for (int piece = lastPieceIndex; piece >= 0; piece--) {
			collector += "piece: " + piece + "\n";
			for (int i = 0; i < LENGTH; i++) {
				for (int j = 0; j < LENGTH; j++) {
					for (int k = 0; k < LENGTH; k++) {
						if (map[i][j][k] == piece) {
							collector += "map[" + i + "][" + j + "][" + k + "]=" +  map[i][j][k] + ";\n";
	
						}
					}
				}
			}
		}
	
	
		return collector;
	
	}

	public int[][][] getMap() {
		return map;
	}

	public void removePiecesAbove(int piece) {
		for (int i = 0; i < LENGTH; i++) {
			for (int j = 0; j < LENGTH; j++) {
				for (int k = 0; k < LENGTH; k++) {
					if (map[i][j][k] > piece) {
						map[i][j][k] = 0;
					}
				}
			}
		}
	}
}