# Lab 06 - Modelling
## Task 1 - Verifier
Code and do some analysis on the Readers & Writers Petri Net. Add a test to check that in no path long at most 100 states mutual
exclusion fails (no more than 1 writer, and no readers and writers together). Can you extract a small API for representing safety properties?
What other properties can be extracted? How the boundness assumption can help?

To complete this task, I developed an implementation of the petri readers and writers network using the DSL already specified.
I then created an enum of all possible Places (Idle, ChooseAction, ReadyToRead, etc.) and their transitions.

I have also implemented methods for testing the safety properties of the petri net:
- **Mutual Exclusion**.
- **Reachability** 
- **Boundedness**.

To test these properties I used the paths method which, given a starting state and a length, finds all possible paths of the specified length. In this way, it was possible to prove that even for very long paths, the properties were not violated.

In accordance with the specifications of the task, I proceeded to extract an API representing the safety properties. Subsequently, I proceeded to remove the safety control functions from the specific PetriNet and to incorporate them into the PetriNet object.

The respective code is available at *src/main/scala/u06/modelling/PetriNet.scala*, and the tests at *src/test/scala/u06/task1/ReadersWritersPetriNetTest.scala*.

## Task 2 - Artist
Create a variation/extension of PetriNet meta-model, with priorities: each transition is given a numerical priority, and no transition can
fire if one with higher priority can fire. Show an example that your pretty new “abstraction” works as expected. Another interesting extension
is “coloring”: tokens have a value attached, and this is read/updated by transitions.

To complete this task, I created an extension of the Petri net, adding priorities and colours.

The respective code is available at *src/main/scala/u06/task2/ExtendedRWPetriNet.scala* and *src/main/scala/u06/modelling/ExtendedPetriNet.scala*.

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

# Lab 07 - Stochastic Modelling

## Task 1 - Simulator
In this task, I've crafted two functions to conduct analyses on the StochasticChannel.

The respective code can be found here: *src/main/scala/u07/examples/StochasticChannelSimulation.scala*

The primary function is designed to calculate the average time taken for communication across n runs.

```
def averageTimeStochasticChannel(nRun: Int): Double =
  (0 to nRun).foldLeft(0.0)((z, t) =>
    z + stocChannel.newSimulationTrace(IDLE, new Random)
      .take(10)
      .toList
      .find(e => e._2 == DONE)
      .map(_.time)
      .getOrElse(0.0)) / nRun
```
To achieve this, I run a simulation of the communication n times. For each run, we track the moment when the DONE state is reached using the foldLeft operator. Then, we aggregate all these times and calculate the average by dividing the total by the number of runs.

The second function is more intricate. It determines the percentage of time during which the system remains in the FAIL state until it successfully completes the communication.

```
def percentageTimeInFailState(nRun: Int): Double =
  val totalTimes = (0 to nRun).foldLeft((0.0, 0.0)) ((acc, _) => {
    val (failTime, totTime) = stocChannel.newSimulationTrace(IDLE, new Random)
    .take(10)
    .toList
    .sliding(2)
    .foldLeft((0.0, 0.0))((z, s) => if s(0).state == FAIL then (z._1 + s(1).time - s(0).time, s(1).time) else (z._1, s(1).time))
    (acc._1 + failTime, acc._2 + totTime)
  })
  totalTimes._1 / totalTimes._2
```

The function generates n runs. For each run, it accumulates a tuple (failTime, totTime) by considering pairs of Event. If the current event is FAIL, it calculates the fail time by subtracting the next event time from the current one.
Once we have the tuple (failTime, totTime) for a single simulation, we accumulate it using an external foldLeft. 

Finally, we divide the time spent in failure by the total time across all simulations to get the percentage.

In a second step, I extracted an API, as the task required. The relevant code is available in the following file: *src/main/scala/u07/modelling/CTMCSimulation.scala*.

In a second step, I extracted an API, as the task required. The relevant code is available in the following file: *src/main/scala/u07/modelling/CTMCSimulation.scala*.

I then implemented the following functions:
```
    def averageTimeToReachState(nRun: Int, initialState: S, stateToCheck: S): Double =
      (0 to nRun).foldLeft(0.0)((z, _) => z + self.newSimulationTrace(initialState, new Random)
        .take(10)
        .toList
        .find(e => e.state == stateToCheck).map(e => e.time).getOrElse(0.0)) / nRun
```

```
 def relativeTimeInState(nRun: Int, initialState: S, stateToCheck: S): Double =
      relativeTimeInCondition(nRun, initialState, _ == stateToCheck)

    private def relativeTimeInCondition(nRun: Int, initialState: S, f: S => Boolean): Double =
      val totalTimes = (0 to nRun).foldLeft((0.0, 0.0))((acc, _) => {
        val (conditionTime, totTime) = self.newSimulationTrace(initialState, new Random)
          .take(10)
          .toList
          .sliding(2)
          .foldLeft((0.0, 0.0))((z, s) => if (f(s(0).state)) (z._1 + (s(1).time - s(0).time), s(1).time) else (z._1, s(1).time))

        (acc._1 + conditionTime, acc._2 + totTime)
      })
      totalTimes._1 / totalTimes._2
```

## Task 2 - Guru
Check the SPN module, that incorporates the ability of CTMC modelling on top of Petri Nets, leading to Stochastic Petri
Nets. Code and simulate Stochastic Readers & Writers shown in previous lesson. Try to study how key parameters/rate
influence average time the system is in read or write state.

To complete this task, I implemented a stochastic Readers and writers petri net. The relevant code is available at: *src/main/scala/u07/task2/RWStochasticPetriNet.scala*
```
  val stochasticRWPetriNet = SPN[Place](
    Trn(MSet(Idle), m => 1.0, MSet(ChooseAction), MSet()),
    Trn(MSet(ChooseAction), m => 200000, MSet(ReadyToRead), MSet()),
    Trn(MSet(ChooseAction), m => 100000, MSet(ReadyToWrite), MSet()),
    Trn(MSet(ReadyToRead, HasPermission), m => 100000, MSet(Reading, HasPermission), MSet()),
    Trn(MSet(Reading), m => 0.1 * m(Reading), MSet(Idle), MSet()),
    Trn(MSet(ReadyToWrite, HasPermission), m => 100000, MSet(Writing), MSet(Reading)),
    Trn(MSet(Writing), m => 0.2, MSet(Idle, HasPermission), MSet())
  )
```
After implementing the petri net, I used the previously developed API to calculate the percentage of time the petri net spends in a given state (read, write and neither of the previous two states).


## Task 3 - Chemist
SPNs can be used to simulate dynamics of chemical reactions. Experiment with it. E.g.: search the “Brussellator” chemical
reaction on wikipedia: it oscillates! Try to reproduce it.

To complete this task, I implemented a Petri net by modelling chemical reactions as transitions. I then modified the rates of the transitions to obtain the desired oscillations.

In order to simulate chemical reactions, I modified the transitions so that the reactants (A and B) never ended. In particular, every time A is consumed, it is immediately added back. Same with the transitions using B. 

This makes it possible to run simulations of the desired length without worrying about the reactants.

```
enum Place:
    case A, B, D, E, X, Y

val brusselatorPetriNet = SPN[Place](
    Trn(MSet(A), m => 1, MSet(X, A), MSet()),
    Trn(MSet(X, X, Y), m => m(Y), MSet(X, X, X), MSet()),
    Trn(MSet(B, X), m => m(X) * 0.5, MSet(Y, D, B), MSet()),
    Trn(MSet(X), m => m(X) * 0.5, MSet(E), MSet()))
```

The complete code is available in the following file: *src/main/scala/u07/task3/BrusselatorPetriNet.scala*.

I then printed out the oscillations of X and Y obtained from the simulation in a graph, shown below.

![](resources/simulation.png)

