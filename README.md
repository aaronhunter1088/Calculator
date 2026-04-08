# Calculator v4.6.3

## How to start the jar file
```
# Start a calculator:
java -jar Calculator-version-jar-with-dependencies.jar <calculator view>
Views: basic, programmer, scientific, date, converter

# Start the jar file with a specific date view:
# Date options are: 1 or 2
java -jar Calculator-version-jar-with-dependencies.jar date 1 
or
java -jar Calculator-version-jar-with-dependencies.jar date 2

# Start the jar file with a specific converter view:
# Converter type options are: area, angle
java -jar Calculator-version-jar-with-dependencies.jar converter <converter type>
```

## History of the project

v4.6.3: Update the main class to utilize program arguments. With these in place, no longer will I need
to comment in and uncomment out different sections of code. This will make it easier to quickly spin up different
views and can stop touching the main class.

v4.6: Merged in programmer redesign changes to ready this repo for assistance with
Copilot.

v4.5: Introduced the Parent Pom used to manage the versions of the dependencies.

This project is one of the few projects I have from college that I was able to keep.
It may look ancient but the goal of this project is to demonstrate my Java programming
skill and my UI skills, and my ability to come up with a somewhat professional design
and planning.

There will be several views to choose from.
The first 4 calculator views it will display are: Basic, Programmer, Scientific, and Date.
The next view, Converter, will itself implement various different units to convert between.

v4. By this version, Basic, and Date are working as expected, and Converter is in the works.
Programmer and Scientific are created but a lot of the logic is not there.

v3: Programmer, Scientific, and Date calculators are all types to be implemented in version3.

v2: Calculator is broken apart, and thoroughly constructed to create more types, eliminate code
duplication, and I began to create a way for the classes to be written in such a way that it is
cleaner, more readable, and will set a standard for future classes. However, version2 was to be
developed using JavaFX and this was a failure. For whatever it just wasn't coming as intuitively
as I feel the programmers meant. So I will stick with programming by hand everything (even the UI).

v1: Basic calculator is created and solidified.