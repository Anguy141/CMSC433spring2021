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

echo -e "${R}Removing${P} *.OUT${C}...${O}"
rm -f *.OUT
echo -e "${C}Script execution complete.${O}"
