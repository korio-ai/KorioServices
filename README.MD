# Camunda Microservice

This application wraps the CMMN and BPMN aspects of the Camunda Engine.  I am creating my own REST calls, even though 
Camunda already has a REST interface.  I think this is the best approach given the aspiration to mix Camunda with many 
non-Camunda services.

This project leverages Camunda Spring Boot Starter and, so, is a Spring Boot application.  In SCSt terms, "CaseML" (this application)
would be what is called a "Processor" ie. both a "Source" and a "Sink". I think there 
would be one application like this per CMMN case.  Camunda is only a 3MB Jar file, so this approach should be fine.

## Setup

Setup should be 100% via the POM file - and it should just work.

This application must be running for the Angular front end to fetch any Camunda stuff.

## Status

It works.  I plan to spawn a steve_dev branch to keep going on this applcation.  

  