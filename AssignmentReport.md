# Report for assignment 3 #

**Project Name:** Karate

**URL:** https://github.com/intuit/karate

## Project Description ##
Karate is the only open-source tool to combine API test-automation, mocks and performance-testing into a single, unified framework. The BDD syntax popularized by Cucumber is language-neutral, and easy for even non-programmers. Besides powerful JSON & XML assertions, you can run tests in parallel for speed - which is critical for HTTP API testing. *(Copied from original repository)*

## Onboarding experience ##

**Did you have to install a lot of additional tools to build the software? Were those tools well documented?**
    We had to install maven and openjfx. The project also requires Java 8. The documentation didn’t mention that we needed openjfx and we had to inspect the Maven output to figure it out.
    
**Were other components installed automatically by the build script?**
    Yes. It uses Maven and it installed the dependencies apart from openjfx when you typed `mvn install`.
    
**Did the build conclude automatically without errors?**
    Yes it did.
    
**How well do examples and tests run on your system(s)?**
    Except for MacOS, yes, once we installed the dependencies. We got one failing test on MacOS (on a test that parses big decimals).
    
**Do you plan to continue or choose another project?**
    Initially we chose TEAMMATES but despite our efforts we couldn’t build it. After some time we decided to switch to Karate.
    
## Complexity ##

**1. What are your results for the ten most complex functions? (If ranking
is not easily possible: ten complex functions)?**
1. Script::matchNestedObject@1235-1464@./src/main/java/com/intuit/karate/Script.java (NLOC: 223 CNN: 74) : Andreas
2. Script::matchStringOrPattern@745-975@./src/main/java/com/intuit/karate/Script.java (NLOC: 224 CNN: 64) : Kartal
3. Script::matchJsonOrObject@1042-1172@./src/main/java/com/intuit/karate/Script.java (NLOC: 129 CNN: 45): Philippa
4. Config::configure@90-207@./src/main/java/com/intuit/karate/Config.java (NLOC: 117 CNN: 32) : Gustaf
5. HttpClient<T>::buildRequestInternal@123-215@./src/main/java/com/intuit/karate/http/HttpClient.java (91, 31) : Marcus
6. DragResizer::mouseDragged@212-332@./src/main/java/com/intuit/karate/ui/DragResizer.java (110, 31) : Gustaf
7. FeatureBackend::buildResponse@165-261@./src/main/java/com/intuit/karate/core/FeatureBackend.java (92, 28) : Kartal
8. Script::evalXmlEmbeddedExpressions@489-568@./src/main/java/com/intuit/karate/Script.java (80, 27) : Philippa
9. Script::evalKarateExpression@251-339@./src/main/java/com/intuit/karate/Script.java (83, 23) : Andreas
10. Engine::stepHtml@307-386@./src/main/java/com/intuit/karate/core/Engine.java (80, 19) : Marcus
   
**Did all tools/methods get the same result?**
         Almost. We measured the Cyclomatic Complexity using both Lizard and JaCoCo and the results were pretty close. The manual counting below gave slightly different results compared to the tools.
         We all ran Lizard on the karate/core and got the same results. 

**Are the results clear?**
         Yes.
         
**Manual count of CC for five complex functions**

buildResponse:
if: 17
ternary?: 3
&&: 4
||: 1
case: 0
while: 0
for: 0
return: 2
throw: 0
CCN: (17 + 3 + 4 + 1) - (2) + 2 = 25

evalXmlEmbeddedExpressions:
if: 11
ternary?: 3
&&: 2
||: 3
case: 0
while: 0
for: 5
return: 1 (1 is implicit)
throw: 0
CCN: (11 + 3 + 2 + 3 + 5) - (1) + 2 = 25

mouseDragged:
if: 17
ternary?: 4
&&: 0
||: 0
case: 9
while: 0
for: 0
return: 2 (1 is implicit)
throw: 0
CCN: (17 + 4 + 9) - (2) + 2 = 30

matchNestedObject:
if: 56
ternary?: 1
&&: 2
||: 8
case: 0 
while: 0
for: 5
return: 45
throw: 1
CCN: (56 + 1 + 2 + 8 + 5) - (45 + 1) + 2 = 28

buildRequestInternal:
if: 16
ternary?: 0
&&: 2
||: 4
case: 0
while: 0
for: 8
return: 5
throw: 3
CCN: (16 + 2 + 4 + 8) - (5 + 3) + 2 = 24

**2. Are the functions just complex, or also long?**
The two most complex functions are over 200 lines of code which can be considered a lot for just one function.

**Below follows a description of the complexity of the ten most complex functions (measured by Lizard)**

1. matchNestedObject:
The function is 223 lines of code and conducts several comparisons and splits into different branches within the function depending on the parameter types. Since we can have many different Object Types, and matchTypes, the complexity rises and naturally the lines of code increases here as there are case handling for every type of object. Depending on object type, the function will split up to 8 different major branches. The different objects that are compared can be null, instanceof String, Map (Where size is compared and examined if certain elements contain or not, if keys are the same or not etc.), Lists and BigDecimals. Within these major branches, depending on the matchType, we will step into smaller sub branches. The depth of these branches are at most 5, but generally they don’t go further than a depth of 3.

**How we would refactor this:** Refactoring is fairly easy in this case as one could just create childfunctions for the different object type that split into one bigger branch. Meaning separating it into a helpfunction for String, Map, List or BigDecimal instead of keeping them all in the same function. The content within these major branches would be unessecary to break down further as they aren't that complex.

2. matchStringOrPattern:
The function is 224 lines long and has a cyclomatic complexity of 64. The reason the function is that long and complicated is because of the large amount of responsibility loaded to it. It is responsible for matching macros, paranthesis expressions, js expressions, nested objects, regexes and more. Each one of these cases requires complex logic to be handled properly, which leaves the function long, complex and deeply nested. See below for details on the purpose of the function and suggestions on possible refactoring.

**How we would refactor this:** Although the logic required to handle all this is bound to be long, the complexity can be dissolved by refactoring this function into smaller functions, each of which is responsible from a smaller subset of possible match types. One function may be responsible for macro queries, another for regexp queries, another for string queries (like CONTAINS, NOT_CONTAINS, EQUALS and so on). This function (matchStringOrPattern) can just call these “sub-functions” and return their result.

3. matchJsonOrObject
Tools indicate the matchJsonOrObject function to be complex with a cyclomatic complexity measured to 45 by Lizard and 30 by JaCoCo.  This can be explained by the fact that the code is basically just a large switch statement. There are 25 different switch cases, although there are a lot of fall through cases . The report by McCabe on page 26-27 implies that we should count the fall through cases as one decision. (http://www.mccabe.com/pdf/mccabe-nist235r.pdf). The method will try to match different input against each other. This requires case handling for every type of possible values and this increases complexity. TODO: Add the total complexity (including the ifs).

**How we would refactor this:**
When inspecting the code, we see that the high complexity is because of the big switch statements. I don't necessarily think this is bad, since if we check the code it makes sense to have it this way. Although the function basically consists of three big switch statements, switch (actual.getType()), switch (expected.getType()) and switch (matchType) and these could possibly be split up into different functions for increased readablity. It is a long function since we could try to match alot of different results with the expected data. This creates alot of different combinations that need to be checked. (The matches is designed for performing assertions against JSON and XML response payloads if I understand things correctly.) 


4. configure
The function is 117 lines long. The cyclomatic complexity of this function is 32. This length is not so bad considering it is basically a switch statement. There are 23 cases in the switch statement including the default. The reason the CCN is higher than this is because some of the cases contain if statements. 
**How we would refactor this:**
If refactoring I would consider moving some of the bodies of the larger cases into their own functions in order to increase readability.

5. HttpClient<T>::buildRequestInternal
The method is 91 lines long which is reasonable because you need to check whether the given request contains certain field and if it does you need to handle this. They to handle each type of field with a method specified with the field but possible refactoring could be breaking out the first part of the function that simple check if the given request is legit. Calculating by hand gives the cyclomatic complexity: 30.

**How we would refactor this:**
Right now, the function is checking if there is a url then build the url, of there is exists paths then build the paths, and does this for all different kinds of fields in a Http request and for each case it also throws an exception if a field does not exist. Refactoring could therefore be done by dividing all this field into their own helper functions and let these function make these checks and throw if they fail. 

```
T buildRequestInternal(request, context) throws RuntimeException{
    buildUrl(T, ,request, context);
    buildPaths(T, request, context);
    buildParams(T, request, context);
    ...
}
```
Each function would alter the state of T.

6. mouseDragged
The function is 110 lines and has a cyclomatic complexity of 31. I would say that this function is quite reasonable despite its length. The function begins with a long switch statement followed by a number of nested if statements. The maximum depth of nesting is 3, which sounds pretty bad, but I would say that it is clearly structured and easy to follow. If I were to refactor something it would be to move the 30 line if statement “if (constrainToParent)” to a separate function.

7. buildResponse
The function is 96 lines and has a cyclomatic complexity of 28. What the function achieves is complex (for a detailed explanation see below), but I think it could be better structured (again, see below for details). The function is responsible from many different aspects of request handling. The main stages of the process is to convert an HTTP request into ScriptValueMap, to process this map using `result`, and convert the result back into an HTTP response. One way to refactor this function would be to seperate out the tasks of HTTP request/responses to/from ScriptValueMap into a seperate function. Then, buildResponse can simply call these and the logic will be much simpler, which'd also decrease the cyclomatic complexity.


8. evalXmlEmbeddedExpressions
JaCoco says the function has a CC of 25 and Lizard evaluated it to  27. The main reason for this function to be complex is the extensive use of ifs and for loops. Although I think some of the if:s need to stay since they are not really suitable as switch statements. Maybe one can split this function but the amount of code is not that much. 

9. evalKarateExpression
The function is 83 lines of code. It’s actually fairly small for its complexity. The callback depth increases this way, but the function becomes more readable. It performs a good amount of comparisons (ifs), but it is actually pretty well structured as it calls other functions as a return value depending on the state. However there are a few general parts that could be reduced to their own function. For example perhaps the if (callOnce || isCallSyntax(text)) on row 261 and the else if(isGetSyntax(text) || isDollarPrefixed(text)) line on row 282

10. Engine::stepHtml
The method is 80 lines of code which does not seem to be too long for the function. However, there is potential for refactoring. One part of the function constructs a html table, this could be move in to a smaller function of its own.

**3. What is the purpose of the functions?**

1. matchNestedObject
the core principles are to compare two objects, the expectedObject with an actualObject and it returns an  AssertionResult depending on the results. If the matchType succeeds it returns a PASS otherwise it gets a return from matchFailed() function in the type of FAIL.
The assertions depends on a matchType enumerator one wishes to conduct (EQUALS, NOT_EQUALS, CONTAINS_ONLY, CONTAINS_ANY for example).

2. Script::matchStringOrPattern
	Given a macro or a string to be matched (the `expected` parameter) against a value (the `actValue`) parameter, returns whether the match was successful or not. For successful matches, returns AssertionResult.PASS, for failed matches, calls matchFailed and returns its return value (which is an AssertionResult indicating that the match failed, it also includes information about the current path the actual value the match was tested against, the pattern (`expected` parameter), and the reason the match failed).

3. matchJsonOrObject
The method matchJsonOrObject returns an AssertionResult which is used to say if things matched or not. The important bits of the function are ScriptValue actual and String expression. These will be compared (or matched if you will) against each other. The String expression will be evaluated (parsed) by evalKarateExpression to determine it’s ScriptValue type (JSON, XML, String etc…). The function compares actual values to expected values and determines it’s output based on the matches. The flow we need to go through to determine a match (ScriptValue can be of different types, and the MatchType values differ etc) creates a lot of different cases so the function becomes quite complex due to this. 

4. configure
This function appears to be used for setting different configurations. The parameters are String “key” and ScriptValue “value”. The entire function is one big switch statement where the key is used to determine which case to select, and the value is then written to the corresponding config setting.

5. HttpClient<T>::buildRequestInternal
Given a http request and a context then create a Object to represent the given request. Branches depending on the kind of http method, the url, requests paths, request params, headers, cookies.

6. mouseDragged
The purpose of this function is to take a mouse drag mouseEvent and resize an active region based on the delta x, y of the mouse. First these deltas are calculated. After this there is a big switch statement used to determine which part of the region we are dragging. Is it the top, top right corner, left, etc. Inside each case the region coordinates and size are updated accordingly. After this there are some checks to see if the resize is allowed and doesn’t make the region go off the screen or outside of the parent region.

7. buildResponse
Called by FeatureServerHandler when a feature request arrives.
If cors is enabled and the request is of HTTP method "options", returns a simple response identifying which HTTP method calls are allowed, and which of them are allowed from a cross-origin context.
Creates a ScriptValueMap using the headers of the request. If the request has a body that is valid JSON/XML parses it. Then, passes this map to the function `handle` which cross-checks it against the list of scenarios to see if it matches any of them. And if it does, handles it accordingly. buildResponse then turns the return value of `handle` (which is another ScriptValueMap) into an HTTP response and returns it.
The function is responsible from many different aspects of request handling. The main stages of the process is to convert an HTTP request into ScriptValueMap, to process this map using `result`, and convert the result back into an HTTP response. See below for suggestions on possible refactoring.

8. evalXmlEmbeddedExpressions
The function evalXmlEmbeddedExpressions does what it sounds like, it tries to evaluate embedded xml-attribute. To understand the function one should know that one of the inputs uses a Node Interface. The Node interface is the primary datatype for the entire Document Object Model. It represents a single node in the document tree. This node can have different values based on its type, for instance ELEMENT_NODE or ATTRIBUTE_NODE etc. The node can have children which makes the function call itself recursively.  

9. evalKarateExpression
The function evaluates what kind of context the expression is given in and returns an object describing the information about the expression. It begins by trimming the input text and check that it's a valid string and not empty or null. Then it checks depending on the parameter ScenarioContext, which contains information such as tags, calldepth, executionHook, config, loggers etc. if the text and context already forms a variable or not. It then handles different cases of text: the first being it starts with "callonce ". It then either returns by executing the expression with another thread that's using the cache or regularly in sequential order. Other expression handling is if it's a JSONexpression or XML. Alternatively it starts with a $ sign that follows a specified protocol. There are 12 parent branches and the branch depth is at most 2 in these.  

10. Engine::stepHtml
Constructing a html representation of a step (a test) result. The method branches depending on if a test has passed, failed or been skipped. Also, a couple of for loop to build a table depending on the size of test.


**Are exceptions taken into account in the given measurements?**

For Lizard we did not find if this was the case. For JaCoCo there seems to be a problem with exceptions not being covered. We found this FAQ of questions that have been asked about JaCoCo.There is a answer to the question "Source code lines with exceptions show no coverage. Why?" https://www.eclemma.org/jacoco/trunk/doc/faq.html

**Is the documentation clear w.r.t. all the possible outcomes?**
None of the functions had any provided documentation or comments. There is also no documentation of the architecture, for example class diagrams etc. It is hard to understand the structure of the project. It would have been much easier for us if something clearly showed the system's classes, their attributes, methods, and the relationships among objects. This was a big limitation in this project and a lot of hours were spent just figuring out these kind of things. 

## Coverage ##

### Tools


#### JaCoCo
https://www.jacoco.org/jacoco/ for code and complexity coverage.

**How well was the tool documented? Was possible/easy/difficult to
integrate it with your build environment?** 

JaCoCo was fairly simple to setup but their official documentation was not enough so we had to search for other guides, the reason for this might be that this was also our first occurrence with Maven. If we would have prior knowledge of Maven, this might not have been a problem. In the it was simply to add the JaCoCo and the Surefire plugin to the Maven file `pom.xml`.

#### DYI

Show a patch that show the instrumented code in main (or the unit
test setup), and the ten methods where branch coverage is measured.

The patch is probably too long to be copied here, so please add
the git command that is used to obtain the patch instead:

**Original main code**
``git checkout master``
``cd karate-core``
``mvn test``
A coverage report is generated after the tests have terminated and clicking on the individual function of interest we can see the state of the code. The report is found in:
karate/>: `` cd karate-core/target/site/jacoco-ut/index.html`` 

What kinds of constructs does your tool support, and how accurate is
its output?

### Evaluation

Report of old coverage: https://drive.google.com/open?id=1QC4cWvRNjPjl410S-WqwiR_0XHGen4Py

Report of new coverage: https://drive.google.com/open?id=1GTiBDzw-B0QfBOlr0ZLsCli_ZWWiOt1d

**Test cases added:**

Switch to branch: **addTests** and run ``git diff --name-status master`` and you'll see the files where tests were added. 

To see exact changes in terminal just run ``git diff master..addTests`` and scroll through the terminal. 

## Branches
1. master - The default open source branch
2. adHocCoverage - A branch containing our own adhoc coverage tool without our own tests
3. addTests - A Branch containing our own tests without the coverage tool
4. addCoverage - A Branch containing both our coverage tool and our added tests.
5. mdReport - A Branch that will contain this report in the top level of the repo. 

## Effort spent / Contributions

Andreas Gylling: 
Along with the rest of the group, sat and looked into different possible open source projects that build without needing any rare third part software to get working (e.g. Android SDK). Tests and adhoc tool written for the Convert() function in TestCommand and getTypeAsShortString() in ScriptValue to increase coverage. Described the function matchNestedObject and evalkarateexpression. Converted and improved the report to markdown format. See below for detailed documentation of time spend.

Gustaf:
Reviewed lots of pull requests. Wrote adhoc manual instrumentation coverage measurements for 2 functions. Wrote tests increasing coverage for configure() in Config.java and MatchStep() in MatchStep.java. Manually counted complexity of 5 functions. Wrote explanations of two complex functions.

Philippa Örnell:
Wrote tests and adhoc instrumentation to GetAsString() in ScriptValue and matchJsonOrObject() in Script. Explained the complex functions matchJsonOrObject() and evalXmlEmbeddedExpressions(). Also structured and wrote parts of this report. Spent alot of time with the rest of the group trying to find a good project.  

Kartal:
Figured out the purpose of, wrote new tests for and suggested possible refactoring for matchStringOrPattern() in Script and buildResponse() in FeatureBackend.
Seperated out the additional tests and adhoc coverage tool into different branches.
Uploaded zip's of old and new coverage

Marcus:
Wrote the purpose of stepHtml() in Engine and buildRequestInternal in HttpClient. Created the adhoc coverage tool class and added it to Maven, and added JaCoCo to the project. Wrote tests and added the adhoc coverage tool to the functions copy() in HttpRequestBuilder and stepHtml() in Engine.

For each team member, how much time was spent in

1. plenary discussions/meetings;
20h/person choosing project and having meetings/discussions, this became a huge problem due to trying to take on a huge project which we in the end did not even manage to build.  

2. discussions within parts of the group;
4h, We work as one strong unit, discussing problems as they unravel.

3. reading documentation;
JaCoCo: 0.5h

4. configuration;
JaCoCo: 1h
Installing dependencies and getting the project to build: ~2h/person. Some had problems 

5. analyzing code/output;
Analyzing complex methods ~2-3h/person (probably more since there was no comments for the functions and this made the whole analyzing part quite difficult for some functions)

6. writing documentation;
Writing this document: 
2019-02-18, spent 3 hours constructing and writing this report. 

7. writing test code;
5-8h/person

8. running code;
1.5h probably ran the tests over a hundred times and almost a minute per run.

## Overall experience

We also learned the importance of documenting your code. Even if things seem clear to you, they are not necessarily clear for others. 

Is there something special you want to mention here?
This was the first time many of us has used a coverage tool. At first we added the tests and the adhoc overage tool to the same branch and seperating these out later became a challenge of understanding Git for us. The maintainer of the original repository asked for a copy of this report.


-----------------------------------
Lizard output:
=======================================
!!!! Warnings (cyclomatic_complexity > 15 or length > 1000 or parameter_count > 100) !!!!
================================================
  NLOC    CCN   token  PARAM  length  location  
------------------------------------------------
     223     74   1594      8     230 Script::matchNestedObject@1235-1464@./src/main/java/com/intuit/karate/Script.java
     224     64   1586      8     231 Script::matchStringOrPattern@745-975@./src/main/java/com/intuit/karate/Script.java
     129     45    902      5     131 Script::matchJsonOrObject@1042-1172@./src/main/java/com/intuit/karate/Script.java
     117     32    711      2     118 Config::configure@90-207@./src/main/java/com/intuit/karate/Config.java
      91     31    687      2      93 HttpClient<T>::buildRequestInternal@123-215@./src/main/java/com/intuit/karate/http/HttpClient.java
     110     31    608      1     121 DragResizer::mouseDragged@212-332@./src/main/java/com/intuit/karate/ui/DragResizer.java
      92     28    776      2      97 FeatureBackend::buildResponse@165-261@./src/main/java/com/intuit/karate/core/FeatureBackend.java
      80     27    645      3      80 Script::evalXmlEmbeddedExpressions@489-568@./src/main/java/com/intuit/karate/Script.java
      83     23    597      3      89 Script::evalKarateExpression@251-339@./src/main/java/com/intuit/karate/Script.java
      80     19    667      4      80 Engine::stepHtml@307-386@./src/main/java/com/intuit/karate/core/Engine.java
      46     18    404      4      49 Script::matchPrimitive@1488-1536@./src/main/java/com/intuit/karate/Script.java
      53     18    313      4      54 Script::call@1619-1672@./src/main/java/com/intuit/karate/Script.java
      63     17    473      1      63 MatchStep::MatchStep@41-103@./src/main/java/com/intuit/karate/core/MatchStep.java
      63     17    401      4      64 JsonUtils::setValueByPath@247-310@./src/main/java/com/intuit/karate/JsonUtils.java
      27     16    296      1      28 DragResizer::isInDraggableZone@183-210@./src/main/java/com/intuit/karate/ui/DragResizer.java
      68     16    457      0      79 FeatureExecutionUnit::run@73-151@./src/main/java/com/intuit/karate/core/FeatureExecutionUnit.java
      58     16    452      6      58 Script::setValueByPath@1560-1617@./src/main/java/com/intuit/karate/Script.java
      53     16    197      0      53 KarateParser::step@868-920@./target/generated-sources/antlr4/com/intuit/karate/core/KarateParser.java

Total nloc   Avg.NLOC  AvgCCN  Avg.token   FunCnt  Warningcnt   FunRt   nlocRt
------------------------------------------------------------------------------------------
     20296       7.7     1.9       57.0     2142           18      0.01    0.10

