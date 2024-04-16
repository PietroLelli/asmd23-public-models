package scala.u07.task2

import u07.modelling.SPN
import u07.utils.MSet

import java.util.Random

object BrusselatorPetriNet:
  enum Place:
    case A, B, D, E, X, Y

  export Place.*
  export u07.modelling.CTMCSimulation.*
  export u07.modelling.SPN.*

  val brusselatorPetriNet = SPN[Place](
    Trn(MSet(A), m => 1.0,   MSet(X),  MSet()),
    Trn(MSet(X, X, Y), m => 1.0,  MSet(X, X, X),  MSet()),
    Trn(MSet(B, X), m => 1.0,   MSet(Y, D),   MSet()),
    Trn(MSet(X), m => 1.0,   MSet(E),   MSet(Y)))

  @main def main =
    val simulation = toCTMC(brusselatorPetriNet).newSimulationTrace(MSet(A,B,B,B,X,Y), new Random)
      .take(10)

    simulation.foreach(e => {
        println("time: " + e._1)
        println("X: " + e._2.countOccurrences(X))
        println("Y: " + e._2.countOccurrences(Y))
      })