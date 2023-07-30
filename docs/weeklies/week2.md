## Week2

Biggest change of existing implementation was the internal representation of a note changing from a string containing pitch and duration info to integer denoting just pitch. I changed the implementation of the TrieNode children from a hashmap to an array where the position in the array signifies the integer value of the note. This way a lookup to the tree does one constant-time array access per note in the search sequence. Also the array of children is fixed-size, accommodating an octave range that suits (or will suit) most of the training data.

I included a training data package and committed it to source control. There's a script and instructions how to unpack it to the correct destination in the README. Now the program can at startup go through the training data and resolve the musical keys (e.g. "A", "C") from the available data and allow user to select from which key to train the model. This is so that the generated music might make more sense when keys are not mixed in the training data.

I also added the initial implementation and tests for the next note prediction. On the infra side, I have my remote repository set up to block all pushes directly to main branch. Pull requests trigger an action to run the full unit test suites and build the project. Failed pipeline prevents merging to main. I Also added some javadocs to my methods (still lots to be added, but main focus has been on implementation) and added a checkstyle goal and IDE integration as per the course schedule suggestions. Moved magic numbers (some of them at least) to a constants class.

Next I will continue the prediction functionality and test components more in combination, i.e. use real data to populate the model and check predictions against the training data.

I learned how to do mocks with Mockito and more about maven. There are several future issues to solve that I won't worry about yet, like in what format to actually produce the generated melody sequence (integer representation of a note to something playable) and how to play it (preferrably with some Java lib). Also gathering more sane and consistent training data seems to take more time than I would like. Better separation of logic and UI will be a topic of some future refactoring session. MusicXML still seems a more comfortable format because there's a library that maps xml elements to Java classes. ABC notation didn't seem to have much available in terms of Java (less than 10 years old at least), so I'm suffering the verbosity of XML to avoid writing my own parser. 


## Hours

|Amount|What I did|
|-|-|
|5|Refactor Trie implementation to use arrays for children and integer representation for note + building unit test coverage.|
|2|Discovering unhandled conditions by using more training data, nullcheck optional constructs etc..|
|4|Get more training data and provide UI for selecting training data based on key (music).|
|4|Start implementing the prediction mechanism, occationally doing TDD.|
|1|Integrate Mockito for mocking stuff in unit tests and add checkstyle goal and IDE integration.|
|0.5|Set up branch protection and CI unit test run, require green before merge.|
|16.5||