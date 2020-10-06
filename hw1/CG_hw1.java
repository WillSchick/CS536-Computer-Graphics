import java.io.*; // import file handling
import java.util.*; // Import our java utilities

/*
private class point{

	public point(Double x1, Double y1, Double z1) {
		x = x1;
		y = y1;
		z = z1;
	}
}
*/ 

public class CG_hw1{

	// Returns true if found any errors
	public static boolean errorCheck(String[] args) {
		// Create a list of strings containing valid CLI args
		List<String> validArgs = new ArrayList<String>();
		validArgs.add("-f");
		validArgs.add("-n");
		validArgs.add("-r");

		// Check to make sure we have an EVEN number of args (even ensures pairs!)
		if (args.length%2 != 0 ) {
			System.out.println("Improper # of args");	
			return true;
		} 
		
		// For each pair of args, check to see if the OPTION exists in validArgs
		for (int i = 0; i<args.length/2; i++){
			String arg = args[i*2].toLowerCase();
			if (!validArgs.contains(arg)) {
				System.out.println("Invalid OPTION");
				return true;
			}
		}
		
		return false;
	}	

	// Recursive Casteljau computation
	private static ArrayList<Double> castelRecurs(ArrayList<ArrayList<Double>> points, Double u) {
		// Base Case: If there's only one point left, this is our interpolated coordinate
		if (points.size() <=1) {
			return points.get(0); 
		}

		// Step: 
		// Create a new array to store interpolated coordinates
		ArrayList<ArrayList<Double>> newPoints = new ArrayList<ArrayList<Double>>();

		// Loop through the input points, interpolating and storing values into the new array 
		for (int pointNDX = 0; pointNDX <= points.size()-2; pointNDX++) {		
			// Create a new point to hold interpolated coords
			ArrayList<Double> newPoint = new ArrayList<Double>(3);
			for (int coord = 0; coord <= 2; coord++) {
				Double cordVal1 = points.get(pointNDX).get(coord); 
				Double cordVal2 = points.get(pointNDX+1).get(coord);
				Double newVal = cordVal1 + ((cordVal2 - cordVal1)*u);
				
				// Round and add new val
				newVal = (double)Math.round(newVal * 1000000d) / 1000000d;
				newPoint.add(newVal);
			}
			// Add this new interpolated point to our new array
			newPoints.add(newPoint);
		}

		//Recursive Call:
		return castelRecurs(newPoints, u);
	}

	//// The de Casteljau Algorithm
	// Input is a list of points (stored as doubles), outputs points on a Bezier curve
	public static ArrayList<ArrayList<Double>> casteljau(
			ArrayList<ArrayList<Double>> controlPoints,
			int n) {

		ArrayList<ArrayList<Double>> bezierPoints = new ArrayList<ArrayList<Double>>();	

		// Create n+1 points, thus creating n line segments
		for (int i = 0; i <= n; i++) {
			// Create point on the bezier curve
			ArrayList<Double> point = new ArrayList<Double>();

			// Call our recursive computation
			Double u = Double.valueOf(i)/n;
			point = castelRecurs(controlPoints, u);

			// Add our new point to the Bezier points
			bezierPoints.add(point);
		}

		return bezierPoints;
	}

	// Printing function used to output in OpenInventor format
	public static void output(
			ArrayList<ArrayList<Double>> controlPoints,
			ArrayList<ArrayList<Double>> bezierPoints, 
			Double r) {
		System.out.println("#Inventor V2.0 ascii");
		System.out.println("");
		System.out.println("Separator {LightModel {model BASE_COLOR} Material {diffuseColor 1.0 1.0 1.0}");
		System.out.println("Coordinate3 { 	point ["); 
		
		// points
		for (ArrayList<Double> point : bezierPoints) {
			System.out.print(point.get(0) + " " + point.get(1) + " " + point.get(2));

			// We don't want a "," at the end of the last point in the bezier pointList
			if (point.equals(bezierPoints.get(bezierPoints.size()-1))) {
				System.out.println("");
			} else {
				System.out.println(",");
			}
		}	

		System.out.println("] }");
		
		System.out.println("IndexedLineSet {coordIndex [");
		for (int i = 0; i < bezierPoints.size(); i++) {
			System.out.print(i + ", ");
		}	
		System.out.println("-1,");
		System.out.println("] } }");

		// Draw Spheres @ controlPoints
		for (ArrayList<Double> point : controlPoints) {
			System.out.println("Separator {LightModel {model PHONG}Material {	diffuseColor 1.0 1.0 1.0}");
			System.out.println("Transform {translation");
			System.out.println(" " + point.get(0) + " " + point.get(1) + " " + point.get(2));
			System.out.println("}Sphere {	radius " + r + " }}");
		}
	}

	// Our main arg
	public static void main(String[] args) {
		// Declare variables and default values
		int n = 20; // Number of line segments
		String file = "cpts_in.txt";
		Double radius = 0.1;
		ArrayList<ArrayList<Double>> controlPoints = new ArrayList<ArrayList<Double>>();
		ArrayList<ArrayList<Double>> bezierPoints;

		// Check for errors, print a usage message and exit 
		if (errorCheck(args)) {
			// Print a usage message and exit
			System.out.println("Usage:$java CG_hw1 [-f FILENAME] [-n n]");
			System.exit(1); // Exit out

		}

		// Set variables
		for (int i = 0; i<args.length/2; i++){
			String arg = args[i*2].toLowerCase();
			if (arg.equals("-f")) {
				file = args[(i*2)+1];
			} else if (arg.equals("-n")) {
				try { 
					n = Integer.parseInt(args[(i*2)+1]); 
				} catch (NumberFormatException e) {
					System.out.println("Please enter a valid value for n.");
					System.exit(1);
				}
			} else if (arg.equals("-r")) {
				try {
					radius = Double.parseDouble(args[(i*2)+1]);
				} catch (NumberFormatException e) {
					System.out.println("Please enter a valid value for radius.");
					System.exit(1);
				}
			}
		}

		// Debug, print variables
		//System.out.println("f = " + file);
		//System.out.println("n = " + n);
		//System.out.println("r = " + radius);

		// Open and read file line by line
		try {
			File f = new File(file);
			Scanner scnr = new Scanner(f);
			while (scnr.hasNextLine()) {
				// Create a list to store a control point's coordinates 
				ArrayList<Double> point = new ArrayList<Double>();
				// Split the line into three coordinates
				String[] splitLine = scnr.nextLine().split(" ", 3); 

				// Iterate through the coords and add them to the point arraylist 
				for ( String coord : splitLine ) {
					point.add(Double.parseDouble(coord));
				}
				
				// Add the point to the arrayList of controlPoints
				controlPoints.add(point);
			}
		} catch (FileNotFoundException e) {
			System.out.println("File does not exist");
			System.exit(1);
		}

		// feed the new list of controlPoints into the castelJAu algorithm 
		bezierPoints = casteljau(controlPoints, n);	
		// Finally, convert to OpenInventor format and output to stdout
		output(controlPoints, bezierPoints, radius);
	}
}
