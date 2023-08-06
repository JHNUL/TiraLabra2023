## Week3

I Implemented more tests using actual training data and checking if the trie size is correct and all expected sequences are found. I also added the actual sequence prediction, now with a fixed length of 50 notes. When a sequence does not have any children (like when a tuple is present only at the end of songs with no notes following it) the generation stops. Generations are written to file at `data/output` currently in plain note names. I tried playing those with JFugue and it worked, at least they sound to be in the key of the training data.

UI needs more work, the UX should be the following:
- at startup all files are read so that UI can provide user the choice of keys (A, D, F etc.) from the training data
- user can select the degree of markov chain
- after selecting the key, user clicks to traing the model
- only after training the model, user can click to generate a melody
- model can be retrained with different key and/or markov degree
- generation works with the retrained model

The above basically works, but the UI is clunky. User should not be able to generate melodies after training if they change the parameters (or changing the parameters should not affect generation which it currently does).

Bumped into floating point arithmetic again with the probability distribution. I need to check that all the probabilities of child nodes add up to one for the weighted random selection. Having an array of doubles with values like 1/3, 2/3 summed up to exactly one in certain order and not in others. So I'm using this kind of threshold check.
```java
double[] probabilities = // probabilities derived from the children of the node
double sum = Arrays.stream(probabilities).sum();
double epsilon = 1e-10;
if (sum == 0.0) { // this is a valid case for no children
    return -1;
} else if (Math.abs(1.0 - sum) > epsilon) {
    // handle exception case
}
return // the index of the next note
```

To do next:

Save generations as musicXML which should be OK as input for JFugue to play the actual tune. Separate logic from UI using some established JavaFX patterns. Possibly implement some UIState object that eventhandlers can use.

No major issues encountered, happy that JFugue can play simple note strings like "A C D D A" for quick testing. Time was tight this week for development.

## Hours

|Amount|What I did|
|-|-|
|15|Detailed above|
|15||