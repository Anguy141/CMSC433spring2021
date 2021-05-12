MPI=life_mpi
SEQ=life_seq
SMP=sample_mpi

TARGETS=$(MPI) $(SEQ) $(SMP)
T=$(TARGETS)

all: $(T)

$(MPI): ../src/life_mpi.c
	mpicc -o ./life_mpi.x ../src/life_mpi.c

$(SEQ): ../src/life_seq.c
	gcc -o ./life_seq.x ../src/life_seq.c

$(SMP): ../src/sample_mpi.c
	mpicc -o ./sample_mpi.x ../src/sample_mpi.c

clean:
	rm -f ./*.x
