# Probability distribution

## Requirements
We need capture probability distributions across our forms.

We will be capturing the distribution in a matrix of numeric values
The capture requirements vary in multiple ways depending on which screen and property we're on (such as trap properties - depth to crest) or at which stage we're at (such as pre-drill)

The matrix can capture the following values:
- Min
- P99
- P90
- Mode
- P50
- Mean
- Constant
- P10
- P1
- Max

There are 6 preset types of these matrices:
- Normal
- Lognormal
- Beta
- Triangular
- Rectangular
- Constant

The user will be able to select which preset type they would like input the distribution values as.  
Depending on which form property and stage the user is dealing with, certain preset types will be available or not.

The first four preset type capture all values in the matrix except constant.   
Rectangular captures all values except min, mean, max and constant.  
Constant captures only constant.  

Each preset type will have different requirements in terms of whether a specific distribution value (such as P50) is required or not.  

|     | Min | P99 | P90 | Mode | P50 | Mean | Constant | P10 | P1  | Max |
|-----|-----|-----|-----|------|-----|------|----------|-----|-----|-----|
|Normal| o   | o   | m   | o    | m   | o    | n/a      | m   | o   | o   |
|Lognormal| o   | o   | m   | o    | m   | o    | n/a      | m   | o   | o   |
|Beta| m   | o   | o   | m    | o   | o    | n/a      | o   | o   | m   |
|Triangular| m   | o   | o   | m    | o   | o    | n/a      | o   | o   | m   |
|Rectangular| m   | n/a | n/a | n/a  | n/a | o    | n/a      | n/a | n/a | m   |
|Constant| n/a | n/a | n/a | n/a  | n/a | n/a  | m        | n/a | n/a | n/a |

We also need to capture distributions in a custom way.  
In these use cases (such as trap properties - depth to crest at FDP stage, or Oil in place) the values we capture and whether they are required or not will be not be preset.  

## Implementation

### Presentation
The template, form and form binding will be handled in a separate approach document.

### Business logic
We will create an enum of distribution types, the 6 presets and a custom type
- Normal
- Lognormal
- Beta
- Triangular
- Rectangular
- Constant
- Custom

The enum will contain a clazz (the class would implement an interface describing the behaviour).  
The behaviour interface would have a way to know whether each distribution value is available and required.  
The interface would look like:

- isMinAvailable
- isMinRequired
- isP99Available
- isP99Required
- isP90Available
- isP90Required
- isModeAvailable
- isModeRequired
- isP50Available
- isP50Required
- isMeanAvailable
- isMeanRequired
- isConstantAvailable
- isConstantRequired
- isP10Available
- isP10Required
- isP1Available
- isP1Required
- isMaxAvailable
- isMaxRequired

There will therefore be 6 behaviour classes, one for each preset.

We will create an enum of probability distribution instances. This will contain:
- Stage enum (such as LICENCE_APPLICATION)
- From and to dates for versioning
- Either
  - a list of presets available (such as Triangular, Rectangular, Constant)
  - a clazz (the class would implement an interface describing the custom behaviour)

The enum will have two constructors, one for a preset list and another for custom behaviour.

There will be a class for each custom instance. We can create a builder class to build these classes.

### Validation
As for each form property we will know which probability distribution instance its associated. We will be able to create a generic validator that can parse the behaviour interface and make sure that each distribution value is numeric if marked as available and not null if required.

### Persistence
We will store the distribution values in single table with a column for each possible distribution value.  
It will also have a column for the type being used and another for the instance name.  
Only the type and instance name will be required will be required, all other values can be null.  

The table will something like this:

| id  | type   | instance_name                     | min | p99  | other distribution values... | p1   | max  |
|-----|--------|-----------------------------------|-----|------|------------------------------|------|------|
| 1   | NORMAL | trap-props-depth-to-crest-lic-app | 1   | 78.9 | ...                          | 2.5  | 80   |
| 2   | CUSTOM | trap-props-depth-to-crest-stage-2 | 4   | NULL | ...                          | NULL | 10.2 |

