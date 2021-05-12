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
NUM_PROC=2

if [ "$#" -eq 1 ]; then
    NUM_PROC=$1;
    echo -e "${C}Number of processes is set to ${P}$1${C}.${O}"
elif [ "$#" -gt 1 ]; then
	echo -e "${R}Usage: ./run_sample_mpi.sh <Number_of_Processes>.${O}"
	NUM_PROC=$1;
	echo -e \
		"${C}Number of processes is set to ${P}$1${C} (the first argument).${O}"
else
	echo -e "${R}Usage: ./run_sample_mpi.sh <Number_of_Processes>.${O}"
    echo -e \
  	  "${C}Number of processes is set to ${P}${NUM_PROC}${C} (default).${O}"
fi

N=${NUM_PROC}
if [ $((${N})) -lt 2 ]; then
	echo -e "${R}Number of threads less than 2, reset it to 2.${O}"
	N=2
elif [ $((${N})) -gt 40 ]; then
	echo -e "${R}Number of threads greater than 40, reset it to 40.${O}"
	N=40
fi

echo -e "${C}Making ${P}sample_mpi${C}...${O}"
make sample_mpi
echo -e \
	"${C}Running ./sample_mpi.x with MPI using ${P}${N}${C} processes.${O}"
mpirun -hostfile ./hostfile -np $((${N})) ./sample_mpi.x

echo -e "${C}Script execution complete.${O}"
