#!/bin/bash

# Define some colors.
LIGHT_RED='\033[1;31m'
LIGHT_PURPLE='\033[1;35m'
LIGHT_CYAN='\033[1;36m'
ORIG_COLOR='\033[0m'

R=${LIGHT_RED}
P=${LIGHT_PURPLE}
C=${LIGHT_CYAN}
O=${ORIG_COLOR}

# Get the number of processes.
# Print usage info when number of argument is not expected.
if [ "$#" -ne 5 ]; then
	echo -e \
		"${R}Usage: ./run_life_mpi.sh <Number_of_Processes> <input file name> <# of generations> <X_limit> <Y_limit>.${O}"
	exit -1
fi

NUM_PROC=$1;
echo -e "${C}Number of processes is set to ${P}$1${C}.${O}"

N=${NUM_PROC}
if [ $((${N})) -lt 2 ]; then
	echo -e "${R}Number of threads less than 2 (too few), reset it to 2.${O}"
	N=2
elif [ $((${N})) -gt 40 ]; then
	echo -e "${R}Number of threads greater than 40 (too many), reset it to 40.${O}"
	N=40
fi

echo -e "${C}Making ${P}life_mpi${C}...${O}"
make life_mpi
echo -e \
	"${C}Running ./life_mpi.x with MPI on ${P}${N}${C} processes.${O}"
echo -e "${C}(Also) Writing the program output to ${P}life_mpi.OUT${C}...${O}"
mpirun -hostfile ./hostfile -np $((${N})) ./life_mpi.x $2 $3 $4 $5 | tee ./life_mpi.OUT

echo -e "${C}Script execution complete.${O}"
