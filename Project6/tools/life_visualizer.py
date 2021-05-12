"""
Visualization tool for Conway's Game of Life
Written by Rimon Melamed for CMSC433
"""
import numpy as np
from matplotlib import pyplot as plt
import matplotlib.animation as animation
import sys

def fill_board(gen, board):
    for i in range(np.shape(gen)[0]):
        x, y = gen[i]
        board[x, y] = 1

    return board

# def update_cell(cur, alive):
#     if (cur == 1 and (alive == 2 or alive == 3)) or (cur == 0 and alive == 3):
#         return 1
#
#     return 0

def next_gen(board):
    newboard = np.zeros(np.shape(board))
    xlim = np.shape(board)[0]
    ylim = np.shape(board)[1]

    i = 0
    j = 0

    count = board[i, j + 1] + board[i + 1, j] + board[i + 1, j + 1]

    if (board[i, j] == 1 and (count == 2 or count == 3)) or (board[i, j] == 0 and count == 3):
        newboard[i, j] = 1
    else:
        newboard[i, j] = 0

    for j in range(1, ylim - 1):
        count = board[i, j - 1] + board[i, j + 1] + board[i + 1, j - 1] + board[i + 1, j] + board[i + 1, j + 1]

        if (board[i, j] == 1 and (count == 2 or count == 3)) or (board[i, j] == 0 and count == 3):
            newboard[i, j] = 1
        else:
            newboard[i, j] = 0

    j = ylim - 1
    count = board[i, j - 1] + board[i + 1, j - 1] + board[i + 1, j]
    if (board[i, j] == 1 and (count == 2 or count == 3)) or (board[i, j] == 0 and count == 3):
        newboard[i, j] = 1
    else:
        newboard[i, j] = 0

    for i in range(1, xlim - 1):
        j = 0
        count = board[i - 1, j] + board[i - 1, j + 1] + board[i, j + 1] + board[i + 1, j] + board[i + 1, j + 1]

        if (board[i, j] == 1 and (count == 2 or count == 3)) or (board[i, j] == 0 and count == 3):
            newboard[i, j] = 1
        else:
            newboard[i, j] = 0

        for j in range(1, ylim - 1):
            count = board[i - 1, j - 1] + board[i - 1, j] + board[i - 1, j + 1] + board[i, j - 1] + board[i, j + 1] \
                    + board[i + 1, j - 1] + board[i + 1, j] + board[i + 1, j + 1]

            if (board[i, j] == 1 and (count == 2 or count == 3)) or (board[i, j] == 0 and count == 3):
                newboard[i, j] = 1
            else:
                newboard[i, j] = 0

        j = ylim - 1
        count = board[i - 1, j - 1] + board[i - 1, j] + board[i, j - 1] + board[i + 1, j - 1] + board[i + 1, j]

        if (board[i, j] == 1 and (count == 2 or count == 3)) or (board[i, j] == 0 and count == 3):
            newboard[i, j] = 1
        else:
            newboard[i, j] = 0

    i = xlim - 1
    j = 0

    count = board[i - 1, j] + board[i - 1, j + 1] + board[i, j + 1]

    if (board[i, j] == 1 and (count == 2 or count == 3)) or (board[i, j] == 0 and count == 3):
        newboard[i, j] = 1
    else:
        newboard[i, j] = 0

    for j in range(1, ylim - 1):
        count = board[i - 1, j - 1] + board[i - 1, j + 1] + board[i - 1, j] + board[i, j - 1] + board[i, j + 1]

        if (board[i, j] == 1 and (count == 2 or count == 3)) or (board[i, j] == 0 and count == 3):
            newboard[i, j] = 1
        else:
            newboard[i, j] = 0

    j = ylim - 1

    count = board[i - 1, j - 1] + board[i - 1, j] + board[i, j - 1]
    if (board[i, j] == 1 and (count == 2 or count == 3)) or (board[i, j] == 0 and count == 3):
        newboard[i, j] = 1
    else:
        newboard[i, j] = 0

    return newboard#np.swapaxes(newboard, 1, 0)

def get_final_array(board, iters):
    arr = []
    cpboard = board.copy()

    for i in range(iters):
        cpboard = next_gen(cpboard)

    for i in range(np.shape(cpboard)[0]):
        for j in range(np.shape(cpboard)[1]):
            if cpboard[i, j] != 0:
                arr.append([i, j])
    return np.array(arr)

def update_board(fnum, board, img):
    newboard = next_gen(board)
    board[:] = newboard[:]
    img.set_data(board)
    return img

args = np.array(sys.argv)
fname = args[1]
num_gens = int(args[2])
xmax = int(args[3])
ymax = int(args[4])
speed = int(args[5])
save_out = int(args[6])
init_gen = np.genfromtxt(fname).astype(int) #"life.data.1.txt"

board = np.zeros((xmax, ymax))
board = fill_board(init_gen, board)

if save_out:
    fin_gen = get_final_array(board, num_gens)
    np.savetxt("test.out", fin_gen, fmt = "%1.0i")

fig, ax = plt.subplots()
img = ax.imshow(board, interpolation='nearest')
#ax.swap_axes()
ani = animation.FuncAnimation(fig, update_board, fargs=(board, img), frames = num_gens, interval = speed, repeat = False)
plt.show()
