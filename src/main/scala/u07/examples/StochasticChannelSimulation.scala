package u07.examples

import u07.utils.Time
import java.util.Random
import u07.examples.StochasticChannel.*

@main def mainStochasticChannelSimulation  =
  println("AVG time stochastic channel: " + stocChannel.averageTimeToReachState(10, IDLE, DONE))
  println("Percentage time in fail state: " + stocChannel.relativeTimeInState(10, IDLE, FAIL))