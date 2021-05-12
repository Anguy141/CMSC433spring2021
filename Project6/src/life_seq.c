// Sequential version of the Game of Life.

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

void error(const char* msg) {
    fprintf(stderr, "%s", msg);
    exit(-1);
}

int main(int argc, const char** argv) {
    if (argc != 5) {
        error("life <input file name> <# of generations> <X_limit> <Y_limit>\n");
    }

    const char* fname = argv[1];
    int iterations, w, h;
    if (sscanf(argv[2], "%d", &iterations) != 1 ||
        sscanf(argv[3], "%d", &h) != 1 ||
        sscanf(argv[4], "%d", &w) != 1 ||
        iterations < 1 ||
        w < 2 ||
        h < 2 ) {
            error("Invalid input\n");
        }

    FILE* fd = fopen(fname, "r");
    if (fd == NULL) {
        error("Could not open data file\n");
    }

    // allocate two boards
    char* boards[2];
    int board_size = (w+2) * (h+2) * sizeof(char);
    boards[0] = (char*) malloc(board_size);
    boards[1] = (char*) malloc(board_size);
    if (boards[0] == NULL || boards[1] == NULL) {
        error("Memory allocation failed\n");
    }

    char* curr = boards[0];
    char* prev = boards[1];
    memset(curr, 0, board_size);
    memset(prev, 0, board_size);

#define BOARD(board, x, y) board[((y)+1) * (w+2) + (x)+1]
#define CURR(x, y) BOARD(curr, (x), (y))
#define PREV(x, y) BOARD(prev, (x), (y))

    // initialize from file
    int x, y;
    while (fscanf(fd, "%d %d", &y, &x) != EOF) {
        if (x < 0 || x >= w || y < 0 || y >= h) {
            error("Invalid data\n");
        }
        BOARD(curr, x, y) = 1;
    }
    fclose(fd);

    // computation
    for (int iter = 0; iter < iterations; ++iter) {
        // swap
        char* tmp = prev;
        prev = curr;
        curr = tmp;

        for (y = 0; y < h; ++y) {
            for (x = 0; x < w; ++x) {
                int count = PREV(x-1, y-1) + PREV(x-1, y) + PREV(x-1, y+1) +
                            PREV(x, y-1) + PREV(x, y+1) +
                            PREV(x+1, y-1) + PREV(x+1, y) + PREV(x+1, y+1);
                if (PREV(x, y) == 0) {
                    CURR(x, y) = count == 3 ? 1 : 0;
                }
                else {
                    CURR(x, y) = (count == 2 || count == 3) ? 1 : 0;
                }
            }
        }
    }

    // print
    for (y = 0; y < h; ++y) {
        for (x = 0; x < w; ++x) {
            if (CURR(x, y) == 1) {
                printf("%d %d\n", y, x);
            }
        }
    }

    // free memory
    free(boards[0]);
    free(boards[1]);

    return 0;
}