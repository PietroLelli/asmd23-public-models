package scala.u09.task3

import java.awt.event.{ActionEvent, ActionListener}
import javax.swing.{SwingUtilities, Timer}
import scala.u09.task2.ExtendedQMatrix
import scala.u09.task2.ExtendedQMatrix.*
import scala.u09.task2.ExtendedQMatrix.Move.*
import scala.swing.*
import scala.swing.event.ButtonClicked

object TryEnemyQLearningMatrixGUI extends SimpleSwingApplication {

  val rlEnemy: ExtendedQMatrix.Facade = Facade(
    width = 10,
    height = 10,
    initial = (0,4),
    terminal = {case _=>false},
    jumps = { PartialFunction.empty },
    obstacles = Set.empty,
    itemsToCollect = Set((9,4)),
    gamma = 0.9, //Future reward importance
    alpha = 0.5, //Past knowledge importance
    epsilon = 0.8, //Exploration factor
    v0 = 1
  )

  rlEnemy.reward = {
    case ((x,y), a)  if (x == 0 && a == LEFT) || (x == rlEnemy.width-1 && a == RIGHT) || (y == 0 && a == UP) || (y == rlEnemy.height-1 && a == DOWN) => -10
    case (s, a) if rlEnemy.getNeighbors(rlEnemy.enemy, 2).contains(s) => -50
    case ((x,y), _) if rlEnemy.itemsToCollect.contains((x,y))=> 10
    case _ => 0 }

  rlEnemy.resetMap = () => { rlEnemy.enemy = (rlEnemy.width/2, rlEnemy.height/2+1); rlEnemy.enemyMoves = List.empty; rlEnemy.patrolPattern = LazyList.continually(List(LEFT, LEFT, UP, UP, RIGHT, RIGHT, DOWN, DOWN)).flatten }

  private var agentPos: (Int, Int) = rlEnemy.initial
  private var enemyPos: (Int, Int) = rlEnemy.enemy

  val q0 = rlEnemy.qFunction
  val q1 = rlEnemy.makeLearningInstance().learn(1000,100,q0)
  println(rlEnemy.show(q1.vFunction,"%2.2f"))

  val agentPath: Seq[(Move, (Int, Int))] = rlEnemy.qSystem.run(q1.bestPolicy).take(30)
  agentPath.toList.zipWithIndex.map { case((e1, e2), index) => (e1, if(index == 0) e2 else agentPath(index-1)._2) }

  private val agentPathList = agentPath.map(_._2)
  private val enemyPathList = rlEnemy.enemyMoves
  private var agentPathIndex = -1

  def top: Frame = new MainFrame {
    val gridPanel: GridPanel = new GridPanel(rlEnemy.height, rlEnemy.width):
      preferredSize = new Dimension(500, 500)
      for
        y <- 0 until rlEnemy.height
        x <- 0 until rlEnemy.width
      do
        if rlEnemy.itemsToCollect.contains(x, y) then contents += new Label("$")
        else contents += new Label(".")

    val moveButton = new Button("Move")
    val runButton = new Button("Run")
    val timer = new Timer(500, (e: ActionEvent) => moveAgents())

    contents = new BorderPanel:
      layout(gridPanel) = BorderPanel.Position.Center
      layout(new FlowPanel(moveButton, runButton)) = BorderPanel.Position.South

    listenTo(moveButton, runButton)
    reactions += {
      case ButtonClicked(`moveButton`) => moveAgents()
      case ButtonClicked(`runButton`) => timer.start()
    }

    def moveAgents(): Unit =
      agentPathIndex += 1

      val nextAgentPos = agentPathList(agentPathIndex)
      //updateCell(agentPos._1, agentPos._2, ".")
      updateCell(nextAgentPos._1, nextAgentPos._2, "A")
      agentPos = (nextAgentPos._1, nextAgentPos._2)

      val nextEnemyPos = enemyPathList(agentPathIndex)
      updateCell(enemyPos._1, enemyPos._2, ".")
      updateCell(nextEnemyPos._1, nextEnemyPos._2, "E")
      enemyPos = (nextEnemyPos._1, nextEnemyPos._2)

      if (agentPathIndex == agentPathList.length - 1)
        timer.stop()

    def updateCell(x: Int, y: Int, content: String): Unit =
      val index = y * rlEnemy.width + x
      gridPanel.contents(index).asInstanceOf[Label].text = content

    updateCell(agentPos._1, agentPos._2, "A")
    updateCell(enemyPos._1, enemyPos._2, "E")
  }
}


