SOURCE DIMENSIONS

The idea of this tool is running complex and sophisticated queries to slice-and-dice source code along dataflow, control flow and structural dimensions using a special query language similar in power to SQL (for example, walk along dataflow or control flow, within a certain scope and possibly for certain conditions like certain var names/patterns or only vars of certain data type(s)). The basic idea was building abstract syntax tree (AST) from large codebases and then special AST walkers can execute user queries.

Very powerful LALR(k) arbitrary lookahead syntax parser builder JikesPG made it possible to create reliable syntax parsers for Java 5.0 and C# 2.0 fully tested on parsing a large number of open-source projects.

C#/Java AST nodes were mapped to common denonimators (for example, Java annotations and C# attributes are the same thing - metadata) and stored in PostgreSQL using Hibernate ORM for Java.  

Eclipse was chosen as a hosting IDE. Parsers were using C++ intro with Eclipse Java SDK. 

The project is not completed - it was stopped on stage where C# or Java code is fully parsed, stored in PostgreSQL and shown inside of Eclipse as a tree.
