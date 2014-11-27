For this project, I extend my previous project to support partial search. My program returns a sorted list of results from my inverted index that start with the query word(s). For example, the input query file will consist of one multi-word search query per line, which are:

1918
a-coming
you
irrelphant
WH

a sample results ﬁle for the above queries might be:

1918
"/home/public/cs212/input/index/simple/pg22014c.txt", 1, 51
"/home/public/cs212/input/index/simple/pg22014b.txt", 1, 62
"/home/public/cs212/input/index/simple/pg22014a.txt", 1, 93

a-coming
"/home/public/cs212/input/index/simple/pg22014a.txt", 1, 36

you
"/home/public/cs212/input/index/simple/pg22014c.txt", 2, 11

irrelphant

WH
"/home/public/cs212/input/index/simple/pg22014a.txt", 3, 13
"/home/public/cs212/input/index/simple/pg22014b.txt", 1, 5