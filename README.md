# SwaggySearch

## About

Our search engine is called SwaggySearch. We have focused on improving user experience by providing a GUI, reducing search time by implementing the TF-IDF algorithm and increasing search accuracy by implementing fuzzy search.

## Usage

After cloning the repository, navigate into the repository and run:

```
ant build
```
This will compile all the files necessary. To launch the search engine, simply run:

```
ant SwaggySearch
```
This will bring up a separate window. Enter your search term(s) in the text field on top, and your top ten search results will appear in the text area below the search field, organized by decreasing relevance.

## Search Examples

To search for "apples", you simply type in
```
apples
```
and press enter.


To display the pages containing both the terms "apples" *and* "oranges", type in
```
apples AND oranges
```
and press enter.


To display the pages containing either "apple" *or* "oranges", type in
```
apples OR oranges
```
and press enter.


To display the pages containing the term "apple" but *not* the term "oranges", type in
```
apples NOT oranges
```
and press enter.

## Features
These are the features we added to improve SwaggySearch:
* GUI
* Fuzzy Search - Note: Currently, this feature only works for single search words.
* TF-IDF
