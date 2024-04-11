package scala.u06.task1

export pc.modelling.PetriNet
import pc.utils.MSet

object ReadersWritersPetriNet:
  enum Place:
    case Idle, ChooseAction, ReadyToRead, ReadyToWrite, Reading, Writing, HasPermission

  export Place.*
  export pc.modelling.PetriNet.*
  export pc.modelling.SystemAnalysis.*
  export pc.utils.MSet

  def petriNetRW = PetriNet[Place](
    MSet(Idle) ~~> MSet(ChooseAction),
    MSet(ChooseAction) ~~> MSet(ReadyToRead),
    MSet(ChooseAction) ~~> MSet(ReadyToWrite),
    MSet(ReadyToRead, HasPermission) ~~> MSet(Reading, HasPermission),
    MSet(Reading) ~~> MSet(Idle),
    MSet(ReadyToWrite, HasPermission) ~~> MSet(Writing) ^^^ MSet(Reading),
    MSet(Writing) ~~> MSet(Idle, HasPermission)
  ).toSystem

  def petriNetRWWithPriority = PetriNet[Place](
    MSet(Idle) ~~> MSet(ChooseAction),
    (MSet(ChooseAction) ~~> MSet(ReadyToRead)) priority 2,
    MSet(ChooseAction) ~~> MSet(ReadyToWrite),
    MSet(ReadyToRead, HasPermission) ~~> MSet(Reading, HasPermission),
    MSet(Reading) ~~> MSet(Idle),
    MSet(ReadyToWrite, HasPermission) ~~> MSet(Writing) ^^^ MSet(Reading),
    MSet(Writing) ~~> MSet(Idle, HasPermission)
  ).toSystem

  def isMutuallyExclusive(initialState: MSet[Place], depth: Int): Boolean =
    val statesMutualExclusion: Seq[Boolean] =
      for
        p <- petriNetRW.paths(initialState, depth)
        s <- p
      yield s.diff(MSet(Reading, Writing)).size == s.size - 2 || s.diff(MSet(Writing, Writing)).size == s.size - 2
    !statesMutualExclusion.contains(true)
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
    val allReachedStates: Seq[Place] =
      for
        path <- petriNetRW.paths(initialState, depth)
        state <- path
        place <- state.asList
      yield place
    allReachedStates.toSet == Place.values.toSet

  private def maxTokenInPN(initialState: MSet[Place]): Int =
    if initialState.matches(MSet(HasPermission)) then initialState.size else initialState.size + 1

  def isBounded(initialState: MSet[Place], depth: Int): Boolean =
    val maxSize = maxTokenInPN(initialState)
    val allReachedStates: Seq[Boolean] =
      for
        path <- petriNetRW.paths(initialState, depth)
        state <- path
      yield state.size <= maxSize
    allReachedStates.forall(identity)

  def isBounded2(initialState: MSet[Place], depth: Int): Boolean =
    (for
      path: Path[Marking[Place]] <- petriNetRW.paths(initialState, depth)
      state <- path
    yield state.size <= maxTokenInPN(initialState)).reduce(_ && _)
  
  @main def mainRWPetriNet =
    //println(petriNetRW.paths(MSet(Idle, Idle, HasPermission), 4).toList.mkString("\n"))
    println(petriNetRWWithPriority.paths(MSet(Idle, ChooseAction, HasPermission),5).toList.mkString("\n"))
