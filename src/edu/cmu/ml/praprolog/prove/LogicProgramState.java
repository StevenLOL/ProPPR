package edu.cmu.ml.praprolog.prove;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;

import edu.cmu.ml.praprolog.prove.VarSketch;
import edu.cmu.ml.praprolog.util.Dictionary;
import edu.cmu.ml.praprolog.util.SymbolTable;

/**
 * Intermediate state of a proof - a conjunction of goals, a
    substitution, and a depth, which is used standardizing apart any
    new rules.  Also contains the original goals and substitution from
    the query, which are used for restarts.
 * @author krivard
 *
 */
public class LogicProgramState extends Component {
	private static final Logger log = Logger.getLogger(LogicProgramState.class);
	protected Goal[] goals,queryGoals,originalQueryGoals;
	protected VarSketch varSketch;
	protected RenamingSubstitution theta;
	protected int depth;
	protected int hash=0;
	
	public LogicProgramState(Goal ... goals) {
		this.init(
				Arrays.copyOf(goals, goals.length),
				Arrays.copyOf(goals, goals.length),
				Arrays.copyOf(goals, goals.length),
				new RenamingSubstitution(0),
				0); // FIXME
	}
	private LogicProgramState(Goal[] originalQueryGoals, Goal[] queryGoals, Goal[] goals, RenamingSubstitution theta, int depth) {
		this.init(originalQueryGoals,queryGoals,goals,theta,depth);
	}
	private void init(Goal[] originalQueryGoals, Goal[] queryGoals, Goal[] goals, RenamingSubstitution theta, int depth) {
		this.queryGoals = queryGoals;
		this.goals = goals;
		this.originalQueryGoals = originalQueryGoals;
		this.theta = theta;
		this.depth = depth;
		if (goals.length > 0) this.freeze();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof LogicProgramState)) return false;
		LogicProgramState s = (LogicProgramState) o;
		if (this.queryGoals.length != s.queryGoals.length) return false;
		if (this.goals.length != s.goals.length) return false;
		for (int i=0; i<this.queryGoals.length; i++) {
			if ( ! this.queryGoals[i].equals(s.queryGoals[i])) return false;
		}
		for (int i=0; i<this.goals.length; i++) {
			if ( ! this.goals[i].equals(s.goals[i])) return false;
		}
		return true;
	}
	@Override
	public int hashCode() {
		return this.hash;
	}
	
	protected void freeze() {
		for (Goal g : this.queryGoals) hash = hash ^ g.hashCode();
		for (Goal g : this.goals) hash = hash ^ g.hashCode();
        //count the number of variables appearing in this state
		this.varSketch = new VarSketch();
		this.varSketch.includeAll(this.queryGoals);
		this.varSketch.includeAll(this.goals);
	}
	public Goal getGoal(int i) {
		if (i<goals.length)
			return goals[i];
		else throw new IllegalArgumentException("Can't get goal "+i+"; only has "+goals.length+" goals");
	}
	/**
	 * Return true iff this state is a solution state - ie, a complete refutation.
	 * @return
	 */
	public boolean isSolution() {
		return goals.length == 0;
	}
	/**
	 * Construct a state that restarts the original query.
	 * @return
	 */
	public LogicProgramState restart() {
		return new LogicProgramState(this.originalQueryGoals);
	}

	@Override
	public List<Outlink> outlinks(LogicProgramState state) {
		throw new IllegalStateException("Should never be calling outlinks() on a LogicProgramState (even though we are a component)");
	}
	@Override
	public boolean claim(LogicProgramState state) {
		throw new IllegalStateException("Should never be calling claim() on a LogicProgramState (even though we are a component)");
	}

	private Goal normalizeVariablesInGoal(Goal g, SymbolTable variableSymTab) {
		Argument[] args = g.getArgs();
		Argument[] newArgs = new Argument[args.length];
		for (int a=0; a<args.length; a++) {
			if (args[a].isConstant()) newArgs[a] = args[a];
			else newArgs[a] = variableSymTab.getId(args[a]);
		}
		return new Goal(g.getFunctor(),newArgs);
	}
	/**
	 * Construct a child of this state.  The first goal is
        removed, the additionalGoals are added, and theta is applied
        to the queryGoals and the new goal list.
	 * @param emptyList
	 * @param additionalTheta
	 * @return
	 */
	public LogicProgramState child(Goal[] additionalGoals,
			RenamingSubstitution additionalTheta) {

		if (log.isDebugEnabled()) {
			log.debug("child of "+this);
			log.debug("plus "+Dictionary.buildString(additionalGoals, new StringBuilder(), " "));
			log.debug("under "+additionalTheta);
		}
		// int newDepths = this.depth + 1;


		Goal[] tmpGoals = new Goal[additionalGoals.length + this.goals.length-1];
		Goal[] tmpGoals1 = additionalTheta.applyToGoalList(additionalGoals, RenamingSubstitution.RENAMED),
				tmpGoals2;
		if (this.goals.length>1) tmpGoals2 = additionalTheta.applyToGoalList(
				Arrays.copyOfRange(this.goals, 1, this.goals.length), // drop goal 0
				RenamingSubstitution.NOT_RENAMED);
		else tmpGoals2 = new Goal[0];
		{ int i=0; for(;i<tmpGoals1.length;i++) tmpGoals[i] = tmpGoals1[i];
		for (int j=0;j<tmpGoals2.length;j++) { tmpGoals[i] = tmpGoals2[j]; i++; }}
		if (log.isDebugEnabled()) log.debug("tmpGoals:"+Dictionary.buildString(tmpGoals,new StringBuilder()," "));
		Goal[] tmpQueryGoals = additionalTheta.applyToGoalList(this.queryGoals,RenamingSubstitution.NOT_RENAMED);

		Goal[][] allGoals = {tmpQueryGoals, tmpGoals1, tmpGoals2};

		SymbolTable variableSymTab = new SymbolTable();
		for (Goal[] goalList : allGoals) {
			for (Goal g : goalList) {
				for (Argument a : g.getArgs()) {
					if (a.isVariable()) variableSymTab.insert(a); 
				}
			}
		}
		for (int i=0; i<tmpGoals.length; i++) {
			tmpGoals[i] = normalizeVariablesInGoal(tmpGoals[i], variableSymTab);
		}
		for (int i=0; i<tmpQueryGoals.length; i++) {
			tmpQueryGoals[i] = normalizeVariablesInGoal(tmpQueryGoals[i], variableSymTab);
		}

		LogicProgramState result = new LogicProgramState(
				this.originalQueryGoals, // FIXME - defensive copy?
				tmpQueryGoals, 
				tmpGoals, 
				this.theta.copy(additionalTheta),
				this.depth+1);
		return result;
	}
	public int getVarSketchSize() {
		return this.varSketch.size();
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("lpState: ");
		Dictionary.buildString(this.queryGoals,sb," ");
		sb.append(" ... ");
		Dictionary.buildString(this.goals,sb," ");
		return sb.toString();
	}
	@Override
	public void compile() {
		// does nothing
	}
	@Override
	public void compile(SymbolTable varialeSymTab) {
		// does  nothing		
	}
	/**
	 * Return a string describing the solution found, relative to the original query.
	 * @return
	 */
	public String description() {
		HashSet<Argument> queryVars = new HashSet<Argument>();
		for (Goal g : this.originalQueryGoals) {
			for (Argument a : g.getArgs()) {
				if (a.isVariable()) queryVars.add(a);
			}
		}
		StringBuilder sb = new StringBuilder();
		ArrayList<Argument> sorted = new ArrayList<Argument>();
		sorted.addAll(queryVars);
		Collections.sort(sorted);
		boolean first=true;
		for (Argument a : sorted) {
			if(first)first=false;
			else sb.append(", ");
			sb.append(a.getName()).append("=").append(this.theta.valueOf(a));
		}
		return sb.toString();
	}
	public int getDepth() {
		return this.depth;
	}
	public RenamingSubstitution getTheta() {
		return this.theta;
	}
	public int getOffset() {
		return this.theta.offset;
	}
	public Goal[] getQueryGoals() {
		return this.queryGoals;
	}
}