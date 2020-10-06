#NAME

CG_hw1 - Generates a set of points on a Bezier curve
 
## SYNOPSIS

./CG_hw1 [OPTION]...
 
## Description

Reads n 3D control points from a file. Outputs 3D points on the bezier curve
in OpenInventor format.

Written in Java on Unix. Uses "javac" for compiling. main() is found in
CG_hw1.java 

-f 
	Specify the name of the input file (default: cpts_in.txt)

-n
	Number of 3D input points (default: 20)

-r
	The radius of spheres in the outpu (default:0.1)
