## Week 1

I chose the topic and after some tinges of uncertainty I decided to stick with it. It's been a couple of years since I wrote any software with Java, but as I did Tira and the programming I and II courses with Java I wanted to revive my dormant Java skills.

I got a Java project up, although the project structure is still in its infancy and configurations may or may not be sensible or correct. I did a lot of small spikes to see if I feel that I can actually get going with this topic and my choice of tools. I'm looking at abcnotation.com as the source of the training data. I am currently going to go with musicxml format as it seemed more convenient to parse than abc.

I learned about Tries and the Markov Chain, both of which were unfamiliar to me. Also the Java ecosystem starts to slowly make sense again.

A note has pitch and duration. I'm thinking of representing notes with a string like D4q where "D" is the type, "4" the octave and "q" the duration (quarter note in this case). This information seems to be easily parseable from musicxml files. I'm still pondering about the Trie structure itself and how my representation of a note should be saved. Is it so that for a 1st degree Markov Chain the Trie will have a depth of 3 and for a 2nd degree it will be 4 and so on?

Next I will start with the Trie itself, writing the implementation and tests.

## Hours

|Amount|What I did|
|-|-|
|4|Scratching the surface of Markov Chains and Tries as both were totally unfamiliar to me.|
|6|Doing small tests with different libraries and machine-readable music notation formats. Banging head against the Java ecosystem after a long hiatus from it. Basically trying to figure out if I can produce anything with this topic or if I should change to something else.|
|2|Project configuration and writing documentation.|