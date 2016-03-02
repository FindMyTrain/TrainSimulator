#! /bin/bash

ant build
echo "Hello Aurobindo!!! I am working on the outputs"
echo "Please wait for a minute!!!"

if [ ! -d pass ]; then
    mkdir pass
fi

if [ ! -d meanSD ]; then
    mkdir meanSD
fi
cd meanSD
if [ ! -d meanSDpass ]; then
    mkdir meanSDpass
fi
cd ..

for pass in 100 200 400 600 800 1000 1500 2000; do
    java -cp ~/Desktop/TrainSimulator/bin Simulator_01.MainActivity $pass 0 > pass/$pass.txt
    echo "..."
done

cp plotCDFpass.py pass
cd pass
python plotCDFpass.py
cd ..

mv result* meanSD/meanSDpass
