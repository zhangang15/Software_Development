For this project, I write a Java program that recursively processes all text files in a directory and builds an inverted index to store the mapping from words to the documents (and position within those documents) where those words were found. For example, suppose we have the following mapping stored in our inverted index:

elephant -> { ( mammals.txt, [ 3, 8 ] ),
             ( endangered.txt, [ 2 ] ) }

This indicates that the word elephant is found in two files, mammals.txt and endangered.txt. In the file mammals.txt, it is found in two locations (the 3rd and 8th word). In file endangered.txt, it is found in one place as the 2nd word in the file.