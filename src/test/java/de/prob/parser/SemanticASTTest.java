package de.prob.parser;

import de.prob.parser.antlr.BProject;
import de.prob.parser.ast.nodes.MachineNode;
import de.prob.parser.ast.nodes.predicate.PredicateOperatorNode;
import de.prob.parser.ast.nodes.predicate.PredicateOperatorWithExprArgsNode;
import de.prob.parser.ast.visitors.TypeChecker;
import de.prob.parser.ast.visitors.TypeErrorException;
import org.junit.Ignore;
import org.junit.Test;

import de.prob.parser.antlr.Antlr4BParser;

import static org.junit.Assert.assertEquals;

public class SemanticASTTest {

	@Ignore
	@Test
	public void testRecords() throws Exception {
		String machine = "MACHINE test2\n";
		machine += "CONSTANTS k\n";
		machine += "PROPERTIES k = rec(a:1, b:TRUE) \n";
		machine += "END";
		check(machine);
		// TODO add class for records and structs
	}


	@Test
	public void testWhileLoop() throws Exception {
		String machine = "MACHINE test2\n";
		machine += "OPERATIONS \n";
		machine += "foo = SELECT 1=1 THEN WHILE 1=1 DO skip INVARIANT 1=1 VARIANT 3 END END\n";
		machine += "END";
		check(machine);
	}

	@Test
	public void testEmptySet() throws Exception {
		String machine = "MACHINE Fin1Test\n" +
				"SETS\n" +
				" ID={aa,bb}\n" +
				"/* DEFINITIONS\n" +
				"  x == (1=1 & y); */\n" +
				"VARIABLES xx\n" +
				"INVARIANT\n" +
				" {} : FIN(ID) & {} /: FIN1(ID) & xx:ID\n" +
				"INITIALISATION xx:=aa\n" +
				"END\n";
		check(machine);
		MachineNode node = Antlr4BParser.createBProjectFromMachineStrings(machine).getMainMachine();
		PredicateOperatorWithExprArgsNode predicate = (PredicateOperatorWithExprArgsNode) ((PredicateOperatorNode) node.getInvariant()).getPredicateArguments().get(0);
		assertEquals(predicate.getExpressionNodes().get(0).getType().toString(), "POW(ID)");
		assertEquals(predicate.getExpressionNodes().get(1).getType().toString(), "POW(POW(ID))");
	}

	@Test
	public void testRelLaws() throws Exception {
		String machine = "\n" +
				"MACHINE RelLaws\n" +
				"SETS /* enumerated */\n" +
				"  setX={el1,el2};\n" +
				"  setY={y1,y2}\n" +
				"ABSTRACT_VARIABLES\n" +
				"  ff,\n" +
				"  gg,\n" +
				"  hh,\n" +
				"  rx,\n" +
				"  ry\n" +
				"/* PROMOTED OPERATIONS\n" +
				"  add_ff,\n" +
				"  add_gg,\n" +
				"  add_hh,\n" +
				"  add_rx,\n" +
				"  add_ry */\n" +
				"INVARIANT\n" +
				"    ff <+ gg = gg \\/ (dom(gg) <<| ff)\n" +
				"  & hh : POW(setX * setX)\n" +
				"  & ry : POW(setY * setY)\n" +
				"  & ff <+ {} = ff\n" +
				"  & {} <+ ff = ff\n" +
				"  & ff <+ ff = ff\n" +
				"  & ff~ = {yy,xx|yy : ran(ff) & xx : dom(ff) & xx |-> yy : ff}\n" +
				"  & id(ff) = {xx,yy|xx : ff & yy : ff & xx = yy}\n" +
				"  & prj1(ff,gg) = prj1(ff,gg)\n" +
				"  & prj2(ff,gg) = prj2(ff,gg)\n" +
				"  & iterate(hh,1) = hh\n" +
				"  & dom(ff >< gg) = dom(ff) /\\ dom(gg)\n" +
				"  & ran(ff >< gg) = {y,z|y : ran(dom(gg) <| ff) & z : ran(dom(ff) <| gg) & #x.(x : dom(ff) & x : dom(gg) & x |-> y : ff & x |-> z : gg)}\n" +
				"  &\n" +
				"    !(fx,fy,gy).(\n" +
				"     (\n" +
				"      fx : setX\n" +
				"      &\n" +
				"      fy : setY\n" +
				"      &\n" +
				"      gy : setY\n" +
				"     )\n" +
				"     =>\n" +
				"     (\n" +
				"      (\n" +
				"       fx |-> fy : ff\n" +
				"       &\n" +
				"       fx |-> gy : gg\n" +
				"      )\n" +
				"      <=>\n" +
				"      (fx |-> (fy |-> gy) : ff >< gg)\n" +
				"     )\n" +
				"    )\n" +
				"  & card(ff >< gg) <= card(ff) * card(gg)\n" +
				"  & dom((ff || gg)) = dom(ff) * dom(gg)\n" +
				"  & ran((ff || gg)) = ran(ff) * ran(gg)\n" +
				"  & card((ff || gg)) = card(ff) * card(gg)\n" +
				"  //& (ff || gg) = {xy,mn|#(x,y,m,n).(xy = x |-> y & mn = m |-> n & x |-> m : ff & y |-> n : gg)}\n" +
				"  &\n" +
				"    !(fx,fy,gx,gy).(\n" +
				"     (\n" +
				"      fx : setX\n" +
				"      &\n" +
				"      fy : setY\n" +
				"      &\n" +
				"      gx : setX\n" +
				"      &\n" +
				"      gy : setY\n" +
				"     )\n" +
				"     =>\n" +
				"     (\n" +
				"      (\n" +
				"       fx |-> fy : ff\n" +
				"       &\n" +
				"       gx |-> gy : gg\n" +
				"      )\n" +
				"      <=>\n" +
				"      (fx |-> gx |-> (fy |-> gy) : (ff || gg))\n" +
				"     )\n" +
				"    )\n" +
				"  &\n" +
				"    !nn.(\n" +
				"     (\n" +
				"      nn : 0 .. 100\n" +
				"      &\n" +
				"      nn > 0\n" +
				"     )\n" +
				"     =>\n" +
				"     iterate(hh,nn) = (iterate(hh,nn - 1) ; hh)\n" +
				"    )\n" +
				"  & ff[dom(rx)] = ran((id(dom(rx)) ; ff))\n" +
				"  & (id(setX) ; ff) = ff\n" +
				"  & (ff ; id(setY)) = ff\n" +
				"  & (id(setX) ; rx) = rx\n" +
				"  & (id(setX) ; gg) = gg\n" +
				"  & (id(setY) ; ry) = ry\n" +
				"  & (rx ; ff)~ = (ff~ ; rx~)\n" +
				"  & dom(ff \\/ gg) = dom(ff) \\/ dom(gg)\n" +
				"  & ran(ff \\/ gg) = ran(ff) \\/ ran(gg)\n" +
				"  & dom(ff /\\ gg) <: dom(ff) /\\ dom(gg)\n" +
				"  & ran(ff /\\ gg) <: ran(ff) /\\ ran(gg)\n" +
				"  & (ff \\/ gg)~ = ff~ \\/ gg~\n" +
				"  & dom((ff ; gg~)) <: dom(ff)\n" +
				"  & (\n" +
				"\n" +
				"     !(xx,yy).(\n" +
				"      xx : dom(ff) &\n" +
				"      yy : ran(ff) &\n" +
				"      xx |-> yy : ff\n" +
				"      =>\n" +
				"      yy : ran(gg)\n" +
				"     )\n" +
				"     =>\n" +
				"     dom((ff ; gg~)) = dom(ff)\n" +
				"    )\n" +
				"  & (\n" +
				"     (ff : setX --> setY)\n" +
				"     <=>\n" +
				"     (\n" +
				"      ff : setX +-> setY\n" +
				"      &\n" +
				"      dom(ff) = setX\n" +
				"     )\n" +
				"    )\n" +
				"  & (\n" +
				"     (ff : setX +-> setY)\n" +
				"     <=>\n" +
				"\n" +
				"     !(xx,yy,zz).(\n" +
				"      xx : dom(ff) &\n" +
				"      yy : ran(ff) &\n" +
				"      zz : ran(ff) &\n" +
				"      (\n" +
				"       xx |-> yy : ff\n" +
				"       &\n" +
				"       xx |-> zz : ff\n" +
				"      )\n" +
				"      =>\n" +
				"      yy = zz\n" +
				"     )\n" +
				"    )\n" +
				"  & (\n" +
				"     (ff : setX >->> setY)\n" +
				"     <=>\n" +
				"     (\n" +
				"      ff : setX >-> setY\n" +
				"      &\n" +
				"      ff~ : setY >-> setX\n" +
				"     )\n" +
				"    )\n" +
				"  & (\n" +
				"     (ff : setX >+> setY)\n" +
				"     <=>\n" +
				"     (\n" +
				"      ff : setX +-> setY\n" +
				"      &\n" +
				"\n" +
				"      !(xx,yy).(\n" +
				"       (\n" +
				"        xx : dom(ff)\n" +
				"        &\n" +
				"        yy : dom(ff)\n" +
				"        &\n" +
				"        xx /= yy\n" +
				"        &\n" +
				"        1=1 /* LEQ_SYM(xx,yy) */\n" +
				"       )\n" +
				"       =>\n" +
				"       ff(xx) /= ff(yy)\n" +
				"      )\n" +
				"     )\n" +
				"    )\n" +
				"  & (\n" +
				"     (ff : setX +->> setY)\n" +
				"     <=>\n" +
				"     (\n" +
				"      ff : setX +-> setY\n" +
				"      &\n" +
				"\n" +
				"      !yy.(\n" +
				"       yy : setY\n" +
				"       =>\n" +
				"       yy : ran(ff)\n" +
				"      )\n" +
				"     )\n" +
				"    )\n" +
				"  & (\n" +
				"     (ff : setX >-> setY)\n" +
				"     <=>\n" +
				"     (\n" +
				"      ff : setX >+> setY\n" +
				"      &\n" +
				"      ff : setX --> setY\n" +
				"     )\n" +
				"    )\n" +
				"  & (\n" +
				"     (ff : setX -->> setY)\n" +
				"     <=>\n" +
				"     (\n" +
				"      ff : setX +->> setY\n" +
				"      &\n" +
				"      ff : setX --> setY\n" +
				"     )\n" +
				"    )\n" +
				"  & (\n" +
				"     (ff : setX >->> setY)\n" +
				"     <=>\n" +
				"     (\n" +
				"      ff : setX -->> setY\n" +
				"      &\n" +
				"      ff : setX >-> setY\n" +
				"     )\n" +
				"    )\n" +
				"  & ff <: ff \\/ gg\n" +
				"  & gg <: ff \\/ gg\n" +
				"  & ff /\\ gg <: ff\n" +
				"  & ff /\\ gg <: gg\n" +
				"  & {xx|xx : ff & xx : gg} = ff /\\ gg\n" +
				"  & {xx|xx : ff & not(xx : gg)} = ff - gg\n" +
				"  &\n" +
				"    !xx.(\n" +
				"     (\n" +
				"      xx : ff\n" +
				"      &\n" +
				"      xx : gg\n" +
				"     )\n" +
				"     =>\n" +
				"     xx : ff /\\ gg\n" +
				"    )\n" +
				"  &\n" +
				"    !xx.(\n" +
				"     (\n" +
				"      xx : ff\n" +
				"      &\n" +
				"      not(xx : gg)\n" +
				"     )\n" +
				"     =>\n" +
				"     (\n" +
				"      xx : ff - gg\n" +
				"      &\n" +
				"      xx /: gg\n" +
				"     )\n" +
				"    )\n" +
				"  & (\n" +
				"\n" +
				"     #xx.(\n" +
				"      xx : ff\n" +
				"      &\n" +
				"      xx /: gg\n" +
				"     )\n" +
				"     =>\n" +
				"     (\n" +
				"      not(ff \\/ gg = gg)\n" +
				"      &\n" +
				"      not(ff - gg = {})\n" +
				"     )\n" +
				"    )\n" +
				"  & (\n" +
				"     ff <<: gg\n" +
				"     or\n" +
				"     ff /<<: gg\n" +
				"    )\n" +
				"  & (\n" +
				"     ff <: gg\n" +
				"     or\n" +
				"     ff /<: gg\n" +
				"    )\n" +
				"  & (\n" +
				"     ff : POW(gg)\n" +
				"     or\n" +
				"     ff /: POW(gg)\n" +
				"    )\n" +
				"  & (\n" +
				"     (\n" +
				"      ff <: gg\n" +
				"      &\n" +
				"      ff /<<: gg\n" +
				"     )\n" +
				"     =>\n" +
				"     ff = gg\n" +
				"    )\n" +
				"  & (\n" +
				"     ff <: gg\n" +
				"     =>\n" +
				"     (\n" +
				"      ff /= gg\n" +
				"      <=>\n" +
				"      ff <<: gg\n" +
				"     )\n" +
				"    )\n" +
				"  & (\n" +
				"     ff <<: gg\n" +
				"     =>\n" +
				"     (\n" +
				"      ff /= gg\n" +
				"      &\n" +
				"      gg /= {}\n" +
				"     )\n" +
				"    )\n" +
				"  & (\n" +
				"     ff = gg\n" +
				"     =>\n" +
				"     ff /<<: gg\n" +
				"    )\n" +
				"  & (\n" +
				"     ff <<: gg\n" +
				"     =>\n" +
				"     ff <: gg\n" +
				"    )\n" +
				"  & (\n" +
				"     ff <<: gg\n" +
				"     =>\n" +
				"\n" +
				"     #xx.(\n" +
				"      xx : gg\n" +
				"      &\n" +
				"      xx /: ff\n" +
				"     )\n" +
				"    )\n" +
				"  & (\n" +
				"     ff <: gg\n" +
				"     <=>\n" +
				"\n" +
				"     !x.(\n" +
				"      x : ff\n" +
				"      =>\n" +
				"      x : gg\n" +
				"     )\n" +
				"    )\n" +
				"  & (\n" +
				"     ff <<: gg\n" +
				"     =>\n" +
				"     card(ff) < card(gg)\n" +
				"    )\n" +
				"  & (\n" +
				"     ff <: gg\n" +
				"     =>\n" +
				"     card(ff) <= card(gg)\n" +
				"    )\n" +
				"  & (\n" +
				"     card(ff) < card(gg)\n" +
				"     =>\n" +
				"     gg /<: ff\n" +
				"    )\n" +
				"  & card(ff \\/ gg) <= card(ff) + card(gg)\n" +
				"  & (\n" +
				"     (ff /\\ gg = {})\n" +
				"     <=>\n" +
				"     ff <: setX * setY - gg\n" +
				"    )\n" +
				"  /*& union({RANGE_LAMBDA__|#ss.(ss <: ff & RANGE_LAMBDA__ = ss)}) = ff\n" +
				"  & (\n" +
				"     card(ff) /= 1\n" +
				"     <=>\n" +
				"     (union({RANGE_LAMBDA__|#ss.(ss <<: ff & RANGE_LAMBDA__ = ss)}) = ff)\n" +
				"    )\n" +
				"  & inter({RANGE_LAMBDA__|#ss.(ss <: ff & RANGE_LAMBDA__ = ss)}) = {}*/\n" +
				"  & (\n" +
				"     (ff /\\ gg) /= {}\n" +
				"     <=>\n" +
				"     (ff /\\ gg) /= {}\n" +
				"    )\n" +
				"  & (\n" +
				"     ff /<: gg\n" +
				"     <=>\n" +
				"\n" +
				"     #ee.(\n" +
				"      ee : ff\n" +
				"      &\n" +
				"      ee /: gg\n" +
				"     )\n" +
				"    )\n" +
				"  & (\n" +
				"     ff <<: gg\n" +
				"     <=>\n" +
				"     (\n" +
				"      ff <: gg\n" +
				"      &\n" +
				"\n" +
				"      #ee.(\n" +
				"       ee : gg\n" +
				"       &\n" +
				"       ee /: ff\n" +
				"      )\n" +
				"     )\n" +
				"    )\n" +
				"  & (\n" +
				"     (ff : dom(gg) <-> ran(gg))\n" +
				"     <=>\n" +
				"     (\n" +
				"      dom(ff) <: dom(gg)\n" +
				"      &\n" +
				"      ran(ff) <: ran(gg)\n" +
				"     )\n" +
				"    )\n" +
				"  & ff |> ran(ff) = ff\n" +
				"  & ff |>> {} = ff\n" +
				"  & ff |>> ran(ff) = {}\n" +
				"  & (\n" +
				"     ff : setX +-> setY\n" +
				"     =>\n" +
				"     ff |> ran(gg) = %fx.(fx : dom(ff) & ff(fx) : ran(gg)|ff(fx))\n" +
				"    )\n" +
				"  & (\n" +
				"     ff : setX +-> setY\n" +
				"     =>\n" +
				"     ff |>> ran(gg) = %fx.(fx : dom(ff) & ff(fx) /: ran(gg)|ff(fx))\n" +
				"    )\n" +
				"  & ff |> ran(gg) = {xy|xy : ff & prj2(setX,setY)(xy) : ran(gg)}\n" +
				"  & ff |>> ran(gg) = {xy|xy : ff & prj2(setX,setY)(xy) /: ran(gg)}\n" +
				"  & dom(ff) <| ff = ff\n" +
				"  & {} <<| ff = ff\n" +
				"  & dom(ff) <<| ff = {}\n" +
				"  & dom(gg) <| ff = {xy|xy : ff & prj1(setX,setY)(xy) : dom(gg)}\n" +
				"  & dom(gg) <<| ff = {xy|xy : ff & prj1(setX,setY)(xy) /: dom(gg)}\n" +
				"  & {dd|dd <: setX & dd <| ff = ff} = {dd|dd <: setX & dom(ff) <: dd}\n" +
				"  & {rr|rr <: setY & ff |> rr = ff} = {rr|rr <: setY & ran(ff) <: rr}\n" +
				"  & {dd|dd <: setX & dd <<| ff = ff} = {dd|dd <: setX & dom(ff) /\\ dd = {}}\n" +
				"  & {rr|rr <: setY & ff |>> rr = ff} = {rr|rr <: setY & ran(ff) /\\ rr = {}}\n" +
				"  & rx <: closure1(rx)\n" +
				"  & rx <: closure(rx)\n" +
				"  & id(dom(rx) \\/ ran(rx)) <: closure(rx)\n" +
				"  & closure(rx) = closure(rx) \\/ id(dom(rx) \\/ ran(rx))\n" +
				"  & closure(rx) = closure(rx \\/ id(dom(rx) \\/ ran(rx)))\n" +
				"  & closure1(rx) <: closure(rx)\n" +
				"  & id(dom(rx) \\/ ran(rx)) <: closure(rx)\n" +
				"  & closure1(rx \\/ id(dom(rx) \\/ ran(rx))) <: closure(rx)\n" +
				"  & closure1(rx) \\/ id(setX) = closure(rx)\n" +
				"  & closure1(rx \\/ id(setX)) = closure(rx)\n" +
				"  & iterate(rx,1) = rx\n" +
				"  & iterate(rx,0) = id(setX)\n" +
				"  & (\n" +
				"     rx <: hh\n" +
				"     =>\n" +
				"     (\n" +
				"      closure1(rx) <: closure1(hh)\n" +
				"      &\n" +
				"      id(setX) <: closure(hh)\n" +
				"      &\n" +
				"      closure1(rx) <: closure(hh)\n" +
				"     )\n" +
				"    )\n" +
				"  & closure(rx) = closure(closure(rx))\n" +
				"  & closure(rx) = closure1(closure(rx))\n" +
				"  & (closure(rx) ; closure(rx)) = closure(rx)\n" +
				"  & closure(rx)~ = closure(rx~)\n" +
				"  & (\n" +
				"     rx[dom(hh)] <: dom(hh)\n" +
				"     =>\n" +
				"     closure(rx)[dom(hh)] = dom(hh)\n" +
				"    )\n" +
				"  //& closure1(rx) = union({RANGE_LAMBDA__|#n.(n : 1 .. card(rx) & RANGE_LAMBDA__ = iterate(rx,n))})\n" +
				"  //& closure(rx) = union({RANGE_LAMBDA__|#n.(n : 0 .. card(rx) & RANGE_LAMBDA__ = iterate(rx,n))})\n" +
				"  & (closure1(rx) ; closure1(rx)) <: closure1(rx)\n" +
				"  & closure1(rx) = closure1(closure1(rx))\n" +
				"  & closure1(rx)~ = closure1(rx~)\n" +
				"  & (\n" +
				"     rx <: hh\n" +
				"     =>\n" +
				"     closure1(rx) <: closure1(hh)\n" +
				"    )\n" +
				"  & closure1(rx)[dom(hh)] = rx[dom(hh)] \\/ (closure1(rx) ; rx)[dom(hh)]\n" +
				"  //& closure1(rx)[dom(hh)] = union({RANGE_LAMBDA__|#n.(n : 1 .. card(rx) & RANGE_LAMBDA__ = iterate(rx,n)[dom(hh)])})\n" +
				"  & (\n" +
				"     rx[dom(hh)] <: dom(hh)\n" +
				"     =>\n" +
				"     closure1(rx)[dom(hh)] <: dom(hh)\n" +
				"    )\n" +
				"  & ff[{}] = {}\n" +
				"  & gg[{}] = {}\n" +
				"  & hh[{}] = {}\n" +
				"  & ff[dom(ff)] = ran(ff)\n" +
				"  & gg[dom(gg)] = ran(gg)\n" +
				"  & hh[dom(hh)] = ran(hh)\n" +
				"  & (ff~)[ran(ff)] = dom(ff)\n" +
				"  & (gg~)[ran(gg)] = dom(gg)\n" +
				"  & (hh~)[ran(hh)] = dom(hh)\n" +
				"  & (\n" +
				"     (ff * hh = gg * hh)\n" +
				"     <=>\n" +
				"     (\n" +
				"      (\n" +
				"       (\n" +
				"        ff = {}\n" +
				"        or\n" +
				"        hh = {}\n" +
				"       )\n" +
				"       &\n" +
				"       (\n" +
				"        gg = {}\n" +
				"        or\n" +
				"        hh = {}\n" +
				"       )\n" +
				"      )\n" +
				"      or\n" +
				"      ff = gg\n" +
				"     )\n" +
				"    )\n" +
				"  & (\n" +
				"     (id(ff) = id(gg))\n" +
				"     <=>\n" +
				"     (ff = gg)\n" +
				"    )\n" +
				"  & (\n" +
				"     id(ff) <: id(gg)\n" +
				"     <=>\n" +
				"     ff <: gg\n" +
				"    )\n" +
				"  & %fnc_x.(fnc_x : dom(ff)|ff[{fnc_x}]) : dom(ff) --> POW(ran(ff))\n" +
				"  & {rel_x,rel_y|rel_x : dom(ff) & rel_y : union(%fnc_x.(fnc_x : dom(ff)|ff[{fnc_x}])[{rel_x}])} = ff\n" +
				"  &\n" +
				"    !x.(\n" +
				"     x : dom(ff)\n" +
				"     =>\n" +
				"     %fnc_x.(fnc_x : dom(ff)|ff[{fnc_x}])(x) = ff[{x}]\n" +
				"    )\n" +
				"  & ff~ = {b,a|b : ran(ff) & a : dom(ff) & a |-> b : ff}\n" +
				"  & dom(ff) = {a|a : dom(ff) & #b.(b : ran(ff) & a |-> b : ff)}\n" +
				"  & ran(ff) = dom(ff~)\n" +
				"  & (ff ; ry) = {a,c|a : dom(ff) & c : ran(ry) & #b.(b : ran(ff) & a |-> b : ff & b |-> c : ry)}\n" +
				"  & id(dom(ff)) = {a,b|a: dom(ff) & b : dom(ff) & a |-> b : dom(ff) * dom(ff) & a = b}\n" +
				"  & dom(ff) <| gg = (id(dom(ff)) ; gg)\n" +
				"  & gg |> ran(ff) = (gg ; id(ran(ff)))\n" +
				"  & dom(ff) <<| gg = dom(gg) - dom(ff) <| gg\n" +
				"  & gg |>> ran(ff) = gg |> ran(gg) - ran(ff)\n" +
				"  & ran(ff) = {b|b : ran(ff) & #a.(a : dom(ff) & a |-> b : ff)}\n" +
				"  & dom(ff) <| gg = {a,b|a : dom(gg) & b : ran(gg) & a |-> b : gg & a : dom(ff)}\n" +
				"  & gg |> ran(ff) = {a,b|a : dom(gg) & b : ran(gg) & a |-> b : gg & b : ran(ff)}\n" +
				"  & dom(ff) <<| gg = {a,b|a : dom(gg) & b : ran(gg) & a |-> b : gg & a /: dom(ff)}\n" +
				"  & gg |>> ran(ff) = {a,b|a : dom(gg) & b : ran(gg) & a |-> b : gg & b /: ran(ff)}\n" +
				"  & ff[dom(gg)] = ran(dom(gg) <| ff)\n" +
				"  & gg <+ ff = (dom(ff) <<| gg) \\/ ff\n" +
				"  & ff >< gg = {a,bc|a : dom(gg) & bc : ran(ff) * ran(gg) & #(b,c).(b : ran(ff) & c : ran(gg) & bc = b |-> c & a |-> b : ff & a |-> c : gg)}\n" +
				"  & prj1(setX,setY) = (id(setX) >< setX * setY)~\n" +
				"  & prj2(setX,setY) = (setY * setX >< id(setY))~\n" +
				"  //& (ff || hh) = (prj1(setX,setX) ; ff) >< (prj2(setX,setX) ; hh)\n" +
				"  //& (ff || ry) = (prj1(setX,setY) ; ff) >< (prj2(setX,setY) ; ry)\n" +
				"  & ff[dom(gg)] = {b|b : ran(ff) & #a.(a : dom(gg) & a |-> b : ff)}\n" +
				"  & gg <+ ff = {a,b|a : dom(gg) & b : ran(gg) & ((a |-> b : gg & a /: dom(ff)) or a |-> b : ff)}\n" +
				"  & prj1(setX,setY) = prj1(setX,setY)\n" +
				"  & prj2(setX,setY) = prj2(setX,setY)\n" +
				"  //& (ff || hh) = {ab,cd|#(a,b,c,d).(ab = a |-> b & cd = c |-> d & a |-> c : ff & b |-> d : hh)}\n" +
				"  //& (ff || ry) = {ab,cd|#(a,b,c,d).(ab = a |-> b & cd = c |-> d & a |-> c : ff & b |-> d : ry)}\n" +
				"  & (ff ; ry)[dom(hh)] = ry[ff[dom(hh)]]\n" +
				"  & (\n" +
				"     (\n" +
				"      dom(hh) <: dom(ff)\n" +
				"      &\n" +
				"      ff~ : setY +-> setX\n" +
				"     )\n" +
				"     =>\n" +
				"     (ff ; ff~)[dom(hh)] = dom(hh)\n" +
				"    )\n" +
				"  & id(dom(hh))[dom(ff)] = dom(hh) /\\ dom(ff)\n" +
				"  & (\n" +
				"     (dom(hh) /\\ dom(ff)) /= {}\n" +
				"     =>\n" +
				"     (dom(hh) * dom(ry))[dom(ff)] = dom(ry)\n" +
				"    )\n" +
				"  & (\n" +
				"     dom(hh) /\\ dom(ff) = {}\n" +
				"     =>\n" +
				"     (dom(hh) * dom(ry))[dom(ff)] = {}\n" +
				"    )\n" +
				"  & (rx ; {}) = {}\n" +
				"  & {} = ({} ; rx)\n" +
				"  & (ff ; gg~) = {b,c|b : dom(ff) & c : dom(gg) & #a.(a : ran(ff) & b |-> a : ff & c |-> a : gg)}\n" +
				"  & (\n" +
				"     (ff~ ; ff) <: id(setY)\n" +
				"     <=>\n" +
				"     (ff : setX +-> setY)\n" +
				"    )\n" +
				"  & (rx ; (ff ; ry)) = ((rx ; ff) ; ry)\n" +
				"  & rx <: ((rx ; rx~) ; rx)\n" +
				"INITIALISATION\n" +
				"    BEGIN\n" +
				"      ff,gg,hh,rx,ry := {},{},{},{},{}\n" +
				"    END\n" +
				"OPERATIONS\n" +
				"  add_ff(xx,yy) =\n" +
				"    PRE\n" +
				"        xx : setX &\n" +
				"        yy : setY &\n" +
				"        (xx |-> yy) /: ff\n" +
				"    THEN\n" +
				"      ff := ff \\/ {xx |-> yy}\n" +
				"    END;\n" +
				"\n" +
				"  add_gg(xx,yy) =\n" +
				"    PRE\n" +
				"        xx : setX &\n" +
				"        yy : setY &\n" +
				"        (xx |-> yy) /: gg\n" +
				"    THEN\n" +
				"      gg := gg \\/ {xx |-> yy}\n" +
				"    END;\n" +
				"\n" +
				"  add_hh(xx,yy) =\n" +
				"    PRE\n" +
				"        xx : setX &\n" +
				"        yy : setX &\n" +
				"        (xx |-> yy) /: hh\n" +
				"    THEN\n" +
				"      hh := hh \\/ {xx |-> yy}\n" +
				"    END;\n" +
				"\n" +
				"  add_rx(xx,yy) =\n" +
				"    PRE\n" +
				"        xx : setX &\n" +
				"        yy : setX &\n" +
				"        (xx |-> yy) /: rx\n" +
				"    THEN\n" +
				"      rx := rx \\/ {xx |-> yy}\n" +
				"    END;\n" +
				"\n" +
				"  add_ry(xx,yy) =\n" +
				"    PRE\n" +
				"        xx : setY &\n" +
				"        yy : setY &\n" +
				"        (xx |-> yy) /: ry\n" +
				"    THEN\n" +
				"      ry := ry \\/ {xx |-> yy}\n" +
				"    END\n" +
				"/* DEFINITIONS\n" +
				"  PREDICATE otherlaw1;\n" +
				"  PREDICATE orderlaw5;\n" +
				"  PREDICATE cardlaw1;\n" +
				"  PREDICATE difflaw3;\n" +
				"  PREDICATE orderlaw9;\n" +
				"  PREDICATE difflaw1;\n" +
				"  PREDICATE orderlaw8;\n" +
				"  PREDICATE difflaw2;\n" +
				"  PREDICATE law15;\n" +
				"  PREDICATE law14;\n" +
				"  PREDICATE law13;\n" +
				"  PREDICATE law12;\n" +
				"  PREDICATE law11;\n" +
				"  PREDICATE law10;\n" +
				"  PREDICATE law4;\n" +
				"  PREDICATE law5;\n" +
				"  PREDICATE ff_is_pf;\n" +
				"  PREDICATE law2;\n" +
				"  PREDICATE existslaw2;\n" +
				"  PREDICATE law3;\n" +
				"  PREDICATE law8;\n" +
				"  PREDICATE law9;\n" +
				"  PREDICATE law6;\n" +
				"  PREDICATE foralllaw1;\n" +
				"  PREDICATE law7;\n" +
				"  PREDICATE foralllaw2;\n" +
				"  PREDICATE otherlaw6;\n" +
				"  PREDICATE orderlaw3;\n" +
				"  PREDICATE otherlaw5;\n" +
				"  PREDICATE cardlaw3;\n" +
				"  PREDICATE otherlaw4;\n" +
				"  PREDICATE cardlaw2;\n" +
				"  PREDICATE orderlaw1;\n" +
				"  PREDICATE otherlaw3;\n" +
				"  PREDICATE otherlaw2;\n" +
				"  PREDICATE cardlaw4;\n" +
				"  PREDICATE otherlaw1b;\n" +
				"  PREDICATE GOAL;\n" +
				"  PREDICATE setcomprlaw4;\n" +
				"  PREDICATE setcomprlaw1;\n" +
				"  PREDICATE law1; */\n" +
				"END\n";
		BProject project = check(machine);
		MachineNode machineNode = project.getMainMachine();
		checkType(machineNode);
	}
	
	private BProject check(String main, String... others) throws Exception {
		return Antlr4BParser.createBProjectFromMachineStrings(main, others);
	}

	private void checkType(MachineNode machineNode) throws TypeErrorException {
		TypeChecker typeChecker = new TypeChecker(machineNode);
	}

}
