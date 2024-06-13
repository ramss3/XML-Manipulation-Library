# XML Manipulation Library

# Description
The XML Manipulation Library is a robust tool designed to simplify the process of reading, writing, and manipulating XML data in various applications. This library provides intuitive functions to parse XML files, modify their content, and serialize the data back to XML format.

# Installation
To install the XML Manipulation Library, clone the repository and install the necessary dependencies.
git clone https://github.com/ramss3/XML-Manipulation-Library.git
cd XML-Manipulation-Library

# Features and Usage
Here's how to use the XML Manipulation Library:

This project centers on two main objects: Entity and Text. The Entity object allows you to create XML fields. You can create a root entity with just a name or a child entity by providing a name and a parent entity. You can add, change, rename, or remove attributes, delete or rename entities, retrieve parents or children, and print entities in XML format.

The Text object represents text within an XML file. You provide the text and the associated entity.

The project also includes a Document object, representing the entire XML file. You can assign a root entity and use the toText() function to generate the complete XML file. The Document object allows you to add, rename, or remove attributes and entities, and search for elements using a specified path.

Additionally, were created annotations if the user wants to create a class instead of creating the fields using the objects mencioned earlier. With the annotations its possible to specify if a class is an Entity, and if its fields are Entities or Attributes. It's also possible to create a custom function to transform String's or a function to customize the created class, and assigning the @XmlString and @XmlAddapter annotations to the respective fields.

Internal DSLs are implemented, including lambda functions for creating Entity or Text objects, operators to retrieve objects by name, and infix notation to access files associated with a given entity.



Rafael Silva 98472, Tiago Felício 99213
