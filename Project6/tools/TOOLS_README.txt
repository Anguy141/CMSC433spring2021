INSTRUCTIONS FOR THE VISUALIZER AND THE RANDOM TEST GENERATOR
I. VISUALIZER

1. Make sure you have python3 installed on your machine
2. Unzip the life_visualizer.py file
3. To run the visualizer, you will need to specify some command line arguments first:
	i. input generation file - this is the starting generation file (e.g. life.data1)
	ii. number of generations - this is the number of times the algorithm is run on the original data (e.g. 100)
	iii. x limit - maximum x size of the board
	iv. y limit - maximum y size of the board
	v. interval speed - speed of the simulation in ms (I recommend using 50 for a normal run, and 500 if you want to see it slowly)
	vi. save final generation array flag - 0 if you don't want to save it, 1 if you do (NOTE: if you save the output array, it will take longer to run initially, so don't panick)
	
	Now, here's an example of running the program with the input file life.data.1.txt, 100 generations, xlim = 255, ylim = 255, speed = 50, and not saving the output array:
	python life_visualizer.py life.data.1.txt 100 255 255 50 0
	
4. Some conclusions: 
-If you save the final generation array, it will appear as test.out in the same folder as life_visualizer.py
-Again, if you save the output array it will take longer for the simulation to start, this is normal

II. TEST GENERATOR
1. Command line arguments:
	i. name of the output test file
	ii. x limit - maximum x size of the board
	iii. y limit - maximum y size of the board
	iv. number of objects to add (usually around 20 will suffice)
	
	Now, here's an example of running the program specifying testfile.out as the output file, with a board size of 255x255 and a length of 20
	python input_generator.py testfile.out 255 255 20
	
Now you can generate random test input files, and visualize them using the visualizer.
