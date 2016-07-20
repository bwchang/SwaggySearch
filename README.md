# SwaggySearch

## About

About... idk how detailed we need to be


## Usage

After cloning the repository, navigate into the repository and run:

```
ant build
```
This will compile all the files necessary. To launch the search engine, simply run:

```
ant SwaggySearch
```
This will bring up a separate window. Enter your search term(s) in the text field on top, and your search results will appear in the text area below the search field, organized by decreasing relevance.

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
or
```
apples + oranges
```
and press enter.


To display the pages containing either "apple" *or* "oranges", type in
```
apples OR oranges
```
or
```
apples | oranges
```
and press enter.


To display the pages containing the term "apple" but *not* the term "oranges", type in
```
apples NOT oranges
```
or
```
apples - oranges
```
and press enter.