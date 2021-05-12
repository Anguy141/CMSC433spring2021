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

# Print usage info when number of argument is not expected.
if [ "$#" -ne 4 ]; then
	echo -e \
		"${R}Usage: ./run_life_seq.sh <input file name> <# of generations> <X_limit> <Y_limit>.${O}"
	exit -1
fi

echo -e "${C}Making ${P}life_seq${C}...${O}"
make life_seq
echo -e "${C}Running ./life_seq.x on ${P}1${C} process...${O}"
echo -e "${C}(Also) Writing the program output to ${P}life_seq.OUT${C}...${O}"
./life_seq.x $1 $2 $3 $4 | tee life_seq.OUT
echo -e "${C}Script execution complete.${O}"
