## Partial implementation of a Federative Database System (FDBS)
/play rimshot

Implementation of a Federative Database System based on a set of homogenous Centralized Database Systems (CDBS Oracle instances), and the implementation of a simple SQL parser. Everything was developed using Java.

### Requirements

Background information, description of the tasks in detail and the functionality of the FDBS layer to be implemented are specified in the document that is released together with this report [AssignmentGP4_V7.docx](doc/AssignmentGP4_V7.docx).

Essential subtasks of the FDBS, among others, are:
- Syntax analysis of SQL statements
- Log of the processed statements
- A Federative DB catalogue (management of the distribution schema)
- Query analysis and query distribution
- Result set management

### Design and Architecture
The architecture of the system is depicted in Figure 1 and briefly presents the principal interaction among a Java application invoking the Federative layer through its facade.

![FDBS architecture and principal components interactions](doc/fdbs.png)
