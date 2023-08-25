## Week6

Produce staccato patterns with JFugue, completely drop generating xml. Quite big gains in terms of removing logic from parser and dropping the MelodyNote intermediate format. With staccato patterns it was easily possible to write the playback to MIDI file as well.

Allowed higher degrees of Markov Chains, up to 30. Testing with data from different keys; with high degrees it almost always happens so that the generation stops short and produces the same generation every time. This should be expected because a long sequence of notes does not appear exactly the same in multiple songs no matter how large the dataset. Therefore the next note of such a sequence always comes from the same song and the generation basically reproduces a melody sequence straight from the training data.


## Hours

|Amount|What I did|
|-|-|
|4|refactoring|
|3|increasing unit test coverage|
|1|fixing fs access for jar and mvn|
|6|documentation, test coverage, testing with more training data|
|||
