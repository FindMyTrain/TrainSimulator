#!/usr/bin/python

import matplotlib.pyplot as plt
import numpy as np
import sys

for loc in [100, 500, 1000]:
	fl = str(loc)+".txt"
	data = np.loadtxt(fl)
	sorted_data = np.sort(data)
	yvals = np.arange(len(sorted_data))/float(len(sorted_data))
	plt.plot(sorted_data,yvals,label= str(loc) + " m loc error")

plt.axis([0,2000,0,1])
plt.xlabel('Error in meters')
plt.ylabel('Probability')
plt.legend(loc=4)
plt.title("Cumulitive Distribution Function")
#plt.show()
plt.savefig("Graph_Loc.png", bbox_inches='tight')
