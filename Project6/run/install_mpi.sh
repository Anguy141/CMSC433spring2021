#!/bin/bash

# Define some colors.
LIGHT_PURPLE='\033[1;35m'
LIGHT_CYAN='\033[1;36m'
ORIG_COLOR='\033[0m'

P=${LIGHT_PURPLE}
C=${LIGHT_CYAN}
O=${ORIG_COLOR}

echo -e "${C}Installing ${P}MPI${C} with apt-get...${O}"
sudo apt-get update
sudo apt-get install libopenmpi-dev
sudo apt-get autoclean
echo -e "${C}Script execution complete.${O}"
