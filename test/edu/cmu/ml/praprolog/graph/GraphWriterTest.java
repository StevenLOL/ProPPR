package edu.cmu.ml.praprolog.graph;

import static org.junit.Assert.*;

import java.util.Collections;

import org.apache.log4j.Appender;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;

import edu.cmu.ml.praprolog.prove.Argument;
import edu.cmu.ml.praprolog.prove.ConstantArgument;
import edu.cmu.ml.praprolog.prove.Goal;
import edu.cmu.ml.praprolog.prove.LogicProgramState;
import edu.cmu.ml.praprolog.prove.RenamingSubstitution;
import edu.cmu.ml.praprolog.prove.VariableArgument;

public class GraphWriterTest {

	/*

0	lpState(samebib(class_338,-1) ... samebib(class_338,-1)) 
	{author: 1.0} 
1	lpState(samebib(class_338,-1) ... author(class_338,-2),sameauthor(-2,-3),authorinverse(-3,-1))
1	lpState(samebib(class_338,-1) ... author(class_338,-2),sameauthor(-2,-3),authorinverse(-3,-1)) 
	{id(cora_raw/train.1.graph): 1.0} 
2	lpState(samebib(class_338,-1) ... sameauthor(author_y_freund_,-2),authorinverse(-2,-1))
2	lpState(samebib(class_338,-1) ... sameauthor(author_y_freund_,-2),authorinverse(-2,-1)) 
	{authorword: 1.0} 
3	lpState(samebib(class_338,-1) ... haswordauthor(author_y_freund_,-2),haswordauthorinverse(-2,-3),keyauthorword(-2),authorinverse(-3,-1))
3	lpState(samebib(class_338,-1) ... haswordauthor(author_y_freund_,-2),haswordauthorinverse(-2,-3),keyauthorword(-2),authorinverse(-3,-1)) 
	{id(cora_raw/train.1.graph): 1.0} 
4	lpState(samebib(class_338,-1) ... haswordauthorinverse(word_freund,-2),keyauthorword(word_freund),authorinverse(-2,-1))
4	lpState(samebib(class_338,-1) ... haswordauthorinverse(word_freund,-2),keyauthorword(word_freund),authorinverse(-2,-1)) 
	{id(cora_raw/train.1.graph): 1.0} 
5	lpState(samebib(class_338,-1) ... keyauthorword(word_freund),authorinverse(author_y_freund_,-1))
5	lpState(samebib(class_338,-1) ... keyauthorword(word_freund),authorinverse(author_y_freund_,-1)) 
	{kaw(word_freund): 1.0} 
6	lpState(samebib(class_338,-1) ... authorinverse(author_y_freund_,-1))
6	lpState(samebib(class_338,-1) ... authorinverse(author_y_freund_,-1)) 
	{id(cora_raw/train.1.graph): 1.0} 
7	lpState(samebib(class_338,class_338) ... )
3	lpState(samebib(class_338,-1) ... haswordauthor(author_y_freund_,-2),haswordauthorinverse(-2,-3),keyauthorword(-2),authorinverse(-3,-1)) 
	{id(cora_raw/train.1.graph): 1.0} 
8	lpState(samebib(class_338,-1) ... haswordauthorinverse(word_y,-2),keyauthorword(word_y),authorinverse(-2,-1))
8	lpState(samebib(class_338,-1) ... haswordauthorinverse(word_y,-2),keyauthorword(word_y),authorinverse(-2,-1)) 
	{id(cora_raw/train.1.graph): 1.0} 
9	lpState(samebib(class_338,-1) ... keyauthorword(word_y),authorinverse(author_y_freund_,-1))
9	lpState(samebib(class_338,-1) ... keyauthorword(word_y),authorinverse(author_y_freund_,-1)) 
	{kaw(word_y): 1.0} 
10	lpState(samebib(class_338,-1) ... authorinverse(author_y_freund_,-1))
10	lpState(samebib(class_338,-1) ... authorinverse(author_y_freund_,-1)) 
	{id(cora_raw/train.1.graph): 1.0} 
11	lpState(samebib(class_338,class_338) ... )	
2	lpState(samebib(class_338,-1) ... sameauthor(author_y_freund_,-2),authorinverse(-2,-1)) 
	{tcauthor: 1.0} 
12	lpState(samebib(class_338,-1) ... sameauthor(author_y_freund_,-2),sameauthor(-2,-3),authorinverse(-3,-1))
12	lpState(samebib(class_338,-1) ... sameauthor(author_y_freund_,-2),sameauthor(-2,-3),authorinverse(-3,-1)) 
	{authorword: 1.0} 
13	lpState(samebib(class_338,-1) ... haswordauthor(author_y_freund_,-2),haswordauthorinverse(-2,-3),keyauthorword(-2),sameauthor(-3,-4),authorinverse(-4,-1))
13	lpState(samebib(class_338,-1) ... haswordauthor(author_y_freund_,-2),haswordauthorinverse(-2,-3),keyauthorword(-2),sameauthor(-3,-4),authorinverse(-4,-1)) 
	{id(cora_raw/train.1.graph): 1.0} 
14	lpState(samebib(class_338,-1) ... haswordauthorinverse(word_freund,-2),keyauthorword(word_freund),sameauthor(-2,-3),authorinverse(-3,-1))
14	lpState(samebib(class_338,-1) ... haswordauthorinverse(word_freund,-2),keyauthorword(word_freund),sameauthor(-2,-3),authorinverse(-3,-1)) 
	{id(cora_raw/train.1.graph): 1.0} 
15	lpState(samebib(class_338,-1) ... keyauthorword(word_freund),sameauthor(author_y_freund_,-2),authorinverse(-2,-1))
15	lpState(samebib(class_338,-1) ... keyauthorword(word_freund),sameauthor(author_y_freund_,-2),authorinverse(-2,-1)) 
	{kaw(word_freund): 1.0} 
16	lpState(samebib(class_338,-1) ... sameauthor(author_y_freund_,-2),authorinverse(-2,-1))
16	lpState(samebib(class_338,-1) ... sameauthor(author_y_freund_,-2),authorinverse(-2,-1)) 
	{authorword: 1.0} 
17	lpState(samebib(class_338,-1) ... haswordauthor(author_y_freund_,-2),haswordauthorinverse(-2,-3),keyauthorword(-2),authorinverse(-3,-1))

	 */
	int i=0, j=0;
	LogicProgramState[] state = new LogicProgramState[18];
	
	int[] nodes = {1,2,3,4,5,6,7,7,8, 9, 9, 9,10,11,12,13,13};
	int[] edges = {1,2,3,4,5,6,7,8,9,10,10,11,12,13,14,15,15};
	int[] ow    = {0,0,0,0,0,0,0,1,0, 0, 1, 1, 0, 0, 0, 0, 1};
	
	public void printAndTest(GraphWriter gw) {
		i++; 
		printAndTest(gw, i-1,i);
	}
	
	public void printAndTest(GraphWriter gw, int from, int to) {
		System.out.println(String.format("%2d %s",to,state[to]));
		
		gw.writeEdge(state[from], state[to], Collections.EMPTY_LIST);
		assertEquals("nodes",nodes[j],gw.getGraph().getNumNodes());
		assertEquals("edges",edges[j],gw.getGraph().getNumEdges());
		System.out.println("   > "+gw.getGraph().getNumNodes());
		if (ow[j]==1) {
			boolean foundit = false;
			for(int k=0; k<to; k++) {
				if (state[k].equals(state[to])) { foundit = true;
					System.out.println("   > equals "+k+" "+ state[k]);
				} if (state[k].hashCode() == state[to].hashCode()) { foundit = true;
					System.out.println("   > hash== "+k+" "+ state[k]);
				}
			}
			if (from < to-1) foundit = true;
			if (!foundit) System.out.println("   > NOT FOUND");
		}
		j++;
	}
	
	@Test
	public void test() {
		BasicConfigurator.configure(); Logger.getRootLogger().setLevel(Level.WARN);
		Logger.getLogger(GraphWriter.class).setLevel(Level.DEBUG);
		
		GraphWriter gw = new GraphWriter();
		
		
		state[0] = new LogicProgramState(Goal.decompile("samebib,class_338,-1"));
		System.out.println(state[0]);
		
		Goal[] goals1 = {
				Goal.decompile("author,-1,-3"),
				Goal.decompile("sameauthor,-3,-4"),
				Goal.decompile("authorinverse,-4,-2")};
		RenamingSubstitution th = RenamingSubstitution.unify(Goal.decompile("samebib,-1,-2"), state[0].getGoal(0), 
				state[0].getVarSketchSize(), 1, 0);
		state[1] = state[0].child(goals1, th); //System.out.println("VARSKETCH "+state[0].getVarSketchSize());
		printAndTest(gw);
		
		th = new RenamingSubstitution(state[1].getOffset()); th.put(new VariableArgument(-2),new ConstantArgument("author_y_freund_"));
		state[2] = state[1].child(new Goal[0], th);
		printAndTest(gw);
		
		Goal[] goals3 = {
				Goal.decompile("haswordauthor,-1,-3"),
				Goal.decompile("haswordauthorinverse,-3,-2"),
				Goal.decompile("keyauthorword,-3")
		};
		state[3] = state[2].child(goals3, RenamingSubstitution.unify(Goal.decompile("sameauthor,-1,-2"), state[2].getGoal(0), 
				state[2].getVarSketchSize(), 1,0));//System.out.println("VARSKETCH "+state[2].getVarSketchSize());
		printAndTest(gw);
		
		th = new RenamingSubstitution(state[3].getOffset()); th.put(new VariableArgument(-2),new ConstantArgument("word_freund"));
		state[4] = state[3].child(new Goal[0],th);
		printAndTest(gw);
		
		th = new RenamingSubstitution(state[4].getOffset()); th.put(new VariableArgument(-2), new ConstantArgument("author_y_freund_"));
		state[5] = state[4].child(new Goal[0],th);
		printAndTest(gw);
		
		state[6] = state[5].child(new Goal[0], new RenamingSubstitution(state[5].getOffset()));
		printAndTest(gw);
		
		th = new RenamingSubstitution(state[6].getOffset()); th.put(new VariableArgument(-1), new ConstantArgument("class_338"));
		state[7] = state[6].child(new Goal[0], th);
		printAndTest(gw);
		
		th = new RenamingSubstitution(state[3].getOffset()); th.put(new VariableArgument(-2),new ConstantArgument("word_y"));
		state[8] = state[3].child(new Goal[0], th);  i++;
		printAndTest(gw,3,8);
		
		th = new RenamingSubstitution(state[8].getOffset()); th.put(new VariableArgument(-2), new ConstantArgument("author_y_freund_"));
		state[9] = state[8].child(new Goal[0],th);
		printAndTest(gw);
		
		state[10] = state[9].child(new Goal[0], new RenamingSubstitution(state[9].getOffset()));
		printAndTest(gw);
		
		th = new RenamingSubstitution(state[10].getOffset()); th.put(new VariableArgument(-1),new ConstantArgument("class_338"));
		state[11] = state[10].child(new Goal[0], th);
		printAndTest(gw);
		
		Goal[] goals12 = {
				Goal.decompile("sameauthor,-1,-3"),
				Goal.decompile("sameauthor,-3,-2")
		};
		state[12] = state[2].child(goals12, RenamingSubstitution.unify(Goal.decompile("sameauthor,-1,-2"), state[2].getGoal(0),
				state[2].getVarSketchSize(), 1, 0)); i++;
		printAndTest(gw,2,12);
		
		Goal[] goals13 = {
				Goal.decompile("haswordauthor,-1,-3"),
				Goal.decompile("haswordauthorinverse,-3,-2"),
				Goal.decompile("keyauthorword,-3")
		};
		state[13] = state[12].child(goals13, RenamingSubstitution.unify(Goal.decompile("sameauthor,-1,-2"), state[12].getGoal(0),
				state[12].getVarSketchSize(), 1, 0));
		printAndTest(gw);
		
		th = new RenamingSubstitution(state[13].getOffset()); th.put(new VariableArgument(-2), new ConstantArgument("word_freund"));
		state[14] = state[13].child(new Goal[0], th);
		printAndTest(gw);
		
		th = new RenamingSubstitution(state[14].getOffset()); th.put(new VariableArgument(-2), new ConstantArgument("author_y_freund_"));
		state[15] = state[14].child(new Goal[0], th);
		printAndTest(gw);
		
		state[16] = state[15].child(new Goal[0], new RenamingSubstitution(state[15].getOffset()));
		printAndTest(gw);
		
		// sameauthor,-1,-2 & haswordauthor,-1,-3 & haswordauthorinverse,-3,-2 & keyauthorword,-3
		Goal[] goals17 = {
				Goal.decompile("haswordauthor,-1,-3"),
				Goal.decompile("haswordauthorinverse,-3,-2"),
				Goal.decompile("keyauthorword,-3")
		};
		state[17] = state[16].child(goals17, RenamingSubstitution.unify(Goal.decompile("sameauthor,-1,-2"), state[16].getGoal(0),
				state[16].getVarSketchSize(), 1, 0));
		assertEquals("hash equality",state[3].hashCode(),state[17].hashCode());
		assertTrue("state equality",state[3].equals(state[17]));
		printAndTest(gw);
		
		
	}

}