#! /bin/bash
ant build
echo "Hello Aurobindo!!! I am working on the outputs"
echo "Please wait for a minute!!!"
pass=$1

if [ ! -d graph ]; then
    mkdir graph
fi
cd graph
if [ ! -d "graph"$pass ]; then
mkdir graph$pass
fi
cd ..

if [ ! -d meanSD ]; then
    mkdir meanSD
fi
cd meanSD
if [ ! -d "meanSD"$pass ]; then
    mkdir meanSD$pass
fi
cd ..

for loc in 100 500 1000 3000; do
    java -cp ~/Desktop/TrainSimulator/bin Simulator_01.MainActivity $pass $loc > graph/graph$pass/$loc.txt
    echo "..."
done

cp plotCDFloc.py graph/graph$pass
cd graph/graph$pass
python plotCDFloc.py
cd ../..

mv result* meanSD/meanSD$pass

