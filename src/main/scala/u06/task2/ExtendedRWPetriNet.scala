package scala.u06.task2

import scala.u06.modelling.ExtendedPetriNet
import scala.u06.modelling.ExtendedPetriNet.*

object ExtendedRWPetriNet:

  enum Place:
    case Idle, ChooseAction, ReadyToRead, ReadyToWrite, Reading, Writing, HasPermission

  export Place.*
  export scala.u06.modelling.ExtendedPetriNet.*
  export u06.modelling.SystemAnalysis.*
  export u06.utils.MSet

  def pnRWPriorities = ExtendedPetriNet[Place](
    MSet(Idle) ~~> MSet(ChooseAction),
    MSet(ChooseAction) ~~> MSet(ReadyToRead) priority 2,
    MSet(ChooseAction) ~~> MSet(ReadyToWrite),
    MSet(ReadyToRead, HasPermission) ~~> MSet(Reading, HasPermission),
    MSet(Reading) ~~> MSet(Idle),
    MSet(ReadyToWrite, HasPermission) ~~> MSet(Writing) ^^^ MSet(Reading),
    MSet(Writing) ~~> MSet(Idle, HasPermission)
  ).toSystem

  @main def mainExtendedPN =
    println(pnRWPriorities.paths(MSet(Idle, HasPermission),15).toList.mkString("\n"))
