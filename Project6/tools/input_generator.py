"""
Random input generator for Conway's Game of Life
Written by Rimon Melamed for CMSC433
"""
import numpy as np
import sys

"""Many cool examples of various objects in the game can be found at this link: 
http://www.radicaleye.com/lifepage/picgloss/picgloss.html"""

"""Adding moving objects below (still lifes are lower down)"""
#creates a glider with (x, y) as the top left coordinates (moves to upper left - make sure to put it in the lower quartile for maximum iterations)
#make sure to leave space for these objects
def add_glider(x, y):
    ret = []
    ret.append([x, y])
    ret.append([x + 1, y])
    ret.append([x + 2, y])
    ret.append([x, y + 1])
    ret.append([x + 1, y + 2])
    return np.array(ret)

def add_lightweight_spaceship(x, y):
    ret = []
    ret.append([x, y])
    ret.append([x - 1, y + 1])
    ret.append([x - 1, y + 2])
    ret.append([x - 1, y + 3])
    ret.append([x , y + 3])
    ret.append([x + 1, y + 3])
    ret.append([x + 2, y + 3])
    ret.append([x + 3, y])
    ret.append([x + 3, y + 2])
    return np.array(ret)

def add_rabbits(x, y):
    ret = []
    ret.append([x, y])
    ret.append([x, y + 1])
    ret.append([x + 1, y + 1])
    ret.append([x + 1, y + 2])
    ret.append([x + 2, y + 1])
    ret.append([x + 4, y])
    ret.append([x + 5, y])
    ret.append([x + 5, y + 1])
    ret.append([x + 6, y])
    return np.array(ret)

def add_beacon(x, y):
    ret = []
    ret.append([x, y])
    ret.append([x, y + 1])
    ret.append([x + 1, y])
    ret.append([x + 2, y + 3])
    ret.append([x + 3, y + 2])
    ret.append([x + 3, y + 3])
    return np.array(ret)

def add_blinker(x, y):
    ret = []
    ret.append([x, y])
    ret.append([x + 1, y])
    ret.append([x + 2, y])
    return np.array(ret)

def add_clock(x, y):
    ret = []
    ret.append([x, y])
    ret.append([x - 1, y + 1])
    ret.append([x - 2, y + 1])
    ret.append([x, y + 2])
    ret.append([x + 1, y + 2])
    ret.append([x - 1, y + 3])
    return np.array(ret)

"""Still lifes added below"""
def add_hat(x, y):
    ret = []
    ret.append([x, y])
    ret.append([x - 1, y + 1])
    ret.append([x + 1, y + 1])
    ret.append([x - 1, y + 2])
    ret.append([x + 1, y + 2])
    ret.append([x - 1, y + 3])
    ret.append([x + 1, y + 3])
    ret.append([x - 2, y + 3])
    ret.append([x + 2, y + 3])
    return np.array(ret)

def add_snake(x, y):
    ret = []
    ret.append([x, y])
    ret.append([x, y + 1])
    ret.append([x + 1, y + 1])
    ret.append([x + 2, y])
    ret.append([x + 3, y])
    ret.append([x + 3, y + 1])
    return np.array(ret)

#def add_

args = np.array(sys.argv)

outfile = args[1]
xmax = int(args[2])
ymax = int(args[3])
length = int(args[4])

#print(add_glider(5, 5))

# coords = np.random.normal(loc = xmax / 2, scale = 1.0, size = (length, 2))
# coords = coords.astype(int)
# print(coords)
#
# coords = np.random.triangular(0, xmax / 2, xmax, size = (length, 2))
# coords = coords.astype(int)
# print(coords)

top_left = [10, 10]
top_right = [xmax - 10, 10]
bottom_left = [10, ymax - 10]
bottom_right = [xmax - 10, ymax - 10]

#add moving objects
#counter = 0
num_moving = 6
num_still = 2
out = np.array([])
out = np.reshape(out, (0, 2))
#out = np.vstack((out, add_glider(50, 50)))

for _ in range(int(length / 2)):
    x = np.random.randint(top_left[0], top_right[0], size = num_moving)
    y = np.random.randint(top_left[1], bottom_left[1], size = num_moving)
    out = np.vstack((out, add_glider(x[0], y[0])))
    out = np.vstack((out, add_lightweight_spaceship(x[1], y[1])))
    out = np.vstack((out, add_rabbits(x[2], y[2])))
    out = np.vstack((out, add_beacon(x[3], y[3])))
    out = np.vstack((out, add_blinker(x[4], y[4])))
    out = np.vstack((out, add_clock(x[5], y[5])))
    #counter = counter + num_moving

for _ in range(int(length / 2)):
    x = np.random.randint(top_left[0], top_right[0], size=num_still)
    y = np.random.randint(top_left[1], bottom_left[1], size=num_still)
    out = np.vstack((out, add_hat(x[0], y[0])))
    out = np.vstack((out, add_snake(x[1], y[1])))
    #counter = counter + num_still

print(out.shape)

np.savetxt(outfile, out, fmt = "%1.0i")
