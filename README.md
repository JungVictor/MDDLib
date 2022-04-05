# MDDLib
MDDLib is distributed under MIT License. Copyright (c) 2021, Victor JUNG, Jean-Charles RÉGIN & Steve MALALEL.

## What is MDDLib
MDDLib is a library to create and manipulate Multi-valud Decision Diagrams, written in **Java 11**.  
MDDLib is designed to be able to run without a garbage collector : `-XX:+UnlockExperimentalVMOptions -XX:+UseEpsilonGC` in JVM Options. You can also try `-XX:+UseSerialGC`.  
This library is a work in progress. **As such, we do not guarantee any forward compatibility.**

## Documentation, Support and Issues
The documentation / wiki / user guide is available at https://jungvictor.github.io/.  
The Javadoc is available at https://jungvictor.github.io/javadoc/.

## Tools

### MDDViewer
This allows you to visualise a .dot file on your browser, without having to install anything. This was made using the [viz.js](https://github.com/mdaines/viz.js) library under MIT License.  
MDDLib is able to generate MDDs under .dot format. More information here : [MDD2Dot](https://jungvictor.github.io/#/mdd2dot).


## Roadmap
- [x] **[90%]** Adding support for pure Binary Decision Diagrams (BDDs).  
  - [x] BDD class and components fully implemented
  - [x] Operations between BDD implemented
  - [ ] Loading / saving in testing phase
- [ ] [20%] Adding a proxy MDD type to perform operation without loading the whole MDD.  
  - [x] Rewrote the base engine for saving / loading DDs
  - [ ] Proxy class and components to implement
  - [ ] Operations between DD and proxy to implement 
- [ ] [0%] Adding support for non-deterministic MDDs.
  - [ ] ND-MDD class and component to implement
  - [ ] Operations between ND-MDD to implement
  - [ ] Operations between ND-MDD and D-MDD to implement
