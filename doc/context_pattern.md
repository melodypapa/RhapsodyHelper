# Context Pattern

A context pattern is a way to describe a path of elements in the model. A context pattern is a comma-separated list of tokens. Each token in the pattern is a Rational® Rhapsody® metatype name that is either a core type or a term.
The following table provides the context pattern BNF definitions. See the section after the table for notes and examples

## IBM Rhapsody Help

* [Context patterns](https://www.ibm.com/support/knowledgecenter/SSB2MU_8.2.1/com.ibm.rhp.matrix.doc/topics/r_context_patterns.html)
* [Using context pattern in tables](https://www.ibm.com/support/knowledgecenter/en/SSB2MU_8.2.1/com.ibm.rhp.matrix.doc/topics/rhp_c_context_patterns_in_tables.html)

## Video

* [IBM Rhapsody Tables: Context Patterns in Table Views](https://www.youtube.com/watch?v=iTHTxF5vOMc&t=0s&index=7&list=PLZGO0qYNSD4VrcVNWT5ltkBI8vbkMDY0Y)
* [IBM Rhapsody Tables: Combining Context Patterns and Java](https://www.youtube.com/watch?v=_FgEcqytjt4&index=13&list=PLZGO0qYNSD4VrcVNWT5ltkBI8vbkMDY0Y)

## Content Patterns in Table Layout for AUTOSAR AR-Model

### Table Layout

#### List the serverPorts of SwComponentType

```
{Pkg} ARPackage+, {Swc} ApplicationSwComponentType+, {port} serverPort, {interface} portType:
```

#### List the clientPorts of SwComponentType

```
{Pkg} ARPackage+, {Swc} ApplicationSwComponentType+, {port} clientPort, {interface} portType:
```

#### List dataSenderPorts of SwComponentType

```
{Pkg} ARPackage+, {Swc} ApplicationSwComponentType+, {port} dataSenderPort, {interface} portType:
```

#### List dataReceiverPorts of SwComponentType

```
{Pkg} ARPackage+, {Swc} ApplicationSwComponentType+, {port} dataReceiverPort, {interface} portType:
```