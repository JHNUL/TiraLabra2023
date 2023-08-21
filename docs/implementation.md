# Implementation

## Structure

#### Package diagram
![package_diagram](/docs/images/package_diagram.png)

- Main class is the entrypoint to the application and a wrapper for the Launcher class that extends JavaFX application and starts the UI.
- UI presentation layer is specified as fxml in a resource file (JavaFX pattern). Fxml is consumed by the launcher and it contains a binding to the controller class.

#### View
- AppController binds UI elements and handles app initialization logic.
- AppEventHandlers contain all logic for event handlers, i.e. what happens when user interacts with the UI.
- View consumes Services and Utils.

#### Services
- TrainingService contains logic for training the model based on the parameters selected by the user.
- GeneratorService contains logic for generating melodies using the trained model.
- Services consume Utils and Domain.

#### Domain
- Trie is the main data structure of the application. It contains sequences of melodies from the training data based on the degree of Markov Chain selected by the user, e.g. 2nd degree -> all 3-tuples in the training data are saved.
- TrieNode is a node saved in the Trie. All nodes keep a list of children, their own note value (except root node which has no value) and count of occurrences in a sequence based on which the probability distribution is calculated.

#### Utils
- FileIo contains wrappers for filesystem access methods.
- ScoreParser contains logic for parsing MusicXML files to Java objects and extracting information required by the application such as the linear sequence of notes in a training data file and the musical key of the tune.Also has helpers for conversions between integer note and MelodyNote.

## Time and space complexities
Starting from the root node, each child is looked up by accessing a fixed-size array with an index number which is given as parameter. To lookup one element in the search/insert key happens in constant time and because of the fixed-size child array no out-of-bounds checks are required. The time requirement for insert and search operations is therefore **O(key_len)**. In practice key sizes in this application are less than 10.

For space complexity in the worst case no key inserted to the trie shares a prefix with another key so the space complexity is **O(key_len * number_of_keys)**. Each node created to the trie by inserting a key also holds a fixed-size array to keep references to its children, whether or not it has any (this is to make lookup faster).

## Bugs, gripes and general shortcomings
- to be added

## Sources
- https://en.wikipedia.org/wiki/Markov_chain
- https://en.wikipedia.org/wiki/Discrete-time_Markov_chain
- https://en.wikipedia.org/wiki/Trie
- https://www.w3.org/2021/06/musicxml40/
- https://opensheetmusicdisplay.org/blog/blog-music-xml-introduction-comparison/
- https://abcnotation.com/
- https://www.baeldung.com/javafx
- https://openjfx.io/openjfx-docs/
