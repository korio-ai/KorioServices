# Korio Services

This application wraps the CMMN and BPMN aspects of the Camunda Engine.  I am creating my own REST calls, even though 
Camunda already has a REST interface.  I think this is the best approach given the aspiration to mix Camunda with many 
non-Camunda services.

## Model-Driven with an Escape Hatch
As a principle, all model-driven aspects of the platform should be "detachable" or "ejectable" from the modeling approach.  In this scenario, the ejected service is is still part of the underlying horizontal framework, but it is detached from the code-genenration capabilities of the modeler.  Even when "deep" code generation is suppressed, the Korio IDE can still offer code snippets for use in these code files.

## High Level Architecture

This project leverages Camunda Spring Boot Starter and, so, is a Spring Boot application.  In SCSt terms, "CaseML" (this application)
would be what is called a "Processor" ie. both a "Source" and a "Sink". I think there 
would be one application like this per CMMN case.  Camunda is only a 3MB Jar file, so this approach should be fine.

## Setup

Setup should be 100% via the POM file - and it should just work.

This application must be running for the Angular front end to fetch any Camunda stuff.

## Status

It works.  I plan to spawn a steve_dev branch to keep going on this applcation.  

  