==========json-2-xml-converter===============
1 Introduction:
Simple Java program which takes json file as input and converting the XML
file with JSON arbitrary information.

2 Design Approach
    Approach - 1 => Implemented Currently
    a) org.json & Jackson Core for getting json file and parsing into values.
    b) STAX Cursor used for writing XML element based on the JSON data type and value information.
       It used XMLStreamWriter API to write the XML contents.
    Approach - 2
    a) Using Jackson or GSON to navigate all elements via JsonParser.
    b) Use DOM parser or SAX parser to write the XML.

3 Build & Run

    a) If you are using the IDE, you can pull this project from github and import as maven project, and
       then by passing two input file names via commandline argument we can able to execute this program.
    b) Create JAR file and run it via Command Prompt


4 Github Repository information




