#!/usr/bin/python

import matplotlib.pyplot as plt
import numpy as np
import sys

for pas in [100, 200, 400, 600, 800, 1000, 1500, 2000]:
	fl = str(pas)+".txt"
	data = np.loadtxt(fl)
	sorted_data = np.sort(data)
	yvals = np.arange(len(sorted_data))/float(len(sorted_data))
	plt.plot(sorted_data,yvals,label= str((pas)) + " pass")

plt.axis([0,2000,0,1])
plt.xlabel('Error in meters')
plt.ylabel('Cumulative Probability')
plt.legend(loc=4)
plt.title("Cumulative Error with change in # user giving input")
#plt.show()
plt.savefig("Graph_Pass.png", bbox_inches='tight')
