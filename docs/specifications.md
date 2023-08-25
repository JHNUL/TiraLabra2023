# Specifications

## Programming language

Project is implemented in Java. In addition I can do code review for projects implemented in Python or JavaScript.

## Topic of the project

This project uses Markov Chain to predict the next note in a melody.

## Data structures and algorithms used

- Trie data structure
- Markov Chain to predict the next note in a melody

## Inputs

The model is trained on a corpus of songs in musicxml format. I chose this format because I deemed it the least painful to parse. User selects:
- musical key
- Markov Chain degree
- generated note duration
- generated melody target length

## Time/space complexities

- Trie insertion O(k), where k = key length
- Trie lookup O(k), where k = key length
- Generation O(n*m), where n is the generated melody length and m is the child array size of each node

## Sources

- https://en.wikipedia.org/wiki/Markov_chain
- https://en.wikipedia.org/wiki/Discrete-time_Markov_chain
- https://en.wikipedia.org/wiki/Trie
- https://www.w3.org/2021/06/musicxml40/
- https://opensheetmusicdisplay.org/blog/blog-music-xml-introduction-comparison/
- https://abcnotation.com/

## Study program

Tietojenkäsittelytieteen kandidaatti (TKT)

## Language of project/documentation

English
