package scala.u06.task1

export u06.modelling.PetriNet
import u06.utils.MSet

object ReadersWritersPetriNet:
  enum Place:
    case Idle, ChooseAction, ReadyToRead, ReadyToWrite, Reading, Writing, HasPermission

  export Place.*
  export u06.modelling.PetriNet.*
  export u06.modelling.SystemAnalysis.*
  export u06.utils.MSet

  def pnRW = PetriNet[Place](
    MSet(Idle) ~~> MSet(ChooseAction),
    MSet(ChooseAction) ~~> MSet(ReadyToRead),
    MSet(ChooseAction) ~~> MSet(ReadyToWrite),
    MSet(ReadyToRead, HasPermission) ~~> MSet(Reading, HasPermission),
    MSet(Reading) ~~> MSet(Idle),
    MSet(ReadyToWrite, HasPermission) ~~> MSet(Writing) ^^^ MSet(Reading),
    MSet(Writing) ~~> MSet(Idle, HasPermission)
  ).toSystem

  def isMutuallyExclusive(initialState: MSet[Place], depth: Int): Boolean =
    (for
      p <- pnRW.paths(initialState, depth)
      s <- p
    yield s.diff(MSet(Reading, Writing)).size != s.size - 2 && s.diff(MSet(Writing, Writing)).size != s.size - 2).reduce(_ && _)

  /*val paths = pnRW.paths(initialState, depth)
  !paths.exists { path =>
    path.exists { state =>
      val diffReadingWriting = state.diff(MSet(Reading, Writing)).size
      val diffWritingWriting = state.diff(MSet(Writing, Writing)).size
      diffReadingWriting == state.size - 2 || diffWritingWriting == state.size - 2
    }
  }*/
  //pnRW.paths(initialState, depth).flatMap(p => p.filter(s => s.diff(MSet(Reading, Writing)).size == s.size - 2 || s.diff(MSet(Writing, Writing)).size == s.size - 2)).isEmpty

  def isReachable(initialState: MSet[Place], depth: Int): Boolean =
    (for
      path <- pnRW.paths(initialState, depth)
      state <- path
      place <- state.asList
    yield place).toSet == Place.values.toSet


  def maxTokenInPN(initialState: MSet[Place]): Int =
    if initialState.matches(MSet(HasPermission)) then initialState.size else initialState.size + 1

  def isBounded(initialState: MSet[Place], depth: Int): Boolean =
    (for
      path: Path[Marking[Place]] <- pnRW.paths(initialState, depth)
      state <- path
    yield state.size <= maxTokenInPN(initialState)).reduce(_ && _)


  @main def mainPNMutualExclusion =
    println(pnRW.paths(MSet(Idle, Idle, HasPermission), 3).toList.mkString("\n"))