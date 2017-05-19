1. java AP -g < $F > 1col.txt { this file should be: "UID","your data" }
This mode ("generate") will take an input file and generate 1 column (the additional
column) of augmented input e.g. "11pm+" as described in class.

2. java AP -v 0.09 0.31 < small.txt ## show the pruned itemsets
This mode ("verbose") will show your implementation of A-priori on a small data set
with the same structure as the main file. In this mode you will use the existing
fields in the file to generate 3 and 4 itemsets.

3. java AP -m minsup minconf $F 1col.txt  ## the real thing
This mode ("master") should discover and write the 3-itemset rules and 4-itemset rules
to standard output.
e.g.
"Heat","11pm+" => "Electric Heater" (sup = 0.03 conf = 0.40)