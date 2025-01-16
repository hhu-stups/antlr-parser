package de.prob.parser;

import de.prob.parser.antlr.Antlr4BParser;
import de.prob.parser.antlr.BProject;
import de.prob.parser.ast.nodes.MachineNode;
import de.prob.parser.ast.nodes.predicate.PredicateOperatorNode;
import de.prob.parser.ast.nodes.predicate.PredicateOperatorWithExprArgsNode;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SemanticASTTest {
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
				"  & rx : POW(setX * setX)\n" +
				"  & (el1 |-> el1) : id(setX)\n" +
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
		check(machine);
	}

	@Test
	public void testExplicitComputations() throws Exception {
		String machine = "MACHINE ExplicitComputations\n" +
				"/* A machine with some explicitly given results for certain computations */\n" +
				"SETS\n" +
				"  PROC = {p1,p2,p3}; SINGLE={one}\n" +
				"CONSTANTS\n" +
				"  r, r2, d, cd, cr, s,\n" +
				"  x,y,z, u\n" +
				"PROPERTIES\n" +
				"  r = {p1 |-> p2, p2|-> p3} &\n" +
				"  r2 = {p1|->p3, p2|->p3} &\n" +
				"  d = {p1,p2} & cd = {p2,p3} &\n" +
				"  cr = {p1|->p2, p2|->p3, p1|->p3} &\n" +
				"  s = {1|->p1, 3|->p3, 2|->p2} &\n" +
				"  x = 3 & y = 3 & z = 3 &\n" +
				"  u = {{0,5,2,4}, {2,4,5}, {2,1,7,5}}\n" +
				"\n" +
				"INVARIANT\n" +
				"  p1=p1 & p1 /= p2 & not(p1=p2) &\n" +
				"\n" +
				"  r: d <-> cd & r: PROC +-> PROC &\n" +
				"  r: d --> cd & r/: PROC --> PROC & r/: d --> d &\n" +
				"  r: d >-> cd & r /: PROC >-> cd & r /: d >-> d &\n" +
				"  r : PROC +->> cd & r : d -->> cd &\n" +
				"  r: PROC >+> PROC & r/: PROC >+> {p3} &\n" +
				"  r: PROC >+>> cd & r: d >+>> cd & r: d >->> cd &\n" +
				"  r /: PROC --> PROC & r /: {p1,p2} --> {p1,p2} &\n" +
				"  r /: d +->> PROC & r /: d -->> PROC &\n" +
				"  r /: PROC >+>> PROC & r /: d >->> PROC &\n" +
				"  r /: {p2} +-> PROC & r /: PROC +-> {p2} &  r /: PROC +-> {p3} &  r /: PROC +-> {p1} &\n" +
				"  r /: PROC +-> {p1,p2} &\n" +
				"  fnc(r) = {p1|->{p2}, p2 |->{p3}} &\n" +
				"  fnc(r): dom(r) --> POW(ran(r)) &\n" +
				"  rel(fnc(r)) = r &\n" +
				"  rel(fnc({x,y|x:1..10 & y:1..x})) = {x,y|x:1..10 & y:1..x} &\n" +
				"\n" +
				"  /* some rules over the global sets */\n" +
				"\n" +
				"  {PROC} = {{p1,p2,p3}} &\n" +
				"  {(PROC - {p1}) \\/ {p1}} = {{p2,p3,p1,p2}} &\n" +
				"  card({ {p1,p2}, PROC, {p1,p3,p2}}) = 2 &\n" +
				"  {{PROC},{{p2,p3,p1}}} = {{{p3,p3,p1,p2}}} &\n" +
				"  {SINGLE} = {{one}} &\n" +
				"\n" +
				"  /* some rules for sets over singleton SET */\n" +
				"  one : {one} & one /: {} &\n" +
				"  !(x,s).(x:SINGLE & s<:SINGLE & x/:s => x=one & s={}) &\n" +
				"  !(x,s).(x:SINGLE & s<:SINGLE & x:s => x=one & s={one}) &\n" +
				"  not(#x.(x:SINGLE & (x=one => x/=one))) &\n" +
				"     (#x.(x:PROC & (x=p1 => x/=p1))) &\n" +
				"  (#x.(x:SINGLE & (x/=one => x=one))) &\n" +
				"  (#x.(x:SINGLE & (x=one => x=one))) &\n" +
				"\n" +
				"  /* testing reification */\n" +
				"  !zz.(zz:PROC => ( zz:{p1,p2} <=> (zz=p1 or zz=p2))) &\n" +
				"\n" +
				"  {1 |->p2, 2|->p2} : NATURAL1 +-> {p2} &\n" +
				"  {1 |->p2, 2|->p2} : 1..2 +-> {p2} &\n" +
				"  {1 |->p2, 2|->p2} /: 1..1 +-> {p2} &\n" +
				"  {1 |->p2, 2|->p2} : 0..3 +-> {p2} &\n" +
				"\n" +
				"  r2 : PROC <-> PROC & p1|->p3 : r2 &\n" +
				"  r2 /: PROC +-> {p1,p2} & r2 : PROC +-> PROC &\n" +
				"  card(r2) = 2 & dom(r2)={p1,p2} & ran(r2)={p3} &\n" +
				"   /* r2 will be {p1|->p3, p2|->p3} */\n" +
				"  r2 /: PROC >+> PROC &\n" +
				"\n" +
				"  {} <-> {} = { {} } &\n" +
				"  PROC <-> {} = { {} } &\n" +
				"  {} <-> PROC = { {} } &\n" +
				"  {p1} <-> {p2} = { {p1|->p2}, {} } &\n" +
				"  card({p1} <-> {p2}) = 2 &\n" +
				"  {p1} <-> {p2,p3} = { {p1|->p2}, {p1|->p3}, {} , {p1|->p2, p1|->p3} } &\n" +
				"  card({p1,p2,p3} <-> {p1,p2,p3}) = 2**(3*3) &\n" +
				"  {} +-> {} = { {} } &\n" +
				"  PROC +-> {} = { {} } &\n" +
				"  {} +-> PROC = { {} } &\n" +
				"  {p1} +-> {p2} = { {p1|->p2}, {} } &\n" +
				"  {p1} +-> {p2,p3} = { {p1|->p2}, {p1|->p3}, {} } &\n" +
				"  {} --> {} = { {} } &\n" +
				"  PROC --> {} = { } &\n" +
				"  {} --> PROC = { {} } &\n" +
				"  {p1} --> {p2} = { {p1|->p2} } &\n" +
				"  {p1} --> {p2,p3} = { {p1|->p2}, {p1|->p3} } &\n" +
				"  card({p1,p2,p3} --> {p1,p2,p3}) = 3**3 &\n" +
				"  card({p1,p2,p3} +-> {p1,p2,p3}) = 4**3 &\n" +
				"  card({p1,p3} --> {p1,p2,p3}) = 3**2 &\n" +
				"  card({p1,p3} +-> {p1,p2,p3}) = 4**2 &\n" +
				"  {p1} >+> {p2,p3} = { {}, {p1|->p2}, {p1|->p3} } &\n" +
				"  {p1} >-> {p2,p3} = { {p1|->p2}, {p1|->p3} } &\n" +
				"  {p1} -->> {p2,p3} = { } &\n" +
				"  {p1} +->> {p2,p3} = { } &\n" +
				"  {p1,p2} -->> {p3} = { {p1|->p3, p2|->p3} } &\n" +
				"  {p1,p2} +->> {p3} = { {p1|->p3}, {p2|->p3}, {p1|->p3, p2|->p3} } &\n" +
				"\n" +
				"  not(d=cd) & cd /= d &\n" +
				"  dom(r) = d &\n" +
				"  ran(r) = cd &\n" +
				"  d = {p2,p1} & d = {p2,p2,p1,p1,p2,p1} &\n" +
				"  d /= {} & {} /= d & d /= {p1,p2,p3} & PROC /= d & d /= {p1} & d /= {p2} &\n" +
				"  r(p1) = p2 & p2 = r(p1) & p3 = r(p2) & r(r(p1)) = p3 & p3 = r(r(p1)) &\n" +
				"  r[d] = cd &\n" +
				"  r[{}] = {} &\n" +
				"  r~[cd] = d &\n" +
				"  r~ = {p2|->p1, p3 |->p2} &\n" +
				"\n" +
				"  (r;r) = {p1|->p3} & (r;{}) = {} & ({};r) = {} & ({};{}) = {} &\n" +
				"  ((r;r);r) = {} & (r;(r;r)) = {} &\n" +
				"  ((r;r);r~) = {p1|->p2} &\n" +
				"  ({1|->11,11|->2,2|->22,22|->1} ; {1|->11,1|->111,2|->22}) = {22|->11,22|->111,11|->22} &\n" +
				"  r <+ {p1|->p3} = {p1|->p3, p2|->p3} &\n" +
				"  r <+ {} = r &\n" +
				"  {} <+ r = r &\n" +
				"  {p1|->p3} <+ r = r &\n" +
				"  id(d) = {p2|->p2, p1|->p1} &\n" +
				"  card(id(d)) = 2 &\n" +
				"  closure1(r) = cr &\n" +
				"  closure1(r~) = cr~ &\n" +
				"  #rr.(rr:BOOL<->BOOL & card(rr)=1 & iterate(rr,2)=rr) &\n" +
				"  #rr.(rr:BOOL<->BOOL & card(rr)=1 & iterate(rr,2)={}) &\n" +
				"  !rr.(rr:BOOL<->BOOL & card(rr)=1 & iterate(rr,2)=rr => (rr={TRUE|->TRUE} or rr={FALSE|->FALSE})) &\n" +
				"  !rr.(rr:BOOL<->BOOL & card(rr)=1 & iterate(rr,2)={} => (rr={TRUE|->FALSE} or rr={FALSE|->TRUE})) &\n" +
				"  #rr.(rr:BOOL<->BOOL & card(rr)=1 & closure1(rr)=rr) &\n" +
				"  !rr.(rr:BOOL<->BOOL & card(rr)=1 => closure1(rr)=rr) &\n" +
				"  #rr.(rr:BOOL<->BOOL & card(rr)=2 & closure1(rr)=rr) &\n" +
				"  #rr.(rr:BOOL<->BOOL & card(rr)=2 & closure1(rr)/=rr) &\n" +
				"  !rr.(rr:BOOL<->BOOL & card(rr)=2 & closure1(rr)/=rr => rr={TRUE|->FALSE,FALSE|->TRUE}) &\n" +
				"  card({rr|rr:BOOL<->BOOL & closure1(rr)=rr}) + card({rr|rr:BOOL<->BOOL & closure1(rr)/=rr}) = 16 &\n" +
				"  {rr|rr:BOOL<->BOOL & card(rr)=4 & closure1(rr)=rr} = {BOOL*BOOL} &\n" +
				"  closure1(%x.(x:1..1000000|x/2))[{1001}]  = {0,1,3,7,15,31,62,125,250,500} &\n" +
				"  closure1(%x.(x:1..10000|x/2))[{1001}]  = {0,1,3,7,15,31,62,125,250,500} &\n" +
				"  {x| closure1(%x.(x:1..1000|x/2))[{x}] = {0,1,2,4} & x:1..100} = {8,9} &\n" +
				"  iterate(r,1) = r &\n" +
				"  iterate(r,2) = (r;r) &\n" +
				"  iterate(r,3) = ((r;r);r) &\n" +
				"  prj1(PROC,PROC)(p1|->p2) = p1 &\n" +
				"  prj2(PROC,PROC)(p1|->p2) = p2 &\n" +
				"  p1 : d & p2: d  & not(p3:d) & p3 /: d &\n" +
				"  cr = r \\/ {p1|->p3} &\n" +
				"  d \\/ cd = PROC & d \\/ {} = d & {} \\/ d = d & d = d\\/d &\n" +
				"  {} \\/ {} = {} &\n" +
				"  d \\/ {p1} = d & d \\/ {p3} = PROC & {p3} \\/ d = PROC & {p1} \\/ d = d &\n" +
				"  d /\\ cd = {p2} & d /\\ {} = {} & {} /\\ d = {} &\n" +
				"  {} /\\ {} = {} &\n" +
				"  d /\\ {p2} = {p2} & {p2} /\\ d = {p2} &\n" +
				"  d - cd = {p1} &\n" +
				"  cd - d = {p3} &\n" +
				"  d /<: cd & d/<<: cd & not(d <: cd) & not(d <<: cd) &\n" +
				"  cd /<: d & cd /<<: d &\n" +
				"  d <<: PROC & d <: PROC & d<:d & d/<<:d &\n" +
				"  cd <<: PROC & cd <: PROC & cd <: cd & cd /<<:cd &\n" +
				"  {p1} <: {p1} & {p2,p3} <: {p3,p2,p1} &\n" +
				"  {1,3,5} <: 1..5 & {1,3,5} /<: 1..4 & {1,3,5} /<: 2..5 &\n" +
				"  d<|r = r & d<<|r = {} &\n" +
				"  r|>cd = r & r |>> cd = {} &\n" +
				"  cd <|r = {p2|->p3} &\n" +
				"  cd <<|r = {p1|->p2} &\n" +
				"  r|>d = {p1|->p2} &\n" +
				"  r|>>d = {p2|->p3} &\n" +
				"\n" +
				"  {p1} <<| {p1|->p2, p1|->p1, p2|->p2} = {p2|->p2} &\n" +
				"  {p2} <| {p1|->p2, p1|->p1, p2|->p2} = {p2|->p2} &\n" +
				"  {p1|->p2, p1|->p1, p2|->p2} |> {p1} = {p1|->p1} &\n" +
				"  {p1|->p2, p1|->p1, p2|->p2} |>> {p1} = {p1|->p2, p2|->p2} &\n" +
				"\n" +
				"  r >< r = { p1|->(p2,p2), p2|->(p3,p3) } &\n" +
				"  {p1|->1, p1|->2, p2|->3, p3|->4} >< {p1|->3,p1|->4} =\n" +
				"       { p1|->(1,3), p1|->(1,4), p1|->(2,3), p1|->(2,4)} &\n" +
				"  {p1|->1, p1|->2, p2|->3, p1|->4} >< {p2|->3, p3|->7} = { p2|->(3,3) } &\n" +
				"  {p1|->1, p1|->2, p2|->3, p3|->4} >< {p1|->3,p1|->4, p3|->7} =\n" +
				"       { p1|->(1,3), p1|->(1,4), p1|->(2,3), p1|->(2,4), p3|->(4,7)} &\n" +
				"  (r || r) = { (p1,p1)|->(p2,p2), (p1,p2)|->(p2,p3), (p2,p1)|->(p3,p2), (p2,p2)|->(p3,p3) } &\n" +
				"\n" +
				"  id({p1}) : PROC +-> PROC &\n" +
				"  id({p1}) : {p1} +-> {p1} &\n" +
				"  id({p1}) : {p1} --> PROC &\n" +
				"  id({p1}) : {p1} --> {p1} &\n" +
				"  id({p1}) /: PROC --> PROC &\n" +
				"  id({p1}) /: {p1,p2} --> PROC &\n" +
				"  id({p1}) /: {p1} --> {p2,p3} &\n" +
				"  id({p1,p3})[{p1,p2}] = {p1} &\n" +
				"  id({p1,p3})[{p3}] = {p3} &\n" +
				"  id((0..5))[(0..5)] = (0..5) &\n" +
				"  id({}) : (0..5) +-> NATURAL &\n" +
				"  id((0..5)) : (0..5) --> (0..5) &\n" +
				"  id((0..5)) : (0..5) --> NATURAL &\n" +
				"  id((0..5)) : NATURAL +-> (0..5) &\n" +
				"  id((0..5)) : NATURAL +-> NATURAL &\n" +
				"  //id(NATURAL1) : NATURAL +-> INTEGER &\n" +
				"  id(1..100000) : NATURAL1 +-> NATURAL1 &\n" +
				"  /*id(NATURAL) : NATURAL --> NATURAL &\n" +
				"  id(NATURAL) : NATURAL --> INTEGER &\n" +
				"  id(NATURAL) : NATURAL >+> NATURAL &\n" +
				"  id(NATURAL1) : NATURAL >+> NATURAL1 &\n" +
				"  id(NATURAL1) : NATURAL >+> NATURAL &\n" +
				"  id(NATURAL) /: NATURAL >+> NATURAL1 &\n" +
				"  id(NATURAL) : NATURAL >+>> NATURAL &\n" +
				"  id(NATURAL1) : NATURAL >+>> NATURAL1 &\n" +
				"  id(NATURAL) : NATURAL >-> NATURAL &\n" +
				"  id(NATURAL) : NATURAL >->> NATURAL &*/\n" +
				"     id((0..5)) /: NATURAL --> NATURAL &\n" +
				"     /*id(NATURAL) /: NATURAL1 --> INTEGER &\n" +
				"     id(NATURAL) /: NATURAL --> NATURAL1 &\n" +
				"     id(NATURAL) /: NATURAL --> 1..100000 &\n" +
				"     id(NATURAL) /: (0..5) +-> INTEGER &\n" +
				"     id(NATURAL1) : NATURAL1 >->> NATURAL1 &\n" +
				"     id(NATURAL) /: NATURAL >->> NATURAL1 &*/\n" +
				"     id({1,2,3}) /: 2..100000 +-> INTEGER &\n" +
				"     id(1..1000000) /: 1..999999 +-> INTEGER &\n" +
				"     id(1..100000) : 1..100001 +-> INTEGER &\n" +
				"     id(0..100000) /: NATURAL1 +-> NATURAL &\n" +
				"     id(0..100000) /: NATURAL +-> NATURAL1 &\n" +
				"     //id(INTEGER*INTEGER) /: ((-1..5)*(-1..5)) --> (INTEGER*INTEGER) &\n" +
				"     /* id(INTEGER*INTEGER) /: ((-1..5)*(-1..5)) +-> (INTEGER*INTEGER) &  subset check for cart products not yet implemented */\n" +
				"     //id(INTEGER*INTEGER) : (INTEGER*INTEGER) --> (INTEGER*INTEGER) &\n" +
				"     //id(INTEGER*INTEGER) : (INTEGER*INTEGER) +-> (INTEGER*INTEGER) &\n" +
				"     //id((-1..5)*INTEGER) : (INTEGER*INTEGER) +-> (INTEGER*INTEGER) &\n" +
				"     /* id((-1..5)*INTEGER) /: (NATURAL*INTEGER) +-> (INTEGER*INTEGER) & */\n" +
				"  //id(NATURAL)[1..10] = 1..10 &\n" +
				"  //id((0..5))~=id((0..5)) & id(NATURAL)~=id(NATURAL) &\n" +
				" /* ((0..5)*(0..5)) : NATURAL <-> INTEGER & (NATURAL*(0..5)) /: NATURAL <-> INTEGER : still expands ! */\n" +
				"\n" +
				" /* Set Comprehensions */\n" +
				"  {x|x:r} = r & {x,y|x|->y:r} = r &\n" +
				"  {y,x|x|->y:r} = r~ &\n" +
				"  card({y,x|x|->y:r}) = card(r) &\n" +
				"  card({y,x|x|->y:r}) = 2 &\n" +
				"  card({y,x|x|->y:r}) >= 2 &\n" +
				"  card({y,x|x|->y:r}) > 1 &\n" +
				"  {x| x <<: {y| y<<: {z | z:0..1}}} = {{}, {{}}, {{},{0}}, {{},{1}}, {{0}}, {{0},{1}}, {{1}}} &\n" +
				"  card({x| x <: {y| y<: {z | z:0..1}}}) = 16 &\n" +
				"  {x,y| x<:0..1 & y<:0..1 & x/<:y & y/<:x } = {({0}|->{1}),({1}|->{0})} &\n" +
				"  {x,y| (x:{y+3,y+5} or y:{x+2,x+4}) & x:0..6 & y:0..4} =\n" +
				"    {(0|->2),(0|->4),(1|->3),(2|->4),(3|->0),(4|->1),(5|->0),(5|->2),(6|->1),(6|->3)} &\n" +
				"  {x|x<:1..4 & card(x)>0 & x /\\ 3..4 /= {} & x /\\ 0..2 = {}}  =  {{3}, {3,4}, {4}} &\n" +
				"  {x|x<:1..10 & card(x)>0 & x /\\ 3..4 /= {} & x /\\ 0..2 = {} &\n" +
				"    (8..10) - x = 9..10 & 6 /: x & 7 : x & #v.(v:x & v mod 5 = 0) & card(x)<5} = {{3,5,7,8},{4,5,7,8}} &\n" +
				"  {xx | xx:{1|->TRUE,2|->TRUE,2|->FALSE} & (xx  :  {(2|->TRUE),(2|->FALSE),(3|->TRUE)} or xx  :  {(1|->TRUE),(1|->FALSE),(2|->TRUE),(2|->FALSE),(3|->TRUE),(3|->FALSE)})} =\n" +
				"  {1|->TRUE, 2|->TRUE, 2|->FALSE} &\n" +
				"  {x| x:1..10 & not(x..x+1 <: 3..8)} = {1,2,8,9,10} &\n" +
				"  {n | [0,1,2,3,4,5,6,7,8,9,10]: 1..n --> 0..10} = {11} &\n" +
				"  {n,m | [0,1,2,3,4,5,6,7,8,9,10]: 1..n --> 0..m & m:0..12} = {11|->10, 11|->11, 11|->12} &\n" +
				"\n" +
				" /* Powersets */\n" +
				"  POW(d) = {{},{p1},{p2},d} & POW({}) = { {} } &\n" +
				"  POW1(d) = {{p1},{p2},d} & POW1({}) =  {} &\n" +
				"  {} /: POW1(d) & {} : POW(d) & (d/={} => d:POW1(d)) & d:POW(d) &\n" +
				"  FIN(d) = {{},{p1},{p2},d} & FIN({}) = { {} } &\n" +
				"  FIN1(d) = {{p1},{p2},d} & FIN1({}) = {} &\n" +
				"  {} /: FIN1(d) & d : FIN1(d) &\n" +
				"  card(POW({p1,p2,p3})) = 2**3 &\n" +
				"  card(FIN({p1,p2,p3})) = 2**3 &\n" +
				"  card(POW1({p1,p2,p3})) = 2**3 - 1 &\n" +
				"  card(FIN1({p1,p2,p3})) = 2**3 - 1 &\n" +
				"  card(POW({ {}, {{}}, {{},{1}}, {{1}} })) = 16 &\n" +
				"  card(POW({ {}, {{}}, {{},{1}}, {{1}} }) - {{}}) = 15 &\n" +
				"  card(POW({ {}, {{}}, {{},{p1}}, {{p1}} }) - {{}}) = 15 &\n" +
				"  POW({ {}, {{}}, {{},{p1}}, {{p1}} }) - { {} } = POW1({ {}, {{}}, {{},{p1}}, {{p1}} }) &\n" +
				"\n" +
				"  /* Cartesian Product: */\n" +
				"  d*{p3} = {p1|->p3, p2|->p3} &\n" +
				//"  d*{} = {} & {}=d*{} & {}*{} = {} &\n" +
				"  d*d = {p1|->p1, p2|->p1, p1|->p2, p2|->p2} &\n" +
				"  p1|->p2 : d*d & p1|->p3 /: d*d &\n" +
				"  /*dom((NATURAL*INTEGER)) = NATURAL &\n" +
				"  dom(((1..3) * NATURAL1)) = 1..3 &\n" +
				"  dom((NATURAL * {})) = {} &\n" +
				"  ran((NATURAL*INTEGER)) = INTEGER &\n" +
				"  ran(((1..3) * NATURAL1)) = NATURAL1 &\n" +
				"  ran((NATURAL * (1..3))) = 1..3 &\n" +
				"  ran((NATURAL * {})) = {} &\n" +
				"  ran(({} * NATURAL)) = {} &*/\n" +
				"  card((1..3)*(1..4)) = 12 &\n" +
				"  //card((1..3)*{}) = 0 &\n" +
				"  card({1}*(1..4)) = 4 &\n" +
				"  card({ x | x <: {1,2,3}*{TRUE,FALSE}}) = 2**6 &\n" +
				"  card(id((1..100)*(1..100))) = 100*100 &\n" +
				"  card(id((1..1000)*(1..10000))) = 1000*10000 &\n" +
				"  card(id((1..1000)*(1..1000))~)=1000*1000 &\n" +
				"  card(((1..1000)*(1..1000))~)=1000*1000 &\n" +
				"  TRUE |-> 99 : ((98..100000)*BOOL)~ &\n" +
				"  TRUE |-> 97 /: ((98..100000)*BOOL)~ &\n" +
				"  {}*{} <: {(1,1)} &\n" +
				"  /*{1,2}*{3,4} <: NATURAL*NATURAL &\n" +
				"  (1..10000)*(1..10000) <: NATURAL*NATURAL1 &\n" +
				"  (1 .. 10000) * (0 .. 10000) /<: NATURAL * NATURAL1 &*/\n" +
				"  (1..1000)*(1..9000) : (1..1000)<->(1..9999) &\n" +
				"  (1..1000)*(1..9000) /: (1..1000)<->(2..9999) &\n" +
				"\n" +
				"  (\t{1}*{41,51} \\/  {2,11}*{41}\n" +
				"    \\/  {6, 7}*{51} \\/  {12}*{41,51}\n" +
				"    \\/  {12,13}*{101} \\/  {18,23}*{111}\n" +
				"    ) = {(1|->41),(1|->51),(2|->41),(6|->51),(7|->51),(11|->41),\n" +
				"         (12|->41),(12|->51),(12|->101),(13|->101),(18|->111),(23|->111)} &\n" +
				"  {x,y| x<:y & y<:0..1 & x*y={}} =\n" +
				"    {x,y| x<:y & y<:0..1 & x*y={}} &\n" +
				"  {x,y| ((x<<:y & y<:0..1) or (y<<:x & x<:0..1)) & x*y={}} =\n" +
				"    {({}|->{0}),({}|->{0,1}),({}|->{1}),({0}|->{}),({0,1}|->{}),({1}|->{})} &\n" +
				"  {x,y| ((x<<:y & y<:0..1) or (y<<:x & x<:0..1)) & x*y/={}} =\n" +
				"    {({0}|->{0,1}),({0,1}|->{0}),({0,1}|->{1}),({1}|->{0,1})} &\n" +
				"  //{x,y|y=card(x*INTEGER) & x<<:{TRUE}} = { {}|->0} & /* check that card of cartesian product of empty and infinite set is 0 */\n" +
				"  //card({}*INTEGER) = 0 &\n" +
				"  //card(seq(BOOL)*{}) = 0 &\n" +
				"\n" +
				"  /* generalized union/(-1..5)er */\n" +
				"  union({d,cd}) = PROC &\n" +
				"  {p1,p2,p3} = union({d,cd}) &\n" +
				"  union({d}) = d & union({}) = {} &\n" +
				"  inter({d,cd}) = {p2} &\n" +
				"  inter({d}) = d &  /* (-1..5)er({}) = {} & not well defined */\n" +
				"  {p2} = inter({d,cd}) &\n" +
				"  /* some rules about disjo(-1..5)'ness */\n" +
				"  not( {} = d /\\ cd ) &\n" +
				"  {} = d /\\ {p3} &\n" +
				"  {} = {p1} /\\ cd  & {} = {} /\\ cd  & {} = cd /\\ {}  & {} = {} /\\ {}  &\n" +
				"  #z.({} = {z} /\\ cd & z=p1) & /* force disjo(-1..5) Prolog predicate to activate */\n" +
				"  #z.({} = z /\\ cd & z={}) & /* force disjo(-1..5) Prolog predicate to activate */\n" +
				"  inter(u) = {2,5} &\n" +
				"  union(u) = {0,1,2,4,5,7} &\n" +
				"\n" +
				"  /* some rules using quantification */\n" +
				"  //#x.(x:INTEGER & {x} \\/ {1,2} = {1,2} ) &\n" +
				"  #x.(x:BOOL & {x} \\/ {TRUE} = {TRUE} ) &\n" +
				"  //#x.(x:BOOL*INTEGER & {x} \\/ {TRUE|->0, FALSE|->1} = {TRUE|->0, FALSE|->1} ) &\n" +
				"  !x.(x:{1,3,5} => (x=1 or x=3 or x=5)) &\n" +
				"  !x.(x:{1,3,5} => (x*x=1 or x*x=9 or x*x=25)) &\n" +
				"  !x.(x:{1,3,5} => #y.(y:{1,3,5} & x>=y)) &\n" +
				"  not(!x.(x:{1,3,5} => #y.(y:{1,3,5} & x>y))) &\n" +
				"  #x.(x:{1,3,5} & !y.(y:{1,3,5} => x>=y)) &\n" +
				"  #x.(x:{1,3,5} & x/: {1,2,4,5}) &\n" +
				"  #x.(x:{1,3,5} & x*x=9) &\n" +
				"  not(#x.(x:{1,3,5} & !y.(y:{1,3,5} => x>y))) &\n" +
				"  (not(#c.(c = 1)) => 1=2) &\n" +
				"  !x.(x:{1,3,5} => (x:{1,3} or x:{3,5})) &\n" +
				"\n" +
				" /* some BOOL */\n" +
				"  TRUE = TRUE & FALSE = FALSE & TRUE /= FALSE & FALSE /= TRUE & not(TRUE=FALSE) & not(FALSE=TRUE) & not(TRUE/=TRUE) & not(FALSE/=FALSE) &  not(not(TRUE=TRUE)) &\n" +
				"  (1=1) & (bool(1=2)=bool(2=3)) & not(bool(1=1)=bool(1=2)) &\n" +
				"  (TRUE=TRUE <=> FALSE=FALSE) & not(TRUE=FALSE <=> FALSE=FALSE) &\n" +
				"  (TRUE=TRUE => FALSE=FALSE) & (TRUE=FALSE => FALSE=TRUE) &\n" +
				"  (TRUE=FALSE => TRUE=TRUE) & not(TRUE=TRUE => FALSE=TRUE) &\n" +
				"  TRUE = bool(TRUE=TRUE) & FALSE = bool(TRUE=FALSE) &\n" +
				"  {x|bool(x**2=x**3) = bool(x**2/=x+x) & x:1..50} = {1,2} &\n" +
				"  {x,t| x:POW(0..1) & bool(x:POW(NATURAL1))=t} =\n" +
				"     {({}|->TRUE),({0}|->FALSE),({0,1}|->FALSE),({1}|->TRUE)} &\n" +
				"  {x,t| x:POW(1024..1025) & bool(x:POW(NATURAL1))=t} =\n" +
				"     {({}|->TRUE),({1024}|->TRUE),({1024,1025}|->TRUE),({1025}|->TRUE)} &\n" +
				"  {x,t| x<:1..120  & card(x)=2 & bool(x<:(2..4))=t & bool(x/<:(118..119))=t} =\n" +
				"     {({2,3}|->TRUE),({2,4}|->TRUE),({3,4}|->TRUE),({118,119}|->FALSE)} &\n" +
				"  {y,t|bool( #x.(x:2..6 & (x>y & x<y+y) ))=t & y:3..6} =\n" +
				"     {(3|->TRUE),(4|->TRUE),(5|->TRUE),(6|->FALSE)} &\n" +
				"  {z|bool(#(x,y).(x:1..2 & x+y=z & y:3..5))=bool(z:{1,2,3,5}) & z:0..10} =\n" +
				"     {0,5,8,9,10} &\n" +
				"  {z|bool(#(x,y).(x:1..2 & x+y=z & y:3..40))=\n" +
				"     bool(z:{1,2,3,5}) & z:0..10} =\n" +
				"     {0,5} &\n" +
				"  {z|bool(#(x,y).((x : 1 .. 2 & (z:{x + y,0,-1} <=> z>=0)) & y : 3 .. 40)) =\n" +
				"     bool(z : {-1,0,1,2,3,5}) & z : 0 .. 10} = {0,5} &\n" +
				"     bool(1=2 & 1=1 or 1=1) = TRUE & // associativity\n" +
				"     bool(1=2 & (1=1 or 1=1)) = FALSE &\n" +
				"\n" +
				"  /* some Arithmetic */\n" +
				"  PI(x).(x:{1,2,3}|x) = 6 &\n" +
				"  PI(x).(x:{1,2,5}|x+1) = 2*3*6 &\n" +
				"  PI(x).(x:{}|x) = 1 &\n" +
				"  PI(x).(x:1..3|x*x) = 36 &\n" +
				"  SIGMA(x).(x:{1,2,4}|x) = 7 &\n" +
				"  SIGMA(x).(x:{1,2,3,5}|x) = SIGMA(x).(x:{1,2,3,5}|x+1) - 4 &\n" +
				"  SIGMA(x).(x:{}|x) = 0 &\n" +
				"  SIGMA(x).(x:1..3|x) = 6 &\n" +
				"  SIGMA(x).(x:1..3|x*x) = 14 &\n" +
				"  SIGMA(x).(x : 1 .. 60000 & x > 0|1) = 60000 &\n" +
				"  SIGMA(x).(x:1..2000 \\/ {-1}|x) = SIGMA(x).(x:1..2000|x) - 1 &\n" +
				"  SIGMA(x).(x:-2000..2000|x) = 0 &\n" +
				"  SIGMA(x).(x:2..2|x) = 2 &\n" +
				"  SIGMA(x).(x:3..2|x) = 0 &\n" +
				"  SIGMA(x).(x:1..100|x) = 5050 &\n" +
				"  SIGMA(x).(x:2..100|x) = 5049 &\n" +
				"  SIGMA(x).(x:1..2**30|x) = 576460752840294400 & /* check that (-1..5)erval sum computed efficiently */\n" +
				"  //succ(1) = 2 & pred(2) = 1 &\n" +
				"  2+3 = 5 & 5 = 2+3 & 2+3 = 3+2 &\n" +
				"  2*3 = 6 & 3*2 = 2*3 &\n" +
				"  2*3 /= 2+3 &\n" +
				"  -(-2) = 2 &\n" +
				"  2+x = 5 & x=3 &  /* checking backwards computation */\n" +
				"  x*x = 9 & /* checking square */\n" +
				"  2*y = 6 & y=3 & /* checking backwards computation */\n" +
				"  2**z = 8 & z=3 & /* checking backwards computation */\n" +
				"  2**3 = 8 & 3 mod 2 = 1 & 2 mod 2 = 0 & 1 mod 2 = 1 &\n" +
				"  4/2 = 2 & 2 = 4/2 & 2 = 5/2 & 5/2 = 2 &\n" +
				"  3**0 = 1 & 0**2 = 0 & 0**0 = 1 &\n" +
				"  #zz.(zz:{0,1,2} & 0**zz = 1) &\n" +
				"  max({3,5,2,1}) = 5 & 4 = max({3,4,2}) &\n" +
				"  min({3,5,2,1}) = 1 & 2 = min({2,3,3}) &\n" +
				"  min(1..10) = 1 & max(1..10) = 10 & min((0..5))=0 & min((1..5)) = 1 & max((1..5)) = 5 &\n" +
				"  max((-1..5)) = 5 & min((-1..5)) = -1 & /*min(NATURAL1) = 1 & min(NATURAL) = 0 &*/\n" +
				"  card({1,2,3}) = 3 & 4 = card({1,2,3,4}) &\n" +
				"  card({2,3,3}) = 2 &\n" +
				"  card({2,3}) > 1 & 1 < card({2,3}) &\n" +
				"  card({2,3}) >= 2 & 2 <= card({2,3}) &\n" +
				"  2 = card({2,3,3}) & card({}) = 0 & card({3,5,2,1}) = 4 &\n" +
				"  1..3 = {1,2,3} &\n" +
				"  {3,2,1} = 1..3 & 1..3 = {3,1,2} & 1..0 = {} & {} = 1..0 &\n" +
				"  0 : (-1..5) & 0:(0..5) & 0 /: (1..5) & 0:NATURAL & 0/:NATURAL1 &\n" +
				"  1 : (-1..5) & 1:(0..5) & 1 : (1..5) & 1:NATURAL & 1:NATURAL1 &\n" +
				"  -1 : (-1..5) & -1 /: (0..5)  & -1 /: (1..5) & -1 /: NATURAL  & -1 /: NATURAL1 &\n" +
				"  (0:INTEGER <=> 0=0) & (0/:INTEGER <=> 0=1) &\n" +
				"  (-1-1:INTEGER <=> 0=0) & (-1-1/:INTEGER <=> 0=1) &\n" +
				"  (5+1:INTEGER <=> 0=0) & (5+1/:INTEGER <=> 0=1) &\n" +
				"  /*!(x,S).(S=NATURAL & x:{1,2} => x:S) &\n" +
				"  !(x,S).(S=NATURAL & x:{-1,-2} => x/:S) &\n" +
				"  !(x,S).(S=NATURAL1 & x:{1,2} => x:S) &\n" +
				"  !(x,S).(S=NATURAL1 & x:{-1,0} => x/:S) &*/\n" +
				"  5 : (-1..5) & 5+1 /: (-1..5) &\n" +
				"  5 : (0..5) & 5+1 /: (0..5) &\n" +
				"  -1 : (-1..5) & -1-1 /: (-1..5) &\n" +
				"  not( 0 /: (0..5)) & not( 0 /: (-1..5)) & not( 0 /: INTEGER) &\n" +
				"  not( 1 /: (1..5)) &\n" +
				"  0<1 & -1 < 0 & 0 <= 1 & 1<= 1 & 1>0 & 0>-1 & 1>=0 & 1>= 1 &\n" +
				"  not(1<0) & not (1<1) & not(1<=0) & not (0>1) & not(0>=1) & not(1>1) &\n" +
				"  not(not(0<1)) &\n" +
				"  //dom(pred) = INTEGER  &\n" +
				"  {x|x:-10..10  & x / 4 =0} = -3..3 &\n" +
				"  {x|x:-10..10  & x / 4 =1} = 4..7 &\n" +
				"  {x|x:-10..10  & x / 4 =-1} = -7..-4 &\n" +
				"  (-1) / 4 = 0 & /* this is different from Z and TLA */\n" +
				"     9/3*2 = 6 & // test associativity\n" +
				"     3*2/6 = 1 &\n" +
				"     8 / 2 * 4 = 16 &\n" +
				"     8 / 4 / 2 = 1 &\n" +
				"     2**3**2 = 512 &\n" +
				"\n" +
				"  /* (-1..5)erval sets */\n" +
				"  1..2 <: 1..2 &  2..0 <: 1..1 &\n" +
				"  1..2 <: NATURAL & 1..2 <: (0..5) & 1..2 <: (1..5) & 1..2 <: NATURAL1 & 1..2 <: (-1..5) &\n" +
				"  1..2 <: 1..3 & 1..2 <: 0..2 & 1..2 <: 0..3 &\n" +
				"  -1 .. 10 <: -2 .. 12 &\n" +
				"  10 : 10..99999 & 11 : 10..99999 & 99999 : 10..99999 &\n" +
				"  9 /: 10..99999 & (99999+1)/: 10..99999 & -1 /: 10..99999 &\n" +
				"  card(0..1) = 2 & card(0..0) = 1 & card(1..0) = 0 &\n" +
				"  card(99..-99)=0 &\n" +
				"  !x.(x:1..99 => ( x:5..10 <=> (x>=5 & x<=10) ) ) &\n" +
				"  !z.(z:1..49 => !x.(x:1..50 => ( x:5..z <=> (x>=5 & x<=z) ) )) &\n" +
				"  1..9999 = 1..9999 &\n" +
				"  2..9999 \\/ {1} = 1..9999 &\n" +
				"  2..9999 \\/ {1} /= 1..9998 &\n" +
				"  #(x,y).( ((1..10000)\\/{-1}) - {-1} = x..y) & /* check quick comparison between (-1..5)erval & AVL */\n" +
				"  (1..10000)\\/{0} = 0..10000 &\n" +
				"  (1..10000)\\/{-1} /= 0..10000 &\n" +
				"  ((1..10000) - {250}) /= 1..10000 &\n" +
				"  (1..999999) \\/ {0,2} = 0..999999 &\n" +
				"  (1..999999) \\/ {2,4,55} = 1..999999 &\n" +
				"  (1..999999) \\/ {0,2,77,999999+1} = 0..1000000 &\n" +
				"  (1..999999) \\/ {30,2,77,999999+1} = 1..1000000 &\n" +
				"  /*{0} \\/ NATURAL1 = NATURAL &\n" +
				"  (0..1000) \\/ NATURAL1 = NATURAL &\n" +
				"  NATURAL \\/ NATURAL1 = NATURAL &\n" +
				"   1..1000 \\/ NATURAL1 = NATURAL1 &\n" +
				"   NATURAL1 \\/ INTEGER = INTEGER &\n" +
				"   NATURAL1 \\/ {0,3,4,77} = NATURAL &\n" +
				"   NATURAL1 \\/ {0,3,4,77} /= NATURAL1 &\n" +
				"   {-1} \\/ INTEGER = INTEGER &\n" +
				"   INTEGER \\/ -100..100 = INTEGER &\n" +
				"   (0..5) \\/ NATURAL1 = NATURAL &*/\n" +
				"   #(i1,i2).(i1..i2 = {1024,1026,1025,1027}) &\n" +
				"   #(j1,j2).({1024,1026,1025,1027,1028} = j1..j2) &\n" +
				"   #(k1,k2).(k1..k2 = {1024}) &\n" +
				"   /* #(i1,i2).(i1..i2 = {}) & */\n" +
				"   //!(ii1,ii2).(ii1:INTEGER & ii2:INTEGER => ii1..ii2 /= {1024,1026,1027}) &\n" +
				"   {x1|x1:0..5 & x1>0} = (1..5) & {x2|x2:(0..5) & x2>=1} = (1..5) &\n" +
				"   /*{x1|x1:NATURAL & x1>0} = NATURAL1 & {x2|x2:NATURAL & x2>=1} = NATURAL1 &\n" +
				"   {x1|x1:NATURAL & x1>-1} = NATURAL &\n" +
				"   {x3|x3:NATURAL & x3<5} = (0..5) - {5} &\n" +
				"   #yy.({x4|x4:NATURAL & x4<yy} = 0..1024) &*/ /* should instantiate yy to 1025 */\n" +
				"   /* not(#yy.({x4|x4:NATURAL & x4<yy} = 1..1024)) & still enumerates */\n" +
				"   {x,y | x:0..3 & y:1..3 & not(not(x..y <<: 1..2))} = {1|->1, 2|->2, 2|->1, 3|->1, 3|->2} &\n" +
				"   {x,y | x:0..4 & y:1..3 & not(x..y <: 1..4)} = {0}*(1..3) &\n" +
				"   {x,y | x:0..3 & y:1..3 & not(x..y <: 1..2)} = {0}*(1..3) \\/ (1..3)*{3} &\n" +
				"   {x,y | x:100..103 & y:101..103 & not(x..y <: 101..102)}  = {100}*(101..103) \\/ (101..103)*{103} &\n" +
				"   {x,y | x:100..103 & y:101..103 & not(x..y <<: 101..102)}  = {(101|->102)} \\/ {100}*(101..103) \\/ (101..103)*{103} &\n" +
				"   /* sets containing (-1..5)ervals */\n" +
				"    {1..1000} = { {x|x: 1..1000 & x mod 2 = 0} \\/ {x|x: 1..1000 & x mod 2 = 1} }  &\n" +
				"    card({ (1..1000) , ({x|x: 1..1000 & x mod 2 = 0} \\/ {x|x: 1..1000 & x mod 2 = 1})}) = 1 &\n" +
				"    card({ii|#nn.(nn:601..609 & ii=1..nn)} \\/ {1..610}) = 10 &\n" +
				"    card({ii|#nn.(nn:601..609 & ii=1..nn)} \\/ {1..610} \\/\n" +
				"        {({x|x: 1..605 & x mod 2 = 0} \\/ {x|x: 1..605 & x mod 2 = 1})} ) = 10 &\n" +
				"    ({x|x: 1..605 & x mod 2 = 0} \\/ {x|x: 1..605 & x mod 2 = 1}) : {ii|#nn.(nn:601..609 & ii=1..nn)} &\n" +
				"\n" +
				"  //{ (1,2), (3,4), (4,4), (1024,1025), (0,0) } [ NATURAL1 ] = {2,4,1025} &\n" +
				"  { (1,2), (3,4), (4,4), (-1 ,33),(1,22) } [ {x|x<2} ] = {2,22,33} &\n" +
				"  { (1,2), (3,4), (4,4), (-1 ,33),(1,22) } [ 3..10000000 ] = {4} &\n" +
				"  { (1,2), (3,4), (4,4), (1024,1025) } [ INTEGER ] = {2,4,1025} &\n" +
				"\n" +
				"  /* complement sets */\n" +
				"  2 /: INTEGER - {2} & 3 : INTEGER - {2} & 333333 : INTEGER - {2} &\n" +
				"  5+2 : INTEGER - {0,1} &\n" +
				"  5 : INTEGER - (0..5) \\/ {5} &\n" +
				"  5-1 /: INTEGER - (0..5) \\/ {5} &\n" +
				"  /*2 /: (((INTEGER - {2}) - {3}) - {4}) &\n" +
				"  3 /: (((INTEGER - {2}) - {3}) - {4}) &\n" +
				"  4 /: (((INTEGER - {2}) - {3}) - {4}) &\n" +
				"  1 :  (((INTEGER - {2}) - {3}) - {4}) &\n" +
				"  3 :  (((INTEGER - {2}) - {3}) - {4}) \\/ {1,3} &\n" +
				"  333333 :  (((INTEGER - {2}) - {3}) - {4}) \\/ {1,3} &\n" +
				"  { 1|->2 , 2|->3, 3|->4} |> (INTEGER - {3}) = {1|->2, 3|->4} &\n" +
				" (INTEGER - {3}) <| { 1|->2 , 2|->3, 3|->4} = {1|->2, 2|->3} &\n" +
				" (INTEGER - {2}) <: INTEGER &*/\n" +
				"   {x| x/: {1,2,10000}} \\/ {2} = {z|z/:{1,10000}} &\n" +
				"  {x| x/: {1,2,10000}} \\/ {2} /= {z|z/:{1,10000,2}} &\n" +
				"  {x| x/: {1,2,10000}} \\/ {10000} /= {z|z/:{1,10000,2}} &\n" +
				"  {b | b=TRUE <=> {x| x/: {1,2,10000}} \\/ {10000} /= {z|z/:{1,10000,2}}} = {TRUE} &\n" +
				"  {x| x/: {1,2,10000}} \\/  {z|z/:{10000,2,3}} = {w|w/:{2,10000}} &\n" +
				"  1..1000 \\/  {z|z/:{10000,2,3}}  = {ww|ww/:{10000}} &\n" +
				"  1..1000 \\/  {z|z/:{10000,2,3}}  = {ww|ww/=10000} &\n" +
				"   {1,3,9999} \\/  {z|z/:{10000,2,3}}  = {ww|ww/:{2,10000}} &\n" +
				"\n" +
				" /* some infinite sets; should be detected as such by ProB */\n" +
				"  /*1024 : {n|n : NATURAL & n > 0}  &\n" +
				"  1024 : {n|n : NATURAL1 & n > 133}  &\n" +
				"  133 /: {n|n : NATURAL1 & n > 133}  &\n" +
				"  0 /: {n|n : NATURAL & n > 0}  &*/\n" +
				"  -200 : {xx| xx<10} &\n" +
				"  200 : {zz| zz>100} &\n" +
				"  {zz|zz>100} /= {} &\n" +
				"  /*card({x|x : NATURAL1 & x < 10024}) = 10023 &\n" +
				"  {x|x : NATURAL1 & x < 10024} = 1..10023 &\n" +
				"  {x|x : NATURAL1 & x > 10024 & x <= 22222} = 10025..22222 &\n" +
				"  {x|x : NATURAL & x >= 10024 &  22222 >= x } = 10024..22222 &\n" +
				"  2048 : {x|x:NATURAL & x>2047 & x<100000} &\n" +
				"  {x|x:NATURAL & x>2047 & x<100000} /\\ {y|y<4096} = 2048..4095 &\n" +
				"\n" +
				"  %x.(x:NATURAL|x+1) : NATURAL --> INTEGER &\n" +
				"  %x.(x:NATURAL1|x+1) : NATURAL +-> INTEGER &\n" +
				"  %x.(x:NATURAL1|x*x)[{200}] = {40000} &\n" +
				"  %x.(x:NATURAL1|x*x)[{0}] = {} &*/\n" +
				"\n" +
				"  {xx|xx<2000} /\\ {vv|vv<1000} = {ww|ww<1000} &\n" +
				"  {xx|xx<2000} \\/  {vv|vv<1000} = {ww|ww<2000} &\n" +
				"  {xx|xx<2000} \\/  {vv|vv<1000} /= {ww|ww<1000} &\n" +
				"  {xx|xx<2000} /\\  {vv|vv>1000} = 1001..1999 &\n" +
				"  {xx|xx<2000} \\/ 1000..3000 = {vv|vv<3001} &\n" +
				"  1000..3000 \\/ {xx|xx<2000} = {vv|vv<=3000} &\n" +
				"  1000..3000 \\/ {xx|xx>2000} = {vv|vv>999} &\n" +
				"\n" +
				"  /*INTEGER1 /\\ {-(2**40),0,2**40} = {2**40} &\n" +
				"  INTEGER1 /\\ {2**40} = {2**40} &\n" +
				"  INTEGER1 /\\ {} = {} &\n" +
				"  INTEGER1 /\\ {-(2**40)} = {} &\n" +
				"   {-(2**40),-1,0}  /\\ INTEGER1 = {} &\n" +
				"  {-(2**40),0,2**40} /\\ INTEGER1 = {2**40} &*/\n" +
				"  {xx|xx>101} /\\ {-(2**40),0,101,2**40} = {2**40} &\n" +
				"  {-(2**40),0,101,2**40} /\\ {xx|xx>101} = {2**40} &\n" +
				"\n" +
				"  //card( %(x,y).(x:INTEGER1 &y:INTEGER1|x*y) /\\ %(y,x).(y:-20000..50 & x:-100..50|x*y)) = 2500 &\n" +
				"\n" +
				"  /* cardinality of functions/relations */\n" +
				"  card((1..10) --> (5..6)) = 1024 &\n" +
				"  (card(((1..10) --> (5..6)) - {%uu.(uu:1..10|5)}) = 1023) &\n" +
				"  card((1..3) >+> (5..6)) = 13 &\n" +
				"  (card(((1..3) >+> (5..6)) \\/ {%uu.(uu:1..3|5)}) = 14) &\n" +
				"  card((1..3) >-> (5..6)) = 0 &\n" +
				"  (card(((1..3) >-> (5..6)) \\/ {%uu.(uu:1..3|5)}) = 1) &\n" +
				"\n" +
				"  /* the union {0|->0} forces expansion and compares symbolic calculation against enumeration */\n" +
				"  (card(((1..3) <-> (5..4)) \\/ { {0|->0} }) = 1+ card((1..3) <-> (5..4))) &\n" +
				"  (card(((1..4) +-> (5..7)) \\/ { {0|->0} }) = 1+ card((1..4) +-> (5..7))) &\n" +
				"  (card(((1..1) +-> (5..7)) \\/ { {0|->0} }) = 1+ card((1..1) +-> (5..7))) &\n" +
				"  (card(((1..3) +-> (5..4)) \\/ { {0|->0} }) = 1+ card((1..3) +-> (5..4))) &\n" +
				"  (card(((1..3) --> (5..4)) \\/ { {0|->0} }) = 1+ card((1..3) --> (5..4))) &\n" +
				"  (card(((1..3) -->> (5..4)) \\/ { {0|->0} }) = 1+ card((1..3) -->> (5..4))) &\n" +
				"  (card(((1..4) >+> (5..7)) \\/ { {0|->0} }) = 1+ card((1..4) >+> (5..7))) &\n" +
				"  (card(((1..1) >+> (5..7)) \\/ { {0|->0} }) = 1+ card((1..1) >+> (5..7))) &\n" +
				"  (card(((1..4) >+> (5..5)) \\/ { {0|->0} }) = 1+ card((1..4) >+> (5..5))) &\n" +
				"  (card(((1..4) >+> (5..4)) \\/ { {0|->0} }) = 1+ card((1..4) >+> (5..4))) &\n" +
				"  (card(((1..4) >-> (2..7)) \\/ { {0|->0} }) = 1+ card((1..4) >-> (2..7))) &\n" +
				"  (card(((1..4) >+>> (2..7)) \\/ { {0|->0} }) = 1+ card((1..4) >+>> (2..7))) &\n" +
				"  (card(((1..4) >->> (2..7)) \\/ { {0|->0} }) = 1+ card((1..4) >->> (2..7))) &\n" +
				"  (card(((1..4) >->> (2..5)) \\/ { {0|->0} }) = 1+ card((1..4) >->> (2..5))) &\n" +
				"  !(n,m).(n:0..3 & m:4..6 =>\n" +
				"     (card(((1..n) <-> (5..m)) \\/ { {0|->0} }) = 1+ card((1..n) <-> (5..m))) &\n" +
				"     (card(((1..n) +-> (5..m)) \\/ { {0|->0} }) = 1+ card((1..n) +-> (5..m))) &\n" +
				"     (card(((1..n) --> (5..m)) \\/ { {0|->0} }) = 1+ card((1..n) --> (5..m))) &\n" +
				"     (card(((1..n) >+> (5..m)) \\/ { {0|->0} }) = 1+ card((1..n) >+> (5..m))) &\n" +
				"     (card(((1..n) >-> (5..m)) \\/ { {0|->0} }) = 1+ card((1..n) >-> (5..m))) &\n" +
				"     (card(((1..n) -->> (5..m)) \\/ { {0|->0} }) = 1+ card((1..n) -->> (5..m))) &\n" +
				"     (card(((1..n) >+>> (5..m)) \\/ { {0|->0} }) = 1+ card((1..n) >+>> (5..m))) &\n" +
				"     (card(((1..n) >->> (5..m)) \\/ { {0|->0} }) = 1+ card((1..n) >->> (5..m)))  ) &\n" +
				"  card(1..10 -->> 1..2) = 1022 &\n" +
				"  card(1..10 +->> 1..1) = 1023 &\n" +
				"  card(1..6 >+>> 1..4) = 360 &\n" +
				"  /*card({}+->INTEGER) = 1 &\n" +
				"  card(INTEGER +-> {}) = 1 &\n" +
				"  card({} --> INTEGER) = 1 &\n" +
				"  card(INTEGER --> {}) = 0 &\n" +
				"  card({} +->> INTEGER) = 0 &\n" +
				"  card(INTEGER +->> {}) = 1 &\n" +
				"  card({} -->> INTEGER) = 0 &\n" +
				"  card(INTEGER -->> {}) = 0 &\n" +
				"  card({}>+>INTEGER) = 1 &\n" +
				"  card(INTEGER >+> {}) = 1 &\n" +
				"  card({}>->INTEGER) = 1 &\n" +
				"  card(INTEGER >-> {}) = 0 &\n" +
				"  card({}>->>INTEGER) = 0 &\n" +
				"  card(INTEGER >->> {}) = 0 &*/\n" +
				"\n" +
				"  /* Arithmetic with Boolean connectives to exercise b_(-1..5)erpreter_check */\n" +
				"\n" +
				"  !x.(x:0..3 =>  (x<3 <=> x<=2)) &\n" +
				"  !x.(x:0..3 =>  (x<=2 <=> x<3)) &\n" +
				"  !x.(x:0..3 =>  (x>2 <=> x>=3)) &\n" +
				"  !x.(x:0..3 =>  (x>=3 <=> x>2)) &\n" +
				"  !x.(x:0..3 =>  (x>3 <=> 1=2)) &\n" +
				"  !x.(x:0..3 =>  (x>0 <=> x/=0)) &\n" +
				"  #x.(x:0..3 &  (x<3 <=> 1=2)) &\n" +
				"  #x.(x:0..3 &  (1=2 <=> x<3)) &\n" +
				"  #x.(x:0..9 & ((x<5 & x>3) <=> 1=1))  &\n" +
				"  #x.(x:0..9 & ((x>8 or x<0) <=> 1=1))  &\n" +
				"  #x.(x:0..9 & ((x<=4 & x>=4) <=> 1=1))  &\n" +
				"  #x.(x:0..9 & ((x>=9 or x<=-1) <=> 1=1))  &\n" +
				"  #x.(x:0..9 & ((x<9 or x>9) <=> 1=2))  &\n" +
				"  #x.(x:0..9 & ((x<=8 or x>=10) <=> 1=2))  &\n" +
				"\n" +
				"  /* Sequences */\n" +
				"  s = [p1,p2,p3] &\n" +
				"  s : seq(PROC) &\n" +
				"\n" +
				"  2|->p2 : s & 2|->p1 /: s & {3|->p3} <<: s & {3|->p3} <: s & s<:s &  s/<<:s &\n" +
				"  ran(s) = PROC & dom(s) = 1..3 &\n" +
				"  s: seq1(PROC) & s:iseq(PROC) & s:iseq1(PROC) & s: perm(PROC) &\n" +
				"  s /: seq({p1}) & {2|->p1} /: seq(PROC) & {} : seq(PROC) & {1 |-> p1, 1|-> p2} /: seq(PROC) &\n" +
				"  s /: iseq({p1}) & [p1,p1] /: iseq(PROC) & {2|->p1} /: iseq({p1}) & {} : iseq({p1}) &\n" +
				"  {} : iseq({}) & {} : seq({}) & [] = {} & {} = [] &\n" +
				"  s /: iseq1({p1}) & [p1,p1] /: iseq1(PROC) & {2|->p1} /: iseq1({p1}) & {} /: iseq1({p1}) &\n" +
				"  s /: perm({p1}) & [p1,p1] /: perm({p1}) & {2|->p1} /: perm({p1}) & {} /: perm({p1}) &\n" +
				"  [p1,p2] /: perm(PROC) & [p3,p2,p1] : perm(PROC) &\n" +
				"  [p1] : perm({p1}) &\n" +
				"  <> : perm({}) &\n" +
				"  card(perm({})) = 1 &\n" +
				"  card(perm({p1})) = 1 &\n" +
				"  card(perm({p1,p2})) = 2 &\n" +
				"  card(perm({p1,p2,p3})) = 6 &\n" +
				"  card(perm(1..10)) = 3628800 &\n" +
				"  <> : iseq({}) &\n" +
				"  card(iseq({})) = 1 &\n" +
				"  card(iseq({p1})) = 2 &\n" +
				"  card(iseq({p1,p2})) = 5 &\n" +
				"  card(iseq({p1,p2,p3})) = 16 &\n" +
				"  iseq({p1,p2,p3}) = iseq1({p1,p2,p3}) \\/ {{}} &\n" +
				"  card(iseq1({p1,p2,p3}) \\/ {{}}) = 16 &\n" +
				"  iseq({p1,p2}) = { {}, [p1],[p2],[p1,p2],[p2,p1] } &\n" +
				"  iseq1({p1,p2}) = { [p1],[p2],[p1,p2],[p2,p1] } &\n" +
				"  card(iseq(1..4)) = 65 &\n" +
				"  card(iseq(1..5)) = 326 &\n" +
				"  card(iseq(1..6)) = 1957 &\n" +
				"  card(iseq1(1..3) \\/ {{}}) = 16 &\n" +
				"/*  card(iseq1(1..6) \\/ {{}}) = 1957 &  (only works if 5 set to at least 6) */\n" +
				"  card(iseq(1..7)) = 13700 &\n" +
				"  card(iseq1({})) = 0 &\n" +
				"  card(iseq1({p1})) = 1 &\n" +
				"  card(iseq1({p1,p2})) = 4 &\n" +
				"  card(iseq1({p1,p2,p3})) = 15 &\n" +
				"  card(iseq1(1..6)) = 1956 &\n" +
				"  <> : seq({}) &\n" +
				"  card(seq({})) = 1 &\n" +
				"  card(seq1({})) = 0 &\n" +
				"  [p1] = {1|->p1} &\n" +
				"  [] : perm({}) &\n" +
				"  size(s) = 3 &\n" +
				"  first(s) = p1 & last(s) = p3 &\n" +
				"  front(s) = [p1,p2] &\n" +
				"  tail(s) = [p2,p3] &\n" +
				"  s = [p1]^[p2,p3] & s = [] ^ s & s = s ^ [] &\n" +
				"  s = [p1,p2] ^ [p3] &\n" +
				"  rev(s) = [p3,p2,p1] &\n" +
				"  s = rev(rev(s)) &\n" +
				"  conc([[p1],[],[p2],[p3],[]]) = s &\n" +
				"  conc([[],[],[p1,p2],[],[p3],[]]) = s &\n" +
				"  conc([s]) = s &\n" +
				"  conc([]) = [] &\n" +
				"  s /|\\ 1 = [p1] & s /|\\ 2 = [p1,p2] & s /|\\ 3 = s & s /|\\ 0 = [] & /* allowed in Atelier B: s /|\\ 4 = s & */\n" +
				"  s \\|/ 0 = s &  s \\|/ 1 = [p2,p3] &  s\\|/ 2 = [p3] & s\\|/3 = [] &  /* not allowed: s \\|/ 4 = [] & */\n" +
				"  s = (p1 -> [p2]) <- p3 & s = p1 -> ([p2] <- p3) &\n" +
				"  //([1,2,3,4,5,6] ; pred) = [0,1,2,3,4,5] & /* test that we can apply infinite function to set */\n" +
				"  //([1001,1002,1003,1004,1005,1006] ; pred ; pred) = [999,1000,1001,1002,1003,1004] &\n" +
				"  //([1001,1002,1003,1004,1005,1006] ; %x.(x:INTEGER|x+x)) = [2002,2004,2006,2008,2010,2012] &\n" +
				"\n" +
				"  /* image of infinite functions */\n" +
				"  //pred[1..100] = 0..99 &\n" +
				"  //succ[1..100] = 2..101 &\n" +
				"  //SIGMA(y).(y:%x.(x:INTEGER|x*x)[1..10]|y) = 385 &\n" +
				"  //SIGMA(y).(y:%x.(x:INTEGER|x*x)[1..100]|y) = 338350 &\n" +
				"  //%x.(x:INTEGER|x*x)[-2..2] = {0,1,4} &\n" +
				"  //max(%x.(x:INTEGER|x*x)[-20..100]) = 10000 &\n" +
				"  //%i.(i:INTEGER|i/2)[pred[ran(%i.(i:1..30|i*2) ; succ)]] = 1..30 &\n" +
				"  //%i.(i:NATURAL1|1000/i)[-1..10] = {100,111,125,142,166,200,250,333,500,1000} &\n" +
				"  //([3,2,1,0,-1] ; %i.(i:NATURAL1|1000/i)) = [333,500,1000] &\n" +
				"\n" +
				"  /* other infinite functions checks */\n" +
				"  //%x.(x:INTEGER|{x}) : INTEGER --> POW(INTEGER) &\n" +
				"  %x.(x:1..99999|{x}) : 1..99999 --> POW(INTEGER) &\n" +
				"  //%x.(x:INTEGER|{x|->x}) : INTEGER --> (INTEGER <-> INTEGER) &\n" +
				" // %x.(x:1..99999|{x|->x}) : 1..99999 --> (INTEGER <-> INTEGER) &\n" +
				"\n" +
				"   /*POW(NATURAL1) <: POW(NATURAL) &\n" +
				"   POW1(NATURAL1) <: POW(NATURAL) &\n" +
				"   FIN(NATURAL1) <: POW(NATURAL) &\n" +
				"   FIN(NATURAL1) <: POW(NATURAL1) &\n" +
				"   FIN1(NATURAL1) <: FIN(NATURAL) &\n" +
				"   POW(POW(NATURAL1)) <: POW(POW(NATURAL)) &\n" +
				"   POW(POW(NATURAL)) /<: POW(POW(NATURAL1)) &\n" +
				"   FIN(POW1(NATURAL)) /<: FIN1(POW1(INTEGER)) &*/\n" +
				"\n" +
				"   /* some finite checks */\n" +
				"   {x|x:10..100 or x<3} /: FIN(INTEGER) &\n" +
				"   {x|x:10..100 or x:200..30000} : FIN(INTEGER) &\n" +
				"\n" +
				"   /* some other checks */\n" +
				"   #e.(e : {1|->2, 2|->3} & prj1(INTEGER,INTEGER)(e)=2) &\n" +
				"   card({e|e : {1|->2, 2|->3, 2|->4} & prj1(INTEGER,INTEGER)(e)=2 }) = 2 &\n" +
				"   {s1,s2|s1 |-> s2 : {(1,2),(2,3),(3,5)} &\n" +
				"     bool({(1,100)}([\"VIA_1\"]~([\"VIA_1\",\"VIA_1\",\"VIA_1\"](s1))) <= {(1,700)}([\"VIA_1\"]~([\"VIA_1\",\"VIA_1\",\"VIA_1\"](s1)))) =\n" +
				"      bool([0,100,200,300,400,500,600,700](s1) <= [0,100,200,300,400,500,600,700](s2))}\n" +
				"     = [2,3,5] &\n" +
				"    {a,b | a:2..5 & b:1..4 & (a>=b <=> a=2*b)} =\n" +
				"       {(2,1),(4,2),  (2,3),(2,4),(3,4) } &\n" +
				"    {a,b | a:2..5 & b:1..4 & bool(a>=b) = bool(a=2*b)} =\n" +
				"       {(2,1),(4,2),  (2,3),(2,4),(3,4) } &\n" +
				"    {a,b | a:2..5 & b:1..4 & bool(a<b) /= bool(a=2*b)} =\n" +
				"       {(2,1),(4,2),  (2,3),(2,4),(3,4) } &\n" +
				"    {S|#(a,b,c,d).(S={(a,b),(c,d)} & {a,c} = 1..2 & {b,d} = 33..34)} =\n" +
				"       {{(1|->33),(2|->34)},{(1|->34),(2|->33)}} &\n" +
				"\n" +
				"  /* some performance tests */\n" +
				"  {x|#(y,v).(x={y,v,3,2,1} & 1 /: x & y:0..2**15 & v:0..2**15)} = {} /* ensure that we detect 1:x straight away without enumerating y,v */\n" +
				"  &\n" +
				"  {x,S,S2|x:S & S<:1..213 & S \\/ {x} = S2 & x/: S2} = {}  /* check that we detect impossibility directly */\n" +
				"  &\n" +
				"  {n| #x.(x<:(BOOL*BOOL*BOOL) & card(x)=n)} = 0..8\n" +
				"  &\n" +
				"  card({f|f: BOOL*BOOL +-> 1..10 & ran(f) <: 3..(card(f))}) = 21 &\n" +
				"  {f|f: BOOL*BOOL +-> 1..10 & ran(f) <: 4..(card(f))} =\n" +
				"     {{},{((FALSE|->FALSE)|->4),((FALSE|->TRUE)|->4),((TRUE|->FALSE)|->4),((TRUE|->TRUE)|->4)}} &\n" +
				"  {f,n|f: 1..3 >->> 1..n} =\n" +
				"     {([2,1,3]|->3),([3,1,2]|->3),([1,2,3]|->3),\n" +
				"      ([3,2,1]|->3),([1,3,2]|->3),([2,3,1]|->3)} &\n" +
				"  card({f,n,g,m| f:2001..n >->> POW(BOOL) & g:1000..m >->> dom(f)}) = 24*24 &\n" +
				"  {f,n,g,m| f:2001..n >->> BOOL & g:1000..m >->> dom(f) & (g;f) : dom(g) >->> BOOL}\n" +
				"    = {((({(2001|->FALSE),(2002|->TRUE)}|->2002)|->{(1000|->2001),(1001|->2002)})|->1001),\n" +
				"       ((({(2001|->FALSE),(2002|->TRUE)}|->2002)|->{(1000|->2002),(1001|->2001)})|->1001),\n" +
				"       ((({(2001|->TRUE),(2002|->FALSE)}|->2002)|->{(1000|->2001),(1001|->2002)})|->1001),\n" +
				"       ((({(2001|->TRUE),(2002|->FALSE)}|->2002)|->{(1000|->2002),(1001|->2001)})|->1001)} &\n" +
				"  {on,sol|on = {\"o\"|-> %x.(x:1..7|8-x), \"d\"|->{}, \"m\" |->{}} &\n" +
				"          sol = {d,f,t|on(f)/={} & t/=f & on(t)={} &\n" +
				"                 d=last(on(f))}} =\n" +
				"  {({(\"d\"|->{}),(\"m\"|->{}),(\"o\"|->[7,6,5,4,3,2,1])}|->{((1|->\"o\")|->\"d\"),((1|->\"o\")|->\"m\")})}&\n" +
				"  {on,sol|on = {\"o\"|-> %x.(x:1..7|8-x), \"d\"|->{}, \"m\" |->{}} & sol = {d,f,t|on(f)/={} & t/=f & on(t)={} & d=on(f)(card(on(f)))}} =\n" +
				"  {({(\"d\"|->{}),(\"m\"|->{}),(\"o\"|->[7,6,5,4,3,2,1])}|->{((1|->\"o\")|->\"d\"),((1|->\"o\")|->\"m\")})} &\n" +
				"{x| {{{{{{{{{{{{{{{{{{{{{{x}}}}}}}}}}}}}}}}}}}}}}={{{{{{{{{{{{{{{{{{{{{{1}}}}}}}}}}}}}}}}}}}}}}} = {1} &\n" +
				"{x| {{{{{{{{{{{{{{{{{{{{{{x}}}}}}}}}}}}}}}}}}}}}}<:{{{{{{{{{{{{{{{{{{{{{{{1}}}}}}}}}}}}}}}}}}}}}}}} = {{1}} &\n" +
				"{x,y| x=bool(bool(y=x)=bool(y=TRUE or y=FALSE))} = {x,y|x:BOOL & y=TRUE} &\n" +
				"{z| bool(bool(z:1..20)=bool(3*3=9)) = bool(bool(z:5..6)=bool(1+1=2)) & bool(z:0..21) = bool(2+2=4)} = {0,5,6,21} &\n" +
				"{x,n|x:1..n-->BOOL & !i.(i:1..(n-2) => (x(i)=TRUE <=> (x(i+1)=FALSE & x(i+2)=FALSE)))\n" +
				"     & n=32 & x(1)=FALSE} =\n" +
				"    {([FALSE,FALSE,TRUE,FALSE,FALSE,TRUE,FALSE,FALSE,TRUE,FALSE,FALSE,TRUE,FALSE,\n" +
				"       FALSE,TRUE,FALSE,FALSE,TRUE,FALSE,FALSE,TRUE,FALSE,FALSE,TRUE,FALSE,\n" +
				"       FALSE,TRUE,FALSE,FALSE,TRUE,FALSE,FALSE]|->32),\n" +
				"    ([FALSE,TRUE,FALSE,FALSE,TRUE,FALSE,FALSE,TRUE,FALSE,FALSE,TRUE,\n" +
				"      FALSE,FALSE,TRUE,FALSE,FALSE,TRUE,FALSE,FALSE,TRUE,FALSE,\n" +
				"      FALSE,TRUE,FALSE,FALSE,TRUE,FALSE,FALSE,TRUE,FALSE,FALSE,TRUE]|->32)} &\n" +
				"\n" +
				" tail({v,r|v:1..10 & r={y|y:1..10 & #z.(z:{y,y+1} & z>v)}} ) =\n" +
				"    [{2,3,4,5,6,7,8,9,10},{3,4,5,6,7,8,9,10},{4,5,6,7,8,9,10},\n" +
				"     {5,6,7,8,9,10},{6,7,8,9,10},{7,8,9,10},{8,9,10},{9,10},{10}] &\n" +
				" f: PROC +-> PROC\n" +
				"VARIABLES f\n" +
				"INITIALISATION f:= {}\n" +
				"OPERATIONS\n" +
				" ovrid1(p) = PRE p:PROC THEN f(p) := p END;\n" +
				" ovrid2(p) = PRE p:PROC THEN f(p) := p2 END;\n" +
				" res <-- GetInter = BEGIN res := inter({d,cd}) END;\n" +
				" res <-- become_such(p) = PRE p:PROC & p=p1 THEN res : (res/=p) END\n" +
				"END\n";
		check(machine);
	}

	@Test
	public void testCore() throws Exception {
		String machine = "MACHINE\n" +
				"    Core\n" +
				"\n" +
				"SETS\n" +
				"    USERS ;\n" +
				"    ROLES;\n" +
				"    SESSIONS;\n" +
				"    ACTIONS;\n" +
				"    RESSOURCES\n" +
				"\n" +
				"\n" +
				"VARIABLES\n" +
				"    Users,\n" +
				"    Roles,\n" +
				"    Sessions,\n" +
				"    Actions,\n" +
				"    Ressources,\n" +
				"    Permissions,\n" +
				"    PA,\n" +
				"    UA,\n" +
				"    User_sessions,\n" +
				"    Session_roles,\n" +
				"    Assigned_users,\n" +
				"    Assigned_permissions\n" +
				"\n" +
				"\n" +
				"INVARIANT\n" +
				"    Users <: USERS &\n" +
				"    Roles <: ROLES &\n" +
				"    Sessions <: SESSIONS &\n" +
				"    Actions <: ACTIONS &\n" +
				"    Ressources <: RESSOURCES &\n" +
				"    /* Permissions <: PERMISSIONS & */\n" +
				"    Permissions <: Actions * Ressources &\n" +
				"    User_sessions : Users <-> Sessions &\n" +
				"    User_sessions~: Sessions --> Users &\n" +
				"\n" +
				"    PA <: Permissions * Roles  &\n" +
				"    UA <: Users * Roles &\n" +
				"    Session_roles : Sessions --> POW(Roles) &\n" +
				"    Assigned_users : Roles --> POW(Users) &\n" +
				"    !role.(role:Roles => Assigned_users(role) = UA~[{role}]) &\n" +
				"    Assigned_permissions : Roles --> POW(Permissions) &\n" +
				"    !(role).(role:Roles => Assigned_permissions(role) = PA~[{role}])\n" +
				"\n" +
				"\n" +
				"\n" +
				"INITIALISATION\n" +
				"\n" +
				"    Users := {} ||\n" +
				"    Roles := {} ||\n" +
				"    Sessions := {} ||\n" +
				"    Actions := ACTIONS ||\n" +
				"    Ressources := RESSOURCES ||\n" +
				"    Permissions := ACTIONS*RESSOURCES ||\n" +
				"    PA := {} ||\n" +
				"    UA := {} ||\n" +
				"    User_sessions := {} ||\n" +
				"    Session_roles := {} ||\n" +
				"    Assigned_users := {} ||\n" +
				"    Assigned_permissions := {}\n" +
				"\n" +
				"\n" +
				"OPERATIONS\n" +
				"    /*\n" +
				"    * AddUser (user) :\n" +
				"    * Ajoute un sujet user dans l'ensemble des sujets\n" +
				"    * et l'ajoute dans l'association user_sessions\n" +
				"    */\n" +
				"    AddUser (user) =\n" +
				"    PRE\n" +
				"        user:USERS & user/:Users\n" +
				"    THEN\n" +
				"        Users := Users \\/ {user}\n" +
				"    END;\n" +
				"\n" +
				"\n" +
				"\n" +
				"    /*\n" +
				"    * DeleteUser (user)\n" +
				"    * Supprime un sujet\n" +
				"    *\n" +
				"    */\n" +
				"\n" +
				"    DeleteUser (user) =\n" +
				"    PRE\n" +
				"        user : USERS & user : Users\n" +
				"    THEN\n" +
				"        Sessions := Sessions - User_sessions[{user}]\n" +
				"        ||\n" +
				"        User_sessions := {user} <<| User_sessions\n" +
				"        ||\n" +
				"        Session_roles := User_sessions[{user}] <<| Session_roles\n" +
				"        ||\n" +
				"        Assigned_users := %role.(role:Roles & role : dom(Assigned_users)|Assigned_users(role)-{user})\n" +
				"        ||\n" +
				"        UA := {user}<<|UA\n" +
				"        ||\n" +
				"        Users := Users - {user}\n" +
				"    END;\n" +
				"\n" +
				"\n" +
				"\n" +
				"    /*\n" +
				"    * AddRole (role) :\n" +
				"    * Ajoute un nouveau role ssi role n'est pas dans Roles\n" +
				"    *\n" +
				"    */\n" +
				"\n" +
				"    AddRole (role) =\n" +
				"    PRE\n" +
				"        role : ROLES & role/:Roles\n" +
				"    THEN\n" +
				"        Roles := Roles \\/ {role}\n" +
				"        ||\n" +
				"        Assigned_users := Assigned_users \\/ {role|->{}}\n" +
				"        ||\n" +
				"        Assigned_permissions  := Assigned_permissions \\/ {role |-> {}}\n" +
				"    END;\n" +
				"\n" +
				"    DeleteRole (role) =\n" +
				"    PRE\n" +
				"        role : ROLES & role: Roles\n" +
				"    THEN\n" +
				"        User_sessions := User_sessions |>> {sess | sess : dom(Session_roles) & role : Session_roles(sess)}\n" +
				"        ||\n" +
				"        Session_roles := {sess | sess : dom(Session_roles) & role : Session_roles(sess)} <<| Session_roles\n" +
				"        ||\n" +
				"        Sessions := Sessions - {sess|sess:dom(Session_roles) & role:Session_roles(sess)}\n" +
				"        ||\n" +
				"        UA := UA |>> {role}\n" +
				"        ||\n" +
				"        Assigned_users := {role} <<| Assigned_users\n" +
				"        ||\n" +
				"        PA := PA |>> {role}\n" +
				"        ||\n" +
				"        Assigned_permissions := {role} <<| Assigned_permissions\n" +
				"        ||\n" +
				"        Roles := Roles - {role}\n" +
				"      END;\n" +
				"    /*\n" +
				"    * AssignUser (user,role) :\n" +
				"    * Assigne un rôle à un sujet\n" +
				"    * Met à jour de UA et de Assigned_users\n" +
				"    */\n" +
				"\n" +
				"    AssignUser ( user , role) =\n" +
				"    PRE\n" +
				"        user : USERS & user : Users & role : ROLES & role : Roles & user|->role/:UA\n" +
				"    THEN\n" +
				"        UA := UA \\/ {user|->role}\n" +
				"        ||\n" +
				"        Assigned_users := {role} <<| Assigned_users \\/ {role|->(Assigned_users(role)\\/{user})}\n" +
				"    END;\n" +
				"\n" +
				"\n" +
				"    /*\n" +
				"    * DeassignUser (user,role) :\n" +
				"    * Enlève un rôle d'un sujet\n" +
				"    * Met à jour UA et Assigned_users\n" +
				"    * Supprime également les sessions en rapport avec le sujet user possédant parmi ses rôles actif role\n" +
				"    */\n" +
				"    DeassignUser (user , role) =\n" +
				"    PRE\n" +
				"        user : USERS & user : Users & role : ROLES & role : Roles  & user |-> role :UA\n" +
				"    THEN\n" +
				"    User_sessions := User_sessions |>> {sess | sess: dom(Session_roles) & role : Session_roles(sess) & user=User_sessions~(sess)}\n" +
				"        ||\n" +
				"   Sessions := Sessions - {sess|sess:dom(Session_roles) & role:Session_roles(sess) & user=User_sessions~(sess)}\n" +
				"        ||\n" +
				"   Session_roles := {sess|sess:dom(Session_roles) & role:Session_roles(sess) & user=User_sessions~(sess)} <<| Session_roles\n" +
				"        ||\n" +
				"        UA := UA - {user|->role}\n" +
				"        ||\n" +
				"    Assigned_users := {role} <<| Assigned_users \\/ {role|->(Assigned_users(role) - {user})}\n" +
				"\n" +
				"    END;\n" +
				"\n" +
				"    /*\n" +
				"    * GrantPermission(ress,action,role)\n" +
				"    * Permet à un rôle role d'effectuer l'action action sur la ressource ress\n" +
				"    *\n" +
				"    */\n" +
				"\n" +
				"    GrantPermission (ress,action,role) =\n" +
				"    PRE\n" +
				"        ress: RESSOURCES & action : ACTIONS & role : ROLES & role : Roles & action|->ress : Permissions & action|->ress|->role /: PA\n" +
				"    THEN\n" +
				"        PA := PA \\/ {action|->ress|->role}\n" +
				"        ||\n" +
				"        Assigned_permissions := {role} <<| Assigned_permissions\\/ {role|->(Assigned_permissions(role)\\/{action|->ress})}\n" +
				"    END  ;\n" +
				"\n" +
				"\n" +
				"    /*\n" +
				"    * RevokePermission(action,ress,role)\n" +
				"    * révoque la permission\n" +
				"    *\n" +
				"    */\n" +
				"\n" +
				"    RevokePermission (action,ress,role) =\n" +
				"    PRE\n" +
				"        action : ACTIONS & ress : RESSOURCES & role : ROLES & role : Roles & action|->ress : Permissions & action|->ress|->role : PA\n" +
				"    THEN\n" +
				"        PA := PA - {action|->ress|->role}\n" +
				"        ||\n" +
				"  /*      Assigned_permissions := {role} <<| Assigned_permissions \\/ {role|->(Assigned_permissions(role) - {action|->ress})}\n" +
				"  */\n" +
				"  Assigned_permissions(role):=Assigned_permissions(role) -{action|->ress}\n" +
				"    END;\n" +
				"\n" +
				"\n" +
				"    /******************************\n" +
				"    * Supporting functions\n" +
				"    * Core RBAC\n" +
				"    *\n" +
				"    *\n" +
				"    ******************************/\n" +
				"\n" +
				"    /*\n" +
				"    * CreateSession(user,sess)\n" +
				"    *\n" +
				"    *\n" +
				"    */\n" +
				"    CreateSession(user,sess) =\n" +
				"    PRE\n" +
				"        user : USERS & user : Users & sess : SESSIONS & sess/: Sessions\n" +
				"    THEN\n" +
				"        Sessions := Sessions \\/ {sess}\n" +
				"        ||\n" +
				"        User_sessions := User_sessions \\/ {user|->sess}\n" +
				"        ||\n" +
				"        Session_roles := Session_roles \\/ {sess|->{}}\n" +
				"\n" +
				"    END;\n" +
				"\n" +
				"    /*\n" +
				"    * DeleteSession (sess,user) :\n" +
				"    * Supprime sess associée à l'utilisateur user de l'ensemble User_Sessions et de l'ensemble Session\n" +
				"    * Supprime également sess de la relation Session_roles\n" +
				"    */\n" +
				"    DeleteSession (sess,user) =\n" +
				"    PRE\n" +
				"        user:USERS & user:Users & sess:SESSIONS & (user|-> sess) : User_sessions\n" +
				"    THEN\n" +
				"        User_sessions := User_sessions - {user|-> sess}\n" +
				"        ||\n" +
				"        Sessions := Sessions - {sess}\n" +
				"        ||\n" +
				"        Session_roles := {sess} <<| Session_roles\n" +
				"    END;\n" +
				"\n" +
				"    /* AddActiveRole(user,sess,role)\n" +
				"    *\n" +
				"    *\n" +
				"    *\n" +
				"    */\n" +
				"\n" +
				"    AddActiveRole(userc,sessc,rolec) =\n" +
				"    PRE\n" +
				"        userc : USERS & userc : Users & sessc : SESSIONS &\n" +
				"        sessc : Sessions & rolec : ROLES & rolec : Roles &\n" +
				"        (userc|-> sessc) : User_sessions & rolec /: Session_roles(sessc) & userc|->rolec : UA\n" +
				"\n" +
				"    THEN\n" +
				"        Session_roles := {sessc} <<| Session_roles \\/ {sessc|->(Session_roles(sessc) \\/ {rolec})}\n" +
				"\n" +
				"    END;\n" +
				"\n" +
				"\n" +
				"    /*\n" +
				"    * DropActiveRole(user,sess,role)\n" +
				"    *\n" +
				"    *\n" +
				"    *\n" +
				"    */\n" +
				"\n" +
				"    DropActiveRole(user,sess,role) =\n" +
				"    PRE\n" +
				"        user : USERS & user : Users & sess: SESSIONS & sess : Sessions & role : ROLES & role : Roles & (user|-> sess) : User_sessions & {role} : Session_roles[{sess}]\n" +
				"    THEN\n" +
				"        Session_roles := {sess}<<| Session_roles \\/ {sess|->(Session_roles(sess)-{role})}\n" +
				"    END;\n" +
				"\n" +
				"\n" +
				"    access <-- CheckAccess (sess,action,ress) =\n" +
				"    PRE\n" +
				"         sess : SESSIONS & sess : Sessions & action : ACTIONS & action : Actions  & ress : RESSOURCES &  ress : Ressources\n" +
				"    THEN\n" +
				"     access := bool(#role.(role:Roles & role:Session_roles(sess) & (action|-> ress)|->role : PA ))\n" +
				"    END\n" +
				"\n" +
				"END\n";
		check(machine);
	}

	@Test
	public void testCompositionEmpty() throws Exception {
		String machine = "MACHINE CompositionEmpty\n" +
				"VARIABLES\n" +
				"  ff\n" +
				"INVARIANT\n" +
				"  ff : POW(INTEGER * INTEGER)\n" +
				"  & ({}; ff) = {}\n" +
				"\n" +
				"INITIALISATION\n" +
				"    BEGIN\n" +
				"      ff := {(1|->2)}\n" +
				"    END\n" +
				"OPERATIONS\n" +
				"  continue = skip\n" +
				"END\n";
		check(machine);
	}
	
	private BProject check(String main, String... others) throws Exception {
		return Antlr4BParser.createBProjectFromMachineStrings(main, others);
	}
}
