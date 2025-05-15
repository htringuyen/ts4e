# Core layer design
The core layer includes following API groups:
- Classification APIs: used directly by applications that implement a syntax highlighter
- TSDocument APIs: provides syntax tree related services for use in higher level APIs like Classification
- DocumentCheckpoint APIs: a helper APIs for asynchronous processing, used by APIs that depend on TSDocument APIs 
- TreeSitter APIs: TreeSitter API provides primitive tree-sitter parsers
- SafeParser APIs: a thin wrapper on top of TreeSitter APIs, that manages a parser pool and ensures thread safety for the parser it provides
- TextBuffer APIs: a bridge between TS4E and the text editor frameworks like that of Eclipse

![full-diagram.puml](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/htringuyen/ts4e/refs/heads/main/design-docs/resources/core-layer/full-picture.puml)

## Classification APIs

Classification layer centered at Classifier API. The classifier connects to a document and classifies the text into spans, 
where each span contains sufficient info for use cases such as syntax highlighting. Client of this API will provide the classifier 
a callback that will be call each time the classification changed, i.e. underlying document changed and the classification spans updated.

![classification.puml](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/htringuyen/ts4e/refs/heads/main/design-docs/resources/core-layer/classification.puml)

## TSDocument APIs
Document layer centered at TSDocument API. A TSDocument is a logical presentation of a text document. The actual read/write in the 
underlying document is managed by a text editor framework, e.g. Eclipse editor. A TSDocument connect to the editor by a TextBuffer 
that provides reading services and notifies the TSDocument when underlying text changed. By accessing the underlying text, the TSDocument 
not only provides basic text reading but also provides APIs related to SyntaxTree of the text.

![ts-document.puml](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/htringuyen/ts4e/refs/heads/main/design-docs/resources/core-layer/ts-document.puml)

## DocumentCheckpoint APIs
A helper layer centered at DocumentCheckpoint API. A checkpoint semantically takes a snapshot of a document at a particular point in time,
and becomes invalid as soon as the text of the document changed. The API provides execution services for use to ensure that any
computations and state updates done in scope of a valid checkpoint will be applied correctly and any such computations/updates done in scope of
an invalid checkpoint will cause no harm and possibly be canceled early to save resources.

![document-checkpoint.puml](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/htringuyen/ts4e/refs/heads/main/design-docs/resources/core-layer/document-checkpoint.puml)

## TextBuffer APIs
Text buffer layer is an immediate layer between TSDocument and the underlying text document the TSDocument representing, while the 
underlying document could come from a text editor framework like that of Eclipse.

![text-buffer.puml](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/htringuyen/ts4e/refs/heads/main/design-docs/resources/core-layer/text-buffer.puml)

## SafeParser APIs
Tree-sitter API is inherently thread unsafe and costly to create. This layer provide a pool of parser that 
could be shared my multiple thread without concern of data corrupted.

![safe-parser.puml](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/htringuyen/ts4e/refs/heads/main/design-docs/resources/core-layer/safe-parser.puml)

## TreeSitter APIs
Java bindings for the tree-sitter library

![tree-sitter.puml](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/htringuyen/ts4e/refs/heads/main/design-docs/resources/core-layer/tree-sitter.puml)
