# Lab 06 - Modelling
## Task 1 - Verifier
Code and do some analysis on the Readers & Writers Petri Net. Add a test to check that in no path long at most 100 states mutual
exclusion fails (no more than 1 writer, and no readers and writers together). Can you extract a small API for representing safety properties?
What other properties can be extracted? How the boundness assumption can help?

To complete this task, I developed an implementation of the petri readers and writers network using the DSL already specified.
I then created an enum of all possible Places (Idle, ChooseAction, ReadyToRead, etc.) and their transitions.

The respective code is available at *scala/u06/task1/ReadersWritersPetriNet.scala*.

I have also implemented methods for testing the safety properties of the petri net:
- **Mutual Exclusion**.
- **Reachability** 
- **Boundedness**.

To test these properties I used the paths method which, given a starting state and a length, finds all possible paths of the specified length. In this way, it was possible to prove that even for very long paths, the properties were not violated.

### Mutual exclusion
codice e spiegazione

### Reachability
codice e spiegazione

### Boundedness
codice e spiegazione

## Task 2 - Artist
Create a variation/extension of PetriNet meta-model, with priorities: each transition is given a numerical priority, and no transition can
fire if one with higher priority can fire. Show an example that your pretty new “abstraction” works as expected. Another interesting extension
is “coloring”: tokens have a value attached, and this is read/updated by transitions.

To complete this task, I created an extension of the Petri net, adding priorities and colours.

The respective code is available at *scala/u06/task2/ExtendedRWPetriNet.scala* and *scala/u06/modelling/ExtendedPetriNet.scala*.

### Priority 
Priority in a Petri net determines the order in which transitions are activated; each arc is associated with a priority that indicates the relative importance of that arc compared to the others. 
When several transitions are ready to be activated at the same time, the one with the highest priority is executed first.

To implement this extension, I added an integer priority field to the Trn class, to which I assigned a default value of 1.

I subsequently modified the toSystem method so that it would execute transitions with a higher priority. 
To do this, I analysed all possible transitions by finding the highest of these priorities, and then filtered the possible transitions to keep only those with the highest priority.

Finally, I added a new operator to the DSL that would allow the priority to be applied to a transition when the petri net was created.

To test the operation of this new functionality I created a Petri net by assigning a higher priority to the read than to the write and verified, by analysing the paths, that the tokens actually only took the read route.

### Colors 

We want to extend the Petri net model by adding the possibility of having coloured tokens (e.g. black or red) and each transition can only accept tokens of a certain colour and when activated it changes the colour of the token it passes.

To implement this new functionality I created an enum for colours and a new case class representing the pair (place, colour). 
Again, I set a default colour value to make it possible to use the Petri net even without using colours. 

To demonstrate that the behaviour is as desired, I created a Petri net having in the readers' branch all transitions with red incoming and outgoing arcs, and having in the writers' branch all transitions with black incoming and outgoing arcs.
Only the last transition that returns the token to the beginning reverses the colour. 

Thus, by calling paths with only one token in the starting place, it is possible to verify that the token alternates between the two branches, executing the readers' branch once and the writers' branch once.


