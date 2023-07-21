## Week 1

I chose the topic and after some tinges of uncertainty I decided to stick with it. It's been a couple of years since I wrote any software with Java, but as I did Tira and the programming I and II courses with Java I wanted to revive my dormant Java skills. I play guitar myself and have studied music in my youth, so the topic of generating music and getting to know something about machine learning in practice made me choose this topic.

I got a Java project up, although the project structure is still in its infancy and configurations may or may not be sensible or correct. I did a lot of small spikes to see if I feel that I can actually get going with this topic and my choice of tools. I'm looking at abcnotation.com as the source of the training data. I am currently going to go with musicxml format as it seemed more convenient to parse than abc, at the expense of being a lot more verbose.

I learned about Tries and the Markov Chain, both of which were unfamiliar to me. Also the Java ecosystem starts to slowly make sense again.

A note has pitch and duration. I'm thinking of representing notes in this project with a string something like D#4q where "D" is the type, "#/b" is for sharp/flat (optional), "4" the octave and "q" the duration (quarter note in this case). This information seems to be easily parseable from musicxml files and encodes what I expect to be needed as a representation of a note for this project. As I am including duration here I'm considering including also rests, but leaving that to a later date. Also time signatures will not be taken into account, so whatever melodies I'm able to generate in the end will be played to some generic time signature and tempo.

_I'm still pondering about the Trie structure itself and how my note sequences should be saved. Is it so that for a 1st degree Markov Chain the Trie will have a root, level n and level n+1, and when predicting the next note based solely on the current note (node), I look at the level n -> get that node's children (from level n+1) and use the probability distribution to pick the next note from those children. Then for the 2nd degree the tree has root, level n-1, n, n+1, and I query with a 2-tuple (n-1, n) to get the children of n and pick from those. So a different Trie for different degrees of markovs and the depth of the tree follows the markov chain degree?_

Next I will continue implementing the basic functionality. More methods and tests for the Trie structure, importantly there needs to be some metadata enabling calculating the probablilities of which child to pick. using the training data to populate the model. Starting with the music generation implementation without MIDI output first. More tests, javadoc, codestyle.

## Hours

|Amount|What I did|
|-|-|
|4|Scratching the surface of Markov Chains and Tries as both were totally unfamiliar to me.|
|6|Doing small tests with different libraries and machine-readable music notation formats. Banging head against the Java ecosystem after a long hiatus from it. Basically trying to figure out if I can produce anything with this topic or if I should change to something else.|
|2|Project configuration and writing documentation.|
|6|Writing the first pieces; reading and parsing a musicxml file, basic UI (just a window and a button), writing the Trie.|
|18||