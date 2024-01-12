package map;


public class PowerPrint {

	// add all print methods you need here
	public static void showMatrix(int[][] matrix) {
		// loop through the matrix's rows and print the items separated by commas with enclosing square brackets 
		for (int x = 0; x < matrix.length; x++) {
			System.out.print("[");
			System.out.print(matrix[x][0]);
			for (int y = 1; y < matrix[x].length; y++) {
				System.out.print(" " + matrix[x][y]);
			}
			System.out.println("]");
		}
	}
}