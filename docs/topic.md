# Topic

Predict/generate melodies based on training data using the Markov chain. Compare 1st, 2nd degree and if feasible, higher degrees.

Core attributes:
- Discrete-time finite state space
- There is always a next state, the process does not terminate by itself
- All possible states and transitions are included in the definition (from training data)
- Transitions have probabilities resolved from the training data
- The probability of the next state must be dependent only on the current state (Markov property)
- In order m Markov chains, the next state depends on the past m states, e.g. a chain of Y_n = (X_n, X_n-1, X_n-2) would be a 2nd degree Markov chain consisting of ordered triplets (3-tuples) of current + 2 past states
- The state transitions must be described in such a fashion that any transition fulfills the Markov property, e.g. a random draw from a set of coins can be described both non-Markov and Markov way

Transition probability distribution matrix for a 1st degree Markov chain

||a|b|c|d|e|f|g|sum|
|-|-|-|-|-|-|-|-|-|
|a|0.1|0.2|0|0.5|0.2|0|0|1|
|b|...|||||||1|
|c|...|||||||1|
|d|...|||||||1|
|e|...|||||||1|

Transition probability distribution matrix for a 2nd degree Markov chain

||a|b|c|d|e|f|g|sum|
|-|-|-|-|-|-|-|-|-|
|aa|0.1|0.2|0|0.5|0.2|0|0|1|
|ab|...|||||||1|
|bc|...|||||||1|
|dd|...|||||||1|
|fe|...|||||||1|

Where from every pair of two previous notes there is a transition to a single new note.

So from any vertex there are edges to other vertices that have weights corresponding to probabilities of the transition. 0-weighted vertices do not exist as they are impossible transitions. There must not be a vertex with no transitions anywhere, i.e. all rows must sum up to exactly 1 (some directed graph check?).

This must be saved in some data structure (trie?), which is then populated by analyzing the training data.

The next note is randomly drawn based on the probability matrix (so not the most common one always, but randomly respecting the probabilities)

When generating melodies with the trained model, some initial state is of course needed as starting point.


### State space S

Consists of all the notes present in the training data
p_i_j, i.e. propability of going from S_i to S_j in the state space can be seen from the transition probability matrix.

### Training data

A note has a pitch and a duration, both have to be accounted for in order to generate music.

Source: abc/midi/musicxml?
Language: Java? JFugue lib, Python? music21 lib

Lots of training data preferrably. Could save intermediate representations if necessary (at least in the beginning might be useful). The model itself (parseable to a trie) should be saved so that the program does not have to be trained every time when it is used, but it can just read the model from disk when starting.

### First steps

Decide on:
- language
- training data format (how it marks both pitch and duration)

Get:
- training data

Core stuff:
- Trie structure (model)
- Source data parsing
- Populating the model
- Using the model to predict the next note

User interface:
- Bare minimum a CLI where some initial conditions can be set (degree of Markov chain, initial note(s) perhaps)
- Nice to have: GUI where there are some selections for initial state and visualization for the generated music.

Challenges:
- Getting a lot of data parsed correctly to populate the model

Unit test jamboree!