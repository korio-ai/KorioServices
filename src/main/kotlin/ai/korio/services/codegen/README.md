# Code Generation
Code is generated into Seed Projects for both the front and back ends.  

In most cases, at least one Code Generation Plan is associated with each element in the model.  Usually, defaults are set at the highest levels in the model and cascade through child elements.

## Code Categories & Framework Mapping

Code Generation Plans are articulated as CodeGenPlanConfig objects.  Again, any one mapping element in CMMN, BPMN and DMN may have one or more CodeGenPlanConfigs associated with it. Code generation happens through a combination of Freemarker templates that are simply fed by a POJO derived from the model and is then pushed to a repository which likely triggers a build.

Since , any language or framework code can be generated.  

### Types of CodeGenPlanConfig
   
#### Platform-Level Code Generation Configurators
- Infrastructure As Code: Provisions services, etc.
- Frontend & Backend Seed Project Selection: Sets the seed projects that generated code will be pushed/pulled into. NOTE: Seeds themselves may not work with certain Code Generation Plans.  Seed configurations must include a list of supported Code Generation Plans.
   
#### Frontend Code Generation Configurators
- Styles: [Case, Process, Task] for specifying CSS
- Barrel Files: [Platform, Case, Process] holds all include/import references. See: https://medium.com/@adrianfaciu/barrel-files-to-use-or-not-to-use-75521cd18e65
- UserTask Data Capture Form: [Case, Process, Task, Submission/Channel/Field] form field layout + submit code (via state manager), (one per channel??), INCLUDES contextual data field publish
- Field-level Validation: [Field] NOTE: probably should NOT use CodeGenPlanConfig!!
- Field and Form-level Analytics: [Platform, Case, Process, Task, Submission/Channel/Field]
- Case State Management: [Case, Process] manage variable scope for overall Case/Service
- Process State Management: [Process] manage variable scope for Process
- Router: [Case, Process] showing the correct component for data capture, publish, etc.
- Object & Case View: [Platform, Case]: Working with Materialized Views in Data Service, creates object and case lists
- DataPublish: [Case, Process, Task] layout for rich data and content publishing
- Tests: ...including test of invariants (can models help with this??)

#### Backend Code Generation Configurators
- API: [Case, Process, Task, Submission/Channel/Field] generates an API, e.g. GraphQL, REST, gRPC, Ws, SSE.  Assume  GraphQL for field-level api
- Generic Command Handler: [Case, Process, User Task, Submission/Channel/Field] A handler that generically passes data capture payload as a single Domain Event
- Custom Command Handler (side-effect): [System Task] creates one or more Domain Events for a SINGLE SIDE-EFFECT, code shell that allows data to be transformed or specially handled outside the edges of the Data Service. NOTE: that this should be largely MODELED and ensure the creation of a PURE FUNCTION with a single side-effect or a properly "contained" side-effect as per functional language practices.
- Custom Process Event Listener Handler: [Throw and Catch Events] ???
- Query: [System Task] Queries a materialized view, mutation-free (does this throw an event??... is it just a Custom Command Handler??)
- Tests: ...including modeling tests for invariants (for Handlers and front-end validation)...consider Given, When, Then (via Spock). Consider using/enforcing use of Camunda Throw Events and variable API... avoid excessive branching in model.

##### Data Service Code Generation Configurators for Backend
- Persistence Strategy: [Platform, Case, Data Service] sets up DB with config, etc.
- Domain Data Service Instance: [Case] ideally one Data Service Instance per Case
- Governed Schema: [Platform, Case, Data Service] schema format to use, defaults to Avro
- Channel Submissions & Data Capture Coverage: [Case, Data Service, Submission/Channel/Field]holds inbound data until submission is ready, given multi-channel data capture and "self-healing" AI, seeks or reports on required/desired data.  Handles mis-ordered or duplicate submissions.
- Reactive Stream Publish/Subscribe: [Platform, Case, Data Service, Submission/Channel/Field]defines reactive framework (RxJava, Reactor, Akka), and pub/sub with Camunda, Domain Events, Data Flow Event Listeners
- Domain Event Persistence: [Platform, Case, Data Service]stores domain events
- Domain Event & Command Replay: [Platform, Case, Data Service] While Event replay is default to avoid side-effects, Command replay is an option, modeled on an element by element basis to allow side-effects, IF desired.
- Materialized View: [Platform, Case, Data Service] Query-side of CQRS/ES

##### AI and Machine Learning Code Generation Configurators for Backend
- AI/Machine Learning Processor: Listens on data stream
- AI/Machine Learning Model Deployer: Deploys and redeploys models to CMMN sentries, BPMN gateways, etc..
       
##### ServiceTask 3rd Party REST calls Code Generation Configurators:
- Communications and Messaging: [Platform, Case, Process, Service Task] Integrations with Twilio, Plivo, etc.
- Financials: [Platform, Case, Process, Service Task]
- Marketing Automation: [Platform, Case, Process, Service Task] Integrations with Marketo, HubSpot, etc.
 