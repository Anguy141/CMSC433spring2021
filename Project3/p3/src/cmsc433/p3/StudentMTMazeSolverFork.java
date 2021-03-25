package cmsc433.p3;

import java.util.List;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ForkJoinPool;

public class StudentMTMazeSolverFork extends SkippingMazeSolver {
	public StudentMTMazeSolverFork(Maze maze) {
		super(maze);

	}

	@Override
	public List<Direction> solve() {
		ForkJoinPool forkJoinPool = new ForkJoinPool(4);
		return forkJoinPool.invoke(new myDFSMazeSolverFork());
	}

	public class myDFSMazeSolverFork extends RecursiveTask<List<Direction>> {
		private static final long serialVersionUID = 1L;

		public myDFSMazeSolverFork() {
		}

		public List<Direction> compute() {
			Choice startC;
			Choice endC;
			List<myDFSMazeSolver> forkListStart = new LinkedList<myDFSMazeSolver>();
			List<myDFSMazeSolver> forkListEnd = new LinkedList<myDFSMazeSolver>();
			List<myDFSMazeSolver> forkList = null;
			List<Direction> listOfSteps = null;
			try {
				startC = firstChoice(maze.getStart());
				endC = firstChoice(maze.getEnd());

				while (!startC.choices.isEmpty()) {
					Choice ch = follow(startC.at, startC.choices.peek());
					Direction initDir = startC.choices.pop();
					forkListStart.add(new myDFSMazeSolver(initDir, ch));
				}

				while (!endC.choices.isEmpty()) {
					Choice ch = follow(endC.at, endC.choices.peek());
					Direction initDir = endC.choices.pop();
					forkListEnd.add(new myDFSMazeSolver(initDir, ch));
				}

				forkList = new LinkedList<myDFSMazeSolver>(forkListStart);
				forkList.addAll(forkListEnd);

			} catch (SolutionFound e) {
				e.printStackTrace();
			}

			int j = forkList.size()/2;
			for (int i = 0; i < forkList.size() / 2; i++) {
				forkList.get(i).fork();

				if ((listOfSteps = forkList.get(i).join()) != null) {
					break;
				} else if (forkList.get(j) != null && (listOfSteps = forkList.get(j).compute()) != null) {
					break;
				}
				j++;

			}

			return listOfSteps;
		}
	}

	public class myDFSMazeSolver extends RecursiveTask<List<Direction>> {
		private static final long serialVersionUID = 1L;
		Direction dir;
		Choice start;

		public myDFSMazeSolver(Direction dir, Choice start) {
			this.dir = dir;
			this.start = start;
		}

		public List<Direction> compute() {
			LinkedList<Choice> choiceStack = new LinkedList<Choice>();
			Choice ch;

			try {
				choiceStack.push(start);
				while (!choiceStack.isEmpty()) {
					ch = choiceStack.peek();
					if (ch.isDeadend()) {
						// backtrack.
						choiceStack.pop();
						if (!choiceStack.isEmpty())
							choiceStack.peek().choices.pop();
						continue;
					}
					choiceStack.push(follow(ch.at, ch.choices.peek()));
				}
				// No solution found.
				return null;
			} catch (SolutionFound e) {
				Iterator<Choice> iter = choiceStack.iterator();
				LinkedList<Direction> solutionPath = new LinkedList<Direction>();
				while (iter.hasNext()) {
					ch = iter.next();
					solutionPath.push(ch.choices.peek());
				}

				// pushes the initial direction to the stack
				solutionPath.push(dir);

				if (maze.display != null)
					maze.display.updateDisplay();
				// System.out.println(pathToFullPath(solutionPath));
				return pathToFullPath(solutionPath);
			}
		}
	}
}
