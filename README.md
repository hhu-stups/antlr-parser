# ANTLR4 Parser for B
**DO NOT USE THIS CODE IN ANYTHING THAT IS IMPORTANT TO YOU.
RATHER, IN ANYTHING AT ALL.
DO NOT EVEN THINK ABOUT USING IT.
STUFF _WILL_ BREAK.
YOU _WILL_ CRY.
WE WILL _NOT CARE_.**

This repo contains a standalone version of the
_still in development_ ANTLR4 Parser feature of the
[ProB Parsers Library](https://github.com/hhu-stups/probparsers).

The code is still in a very early and experimental state.
Just do not use it unless you have contacted us
and we deemed usage of this library suitable for your goals.


## Using the Parser on the Command Line

You can use gradle to parse a FILE like this:
```
	./gradlew run -Pfile="FILE"
```
To disable type checking (which can still be slow and is less powerful than ProB's typechecking) you can do this:

```
	./gradlew run -Pfile="FILE" -Ptypecheck="false"
```

You can also build a stand-alone JAR like this:
```
	./gradlew shadowJar
```

You can then use it as follows:

```
	time java -jar build/libs/antlr-parser-0.1.0-SNAPSHOT-all.jar $(FILE) false
```

Note this is more or less equivalent to using [ProB](https://prob.hhu.de/)'s SableCC parser (available in ProB's lib folder):
```
	time java -jar probcliparser.jar FILE -prolog -time
```

## Limitations

Compared to ProB's parser there are still quite a few limitations:
- no support for DEFINITIONS
- no support for pragmas
- no support for locating files in the stdlib folder of ProB (LibraryStrings.def, ...)



## Licence

The ANTLR4 Parser for B and the ProB Parser Library source code
is distributed under the Eclipse Public License - v 1.0
(available at https://www.eclipse.org/legal/epl-v10.html)

The Parser Library comes with ABSOLUTELY NO WARRANTY OF ANY KIND !
This software is distributed in the hope that it will be useful but
WITHOUT ANY WARRANTY.
The author(s) do not accept responsibility to anyone for the consequences of
using it or for whether it serves any particular purpose or works at all.
No warranty is made about the software or its performance.

(c) 2011-2020 STUPS group, University of Düsseldorf
